package com.bol.mancala.service;

import com.bol.mancala.dao.MatchDao;
import com.bol.mancala.dto.*;
import com.bol.mancala.exception.InvalidMovementException;
import com.bol.mancala.exception.MyResourceAlreadyExistsException;
import com.bol.mancala.exception.MyResourceNotFoundException;
import com.bol.mancala.exception.MyResourcePermissionDeniedException;
import com.bol.mancala.model.Board;
import com.bol.mancala.model.Match;
import com.bol.mancala.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class MatchService implements IMatchService{

    @Autowired
    private MatchDao matchDao;

    private void validateMovementDTO(MovementDTO movementDTO) {
        if(movementDTO.getSessionId() == null || movementDTO.getMatchId() == null ||
                !Player.ORDERS.contains(movementDTO.getPlayerId()) || Board.PITS_VALID.contains(movementDTO.getPit()))
            throw new InvalidMovementException("Movement data is invalid");
    }

    public MatchDTO makeAMove(MovementDTO movementDTO) {
        //TODO: See if its better to move all the other validations here
        validateMovementDTO(movementDTO);

        Optional<Match> optionalMatch = matchDao.get(movementDTO.getMatchId());
        if (optionalMatch.isEmpty())
            throw new MyResourceNotFoundException("Match with ID: " + movementDTO.getMatchId() + "was not found.");
        Match match = optionalMatch.get();

        if(!movementDTO.getSessionId().equals(match.getSession()))
            throw new MyResourcePermissionDeniedException("You do not have permissions to delete this mach");

        Optional<Player> optionalPlayerToPlay = match.getPlayerById(movementDTO.getPlayerId());
        if (optionalPlayerToPlay.isEmpty())
            throw new MyResourceNotFoundException("Player with ID: " + movementDTO.getPlayerId() + "was not found.");
        Player playerToPlay = optionalPlayerToPlay.get();

        if (!canPlay(playerToPlay, match))
            throw new InvalidMovementException("Player can not move this turn.");

        if (!isValidPitToPlay(movementDTO.getPit(), match, playerToPlay))
            throw new InvalidMovementException("It is an invalid pit.");

        match = play(movementDTO.getPit(), match, playerToPlay);

        return convertMatchToDTO(match);
    }

    private boolean isValidPitToPlay(Integer pit, Match match, Player player) {
        // Check if is a playable pit
        boolean isPlayablePit = Arrays.stream(Board.PITS_PLAYABLE)
                .anyMatch(pit::equals);

        // Check if player has elements to move there
        Integer elementsInPit = match.getElementsInPit(pit);

        return isPlayablePit && elementsInPit>0;
    }

    private MatchDTO convertMatchToDTO(Match match) {
        MatchDTO matchDTO = new MatchDTO();

        matchDTO.setSession(match.getSession());
        matchDTO.setId(match.getId());

        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setPits(match.getBoard().getPits());
        matchDTO.setBoardDTO(boardDTO);

        PlayerDTO player1DTO = new PlayerDTO();
        Boolean player1HasToPlay = match.getWhoIsNext() == null
                || match.getWhoIsNext().equals(match.getPlayer1());
        player1DTO.setId(match.getPlayer1().getOrder());
        player1DTO.setName(match.getPlayer1().getName());
        player1DTO.setHasToPlay(player1HasToPlay);
        matchDTO.setPlayer1(player1DTO);

        PlayerDTO player2DTO = new PlayerDTO();
        player2DTO.setId(match.getPlayer2().getOrder());
        player2DTO.setName(match.getPlayer2().getName());
        matchDTO.setPlayer2(player2DTO);
        player2DTO.setHasToPlay(!player1HasToPlay);

        return matchDTO;
    }

    // TODO: Do not forget about the lastPit, which and validate
    private Match deposit(int pit, int elements, Player player, Match match) {
        // Remove beans from initial pit and fill the next ones
        match.zeroOutPit(pit);
        int lastPit = pit;
        while (elements>0) {
            int pitToDeposit = match.getBoard().getNextValidPit(pit, player.getOrder());
            match.addToPit(pitToDeposit, 1);
            elements--;
            lastPit = pitToDeposit;
            pit++;
        }

        if(match.doesPlayerPlayAgain(lastPit, player)) {
            match.setWhoIsNext(player);
            return match;
        }

        if (doStealFromPit(match, lastPit, player))
            stealFromPit(match, lastPit, player);

        changeNextPlayer(match);
        return match;
        //TODO: check too if match stays modified or if I have to assign the list again to it
    }

    private void changeNextPlayer(Match match) {
        Player nextPlayer = match.getWhoIsNext().equals(match.getPlayer1()) ? match.getPlayer2() : match.getPlayer1();
        match.setWhoIsNext(nextPlayer);
    }

    private boolean doStealFromPit(Match match, int lastPit, Player player) {
        boolean lastPitIsMain = match.getBoard().isMainPit(lastPit);
        if (lastPitIsMain)
            return false;

        boolean lastPitFellInPlayersSide = match.getBoard().isPlayersPit(lastPit, player);
        if (!lastPitFellInPlayersSide)
            return false;

        if(match.getElementsInPit(lastPit)>1)
            return false;

        int elementsFromOpposite = match.getElementsInOppositePit(lastPit);
        if (elementsFromOpposite==0)
            return false;

        return true;
    }
    private void stealFromPit(Match match, int lastPit, Player player) {
        int beansToSteal = match.getElementsInOppositePit(lastPit);
        match.zeroOutOppositePit(lastPit);
        giveBeans(match, player, beansToSteal);
    }

    private void giveBeans(Match match, Player player, int beans) {
        match.giveBeans(player, beans);
    }

    private Match play(Integer pit, Match match, Player player) {
        int elements = match.getElementsInPit(pit);
        deposit(pit, elements, player, match);
        return matchDao.update(match);
    }

    private Boolean canPlay(Player playerToMove, Match match) {
        return playerToMove.equals(match.getWhoIsNext()) ||
                (match.getWhoIsNext()==null && playerToMove.getGoesFirst());
    }

    public void deleteMatch(DeleteDTO deleteDTO) {
        if (deleteDTO.getSession()==null)
            throw new IllegalArgumentException();
        Optional<Match> matchToDelete = matchDao.get(deleteDTO.getMatchId());
        if(matchToDelete.isEmpty())
            throw new MyResourceNotFoundException("Match with ID: " + deleteDTO.getMatchId() + "was not found.");
        if(!deleteDTO.getSession().equals(matchToDelete.get().getSession()))
            throw new MyResourcePermissionDeniedException("You do not have permissions to delete this mach");

        matchDao.delete(matchToDelete.get());
    }

    public MatchDTO startMatch(StartMatchDTO start) {
        Match match = new Match(start.getSession(), start.getPlayer1(), start.getPlayer2());
        if (matchExists(match.hashCode())) {
            throw new MyResourceAlreadyExistsException("Match already exists");
        }
        match = matchDao.save(match);

        return convertMatchToDTO(match);
    }

    public Boolean matchExists(Integer matchId) {
        return matchDao.get(matchId).isPresent();
    }

    public MatchDTO getMatch(Integer matchId) {
        Optional<Match> match = matchDao.get(matchId);
        if (match.isEmpty()) {
            throw new MyResourceNotFoundException("Match with ID: " + matchId + "was not found.");
        }
        return convertMatchToDTO(match.get());
    }

    private int generateHashForMatch(String key) {
        return key.hashCode();
    }
}
