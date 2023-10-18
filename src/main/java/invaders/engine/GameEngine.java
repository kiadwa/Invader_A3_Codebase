package invaders.engine;

import java.util.ArrayList;
import java.util.List;

import invaders.ConfigReader;
import invaders.builder.BunkerBuilder;
import invaders.builder.Director;
import invaders.builder.EnemyBuilder;
import invaders.factory.EnemyProjectile;
import invaders.factory.PlayerProjectile;
import invaders.factory.Projectile;
import invaders.gameobject.Bunker;
import invaders.gameobject.Enemy;
import invaders.gameobject.GameObject;
import invaders.entities.Player;
import invaders.mementoUndo.Caretaker;
import invaders.mementoUndo.GameEngineMemento;
import invaders.mementoUndo.GameMemento;
import invaders.mementoUndo.Originator;
import invaders.rendering.Renderable;
import org.json.simple.JSONObject;

/**
 * This class manages the main loop and logic of the game
 */
public class GameEngine implements Originator {
	//TODO change this 2 to change difficulty
	private List<GameObject> gameObjects = new ArrayList<>(); // A list of game objects that gets updated each frame
	private List<Renderable> renderables =  new ArrayList<>();



	private List<GameObject> pendingToAddGameObject = new ArrayList<>();
	private List<GameObject> pendingToRemoveGameObject = new ArrayList<>();

	private List<Renderable> pendingToAddRenderable = new ArrayList<>();
	private List<Renderable> pendingToRemoveRenderable = new ArrayList<>();


	private Player player;

	private boolean left;
	private boolean right;
	private int gameWidth;
	private int gameHeight;
	private int timer = 45;
	private Caretaker caretaker;

	public GameEngine(String config){
		// Read the config here

		ConfigReader.parse(config);

		// Get game width and height
		gameWidth = ((Long)((JSONObject) ConfigReader.getGameInfo().get("size")).get("x")).intValue();
		gameHeight = ((Long)((JSONObject) ConfigReader.getGameInfo().get("size")).get("y")).intValue();

		//Get player info
		this.player = new Player(ConfigReader.getPlayerInfo());
		renderables.add(player);


		Director director = new Director();
		BunkerBuilder bunkerBuilder = new BunkerBuilder();
		//Get Bunkers info
		for(Object eachBunkerInfo:ConfigReader.getBunkersInfo()){
			Bunker bunker = director.constructBunker(bunkerBuilder, (JSONObject) eachBunkerInfo);
			gameObjects.add(bunker);
			renderables.add(bunker);
		}


		EnemyBuilder enemyBuilder = new EnemyBuilder();
		//Get Enemy info
		for(Object eachEnemyInfo:ConfigReader.getEnemiesInfo()){
			Enemy enemy = director.constructEnemy(this,enemyBuilder,(JSONObject)eachEnemyInfo);
			gameObjects.add(enemy);
			renderables.add(enemy);
		}

	}



