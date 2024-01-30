import java.util.*;

public class GameLogic implements PlayableLogic {

    private ConcretePiece[][] gameBoard; // Main GameBoard, which holds the pieces in their position
    private Stack<ConcretePiece[][]> gameBoardHistory; // Holds snapshots of the GameBoard after any movement
    private ConcretePlayer secondPlayer = new ConcretePlayer(false); // Attacker player
    private ConcretePlayer firstPlayer = new ConcretePlayer(true); // Defender player
    private final Position[] corners = {new Position(0,0), new Position(10,0), new Position(0,10), new Position(10,10)};

    private int currentWinner; // variable that helps to know which player has won recently
    private boolean isSecondPlayerTurn = true; // first turn is for the attackers, therefore, it's initialized to be true


    private ArrayList<ArrayList<Position>> firstPlayerPositions; // Holds each defender pieces positions' history (index = id)
    private ArrayList<ArrayList<Position>> secondPlayerPositions; // Holds each attacker pieces positions' history (index = id)

    private ArrayList[][] squareHistory; // Each index will hold the different pieces that stepped on it


    /**
     * Constructor: Upon creating a new gameLogic, the game starts according
     * to the gameLogic methods.
     */
    public GameLogic() {
        this.start();
    }

    /**
     * move: using the two positions, the function checks if the move is legit using different
     * other functions.
     * @param a The starting position of the piece.
     * @param b The destination position for the piece.
     * @return true if the step is legit according to the game's logic
     */

    @Override
    public boolean move(Position a, Position b) {

        if (getPieceAtPosition(a) != null && insideBoard(b)) {
            Player currentPlayer = isSecondPlayerTurn() ? secondPlayer : firstPlayer;
//            System.out.println(getPieceAtPosition(a).getOwner() == currentPlayer);
//            System.out.println(a.notDiagonal(b));
//            System.out.println(getPieceAtPosition(b) == null);
//            System.out.println("isBlocked? "+isBlocked(a, b));
            if (getPieceAtPosition(a).getOwner() == currentPlayer && a.notDiagonal(b) && getPieceAtPosition(b) == null && !isBlocked(a, b)) {
                if(isInCorner(b) && getPieceAtPosition(a) instanceof Pawn){
                    return false;
                }
                // if all the conditions for a legit move are fulfilled,
                // then the actual moveIt will do the move
                moveIt(a, b);
                // after a move, the player turn is changed via:
                isSecondPlayerTurn = !isSecondPlayerTurn();
                return true;
            }
        }

        return false;
    }

    /**
     * Eat: After each move on the board, this function
     * checks the possibility of capturing the opponent piece, if possible.
     * Capturing is only allowed to pieces who are Pawn and not King.
     * The check is performed using four functions that check each direction.
     * @param b destination
     * @param currentPlayer who's made the move
     */
    private void eat(Position b, Player currentPlayer) {
        // TODO: [v] eating against the borders
        //       [v] sandwiching
        if(getPieceAtPosition(b) instanceof Pawn) {
            eatUp(b, currentPlayer);
            eatDown(b, currentPlayer);
            eatRight(b, currentPlayer);
            eatLeft(b, currentPlayer);
        }

    }

    private void eatLeft(Position b, Player currentPlayer) {
        Position LEFT = new Position(b.getCol()-1, b.getRow());
        Position LEFT2 = new Position(b.getCol()-2, b.getRow());
        if(!isOutside(LEFT)) {
            if (getPieceAtPosition(LEFT) != null) {
                if (getPieceAtPosition(LEFT).getOwner() != currentPlayer) {
                    if (isOutside(LEFT2) && getPieceAtPosition(LEFT) instanceof Pawn) {
                        getPawnAtPosition(b).addKill();
                        clear(LEFT);
                    } else {
                        if (getPieceAtPosition(LEFT2) != null) {
                            if (getPieceAtPosition(LEFT2).getOwner() == currentPlayer
                                    && getPieceAtPosition(LEFT) instanceof Pawn) {
                                getPawnAtPosition(b).addKill();
                                clear(LEFT);
                            }
                        }
                    }
                }
            }
        }
    }

