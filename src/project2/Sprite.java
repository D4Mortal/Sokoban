package project2;

import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Graphics;

/**
 * Parent class of all sprites, containing shared methods such as update and moving etc
 * built based on the sample project 1 provided by the All-seeing and All-knowing Eleanor
 * @author dhao1
 *
 */

public class Sprite {

	private Image image = null;
	private float x;
	private float y;
	
	public Sprite(String image_src, float x, float y) {
		try {
			image = new Image(image_src);
		} catch (SlickException e) {
			e.printStackTrace();
		}
		
		this.x = x;
		this.y = y;
		snapToGrid();
	}
	
	//Overloading update method, as different characters may need different parameters
	public void update(Input input, int delta) {
		
	}
	public void update(int dir) {
		
	}
	public void update(int dir, float playerX, float playerY) {
		
	}
	
	public void render(Graphics g) {
		image.drawCentered(x, y);	
	}
	
	/**
	 * Forces this sprite to align to the grid
	 */
	public void snapToGrid() {
		x /= App.TILE_SIZE;
		y /= App.TILE_SIZE;
		x = Math.round(x);
		y = Math.round(y);
		x *= App.TILE_SIZE;
		y *= App.TILE_SIZE;
	}
	
	/**
	 * move to to destination if it's not blocked
	 * @param dir direction attempting to move in
	 */
	public void moveToDest(int dir) {
		// Translate the direction to an x and y displacement
		float delta_x = 0,
			  delta_y = 0;
		switch (dir) {
			case World.LEFT:
				delta_x = -World.TILE;
				break;
			case World.RIGHT:
				delta_x = World.TILE;
				break;
			case World.UP:
				delta_y = -World.TILE;
				break;
			case World.DOWN:
				delta_y = World.TILE;
				break;
		}
		
		// Make sure the position isn't occupied!
		if (!Loader.isBlocked(x + delta_x, y + delta_y)) {
			x += delta_x;
			y += delta_y;
		}
	}
	
	/**
	 * move relying on the direction input, so it's not moving whenever update is called but when there's a new direction provided
	 * used mainly for characters that move in direction different to player, e.g skeleton, rogue
	 * @param wantedDir intended direction to move in
	 * @param inputDir	input direction from user
	 */
	public void moveAccordingly(int wantedDir, int inputDir) {	
	
		
		if (wantedDir == World.LEFT) {
			if (inputDir == World.RIGHT) {
				moveToDest(inputDir-1);
			}
			else if (inputDir == World.UP) {
				moveToDest(inputDir-2);
			}
			else if (inputDir == World.DOWN) {
				moveToDest(inputDir-3);
			}
			else {
				moveToDest(inputDir);
			}
		}
		
		if (wantedDir == World.RIGHT) {
			if (inputDir == World.LEFT) {
				moveToDest(inputDir + 1);
			}
			else if (inputDir == World.UP) {
				moveToDest(inputDir - 1);
			}
			else if (inputDir == World.DOWN) {
				moveToDest(inputDir - 2);
			}
			else {
				moveToDest(inputDir);
			}
		}
		
		if (wantedDir == World.UP) {
			if (inputDir == World.LEFT) {
				moveToDest(inputDir + 2);
			}
			else if (inputDir == World.RIGHT) {
				moveToDest(inputDir + 1);
			}
			else if (inputDir == World.DOWN) {
				moveToDest(inputDir - 1);
			}
			else {
				moveToDest(inputDir);
			}
		}
		
		if (wantedDir == World.DOWN) {
			if (inputDir == World.LEFT) {
				moveToDest(inputDir + 3);
			}
			else if (inputDir == World.RIGHT) {
				moveToDest(inputDir + 2);
			}
			else if (inputDir == World.UP) {
				moveToDest(inputDir + 1);
			}
			else {
				moveToDest(inputDir);
			}
		}
		
	}
	
	/**
	 * check if sprite can move in desired direction
	 * 
	 * @param dir intended direction
	 * @param x	current X position
	 * @param y current Y position
	 * @return return true if destination is not blocked, else false
	 */
	public boolean canMoveToDest(int dir, float x, float y) {
		float speed = 32;

		if (dir == World.LEFT) {
			return !Loader.isBlocked(x - speed, y);
			
		}
		else if (dir == World.RIGHT) {
			return !Loader.isBlocked(x + speed, y);
		}
		else if (dir == World.UP) {
			return !Loader.isBlocked(x, y - speed);
		}
		else if (dir == World.DOWN){
			return !Loader.isBlocked(x, y + speed);
		}
		return true;
	}
	
	
	public float getx() {
		return this.x;
	}
	public float gety() {
		return this.y;
	}
	public void setX(float value) {
		this.x = value;
	}
	public void setY(float value) {
		this.y = value;
	}
	
}
