package com.bol.mancala.dto;

public class MovementDTO {
    private String sessionId;
    private Integer matchId;
    private Integer playerId;
    private Integer pit;

    public MovementDTO() {
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getMatchId() {
        return matchId;
    }

    public void setMatchId(Integer matchId) {
        this.matchId = matchId;
    }

    public Integer getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }

    public Integer getPit() {
        return pit;
    }

    public void setPit(Integer pit) {
        this.pit = pit;
    }
}