    private void eatRight(Position b, Player currentPlayer) {
        Position RIGHT = new Position(b.getCol()+1, b.getRow());
        Position RIGHT2 = new Position(b.getCol()+2, b.getRow());
        if(!isOutside(RIGHT)) {
            if (getPieceAtPosition(RIGHT) != null) {
                if (getPieceAtPosition(RIGHT).getOwner() != currentPlayer) {
                    if (isOutside(RIGHT2) && getPieceAtPosition(RIGHT) instanceof Pawn) {
                        getPawnAtPosition(b).addKill();
                        clear(RIGHT);
                    } else {
                        if (getPieceAtPosition(RIGHT2) != null) {
                            if (getPieceAtPosition(RIGHT2).getOwner() == currentPlayer
                                    && getPieceAtPosition(RIGHT) instanceof Pawn) {
                                getPawnAtPosition(b).addKill();
                                clear(RIGHT);
                            }
                        }
                    }
                }
            }
        }
    }

    private void eatUp(Position b, Player currentPlayer){
        Position UP = new Position(b.getCol(), b.getRow()-1);
        Position UP2 = new Position(b.getCol(), b.getRow()-2);
        if(!isOutside(UP)) {
            if (getPieceAtPosition(UP) != null) {
                if (getPieceAtPosition(UP).getOwner() != currentPlayer) {
                    if (isOutside(UP2) && getPieceAtPosition(UP) instanceof Pawn) {
                        getPawnAtPosition(b).addKill();
                        clear(UP);
                    } else {
                        if (getPieceAtPosition(UP2) != null) {
                            if (getPieceAtPosition(UP2).getOwner() == currentPlayer
                                    && getPieceAtPosition(UP) instanceof Pawn) {
                                getPawnAtPosition(b).addKill();
                                clear(UP);
                            }
                        }
                    }
                }
            }
        }
    }

    private void eatDown(Position b, Player currentPlayer){
        Position DOWN = new Position(b.getCol(), b.getRow()+1);
        Position DOWN2 = new Position(b.getCol(), b.getRow()+2);
        if(!isOutside(DOWN)){
        if(getPieceAtPosition(DOWN) != null) {
            if (getPieceAtPosition(DOWN).getOwner() != currentPlayer) {
                if (isOutside(DOWN2) && getPieceAtPosition(DOWN) instanceof Pawn) {
                    getPawnAtPosition(b).addKill();
                    clear(DOWN);
                } else {
                    if (getPieceAtPosition(DOWN2) != null) {
                        if (getPieceAtPosition(DOWN2).getOwner() == currentPlayer
                                && getPieceAtPosition(DOWN) instanceof Pawn) {
                            getPawnAtPosition(b).addKill();
                            clear(DOWN);
                        }
                    }
                }
            }
        }
        }
    }

    /**
     * isBlocked: Upon an attempted move, this function
     * returns true if the piece is blocked on the board
     * which will result in not moving.
     * The calculation is performed by checking the desired direction
     * of movement (using differentials) and checks if on the path there are
     * any blocking pieces.
     * @param a beginning position
     * @param b desired destination
     * @return true if it's blocked by other pieces on the path
     */
    public boolean isBlocked(Position a, Position b) {
        int dCol = 0;
        int dRow = 0;
        if (b.getCol() > a.getCol()) {
            dCol = 1;
        } else if (b.getCol() < a.getCol()) {
            dCol = -1;
        }
        if (b.getRow() > a.getRow()) {
            dRow = 1;
        } else if (b.getRow() < a.getRow()) {
            dRow = -1;
        }
        Position t = new Position(a.getCol() + dCol, a.getRow() + dRow);

        while (t.getRow() != b.getRow() || t.getCol() != b.getCol()) {
            if (getPieceAtPosition(t) != null) {
                return true;
            }
            t.setRow(t.getRow() + dRow);
            t.setCol(t.getCol() + dCol);
        }

        return false;
    }

