package invaders.mementoUndo;

import java.util.LinkedList;
import java.util.Queue;

public class Caretaker {
    private  GameEngineMemento gameEngineMementos;

    public void addMemento(GameEngineMemento gameEngineMemento){
        gameEngineMementos = gameEngineMemento;
    }
    public GameEngineMemento getMemento(){
        return gameEngineMementos;
    }


}
