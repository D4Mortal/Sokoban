package project2;

/**
 * Class handling mage movements and behaviors
 * @author dhao1
 *
 */
public class Mage extends Character {
	
	public Mage(float x, float y) {
		super("res/mage.png", x, y);
	}
	
	/**
	 * Take player position and move accordingly
	 * @param dir direction player moved in
	 * @param playerX player's X position
	 * @param playerY palyer's Y position
	 */
	@Override		
	public void update(int dir, float playerX, float playerY) {
		
		boolean canMove = true;
		float distanceX = playerX - super.getx() ;
		float distanceY = playerY - super.gety();
		
		if (Float.compare(Math.abs(distanceX), Math.abs(distanceY)) > 0) {
			if (distanceX < 0) {
				if (!canMoveToDest(World.LEFT, super.getx(), super.gety())) {
					canMove = false;
				}
				moveAccordingly(World.LEFT, dir);
			}
			else {
				if (!canMoveToDest(World.RIGHT, super.getx(), super.gety())) {
					canMove = false;
				}
				moveAccordingly(World.RIGHT, dir);
			}
		}
		
		else if (!canMove || Float.compare(Math.abs(distanceX), Math.abs(distanceY)) <= 0){
			if (distanceY < 0) {
				moveAccordingly(World.UP, dir);
			}
			else {
				moveAccordingly(World.DOWN, dir);
			}
		}
	}
	
	/**
	 * get's player position and calculates the direction it needs to move in
	 * 
	 * @param dir direction player moved in
	 * @param playerX player's X position
	 * @param playerY palyer's Y position
	 * @return integer that represents the direction the mage will move in
	 */
	public int getDir(int dir, float playerX, float playerY) {
		// takes player position and moves accordingly
		boolean canMove = true;
		float distanceX = playerX - super.getx() ;
		float distanceY = playerY - super.gety();
		
		if (Float.compare(Math.abs(distanceX), Math.abs(distanceY)) > 0) {
			if (distanceX < 0) {
				if (!canMoveToDest(World.LEFT, super.getx(), super.gety())) {
					canMove = false;
				}
				else{
					return World.LEFT;
				}
			}
			else {
				if (!canMoveToDest(World.RIGHT, super.getx(), super.gety())) {
					canMove = false;
				}
				else{
					return World.RIGHT;
				}
			}
		}	
		else if (!canMove || Float.compare(Math.abs(distanceX), Math.abs(distanceY)) <= 0){
			if (distanceY < 0) {
				return World.UP;
			}
			else {
				return World.DOWN;
			}
		}
		return World.INVALID;
	}

}