    /**
     * isInCorner:
     * @param p checked position
     * @return true if the position is in the game board's corners
     */
    public boolean isInCorner(Position p){
        for (Position corner : corners) {
            if (p.getRow() == corner.getRow() && p.getCol() == corner.getCol()) {
                return true;
            }
        }
        return false;
    }


    /**
     * MoveIt: performs the actual movement and changes the game board.
     * @param a source position
     * @param b destination position
     */
    public void moveIt(Position a, Position b){
            if(getPieceAtPosition(a).getOwner() == secondPlayer){
                this.secondPlayerPositions.get(getPieceAtPosition(a).getID()).add(b);
            } else {
                this.firstPlayerPositions.get(getPieceAtPosition(a).getID()).add(b);
            }
            //System.out.println(getPieceAtPosition(a).getID()+"moved from"+a.toString()+" to "+b.toString());
            if(!squareHistory[b.getCol()][b.getRow()].contains(getPieceAtPosition(new Position(a.getCol(), a.getRow())))){
                squareHistory[b.getCol()][b.getRow()].add(getPieceAtPosition(new Position(a.getCol(), a.getRow())));
            }

            this.gameBoard[b.getCol()][b.getRow()] = (ConcretePiece) getPieceAtPosition(a);
            this.gameBoard[a.getCol()][a.getRow()] = null;
            eat(b, getPieceAtPosition(b).getOwner());
            ConcretePiece[][] gameSnapshot = copyGameBoard();
            gameBoardHistory.push(gameSnapshot);
            isGameFinished();
            //printAroundKing();
    }

    /**
     * copyGameBoard: creates a deepCopy of the gameBoard
     * @return deepCopy of the gameBoard
     */
    private ConcretePiece[][] copyGameBoard() {
        ConcretePiece[][] deepCopy = new ConcretePiece[getBoardSize()][getBoardSize()];
        for (int i = 0; i < getBoardSize(); i++) {
            for (int j = 0; j < getBoardSize(); j++) {
                deepCopy[i][j] = gameBoard[i][j];
            }
        }

        return deepCopy;
    }


    private void printAroundKing() {
        Position kingPos = firstPlayerPositions.get(7).get(firstPlayerPositions.get(7).size() - 1);
        Position[] aroundKing = {new Position(kingPos.getCol(), kingPos.getRow() - 1),
                new Position(kingPos.getCol(), kingPos.getRow() + 1),
                new Position(kingPos.getCol() - 1, kingPos.getRow()),
                new Position(kingPos.getCol() + 1, kingPos.getRow())};
        for(Position p : aroundKing){
            System.out.println(getPieceAtPosition(p));
        }
    }

    /**
     * getPieceAtPosition: returns the piece which is in the current position
     * @param position The position for which to retrieve the piece.
     * @return ConcretePiece
     */
    @Override
    public ConcretePiece getPieceAtPosition(Position position) {
        if (!isOutside(position)) {
            if(gameBoard[position.getCol()][position.getRow()] instanceof Pawn){
                getPawnAtPosition(position);
            }
            return gameBoard[position.getCol()][position.getRow()];
        }
        return null;
    }

    public Pawn getPawnAtPosition(Position position){
        return (Pawn) gameBoard[position.getCol()][position.getRow()];
    }

    /**
     * clear: removes a piece from a position
     * @param p position
     */
    public void clear(Position p){
        if(getPieceAtPosition(p).getOwner() == firstPlayer){
            this.firstPlayerPositions.get(getPieceAtPosition(p).getID()).clear();
        } else {
            this.secondPlayerPositions.get(getPieceAtPosition(p).getID()).clear();
        }
        gameBoard[p.getCol()][p.getRow()] = null;
    }

    /**
     * isOutside: checks if a position is outside the game board's borders
     * @param p position
     * @return true if the position is outside the board
     */
    public boolean isOutside(Position p){
        return (p.getRow() >= getBoardSize() || p.getCol() >= getBoardSize()) ||
                (p.getRow() < 0 || p.getCol() < 0);
    }

    /**
     * getSecondPlayer:
     * @return secondPlayer
     */
    @Override
    public Player getSecondPlayer() {
        return secondPlayer;
    }

