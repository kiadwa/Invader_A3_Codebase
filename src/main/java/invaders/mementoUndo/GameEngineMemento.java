package invaders.mementoUndo;

import invaders.entities.EntityView;
import invaders.entities.Player;
import invaders.gameobject.GameObject;
import invaders.rendering.Renderable;

import java.util.List;

public interface GameEngineMemento {
    void setEntityViews(List<EntityView> entityViews);
    void setGameRenderablesState(List<Renderable> renderables);
    void setGameGameObjectsState(List<GameObject> gameObjects);
    void setPlayer(Player player);
    Player getPlayer();
    List<Renderable> getGameRenderablesState();
    List<GameObject> getGameObjectsState();
    List<EntityView> getEntityViews();


}
