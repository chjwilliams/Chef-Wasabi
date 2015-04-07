package GameState;


import Entity.Player;
import Main.GamePanel;
import TileMap.*;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public class Level1State extends GameState {
	
	private TileMap tileMap;
	
	private Background bg;
	
	private Player player;
	
	public Level1State(GameStateManager gsm){
		this.gsm = gsm;
		init();
		
	}


	public void init() {
		tileMap = new TileMap(30);
		tileMap.loadTiles("src/res/Tilesets/Level_1_Tiles.png");
		tileMap.loadMap("src/res/Maps/level1-1.txt");
		tileMap.setPosition(0 ,0);
		//Maps/level1-1.map
		
		bg = new Background("src/res/Backgrounds/Level_1_Background.png", 1);
		
		player = new Player(tileMap);
		player.setPosition(100, 100);
	}


	public void update() {
		
		//update player
		player.update();
		tileMap.setPosition(GamePanel.WIDTH / 2 - player.getX(), 
				GamePanel.HEIGHT / 2 - player.getY());
	}


	public void draw(Graphics2D g) {
		//Draws background
		bg.draw(g);
		
		//draw tile map
		tileMap.draw(g);
		
		//draws player
		player.draw(g);
	}

	public void keyPressed(int k) {
		
		if(k == KeyEvent.VK_LEFT){
			player.setLeft(true);
		}
		if(k == KeyEvent.VK_RIGHT){
			player.setRight(true);
		}
		if(k == KeyEvent.VK_UP){
			player.setUp(true);
		}
		if(k == KeyEvent.VK_DOWN){
			player.setDown(true);
		}
		if(k == KeyEvent.VK_W){
			player.setJumping(true);
		}
		if(k == KeyEvent.VK_F){
			player.setAttacking();
		}
		
	}

	public void keyReleased(int k) {
		if(k == KeyEvent.VK_LEFT)
			player.setLeft(false);
		if(k == KeyEvent.VK_RIGHT)
			player.setRight(false);
		if(k == KeyEvent.VK_UP)
			player.setUp(false);
		if(k == KeyEvent.VK_DOWN)
			player.setDown(false);
		if(k == KeyEvent.VK_W)
			player.setJumping(false);
		
	}
}
