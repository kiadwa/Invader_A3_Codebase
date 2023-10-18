package invaders.mementoUndo;

import invaders.gameobject.GameObject;
import invaders.rendering.Renderable;

import java.util.List;

public interface GameEngineMemento {
    void setGameRenderablesState(List<Renderable> renderables);
    void setGameGameObjectsState(List<GameObject> gameObjects);
    void setPendingToAddRenderables(List<Renderable> renderables);
    void setPendingToRemoveRenderables(List<Renderable> renderables);
    void setPendingToAddGameObjects(List<GameObject> gameObjects);
    void setPendingToRemoveGameObjects(List<GameObject> gameObjects);


    List<Renderable> getGameRenderablesState();
    List<Renderable> getPendingToAddRenderables();
    List<Renderable> getPendingToRemoveRenderables();


    List<GameObject> getGameObjectsState();
    List<GameObject> getPendingToAddGameObjects(List<GameObject> gameObjects);
    List<GameObject> getPendingToRemoveGameObjects(List<GameObject> gameObjects);


}
