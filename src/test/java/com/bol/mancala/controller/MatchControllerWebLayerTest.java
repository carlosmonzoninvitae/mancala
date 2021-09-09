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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.junit.jupiter.api.Assertions.assertEquals;

/** This class just tests the WebLayer, the response of the IMatchService is not relevant. */
@AutoConfigureMockMvc
@WebMvcTest
public class MatchControllerWebLayerTest {

  private final String CREATE_MATCH_URL = "/matches";
  private final String GET_MATCH_URL = "/matches/12345";
  private final String DELETE_MATCH_URL = "/matches/12345";
  private final String MAKE_A_MOVE_URL = "/matches/12345/move";

  private final String SOME_ERROR_MESSAGE = "Some message";

  @Autowired private MockMvc mockMvc;

  @MockBean IMatchService matchService;

  // ================================================================================
  // Match Creation Web Layer Tests
  // ================================================================================
  @Test
  @DisplayName(
      "Match creation WebLayer Test - Verifies JSONContent and HttpStatusCode OK(200) when IMatchServices does not throw any Exception")
  public void createNewMatch() throws Exception {
    // IMatchService will give a result without any problems
    Mockito.when(matchService.startMatch(Mockito.any(StartMatchDTO.class)))
        .thenReturn(new MatchDTO());

    // Prepare builder with controller URL and parameters
    RequestBuilder builder =
        MockMvcRequestBuilders.post(CREATE_MATCH_URL)
            .content(new ObjectMapper().writeValueAsString(new StartMatchDTO()))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON);

