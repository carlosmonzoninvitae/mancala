package com.bol.mancala.dto;

public class DeleteDTO {
    private String session;
    private Integer matchId;

    public DeleteDTO() {
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public Integer getMatchId() {
        return matchId;
    }

    public void setMatchId(Integer matchId) {
        this.matchId = matchId;
    }
}
