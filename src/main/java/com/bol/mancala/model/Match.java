package com.bol.mancala.model;

import com.bol.mancala.exception.MyResourceNotFoundException;

import java.util.Objects;
import java.util.Optional;

public class Match implements Cloneable {
    private final String session;
    private Integer id;
    private Board board;
    private Player player1;
    private Player player2;
    private Player whoIsNext;

    public Match(String session, String player1Name, String player2Name) {
        this.session = session;
        this.player1 = new Player(Player.ONE, player1Name, true);
        this.player2 = new Player(Player.TWO, player2Name);
        this.whoIsNext = this.player1;
        this.board = new Board();
    }

    public Match(Match match) {
        this.session = match.session;
        this.id = match.id;
        this.board = new Board(match.board);
        this.player1 = new Player(match.player1);
        this.player2 = new Player(match.player2);
        this.whoIsNext = new Player(match.whoIsNext);
    }

    public String getSession() {
        return session;
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

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
        return Objects.hash(session);
    }

    public Optional<Player> getPlayerById(Integer playerId) {
        if (this.player1.getOrder().equals(playerId)) {
            return Optional.ofNullable(this.player1);
        } else if (this.player2.getOrder().equals(playerId)) {
            return Optional.ofNullable(this.player2);
        }
        return Optional.empty();
    }

    public Integer getElementsInPit(Integer pit) {
        return getBoard().getElementsInPit(pit);
    }

    public Integer getElementsInOppositePit(Integer pit) {
        return getBoard().getElementsFromOppositePit(pit);
    }

    public void addToPit(int pitToDeposit, int quantity) {
        this.getBoard().addToPit(pitToDeposit, quantity);
    }

    public void zeroOutOppositePit(int pit) {
        getBoard().zeroOutOppositePit(pit);
    }

    public void zeroOutPit(int pit) {
        getBoard().zeroOutPit(pit);
    }

    public void giveBeans(Player player, int beans) {
        board.giveBeans(player, beans);
    }

    public boolean doesPlayerPlayAgain(int lastPit, Player player) {
        return lastPit == board.getMainPitByPlayer(player);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
