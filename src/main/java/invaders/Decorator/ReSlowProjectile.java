package invaders.Decorator;

import invaders.engine.GameEngine;
import invaders.factory.EnemyProjectile;
import invaders.rendering.Renderable;
import invaders.strategy.FastProjectileStrategy;
import invaders.strategy.SlowProjectileStrategy;

import java.util.List;

public class ReSlowProjectile extends CheatingDecorator{
    public ReSlowProjectile(GameEngine gameEngine) {
        super(gameEngine);
    }

    @Override
    public void remove(){
        this.quantity = 0;
        List<Renderable> renderableList = this.gameEngine.getRenderables();
        for(Renderable renderable: renderableList){
            if(renderable.getRenderableObjectName().equals("EnemyProjectile")){
                EnemyProjectile projectile = (EnemyProjectile)renderable;
                if(projectile.getStrategy() instanceof SlowProjectileStrategy){
                    renderable.takeDamage(renderable.getHealth());
                    this.quantity++;
                }
            }
        }
    }
}