	/**
	 * Updates the game/simulation
	 */
	public void update(){
		timer+=1;

		movePlayer();

		for(GameObject go: gameObjects){
			go.update(this);
		}

		for (int i = 0; i < renderables.size(); i++) {
			Renderable renderableA = renderables.get(i);
			for (int j = i+1; j < renderables.size(); j++) {
				Renderable renderableB = renderables.get(j);

				if((renderableA.getRenderableObjectName().equals("Enemy") && renderableB.getRenderableObjectName().equals("EnemyProjectile"))
						||(renderableA.getRenderableObjectName().equals("EnemyProjectile") && renderableB.getRenderableObjectName().equals("Enemy"))||
						(renderableA.getRenderableObjectName().equals("EnemyProjectile") && renderableB.getRenderableObjectName().equals("EnemyProjectile"))){
				}else{
					if(renderableA.isColliding(renderableB) && (renderableA.getHealth()>0 && renderableB.getHealth()>0)) {
						renderableA.takeDamage(1);
						renderableB.takeDamage(1);
					}
				}
			}
		}


		// ensure that renderable foreground objects don't go off-screen
		int offset = 1;
		for(Renderable ro: renderables){
			if(!ro.getLayer().equals(Renderable.Layer.FOREGROUND)){
				continue;
			}
			if(ro.getPosition().getX() + ro.getWidth() >= gameWidth) {
				ro.getPosition().setX((gameWidth - offset) -ro.getWidth());
			}

			if(ro.getPosition().getX() <= 0) {
				ro.getPosition().setX(offset);
			}

			if(ro.getPosition().getY() + ro.getHeight() >= gameHeight) {
				ro.getPosition().setY((gameHeight - offset) -ro.getHeight());
			}

			if(ro.getPosition().getY() <= 0) {
				ro.getPosition().setY(offset);
			}
		}

	}
	public void setCaretaker(Caretaker caretaker){
		this.caretaker = caretaker;
	}
	public Caretaker getCaretaker(){
		return this.caretaker;
	}


	public List<Renderable> getRenderables(){
		return renderables;
	}

	public List<GameObject> getGameObjects() {
		return gameObjects;
	}
	public List<GameObject> getPendingToAddGameObject() {
		return pendingToAddGameObject;
	}

	public List<GameObject> getPendingToRemoveGameObject() {
		return pendingToRemoveGameObject;
	}

	public List<Renderable> getPendingToAddRenderable() {
		return pendingToAddRenderable;
	}

	public List<Renderable> getPendingToRemoveRenderable() {
		return pendingToRemoveRenderable;
	}


	public void leftReleased() {
		this.left = false;
	}

	public void rightReleased(){
		this.right = false;
	}

	public void leftPressed() {
		this.left = true;
	}
	public void rightPressed(){
		this.right = true;
	}

	public boolean shootPressed(){
		if(timer>45 && player.isAlive()){
			Projectile projectile = player.shoot();
			gameObjects.add(projectile);
			renderables.add(projectile);
			timer=0;
			return true;
		}
		return false;
	}

	private void movePlayer(){
		if(left){
			player.left();
		}

		if(right){
			player.right();
		}
	}

	public int getGameWidth() {
		return gameWidth;
	}

	public int getGameHeight() {
		return gameHeight;
	}

	public Player getPlayer() {
		return player;
	}

