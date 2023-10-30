package invaders.gameobject;

import invaders.engine.GameEngine;
import invaders.factory.EnemyProjectile;
import invaders.factory.EnemyProjectileFactory;
import invaders.factory.Projectile;
import invaders.factory.ProjectileFactory;
import invaders.physics.Vector2D;
import invaders.rendering.Renderable;
import invaders.strategy.FastProjectileStrategy;
import invaders.strategy.ProjectileStrategy;
import invaders.strategy.SlowProjectileStrategy;
import javafx.scene.image.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Enemy implements GameObject, Renderable {
    private Vector2D position;
    private int lives = 1;
    private Image image;
    private int xVel = -1;

    private ArrayList<Projectile> enemyProjectile;
    private ArrayList<Projectile> pendingToDeleteEnemyProjectile;
    private ProjectileStrategy projectileStrategy;
    private ProjectileFactory projectileFactory;
    private Image projectileImage;
    private Random random = new Random();

    public Enemy(Vector2D position) {
        this.position = position;
        this.projectileFactory = new EnemyProjectileFactory();
        this.enemyProjectile = new ArrayList<>();
        this.pendingToDeleteEnemyProjectile = new ArrayList<>();
    }

    @Override
    public void start() {}

    @Override
    public void update(GameEngine engine) {
        if(enemyProjectile.size()<3){
            if(this.isAlive() &&  random.nextInt(120)==20){
                Projectile p = projectileFactory.createProjectile(new Vector2D(position.getX() + this.image.getWidth() / 2, position.getY() + image.getHeight() + 2),projectileStrategy, projectileImage);
                enemyProjectile.add(p);
                engine.getPendingToAddGameObject().add(p);
                engine.getPendingToAddRenderable().add(p);
            }
        }else{
            pendingToDeleteEnemyProjectile.clear();
            for(Projectile p : enemyProjectile){
                if(!p.isAlive()){
                    engine.getPendingToRemoveGameObject().add(p);
                    engine.getPendingToRemoveRenderable().add(p);
                    pendingToDeleteEnemyProjectile.add(p);
                }
            }

            for(Projectile p: pendingToDeleteEnemyProjectile){
                enemyProjectile.remove(p);
            }
        }

        if(this.position.getX()<=this.image.getWidth() || this.position.getX()>=(engine.getGameWidth()-this.image.getWidth()-1)){
            this.position.setY(this.position.getY()+25);
            //System.out.println("Going back");
            xVel*=-1;
        }

        this.position.setX(this.position.getX() + xVel);

        if((this.position.getY()+this.image.getHeight())>=engine.getPlayer().getPosition().getY()){
            engine.getPlayer().takeDamage(Integer.MAX_VALUE);
        }

        /*
        Logic TBD
         */

    }

    @Override
    public Image getImage() {
        return this.image;
    }

    @Override
    public double getWidth() {
        return this.image.getWidth();
    }
    public void setxVel(int xVel){
        this.xVel = xVel;
    }

    @Override
    public double getHeight() {
       return this.image.getHeight();
    }

    @Override
    public Vector2D getPosition() {
        return this.position;
    }

    @Override
    public Layer getLayer() {
        return Layer.FOREGROUND;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setProjectileImage(Image projectileImage) {
        this.projectileImage = projectileImage;
    }

    @Override
    public void takeDamage(double amount) {
        this.lives-=1;
    }

    @Override
    public double getHealth() {
        return this.lives;
    }

    @Override
    public String getRenderableObjectName() {
        return "Enemy";
    }
    public void setProjectileFactory(ProjectileFactory projectileFactory){
        this.projectileFactory = projectileFactory;
    }
    public void setRandom(Random random){this.random = random;}
    public void setEnemyProjectile(ArrayList<Projectile> enemyProjectile){
        this.enemyProjectile = enemyProjectile;
    }
    public void setPendingToDeleteEnemyProjectile(ArrayList<Projectile> pendingToDeleteEnemyProjectile){
        this.pendingToDeleteEnemyProjectile = pendingToDeleteEnemyProjectile;
    }
    @Override
    public Renderable copyR() {
        ArrayList<Projectile> enemyProjectile = new ArrayList<>();
        ArrayList<Projectile> pendingToDeleteEnemyProjectile = new ArrayList<>();

        for(Projectile projectile: this.enemyProjectile){
            enemyProjectile.add((Projectile) projectile.copyR());
        }
        for(Projectile projectile: this.pendingToDeleteEnemyProjectile){
            pendingToDeleteEnemyProjectile.add((Projectile) projectile.copyR());
        }
        Enemy enemy = new Enemy(new Vector2D(this.position.getX(),this.position.getY()));
        int xvel = this.xVel;
        int lives1 = this.lives;

        enemy.setLives(lives1);
        enemy.setxVel(xVel);
        enemy.setProjectileFactory(new EnemyProjectileFactory());

        //enemy.setEnemyProjectile(enemyProjectile);
        //enemy.setPendingToDeleteEnemyProjectile(pendingToDeleteEnemyProjectile);
        //enemy.setRandom(new Random());

        if(this.projectileStrategy instanceof SlowProjectileStrategy){
            enemy.setProjectileStrategy(new SlowProjectileStrategy());
            enemy.setImage(new Image(new File("src/main/resources/slow_alien.png").toURI().toString(), 20, 20, true, true));
            enemy.setProjectileImage(new Image(new File("src/main/resources/alien_shot_slow.png").toURI().toString(), 10, 10, true, true));
        }else{
            enemy.setProjectileStrategy(new FastProjectileStrategy());
            enemy.setImage(new Image(new File("src/main/resources/fast_alien.png").toURI().toString(), 20, 20, true, true));
            enemy.setProjectileImage(new Image(new File("src/main/resources/alien_shot_fast.png").toURI().toString(), 10, 10, true, true));
        }

        return enemy;

    }

    @Override
    public boolean isAlive() {
        return this.lives>0;
    }

    public void setProjectileStrategy(ProjectileStrategy projectileStrategy) {
        this.projectileStrategy = projectileStrategy;
    }

    public invaders.strategy.ProjectileStrategy getProjectileStrategy() {
        return projectileStrategy;
    }



}