    /**
     * getFirstPlayer:
     * @return firstPlayer
     */
    @Override
    public Player getFirstPlayer() {
        return firstPlayer;
    }

    /**
     * isGameFinished: checks if any of the winning
     * scenarios has happened on the board.
     * @return true if any of the sides won
     */
    @Override
    public boolean isGameFinished() {
        //      [v] king in corners (PlayerOne wins + 1)
        //      [v] PlayerTwo's team eliminated (PlayerOne wins + 1)
        //      [v] King captured (PlayerTwo wins + 1)

        if(kingInCorners() || secondTeamEliminated()){
            //System.out.println("something");
            secondPlayer.addWins();
            currentWinner = 1;
            printStats(false);
            start();
            return true;
        }
        if (kingCaptured()) {
            //System.out.println("king captured");
            currentWinner = -1;
            firstPlayer.addWins();
            printStats(true);
            start();
            return true;
        }

        return false;

}

    /**
     * computeSteps: receives an ArrayList of positions, and computes steps
     * @param pHistory positions history
     * @return number of steps performed
     */
    public static int computeSteps(ArrayList<Position> pHistory){
        int steps = 0;
        for (int i = 1; i < pHistory.size(); i++) {
            Position currentPos = pHistory.get(i);
            Position prevPos = pHistory.get(i - 1);
            int colDiff = Math.abs(currentPos.getCol() - prevPos.getCol());
            int rowDiff = Math.abs(currentPos.getRow() - prevPos.getRow());
            steps += colDiff + rowDiff;
        }

        return steps;
    }

    /**
     * stepsComparator: Comparator for sorting ArrayLists of Positions
     * based on the number of steps and, if equal, the IDs of the pieces at their final positions.
     */
   Comparator<ArrayList<Position>> stepsComparator = new Comparator<>() {
       @Override
       public int compare(ArrayList o1, ArrayList o2) {
           if(o1.size() == o2.size() && !o1.isEmpty()){
                return getPieceAtPosition((Position) o1.get(o1.size()-1)).getID() - getPieceAtPosition((Position) o2.get(o2.size()-1)).getID();
           }
           return o1.size() - o2.size();
       }
   };

    /**
     * printStats: upon a finished game this function is called
     * and prints statistics according to the game.
     * @param attackersWon to know which player won
     */
    private void printStats(boolean attackersWon) {
        secondPlayerPositions.sort(stepsComparator);
        firstPlayerPositions.sort(stepsComparator);
        if(attackersWon){
            printAttackersHistory();
            printDefendersHistory();
            printStars();
        } else {
            printDefendersHistory();
            printAttackersHistory();
            printStars();
        }
        System.out.println();
        printKills();
        printStars();
        System.out.println();
        printPiecesTotalTravel();
        printStars();
        System.out.println();
        printSquaresHistory();
        printStars();
        System.out.println();

    }
    /**
     * compareSquareHistory: Comparator for sorting Positions based on the size of their corresponding
     * square history lists.
     * If sizes are equal, it further compares based on column
     * and row indices of the Positions.
     * Sorting is done in descending order of
     * square history list sizes.
     */
    Comparator<Position> compareSquareHistory = new Comparator<Position>() {
        @Override
        public int compare(Position o1, Position o2) {
            if (squareHistory[o1.getCol()][o1.getRow()].size() == squareHistory[o2.getCol()][o2.getRow()].size()) {
                if (o1.getCol() != o2.getCol()) {
                    return o1.getCol() - o2.getCol();
                } else {
                    return o1.getRow() - o2.getRow();
                }

            }
            return squareHistory[o2.getCol()][o2.getRow()].size() - squareHistory[o1.getCol()][o1.getRow()].size();
        }
    };

