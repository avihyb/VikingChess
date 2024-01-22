public class King extends ConcretePiece{

    int id;

    public King(ConcretePlayer p, int id) {
        super(p, id);
        this.id = id;
    }

    @Override
    public String getType() {
        return "♔";
    }




}
