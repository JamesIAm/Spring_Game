package james.springboot.spring_game.Services;

import james.springboot.spring_game.Models.*;
import james.springboot.spring_game.Utilities.Utilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

// @Getter
@Service
@Slf4j
public class AgentService {
    private final Integer ID = 2;
    private final Integer BOARD_SIZE = 10;
    private final Integer X_IN_A_LINE = 5;
    private final Integer MAX_DEPTH = 10; // Maximum depth the program will search to. Must be greater than 1

    private final Integer SEARCH_RADIUS = 1; // How far around the current min and max co-ords the system looks for new
    // moves. At 1, a tile on 3,3 will result in a search of 2,2 to 4,4
    private final Integer[] INDEXES = {(X_IN_A_LINE + 1) - 1, ((X_IN_A_LINE + 1) * 2) - 1,
            ((X_IN_A_LINE + 1) * 3) - 1}; // These indicate, of all the scores,
    // which ones indicate a win situation. 5 in a row (enclosed on both sides), 5
    // in a row (enclosed on one side), and 5 in a row (not enclosed)
    private final Integer SEARCH_TIME = 5; // How many seconds the system searches before returning it's best solution
    // private final Integer counter = 0
    private ArrayList<Move> priorityMoves = new ArrayList<>();
    // Contains a list of moves that should be searched first (continuations of the
    // line of the last move played)
    private ArrayList<ArrayList<Pair<Integer, Integer>>> horizontalCoords = new ArrayList<>();
    private ArrayList<ArrayList<Pair<Integer, Integer>>> verticalCoords = new ArrayList<>();
    private ArrayList<ArrayList<Pair<Integer, Integer>>> upDiagCoords = new ArrayList<>();
    private ArrayList<ArrayList<Pair<Integer, Integer>>> downDiagCoords = new ArrayList<>();
    private Long startTime;

    public AgentService() {
        Utilities.defineOrders(this.BOARD_SIZE, this.horizontalCoords, this.verticalCoords, this.upDiagCoords, this.downDiagCoords);
    }


    // Tries to find a move within a given time.
    // Iterates depth of search so if it runs out of time, it will default to the
    // last found best move
    // If the program throws any error it defaults to a valid value
    public Move move(final int[][] board) {
        Move prevBestMove = null;
        //Starts off with a valid move
        for (int y = 0; y < this.BOARD_SIZE; y++) {
            if (prevBestMove != null) {
                break;
            }
            for (int x = 0; x < this.BOARD_SIZE; x++) {
                if (prevBestMove != null) {
                    break;
                }
                if (board[y][x] == 0) {
                    prevBestMove = new Move(x, y);
                }
            }
        }

        try {
            this.startTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

            // this.counter = 0
            Score myStartScore = this.countLines(board, this.ID);
            Score theirStartScore = this.countLines(board, 1);
            for (int depth = 1; depth < this.MAX_DEPTH; depth++) {
                Move bestMove = this.findMyMove(board, 0, depth, -10000, 10000, myStartScore, theirStartScore,
                        new ArrayList<>(), this.ID);
                if (bestMove == null || bestMove.score == 0) {
//                    break;
                } else {
                    log.info("Depth: " + depth + " complete");
                    // print("Likelihood of winning: ", bestScore, "\tBest Move", bestMove,
                    // "\tDepth", depth) // Shows the progression of the algorithm
                    prevBestMove = bestMove;
                }
            }
            // print(bestMove)
            // print("Counter = ", this.counter)
            return prevBestMove;
        } catch (Exception e) {
            log.error("Agent main loop error: " + e.getMessage());
            log.error("Agent main loop error2: " + Arrays.toString(e.getStackTrace()));
            return prevBestMove;
            // print("Error occurred, default value returned\nError -", e)// Logs occurance
            // of an error


        }
    }

