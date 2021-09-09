package com.bol.mancala.controller;

import com.bol.mancala.dto.FindMatchDTO;
import com.bol.mancala.dto.MatchDTO;
import com.bol.mancala.dto.MovementDTO;
import com.bol.mancala.dto.StartMatchDTO;
import com.bol.mancala.exception.InvalidMovementException;
import com.bol.mancala.exception.MyResourceAlreadyExistsException;
import com.bol.mancala.exception.MyResourceNotFoundException;
import com.bol.mancala.exception.MyResourcePermissionDeniedException;
import com.bol.mancala.service.IMatchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;

@Api(value = "Match")
@RestController
public class MatchController {

  @Autowired private IMatchService matchService;

  Logger logger = LoggerFactory.getLogger(MatchController.class);

  @ApiOperation(value = "Creates a new match using your session id")
  @PostMapping("/matches")
  public ResponseEntity<MatchDTO> createNewMatch(
      @RequestBody StartMatchDTO startMatchDTO, HttpServletRequest request) {
    startMatchDTO.setSession(request.getSession().getId());

    MatchDTO newMatch = null;
    try {
      newMatch = this.matchService.startMatch(startMatchDTO);
      return ResponseEntity.ok(newMatch);
    } catch (MyResourceAlreadyExistsException e) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
    }
  }

  @ApiOperation(value = "Retrieves a match with the match id and your session")
  @GetMapping("/matches/{id}")
  public ResponseEntity<MatchDTO> getMatch(@PathVariable String id, HttpServletRequest request) {
    if (id == null) {
      logger.error("Id is missing to find the match");
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id not found as endpoint path parameter");
    }
    FindMatchDTO findMatchDTO = createFindMatchDTO(request, id);

    MatchDTO newMatch = null;
    try {
      newMatch = this.matchService.getMatch(findMatchDTO);
      return ResponseEntity.ok(newMatch);
    } catch (MyResourceNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
    } catch (MyResourcePermissionDeniedException e) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage(), e);
    }
  }

  @ApiOperation(value = "Deletes a match")
  @DeleteMapping("/matches/{id}")
  public ResponseEntity<Void> deleteMatch(@PathVariable String id, HttpServletRequest request) {
    if (id == null) {
      logger.error("Id is missing to find the match");
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id not found as endpoint path parameter");
    }
    FindMatchDTO findMatchDTO = createFindMatchDTO(request, id);

    try {
      this.matchService.deleteMatch(findMatchDTO);
      return ResponseEntity.noContent().build();
    } catch (MyResourceNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
    } catch (MyResourcePermissionDeniedException e) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage(), e);
    }
  }

  @ApiOperation(value = "Make a move in the game")
  @PostMapping("/matches/{id}/move")
  public ResponseEntity<MatchDTO> makeAMove(
      @PathVariable String id, @RequestBody MovementDTO movementDTO, HttpServletRequest request) {
    if (id == null) {
      logger.error("Id is missing to find the match");
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id not found as endpoint path parameter");
    }
    // Set needed data for movementDTO received from request
    movementDTO.setSessionId(request.getSession().getId());
    movementDTO.setMatchId(Integer.parseInt(id));

    MatchDTO updatedMatch = null;
    try {
      updatedMatch = this.matchService.makeAMove(movementDTO);
      return ResponseEntity.ok(updatedMatch);
    } catch (MyResourceNotFoundException e) {
      logger.error("A resource was not found: ", e);
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
    } catch (MyResourcePermissionDeniedException e) {
      logger.error("A resource was tried to be operated without permissions: ", e);
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage(), e);
    } catch (InvalidMovementException e) {
      logger.error("A resource was tried to be operated without permissions: ", e);
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
    }
  }

  private FindMatchDTO createFindMatchDTO(HttpServletRequest request, String matchId) {
    FindMatchDTO findMatchDTO = new FindMatchDTO();
    findMatchDTO.setSession(request.getSession().getId());
    findMatchDTO.setMatchId(Integer.parseInt(matchId));

    return findMatchDTO;
  }
}
