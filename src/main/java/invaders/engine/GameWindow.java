package invaders.engine;

import java.util.List;
import java.util.ArrayList;

import invaders.entities.EntityViewImpl;
import invaders.entities.Player;
import invaders.entities.SpaceBackground;
import invaders.factory.EnemyProjectile;
import invaders.factory.PlayerProjectile;
import invaders.gameobject.Bunker;
import invaders.gameobject.Enemy;
import invaders.gameobject.GameObject;
import invaders.mementoUndo.GameEngineMemento;
import invaders.mementoUndo.GameMemento;
import invaders.mementoUndo.Originator;
import invaders.observer.ConcreteScoreObs;
import invaders.observer.ConcreteTimeObs;
import invaders.observer.Observer;
import invaders.observer.Subject;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.util.Duration;

import invaders.entities.EntityView;
import invaders.rendering.Renderable;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public class GameWindow implements Subject, Originator {
	private final int width;
    private final int height;
	private Scene scene;
    private Pane pane;
    private GameEngine model;
    private List<EntityView> entityViews =  new ArrayList<EntityView>();
    private Renderable background;
    Timeline timeline;
    private double xViewportOffset = 0.0;
    private double yViewportOffset = 0.0;
    private KeyboardInputHandler keyboardInputHandler;
    private boolean diffChanged = false;
    // private static final double VIEWPORT_MARGIN = 280.0;
    private GraphicsContext gc;
    private ConcreteTimeObs timeObs;
    private GameEngineMemento gameEngineMemento;
	public GameWindow(GameEngine model){
        this.model = model;
		this.width =  model.getGameWidth();
        this.height = model.getGameHeight();
        timeObs = new ConcreteTimeObs();
        pane = new Pane();
        scene = new Scene(pane, width, height);
        this.background = new SpaceBackground(model, pane);

        keyboardInputHandler = new KeyboardInputHandler(this.model);

        scene.setOnKeyPressed(keyboardInputHandler::handlePressed);
        scene.setOnKeyReleased(keyboardInputHandler::handleReleased);

        Canvas canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();
        pane.getChildren().add(canvas);
    }

	public void run() {
         timeline = new Timeline(new KeyFrame(Duration.millis(17), t -> this.draw()));

         timeline.setCycleCount(Timeline.INDEFINITE);
         timeline.play();
    }
    //pause game
    public void pauseGame(){
        if(timeline != null){
            timeline.pause();
        }
    }
    //resume game
    public void resumeGame(){
        if(timeline!= null){
            timeline.play();
        }
    }
    public void detectModeChange(){
        if(keyboardInputHandler.isEasyMode() && keyboardInputHandler.getModeJustChanged()){
            keyboardInputHandler.setModeJustChanged(false);
            this.diffChanged = true;
        }else if(keyboardInputHandler.getModeJustChanged() && keyboardInputHandler.isMediumMode()){
            keyboardInputHandler.setModeJustChanged(false);
            this.diffChanged = true;
        }else if(keyboardInputHandler.getModeJustChanged() && keyboardInputHandler.isHardMode()){
            keyboardInputHandler.setModeJustChanged(false);
            this.diffChanged = true;
        }
    }
    public void revertModeChange(){
        if(diffChanged){
            //put operations to change mode here.
            System.out.println("Change Mode now");
            this.diffChanged = false;
        }
    }
    public void printScoreBoard(){
        gc.setFill(Paint.valueOf("WHITE"));
        Font font = new Font("Ariel", 40);
        gc.setFont(font);
        gc.fillText(model.getObservers().toString(),
                300,
                40, 100);
    }
    public void printClock(){
        gc.setFill(Paint.valueOf("WHITE"));
        Font font = new Font("Ariel", 40);
        gc.setFont(font);
        gc.fillText(this.timeObs.toString(),500,40,100);
    }


    private void draw(){

        this.timeObs.update();
        gc.clearRect(0, 0, width, height);
        model.update();
        detectModeChange();
        revertModeChange();

        printScoreBoard();
        printClock();
        if(this.keyboardInputHandler.isSaving()){
            this.gameEngineMemento = this.save();
            this.keyboardInputHandler.setSaving(false);
        }
        if(this.keyboardInputHandler.isRestoring()){
            if(this.gameEngineMemento != null){
                this.restore(gameEngineMemento);
                this.keyboardInputHandler.setRestoring(false);
            }
        }

        List<Renderable> renderables = model.getRenderables();
        for (Renderable entity : renderables) {
            boolean notFound = true;
            for (EntityView view : entityViews) {
                if (view.matchesEntity(entity)) {
                    notFound = false;
                    view.update(xViewportOffset, yViewportOffset);
                    break;
                }
            }
            if (notFound) {
                EntityView entityView = new EntityViewImpl(entity);
                entityViews.add(entityView);
                pane.getChildren().add(entityView.getNode());
            }
        }

        for (Renderable entity : renderables){
            if (!entity.isAlive()){
                for (EntityView entityView : entityViews){
                    if (entityView.matchesEntity(entity)){
                        entityView.markForDelete();
                    }
                }
            }
        }

        for (EntityView entityView : entityViews) {
            if (entityView.isMarkedForDelete()) {
                pane.getChildren().remove(entityView.getNode());
            }
        }



        model.getGameObjects().removeAll(model.getPendingToRemoveGameObject());
        model.getGameObjects().addAll(model.getPendingToAddGameObject());
        model.getRenderables().removeAll(model.getPendingToRemoveRenderable());
        model.getRenderables().addAll(model.getPendingToAddRenderable());

        model.getPendingToAddGameObject().clear();
        model.getPendingToRemoveGameObject().clear();
        model.getPendingToAddRenderable().clear();
        model.getPendingToRemoveRenderable().clear();

        entityViews.removeIf(EntityView::isMarkedForDelete);


    }

	public Scene getScene() {
        return scene;
    }

    @Override
    public void addObserver(Observer obs) {
        this.timeObs = (ConcreteTimeObs) obs;
    }

    @Override
    public void removeObserver(Observer obs) {
        this.timeObs = null;
    }

    @Override
    public void notifyObserver() {
        this.timeObs.update();
    }

    @Override
    public GameEngineMemento save() {
        GameMemento gameMemento = new GameMemento();
        ArrayList<Renderable> renderableMemento = new ArrayList<>();
        ArrayList<GameObject> gameObjectMemento = new ArrayList<>();
        //ArrayList<EntityView> entityViewsMemento = new ArrayList<>();
        for(Renderable renderable: model.getRenderables()){
            Renderable copyRenderable = renderable.copyR();
            renderableMemento.add(copyRenderable);
            //entityViewsMemento.add(new EntityViewImpl(renderable));
            if(copyRenderable.getRenderableObjectName().equals("Enemy")){
                gameObjectMemento.add((Enemy)copyRenderable);
            }else if(copyRenderable.getRenderableObjectName().equals("Bunker")){
                gameObjectMemento.add((Bunker) copyRenderable);
            }else if(copyRenderable.getRenderableObjectName().equals("EnemyProjectile")){
                gameObjectMemento.add((EnemyProjectile) copyRenderable);
            }else if(copyRenderable.getRenderableObjectName().equals("PlayerProjectile")){
                gameObjectMemento.add((PlayerProjectile) copyRenderable);
            }
        }
        gameMemento.setPlayer((Player)model.getPlayer().copyR());
        //gameMemento.setEntityViews(entityViewsMemento);
        gameMemento.setGameGameObjectsState(gameObjectMemento);
        gameMemento.setGameRenderablesState(renderableMemento);

        System.out.println("Org renderable size " + model.getRenderables().size());
        int cntEnemy = 0;
        int cntEnemyProjectile = 0;
        int cntBunker = 0;
        int cntPlayer = 0;
        int cntPlayerProjectile = 0;
        for(Renderable renderable: model.getRenderables()){
            if(renderable.getRenderableObjectName().equals("Enemy")){
                cntEnemy++;
            }else if(renderable.getRenderableObjectName().equals("EnemyProjectile")){
                cntEnemyProjectile++;
            }else if(renderable.getRenderableObjectName().equals("Bunker")){
                cntBunker ++;
            }else if(renderable.getRenderableObjectName().equals("Player")){
                cntPlayer++;
            }else if (renderable.getRenderableObjectName().equals("PlayerProjectile")){
                cntPlayerProjectile++;
            }
        }
        System.out.println("Enemy "+ cntEnemy +" EnemyProj " + cntEnemyProjectile + " Bunker "+ cntBunker +" cnt Player "+ cntPlayer + " cnt Player Projectile "+ cntPlayerProjectile);

        System.out.println("Org game object size " + model.getGameObjects().size());

        System.out.println("Memento ren size " + gameMemento.getGameRenderablesState().size() );
        System.out.println("Memento go size " + gameMemento.getGameObjectsState().size() );
        System.out.println("Mement Ent V size " + gameMemento.getEntityViews().size());

        return gameMemento;
    }

    @Override
    public void restore(GameEngineMemento memento) {
        this.model.setRenderables(memento.getGameRenderablesState());
        this.model.setGameObjects(memento.getGameObjectsState());
        //this.entityViews = memento.getEntityViews();
        this.model.setPlayer(memento.getPlayer());
    }
}
