package com.bol.mancala.dto;

public class PlayerDTO {
  private Integer id;
  private String name;
  private Boolean hasToPlay = false;

  public PlayerDTO() {}

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Boolean getHasToPlay() {
    return hasToPlay;
  }

  public void setHasToPlay(Boolean hasToPlay) {
    this.hasToPlay = hasToPlay;
  }
}
