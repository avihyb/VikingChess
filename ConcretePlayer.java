public class ConcretePlayer implements Player{

    boolean playerOne;
    int getWins;

    public ConcretePlayer(boolean playerOne) {

        this.playerOne = playerOne;
        this.getWins = 0;
    }

    @Override
    public boolean isPlayerOne() {
        return playerOne;
    }

    @Override
    public int getWins() {
        return getWins;
    }

    public void addWins(){
        getWins++;
    }

    public void clearWins(){
        getWins = 0;
    }


}
