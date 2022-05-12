package garthlb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Used in API responses to communicate the ranked score record
 * for a user.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserScoreRankRecord {
    private String userId;
    private int score;
    private int rank;
}
