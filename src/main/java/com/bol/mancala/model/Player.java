package com.bol.mancala.model;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Player {
  private final Integer order;
  private final String name;
  private final Boolean goesFirst;

  public static Integer ONE = 1;
  public static Integer TWO = 2;

  public static final List<Integer> ORDERS = Arrays.asList(ONE, TWO);

  public Player(Integer order, String name) {
    this.order = order;
    this.name = name;
    this.goesFirst = false;
  }

  public Player(Integer order, String name, Boolean goesFirst) {
    this.order = order;
    this.name = name;
    this.goesFirst = goesFirst;
  }

  public Player(Player player) {
    this.order = player.order;
    this.name = player.name;
    this.goesFirst = player.goesFirst;
  }

  public Integer getOrder() {
    return order;
  }

  public String getName() {
    return name;
  }

  public Boolean getGoesFirst() {
    return goesFirst;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Player player = (Player) o;
    return order.equals(player.order)
        && name.equals(player.name)
        && goesFirst.equals(player.goesFirst);
  }

  @Override
  public int hashCode() {
    return Objects.hash(order, name, goesFirst);
  }
}
