package garthlb.health;

import com.codahale.metrics.health.HealthCheck;

/**
 * Boilerplate requirement for dropwizard
 */
public class BasicHealthCheck extends HealthCheck {
    private String version;

    public BasicHealthCheck(String version) {
        this.version = version;
    }

    @Override
    protected Result check() {
        return Result.healthy("OK! Version: " + version);
    }
}
