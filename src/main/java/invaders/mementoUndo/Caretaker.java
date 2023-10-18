package invaders.mementoUndo;

import java.util.LinkedList;
import java.util.Queue;

public class Caretaker {
    private Queue<GameEngineMemento> gameEngineMementos = new LinkedList<>();

    public void addMemento(GameEngineMemento gameEngineMemento){
        gameEngineMementos.add(gameEngineMemento);
    }
    public GameEngineMemento getMemento(){
        return gameEngineMementos.remove();
    }
    public Queue<GameEngineMemento> getGameEngineMementos(){
        return this.gameEngineMementos;
    }
}
