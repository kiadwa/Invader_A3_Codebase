package invaders.mementoUndo;

import invaders.gameobject.GameObject;
import invaders.rendering.Renderable;

import java.util.ArrayList;
import java.util.List;

public class GameMemento implements GameEngineMemento{
    private List<Renderable> renderables = new ArrayList<>();
    private List<Renderable> pendingToAddRenderables  = new ArrayList<>();
    private List<Renderable> pendingToRemoveRenderables  = new ArrayList<>();

    private List<GameObject> gameObjects  = new ArrayList<>();
    private List<GameObject> pendingToAddGameObjects  = new ArrayList<>();
    private List<GameObject> pendingToRemoveGameObjects  = new ArrayList<>();

    @Override
    public void setGameRenderablesState(List<Renderable> renderables) {
        this.renderables = renderables;
    }

    @Override
    public void setGameGameObjectsState(List<GameObject> gameObjects) {
        this.gameObjects = gameObjects;
    }

    @Override
    public void setPendingToAddRenderables(List<Renderable> renderables) {
        this.pendingToAddRenderables = renderables;
    }

    @Override
    public void setPendingToRemoveRenderables(List<Renderable> renderables) {
        this.pendingToRemoveRenderables = renderables;
    }

    @Override
    public void setPendingToAddGameObjects(List<GameObject> gameObjects) {
        this.pendingToAddGameObjects = gameObjects;
    }

    @Override
    public void setPendingToRemoveGameObjects(List<GameObject> gameObjects) {
        this.pendingToRemoveGameObjects = gameObjects;
    }

    @Override
    public List<Renderable> getGameRenderablesState() {
        return this.renderables;
    }

    @Override
    public List<Renderable> getPendingToAddRenderables() {
        return this.pendingToAddRenderables;
    }

    @Override
    public List<Renderable> getPendingToRemoveRenderables() {
        return this.pendingToRemoveRenderables;

    }

    @Override
    public List<GameObject> getGameObjectsState() {
        return this.gameObjects;
    }

    @Override
    public List<GameObject> getPendingToAddGameObjects() {
        return this.pendingToAddGameObjects;
    }

    @Override
    public List<GameObject> getPendingToRemoveGameObjects() {
        return this.pendingToRemoveGameObjects;
    }
}
