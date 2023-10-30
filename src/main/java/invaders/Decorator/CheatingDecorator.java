package invaders.Decorator;

import invaders.engine.GameEngine;

public abstract class CheatingDecorator implements ICheating {
    protected GameEngine gameEngine;
    int quantity;

    public CheatingDecorator(GameEngine gameEngine){
        this.gameEngine = gameEngine;
    }

    public int getQuantity(){
        return this.quantity;
    }
    @Override
    public void remove(){

    }
}
