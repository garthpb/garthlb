package garthlb.model;

/**
 * Scoring method -
 * Determines how scores are collected for the leaderboard
 */
public enum ScoreMethod {
    /**
     * Incoming scores replace existing scores
     */
    REPLACE,

    /**
     * Incoming scores are added to existing score value (if present)
     */
    CUMULATIVE
}
