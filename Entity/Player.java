package Entity;

import TileMap.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends MapObject {

	// player info
	private int health;
	private int maxHealth;
	private int attackSwing;
	private int fullAttackSwing;
	private boolean dead;
	private boolean flinching;
	private long flinchTimer;

	private boolean jumping;
	private boolean falling;
	private boolean gliding = false;

	// attack
	private boolean attacking;
	private int attackCost;
	private int attackDamage;
	private int attackRange;
	// To implement more attacks
	// private ArrayList<Attacks> attacks;

	// animations
	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = { 6, 3, 5, 1 };

	// animation actions
	private static final int IDLE = 0;
	private static final int RUNNING = 1;
	private static final int ATTACKING = 2;
	private static final int JUMPING_FALLING = 3;

	public Player(TileMap tm) {
		super(tm);

		width = 30;
		height = 30;
		cwidth = 20;
		cheight = 20;

		// test new movement speeds
		moveSpeed = 0.3;
		maxSpeed = 1.6;
		stopSpeed = 0.4;
		fallSpeed = .15;
		maxFallSpeed = 4.0;
		jumpStart = -4.8;
		stopJumpSpeed = 0.3;

		facingRight = true;

		health = maxHealth = 5;

		attackSwing = fullAttackSwing = 1000;

		attackCost = 100;
		attackDamage = 5;
		// attacks = new ArrayList<Attacks>();

		attackRange = 40;

		// load sprites
		try {
			System.out.println("Loading Player Sprites...");

			File loadThis = new File(
					"src/res/Sprites/Player/Chef_Wasabi_Spritesheet1.png");
			String path = loadThis.getAbsolutePath();
			BufferedImage spritesheet = ImageIO.read(new FileInputStream(path));

			sprites = new ArrayList<BufferedImage[]>();

			/*
			 * to add new animations look here to add new animations look here
			 * to add new animations look here to add new animations look here
			 * 
			 * replace 5
			 * 
			 * 0 = IDLE, 1 = RUNNING, 2 = ATTACKING, 3 = JUMPING/FALLING
			 */

			for (int i = 0; i < 4; i++) {
				BufferedImage[] bi = new BufferedImage[numFrames[i]];
				if (i == 0 || i == 1) {// IDLE and RUNNING
					for (int j = 0; j < numFrames[i]; j++) {
						bi[j] = spritesheet.getSubimage(j * width, i * height,
								width, height);
					}
				} else if (i == 2) {// ATTACKING
					for (int j = 0; j < numFrames[i]; j++) {
						bi[j] = spritesheet.getSubimage(j * width * 2, i
								* height, width * 2, height);

					}
				} else {// JUMPING_FALLING
					for (int j = 0; j < numFrames[i]; j++) {
						bi[j] = spritesheet.getSubimage(j * width,
								(i * height), width, height + 7);
					}
				}
				sprites.add(bi);
			}

			System.out.println("Player Sprites successfully loaded!");
		} catch (Exception e) {
			System.out.println("Failed to Load Player Sprites");
			e.printStackTrace();
		}

		animation = new Animation();
		currentAction = IDLE;
		animation.setFrames(sprites.get(IDLE));
		animation.setDelay(100);
	}

	public int getHealth() {
		return health;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public int getAttack() {
		return attackSwing;
	}

	public int getMaxAttackSwing() {
		return fullAttackSwing;
	}

	public void setAttacking() {
		attacking = true;
	}

	public void setJumping(boolean b) {
		jumping = b;
		falling = !b;
	}

	private void getNextPosition() {

		// movement
		if (left) {
			dx -= moveSpeed;
			if (dx < -maxSpeed) {
				dx = -maxSpeed;
			}
		} else if (right) {
			dx += moveSpeed;
			if (dx > maxSpeed) {
				dx = maxSpeed;
			}
		} else {
			if (dx > 0) {
				dx -= stopSpeed;
				if (dx < 0) {
					dx = 0;
				}
			} else if (dx < 0) {
				dx += stopSpeed;
				if (dx > 0) {
					dx = 0;
				}
			}
		}

		// cannot move while attacking unless in air
		if ((currentAction == ATTACKING) && !(jumping || falling)) {
			dx = 0;
		}

		// jumping
		if (jumping && !falling) {
			dy = jumpStart;
			falling = true;
		}

		// falling
		if (falling) {

			if (dy > 0 && gliding)
				dy += fallSpeed * .1;
			else
				dy += fallSpeed;

			if (dy > 0)
				jumping = false;
			if (dy < 0 && !jumping)
				dy += stopJumpSpeed;

			if (dy > maxFallSpeed)
				dy = maxFallSpeed;
		}

	}

	public void update() {

		// update position
		getNextPosition();
		checkTileMapCollision();
		setPosition(xtemp, ytemp);

		// set animations
		if (attacking) {
			if (currentAction != ATTACKING) {
				currentAction = ATTACKING;
				animation.setFrames(sprites.get(ATTACKING));
				animation.setDelay(50);
				width = 60;

			}
			if (animation.hasPlayedOnce()) {
				attacking = false;
			}

		} else if (dy < 0 || dy > 0) {
			if (currentAction != JUMPING_FALLING) {
				currentAction = JUMPING_FALLING;
				animation.setFrames(sprites.get(JUMPING_FALLING));
				animation.setDelay(100);
				width = 30;
			}
			if (!(animation.hasPlayedOnce())) {
				jumping = true;
			}
		} else if (left || right) {
			if (currentAction != RUNNING) {
				currentAction = RUNNING;
				animation.setFrames(sprites.get(RUNNING));
				animation.setDelay(40);
				width = 30;
			}
		} else if (dy == 0) {
			if (currentAction != IDLE) {
				currentAction = IDLE;
				animation.setFrames(sprites.get(IDLE));
				animation.setDelay(100);
				width = 30;
			}
		}
	
		animation.update();

		// set direction
		if (currentAction != ATTACKING) {
			if (right)
				facingRight = true;
			if (left)
				facingRight = false;
		}
	}

	public void draw(Graphics2D g) {

		setMapPositon();

		// draw player
		if (flinching) {
			long elapsed = (System.nanoTime() - flinchTimer) / 1000000;
			if (elapsed / 100 % 2 == 0)
				return;
		}

		if (facingRight) {
			g.drawImage(animation.getImage(), (int) (x + xmap - width / 2),
					(int) (y + ymap - height / 2), null);
		} else {
			
			if(jumping || falling)
				g.drawImage(animation.getImage(),
					(int) (x + xmap - width / 2 + width),
					(int) (y + ymap - height / 2), -width, height + 7, null);
			else
				g.drawImage(animation.getImage(),
						(int) (x + xmap - width / 2 + width),
						(int) (y + ymap - height / 2), -width, height, null);
		}
	}

}
