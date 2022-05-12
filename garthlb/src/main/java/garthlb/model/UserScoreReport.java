package garthlb.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Used to report a user's new score to the service
 */
@Data
public class UserScoreReport {
    @NotNull
    private String userId;

    private int score;
}
