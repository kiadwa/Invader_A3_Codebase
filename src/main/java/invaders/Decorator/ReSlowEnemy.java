package invaders.Decorator;

import invaders.engine.GameEngine;
import invaders.gameobject.Enemy;
import invaders.rendering.Renderable;
import invaders.strategy.FastProjectileStrategy;
import invaders.strategy.SlowProjectileStrategy;

import java.util.List;

public class ReSlowEnemy extends CheatingDecorator{
    public ReSlowEnemy(GameEngine gameEngine) {
        super(gameEngine);
    }

    @Override
    public void remove(){
        this.quantity = 0;
        List<Renderable> renderableList = this.gameEngine.getRenderables();
        for(Renderable renderable: renderableList){
            if(renderable.getRenderableObjectName().equals("Enemy")){
                Enemy enemy = (Enemy)renderable;
                if(enemy.getProjectileStrategy() instanceof SlowProjectileStrategy){
                    renderable.takeDamage(renderable.getHealth());
                    this.quantity++;
                }
            }
        }
    }
}
