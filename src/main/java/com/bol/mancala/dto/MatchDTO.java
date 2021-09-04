package com.bol.mancala.dto;

public class MatchDTO {

    private String session;
    private Long id;
    private BoardDTO boardDTO;
    private PlayerDTO player1;
    private PlayerDTO player2;

    public MatchDTO() {}

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public BoardDTO getBoardDTO() {
        return boardDTO;
    }

    public void setBoardDTO(BoardDTO boardDTO) {
        this.boardDTO = boardDTO;
    }

    public PlayerDTO getPlayer1() {
        return player1;
    }

    public void setPlayer1(PlayerDTO player1) {
        this.player1 = player1;
    }

    public PlayerDTO getPlayer2() {
        return player2;
    }

    public void setPlayer2(PlayerDTO player2) {
        this.player2 = player2;
    }
}
