package invaders.engine;

import java.util.List;
import java.util.ArrayList;

import invaders.entities.EntityViewImpl;
import invaders.entities.SpaceBackground;
import invaders.factory.EnemyProjectile;
import invaders.factory.PlayerProjectile;
import invaders.gameobject.Bunker;
import invaders.gameobject.Enemy;
import invaders.gameobject.GameObject;
import invaders.mementoUndo.*;
import invaders.observer.ConcreteTimeObs;
import invaders.observer.Observer;
import invaders.observer.Subject;
import invaders.physics.Vector2D;
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
    Caretaker caretaker = new Caretaker();
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
            //need a mechanic to remove entity sprites when switch difficulty
            resetPane();

            if(keyboardInputHandler.isEasyMode()) {
                model.changeDifficultyLevel(1);
                this.caretaker.setGameMementos(null);
            }else if(keyboardInputHandler.isMediumMode()){
                model.changeDifficultyLevel(2);
                this.caretaker.setGameMementos(null);
            }else if(keyboardInputHandler.isHardMode()){
                model.changeDifficultyLevel(3);
                this.caretaker.setGameMementos(null);
            }
            this.diffChanged = false;
        }
    }
    private void resetPane(){
        for(EntityView entityView: this.entityViews){
            this.pane.getChildren().remove(entityView.getNode());
        }
        this.entityViews.clear();
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
       // System.out.println(entityViews.size());
        if(!model.checkIfGameNotEnd()){
            return;
        }
        this.timeObs.update();
        gc.clearRect(0, 0, width, height);
        model.update();
        detectModeChange();
        revertModeChange();

        printScoreBoard();
        printClock();


        if(this.keyboardInputHandler.isSaving()){
            if(detectIfPlayerHasShoot()) {
                this.caretaker.setGameMementos(this.save());
                this.keyboardInputHandler.setSaving(false);
            }
        }
        if(this.keyboardInputHandler.isRestoring()){
            if(this.caretaker.getGameMementos() != null){
                this.restore(caretaker.getGameMementos());
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
    public GameMemento save() {
        GameMemento gameMemento = new GameMemento();

        ArrayList<Renderable> renderableMemento = new ArrayList<>();
        ArrayList<GameObject> gameObjectMemento = new ArrayList<>();

        for(Renderable renderable: model.getRenderables()) {
            if (!renderable.getRenderableObjectName().equals("Player")) {
                Renderable copyRenderable = renderable.copyR();
                renderableMemento.add(copyRenderable);
                //entityViewsMemento.add(new EntityViewImpl(renderable));
                if (copyRenderable.getRenderableObjectName().equals("Enemy")) {
                    gameObjectMemento.add((Enemy) copyRenderable);
                } else if (copyRenderable.getRenderableObjectName().equals("Bunker")) {
                    gameObjectMemento.add((Bunker) copyRenderable);
                } else if (copyRenderable.getRenderableObjectName().equals("EnemyProjectile")) {
                    gameObjectMemento.add((EnemyProjectile) copyRenderable);
                } else if (copyRenderable.getRenderableObjectName().equals("PlayerProjectile")) {
                    gameObjectMemento.add((PlayerProjectile) copyRenderable);
                }
            }
        }
        //Add player original into renderable memento to make sure player exist when restore
        renderableMemento.add(this.model.getPlayer());

        //init player state memento
        PlayerMemento playerMemento = new PlayerMemento();
        playerMemento.setPosition(new Vector2D(this.model.getPlayer().getPosition().getX(),this.model.getPlayer().getPosition().getY()));
        playerMemento.setHealth(this.model.getPlayer().getHealth());
        //init time and score observer memento
        TimeObserverMemento timeObserverMemento = new TimeObserverMemento(this.timeObs.getMinute(),this.timeObs.getSecond(),this.timeObs.getMillis());
        ScoreObserverMemento scoreObserverMemento = new ScoreObserverMemento(this.model.getObservers().getTotalScore());
        //save all states into game memento
        gameMemento.setGameGameObjectsState(gameObjectMemento);
        gameMemento.setGameRenderablesState(renderableMemento);
        gameMemento.setPlayerMemento(playerMemento);
        gameMemento.setTimeObserverMemento(timeObserverMemento);
        gameMemento.setScoreObserverMemento(scoreObserverMemento);
        return gameMemento;
    }

    @Override
    public void restore(GameMemento memento) {
        if(caretaker.getGameMementos() !=null) {
            resetPanePostChanges();
            //need to use matchesEntity() to remove old objects from entityview
            this.model.getPlayer().setPosition(memento.getPlayerMemento().getPosition());
            this.model.getPlayer().setHealth(memento.getPlayerMemento().getHealth());
            this.model.setRenderables(memento.getGameRenderablesState());
            this.model.setGameObjects(memento.getGameObjectsState());
            this.model.getPendingToAddGameObject().clear();
            this.model.getPendingToAddRenderable().clear();
            this.timeObs.setMillis(caretaker.getGameMementos().getTimeObserverMemento().getMillis());
            this.timeObs.setMinute(caretaker.getGameMementos().getTimeObserverMemento().getMinute());
            this.timeObs.setSecond(caretaker.getGameMementos().getTimeObserverMemento().getSecond());
            this.model.getObservers().setTotalScore(caretaker.getGameMementos().getScoreObserverMemento().getScore());
            caretaker.setGameMementos(null);
        }
    }
    /***
     * Method for removing obsolete entity view from the pane after any changes made to
     * the state of GameEngine
     */
    public void resetPanePostChanges(){
        for(Renderable renderable: this.model.getRenderables()){
            //if(!renderable.getRenderableObjectName().equals("Player")) {
                for (EntityView entityView : this.entityViews) {
                    if (entityView.matchesEntity(renderable)) {
                        this.entityViews.remove(entityView);
                        this.pane.getChildren().remove(entityView.getNode());
                        break;
                    }
                }
            //}
        }
    }
    public boolean detectIfPlayerHasShoot(){
        for(EntityView entityView: this.entityViews) {
            if(entityView.getEntity().getRenderableObjectName().equals("PlayerProjectile")){
                return true;
            }
        }
        return false;
    }

}
