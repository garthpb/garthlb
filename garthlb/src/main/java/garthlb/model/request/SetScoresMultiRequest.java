package garthlb.model.request;

import garthlb.model.UserScoreReport;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetScoresMultiRequest {

    @NotEmpty
    private String leaderboardName;


    @NotEmpty
    private List<UserScoreReport> scoreList;
}
