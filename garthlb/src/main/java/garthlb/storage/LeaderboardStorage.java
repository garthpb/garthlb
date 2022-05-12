package garthlb.storage;


import garthlb.model.*;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.*;
import org.redisson.client.protocol.ScoredEntry;

import javax.ws.rs.BadRequestException;
import java.util.*;

/**
 * LeaderboardStorage -
 * Manages storage and retrieval of leaderboard data.
 *
 * Here's the general gist of how this works:
 * This is backed by redis (using redisson client).
 *
 * LeaderboardStorage uses a redis map to store leaderboard information,
 * keyed by name.
 *
 * Reddison has a "scoredSortedSet" which automatically
 * sorts entries as they're inserted (also keyed by leaderboard name)
 *
 *
 * Each leaderboard has the following pieces (and corresponding redis data-backing):
 * - "info" : the leaderboard 'info' data set
 * - "scores" : the sorted set that handles the actual sorting of the data and ranking of scores
 *
 */
public class LeaderboardStorage {

    private final String INFO_KEY_FORMAT = "lb:%s:info";
    private final String SCORES_KEY_FORMAT = "lb:%s:scores";

    private RedissonClient redisClient;

    public LeaderboardStorage(final RedissonClient redisClient) {
        this.redisClient = redisClient;
    }

    /**
     * Leaderboard data is stored with multiple keys in redis.
     * This builds the 'info' key, which corresponds to
     * the leaderboard's definition.
     */
    private String buildInfoKey(final String leaderboardName) {
        return String.format(INFO_KEY_FORMAT, leaderboardName);
    }

    /**
     * Leaderboard data is stored with multiple keys in redis.
     * This builds the 'scores' key, which corresponds to
     * the set which sorts userIds by their scores.
     */
    private String buildScoresKey(final String leaderboardName) {
        return String.format(SCORES_KEY_FORMAT, leaderboardName);
    }

    /**
     * Helper function to get the score-sorted redis set for userid-score data
     */
    private RScoredSortedSet<String> getScoresSet(final String leaderboardName) {
        final String scoresKey = buildScoresKey(leaderboardName);
        return redisClient.getScoredSortedSet(scoresKey);
    }

    /**
     * Checks if specified leaderboard is populated with valid data
     */
    private boolean leaderboardIsValid(final LeaderboardInfo lbInfo) {
        return lbInfo != null
            && StringUtils.isNotEmpty(lbInfo.getName());
    }

    /**
     * Creates leaderboard info and adds it to leaderboard table
     */
    public void createNewLeaderboard(final LeaderboardInfo newLeaderboardInfo) {
        final String infoKey = buildInfoKey(newLeaderboardInfo.getName());

        // Need to check if data already exists; if it does, we need to throw an error.
        final RBucket<LeaderboardInfo> bucket = redisClient.getBucket(infoKey);
        final LeaderboardInfo existingItem = bucket.get();

        if(leaderboardIsValid(existingItem)) {
            throw new BadRequestException("Leaderboard already exists with name: " + newLeaderboardInfo.getName());
        }

        // populate bucket with new data
        bucket.set(newLeaderboardInfo);
    }

    /**
     * Retrieves leaderboardInfo by leaderboard name
     */
    public LeaderboardInfo getLeaderboardInfoByName(final String leaderboardName) {
        final String infoKey = buildInfoKey(leaderboardName);
        RBucket<LeaderboardInfo> bucket = redisClient.getBucket(infoKey);
        LeaderboardInfo lbInfo = bucket.get();
        if(!leaderboardIsValid(lbInfo)) {
            throw new BadRequestException("Leaderboard does not exist with name: " + leaderboardName);
        }
        return lbInfo;
    }

