package project2;

import java.util.ArrayList;

/**
 * Class for handling TNT sprite
 * @author dhao1
 *
 */
public class TNT extends Block implements Undo{
	private ArrayList<float[]> history = new ArrayList<float[]>(); 
	public TNT(float x, float y) {
		super("res/tnt.png", x, y);
	}
	
	public void update(int dir) {
		// Move to our destination
		moveToDest(dir);
	}
	
	/**
	 * set position according to the information stored in the arraylist and remove the last item
	 */
	public void undo() {
		// set position according to the information stored in the arraylist and remove the last item
		if (history.size() > 0) {
			super.setX(history.get(history.size() - 1)[0]);
			super.setY(history.get(history.size() - 1)[1]);
			history.remove(this.history.size() - 1);
		}	
	}
	
	/**
	 * //record the moves into an array and add it to the arraylist
	 */
	@Override
	public void recordMoves(float positionX, float positionY) {
		//record the moves into an array and add it to the arraylist
		float[] location = new float[2]; 
		location[0] = positionX;
		location[1] = positionY;
		this.history.add(location);
	}
} 


