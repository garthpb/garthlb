package garthlb.model.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.util.List;


@Data
public class GetScoresForUsersRequest {

    @NotEmpty
    private String leaderboardName;

    @NotEmpty
    @Length(max=1000)
    private List<String> userIds;
}