    /**
     * printSquareHistory: prints how many different pieces have stepped on a position.
     */
    private void printSquaresHistory() {
        ArrayList<Position> squaresWithHistory = new ArrayList<>();
        for (int i = 0; i < squareHistory.length; i++) {
            for (int j = 0; j < squareHistory.length; j++) {
                Position currentPos = new Position(i, j);
                if(squareHistory[i][j].size() > 1){
                    squaresWithHistory.add(currentPos);
                }
            }
        }
        squaresWithHistory.sort(compareSquareHistory);
        for (int i = 0; i < squaresWithHistory.size(); i++) {
            Position pos = new Position(squaresWithHistory.get(i));
            System.out.println("("+pos.getCol()+", "+ pos.getRow()+")"+squareHistory[pos.getCol()][pos.getRow()].size()+" pieces");
        }
    }

    /**
     * printPiecesTotalTravel: prints pieces that traveled and their positions' history.
     */
    private void printPiecesTotalTravel() {
        ArrayList<ArrayList<Position>> allPos = new ArrayList<>();
        for (int i = 0; i < firstPlayerPositions.size(); i++) {
            if(firstPlayerPositions.get(i).size() > 1){
                allPos.add(firstPlayerPositions.get(i));
            }
        }
        for (int i = 0; i < secondPlayerPositions.size(); i++) {
            if(secondPlayerPositions.get(i).size() > 1){
                allPos.add(secondPlayerPositions.get(i));
            }
        }
//        allPos.addAll(firstPlayerPositions);
//        allPos.addAll(secondPlayerPositions);

        allPos.sort(travelComparator);
        for (int i = 0; i < allPos.size(); i++) {
            ConcretePiece piece = getPieceAtPosition(allPos.get(i).get(allPos.get(i).size()-1));
            if(piece.getOwner() == firstPlayer){
                if(piece.getID() != 7){
                System.out.println("D"+piece.getID()+": "+computeSteps(allPos.get(i))+" squares");
                } else {
                    System.out.println("K"+piece.getID()+": "+computeSteps(allPos.get(i))+" squares");
                }
            } else {
                System.out.println("A"+piece.getID()+": "+computeSteps(allPos.get(i))+" squares");
            }

        }


    }

    /**
     * travelComparator:
     *  Comparator for sorting ArrayLists of Positions based on the total steps
     *  taken to reach their final positions.
     *  If step counts are equal, it further compares based on the IDs of the pieces at their final positions.
     *  If both are equal, it considers the current game winner and prioritizes the secondPlayer.
     *  Sorting is done in descending order of step counts.
     */
    Comparator<ArrayList<Position>> travelComparator = new Comparator<ArrayList<Position>>() {
        @Override
        public int compare(ArrayList<Position> o1, ArrayList<Position> o2) {
            ConcretePiece o1Piece = getPieceAtPosition(o1.get(o1.size()-1));
            ConcretePiece o2Piece = getPieceAtPosition(o2.get(o2.size()-1));
            if(computeSteps(o1) == computeSteps(o2)){
                if(o1Piece.getID() == o2Piece.getID()){
                    if(currentWinner == -1 && o1Piece.getOwner() == secondPlayer){
                        return 1;
                    } else {
                        return -1;
                    }
                }
                return o1Piece.getID() - o2Piece.getID();
            }
            return computeSteps(o2) - computeSteps(o1);
        }
    };

    /**
     * printAttackersHistory: prints Attackers' position history.
     */
    private void printAttackersHistory(){
        for (int i = 0; i < secondPlayerPositions.size(); i++) {
            if(secondPlayerPositions.get(i).size() > 1 && secondPlayerPositions.get(i).get(0) != null) {
                System.out.print("A" + getPieceAtPosition(secondPlayerPositions.get(i).get(secondPlayerPositions.get(i).size()-1)).getID() + ": [");
                for (int j = 0; j < secondPlayerPositions.get(i).size(); j++) {
                    if(j != secondPlayerPositions.get(i).size() -1){
                    System.out.print(secondPlayerPositions.get(i).get(j)+", ");}
                    else{
                        System.out.print(secondPlayerPositions.get(i).get(j)+"]");
                    }
                }

                //System.out.print("TOTAL STEPS:"+computeSteps(sP.get(i)));
                System.out.println();
            }
        }
    }

