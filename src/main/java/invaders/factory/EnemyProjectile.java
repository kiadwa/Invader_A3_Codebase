package invaders.factory;

import invaders.engine.GameEngine;
import invaders.gameobject.GameObject;
import invaders.physics.Vector2D;
import invaders.rendering.Renderable;
import invaders.strategy.FastProjectileStrategy;
import invaders.strategy.ProjectileStrategy;
import invaders.strategy.SlowProjectileStrategy;
import javafx.scene.image.Image;

import java.io.File;

public class EnemyProjectile extends Projectile  {
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
    public Renderable copyR() {
        Vector2D vector2D = new Vector2D(this.getPosition().getX(),this.getPosition().getY());
        Image image;
        ProjectileStrategy projectileStrategy;
        if(this.strategy instanceof SlowProjectileStrategy) {
            image = new Image(new File("src/main/resources/alien_shot_slow.png").toURI().toString(), 10, 10, true, true);
            projectileStrategy = new SlowProjectileStrategy();
        }
        else{
            image = new Image(new File("src/main/resources/alien_shot_fast.png").toURI().toString(), 10, 10, true, true);
            projectileStrategy = new FastProjectileStrategy();
        }
        return new EnemyProjectile(vector2D,projectileStrategy,image);
    }



}
