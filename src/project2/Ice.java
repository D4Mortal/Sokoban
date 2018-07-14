package project2;

import java.util.ArrayList;

/**
 * Class for handling how ice interacts
 * @author dhao1
 *
 */
public class Ice extends Block implements Undo{
	
	// boolean flag to indicate if the ice is moving
	private boolean isMoving = false;
	
	// record the direction is ice needs to slide in
	private int direction;
	
	// arraylist to hold past positons of the ice
	private ArrayList<float[]> history = new ArrayList<float[]>(); 
	
	private int timer = 0;
	public Ice(float x, float y) {
		super("res/ice.png", x, y);
	}
	
	/**
	 * move one tile when it's being pushed, after that use another function to update movement
	 * @param dir direction the sprite is being pushed in
	 */
	public void update(int dir) {
		this.direction = dir;
		
		moveToDest(this.direction);
		this.timer = 0;
	}
	
	/**
	 * Move the ice constantly in the direction it's being pushed
	 */
	public void sliding() {
		moveToDest(this.direction);
		if (!canMoveToDest(this.direction, super.getx(), super.gety())) {
			this.isMoving = false;
		}
		this.timer = 0;
	}
	
	/**
	 * set the position of the ice to where it was last stopped
	 */
	public void undo() {
		if (history.size() > 0) {
			super.setX(history.get(history.size() - 1)[0]);
			super.setY(history.get(history.size() - 1)[1]);
			history.remove(this.history.size() - 1);
		}	
	}
	
	/**
	 * record the position of ice so it can be undone
	 */
	@Override
	public void recordMoves(float positionX, float positionY) {
		float[] location = new float[2]; 
		location[0] = positionX;
		location[1] = positionY;
		this.history.add(location);
	}
	
	
	public void startMoving() {
		this.isMoving = true;
	}
	

	public void stopMoving() {
		this.isMoving = false;
		this.timer = 0;
	}
	

	public boolean isIceMoving() {
		return isMoving;
	}
	

	public int getDir() {
		return this.direction;
	}
	
	public int getTime() {
		return this.timer;
	}
	
	public void timePassed(int delta) {
		this.timer += delta;
	}
}

