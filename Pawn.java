

public class Pawn extends ConcretePiece{

   public int kills;
    public Pawn(ConcretePlayer player, int id) {
        super(player, id);
        this.kills = 0;

    }

    @Override
    public String getType() {
        if(p.isPlayerOne()){
            return "♙";
        } else {
            return "♟";
        }


    }


    public int getKills() {
        return kills;
    }

    public void addKill() {
        this.kills++;
    }
}
