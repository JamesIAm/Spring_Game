package james.springboot.spring_game.Services;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.stream.Stream;

import james.springboot.spring_game.Move;
import james.springboot.spring_game.Pair;
import lombok.Getter;

// @Getter
public class AgentService {
    private final Integer ID = 1;
    private final Integer BOARD_SIZE = 10;
    private final Integer X_IN_A_LINE = 5;
    private final Integer MAX_DEPTH = 10; // Maximum depth the program will search to. Must be greater than 1
    private final Integer[] NEXT_PLAYER_WEIGHTS = { 0, 0, 0, 0, 0, 1000, 0, 0, 2, 10, 500, 1000, 0, 0, 10, 100, 500,
            1000 };// Indicates the weights that scores are multiplied by.
    // The scores represent how many lines of a certain length and certain openess
    // (whether the two ends are blocked) exist in the board
    private final Integer[] CURRENT_PLAYER_WEIGHTS = { 0, 0, 0, 0, 0, 1000, 0, 0, 2, 10, 50, 1000, 0, 0, 10, 50, 200,
            1000 };
    private final Integer SEARCH_RADIUS = 1; // How far around the current min and max co-ords the system looks for new
                                             // moves. At 1, a tile on 3,3 will result in a search of 2,2 to 4,4
    private final Integer[] INDEXES = { (X_IN_A_LINE + 1) - 1, ((X_IN_A_LINE + 1) * 2) - 1,
            ((X_IN_A_LINE + 1) * 3) - 1 }; // These indicate, of all the scores,
    // which ones indicate a win situation. 5 in a row (enclosed on both sides), 5
    // in a row (enclosed on one side), and 5 in a row (not enclosed)
    private final Integer SEARCH_TIME = 5; // How many seconds the system searches before returning it's best solution
    // private final Integer counter = 0
    private final ArrayList<Move> priorityMoves = new ArrayList<Move>();
    // Contains a list of moves that should be searched first (continuations of the
    // line of the last move played)
    private ArrayList<ArrayList<Pair<Integer, Integer>>> horizontalCoords = new ArrayList<>();
    private ArrayList<ArrayList<Pair<Integer, Integer>>> verticalCoords = new ArrayList<>();
    private ArrayList<ArrayList<Pair<Integer, Integer>>> upDiagCoords = new ArrayList<>();
    private ArrayList<ArrayList<Pair<Integer, Integer>>> downDiagCoords = new ArrayList<>();
    private Long startTime;

    public AgentService() {
        defineOrders();
    }

    // Creates the order that the board should be searched for existing lines. This
    // was from a previous iteration of the line
    // search that searched the entire board every time
    // It defines an order to search over the data so that each row goes vertically,
    // horizonatally or diagonally
    public void defineOrders() {
        for (int y = 0; y < this.BOARD_SIZE; y++) {
            ArrayList<Pair<Integer, Integer>> lineOfMoves = new ArrayList<>();
            for (int x = 0; x < this.BOARD_SIZE; x++) {
                lineOfMoves.add(new Pair<Integer, Integer>(x, y));

            }
            this.horizontalCoords.add(lineOfMoves);
        }

        // TODO:
        // this.verticalCoords=np.swapaxes(this.horizontalCoords,0,1);
        // TODO:
        // ArrayList<Integer>downDiagTemp=new ArrayList<Integer>();for(int
        // x=1-this.BOARD_SIZE;x<this.BOARD_SIZE;x++){downDiagTemp.append(np.diag(this.horizontalCoords,x))}this.downDiagCoords=np.array(downDiagTemp,dtype=object)
        // TODO:
        // ArrayList<Integer>upDiagTemp=new
        // ArrayList<Integer>();flippedCoords=np.fliplr(this.horizontalCoords);for(int
        // x=1-this.BOARD_SIZE;x<this.BOARD_SIZE;x++){upDiagTemp.append(np.diag(flippedCoords,x));this.upDiagCoords=np.array(upDiagTemp,dtype=object);
    }

