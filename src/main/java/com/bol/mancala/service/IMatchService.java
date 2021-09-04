package com.bol.mancala.service;

import com.bol.mancala.dto.MatchDTO;
import com.bol.mancala.dto.StartMatchDTO;
import com.bol.mancala.model.Match;

public interface IMatchService{

//    ModelMapper modelMapper = new ModelMapper();
//    // user here is a prepopulated User instance
//    UserDTO userDTO = modelMapper.map(user, UserDTO.class);

//    public Match makeAMove(MovementDTO movementDTO) {
//        return null;
//    }

    public MatchDTO startMatch(StartMatchDTO start);

    public Boolean stopMatch();

    public Match getMatchStatus();
}
