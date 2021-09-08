package com.bol.mancala.dto;

import com.bol.mancala.dto.PlayerDTO;

import java.util.Objects;

public class MatchDTO {

    private String session;
    private Integer id;
    private BoardDTO boardDTO;
    private PlayerDTO player1;
    private PlayerDTO player2;

    public MatchDTO() {}

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchDTO matchDTO = (MatchDTO) o;
        return Objects.equals(id, matchDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