	@Override
	public GameEngineMemento save() {
		GameMemento gameMemento = new GameMemento();
		//saving deep copy of renderables
		for(Renderable renderable: this.renderables){
			if(renderable.getRenderableObjectName().equals("Player")){
				Player player = ((Player) renderable).copy();
				gameMemento.getGameRenderablesState().add(player);
			}else if(renderable.getRenderableObjectName().equals("Bunker")){
				Bunker bunker = ((Bunker) renderable).copy();
				gameMemento.getGameRenderablesState().add(bunker);
			}else if(renderable.getRenderableObjectName().equals("Enemy")){
				Enemy enemy = ((Enemy) renderable).copy();
				gameMemento.getGameRenderablesState().add(enemy);
			}else if(renderable.getRenderableObjectName().equals("EnemyProjectile")) {
				EnemyProjectile projectile = ((EnemyProjectile) renderable).copy();
				gameMemento.getGameRenderablesState().add(projectile);
			}
		}
		//saving deep copy of GameObjects from the Memento's Renderables list;
		for(Renderable renderable: gameMemento.getGameRenderablesState()){
			if(renderable.getRenderableObjectName().equals("Bunker")){
				gameMemento.getGameObjectsState().add((GameObject) renderable);
			}else if(renderable.getRenderableObjectName().equals("Enemy")){
				gameMemento.getGameObjectsState().add((GameObject) renderable);
			}else if(renderable.getRenderableObjectName().equals("EnemyProjectile")){
				gameMemento.getGameObjectsState().add((GameObject) renderable);
			}
		}
		//saving deep copy of pending to add renderables
		for(Renderable renderable: this.pendingToAddRenderable){
			if(renderable.getRenderableObjectName().equals("Player")){
				Player player = ((Player) renderable).copy();
				gameMemento.getPendingToAddRenderables().add(player);
			}else if(renderable.getRenderableObjectName().equals("Bunker")){
				Bunker bunker = ((Bunker) renderable).copy();
				gameMemento.getPendingToAddRenderables().add(bunker);
			}else if(renderable.getRenderableObjectName().equals("Enemy")){
				Enemy enemy = ((Enemy) renderable).copy();
				gameMemento.getPendingToAddRenderables().add(enemy);
			}else if(renderable.getRenderableObjectName().equals("EnemyProjectile")) {
				EnemyProjectile projectile = ((EnemyProjectile) renderable).copy();
				gameMemento.getPendingToAddRenderables().add(projectile);
			}
		}

		//saving deep copy of pending to add renderables
		for(Renderable renderable: this.pendingToRemoveRenderable){
			if(renderable.getRenderableObjectName().equals("Player")){
				Player player = ((Player) renderable).copy();
				gameMemento.getPendingToRemoveRenderables().add(player);
			}else if(renderable.getRenderableObjectName().equals("Bunker")){
				Bunker bunker = ((Bunker) renderable).copy();
				gameMemento.getPendingToRemoveRenderables().add(bunker);
			}else if(renderable.getRenderableObjectName().equals("Enemy")) {
				Enemy enemy = ((Enemy) renderable).copy();
				gameMemento.getPendingToRemoveRenderables().add(enemy);
			}else if(renderable.getRenderableObjectName().equals("EnemyProjectile")) {
				EnemyProjectile projectile = ((EnemyProjectile) renderable).copy();
				gameMemento.getPendingToRemoveRenderables().add(projectile);
			}
		}
		//saving deep a copy of pending to add game objects
		for(Renderable renderable: gameMemento.getPendingToAddRenderables()) {
			if(renderable.getRenderableObjectName().equals("Bunker")){
				gameMemento.getPendingToAddGameObjects().add((GameObject) renderable);
			}else if(renderable.getRenderableObjectName().equals("Enemy")){
				gameMemento.getPendingToAddGameObjects().add((GameObject) renderable);
			}else if(renderable.getRenderableObjectName().equals("EnemyProjectile")){
				gameMemento.getPendingToAddGameObjects().add((GameObject) renderable);
			}
		}




		//saving deep copy of pending to remove game objects
		for(Renderable renderable: gameMemento.getPendingToRemoveRenderables()){
			if(renderable.getRenderableObjectName().equals("Bunker")){
				gameMemento.getPendingToRemoveGameObjects().add((GameObject) renderable);
			}else if(renderable.getRenderableObjectName().equals("Enemy")){
				gameMemento.getPendingToRemoveGameObjects().add((GameObject) renderable);
			}else if(renderable.getRenderableObjectName().equals("EnemyProjectile")){
				gameMemento.getPendingToRemoveGameObjects().add((GameObject) renderable);
			}
		}

		//TODO saving Observer



		return gameMemento;
	}

	@Override
	public void restore(GameEngineMemento memento) {
		renderables.clear();
		gameObjects.clear();
		pendingToRemoveRenderable.clear();
		pendingToAddRenderable.clear();
		pendingToAddGameObject.clear();
		pendingToRemoveGameObject.clear();



		renderables.addAll(memento.getGameRenderablesState());
		gameObjects.addAll(memento.getGameObjectsState());
		pendingToRemoveRenderable.addAll(memento.getPendingToRemoveRenderables());
		pendingToAddRenderable.addAll(memento.getPendingToAddRenderables());
		pendingToAddGameObject.addAll(memento.getPendingToAddGameObjects());
		pendingToRemoveGameObject.addAll(memento.getPendingToRemoveGameObjects());

	}
}
