package garthlb.storage;

import garthlb.model.CollationType;
import garthlb.model.LeaderboardInfo;
import garthlb.model.ScoreMethod;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import javax.ws.rs.BadRequestException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LeaderboardStorageTest {

    private final RedissonClient redisClient = mock(RedissonClient.class);
    private final LeaderboardStorage leaderboardStorage = new LeaderboardStorage(redisClient);

    private final String leaderboardNameA = "leaderboard-a";

    private final LeaderboardInfo leaderboardA = new LeaderboardInfo(
            leaderboardNameA,
            DateTime.now(),
            ScoreMethod.REPLACE,
            CollationType.ASCENDING);

    private final LeaderboardInfo emptyLeaderboard = new LeaderboardInfo();

    @BeforeAll
    void setUp() {
    }

    private void setupLeaderboardMock(final String name, final LeaderboardInfo lbInfo) {
        RBucket mockBucket = mock(RBucket.class);
        when(mockBucket.get()).thenReturn(lbInfo);
        when(redisClient.getBucket(name)).thenReturn(mockBucket);
    }

    @Test
    void createNewLeaderboard() {
        // arrange
        setupLeaderboardMock(leaderboardNameA, emptyLeaderboard);

        // act
        leaderboardStorage.createNewLeaderboard(leaderboardA);

        // assert
        // expect success
    }

    @Test()
    void createNewLeaderboard_AlreadyExists() {
        // arrange
        setupLeaderboardMock(leaderboardNameA, leaderboardA);

        // act
        leaderboardStorage.createNewLeaderboard(leaderboardA);

        // assert
        // expect exception
    }

    @Test
    void getByName() {

        // arrange
        // This test case uses mocks set up in "setUp"

        // act
        LeaderboardInfo result = leaderboardStorage.getLeaderboardInfoByName(leaderboardNameA);

        // assert
        assertEquals(result, leaderboardA);
    }

    @Test
    void getByName_ExpectedNotExist() {

        // arrange
        setupLeaderboardMock(leaderboardNameA, emptyLeaderboard);

        // act
        Exception exception = assertThrows(
                BadRequestException.class,
                () -> leaderboardStorage.getLeaderboardInfoByName(leaderboardNameA));

        // assert
        assertNotNull(exception);
    }
}