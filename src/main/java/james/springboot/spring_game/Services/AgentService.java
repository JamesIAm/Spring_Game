package james.springboot.spring_game.Services;

import java.util.ArrayList;

public class AgentService {
    private final Integer ID = 1;
    private final Integer BOARD_SIZE = 10;
    private final Integer X_IN_A_LINE = 5;
    private final Integer MAX_DEPTH = 10; // Maximum depth the program will search to. Must be greater than 1
    private final Integer[] NEXT_PLAYER_WEIGHTS ={0,0,0,0,0,1000,0,0,2,10,500,1000,0,0,10,100,500,1000};// Indicates the weights that scores are multiplied by.
    // The scores represent how many lines of a certain length and certain openess (whether the two ends are blocked) exist in the board
    private final Integer[] CURRENT_PLAYER_WEIGHTS ={0,0,0,0,0,1000,0,0,2,10,50,1000,0,0,10,50,200,1000};
    private final Integer SEARCH_RADIUS = 1; //  How far around the current min and max co-ords the system looks for new moves. At 1, a tile on 3,3 will result in a search of 2,2 to 4,4
    private final Integer[] INDEXES = {(X_IN_A_LINE+1)-1, ((X_IN_A_LINE+1)*2)-1, ((X_IN_A_LINE+1)*3)-1}; //  These indicate, of all the scores, 
    // which ones indicate a win situation. 5 in a row (enclosed on both sides), 5 in a row (enclosed on one side), and 5 in a row (not enclosed)
    private final Double SEARCH_TIME = 5D; // How many seconds the system searches before returning it's best solution
    //  private final Integer counter = 0
    private final ArrayList<Integer> priorityMoves = new ArrayList<Integer>();// Contains a list of moves that should be searched first (continuations of the line of the last move played)
    public AgentService (){
        defineOrders();
    }

    //  Creates the order that the board should be searched for existing lines. This was from a previous iteration of the line
    //  search that searched the entire board every time
    //  It defines an order to search over the data so that each row goes vertically, horizonatally or diagonally
    public void defineOrders (): 
        self.horizontalCoords = np.zeros([self.BOARD_SIZE, self.BOARD_SIZE], dtype=object)
        for y in range(self.BOARD_SIZE):
            for x in range(self.BOARD_SIZE):
                self.horizontalCoords[y][x] = ((x,y))

        self.verticalCoords=np.swapaxes(self.horizontalCoords, 0, 1)

        downDiagTemp = []
        for x in range(1-self.BOARD_SIZE, self.BOARD_SIZE):
            downDiagTemp.append(np.diag(self.horizontalCoords, x))
        self.downDiagCoords = np.array(downDiagTemp, dtype=object)

        upDiagTemp = []
        flippedCoords = np.fliplr(self.horizontalCoords)
        for x in range(1-self.BOARD_SIZE, self.BOARD_SIZE):
            upDiagTemp.append(np.diag(flippedCoords, x))
        self.upDiagCoords = np.array(upDiagTemp, dtype=object)
    //  Tries to find a move within a given time.
    //  Iterates depth of search so if it runs out of time, it will default to the last found best move
    //  If the program throws any error it defaults to a valid value
    public void move (board):
        try:
            self.startTime = time()
            prevBestMove = (1,1)
            //  self.counter = 0
            myStartScore = self.countLines(board, self.ID)
            theirStartScore = self.countLines(board, self.ID*-1)
            for depth in range (1, self.MAX_DEPTH):
                bestScore, bestMove = self.findMyMove(board, 0, depth, -10000, 10000, myStartScore, theirStartScore, [])
                if (bestMove == None or bestScore == None):
                    bestMove = prevBestMove
                    break
                else: 
                    //  print("Likelihood of winning: ", bestScore, "\tBest Move", bestMove, "\tDepth", depth) //  Shows the progression of the algorithm
                    prevBestMove = bestMove
            //  print(bestMove)
            //  print("Counter = ", self.counter)
            return bestMove
        except Exception as e:
            //  print("Error occurred, default value returned\nError -", e)// Logs occurance of an error
            for x in range (self.BOARD_SIZE):
                for y in range (self.BOARD_SIZE):
                    moveLoc = (y,x)
                    if legalMove(board, moveLoc):
                        return moveLoc
    
