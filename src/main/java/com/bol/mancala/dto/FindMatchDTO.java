package com.bol.mancala.dto;

public class FindMatchDTO {
  private String session;
  private Integer matchId;

  public FindMatchDTO() {}

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