    /**
     * printDefendersHistory: prints Defenders' position history.
     */
    private void printDefendersHistory(){
        for (int i = 0; i < firstPlayerPositions.size(); i++) {
            if(firstPlayerPositions.get(i) != null && !firstPlayerPositions.get(i).isEmpty() && firstPlayerPositions.get(i).size() > 1 && firstPlayerPositions.get(i).get(0) != null) {
                if(getPieceAtPosition(firstPlayerPositions.get(i).get(firstPlayerPositions.get(i).size()-1)).getID() != 7){
                System.out.print("D" + getPieceAtPosition(firstPlayerPositions.get(i).get(firstPlayerPositions.get(i).size()-1)).getID() + ": [");
                } else {
                    System.out.print("K" + getPieceAtPosition(firstPlayerPositions.get(i).get(firstPlayerPositions.get(i).size()-1)).getID() + ": [");
                }
                for (int j = 0; j < firstPlayerPositions.get(i).size(); j++) {
                    if(j != firstPlayerPositions.get(i).size() -1){
                        System.out.print(firstPlayerPositions.get(i).get(j)+", ");}
                    else{
                        System.out.print(firstPlayerPositions.get(i).get(j)+"]");
                    }
                }

                //System.out.print("TOTAL STEPS:"+computeSteps(fP.get(i)));
                System.out.println();
            }
        }
    }

    /**
     * printStars: prints 75 stars to divide between printed statistics
     */
    private void printStars(){
        for (int i = 0; i < 75; i++) {
            System.out.print("*");
        }
    }


    /** killsComparator:
     * Comparator for sorting Pawns based on the number of kills they have achieved.
     * If kill counts are equal, it further compares based on the IDs of the Pawns.
     * If both are equal, it considers the current game winner and prioritizes the secondPlayer.
     * Sorting is done in descending order of kill counts.
     */
    Comparator<Pawn> killsComparator = new Comparator<Pawn>() {
        @Override
        public int compare(Pawn o1, Pawn o2) {
            if(o1.getKills() == o2.getKills()){
                if(o1.getID() == o2.getID()){
                    if(currentWinner == -1 && o1.getOwner() == secondPlayer){
                        return 1;
                    } else {
                        return -1;
                    }
                }
                return o1.getID() - o2.getID();
            }
            return o2.getKills() - o1.getKills();
        }
    };

    /**
     * printKills: prints the kills of pieces who had captured other pieces and stayed until the end of the game.
     */
    private void printKills(){
        ArrayList<Pawn> leftPieces = new ArrayList<Pawn>();
        for (int i = 0; i < getBoardSize(); i++) {
            for (int j = 0; j < getBoardSize(); j++) {
                if (!(gameBoard[i][j] instanceof King)) {
                    Pawn piece = getPawnAtPosition(new Position(i, j));
                    if (gameBoard[i][j] != null && piece != null && piece.getKills() > 0) {
                        leftPieces.add(piece);
                    }
                }
            }
        }
        leftPieces.sort(killsComparator);
        for (int i = 0; i < leftPieces.size(); i++) {
            if(leftPieces.get(i).getOwner() == firstPlayer){
            System.out.println("D"+leftPieces.get(i).getID()+": "+leftPieces.get(i).getKills()+" kills");
            } else {
                System.out.println("A"+leftPieces.get(i).getID()+": "+leftPieces.get(i).getKills()+" kills");
        }

        }

    }

    /**
     * kingCaptured: each game move/iteration this function checks the latest position of the kind
     * and if he is surrounded according to game laws.
     * @return true if the king is surrounded by four attackers, or by 3 and against the border
     */
    private boolean kingCaptured() {
        Position kingPos = firstPlayerPositions.get(7).get(firstPlayerPositions.get(7).size() - 1);
        int attackers = 0;

        if (kingPos != null) {
            Position[] aroundKing = {new Position(kingPos.getCol(), kingPos.getRow() - 1),
                    new Position(kingPos.getCol(), kingPos.getRow() + 1),
                    new Position(kingPos.getCol() - 1, kingPos.getRow()),
                    new Position(kingPos.getCol() + 1, kingPos.getRow())};

            int nullCounter = 0;
            int whichOne = 0;
            for (int i = 0; i < aroundKing.length; i++) {
                if (getPieceAtPosition(aroundKing[i]) != null) {
                    if (getPieceAtPosition(aroundKing[i]).getOwner() == firstPlayer) {

                        return false;
                        } else {
                        attackers++;
                        }
                } else {
                    nullCounter++;
                    whichOne = i;
                }
            }
            if (nullCounter == 1) {
                if (isOutside(aroundKing[whichOne])) {
                    return true;
                }
            }
        }

        return attackers == 4;
    }