    //  Finds the next move for the current player. This will call findTheirMove, which will again call findMyMove 
    //  until maxDepth is reached
    //  It searches depth first, and uses alpha beta pruning to cut down on the number of searched nodes
    public void findMyMove (board, depth, maxDepth, alpha, beta, prevThisPlayerScore, prevOtherPlayerScore, priorityMoves):
        if (time() > (self.startTime + self.SEARCH_TIME)):
                return 0, None
        if (depth < maxDepth):
            // print("Alpha: ", alpha, "\tBeta: ", beta)
            validMoves = self.findMoves(board, priorityMoves.copy())
            bestScore = None
            bestMove = ()
            for move in validMoves:
                thisPlayerScore, priorityMoves1 = self.countChangeAdd(self.ID, board, prevThisPlayerScore.copy(), move)
                otherPlayerScore, priorityMoves2 = self.countChangeMinus(self.ID * -1, board, prevOtherPlayerScore.copy(), move)
                newPriorityMoves = priorityMoves1 + priorityMoves2
                simulatedBoard = self.simulateMove(board, move[1], move[0], self.ID)
                if (thisPlayerScore[self.INDEXES[0]] > 0 or thisPlayerScore[self.INDEXES[1]] > 0 or thisPlayerScore[self.INDEXES[2]] > 0):
                    // Check if the game is won
                    return 1000, move
                else:
                    // If not won, go a layer deeper and calculate their move
                    score, newMove = self.findTheirMove(simulatedBoard, depth + 1, maxDepth, alpha, beta, otherPlayerScore, thisPlayerScore, newPriorityMoves)
                    if (score == None):
                        break
                    else:
                        // Initialise the best score
                        if (bestScore == None):
                            bestScore = score
                            bestMove = move
                        // Update the best score if it's higher than the current best
                        if (score > bestScore):
                            bestScore = score
                            bestMove = move
                        if (bestScore >= beta):
                            break
                        if (bestScore > alpha):
                            alpha = bestScore
            // If time is up, return 0 (causes the move() function to return the best found previous move)
            if (time() > (self.startTime + self.SEARCH_TIME)):
                return 0, None
            else:
                //  if (depth==2):
                //      print(bestScore, bestMove)
                return bestScore, bestMove
            
        else:
            // At max depth, calculate the score based on the number of lines of differing lengths it has
            //  self.counter+=1
            myScore = prevThisPlayerScore
            theirScore = prevOtherPlayerScore
            return (sum(self.NEXT_PLAYER_WEIGHTS * myScore) - sum(self.CURRENT_PLAYER_WEIGHTS*theirScore)), []


    //  Almost identical to findMyMove, but separated out to make tracking the ID simpler.
    //  In hindsight, separating these two was a mistake, as a number of changes had to be done to both functions
    public void findTheirMove (board, depth, maxDepth, alpha, beta, prevThisPlayerScore, prevOtherPlayerScore, priorityMoves):
        if (time() > (self.startTime + self.SEARCH_TIME)):
                return 0, None
        if (depth < maxDepth):
            validMoves = self.findMoves(board, priorityMoves.copy())
            bestScore = None
            bestMove = ()
            for move in validMoves:
                thisPlayerScore, priorityMoves1 = self.countChangeAdd(self.ID * -1, board, prevThisPlayerScore.copy(), move)
                otherPlayerScore, priorityMoves2 = self.countChangeMinus(self.ID, board, prevOtherPlayerScore.copy(), move)
                newPriorityMoves = priorityMoves1 + priorityMoves2
                simulatedBoard = self.simulateMove(board, move[1], move[0], self.ID*-1)
                if (thisPlayerScore[self.INDEXES[0]] > 0 or thisPlayerScore[self.INDEXES[1]] > 0 or thisPlayerScore[self.INDEXES[2]] > 0):
                    return -1000, move
                else:
                    score, newMove = self.findMyMove(simulatedBoard, depth + 1, maxDepth, alpha, beta, otherPlayerScore, thisPlayerScore, newPriorityMoves)
                    if (score == None):
                        break
                    else:
                        if (bestScore == None):
                            bestScore = score
                            bestMove = move
                        // pick the minimum score
                        if (score < bestScore):
                            bestScore = score
                            bestMove = move
                        if (bestScore <= alpha): // If the score is less than the current max path, return the score and trim the branch
                            break
                        if (bestScore < beta):
                            beta = bestScore
            if (time() > (self.startTime + self.SEARCH_TIME)):
                return 0, None
            else:
                return bestScore, bestMove
        else:
            //  self.counter+=1
            myScore = prevOtherPlayerScore
            theirScore = prevThisPlayerScore
            return (sum(self.CURRENT_PLAYER_WEIGHTS * myScore) - sum(self.NEXT_PLAYER_WEIGHTS*theirScore)), []

    //  Returns a board with an additional square filled in at the x and y coords
    public void simulateMove (board, x, y, id):
        simulatedBoard = np.copy(board)
        simulatedBoard[y][x] = id
        return simulatedBoard
    
