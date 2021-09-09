package com.bol.mancala.service;

import com.bol.mancala.dto.FindMatchDTO;
import com.bol.mancala.dto.MatchDTO;
import com.bol.mancala.dto.MovementDTO;
import com.bol.mancala.dto.StartMatchDTO;

public interface IMatchService {

  MatchDTO startMatch(StartMatchDTO start);

  void deleteMatch(FindMatchDTO findMatchDTO);

  MatchDTO getMatch(FindMatchDTO findMatchDTO);

  Boolean matchExists(Integer matchId);

  MatchDTO makeAMove(MovementDTO movementDTO);
}
