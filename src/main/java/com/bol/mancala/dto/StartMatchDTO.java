package com.bol.mancala.dto;

public class StartMatchDTO {
    private Long matchId;
    private String player1;
    private String player2;

    public StartMatchDTO(Long matchId, String player1, String player2) {
        this.matchId = matchId;
        this.player1 = player1;
        this.player2 = player2;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }
}