    /**
     * Sets multiple users' scores
     */
    public int setScoresMultiple(final String leaderboardName, final List<UserScoreReport> scores) {
        final LeaderboardInfo leaderboardInfo = getLeaderboardInfoByName(leaderboardName);

        RScoredSortedSet<String> scoresSet = getScoresSet(leaderboardName);

        if(leaderboardInfo.getScoreMethod().equals(ScoreMethod.CUMULATIVE))
        {
            return updateRecordsWithAccumulation(scoresSet, scores);
        }
        else
        {
            return updateRecordsWithReplace(scoresSet, scores);
        }
    }

    /**
     * Updates records using accumulation - scores increase by the values specified
     */
    private int updateRecordsWithAccumulation(RScoredSortedSet<String> scoredSet,
                                              List<UserScoreReport> scores) {
        int recordsUpdated = 0;

        for(final UserScoreReport score : scores) {
            scoredSet.addScore(score.getUserId(), score.getScore());
            recordsUpdated++;

        }
        return recordsUpdated;
    }

    /**
     * Updates records using replacement - scores are overwritten by values specified
     */
    private int updateRecordsWithReplace(RScoredSortedSet<String> scoredSet,
                                         List<UserScoreReport> scores) {
        int recordsUpdated = 0;

        for(final UserScoreReport score : scores) {
            scoredSet.add(score.getScore(), score.getUserId());
            recordsUpdated++;
        }
        return recordsUpdated;
    }

    /**
     * Retrieves scores from leaderboard, sorted in order determined by
     * leaderboard configuration (specified at creation)
     * @param leaderboardName unique leaderboard identifier
     * @param offset start reading results at this offset
     * @param count returns this many records
     * @return list of UserScores from specified leaderboard
     */
    public List<UserScoreRankRecord> getScores(final String leaderboardName, int offset, int count) {
        final LeaderboardInfo leaderboardInfo = getLeaderboardInfoByName(leaderboardName);

        RScoredSortedSet<String> scoresSet = getScoresSet(leaderboardName);

        boolean ascending = leaderboardInfo.getCollationType().equals(CollationType.ASCENDING);

        Collection<ScoredEntry<String>> rawResults = (ascending)
                ? scoresSet.entryRange(offset, offset + count)
                : scoresSet.entryRangeReversed(offset, offset + count);

        List<UserScoreRankRecord> results = new ArrayList<>();
        int rank = offset;
        for(final ScoredEntry<String> entry : rawResults) {
            final UserScoreRankRecord record = new UserScoreRankRecord(entry.getValue(),
                    entry.getScore().intValue(),
                    rank++);
            results.add(record);
        }

        return results;
    }


    /**
     * Retrives scores from leaderboard, sorted in order determined by
     * leaderboard configuration (specified at creation)
     * @param leaderboardName unique leaderboard identifier
     * @param userIds data will be retrieved for these users
     * @return list of UserScores from specified leaderboard
     */
    public List<UserScoreRankRecord> getScoresForUsers(final String leaderboardName, List<String> userIds) {
        final LeaderboardInfo leaderboardInfo = getLeaderboardInfoByName(leaderboardName);

        RScoredSortedSet<String> scoresSet = getScoresSet(leaderboardName);

        boolean ascending = leaderboardInfo.getCollationType().equals(CollationType.ASCENDING);
        Comparator<UserScoreRankRecord> comparator = ascending
                ? Comparator.comparingInt(UserScoreRankRecord::getRank)
                : Comparator.comparingInt(UserScoreRankRecord::getRank).reversed();

        List<UserScoreRankRecord> results = new ArrayList<>();

        for(final String userId : userIds) {
            Double rawScore = scoresSet.getScore(userId);

            // If user hasn't submitted score for this leaderboard, skip it.
            if(rawScore == null) {
                continue;
            }

            int score = rawScore.intValue();
            int rank = scoresSet.rank(userId);

            final UserScoreRankRecord record = new UserScoreRankRecord(userId,
                    score,
                    rank);
            results.add(record);
        }

        Collections.sort(results, comparator);

        return results;
    }

}
