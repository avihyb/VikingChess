

public abstract class ConcretePiece implements Piece{
    ConcretePlayer p;
     int id;




    public ConcretePiece(ConcretePlayer p, int id) {
        this.p = p;
        this.id = id;

    }

    @Override
    public Player getOwner() {
        return p;
    }


    public int getID() {
        return this.id;
    }





}
