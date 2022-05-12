package garthlb.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardInfo {
    private String name;
    private DateTime createdDate;
    private ScoreMethod scoreMethod;
    private CollationType collationType;
}