    // Do the call and check response
    MockHttpServletResponse mockResponse = mockMvc.perform(builder).andReturn().getResponse();
    assertEquals(mockResponse.getContentType(), MediaType.APPLICATION_JSON_VALUE);
    assertEquals(mockResponse.getStatus(), HttpStatus.OK.value());
  }

  @Test
  @DisplayName(
      "Match creation WebLayer Test - Verifies HttpStatusCode CONFLICT(409) and ErrorMessage when IMatchServices throws MyResourceAlreadyExistsException")
  public void createNewMatchWithAlreadyExistsException() throws Exception {
    // IMatchService will give a result without any problems
    Mockito.when(matchService.startMatch(Mockito.any(StartMatchDTO.class)))
        .thenThrow(new MyResourceAlreadyExistsException(SOME_ERROR_MESSAGE));

    // Prepare builder with controller URL and parameters
    RequestBuilder builder =
        MockMvcRequestBuilders.post(CREATE_MATCH_URL)
            .content(new ObjectMapper().writeValueAsString(new StartMatchDTO()))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON);

    // Do the call and check response
    MockHttpServletResponse mockResponse = mockMvc.perform(builder).andReturn().getResponse();
    assertEquals(mockResponse.getStatus(), HttpStatus.CONFLICT.value());
    assertEquals(mockResponse.getErrorMessage(), SOME_ERROR_MESSAGE);
  }

  // ================================================================================
  // Match Retrieval Web Layer Tests
  // ================================================================================
  @Test
  @DisplayName(
      "Match Get WebLayer Test - Verifies JSONContent and HttpStatusCode OK(200) when IMatchServices does not throw any Exception")
  public void getMatch() throws Exception {
    // IMatchService will give a result without any problems
    Mockito.when(matchService.getMatch(Mockito.any(FindMatchDTO.class))).thenReturn(new MatchDTO());

    // Prepare builder with controller URL and parameters
    RequestBuilder builder =
        MockMvcRequestBuilders.get(GET_MATCH_URL)
            .content(new ObjectMapper().writeValueAsString(new FindMatchDTO()))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON);

    // Do the call and check response
    MockHttpServletResponse mockResponse = mockMvc.perform(builder).andReturn().getResponse();
    assertEquals(mockResponse.getContentType(), MediaType.APPLICATION_JSON_VALUE);
    assertEquals(mockResponse.getStatus(), HttpStatus.OK.value());
  }

  @Test
  @DisplayName(
      "Match Get WebLayer Test - Verifies HttpStatusCode NOT_FOUND(404) and ErrorMessage when IMatchServices throws MyResourceNotFoundException")
  public void getMatchNotFoundException() throws Exception {
    // IMatchService will give a result without any problems
    Mockito.when(matchService.getMatch(Mockito.any(FindMatchDTO.class)))
        .thenThrow(new MyResourceNotFoundException(SOME_ERROR_MESSAGE));

    // Prepare builder with controller URL and parameters
    RequestBuilder builder =
        MockMvcRequestBuilders.get(GET_MATCH_URL)
            .content(new ObjectMapper().writeValueAsString(new FindMatchDTO()))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON);

    // Do the call and check response
    MockHttpServletResponse mockResponse = mockMvc.perform(builder).andReturn().getResponse();
    assertEquals(mockResponse.getStatus(), HttpStatus.NOT_FOUND.value());
    assertEquals(mockResponse.getErrorMessage(), SOME_ERROR_MESSAGE);
  }

  @Test
  @DisplayName(
      "Match Get WebLayer Test - Verifies HttpStatusCode UNAUTHORIZED(401) and ErrorMessage when IMatchServices throws MyResourcePermissionDeniedException")
  public void getMatchPermissionDeniedException() throws Exception {
    // IMatchService will give a result without any problems
    Mockito.when(matchService.getMatch(Mockito.any(FindMatchDTO.class)))
        .thenThrow(new MyResourcePermissionDeniedException(SOME_ERROR_MESSAGE));

    // Prepare builder with controller URL and parameters
    RequestBuilder builder =
        MockMvcRequestBuilders.get(GET_MATCH_URL)
            .content(new ObjectMapper().writeValueAsString(new FindMatchDTO()))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON);

    // Do the call and check response
    MockHttpServletResponse mockResponse = mockMvc.perform(builder).andReturn().getResponse();
    assertEquals(mockResponse.getStatus(), HttpStatus.UNAUTHORIZED.value());
    assertEquals(mockResponse.getErrorMessage(), SOME_ERROR_MESSAGE);
  }

  // ================================================================================
  // Match Delete Web Layer Tests
  // ================================================================================
  @Test
  @DisplayName(
      "Match Delete WebLayer Test - Verifies HttpStatusCode NO_CONTENT(204) when IMatchServices does not throw any Exception")
  public void deleteMatch() throws Exception {
    // IMatchService will give a result without any problems
    Mockito.doNothing().when(matchService).deleteMatch(Mockito.any(FindMatchDTO.class));

    // Prepare builder with controller URL and parameters
    RequestBuilder builder =
        MockMvcRequestBuilders.delete(DELETE_MATCH_URL)
            .content(new ObjectMapper().writeValueAsString(new FindMatchDTO()))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON);

    // Do the call and check response
    MockHttpServletResponse mockResponse = mockMvc.perform(builder).andReturn().getResponse();
    assertEquals(mockResponse.getStatus(), HttpStatus.NO_CONTENT.value());
  }

  @Test
  @DisplayName(
      "Match Delete WebLayer Test - Verifies HttpStatusCode UNAUTHORIZED(401) and ErrorMessage when IMatchServices throws MyResourcePermissionDeniedException")
  public void deleteMatchWithPermissionDeniedException() throws Exception {
    // IMatchService will give a result without any problems
    Mockito.doThrow(new MyResourcePermissionDeniedException(SOME_ERROR_MESSAGE))
        .when(matchService)
        .deleteMatch(Mockito.any(FindMatchDTO.class));

    // Prepare builder with controller URL and parameters
    RequestBuilder builder =
        MockMvcRequestBuilders.delete(DELETE_MATCH_URL)
            .content(new ObjectMapper().writeValueAsString(new FindMatchDTO()))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON);

    // Do the call and check response
    MockHttpServletResponse mockResponse = mockMvc.perform(builder).andReturn().getResponse();
    assertEquals(mockResponse.getStatus(), HttpStatus.UNAUTHORIZED.value());
    assertEquals(mockResponse.getErrorMessage(), SOME_ERROR_MESSAGE);
  }

  @Test
  @DisplayName(
      "Match Delete WebLayer Test - Verifies HttpStatusCode NOT_FOUND(404) and ErrorMessage when IMatchServices throws MyResourceNotFoundException")
  public void deleteMatchWithNotFoundException() throws Exception {
    // IMatchService will give a result without any problems
    Mockito.doThrow(new MyResourceNotFoundException(SOME_ERROR_MESSAGE))
        .when(matchService)
        .deleteMatch(Mockito.any(FindMatchDTO.class));

    // Prepare builder with controller URL and parameters
    RequestBuilder builder =
        MockMvcRequestBuilders.delete(DELETE_MATCH_URL)
            .content(new ObjectMapper().writeValueAsString(new FindMatchDTO()))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON);

    // Do the call and check response
    MockHttpServletResponse mockResponse = mockMvc.perform(builder).andReturn().getResponse();
    assertEquals(mockResponse.getStatus(), HttpStatus.NOT_FOUND.value());
    assertEquals(mockResponse.getErrorMessage(), SOME_ERROR_MESSAGE);
  }

  // ================================================================================
  // Match Move Web Layer Tests
  // ================================================================================
  @Test
  @DisplayName(
      "Match Move WebLayer Test - Verifies JSONContent and HttpStatusCode OK(200) when IMatchServices does not throw any Exception")
  public void makeAMove() throws Exception {
    // IMatchService will give a result without any problems
    Mockito.when(matchService.makeAMove(Mockito.any(MovementDTO.class))).thenReturn(new MatchDTO());

    // Prepare builder with controller URL and parameters
    RequestBuilder builder =
        MockMvcRequestBuilders.post(MAKE_A_MOVE_URL)
            .content(new ObjectMapper().writeValueAsString(new MatchDTO()))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON);

    // Do the call and check response
    MockHttpServletResponse mockResponse = mockMvc.perform(builder).andReturn().getResponse();
    assertEquals(mockResponse.getContentType(), MediaType.APPLICATION_JSON_VALUE);
    assertEquals(mockResponse.getStatus(), HttpStatus.OK.value());
  }

  @Test
  @DisplayName(
      "Make a move WebLayer Test - Verifies HttpStatusCode NOT_FOUND(404) and ErrorMessage when IMatchServices throws MyResourceNotFoundException")
  public void makeAMoveWithNotFoundException() throws Exception {
    // IMatchService will give a result without any problems
    Mockito.when(matchService.makeAMove(Mockito.any(MovementDTO.class)))
        .thenThrow(new MyResourceNotFoundException(SOME_ERROR_MESSAGE));

    // Prepare builder with controller URL and parameters
    RequestBuilder builder =
        MockMvcRequestBuilders.post(MAKE_A_MOVE_URL)
            .content(new ObjectMapper().writeValueAsString(new FindMatchDTO()))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON);

    // Do the call and check response
    MockHttpServletResponse mockResponse = mockMvc.perform(builder).andReturn().getResponse();
    assertEquals(mockResponse.getStatus(), HttpStatus.NOT_FOUND.value());
    assertEquals(mockResponse.getErrorMessage(), SOME_ERROR_MESSAGE);
  }

  @Test
  @DisplayName(
      "Make a move WebLayer Test - Verifies HttpStatusCode UNAUTHORIZED(401) and ErrorMessage when IMatchServices throws MyResourcePermissionDeniedException")
  public void makeAMoveWithPermissionDeniedException() throws Exception {
    // IMatchService will give a result without any problems
    Mockito.when(matchService.makeAMove(Mockito.any(MovementDTO.class)))
        .thenThrow(new MyResourcePermissionDeniedException(SOME_ERROR_MESSAGE));

    // Prepare builder with controller URL and parameters
    RequestBuilder builder =
        MockMvcRequestBuilders.post(MAKE_A_MOVE_URL)
            .content(new ObjectMapper().writeValueAsString(new FindMatchDTO()))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON);

    // Do the call and check response
    MockHttpServletResponse mockResponse = mockMvc.perform(builder).andReturn().getResponse();
    assertEquals(mockResponse.getStatus(), HttpStatus.UNAUTHORIZED.value());
    assertEquals(mockResponse.getErrorMessage(), SOME_ERROR_MESSAGE);
  }

  @Test
  @DisplayName(
      "Make a move WebLayer Test - Verifies HttpStatusCode BAD_REQUEST(400) and ErrorMessage when IMatchServices throws InvalidMovementException")
  public void makeAMoveWithInvalidMovementException() throws Exception {
    // IMatchService will give a result without any problems
    Mockito.when(matchService.makeAMove(Mockito.any(MovementDTO.class)))
        .thenThrow(new InvalidMovementException(SOME_ERROR_MESSAGE));

    // Prepare builder with controller URL and parameters
    RequestBuilder builder =
        MockMvcRequestBuilders.post(MAKE_A_MOVE_URL)
            .content(new ObjectMapper().writeValueAsString(new FindMatchDTO()))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON);

    // Do the call and check response
    MockHttpServletResponse mockResponse = mockMvc.perform(builder).andReturn().getResponse();
    assertEquals(mockResponse.getStatus(), HttpStatus.BAD_REQUEST.value());
    assertEquals(mockResponse.getErrorMessage(), SOME_ERROR_MESSAGE);
  }
}
