package project2;

/**
 * Class handling skeleton characters
 * @author Admin
 *
 */
public class Skeleton extends Character {
	private int direction = World.UP;
	private boolean up = true;
	private int timer = 0;
	public Skeleton(float x, float y) {
		super("res/skull.png", x, y);
	}
	
	@Override		
	public void update(int dir) {
		moveAccordingly(this.direction, dir);
		if (!canMoveToDest(this.direction, super.getx(), super.gety())) {	
			changeDir();	
		}
		this.timer = 0;
	}

	public void changeDir() {
		this.up = !this.up;
		if (this.up) {
			this.direction = World.UP;
		}
		else {
			this.direction = World.DOWN;
		}
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
