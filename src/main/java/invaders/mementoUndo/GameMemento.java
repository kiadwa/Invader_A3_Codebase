package invaders.mementoUndo;

import invaders.entities.EntityView;
import invaders.entities.Player;
import invaders.gameobject.GameObject;
import invaders.rendering.Renderable;

import java.util.ArrayList;
import java.util.List;

public class GameMemento implements GameEngineMemento{
    private List<Renderable> renderables = new ArrayList<>();
    private List<GameObject> gameObjects  = new ArrayList<>();
    private List<EntityView> entityViews = new ArrayList<>();
    private Player player;

    @Override
    public void setEntityViews(List<EntityView> entityViews){
        this.entityViews = entityViews;
    }
    @Override
    public List<EntityView> getEntityViews(){
        return this.entityViews;
    }

    @Override
    public void setGameRenderablesState(List<Renderable> renderables) {
        this.renderables = renderables;
    }
    @Override
    public void setGameGameObjectsState(List<GameObject> gameObjects) {
        this.gameObjects = gameObjects;
    }

    @Override
    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public List<Renderable> getGameRenderablesState() {
        return this.renderables;
    }
    @Override
    public List<GameObject> getGameObjectsState() {
        return this.gameObjects;
    }

}
