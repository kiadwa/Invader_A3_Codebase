package invaders.mementoUndo;

import invaders.physics.Vector2D;

public class PlayerMemento {
    Vector2D position;
    double health;

    public void setPosition(Vector2D vector2D) {
        this.position = vector2D;
    }


    public void setHealth(double health) {
        this.health = health;
    }


    public Vector2D getPosition() {
        return this.position;
    }


    public double getHealth() {
        return this.health;
    }
}