    // Tries to find a move within a given time.
    // Iterates depth of search so if it runs out of time, it will default to the
    // last found best move
    // If the program throws any error it defaults to a valid value
    public Move move(Integer[][] board) {
        try {
            this.startTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            Move prevBestMove = new Move(1, 1, 0);
            // this.counter = 0
            Integer[] myStartScore = this.countLines(board, this.ID);
            Integer[] theirStartScore = this.countLines(board, this.ID * -1);
            Move bestMove = prevBestMove;
            for (int depth = 1; depth < this.MAX_DEPTH; depth++) {
                bestMove = this.findMyMove(board, 0, depth, -10000, 10000, myStartScore, theirStartScore,
                        new ArrayList<Move>());
                if (bestMove == null || bestMove.score == 0) {
                    bestMove = prevBestMove;
                    break;
                } else {
                    // print("Likelihood of winning: ", bestScore, "\tBest Move", bestMove,
                    // "\tDepth", depth) // Shows the progression of the algorithm
                    prevBestMove = bestMove;
                }
            }
            // print(bestMove)
            // print("Counter = ", this.counter)
            return bestMove;
        } catch (Exception e) {
            // print("Error occurred, default value returned\nError -", e)// Logs occurance
            // of an error
            for (int x = 0; x < this.BOARD_SIZE; x++) {
                for (int y = 0; y < this.BOARD_SIZE; y++) {
                    if (board[y][x] == 0) {
                        return new Move(x, y);
                    }
                }
            }

        }
    }

