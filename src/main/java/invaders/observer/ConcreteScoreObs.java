package invaders.observer;

import invaders.engine.GameEngine;
import invaders.factory.EnemyProjectile;
import invaders.factory.PlayerProjectile;
import invaders.factory.Projectile;
import invaders.gameobject.Enemy;
import invaders.rendering.Renderable;
import invaders.strategy.FastProjectileStrategy;
import invaders.strategy.SlowProjectileStrategy;

public class ConcreteScoreObs implements Observer {
    int totalScore = 0;
    int score_slowProjectile = 1;
    int score_fastProjectile = 2;
    int score_slowAlien = 3;
    int score_fastAlien = 4;

    Renderable entityHit;

    public ConcreteScoreObs(int initScore) {
        this.totalScore = initScore;

    }
    public void setTotalScore(int score){this.totalScore = score;}

    public int getTotalScore() {
        return totalScore;
    }
    @Override
    public String toString(){
        return "" + totalScore;
    }
    public void checking(Renderable entityHit){
        if(entityHit == null) return;
        this.entityHit = entityHit;
    }

    @Override
    public void update() {
        if(entityHit == null){
            return;
        }else if(entityHit.getRenderableObjectName().equals("Enemy")){
            if(((Enemy) entityHit).getProjectileStrategy() instanceof SlowProjectileStrategy){
                totalScore += score_slowAlien;
            }else {
                totalScore += score_fastAlien;
            }
        }else if(entityHit.getRenderableObjectName().equals("EnemyProjectile")){
            if(((EnemyProjectile) entityHit).getStrategy() instanceof  SlowProjectileStrategy){
                totalScore += score_slowProjectile;
                //System.out.println("hit slow projectile");
            }else {
                totalScore += score_fastProjectile;
                //System.out.println("hit fast projectile");
            }
        }
        entityHit = null;
    }
}
