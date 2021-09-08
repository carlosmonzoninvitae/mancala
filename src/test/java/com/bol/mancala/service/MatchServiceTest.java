package com.bol.mancala.service;

import com.bol.mancala.dto.*;
import com.bol.mancala.exception.InvalidMovementException;
import com.bol.mancala.exception.MyResourceAlreadyExistsException;
import com.bol.mancala.exception.MyResourceNotFoundException;
import com.bol.mancala.model.Board;
import com.bol.mancala.model.Match;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MatchServiceTest {

    @Autowired
    MatchService matchService;

    private final String sessionId = "fu5e5ytuu6yguoutdecetdrf";


    //================================================================================
    // Creation Tests
    //================================================================================
    @Test
    @DisplayName("Correct creation of a match")
    void testNewMatchCreationSuccessful() {
        //Create match and check that exists
        MatchDTO newMatchDTO = createNewMatch(sessionId);
        assertNotNull(newMatchDTO.getId(), "None match was created");
        //TODO: Add more validations?
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

    //================================================================================
    // Deletion of matches
    //================================================================================

    @Test
    @DisplayName("Deletes an existing match")
    void testDeleteMatchSuccessful() {
        //Create match and check that exists
        MatchDTO match = createNewMatch(sessionId);
        assertTrue(matchService.matchExists(match.getId()));

        DeleteDTO deleteDTO = new DeleteDTO();
        deleteDTO.setSession(sessionId);
        deleteDTO.setMatchId(match.getId());

        //Delete match and check it doesnt exist
        matchService.deleteMatch(deleteDTO);
        assertFalse(matchService.matchExists(match.getId()));
    }

    @Test
    @DisplayName("Trying to delete a non existing match and throw not found exception")
    void testDeleteNonExistingMatch() {
        // Check for non existing match
        Integer nonExistingMatchId = 10;
        assertFalse(matchService.matchExists(nonExistingMatchId));

        DeleteDTO deleteDTO = new DeleteDTO();
        deleteDTO.setSession(sessionId);
        deleteDTO.setMatchId(nonExistingMatchId);

        // Try to delete it and throw exception
        assertThrows(MyResourceNotFoundException.class, () -> {
            matchService.deleteMatch(deleteDTO);
        });
    }


    //======================================//######12#11##10###9##8##7######//
    // Play moves                           //#13###BOARD REPRESENTATION###6#//
    //======================================//#######0##1###2###3##4##5######//
    // Note: The pit values that will be asserted are just the relevant ones for the test results and logic.
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
    @DisplayName("Make the first move with Player 1 selecting the first pit")
    void makeAMoveWithNoOwnMatch() {
        // Create a new match
        MatchDTO match = createNewMatch(sessionId);
        assertTrue(matchService.matchExists(match.getId()));

        // Play with a different session
        match.setSession("DifferentSession");
        assertThrows(InvalidMovementException.class, () -> {
            moveForPlayerAndPit(match, match.getPlayer1().getId(), 3);
        });

        clean(match);
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
    @DisplayName("Make the first move with Player 1 and fail trying to move again being his turn")
    void makeAMoveAndCanNotPlayAgainInAnothersOneTurnFail() {
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
    @DisplayName("Retrieves match")
    void retrievesMatchSuccessful() {
        MatchDTO match = createNewMatch(sessionId);
        assertTrue(matchService.matchExists(match.getId()));

        MatchDTO retrievedMatch = matchService.getMatch(match.getId());
        assertEquals(match, retrievedMatch);

        clean(match);
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
        movementDTO.setMatchId(matchDTO.getId());
        movementDTO.setPit(pit);
        movementDTO.setPlayerId(playerId);
        return matchService.makeAMove(movementDTO);
    }

    private void clean(MatchDTO matchDTO) {
        DeleteDTO deleteDTO = new DeleteDTO();
        deleteDTO.setSession(matchDTO.getSession());
        deleteDTO.setMatchId(matchDTO.getId());

        matchService.deleteMatch(deleteDTO);
    }
}
