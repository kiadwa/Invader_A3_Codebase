package invaders.Decorator;

import invaders.engine.GameEngine;
import invaders.factory.EnemyProjectile;
import invaders.gameobject.Enemy;
import invaders.rendering.Renderable;
import invaders.strategy.FastProjectileStrategy;

import java.util.List;

public class ReFastProjectile extends CheatingDecorator{
    public ReFastProjectile(GameEngine gameEngine) {
        super(gameEngine);
    }

    @Override
    public void remove(){
        this.quantity = 0;
        List<Renderable> renderableList = this.gameEngine.getRenderables();
        for(Renderable renderable: renderableList){
            if(renderable.getRenderableObjectName().equals("EnemyProjectile")){
                EnemyProjectile projectile = (EnemyProjectile)renderable;
                if(projectile.getStrategy() instanceof FastProjectileStrategy){
                    renderable.takeDamage(renderable.getHealth());
                    this.quantity++;
                }
            }
        }
    }
}
