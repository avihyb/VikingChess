public class Position {
    private int col;
    private int row;

    public Position(int col, int row){
        this.col = col;
        this.row = row;
    }

    public Position(Position position){
        this.col = position.col;
        this.row = position.row;
    }

    public int getCol(){
        return this.col;
    }

    public void setCol(int col){
        this.col = col;
    }
    public void setRow(int row){
        this.row = row;
    }

    public int getRow(){
        return this.row;
    }

    public boolean notDiagonal(Position b) {
        return col == b.getCol() || row == b.getRow();
    }


    public boolean equals(Position p){
        return getRow() == p.getRow() && getCol() == p.getCol();
    }

    public String toString(){
        return "("+getCol()+", "+getRow()+")";
    }
}