    /**
     * secondTeamEliminated: checks if the attackers' array is empty which points on elimination of the team
     * @return true if all the attackers' pieces have been captured
     */
    private boolean secondTeamEliminated() {
        for (int i = 0; i < secondPlayerPositions.size(); i++) {
            if(!secondPlayerPositions.get(i).isEmpty()){
                return false;
            }
        }
        return true;
    }

    /**
     * kingInCorners: checks if the game made it to the corners
     * @return true of the king has got to one of the board's corners
     */
    public boolean kingInCorners(){

    if(gameBoard[0][0] instanceof King || gameBoard[10][0] instanceof King ||
            gameBoard[0][10] instanceof King || gameBoard[10][10] instanceof King) {
        //System.out.println("king in corner");
        return true;
    }
    return false;
}


    /**
     * isSecondPlayerTurn:
     * @return true if it's attackers move
     */
    @Override
    public boolean isSecondPlayerTurn() {
        return isSecondPlayerTurn;
    }

    /**
     * reset: initializes the game and statistics and starts again.
     */
    @Override
    public void reset() {
        secondPlayer = new ConcretePlayer(false); // Attackers
        firstPlayer = new ConcretePlayer(true); // Defender
            this.isSecondPlayerTurn = true;
            for(int i = 0; i < getBoardSize(); i++){
                for (int j = 0; j < getBoardSize(); j++) {
                    this.gameBoard[i][j] = null;
                }
            }

            this.start();

    }

    /**
     * start: initializes all the data structures needed for the game.
     *        initializes all the pieces on the board.
     */
    public void start(){
        gameBoard = new ConcretePiece[getBoardSize()][getBoardSize()];
        currentWinner = 0; // 0 = No one won yet. 1 = Defenders won last. -1 = Attackers won last.
        firstPlayerPositions = new ArrayList<>();
        secondPlayerPositions = new ArrayList<>();
        squareHistory = new ArrayList[getBoardSize()][getBoardSize()];
        gameBoardHistory = new Stack<>();

        // DEFENDERS POSITION TRACKER
        for (int i = 0; i <= 14; i++) {
            firstPlayerPositions.add(i, new ArrayList<Position>());
        }
        // ATTACKERS POSITIONS TRACKER
        for (int i = 0; i <= 25; i++) {
            secondPlayerPositions.add(i, new ArrayList<Position>());
        }

        // SQUARE HISTORY INITIALIZER
        for (int i = 0; i < squareHistory.length; i++) {
            for (int j = 0; j < squareHistory.length; j++) {
                squareHistory[i][j] = new ArrayList<ConcretePiece>();
            }
        }

        attackerInitializer();
        defenderInitializer();

        for (int i = 0; i < getBoardSize(); i++) {
            for (int j = 0; j < getBoardSize(); j++) {
                if(gameBoard[i][j] != null && getPieceAtPosition(new Position(j, i)) != null){
                    if(getPieceAtPosition(new Position(j, i)).getOwner() == firstPlayer){
                        firstPlayerPositions.get(getPieceAtPosition(new Position(i, j)).getID()).add(new Position(i, j));
                        squareHistory[i][j].add(getPieceAtPosition(new Position(i, j)));
                    }
                    else {
                        secondPlayerPositions.get(getPieceAtPosition(new Position(i, j)).getID()).add(new Position(i, j));
                        squareHistory[i][j].add(getPieceAtPosition(new Position(i, j)));
                    }
                }
            }
        }

        ConcretePiece[][] initialSnapshot = copyGameBoard();
        gameBoardHistory.clear(); // every new game results
        // in clearing the game history to prevent undo to a previous game
        gameBoardHistory.push(initialSnapshot);
    }

