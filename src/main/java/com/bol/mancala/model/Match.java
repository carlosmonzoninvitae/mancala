package com.bol.mancala.model;

import java.util.Objects;
import java.util.Optional;

public class Match {
    private Long id;
    private Board board;
    private Player player1;
    private Player player2;
    private Player whoIsNext;

    public Match(Long id, String player1Name, String player2Name) {
        this.id = id;
        this.player1 = new Player(1, player1Name);
        this.player2 = new Player(2, player2Name);
        this.whoIsNext = this.player1;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public Player getWhoIsNext() {
        return whoIsNext;
    }

    public void setWhoIsNext(Player whoIsNext) {
        this.whoIsNext = whoIsNext;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Match match = (Match) o;
        return id.equals(match.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Optional<Player> getPlayerById(Integer playerId) {
        if (this.player1.getId().equals(playerId)) {
            return Optional.ofNullable(this.player1);
        } else if (this.player2.getId().equals(playerId)) {
            return Optional.ofNullable(this.player2);
        }
        return Optional.empty();
    }
}
