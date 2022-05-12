package garthlb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import garthlb.storage.LeaderboardStorage;
import garthlb.health.BasicHealthCheck;
import garthlb.resources.LeaderboardService;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

import java.io.File;
import java.io.IOException;

public class GarthLbApplication extends Application<GarthLbConfiguration> {

    public static void main(final String[] args) throws Exception {
        new GarthLbApplication().run(args);
    }

    @Override
    public String getName() {
        return "garthlb";
    }

    @Override
    public void initialize(final Bootstrap<GarthLbConfiguration> bootstrap) {
    }

    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        return mapper;
    }

    @Override
    public void run(final GarthLbConfiguration config,
                    final Environment env) throws IOException {

        // A dependency injection framework would be an improvement.
        // For now, I'm manually managing dependencies

        // mapper setup
        ObjectMapper mapper = createObjectMapper();

        // redis setup
        Config redisConfig = Config.fromYAML(new File(config.getRedisConfigFile()));
        redisConfig.setCodec(new JsonJacksonCodec(mapper));
        RedissonClient redisClient = Redisson.create(redisConfig);

        // leaderboard setup
        LeaderboardStorage leaderboardStorage = new LeaderboardStorage(redisClient);

        // Register resources
        env.jersey().register(new LeaderboardService(leaderboardStorage));

        env.healthChecks().register("Basic",
                new BasicHealthCheck(config.getVersion()));

    }

}
