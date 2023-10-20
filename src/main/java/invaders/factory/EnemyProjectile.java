package invaders.factory;

import invaders.engine.GameEngine;
import invaders.physics.Collider;
import invaders.physics.Vector2D;
import invaders.prototype.EnemyProjectilePrototype;
import invaders.prototype.EnemyPrototype;
import invaders.strategy.ProjectileStrategy;
import javafx.scene.image.Image;

public class EnemyProjectile extends Projectile implements EnemyProjectilePrototype {
    private ProjectileStrategy strategy;

    public EnemyProjectile(Vector2D position, ProjectileStrategy strategy, Image image) {
        super(position,image);
        this.strategy = strategy;
    }
    public ProjectileStrategy getStrategy(){
        return this.strategy;
    }

    @Override
    public void update(GameEngine model) {
        strategy.update(this);

        if(this.getPosition().getY()>= model.getGameHeight() - this.getImage().getHeight()){
            this.takeDamage(1);
        }

    }
    @Override
    public String getRenderableObjectName() {
        return "EnemyProjectile";
    }

    @Override
    public EnemyProjectile copy() {
        EnemyProjectile enemyProjectile = new EnemyProjectile(this.getPosition(),this.strategy,this.getImage());
        return enemyProjectile;
    }
}
