package james.springboot.spring_game.Services;

import james.springboot.spring_game.Models.Board;
import james.springboot.spring_game.Models.Move;
import james.springboot.spring_game.Models.Score;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgentServiceTest {
  AgentService agentService = new AgentService();

  @Mock
  Board board;
  @Mock
  Score score1;
  @Mock
  Score score2;

  //
//  @Test
//  void whenMove_throwsException_validMoveIsReturned() throws InvalidOpenessStateException, Exception {
//    int[][] emptyBoard = new int[10][10];
//    doThrow()when(mockAgentService.findMyMove(eq(emptyBoard), anyInt(), anyInt(), anyInt(), anyInt(), any(), any(), any(), anyInt())).thenThrow(FailedIfException.class);
//
//  }
//
  @Test
  void calculateChangesToLinesCallsFindLinesNextToPlayedMove8Times() {
    Move move = new Move(5, 5);
    agentService.caculateChangesToLines(1, board, score1, score2, move);
    verify(board, times(8)).findLinesNextToPlayedMove(anyInt(), anyInt(), anyInt(), anyInt());
    verify(board, times(1)).findLinesNextToPlayedMove(anyInt(), anyInt(), eq(0), eq(1));
    verify(board, times(1)).findLinesNextToPlayedMove(anyInt(), anyInt(), eq(1), eq(1));
    verify(board, times(1)).findLinesNextToPlayedMove(anyInt(), anyInt(), eq(1), eq(0));
    verify(board, times(1)).findLinesNextToPlayedMove(anyInt(), anyInt(), eq(1), eq(-1));
    verify(board, times(1)).findLinesNextToPlayedMove(anyInt(), anyInt(), eq(0), eq(-1));
    verify(board, times(1)).findLinesNextToPlayedMove(anyInt(), anyInt(), eq(-1), eq(-1));
    verify(board, times(1)).findLinesNextToPlayedMove(anyInt(), anyInt(), eq(-1), eq(0));
    verify(board, times(1)).findLinesNextToPlayedMove(anyInt(), anyInt(), eq(-1), eq(1));
  }
}