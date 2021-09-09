package com.bol.mancala.service;

import com.bol.mancala.dao.MatchDao;
import com.bol.mancala.dto.MovementDTO;
import com.bol.mancala.dto.MatchDTO;
import com.bol.mancala.dto.FindMatchDTO;
import com.bol.mancala.dto.StartMatchDTO;
import com.bol.mancala.dto.PlayerDTO;
import com.bol.mancala.dto.BoardDTO;
import com.bol.mancala.exception.InvalidMovementException;
import com.bol.mancala.exception.MyResourceAlreadyExistsException;
import com.bol.mancala.exception.MyResourceNotFoundException;
import com.bol.mancala.exception.MyResourcePermissionDeniedException;
import com.bol.mancala.model.Board;
import com.bol.mancala.model.Match;
import com.bol.mancala.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class MatchService implements IMatchService {

  @Autowired private MatchDao matchDao;

  Logger logger = LoggerFactory.getLogger(MatchService.class);

  public MatchDTO makeAMove(MovementDTO movementDTO) {
    validateMovementDTOFields(movementDTO);

    Optional<Match> optionalMatch = matchDao.get(movementDTO.getMatchId());
    if (optionalMatch.isEmpty()) {
      logger.error("There is no match for that matchId");
      throw new MyResourceNotFoundException("Match was not found.");
    }
    Match match = optionalMatch.get();

    if (!movementDTO.getSessionId().equals(match.getSession())) {
      logger.error("Session id does not match with match id");
      throw new MyResourcePermissionDeniedException("Unauthorized for this match");
    }

    Optional<Player> optionalPlayerToPlay = match.getPlayerById(movementDTO.getPlayerId());
    if (optionalPlayerToPlay.isEmpty())
      throw new MyResourceNotFoundException("Player was not found.");
    Player playerToPlay = optionalPlayerToPlay.get();

    if (!canPlay(playerToPlay, match)) {
      logger.error("Player " + playerToPlay.getOrder() + "is trying to play in the opponents turn");
      throw new InvalidMovementException("Player can not move this turn.");
    }

    if (!isValidPitToPlay(movementDTO.getPit(), match, playerToPlay)) {
      logger.error("Pit: " + movementDTO.getPit() + " is invalid or is an opponent's pit");
      throw new InvalidMovementException("It is an invalid pit.");
    }

    match = play(movementDTO.getPit(), match, playerToPlay);

    return convertMatchToDTO(match);
  }

  public void deleteMatch(FindMatchDTO findMatchDTO) {
    Optional<Match> matchToDelete = matchDao.get(findMatchDTO.getMatchId());
    if (matchToDelete.isEmpty()) throw new MyResourceNotFoundException("Match was not found.");
    if (!findMatchDTO.getSession().equals(matchToDelete.get().getSession())) {
      logger.error("Trying to delete a match that doesnt belong to his session");
      throw new MyResourcePermissionDeniedException(
          "You do not have permissions to delete this match");
    }

    matchDao.delete(matchToDelete.get());
  }

  public MatchDTO startMatch(StartMatchDTO start) {
    Match match = new Match(start.getSession(), start.getPlayer1(), start.getPlayer2());
    if (matchExists(match.hashCode())) {
      logger.error("There is an already existing match for that session");
      throw new MyResourceAlreadyExistsException("Match already exists");
    }
    match = matchDao.save(match);

    return convertMatchToDTO(match);
  }

  public Boolean matchExists(Integer matchId) {
    return matchDao.get(matchId).isPresent();
  }

  public MatchDTO getMatch(FindMatchDTO findMatchDTO) {
    Optional<Match> match = matchDao.get(findMatchDTO.getMatchId());
    if (match.isEmpty()) {
      logger.error("There is not match for that matchId");
      throw new MyResourceNotFoundException("Match was not found.");
    }
    if (!match.get().getSession().equals(findMatchDTO.getSession())) {
      logger.error("Match belongs to another sessionId");
      throw new MyResourcePermissionDeniedException("Unauthorized to see this match");
    }

    return convertMatchToDTO(match.get());
  }

  private void validateMovementDTOFields(MovementDTO movementDTO) {
    if (movementDTO.getSessionId() == null
        || movementDTO.getMatchId() == null
        || !Player.ORDERS.contains(movementDTO.getPlayerId())
        || !Board.PITS_VALID.contains(movementDTO.getPit())) {
      logger.error("Something is missing or invalid in MovementDTO fields");
      throw new InvalidMovementException("Movement data is invalid");
    }
  }

  private boolean isValidPitToPlay(Integer pit, Match match, Player player) {
    // Check if is a playable pit
    boolean isPlayablePit =
        Arrays.stream(Board.PITS_PLAYABLE).anyMatch(pit::equals) && Board.isPlayersPit(pit, player);

    // Check if player has elements to move there
    Integer elementsInPit = match.getElementsInPit(pit);

    return isPlayablePit && elementsInPit > 0;
  }

  private void changeNextPlayer(Match match) {
    Player nextPlayer =
        match.getWhoIsNext().equals(match.getPlayer1()) ? match.getPlayer2() : match.getPlayer1();
    match.setWhoIsNext(nextPlayer);
  }

  private boolean doStealFromPit(Match match, int lastPit, Player player) {
    boolean lastPitIsMain = match.getBoard().isMainPit(lastPit);
    if (lastPitIsMain) return false;

    boolean lastPitFellInPlayersSide = Board.isPlayersPit(lastPit, player);
    if (!lastPitFellInPlayersSide) return false;

    if (match.getElementsInPit(lastPit) != 1) return false;

    int elementsFromOpposite = match.getElementsInOppositePit(lastPit);
    return elementsFromOpposite > 0;
  }

  private void stealFromPit(Match match, int lastPit, Player player) {
    int beansToDeposit = match.getElementsInOppositePit(lastPit) + match.getElementsInPit(lastPit);
    match.zeroOutPit(lastPit);
    match.zeroOutOppositePit(lastPit);
    giveBeans(match, player, beansToDeposit);
  }

  private void giveBeans(Match match, Player player, int beans) {
    match.giveBeans(player, beans);
  }

  private Match play(Integer pit, Match match, Player player) {
    int elements = match.getElementsInPit(pit);
    // Remove beans from initial pit and fill the next ones
    match.zeroOutPit(pit);
    int lastPit = pit;
    while (elements > 0) {
      // Use recursive getNextValidPit function to go through next n pits
      int pitToDeposit = match.getBoard().getNextValidPit(pit, player.getOrder());
      match.addToPit(pitToDeposit, 1);
      elements--;
      lastPit = pitToDeposit;
      pit++;
    }

    // Check if goes again
    if (match.doesPlayerPlayAgain(lastPit, player)) match.setWhoIsNext(player);
    else changeNextPlayer(match);

    // Check if can steal
    if (doStealFromPit(match, lastPit, player)) stealFromPit(match, lastPit, player);

    // Check if match is over
    if (match.isPlayerBoardEmpty(match.getPlayer1())
        || match.isPlayerBoardEmpty(match.getPlayer2())) {
      finishTheGame(match);
      matchDao.delete(match);
      return match;
    }

    return matchDao.update(match);
  }

  // whoIsNext = null means that the game finished
  private void finishTheGame(Match match) {
    setWinner(match);
    match.setWhoIsNext(null);
  }

  // Sets the winner or leaves it as null if its a draw
  private void setWinner(Match match) {
    int totalBeansP1 = match.getTotalBeansFromPlayer(match.getPlayer1());
    int totalBeansP2 = match.getTotalBeansFromPlayer(match.getPlayer2());

    match.getBoard().setFinalBoard(totalBeansP1, totalBeansP2);

    if (totalBeansP1 > totalBeansP2) match.setWinner(match.getPlayer1());
    else if (totalBeansP2 > totalBeansP1) match.setWinner(match.getPlayer2());
  }

  private Boolean canPlay(Player playerToMove, Match match) {
    return playerToMove.equals(match.getWhoIsNext())
        || (match.getWhoIsNext() == null && playerToMove.getGoesFirst());
  }

  private MatchDTO convertMatchToDTO(Match match) {
    MatchDTO matchDTO = new MatchDTO();

    matchDTO.setSession(match.getSession());
    matchDTO.setId(match.getId());
    matchDTO.setWhoWon(match.getWinner() != null ? match.getWinner().getOrder() : null);

    BoardDTO boardDTO = new BoardDTO();
    boardDTO.setPits(match.getBoard().getPits());
    matchDTO.setBoardDTO(boardDTO);

    PlayerDTO player1DTO = new PlayerDTO();
    player1DTO.setId(match.getPlayer1().getOrder());
    player1DTO.setName(match.getPlayer1().getName());
    player1DTO.setHasToPlay(match.getPlayer1().equals(match.getWhoIsNext()));
    matchDTO.setPlayer1(player1DTO);

    PlayerDTO player2DTO = new PlayerDTO();
    player2DTO.setId(match.getPlayer2().getOrder());
    player2DTO.setName(match.getPlayer2().getName());
    matchDTO.setPlayer2(player2DTO);
    player2DTO.setHasToPlay(match.getPlayer2().equals(match.getWhoIsNext()));

    return matchDTO;
  }
}
