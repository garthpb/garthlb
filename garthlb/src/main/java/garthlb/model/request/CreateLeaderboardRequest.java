package garthlb.model.request;

import garthlb.model.CollationType;
import garthlb.model.ScoreMethod;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CreateLeaderboardRequest {

    @NotEmpty
    private String name;

    @NotEmpty
    private ScoreMethod scoreMethod;

    @NotEmpty
    private CollationType collationType;
}