    /**
     * defenderInitializer: initializes the first team (defenders) on the game board.
     */
    private void defenderInitializer() {
        int id = 2;
        gameBoard[5][3] = new Pawn(this.firstPlayer, 1);
        gameBoard[5][7] = new Pawn(this.firstPlayer, 13);
        for (int i = 0; i < 3; i++) {
            gameBoard[i + 4][4] = new Pawn(this.firstPlayer, id++);
        }
        for (int i = 0; i < 5; i++) {
            if(i == 2){
                gameBoard[5][5] = new King(firstPlayer, 7);
                id++;
                continue;
            } gameBoard[i + 3][5] = new Pawn(this.firstPlayer, id++);
        }
        for (int i = 0; i < 3; i++) {
            gameBoard[i + 4][6] = new Pawn(this.firstPlayer, id++);
        }
    }

    /**
     * attackersInitializer: initializes the second team (attackers) on the game board.
     */
    private void attackerInitializer() {
        for (int i = 0; i < 5; i++) {
            gameBoard[i + 3][0] = new Pawn(this.secondPlayer, i +1);
            gameBoard[i + 3][10] = new Pawn(this.secondPlayer, 20 + i);
        }
        gameBoard[5][1] = new Pawn(this.secondPlayer, 6);
        gameBoard[5][9] = new Pawn(this.secondPlayer, 19);
        int id = 7;
        for (int i = 0; i < 2; i++) {
            gameBoard[0][i + 3] = new Pawn(this.secondPlayer, id);
            gameBoard[10][i + 3] = new Pawn(this.secondPlayer, id+1);
            id+=2;
        }
        for (int i = 0; i < 2; i++) {
            gameBoard[i][5] = new Pawn(this.secondPlayer, i + 11);
            gameBoard[10 - i][5] = new Pawn(this.secondPlayer, 14 - i);
        }
        id = 15;
        for (int i = 0; i < 2; i++) {
            gameBoard[0][i + 6] = new Pawn(this.secondPlayer, id);
            gameBoard[10][i + 6] = new Pawn(this.secondPlayer, id+1);
            id+=2;
        }

    }

    /**
     * undoLastMove: using a data structure of stack that holds
     * snapshots of every single move on the board as gameBoards[][] of ConcretePieces.
     * This function can return backwards in game history without interrupting game wins / player turns abuse.
     */

    @Override
    public void undoLastMove() {
        System.out.println(gameBoardHistory.size());
        if(!gameBoardHistory.isEmpty()){
            if(gameBoardHistory.size() > 1) {
                gameBoardHistory.pop();
                ConcretePiece[][] lastMoveBoard = gameBoardHistory.pop();
                restoreSnapshot(lastMoveBoard);
                gameBoardHistory.push(lastMoveBoard);
                this.isSecondPlayerTurn = !isSecondPlayerTurn();
            } else {
                ConcretePiece[][] lastMoveBoard = gameBoardHistory.pop();
                restoreSnapshot(lastMoveBoard);
                gameBoardHistory.push(lastMoveBoard);
            }
        }
    }

    /**
     * restoreSnapshot: initializes the gameBoard to be as the desired snapshot.
     * @param snapshot desired snapshot (usually one step backward)
     */
    private void restoreSnapshot(ConcretePiece[][] snapshot){
        for (int i = 0; i < getBoardSize(); i++) {
            for (int j = 0; j < getBoardSize(); j++) {
                gameBoard[i][j] = snapshot[i][j];
            }
        }
    }

    /**
     * getBoardSize: Viking Chess board is 11x11
     * @return 11
     */
    @Override
    public int getBoardSize() {
        return 11;
    }

    /**
     * insideBoard: checks if a position is within the board.
     * @param p position
     * @return true if the position is inside the board.
     */
    public boolean insideBoard(Position p){
        return (p.getRow() <= getBoardSize() && p.getCol() <= getBoardSize()) &&
                (p.getCol() >= 0 && p.getRow() >= 0);


    }
}
