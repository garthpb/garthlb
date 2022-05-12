package garthlb.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetScoresSingleRequest {
    @NotEmpty
    private String leaderboardName;

    @NotEmpty
    private String userId;

    private int score;
}
