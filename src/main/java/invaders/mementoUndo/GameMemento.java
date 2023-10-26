package invaders.mementoUndo;

import invaders.gameobject.GameObject;
import invaders.rendering.Renderable;

import java.util.ArrayList;
import java.util.List;

public class GameMemento {
    private List<Renderable> renderables = new ArrayList<>();
    private List<GameObject> gameObjects  = new ArrayList<>();
    private PlayerMemento playerMemento;
    private ScoreObserverMemento scoreObserverMemento;
    private TimeObserverMemento timeObserverMemento;

    public void setTimeObserverMemento(TimeObserverMemento timeObserverMemento){
        this.timeObserverMemento = timeObserverMemento;
    }
    public TimeObserverMemento getTimeObserverMemento(){
        return this.timeObserverMemento;
    }

    public ScoreObserverMemento getScoreObserverMemento() {
        return scoreObserverMemento;
    }

    public void setScoreObserverMemento(ScoreObserverMemento scoreObserverMemento){
        this.scoreObserverMemento = scoreObserverMemento;
    }

    public void setGameRenderablesState(List<Renderable> renderables) {
        this.renderables = renderables;
    }
    public void setGameGameObjectsState(List<GameObject> gameObjects) {
        this.gameObjects = gameObjects;
    }

    public void setPlayerMemento(PlayerMemento playerMemento){
        this.playerMemento = playerMemento;
    }
    public PlayerMemento getPlayerMemento(){
        return this.playerMemento;
    }

    public List<Renderable> getGameRenderablesState() {
        return this.renderables;
    }
    public List<GameObject> getGameObjectsState() {
        return this.gameObjects;
    }

}