    //  Old count lines function, only used for the initial count as it's much slower
    public void countLines (board, id):
        horizontalScores = self.countLinesInDirection(board, self.horizontalCoords, id)
        verticalScores = self.countLinesInDirection(board, self.verticalCoords, id)
        downDiagScores = self.countLinesInDirection(board, self.downDiagCoords, id)
        upDiagScores = self.countLinesInDirection(board, self.upDiagCoords, id)
        totalScores = np.concatenate([(horizontalScores[0] + verticalScores[0] + downDiagScores[0] + upDiagScores[0]), (horizontalScores[1] + verticalScores[1] + downDiagScores[1] + upDiagScores[1]), (horizontalScores[2] + verticalScores[2] + downDiagScores[2] + upDiagScores[2])])
        return totalScores //  Fix to have weights

    //  Goes through row by row, and measures the size of every consectutive line and whether it's open ended or not
    public void countLinesInDirection (board, orderedItems, id):
        scoresOpen = np.zeros(self.X_IN_A_LINE+1)
        scoresSemi = np.zeros(self.X_IN_A_LINE+1)
        scoresDead = np.zeros(self.X_IN_A_LINE+1)
        length = 0
        openEnded = False
        lastNumber = id * - 1 // Represents the last number observed, to see if lines are open ended or not. 
        // At the start of every line it defaults to the inverse of the id, so lines that start at the edge of the board are not open ended 
        for row in range(len(orderedItems)):
            for column in range(len(orderedItems[row])):
                x,y = orderedItems[row][column]
                // print(x,y,length)
                if (board[y][x] == id):
                    if (length == 0 and lastNumber == 0):
                        openEnded = True
                    length += 1
                elif (length > 0):
                    if (openEnded and board[y][x] == 0):
                        scoresOpen[length] += 1
                    elif (openEnded or board[y][x] == 0):
                        scoresSemi[length] += 1
                    else:
                        scoresDead[length] += 1
                    length = 0
                    openEnded = False
                lastNumber = board[y][x]
            if (length > 0):
                if (openEnded):
                    scoresSemi[length] += 1
                else:
                    scoresDead[length] += 1
                openEnded = False
            length = 0
            lastNumber = id * - 1
        return [scoresDead, scoresSemi, scoresOpen]
    
    //  Counts how the scores change when adding the move to the board. 
    //  Extends and joins any lines that attach to the move.
    public void countChangeAdd (id, prevBoard, prevScore, move):
        self.priorityMoves = []
        score = prevScore
        y = move[0]
        x = move[1]
        horLength = 1
        verLength = 1
        upDiLength = 1
        doDiLength = 1
        horOpeness = 0
        verOpeness = 0
        upDiOpeness = 0
        doDiOpeness = 0
        prevIndex, horLength, horOpeness = self.calculateNewLines(prevBoard, id, x, y, score, horLength, horOpeness, 1, 0)
        score[prevIndex] -= 1
        prevIndex, horLength, horOpeness = self.calculateNewLines(prevBoard, id, x, y, score, horLength, horOpeness, -1, 0)
        score[prevIndex] -= 1
        prevIndex, verLength, verOpeness = self.calculateNewLines(prevBoard, id, x, y, score, verLength, verOpeness, 0, 1)
        score[prevIndex] -= 1
        prevIndex, verLength, verOpeness = self.calculateNewLines(prevBoard, id, x, y, score, verLength, verOpeness, 0, -1)
        score[prevIndex] -= 1
        prevIndex, doDiLength, doDiOpeness = self.calculateNewLines(prevBoard, id, x, y, score, doDiLength, doDiOpeness, 1, -1)
        score[prevIndex] -= 1
        prevIndex, doDiLength, doDiOpeness = self.calculateNewLines(prevBoard, id, x, y, score, doDiLength, doDiOpeness, -1, 1)
        score[prevIndex] -= 1
        prevIndex, upDiLength, upDiOpeness = self.calculateNewLines(prevBoard, id, x, y, score, upDiLength, upDiOpeness, 1, 1)
        score[prevIndex] -= 1
        prevIndex, upDiLength, upDiOpeness = self.calculateNewLines(prevBoard, id, x, y, score, upDiLength, upDiOpeness, -1, -1)
        score[prevIndex] -= 1
        horLength = min(horLength, 5)// Stops a 6 long line from breaking the system
        verLength = min(verLength, 5)// This can occur when 2 adding to 5 or more are joined together. E.g 2 3's
        doDiLength = min(doDiLength, 5)
        upDiLength = min(upDiLength, 5)
        score[(horOpeness * (self.X_IN_A_LINE+1)) + horLength] += 1
        score[(verOpeness * (self.X_IN_A_LINE+1)) + verLength] += 1
        score[(doDiOpeness * (self.X_IN_A_LINE+1)) + doDiLength] += 1
        score[(upDiOpeness * (self.X_IN_A_LINE+1)) + upDiLength] += 1
        return score, self.priorityMoves
        
