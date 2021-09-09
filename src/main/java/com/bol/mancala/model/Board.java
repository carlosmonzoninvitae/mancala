package com.bol.mancala.model;

import java.util.Arrays;
import java.util.List;

// ######12#11##10###9##8##7######//
// #13###BOARD REPRESENTATION###6#//
// #######0##1###2###3##4##5######//
public class Board {
  private int[] pits;

  public static final int[] PITS_PLAYABLE = {0, 1, 2, 3, 4, 5, 7, 8, 9, 10, 11, 12};
  public static final int[] PITS_P1 = {0, 1, 2, 3, 4, 5, 6};
  public static final int[] PITS_P2 = {7, 8, 9, 10, 11, 12, 13};
  public static final int PIT_MAIN_P1 = 6;
  public static final int PIT_MAIN_p2 = 13;

  public static final int INITIAL_STOCK = 4;

  public static final List<Integer> PITS_VALID =
      Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13);

  public Board() {
    pits = new int[14];
    Arrays.fill(pits, INITIAL_STOCK);
    pits[PIT_MAIN_P1] = 0;
    pits[PIT_MAIN_p2] = 0;
  }

  public Board(Board board) {
    this.pits = Arrays.copyOf(board.getPits(), board.getPits().length);
  }

  public int[] getPits() {
    return pits;
  }

  // Recursive function to find the next valid pit
  public int getNextValidPit(int actualPit, int playerOrder) {
    int lastPlayablePit = PITS_PLAYABLE[PITS_PLAYABLE.length - 1];
    if (actualPit == lastPlayablePit) {
      return PITS_PLAYABLE[0];
    }
    if (actualPit == getNotPlayablePitByPlayer(playerOrder)) {
      getNextValidPit(actualPit + 1, playerOrder);
    }
    return actualPit + 1;
  }

  private int getNotPlayablePitByPlayer(int playerOrder) {
    return Player.ONE.equals(playerOrder) ? PIT_MAIN_p2 : PIT_MAIN_P1;
  }

  public void addToPit(int pitToDeposit, int quantity) {
    pits[pitToDeposit] = pits[pitToDeposit] + quantity;
  }

  private int getOppositePit(int pit) {
    return Math.abs(12 - pit);
  }

  public int getElementsFromOppositePit(int pit) {
    return pits[getOppositePit(pit)];
  }

  public Integer getElementsInPit(Integer pit) {
    return pits[pit];
  }

  public int getPlayableBeansFromPlayer(Player player) {
    int[] playerPits = player.getOrder().equals(Player.ONE) ? PITS_P1 : PITS_P2;
    int sum = 0;
    int playerIndex = 0;
    for (int i = 0; i < playerPits.length - 1; i++) {
      playerIndex = playerPits[i];
      sum = sum + pits[playerIndex];
    }

    return sum;
  }

  private static int whosePit(int pit) {
    if (Arrays.stream(Board.PITS_P1).anyMatch(p -> p == pit)) {
      return Player.ONE;
    } else if (Arrays.stream(Board.PITS_P2).anyMatch(p -> p == pit)) {
      return Player.TWO;
    }
    throw new IllegalArgumentException("Pit: " + pit + " is in valid");
  }

  public static boolean isPlayersPit(int pit, Player player) {
    return player.getOrder().equals(whosePit(pit));
  }

  public boolean isMainPit(int pit) {
    return pit == PIT_MAIN_P1 || pit == PIT_MAIN_p2;
  }

  public int getMainPitByPlayer(Player player) {
    if (Player.ONE.equals(player.getOrder())) return PIT_MAIN_P1;

    if (Player.TWO.equals(player.getOrder())) return PIT_MAIN_p2;

    throw new IllegalArgumentException("Player invalid: has no correct order");
  }

  public void zeroOutOppositePit(int pit) {
    pits[getOppositePit(pit)] = 0;
  }

  public void zeroOutPit(int pit) {
    pits[pit] = 0;
  }

  public void giveBeans(Player player, int beans) {
    int mainPit = getMainPitByPlayer(player);
    addToPit(mainPit, beans);
  }

  public void setFinalBoard(int player1Total, int player2Total) {
    pits = new int[14];
    pits[PIT_MAIN_P1] = player1Total;
    pits[PIT_MAIN_p2] = player2Total;
  }
}
