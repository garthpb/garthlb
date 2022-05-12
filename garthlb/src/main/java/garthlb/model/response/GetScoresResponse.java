package garthlb.model.response;

import garthlb.model.UserScoreRankRecord;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetScoresResponse {
    private List<UserScoreRankRecord> scores;
}
