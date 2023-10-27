package invaders.mementoUndo;

import java.util.LinkedList;
import java.util.Queue;

public class Caretaker {
    private  GameMemento gameMemento;

    public void setGameMementos(GameMemento gameMemento){
        this.gameMemento = gameMemento;
    }
    public GameMemento getGameMementos(){
        return gameMemento;
    }


}