    // Finds the next move for the current player. This will call findTheirMove,
    // which will again call findMyMove
    // until maxDepth is reached
    // It searches depth first, and uses alpha beta pruning to cut down on the
    // number of searched nodes
    public Move findMyMove(Integer[][] board, int depth, int maxDepth, int alpha, int beta,
            Integer[] prevThisPlayerScore, Integer[] prevOtherPlayerScore, ArrayList<Move> priorityMoves) {
        if (LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) > (this.startTime + this.SEARCH_TIME)) {
            return new Move();
        }
        if (depth < maxDepth) {
            // print("Alpha: ", alpha, "\tBeta: ", beta)
            ArrayList<Move> validMoves = this.findMoves(board, priorityMoves);
            Move bestMove = new Move();
            for (Move move : validMoves) {
                Pair<Integer[], ArrayList<Move>> thisPlayerChange = this.countChangeAdd(this.ID, board,
                        prevThisPlayerScore, move);
                Pair<Integer[], ArrayList<Move>> otherPlayerChange = this.countChangeMinus(this.ID * -1, board,
                        prevOtherPlayerScore, move);
                Integer[] thisPlayerScore = thisPlayerChange.a;
                Integer[] otherPlayerScore = otherPlayerChange.a;
                ArrayList<Move> newPriorityMoves = thisPlayerChange.b;
                newPriorityMoves.addAll(otherPlayerChange.b);
                Integer[][] simulatedBoard = this.simulateMove(board, move.x, move.y, this.ID);
                if (thisPlayerScore[this.INDEXES[0]] > 0 || thisPlayerScore[this.INDEXES[1]] > 0
                        || thisPlayerScore[this.INDEXES[2]] > 0) {
                    // Check if the game is won
                    move.score = 1000;
                    return move;
                } else {
                    // If not won, go a layer deeper and calculate their move
                    Move newMove = this.findMyMove(simulatedBoard, depth + 1, maxDepth, alpha, beta,
                            otherPlayerScore, thisPlayerScore, newPriorityMoves);
                    if (newMove.score == 0) {
                        break;
                    } else {
                        // Initialise the best score
                        if (bestMove.score == 0) {
                            bestMove = newMove;
                        }
                        // Update the best score if it's higher than the current best
                        if (newMove.score > bestMove.score) {
                            bestMove = newMove;
                        }
                        if (bestMove.score >= beta) {
                            break;
                        }
                        if (bestMove.score > alpha) {
                            alpha = bestMove.score;
                        }
                        // If time is up, return 0 (causes the move() function to return the best found
                        // previous move)
                        // if (depth==2):
                        // print(bestScore, bestMove)
                        return bestMove;
                    }
                }
            }
        } else {
            // At max depth, calculate the score based on the number of lines of differing
            // lengths it has
            // this.counter+=1
            // int myScore = prevThisPlayerScore;
            // int theirScore = prevOtherPlayerScore;
            // TODO:
            int sum = 0;
            for (int index = 0; index < this.NEXT_PLAYER_WEIGHTS.length; index++) {
                sum += this.NEXT_PLAYER_WEIGHTS[index] * prevOtherPlayerScore[index];
            }
            for (int index = 0; index < this.NEXT_PLAYER_WEIGHTS.length; index++) {
                sum += this.NEXT_PLAYER_WEIGHTS[index] * prevOtherPlayerScore[index];
            }
            return new Move(sum);
        }
    }

    // Almost identical to findMyMove, but separated out to make tracking the ID
    // simpler.
    // In hindsight, separating these two was a mistake, as a number of changes had
    // to be done to both functions
    // public Move findTheirMove(Integer[][] board, int depth, int maxDepth, int
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
    public Integer[][] simulateMove(Integer[][] board, int x, int y, int id) {
        Integer[][] simulatedBoard = board.clone();
        simulatedBoard[y][x] = id;
        return simulatedBoard;
    }

    // Old count lines function, only used for the initial count as it's much slower
    public Integer[] countLines(Integer[][] board, int id) {
        Integer[] horizontalScores = this.countLinesInDirection(board, this.horizontalCoords, id);
        Integer[] verticalScores = this.countLinesInDirection(board, this.verticalCoords, id);
        Integer[] downDiagScores = this.countLinesInDirection(board, this.downDiagCoords, id);
        Integer[] upDiagScores = this.countLinesInDirection(board, this.upDiagCoords, id);

        Integer[] totalScores = new Integer[horizontalScores.length];
        for (int index = 0; index < horizontalScores.length; index++) {
            totalScores[index] = horizontalScores[index] + verticalScores[index] + upDiagScores[index]
                    + downDiagScores[index];
        }
        return totalScores; // Fix
                            // to
                            // have
                            // weights
    }

    // Goes through row by row, and measures the size of every consectutive line and
    // whether it's open ended or not
    public Integer[] countLinesInDirection(Integer[][] board,
            ArrayList<ArrayList<Pair<Integer, Integer>>> orderedItems,
            int id) {
        Integer[] scoresOpen = new Integer[this.X_IN_A_LINE + 1];
        Integer[] scoresSemi = new Integer[this.X_IN_A_LINE + 1];
        Integer[] scoresDead = new Integer[this.X_IN_A_LINE + 1];
        int length = 0;
        boolean openEnded = false;
        int lastNumber = id * -1; // Represents the last number observed, to see if lines are open ended or not.
        // At the start of every line it defaults to the inverse of the id, so lines
        // that start at the edge of the board are not open ended
        for (ArrayList<Pair<Integer, Integer>> line : orderedItems) {
            for (Pair<Integer, Integer> cell : line) {
                int x = cell.a;
                int y = cell.b;
                // print(x,y,length)
                if (board[y][x] == id) {
                    if (length == 0 && lastNumber == 0) {
                        openEnded = true;
                    }
                    length += 1;
                } else if (length > 0) {
                    if (openEnded && board[y][x] == 0) {
                        scoresOpen[length] += 1;
                    }
                }

                else if (openEnded || board[y][x] == 0) {
                    scoresSemi[length] += 1;
                } else {
                    scoresDead[length] += 1;
                }
                length = 0;
                openEnded = false;
            }
            if (length > 0) {
                if (openEnded) {
                    scoresSemi[length] += 1;
                }
            } else {
                scoresDead[length] += 1;
            }
            openEnded = false;
            length = 0;
            lastNumber = id * -1;
        }
        Integer[] result = Stream.of(scoresDead, scoresSemi, scoresOpen).flatMap(Stream::of).toArray(Integer[]::new);
        return result;

    }

    // Counts how the scores change when adding the move to the board.
    // Extends and joins any lines that attach to the move.
    public Pair<Integer[], ArrayList<Move>> countChangeAdd(int id, Integer[][] prevBoard, Integer[] prevScore,
            Move move) {
        this.priorityMoves = new ArrayList<Move>();
        Integer[] score = prevScore.clone();
        Integer y = move.y, x = move.x,
                horLength = 1, verLength = 1, upDiLength = 1, doDiLength = 1, horOpeness = 0, verOpeness = 0,
                upDiOpeness = 0, doDiOpeness = 0, prevIndex;
        // TODO: Does openness pass by value or reference, should pass by refernce
        prevIndex = this.calculateNewLines(prevBoard, id, x, y, score, horLength, horOpeness, 1, 0);
        score[prevIndex] -= 1;
        prevIndex = this.calculateNewLines(prevBoard, id, x, y, score, horLength, horOpeness, -1, 0);
        score[prevIndex] -= 1;
        prevIndex = this.calculateNewLines(prevBoard, id, x, y, score, verLength, verOpeness, 0, 1);
        score[prevIndex] -= 1;
        prevIndex = this.calculateNewLines(prevBoard, id, x, y, score, verLength, verOpeness, 0, -1);
        score[prevIndex] -= 1;
        prevIndex = this.calculateNewLines(prevBoard, id, x, y, score, doDiLength, doDiOpeness, 1, -1);
        score[prevIndex] -= 1;
        prevIndex = this.calculateNewLines(prevBoard, id, x, y, score, doDiLength, doDiOpeness, -1, 1);
        score[prevIndex] -= 1;
        prevIndex = this.calculateNewLines(prevBoard, id, x, y, score, upDiLength, upDiOpeness, 1, 1);
        score[prevIndex] -= 1;
        prevIndex = this.calculateNewLines(prevBoard, id, x, y, score, upDiLength, upDiOpeness, -1, -1);
        score[prevIndex] -= 1;
        horLength = Math.min(horLength, 5);// Stops a 6 long line from breaking the system
        verLength = Math.min(verLength, 5);// This can occur when 2 adding to 5 or more are joined together. E.g 2 3's
        doDiLength = Math.min(doDiLength, 5);
        upDiLength = Math.min(upDiLength, 5);
        score[(horOpeness * (this.X_IN_A_LINE + 1)) + horLength] += 1;
        score[(verOpeness * (this.X_IN_A_LINE + 1)) + verLength] += 1;
        score[(doDiOpeness * (this.X_IN_A_LINE + 1)) + doDiLength] += 1;
        score[(upDiOpeness * (this.X_IN_A_LINE + 1)) + upDiLength] += 1;
        return new Pair<Integer[], ArrayList<Move>>(score, this.priorityMoves);
    }

    // For each direction, searches how long the new line is, subtracts one score
    // from length-1
    // And adds 1 score to the length
    public Integer calculateNewLines(Integer[][] board, int id, int x, int y, Integer[] prevScore, Integer length,
            Integer openess, int xChange, int yChange) {
        for (int i = 1; i < this.X_IN_A_LINE + 1; i++) {// Check positive horizontal
            int newX = x + (xChange * i);
            int newY = y + (yChange * i);
            if (newX < this.BOARD_SIZE && newY < this.BOARD_SIZE && newX >= 0 && newY >= 0) {
                int value = board[newY][newX];
                if (value == 0) {
                    // If it is now semi open. The previous line was open
                    // this.priorityMoves.append(tuple(newY, newX))
                    this.priorityMoves.add(new Move(newY, newX));
                    length = length + (i - 1);
                    openess = openess + 1;
                    return ((i - 1) + ((this.X_IN_A_LINE + 1) * 2));
                } else if (value != id) {
                    // If it is now closed, the previous line was semi open
                    length = length + (i - 1);
                    return ((i - 1) + (this.X_IN_A_LINE + 1));
                }
            } else {
                length = length + (i - 1);
                return ((i - 1) + (this.X_IN_A_LINE + 1));
            }
        }
    }

    // Finds lines that are no longer open ended and adjusts the score accordingly
    public Pair<Integer[], Integer[]> countChangeMinus(int id, Integer[][] prevBoard, Integer[] prevScore, Move move) {
        this.priorityMoves = new ArrayList<Move>();
        // TODO: Reset priority moves twice?
        Integer[] score = prevScore.clone();
        int y = move.y;
        int x = move.x;
        for (Integer[] xyChange : new Integer[][] { { -1, -1 }, { -1, 0 }, { -1, 1 }, { 0, -1 }, { 1, 1 }, { 1, -1 },
                { 1, 0 }, { 1, 1 } }) {
            // print(this.calculateOldLines(prevBoard, id, x, y, score, xChange, yChange))
            Pair<Integer, Integer> indexes = this.calculateOldLines(prevBoard, id, x, y, score, xyChange[0],
                    xyChange[0]);
            // print(prevIndex, newIndex, "returned")
            score[indexes.a] -= 1;
            score[indexes.b] += 1;
        }
        return new Pair<Integer[], Integer[]>(score, this.priorityMoves);
    }

    // Finds the old index of the lines and returns a new index for them as well
    public Pair<Integer, Integer> calculateOldLines (Integer[] board, int id, int x, int y, Integer[] prevScore, int xChange, int yChange) {
        // print("Changes", xChange, yChange)
        for (int i = 1; i < this.X_IN_A_LINE+1; i++){// Check positive horizontal
            newX = x + (xChange * i);
            newY = y + (yChange * i);
            // print(newY, newX)
            if (newX < this.BOARD_SIZE && newY < this.BOARD_SIZE && newX >= 0 && newY >= 0){
                value = board[newY][newX];
                if (value == 0){
                    // If it is now semi open. The previous line was open
                    this.priorityMoves.append((newY, newX));
                    //  print(this.priorityMoves)
                    return (i-1)+((this.X_IN_A_LINE+1)*2), (i-1)+((this.X_IN_A_LINE+1)*1);
                }
                else if (value != id){
                    // If it is now closed, the previous line was semi open
                    return ((i-1)+(this.X_IN_A_LINE+1)), (i-1);
                }
                //  else:
                //      print(value, i)
                //      print(board)
                //      print(newY, newX)
            }
            else {
                //  print(((i-1)+(this.X_IN_A_LINE+1)), (i-1))
                return ((i-1)+(this.X_IN_A_LINE+1)), (i-1);
            }
        }
    }

    // Finds moves that are valid to explore, only finds moves plus minus one of the
    // total area currently played in
    // Starts with any lines that were made longer or shorter by the last move.
    // (From priorityMoves)
    public ArrayList<Move> findMoves (Integer[][] board, ArrayList<Move> priorityMoves){
        minX = this.BOARD_SIZE;
        minY = this.BOARD_SIZE;
        maxX = 0;
        maxY = 0;
        ArrayList<Move> validMoves = new ArrayList<>();
        for (Move move : priorityMoves) {
            validMoves.add(move);
        }
        for (int y = 0; y < this.BOARD_SIZE; y++){
            for (int x = 0; x < this.BOARD_SIZE; x++){
                if (board[y][x] != 0){
                    if (x < minX){
                        minX = x;
                    }
                    if (x >= maxX)
                        {maxX = x + 1;}
                    if (y < minY)
                        {minY = y;}
                    if (y >= maxY)
                        {maxY = y + 1;}
                }
            }
        }
        minX = max(minX-this.SEARCH_RADIUS, 0);
        minY = max(minY-this.SEARCH_RADIUS, 0);
        maxX = min(maxX+this.SEARCH_RADIUS, this.BOARD_SIZE);
        maxY = min(maxY+this.SEARCH_RADIUS, this.BOARD_SIZE);
        for (int y = minY; y < maxY; y++){
            for (int x = minX; x < maxX; x++){
                if (board[y][x] == 0){//  and (y,x) not in validMoves):
                    validMoves.append((y,x));
                }
            }
        }
        if (validMoves == []){
            if (maxX > minX){
                return [];
            }
            else{
                return [(1,1)];// Set default starting move
            }
        }
        return validMoves;
    }
}