package project2;

/**
 * Interface for undoing moves
 * @author dhao1
 *
 */
public interface Undo {
	
	public void undo();
	public void recordMoves(float positionX, float positionY);
}
