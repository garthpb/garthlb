package garthlb;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;

public class GarthLbConfiguration extends Configuration {

    ///////////////////
    // Data

    @NotNull
    private String version;

    @NotNull
    private String redisConfigFile;

    ///////////////////
    // Accessors

    @JsonProperty
    public String getVersion() {
        return version;
    }

    @JsonProperty
    public String getRedisConfigFile() {
        return redisConfigFile;
    }
}
