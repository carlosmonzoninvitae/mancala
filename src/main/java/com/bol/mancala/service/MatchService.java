package com.bol.mancala.service;

import com.bol.mancala.dao.MatchDao;
import com.bol.mancala.dto.*;
import com.bol.mancala.model.Match;
import com.bol.mancala.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MatchService implements IMatchService{

    @Autowired
    private MatchDao matchDao;

    public MatchDTO makeAMove(MovementDTO movementDTO) {
        Optional<Match> optionalMatch = matchDao.get(movementDTO.getMatchId());
        if (optionalMatch.isEmpty()) {
            return null; //TODO: Return MatchNotFoundException
        }
        Match match = optionalMatch.get();

        Optional<Player> optionalPlayerToPlay = match.getPlayerById(movementDTO.getPlayerId());
        if (optionalPlayerToPlay.isEmpty()) {
            return null; //TODO: Return PlayerNotFoundExcepton
        }
        Player playerToPlay = optionalPlayerToPlay.get();

        if (!canPlay(playerToPlay, match)) {
            return null; //TODO: Return PlayerCantMoveException
        }

        match = play(movementDTO, match);
        return convertMatchToDTO(match);
    }

    private MatchDTO convertMatchToDTO(Match match) {
        MatchDTO matchDTO = new MatchDTO();
        matchDTO.setId(match.getId());

        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setSide1(match.getBoard().getSide1());
        boardDTO.setSide2(match.getBoard().getSide2());
        matchDTO.setBoardDTO(boardDTO);

        PlayerDTO player1DTO = new PlayerDTO();
        Boolean player1HasToPlay = match.getWhoIsNext() == null
                || match.getWhoIsNext().equals(match.getPlayer2());
        player1DTO.setId(match.getPlayer1().getId());
        player1DTO.setName(match.getPlayer1().getName());
        player1DTO.setHasToPlay(player1HasToPlay);
        matchDTO.setPlayer1(player1DTO);

        PlayerDTO player2DTO = new PlayerDTO();
        player2DTO.setId(match.getPlayer2().getId());
        player2DTO.setName(match.getPlayer2().getName());
        matchDTO.setPlayer2(player2DTO);
        player2DTO.setHasToPlay(!player1HasToPlay);

        return new MatchDTO();
    }

    private Match play(MovementDTO movementDTO, Match match) {
        //TODO: Do logic of movement
        return matchDao.saveOrUpdate(match);
    }

    public Boolean canPlay(Player playerToMove, Match match) {
        return !playerToMove.equals(match.getWhoIsNext()) ||
                (match.getWhoIsNext()==null && playerToMove.getGoesFirst());
    }

    public MatchDTO startMatch(StartMatchDTO start) {
        Match match = new Match(start.getMatchId(), start.getPlayer1(), start.getPlayer2());
        return convertMatchToDTO(match);
    }

    // TODO: Define for possible returns
    public Boolean stopMatch() {
        return null;
    }

    public Match getMatchStatus() {
        return null;
    }
}