    // Finds the next move for the current player. This will call findTheirMove,
    // which will again call findMyMove
    // until maxDepth is reached
    // It searches depth first, and uses alpha beta pruning to cut down on the
    // number of searched nodes
    public Move findMyMove(int[][] board, int depth, int maxDepth, Integer alpha, Integer beta,
                           Score prevThisPlayerScore, Score prevOtherPlayerScore, ArrayList<Move> priorityMoves, int id) throws Exception {
        if (LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) > (this.startTime + this.SEARCH_TIME)) {
            throw new TimeoutException();
        }
        String depthSpace = new String(new char[depth]).replace("\0", "\t");
        if (depth <= maxDepth) {
            int otherPlayerId = id == 1 ? 2 : 1;
            // print("Alpha: ", alpha, "\tBeta: ", beta)
            //TDOD: VALIDMOVES INCLUDES ALREADY DONE MOVES???
            ArrayList<Move> validMoves = this.findMoves(board, priorityMoves);
            Move bestMove = new Move(-10000);
            for (Move move : validMoves) {
                log.debug(depthSpace + move.x + " " + move.y);
                Pair<Score, ArrayList<Move>> thisPlayerChange = this.countChangeAdd(id, board,
                        prevThisPlayerScore, move);
                Pair<Score, ArrayList<Move>> otherPlayerChange = this.countChangeMinus(otherPlayerId, board,
                        prevOtherPlayerScore, move);
                Score thisPlayerScore = thisPlayerChange.a;
                Score otherPlayerScore = otherPlayerChange.a;
                ArrayList<Move> newPriorityMoves = thisPlayerChange.b;
                newPriorityMoves.addAll(otherPlayerChange.b);
                int[][] simulatedBoard = this.simulateMove(board, move.x, move.y, id);
                if (thisPlayerScore.winCheck()) {
                    // Check if the game is won
                    move.score = 1000;
                    log.debug(depthSpace + 1000);
                    return move;
                } else {
                    // If not won, go a layer deeper and calculate their move
                    Move newMove = this.findMyMove(simulatedBoard, depth + 1, maxDepth, alpha, beta,
                            otherPlayerScore.clone(), thisPlayerScore.clone(), newPriorityMoves, otherPlayerId);
                    move.score = newMove.score * -1;
                    if (bestMove.score == -10000) {
                        bestMove = move;
                    }
                    if (move.score != 0) {
                        // Initialise the best score
                        // Update the best score if it's higher than the current best
                        if (move.score > bestMove.score) {
                            bestMove = move;
                        }
                        if (bestMove.score >= beta) {
                            break;
                        }
                        if (id == this.ID){
                            if (bestMove.score < alpha) {
                                break;
                            }
                        }
                        // If time is up, return 0 (causes the move() function to return the best found
                        // previous move)
                        // if (depth==2):
                        // print(bestScore, bestMove)
                    }
                }
            }
            log.debug(depthSpace + "bestMove:" + bestMove.score + " " + bestMove.x + " " + bestMove.y);
            return bestMove;
        } else {
            // At max depth, calculate the score based on the number of lines of differing
            // lengths it has
            // this.counter+=1
            // int myScore = prevThisPlayerScore;
            // int theirScore = prevOtherPlayerScore;
            // TODO:
            int sum = 0;
            //TODO: WHAT IS THIS PLAYER, WHAT IS NEXT PLAYER
            int justPlayedPlayerScore = prevOtherPlayerScore.calculateScore(true);
            int aboutToPlayPlayerScore = prevThisPlayerScore.calculateScore(false);
            sum += aboutToPlayPlayerScore;
            sum -= justPlayedPlayerScore;
            log.debug(depthSpace + sum);
            return new Move(sum);
        }
    }

    // Almost identical to findMyMove, but separated out to make tracking the ID
    // simpler.
    // In hindsight, separating these two was a mistake, as a number of changes had
    // to be done to both functions
    // public Move findTheirMove(int[][]board, int depth, int maxDepth, int
    // alpha, int beta, Integer[] prevThisPlayerScore,
    // Integer[] prevOtherPlayerScore, ArrayList<Move> priorityMoves) {
    // if(time()>(this.startTime+this.SEARCH_TIME)){return
    // 0,None;}if(depth<maxDepth){validMoves=this.findMoves(board,priorityMoves.copy());bestScore=None;bestMove=();for(int
    // move:validMoves){thisPlayerScore,priorityMoves1=this.countChangeAdd(this.ID*-1,board,prevThisPlayerScore.copy(),move);otherPlayerScore,priorityMoves2=this.countChangeMinus(this.ID,board,prevOtherPlayerScore.copy(),move);newPriorityMoves=priorityMoves1+priorityMoves2;simulatedBoard=this.simulateMove(board,move[1],move[0],this.ID*-1);if(thisPlayerScore[this.INDEXES[0]]>0
    // or thisPlayerScore[this.INDEXES[1]]>0 or
    // thisPlayerScore[this.INDEXES[2]]>0){return-1000,move;}else{score,newMove=this.findMyMove(simulatedBoard,depth+1,maxDepth,alpha,beta,otherPlayerScore,thisPlayerScore,newPriorityMoves);if(score==None){break;}else{if(bestScore==None){bestScore=score;bestMove=move;}
    // // pick the minimum score
    // if(score<bestScore){bestScore=score;bestMove=move;}if(bestScore<=alpha){ //
    // If the score is less than the
    // // current max path, return the score
    // // and trim the branch
    // break;}if(bestScore<beta){beta=bestScore;}}}if(time()>(this.startTime+this.SEARCH_TIME)){return
    // 0,None;}else{return bestScore,bestMove;}}else{
    // // this.counter+=1
    // myScore=prevOtherPlayerScore;theirScore=prevThisPlayerScore;return(sum(this.CURRENT_PLAYER_WEIGHTS*myScore)-sum(this.NEXT_PLAYER_WEIGHTS*theirScore)),[];}
    // }

    // Returns a board with an additional square filled in at the x and y coords
    public int[][] simulateMove(int[][] board, int x, int y, int id) {
        int[][] simulatedBoard = Utilities.deepCopyDoubleIntArray(board, BOARD_SIZE, BOARD_SIZE);
        simulatedBoard[y][x] = id;
        return simulatedBoard;
    }

    // Old count lines function, only used for the initial count as it's much slower
    public Score countLines(int[][] board, int id) {
        Score score = new Score(X_IN_A_LINE);
        this.countLinesInDirection(board, this.horizontalCoords, id, score);
        this.countLinesInDirection(board, this.verticalCoords, id, score);
        this.countLinesInDirection(board, this.downDiagCoords, id, score);
        this.countLinesInDirection(board, this.upDiagCoords, id, score);

        return score; // Fix
        // to
        // have
        // weights
    }

    // Goes through row by row, and measures the size of every consectutive line and
    // whether it's open ended or not
    public void countLinesInDirection(int[][] board, ArrayList<ArrayList<Pair<Integer, Integer>>> orderedItems,
                                      int id, Score score) {
        int length = 0;
        Openess openEnded = Openess.CLOSED;
        int lastNumber = -1; // Represents the last number observed, to see if lines are open ended or not.
        // At the start of every line it defaults to the -1, so lines
        // that start at the edge of the board are not open ended
        for (ArrayList<Pair<Integer, Integer>> line : orderedItems) {
            for (Pair<Integer, Integer> cell : line) {
                int currentCell = board[cell.b][cell.a];
                //If next tile is this players id, increment the length
                if (currentCell == id) {
                    if (length == 0 && lastNumber == 0) {
                        openEnded = Openess.SEMI;
                    }
                    length += 1;
                } else if (length > 0) {
                    if (openEnded == Openess.SEMI && currentCell == 0) {
                        score.incrementScore(Openess.OPEN, length);
                    } else if (openEnded == Openess.SEMI || currentCell == 0) {
                        score.incrementScore(Openess.SEMI, length);
                    } else {
                        score.incrementScore(Openess.CLOSED, length);
                    }
                    length = 0;
                    openEnded = Openess.CLOSED;
                }
                lastNumber = currentCell;

            }
            if (length > 0) {
                if (openEnded != Openess.CLOSED) {
                    score.incrementScore(Openess.SEMI, length);
                } else {
                    score.incrementScore(Openess.CLOSED, length);
                }
            }
            openEnded = Openess.CLOSED;
            length = 0;
            lastNumber = -1;
        }

    }

    // Counts how the scores change when adding the move to the board.
    // Extends and joins any lines that attach to the move.
    public Pair<Score, ArrayList<Move>> countChangeAdd(int id, int[][] prevBoard, Score prevScore,
                                                       Move move) throws Exception {
        this.priorityMoves = new ArrayList<>();
        Score score = prevScore.clone();
        Integer y = move.y, x = move.x,
                horLength = 1, verLength = 1, upDiLength = 1, doDiLength = 1, horOpeness = 0, verOpeness = 0,
                upDiOpeness = 0, doDiOpeness = 0;
        Triplet<Integer, Integer, Integer> prevIndexLengthOpeness;
        // TODO: Does openness pass by value or reference, should pass by refernce
        do2LinesIn1RenameMe(prevBoard, id, x, y, score, 0, 1);
        do2LinesIn1RenameMe(prevBoard, id, x, y, score, 1, 1);
        do2LinesIn1RenameMe(prevBoard, id, x, y, score, 1, 0);
        do2LinesIn1RenameMe(prevBoard, id, x, y, score, 1, -1);
        do2LinesIn1RenameMe(prevBoard, id, x, y, score, 0, -1);
        do2LinesIn1RenameMe(prevBoard, id, x, y, score, -1, -1);
        do2LinesIn1RenameMe(prevBoard, id, x, y, score, -1, 0);
        do2LinesIn1RenameMe(prevBoard, id, x, y, score, -1, 1);
        return new Pair<>(score, this.priorityMoves);
    }

    private void do2LinesIn1RenameMe(int[][] prevBoard, int playerId, Integer newMoveX, Integer newMoveY, Score score, int xChange, int yChange) throws Exception {
        Pair<Integer, Openess> scoreDataPositive = this.calculateNewLines(prevBoard, playerId, newMoveX, newMoveY, xChange, yChange);
        Pair<Integer, Openess> scoreDataNegative = this.calculateNewLines(prevBoard, playerId, newMoveX, newMoveY, xChange * -1, yChange * -1);
//        if (scoreDataPositive.a + scoreDataNegative.a == 4) {
//            //TODO: Debug on this line
//            int a = 1;
//        }
        score.increaseScore(scoreDataPositive, scoreDataNegative);
    }

    // For each direction, searches how long the new line is, subtracts one score
    // from length-1
    // And adds 1 score to the length
    //Triplet return is:
    //1:
    public Pair<Integer, Openess> calculateNewLines(int[][] board, int currentPlayerId, int x, int y, int xChange, int yChange) throws Exception {
        for (int previousLineLength = 1; previousLineLength < this.X_IN_A_LINE + 1; previousLineLength++) {// Check positive horizontal
            int newX = x + (xChange * previousLineLength);
            int newY = y + (yChange * previousLineLength);
            if (newX < this.BOARD_SIZE && newY < this.BOARD_SIZE && newX >= 0 && newY >= 0) {
                int value = board[newY][newX];
                if (value == 0) {
                    // If it is now semi open. The previous line was open
                    // this.priorityMoves.append(tuple(newY, newX))
                    this.priorityMoves.add(new Move(newX, newY));
                    //-1 as the current value is not part of the line
                    //Will never be less than 1 as previous line length starts from 0
                    return new Pair<>((previousLineLength - 1), Openess.SEMI);
                } else if (value != currentPlayerId) {
                    // If it is now closed, the previous line was semi open
                    return new Pair<>((previousLineLength - 1), Openess.CLOSED);
                }
            } else {
                return new Pair<>((previousLineLength - 1), Openess.CLOSED);
            }
        }
        throw new Exception("Failed in calculate new lines function");
    }

    // Finds lines that are no longer open ended and adjusts the score accordingly
    public Pair<Score, ArrayList<Move>> countChangeMinus(int id, int[][] prevBoard, Score prevScore,
                                                         Move move) throws Exception {
        this.priorityMoves = new ArrayList<>();
        // TODO: Reset priority moves twice?
        Score score = prevScore.clone();
        int y = move.y;
        int x = move.x;
        for (int[] xyChange : new int[][]{{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {1, 1}, {1, -1},
                {1, 0}, {1, 1}}) {
            // print(this.calculateOldLines(prevBoard, id, x, y, score, xChange, yChange))
            Pair<Openess, Integer> oldLineData = this.calculateOldLines(prevBoard, id, x, y, score, xyChange[0],
                    xyChange[0]);
            // print(prevIndex, newIndex, "returned")
            if (oldLineData.b > 0) {
                score.decreaseOpeness(oldLineData);
            }

        }
        return new Pair<>(score, this.priorityMoves);
    }

    // Finds the old index of the lines and returns a new index for them as well
    public Pair<Openess, Integer> calculateOldLines(int[][] board, int id, int x, int y, Score prevScore, Integer xChange, Integer yChange) throws Exception {
        // print("Changes", xChange, yChange)
        for (int previousLineLength = 1; previousLineLength < this.X_IN_A_LINE + 1; previousLineLength++) {// Check positive horizontal
            int newX = x + (xChange * previousLineLength);
            int newY = y + (yChange * previousLineLength);
            // print(newY, newX)
            if (newX < this.BOARD_SIZE && newY < this.BOARD_SIZE && newX >= 0 && newY >= 0) {
                int value = board[newY][newX];
                if (value == 0) {
                    // If it is now semi open. The previous line was open
                    this.priorityMoves.add(new Move(newX, newY));
                    //  print(this.priorityMoves)
                    return new Pair<>(Openess.OPEN, previousLineLength - 1);
                } else if (value != id) {
                    // If it is now closed, the previous line was semi open
                    return new Pair<>(Openess.SEMI, previousLineLength - 1);
                }
                //  else:
                //      print(value, i)
                //      print(board)
                //      print(newY, newX)
            } else {
                //  print(((i-1)+(this.X_IN_A_LINE+1)), (i-1))
                return new Pair<>(Openess.SEMI, previousLineLength - 1);
            }
        }
        return new Pair<>(Openess.CLOSED, 0);
    }

    // Finds moves that are valid to explore, only finds moves plus minus one of the
    // total area currently played in
    // Starts with any lines that were made longer or shorter by the last move.
    // (From priorityMoves)
    public ArrayList<Move> findMoves(int[][] board, ArrayList<Move> priorityMoves) {
        int minX = this.BOARD_SIZE;
        int minY = this.BOARD_SIZE;
        int maxX = 0;
        int maxY = 0;
        ArrayList<Move> validMoves = new ArrayList<>();//TODO:priorityMoves);
        for (int y = 0; y < this.BOARD_SIZE; y++) {
            for (int x = 0; x < this.BOARD_SIZE; x++) {
                if (board[y][x] != 0) {
                    if (x < minX) {
                        minX = x;
                    }
                    if (x >= maxX) {
                        maxX = x + 1;
                    }
                    if (y < minY) {
                        minY = y;
                    }
                    if (y >= maxY) {
                        maxY = y + 1;
                    }
                }
            }
        }
        minX = Math.max(minX - this.SEARCH_RADIUS, 0);
        minY = Math.max(minY - this.SEARCH_RADIUS, 0);
        maxX = Math.min(maxX + this.SEARCH_RADIUS, this.BOARD_SIZE);
        maxY = Math.min(maxY + this.SEARCH_RADIUS, this.BOARD_SIZE);
        for (int y = minY; y < maxY; y++) {
            for (int x = minX; x < maxX; x++) {
                if (board[y][x] == 0) {//  and (y,x) not in validMoves):
                    validMoves.add(new Move(x, y));
                }
            }
        }
        if (validMoves.size() == 0) {
            if (maxX > minX) {
                return new ArrayList<>();
            } else {
                ArrayList<Move> moves = new ArrayList<>();
                moves.add(new Move(1, 1));// Set default starting move
                return moves;
            }
        }
        return validMoves;
    }
}