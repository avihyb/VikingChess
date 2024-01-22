import java.util.*;

public class GameLogic implements PlayableLogic {

    ConcretePiece[][] gameBoard = new ConcretePiece[getBoardSize()][getBoardSize()];
    ConcretePlayer secondPlayer = new ConcretePlayer(false); // Attackers
    ConcretePlayer firstPlayer = new ConcretePlayer(true); // Defender
    Position[] corners = {new Position(0,0), new Position(10,0), new Position(0,10), new Position(10,10)};

    int currentWinner = 0; // 0 = No one won yet. 1 = Defenders won last. -1 = Attackers won last.
    boolean isSecondPlayerTurn = true;


    ArrayList<ArrayList<Position>> firstPlayerPositions = new ArrayList<>();
    ArrayList<ArrayList<Position>> secondPlayerPositions = new ArrayList<>();

    private ArrayList[][] squareHistory = new ArrayList[getBoardSize()][getBoardSize()];



    public GameLogic() {
        this.start();


    }



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

                moveIt(a, b);

                isSecondPlayerTurn = !isSecondPlayerTurn();
                return true;
            }
        }

        return false;
    }

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

    public boolean isInCorner(Position p){
        for (Position corner : corners) {
            if (p.getRow() == corner.getRow() && p.getCol() == corner.getCol()) {
                return true;
            }
        }
        return false;
    }



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
            isGameFinished();
            //printAroundKing();

            eat(b, getPieceAtPosition(b).getOwner());

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

    public void clear(Position p){
        if(getPieceAtPosition(p).getOwner() == firstPlayer){
            this.firstPlayerPositions.get(getPieceAtPosition(p).getID()).clear();
        } else {
            this.secondPlayerPositions.get(getPieceAtPosition(p).getID()).clear();
        }
        gameBoard[p.getCol()][p.getRow()] = null;
    }

    public boolean isOutside(Position p){
        return (p.getRow() >= getBoardSize() || p.getCol() >= getBoardSize()) ||
                (p.getRow() < 0 || p.getCol() < 0);
    }

    @Override
    public Player getSecondPlayer() {
        return secondPlayer;
    }

    @Override
    public Player getFirstPlayer() {
        return firstPlayer;
    }

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
   Comparator<ArrayList<Position>> stepsComperator = new Comparator<>() {
       @Override
       public int compare(ArrayList o1, ArrayList o2) {
           if((o1.size() == o2.size() && !o1.isEmpty() && !o2.isEmpty())){
                return getPieceAtPosition((Position) o1.get(o1.size()-1)).getID() - getPieceAtPosition((Position) o2.get(o2.size()-1)).getID();
           }
           return o1.size() - o2.size();
       }
   };

    private void printStats(boolean attackersWon) {
        secondPlayerPositions.sort(stepsComperator);
        firstPlayerPositions.sort(stepsComperator);
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

    private void printStars(){
        for (int i = 0; i < 75; i++) {
            System.out.print("*");
        }
    }



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

    private boolean secondTeamEliminated() {
        for (int i = 0; i < secondPlayerPositions.size(); i++) {
            if(!secondPlayerPositions.get(i).isEmpty()){
                return false;
            }
        }
        return true;
    }

    public boolean kingInCorners(){

    if(gameBoard[0][0] instanceof King || gameBoard[10][0] instanceof King ||
            gameBoard[0][10] instanceof King || gameBoard[10][10] instanceof King) {
        //System.out.println("king in corner");
        return true;
    }
    return false;
}




    @Override
    public boolean isSecondPlayerTurn() {
        return isSecondPlayerTurn;
    }

    @Override
    public void reset() {

            this.isSecondPlayerTurn = true;
            //secondPlayer.getWins = 0;
            //firstPlayer.getWins = 0;
            for(int i = 0; i < getBoardSize(); i++){
                for (int j = 0; j < getBoardSize(); j++) {
                    this.gameBoard[i][j] = null;
                }
            }

            start();

    }

    public void start(){

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

    }


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


    @Override
    public void undoLastMove() {

    }

    @Override
    public int getBoardSize() {
        return 11;
    }

    public boolean insideBoard(Position p){
        return (p.getRow() <= getBoardSize() && p.getCol() <= getBoardSize()) &&
                (p.getCol() >= 0 && p.getRow() >= 0);


    }
}
