package garthlb.resources;


import garthlb.model.UserScoreReport;
import garthlb.model.request.GetScoresForUsersRequest;
import garthlb.model.request.SetScoresMultiRequest;
import garthlb.model.request.SetScoresSingleRequest;
import garthlb.model.response.GetScoresResponse;
import garthlb.model.response.SetScoresResponse;
import garthlb.storage.LeaderboardStorage;
import garthlb.model.LeaderboardInfo;
import garthlb.model.request.CreateLeaderboardRequest;
import garthlb.model.UserScoreRankRecord;
import org.joda.time.DateTime;

import javax.validation.constraints.Max;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/leaderboard")
public class LeaderboardService {
    private final int DEFAULT_SCORE_COUNT = 100;
    private final int MAX_SCORE_COUNT = 1000;

    private LeaderboardStorage leaderboardStorage;

    public LeaderboardService(final LeaderboardStorage leaderboardStorage) {
        this.leaderboardStorage = leaderboardStorage;
    }

    /**
     * Retrives leaderboardInfo (definition for leaderboard, but no data entries)
     */
    @GET
    @Path("/get-info/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public LeaderboardInfo getLeaderboardInfo(final @PathParam("name") String name) {
        return leaderboardStorage.getLeaderboardInfoByName(name);
    }



    /**
     *  create leaderboard Info structure and save it to db
     */
    @POST
    @Path("/create")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.APPLICATION_JSON})
    public LeaderboardInfo createLeaderboard(final CreateLeaderboardRequest request) {
        // TODO: sanitize input

        final LeaderboardInfo newLbInfo = new LeaderboardInfo();
        newLbInfo.setName(request.getName());
        newLbInfo.setScoreMethod(request.getScoreMethod());
        newLbInfo.setCollationType(request.getCollationType());
        newLbInfo.setCreatedDate(DateTime.now());

        leaderboardStorage.createNewLeaderboard(newLbInfo);

        return newLbInfo;
    }

    /**
     *  sets multiple users' score data
     */
    @POST
    @Path("/set-scores-multi")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.APPLICATION_JSON})
    public SetScoresResponse setScoresMultiple(final SetScoresMultiRequest request) {

        final int recordsUpdated = leaderboardStorage.setScoresMultiple(request.getLeaderboardName(),
                request.getScoreList());

        // Possible extension:
        // Validate recordsUpdated versus records submitted -
        // I'm not sure if redis client 'scored set' updates can fail, though -
        // I think the validation would be redundant, but merits further investigation.

        return SetScoresResponse.builder()
                .recordsUpdated(recordsUpdated)
                .build();
    }


    /**
     *  set single score for user
     */
    @POST
    @Path("/set-scores-single")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.APPLICATION_JSON})
    public SetScoresResponse setScoresSingle(final SetScoresSingleRequest request) {

        List<UserScoreReport> scores = new ArrayList<>();
        final UserScoreReport newScore = new UserScoreReport();
        newScore.setUserId(request.getUserId());
        newScore.setScore(request.getScore());
        scores.add(newScore);

        final int recordsUpdated = leaderboardStorage.setScoresMultiple(request.getLeaderboardName(),
                scores);

        // Possible extension:
        // Validate recordsUpdated versus records submitted -
        // I'm not sure if redis client 'scored set' updates can fail, though -
        // I think the validation would be redundant, but merits further investigation.

        return SetScoresResponse.builder()
                .recordsUpdated(recordsUpdated)
                .build();
    }

    /**
     *  retrieves score data for specified leaderboard
     */
    @GET
    @Path("/get-scores/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public GetScoresResponse getScores(final @PathParam("name") String leaderboardName,
                                             final @QueryParam("offset") int offset,
                                             @QueryParam("count") @Max(value = MAX_SCORE_COUNT) int count) {

        if(count == 0) {
            count = DEFAULT_SCORE_COUNT;
        }

        final List<UserScoreRankRecord> scoreList = leaderboardStorage.getScores(leaderboardName,
                offset,
                count);

        return GetScoresResponse.builder()
                .scores(scoreList)
                .build();
    }

    /**
     *  retrieves score data for specified leaderboard,
     *  for specified users
     */
    @POST
    @Path("/get-users-scores")
    @Produces(MediaType.APPLICATION_JSON)
    public GetScoresResponse getScoresForUsers(final GetScoresForUsersRequest request) {

        final List<UserScoreRankRecord> scoreList = leaderboardStorage.getScoresForUsers(request.getLeaderboardName(),
                request.getUserIds());

        return GetScoresResponse.builder()
                .scores(scoreList)
                .build();
    }

    /**
     *  retrieves score data for specified leaderboard,
     *  for user's friends list
     */
    @GET
    @Path("/get-friend-scores/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public GetScoresResponse getScoresForFriends(final @PathParam("name") String leaderboardName) {

        // stubbing this out because there isn't a real friends list.
        // this would be a lookup from an external service
        final List<String> friendsList = new ArrayList<>();
        friendsList.add("sara137");
        friendsList.add("douglas654");
        friendsList.add("jimmy543");

        final List<UserScoreRankRecord> scoreList = leaderboardStorage.getScoresForUsers(leaderboardName,
                friendsList);

        return GetScoresResponse.builder()
                .scores(scoreList)
                .build();
    }
}
