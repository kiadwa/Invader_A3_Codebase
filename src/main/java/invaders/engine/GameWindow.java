package invaders.engine;

import java.util.List;
import java.util.ArrayList;

import invaders.entities.EntityViewImpl;
import invaders.entities.SpaceBackground;
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

public class GameWindow implements Subject {
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
}
