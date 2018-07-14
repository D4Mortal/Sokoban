package project2;


/**
 * Class handling rogue characters
 * @author dhao1
 *
 */
public class Rogue extends Character {
	
	private boolean left = true;
	public int direction = World.LEFT;

	public Rogue(float x, float y) {
		super("res/rogue.png", x, y);
	}
	
	@Override		
	public void update(int dir) {
		moveAccordingly(this.direction, dir);
		if (!canMoveToDest(this.direction, super.getx(), super.gety())) {		
			changeDir();	
		}	
	}

	public void changeDir() {
		this.left = !this.left;
		if (this.left) {
			this.direction = World.LEFT;
		}
		else {
			this.direction = World.RIGHT;
		}
	}
	
	public int getDir() {
		return this.direction;
	}
}