    //  For each direction, searches how long the new line is, subtracts one score from length-1
    //  And adds 1 score to the length
    public void calculateNewLines (board, id, x, y, prevScore, length, openess, xChange, yChange): 
        for i in range(1, self.X_IN_A_LINE+1):// Check positive horizontal
            newX = x + (xChange * i)
            newY = y + (yChange * i)
            if (newX < self.BOARD_SIZE and newY < self.BOARD_SIZE and newX >= 0 and newY >= 0):
                value = board[newY][newX]
                if (value == 0):
                    // If it is now semi open. The previous line was open
                    //  self.priorityMoves.append(tuple(newY, newX))
                    self.priorityMoves.append((newY, newX))
                    return ((i-1)+((self.X_IN_A_LINE+1)*2)), length+(i-1), openess+1
                elif (value != id):
                    // If it is now closed, the previous line was semi open
                    return ((i-1)+(self.X_IN_A_LINE+1)), length+(i-1), openess
            else:
                return ((i-1)+(self.X_IN_A_LINE+1)), length+(i-1), openess
            
    //  Finds lines that are no longer open ended and adjusts the score accordingly
    public void countChangeMinus (id, prevBoard, prevScore, move):
        self.priorityMoves = []
        score = prevScore
        y = move[0]
        x = move[1]
        for (xChange, yChange) in [(-1, -1), (-1, 0), (-1, 1), (0, -1), (1, 1), (1, -1), (1, 0), (1, 1)]:
        //  for xChange in range (-1, 2):
        //      for yChange in range (-1, 2):
                //  print(self.calculateOldLines(prevBoard, id, x, y, score, xChange, yChange))
            prevIndex, newIndex = self.calculateOldLines(prevBoard, id, x, y, score, xChange, yChange)
            //  print(prevIndex, newIndex, "returned")
            score[prevIndex] -= 1
            score[newIndex] += 1
        return score, self.priorityMoves
    // Finds the old index of the lines and returns a new index for them as well
    public void calculateOldLines (board, id, x, y, prevScore, xChange, yChange): 
        // print("Changes", xChange, yChange)
        for i in range(1, self.X_IN_A_LINE+1):// Check positive horizontal
            newX = x + (xChange * i)
            newY = y + (yChange * i)
            // print(newY, newX)
            if (newX < self.BOARD_SIZE and newY < self.BOARD_SIZE and newX >= 0 and newY >= 0):
                value = board[newY][newX]
                if (value == 0):
                    // If it is now semi open. The previous line was open
                    self.priorityMoves.append((newY, newX))
                    //  print(self.priorityMoves)
                    return (i-1)+((self.X_IN_A_LINE+1)*2), (i-1)+((self.X_IN_A_LINE+1)*1)
                elif (value != id):
                    // If it is now closed, the previous line was semi open
                    return ((i-1)+(self.X_IN_A_LINE+1)), (i-1)
                //  else:
                //      print(value, i)
                //      print(board)
                //      print(newY, newX)
            else:
                //  print(((i-1)+(self.X_IN_A_LINE+1)), (i-1))
                return ((i-1)+(self.X_IN_A_LINE+1)), (i-1)
        
        
    //  Finds moves that are valid to explore, only finds moves plus minus one of the total area currently played in
    //  Starts with any lines that were made longer or shorter by the last move. (From priorityMoves)
    public void findMoves (board, priorityMoves):
        minX = self.BOARD_SIZE
        minY = self.BOARD_SIZE
        maxX = 0
        maxY = 0
        validMoves = priorityMoves
        for y in range(self.BOARD_SIZE):
            for x in range(self.BOARD_SIZE):
                if (board[y][x] != 0):
                    
                    if (x < minX):
                        minX = x
                    if (x >= maxX):
                        maxX = x + 1
                    if (y < minY):
                        minY = y
                    if (y >= maxY):
                        maxY = y + 1
        minX = max(minX-self.SEARCH_RADIUS, 0)
        minY = max(minY-self.SEARCH_RADIUS, 0)
        maxX = min(maxX+self.SEARCH_RADIUS, self.BOARD_SIZE)
        maxY = min(maxY+self.SEARCH_RADIUS, self.BOARD_SIZE)
        for y in range(minY, maxY):
            for x in range(minX, maxX):
                if (board[y][x] == 0)://  and (y,x) not in validMoves):
                    validMoves.append((y,x))
        if (validMoves == []):
            if (maxX > minX):
                return []
            else:
                return [(1,1)]// Set default starting move
        return validMoves

}
