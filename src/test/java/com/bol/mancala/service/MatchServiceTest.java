package com.bol.mancala.service;

import com.bol.mancala.dto.*;
import com.bol.mancala.exception.InvalidMovementException;
import com.bol.mancala.exception.MyResourceAlreadyExistsException;
import com.bol.mancala.exception.MyResourceNotFoundException;
import com.bol.mancala.exception.MyResourcePermissionDeniedException;
import com.bol.mancala.model.Board;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MatchServiceTest {

    @Autowired
    IMatchService matchService;

    private final String sessionId = "fu5e5ytuu6yguoutdecetdrf";


    //================================================================================
    // Match Creation and Retrieval Tests
    //================================================================================
    @Test
    @DisplayName("Correct creation of a match")
    void testNewMatchCreationSuccessful() {
        //Create match and check that exists
        MatchDTO newMatchDTO = createNewMatch(sessionId);
        assertNotNull(newMatchDTO.getId(), "None match was created");
        assertTrue(matchService.matchExists(newMatchDTO.getId()));
        clean(newMatchDTO);
    }

    @Test
    @DisplayName("Throws an exception when try to start a match that is already created")
    void testMatchAlreadyExistsOnCreation() {
        //Create match and check that exists
        MatchDTO match1 = createNewMatch(sessionId);
        assertTrue(matchService.matchExists(match1.getId()));

        //Try to create a new match with existing id and receive an exception
        assertThrows(MyResourceAlreadyExistsException.class, () -> {
            createNewMatch(sessionId);
        });

        clean(match1);
    }

    @Test
    @DisplayName("Retrieves match")
    void retrievesMatchSuccessful() {
        MatchDTO match = createNewMatch(sessionId);
        assertTrue(matchService.matchExists(match.getId()));

        FindMatchDTO findMatchDTO = new FindMatchDTO();
        findMatchDTO.setMatchId(match.getId());
        findMatchDTO.setSession(sessionId);
        MatchDTO retrievedMatch = matchService.getMatch(findMatchDTO);
        assertEquals(match, retrievedMatch);

        clean(match);
    }

    @Test
    @DisplayName("Retrieves not own match - FAIL")
    void retrievesNotOwnMatch() {
        MatchDTO match = createNewMatch(sessionId);
        assertTrue(matchService.matchExists(match.getId()));

        FindMatchDTO findMatchDTO = new FindMatchDTO();
        findMatchDTO.setMatchId(match.getId());
        findMatchDTO.setSession("DifferentSession");
        assertThrows(MyResourcePermissionDeniedException.class, () -> {
            MatchDTO retrievedMatch = matchService.getMatch(findMatchDTO);
        });

        clean(match);
    }

    //================================================================================
    // Deletion of matches
    //================================================================================

    @Test
    @DisplayName("Deletes an existing match")
    void testDeleteMatchSuccessful() {
        //Create match and check that exists
        MatchDTO match = createNewMatch(sessionId);
        assertTrue(matchService.matchExists(match.getId()));

        FindMatchDTO findMatchDTO = new FindMatchDTO();
        findMatchDTO.setSession(sessionId);
        findMatchDTO.setMatchId(match.getId());

        //Delete match and check it doesnt exist
        matchService.deleteMatch(findMatchDTO);
        assertFalse(matchService.matchExists(match.getId()));
    }

    @Test
    @DisplayName("Trying to delete a non existing match and throw not found exception")
    void testDeleteNonExistingMatch() {
        // Check for non existing match
        Integer nonExistingMatchId = 10;
        assertFalse(matchService.matchExists(nonExistingMatchId));

        FindMatchDTO findMatchDTO = new FindMatchDTO();
        findMatchDTO.setSession(sessionId);
        findMatchDTO.setMatchId(nonExistingMatchId);

        // Try to delete it and throw exception
        assertThrows(MyResourceNotFoundException.class, () -> {
            matchService.deleteMatch(findMatchDTO);
        });
    }


    //======================================//######12#11##10###9##8##7######//
    // Play moves Tests                     //#13###BOARD REPRESENTATION###6#//
    //======================================//#######0##1###2###3##4##5######//
    // Note: The pit values that will be asserted are just the relevant ones for the test results and logic.

    @Test
    @DisplayName("Player 1 plays first and Player 2 don't")
    void player1CanPlayAtFirst() {
        // Create a new match
        MatchDTO match = createNewMatch(sessionId);
        assertTrue(matchService.matchExists(match.getId()));

        // Get player 1
        PlayerDTO player1 = match.getPlayer1();
        assertNotNull(player1);

        // Check if he can play first
        assertEquals(match.getPlayer1().getId(), player1.getId());
        assertTrue(match.getPlayer1().getHasToPlay());
        assertFalse(match.getPlayer2().getHasToPlay());

        clean(match);
    }

    @Test
    @DisplayName("Make the first move with Player 1 selecting the first pit")
    void makeAMoveWithPlayerOne() {
        // Create a new match
        MatchDTO match = createNewMatch(sessionId);
        assertTrue(matchService.matchExists(match.getId()));
        int[] pits = match.getBoardDTO().getPits();
        assertEquals(pits[0], Board.INITIAL_STOCK);
        assertEquals(pits[1], Board.INITIAL_STOCK);
        assertEquals(pits[2], Board.INITIAL_STOCK);
        assertEquals(pits[3], Board.INITIAL_STOCK);
        assertEquals(pits[4], Board.INITIAL_STOCK);
        assertEquals(pits[5], Board.INITIAL_STOCK);

        match = moveForPlayerAndPit(match, match.getPlayer1().getId(), 0);

        int[] pitsResult = match.getBoardDTO().getPits();
        assertEquals(pitsResult[0], 0); // The one who was zeroed out
        assertEquals(pitsResult[1], Board.INITIAL_STOCK + 1); // Gained one from selected pit
        assertEquals(pitsResult[2], Board.INITIAL_STOCK + 1); // Gained one from selected pit
        assertEquals(pitsResult[3], Board.INITIAL_STOCK + 1); // Gained one from selected pit
        assertEquals(pitsResult[4], Board.INITIAL_STOCK + 1); // Gained one from selected pit
        assertEquals(pitsResult[5], Board.INITIAL_STOCK); //This must stay as it was before.

        clean(match);
    }

    @Test
    @DisplayName("Make the first move for a different match session")
    void makeAMoveWithNoOwnMatch() {
        // Create a new match
        MatchDTO match = createNewMatch(sessionId);
        assertTrue(matchService.matchExists(match.getId()));

        // Play with a different session
        match.setSession("DifferentSession");
        assertThrows(MyResourcePermissionDeniedException.class, () -> {
            moveForPlayerAndPit(match, match.getPlayer1().getId(), 3);
        });

       // This set is just to clean the right one
        match.setSession(sessionId);
        clean(match);
    }

    @Test
    @DisplayName("Make the first move for a different match session")
    void makeAMoveInNotExistingMatch() {
        // Create a new match
        MatchDTO nonExistingMatch = new MatchDTO();
        nonExistingMatch.setSession("notExist");
        nonExistingMatch.setId(1000);
        assertFalse(matchService.matchExists(nonExistingMatch.getId()));

        // Play with a different session
        assertThrows(MyResourceNotFoundException.class, () -> {
            moveForPlayerAndPit(nonExistingMatch, 1, 3);
        });
    }

    @Test
    @DisplayName("Make the first move with Player 1 and leaving the last bean in his main pit")
    void makeAMoveAndPlayAgain() {
        // Create a new match
        MatchDTO match = createNewMatch(sessionId);
        assertTrue(matchService.matchExists(match.getId()));
        int[] pits = match.getBoardDTO().getPits();
        // Pit 2 should have 4 beans
        assertEquals(pits[2], Board.INITIAL_STOCK);

        // By selecting the pit 2, the last bean will fall into the P1 main pit and he could play again by the rules
        match = moveForPlayerAndPit(match, match.getPlayer1().getId(), 2);
        int[] pitsResult = match.getBoardDTO().getPits();
        assertEquals(pitsResult[2], 0); // Zeroed out
        assertEquals(pitsResult[Board.PIT_MAIN_P1], 1); // Gained one
        assertEquals(pitsResult[Board.PITS_P2[0]], Board.INITIAL_STOCK); // Last bean fall in the previous pit

        // He can make a new move
        assertEquals(pitsResult[3], Board.INITIAL_STOCK + 1);
        match = moveForPlayerAndPit(match, match.getPlayer1().getId(), 3);
        pitsResult = match.getBoardDTO().getPits();
        assertEquals(pitsResult[3], 0);
        assertEquals(pitsResult[4], Board.INITIAL_STOCK + 2);

        clean(match);
    }

    @Test
    @DisplayName("Make a move using an empty pit and fails")
    void makeAMoveAndPlayAgainUsingAnEmptyPitFail() {
        // Create a new match
        MatchDTO match = createNewMatch(sessionId);
        assertTrue(matchService.matchExists(match.getId()));
        int[] pits = match.getBoardDTO().getPits();
        // Pit 2 should have 4 beans
        final int pit2 = 2;
        assertEquals(pits[pit2], Board.INITIAL_STOCK);

        // After selecting the pit 2, its going to be zeroed out
        match = moveForPlayerAndPit(match, match.getPlayer1().getId(), pit2);
        int[] pitsResult = match.getBoardDTO().getPits();
        assertEquals(pitsResult[pit2], 0); // Zeroed out


        final int playerId = match.getPlayer1().getId(); // Created just because of assertThrows necessity
        MatchDTO finalMatch = match; // Created just because of assertThrows necessity
        // Then he selects the same one and fails because he has not beans to move
        assertThrows(InvalidMovementException.class, () -> {
            moveForPlayerAndPit(finalMatch, playerId, pit2);;
        });

        clean(match);
    }

    @Test
    @DisplayName("Make a move using an opponent's pit - FAILS")
    void makeAMoveAInOpponentsPitFail() {
        // Create a new match
        MatchDTO match = createNewMatch(sessionId);
        assertTrue(matchService.matchExists(match.getId()));
        final int opponentsPit = Board.PITS_P2[0];

        final int playerId = match.getPlayer1().getId(); // Created just because of assertThrows necessity
        MatchDTO finalMatch = match; // Created just because of assertThrows necessity
        // Then he selects the same one and fails because he has not beans to move
        assertThrows(InvalidMovementException.class, () -> {
            moveForPlayerAndPit(finalMatch, playerId, opponentsPit);;
        });

        clean(match);
    }

    @Test
    @DisplayName("Make the first move with Player 1 and fail trying to move again in opponent's turn - FAIL")
    void makeAMoveInOpponentsTurnFail() {
        // Create a new match
        MatchDTO match = createNewMatch(sessionId);
        assertTrue(matchService.matchExists(match.getId()));
        int[] pits = match.getBoardDTO().getPits();
        // Pit 2 should have 4 beans
        assertEquals(pits[2], Board.INITIAL_STOCK);

        // By selecting the pit 2, the last bean will fall into the P1 main pit and he could play again by the rules
        match = moveForPlayerAndPit(match, match.getPlayer1().getId(), 0);
        int[] pitsResult = match.getBoardDTO().getPits();
        assertEquals(pitsResult[0], 0); // Zeroed out
        assertEquals(pitsResult[3], Board.INITIAL_STOCK + 1); //Gained one
        assertEquals(pitsResult[Board.PIT_MAIN_P1], 0); // Gained nothing, do not play again


        MatchDTO finalMatch = match; // Created just because of assertThrows necessity
        MatchDTO finalMatch1 = match; // Created just because of assertThrows necessity
        //Trying to move without being his turn should throw an InvalidMovementException
        assertThrows(InvalidMovementException.class, () -> {
            moveForPlayerAndPit(finalMatch, finalMatch1.getPlayer1().getId(), 3);
        });

        clean(match);
    }

    @Test
    @DisplayName("Player 1: 12 and Player 2: 36(Winner)")
    void makeAMoveP1LastTurnAndP2Wins() {
        // Create a new match
        MatchDTO match = createNewMatch(sessionId);
        assertTrue(matchService.matchExists(match.getId()));

        match = moveForPlayerAndPit(match, match.getPlayer1().getId(), 0);
        match = moveForPlayerAndPit(match, match.getPlayer2().getId(), 7);
        match = moveForPlayerAndPit(match, match.getPlayer1().getId(), 1);
        match = moveForPlayerAndPit(match, match.getPlayer1().getId(), 2);
        match = moveForPlayerAndPit(match, match.getPlayer2().getId(), 7);
        match = moveForPlayerAndPit(match, match.getPlayer1().getId(), 3);
        match = moveForPlayerAndPit(match, match.getPlayer2().getId(), 7);
        match = moveForPlayerAndPit(match, match.getPlayer1().getId(), 4);
        match = moveForPlayerAndPit(match, match.getPlayer2().getId(), 7);
        match = moveForPlayerAndPit(match, match.getPlayer1().getId(), 5);

        assertEquals(match.getPlayer2().getId(), match.getWhoWon());
        assertFalse(match.getPlayer1().getHasToPlay());
        assertFalse(match.getPlayer2().getHasToPlay());
        assertEquals(12, match.getBoardDTO().getPits()[Board.PIT_MAIN_P1]);
        assertEquals(36, match.getBoardDTO().getPits()[Board.PIT_MAIN_p2]);

    }


    //================================================================================
    // NOT TESTS - Util functions to setup and clean tests code
    //================================================================================
    private StartMatchDTO createStartMatchDTO(String sessionId) {
        StartMatchDTO startMatchDTO = new StartMatchDTO();
        startMatchDTO.setSession(sessionId);
        startMatchDTO.setPlayer1("Bob");
        startMatchDTO.setPlayer2("John");

        return startMatchDTO;
    }

    private MatchDTO createNewMatch(String sessionId) {
        StartMatchDTO startMatchDTO = createStartMatchDTO(sessionId);
        return matchService.startMatch(startMatchDTO);
    }

    private MatchDTO moveForPlayerAndPit(MatchDTO matchDTO, int playerId, int pit) {
        MovementDTO movementDTO = new MovementDTO();
        movementDTO.setSessionId(matchDTO.getSession());
        movementDTO.setMatchId(matchDTO.getId());
        movementDTO.setPit(pit);
        movementDTO.setPlayerId(playerId);
        return matchService.makeAMove(movementDTO);
    }

    private void clean(MatchDTO matchDTO) {
        FindMatchDTO findMatchDTO = new FindMatchDTO();
        findMatchDTO.setSession(matchDTO.getSession());
        findMatchDTO.setMatchId(matchDTO.getId());

        matchService.deleteMatch(findMatchDTO);
    }
}
