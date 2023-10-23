package invaders.gameobject;

import invaders.engine.GameEngine;
import invaders.physics.Vector2D;
import invaders.rendering.Renderable;
import invaders.state.BunkerState;
import invaders.state.GreenState;
import invaders.state.RedState;
import invaders.state.YellowState;
import javafx.scene.image.Image;

import java.io.File;

public class Bunker implements GameObject, Renderable {
    private Vector2D position;
    private double width;
    private double height;
    private int lives;
    private Image image;
    private BunkerState state = new GreenState(this);


    @Override
    public void start() {}

    @Override
    public void update(GameEngine model) {
        /*
        Logic TBD
         */

    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

    @Override
    public Vector2D getPosition() {
        return this.position;
    }

    @Override
    public Layer getLayer() {
        return Layer.FOREGROUND;
    }

    @Override
    public Image getImage() {
        return image;
    }

    @Override
    public void takeDamage(double amount){
        this.lives -= 1;
        this.state.takeDamage();
    }

    @Override
	public double getHealth(){
	    return this.lives;
	}

    @Override
    public String getRenderableObjectName() {
        return "Bunker";
    }

    @Override
    public Renderable copyR() {
        Bunker bunker = new Bunker();

        if(this.getState() instanceof GreenState){
            bunker.setState(new GreenState(bunker));
            bunker.setImage(new Image(new File("src/main/resources/bunkerGreen.png").toURI().toString(), width, height, true, true));
        }else if(this.getState() instanceof YellowState){
            bunker.setState(new YellowState(bunker));
            bunker.setImage(new Image(new File("src/main/resources/bunkerYellow.png").toURI().toString(), width, height, true, true));
        }else{
            bunker.setState(new RedState(bunker));
            bunker.setImage(new Image(new File("src/main/resources/bunkerRed.png").toURI().toString(), width, height, true, true));
        }
        bunker.setPosition(new Vector2D(this.position.getX(),this.position.getY()));
        bunker.setWidth((int) this.width);
        bunker.setHeight((int) this.getHeight());
        return bunker;
    }

    @Override
	public boolean isAlive(){
	    return this.lives > 0;
	}


    @Override
    public double getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public BunkerState getState() {
        return state;
    }

    public void setState(BunkerState state) {
        this.state = state;
    }



}
