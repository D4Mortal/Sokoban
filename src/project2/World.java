package project2;

import java.util.ArrayList;
import java.util.Iterator;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
/**
 * Game world handling the state of the game
 * @author dhao1
 *
 */
public class World {
	
	public static final int RESTART = 0;
	public static final int LEFT = 1;
	public static final int RIGHT = 2;
	public static final int UP = 3;
	public static final int DOWN = 4;
	public static final int UNDO = 5;
	public static final int PREV = 99;
	public static final int NEXT = 100;
	public static final double EPSILON = 0.01;
	public static final int TILE = 32;
	public static final int INVALID = 10;
	
	private ArrayList<Sprite> sprites;
	private int direction;
	
	// Ice's position when it last stopped
	private float iceX;								
	private float iceY;
	
	//Player's position before update
	private float oldX;								
	private float oldY;

	//Rogue's position before update
	private float rogueX;							
	private float rogueY;
	
	//Skeleton's position before update
	private float skeletonX;						
	private float skeletonY;
	
	//Mages's position before update
	private float mageX;							
	private float mageY;
	
	//counter for the number time update is being called, and the current level
	private int currentLvl = 0;						
								
	
	// boolean value for cracked wall and door
	private boolean needRemove = false;				
	private boolean doorOpened = true;				
	
	//position of explosion
	private float explodeX;							
	private float explodeY;
	private boolean explosion = false;
	
	//timer to keep track how long the explosion needs to be
	private int timer;								
	
	public World() {
		sprites = Loader.loadSprites(currentLvl);
		
	}

	public void update(Input input, int delta) {

		timer += delta;
		
		this.direction = getDir(input);
		
		//press R to restart level
		if (this.direction == RESTART) {											
			loadLevel(currentLvl);
		}
		
		//press P to return to previous level
		if (this.direction == PREV && currentLvl > 0) {						
			currentLvl--;
			loadLevel(currentLvl);	
		}
		
		// Press N to go to next level
		if (this.direction == NEXT && currentLvl < 5) {						
			currentLvl++;
			loadLevel(currentLvl);		
		}
		
		// press Z to undo
		if (this.direction == UNDO) {								
			undo(sprites);
		}
		
		
		for (Sprite sprite : sprites) {									
			
			//find the player sprite and record player position before update
			if (sprite != null && sprite instanceof Player) {							
				this.oldX = sprite.getx();												
				this.oldY = sprite.gety();
				
				//check if is a block in the direction of user input
				if (isBlockAhead(sprites, this.direction, sprite)) {					
					for (Sprite sprite2 : sprites) {	
						if (sprite2 != null && (sprite2 instanceof Stone || sprite2 instanceof TNT)) {		
							
							//check if the block is pushable
							if (canPush(sprites, direction, sprite, sprite2)){			
								sprite.update(this.direction);
								sprite2.update(this.direction);								
							}
							
							else if (sprite2 instanceof TNT) {		
								
								//check if the tnt collided into the cracked wall or not
								if (willExplode(this.sprites, this.direction, sprite2)) {
									needRemove = true;
									explosion = true;
								}
							}	
						}
						if (sprite2 != null && sprite2 instanceof Ice) {							
							if (canPush(sprites, direction, sprite, sprite2)){
								((Ice)sprite2).startMoving();
								sprite.update(this.direction);	
								sprite2.update(this.direction);
								
							}
						}
					}
				}
				
				// if there's no block ahead, simply try to move in the direction of input
				else{
					sprite.update(this.direction);	
				}				
			}
			
			// update the ice using a timer so it's "sliding"
			if (sprite != null && sprite instanceof Ice) {					
				((Ice)sprite).timePassed(delta);
				if (!((Ice)sprite).isIceMoving()) {
					this.iceX = sprite.getx();
					this.iceY = sprite.gety();
				}
				
				else if (((Ice)sprite).isIceMoving() && (((Ice)sprite).getTime() >= 250)) {
					((Ice)sprite).sliding();
					
				}
				
				if (isBlockAhead(sprites, ((Ice)sprite).getDir(), sprite)) {
					((Ice)sprite).stopMoving();
					
				}
			}
			
			
			
			if (sprite != null && sprite instanceof Rogue) {
				
				//record the rogue's position before moving
				this.rogueX = sprite.getx();
				this.rogueY = sprite.gety();	
				
				//check if there is a stone in the direction of rogues movement
				if (isBlockAhead(sprites, ((Rogue)sprite).getDir(), sprite)) {					

					for (Sprite sprite2 : sprites) {
						if (sprite2 != null && sprite2 instanceof Block) {							
							
							//check if the block can be pushed in rogue's moving direction
							if (canPush(sprites, ((Rogue)sprite).getDir(), sprite, sprite2)){			
																										 				
								((Rogue)sprite).update(this.direction);	
								
								//push the block in the direction the rogue is currently moving in
								sprite2.moveAccordingly(((Rogue)sprite).getDir(), direction);
											
							}

							if (findBlock(((Rogue)sprite).getDir(), sprite, sprite2)){											
								
								//if the rogue runs into a wall or a unpushable stone, change direction
								if (!sprite2.canMoveToDest(((Rogue)sprite).getDir(), sprite2.getx(), sprite2.gety())) {								
									((Rogue)sprite).changeDir();
								} 
								
								if (isBlockAhead(sprites, ((Rogue)sprite).getDir(), sprite2)) {
									((Rogue)sprite).changeDir();					
								} 
							}
						}
					}	
				}
				
				//if there's no blocks ahead, update normally 
				else {
					((Rogue)sprite).update(this.direction);						
				}	
			}
			
			//update the skeleton every 1000ms has passed
			if (sprite != null && sprite instanceof Skeleton) {	
				
				((Skeleton)sprite).timePassed(delta);
				
				if (((Skeleton)sprite).getTime() >= 1000) {
					
					this.skeletonX = sprite.getx();
					this.skeletonY = sprite.gety();	
					
					if (isBlockAhead(sprites, ((Skeleton)sprite).getDir(), sprite)) {			
						((Skeleton)sprite).changeDir();
					}
					
					// update using any direction, not effected, simply providing a reference
					else {
						sprite.update(LEFT);		
					}
				}
				
			}
			
			
			if (sprite != null && sprite instanceof Mage) {	
								
				mageX = sprite.getx();
				mageY = sprite.gety();
				
				// let the mage move only if there's no blocks in mage's way
				if (!isBlockAhead(sprites, ((Mage)sprite).getDir(this.direction, oldX, oldY), sprite)) {
					((Mage)sprite).update(direction, oldX, oldY);
				}

			}
		}
		
		//check for collision between player and other characters
		if (checkCollision(sprites, oldX, oldY, rogueX, rogueY, skeletonX, skeletonY, mageX, mageY)) {				
			needRemove = false;
			explosion = false;
			sprites = Loader.loadSprites(currentLvl);
		
		}	
		
		//check if the level is complete
		if (isLvlComplete(sprites) && currentLvl < 5) {													
			currentLvl++;
			loadLevel(currentLvl);
		}
		
		//check if the switch is pressed
		if (isSwitchCovered(sprites)) {																	
			doorOpened = true;
		}
		else {
			doorOpened = false;
		}
		
		//remove the cracked wall and TNT from arraylist
		if (needRemove) {																				
			for (Iterator<Sprite> iterator = sprites.iterator(); iterator.hasNext();) {
			    Sprite sprite = iterator.next();
			    if (sprite != null && (sprite instanceof TNT || sprite instanceof Cracked)) {
			        // Remove the current element from the iterator and the list, collect location of wall to render explosion
			    	if (sprite instanceof Cracked) {
				    	this.explodeX = sprite.getx();
				    	this.explodeY = sprite.gety();
			    	}
			    	iterator.remove();
			        Loader.removeCrack();
			        needRemove = false;     
			    }
			}
			sprites.add(new Explosion(this.explodeX, this.explodeY));
			timer = 0;
		}
		
		//remove explosion effect from arraylist after 400ms, provide a upper limit so it's not called too many times.
		if (timer >= 400 && explosion) {																				
			for (Iterator<Sprite> iterator = sprites.iterator(); iterator.hasNext();) {
			    Sprite sprite = iterator.next();
			    if (sprite != null && (sprite instanceof Explosion)) {
			        // Remove the current element from the iterator and the list.
			    	iterator.remove();
			        Loader.removeCrack();		        
			    }
			}
			explosion = false;
		}
		
	}
	

	public void render(Graphics g) {
		for (Sprite sprite : sprites) {
			//render everything but player and door
			if (sprite != null && !(sprite instanceof Player) && !(sprite instanceof Door)) {	
				sprite.render(g);
			}
			
			// render player separately so it can display movement count
			else if (sprite != null && sprite instanceof Player) {
				g.drawString("Moves : " + ((Player)sprite).countMoves(), 0, 0);				
				sprite.render(g);
			}
			
			// do not render door if the door is open, render if it's closed
			else if (sprite != null && doorOpened) {											
				Loader.openDoor();
			}
			else if (sprite != null && !doorOpened){											
				sprite.render(g);
				Loader.closeDoor();
			}
		}
	}
	
	
	/**
	 * loop through the arraylist and check if the switch is covered by a block
	 * 
	 * @param sprites Arraylist of sprites containing every sprite in the current level
	 * @return return true if the switch is covered if present, else false
	 */
	private boolean isSwitchCovered(ArrayList<Sprite> sprites) {
		 for (Sprite sprite : sprites) {										
				if (sprite != null && (sprite instanceof Switch)) {
					for (Sprite sprite2 : sprites) {										
						if (sprite2 != null && (sprite2 instanceof Block)) {
							if (isEqual(sprite.getx(), sprite2.getx()) && isEqual(sprite.gety(), sprite2.gety())) {
								return true;
							}
						}
					}
				}
			}
		 return false;
	 }
	
	
	/**
	 * loop through the arraylist and check if a specific target is covered by a block
	 * 
	 * @param sprites Arraylist of sprites containing every sprite in the current level
	 * @param target The specific target that needs to be check if it's covered by any blocks
	 * @return
	 */
	private boolean isTargetCovered(ArrayList<Sprite> sprites, Sprite target) {									
		for (Sprite sprite : sprites) {										
			if (sprite != null && (sprite instanceof Block)) {
				if (isEqual(sprite.getx(), target.getx()) && isEqual(sprite.gety(), target.gety()) ) {
					return true;
				}
			}
		}
		return false;
	}
	

		
		
	/**
	 * loop through the entire arraylist, use isCovered to check if all the targets are covered
	 * 
	 * @param sprites Arraylist of sprites containing every sprite in the current level
	 * @return	return true if all the targets are covered, else false
	 */
	private boolean isLvlComplete(ArrayList<Sprite> sprites) {
		for (Sprite sprite : sprites) {										
			if (sprite != null && sprite instanceof Target) {
				if (!isTargetCovered(sprites, sprite)) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Simple function to load a level for avoiding repetitive codes
	 * 
	 * @param lvl the level that needs to be loaded
	 */
	private void loadLevel(int lvl) {
		this.needRemove = false;
		this.explosion = false;
		this.sprites = Loader.loadSprites(currentLvl);
	}
	
	
	/**
	* Check if there's another character on the same tile as player 
	* Check if the player and another character swapped position, if they did, must've crossed each other, therefor collided
	*
	* @param prevX  the player's  X position before moving
	* @param prevY  the player's  Y position before moving
	* @param rX the rogue's X position before moving
	* @param rY the rogue's Y position before moving
	* @param sX the skeleton's X position before moving
	* @param sY the skeleton's Y position before moving
	* @param mX the mage's X position before moving
	* @param mY the mage's Y position before moving
	* @return return true if there are collisions otherwise false
	*/
	private boolean checkCollision(ArrayList<Sprite> sprites, float prevX, float prevY, float rX, float rY, float sX, float sY, float mX, float mY) {			
																														
		float playerX = 0;																								
		float playerY = 0;																								
		for (Sprite sprite : sprites) {										
			if (sprite != null && sprite instanceof Player) {
				playerX = sprite.getx();
				playerY = sprite.gety();
			}
		}
		for (Sprite sprite : sprites) {										
			if (sprite != null && sprite instanceof Character && !(sprite instanceof Player)) {
				
				//Check if there's another character on the same tile as player 
				if (isEqual(sprite.getx(), playerX) && isEqual(sprite.gety(), playerY)) {
					return true;
				}
				
				
				// check if any characters swapped position with player
				if (sprite instanceof Rogue) {
					if ((isEqual(sprite.getx(), prevX)  && isEqual(sprite.gety(), prevY) ) && 
						isEqual(playerX, rX)  && isEqual(playerY, rY)) {
						return true;
					}
				}
				
				if (sprite instanceof Skeleton) {
					if (isEqual(sprite.getx(), prevX)  && isEqual(sprite.gety(), prevY) && 
						isEqual(playerX, sX)  && isEqual(playerY, sY)) {
						return true;
					}
				}
				
				if (sprite instanceof Mage) {
					if (isEqual(sprite.getx(), prevX)  && isEqual(sprite.gety(), prevY) && 
						isEqual(playerX, mX) && isEqual(playerY, mY)) {
						return true;
					}
				}				
			}
		}
		return false;	
		
	}
	
	
	
	/**
	 * check if the current stone is the stone that's in front of the character
	 * 
	 * @param dir the direction the character is trying to move in
	 * @param character the character sprite that's trying to move
	 * @param block	a block to check if it is the one in the direction of the character is trying to move in
	 * @return true if it is the block in the direction of the character is trying to move in, else false
	 */
	private boolean findBlock(int dir, Sprite character, Sprite block ) {													
		if (dir == LEFT) { 
			if (isEqual(block.getx(),character.getx() - TILE) && isEqual(block.gety(), character.gety())) {	
					return true;	
			}
		}
		
		else if (dir == RIGHT) {
			if (isEqual(block.getx(),character.getx() + TILE) && isEqual(block.gety(), character.gety())) {
					return true;		
			}
		}
		
		else if (dir == UP) {
			if (isEqual(block.getx(),character.getx()) && isEqual(block.gety(), character.gety() - TILE)){
					return true;	
			}
		}
		
		else if (dir == DOWN) {
			if (isEqual(block.getx(),character.getx()) && isEqual(block.gety(), character.gety() + TILE)){
					return true;	
			}
		}
		return false;
	}
	
	
	/**
	 * check if a block can be pushed by checking walls and are there any stone in front of the stone being pushed (can only push one stone at a time)
	 * 
	 * @param S Arraylist of sprites containing every sprite in the current level
	 * @param dir the direction the character is trying to move in
	 * @param character the character sprite that's trying to move
	 * @param block the block is being pushed
	 * @return true if it can be pushed, else false
	 */
	private boolean canPush(ArrayList<Sprite> S, int dir, Sprite character, Sprite block){											
		if (dir == LEFT) {																													
			if (isEqual(block.getx(), character.getx() - TILE) && isEqual(block.gety(), character.gety())) {
				if (block.canMoveToDest(dir, block.getx(), block.gety()) && !isBlockAhead(S, dir, block) ) {
					return true;
				}
			}
		}
		
		else if (dir == RIGHT) {
			if (isEqual(block.getx(), character.getx() + TILE) && isEqual(block.gety(), character.gety())) {
				if (block.canMoveToDest(dir, block.getx(), block.gety()) && !isBlockAhead(S, dir, block) ) {
					return true;
				}
			}
		}
		
		if (dir == UP) {
			if (isEqual(block.getx(), character.getx()) && isEqual(block.gety(), character.gety() - TILE)){
				if (block.canMoveToDest(dir, block.getx(), block.gety()) && !isBlockAhead(S, dir, block) ) {
					return true;
				}
			}
		}
		
		if (dir == DOWN) {
			if (isEqual(block.getx(), character.getx()) && isEqual(block.gety(), character.gety() + TILE)){
				if (block.canMoveToDest(dir, block.getx(), block.gety()) && !isBlockAhead(S, dir, block) ) {
					return true;

				}
			}
		}
		return false;
	}
	
	
	/**
	 * check if the TNT is pushed in the direction will there be explosion
	 * 
	 * @param S	Arraylist of sprites containing every sprite in the current level
	 * @param dir the direction the character is trying to move in
	 * @param tnt the particular tnt sprite that is being pushed
	 * @return true if there will be explosion, else false
	 */
	private boolean willExplode(ArrayList<Sprite> S, int dir, Sprite tnt) {
		for (Sprite sprite : S) {
			if (sprite != null && sprite instanceof Cracked) {
				if (dir == LEFT) {																											
					if (isEqual(sprite.getx(), tnt.getx() - TILE) && isEqual(sprite.gety(), tnt.gety())) {				
						return true;
					}
				}
				
				else if (dir == RIGHT) {
					if (isEqual(sprite.getx(), tnt.getx() + TILE) && isEqual(sprite.gety(), tnt.gety())) {
						return true;
					}
				}
				
				if (dir == UP) {
					if (isEqual(sprite.getx(), tnt.getx()) && isEqual(sprite.gety(), tnt.gety() - TILE)) {
						return true;
					}
				}
				
				if (dir == DOWN) {
					if (isEqual(sprite.getx(), tnt.getx()) && isEqual(sprite.gety(), tnt.gety() + TILE)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	
	/**
	 * check if there are any stones ahead of the direction of input
	 * 
	 * @param S Arraylist of sprites containing every sprite in the current level
	 * @param dir the direction the character is trying to move in
	 * @param character The character sprite that is trying to move
	 * @return true if there is a stone ahead, else false
	 */
	private boolean isBlockAhead(ArrayList<Sprite> S, int dir, Sprite character) {															
		for (Sprite sprite : S) {																											
			if (sprite != null && sprite instanceof Block) {
				if (dir == LEFT) {	
					if (isEqual(sprite.getx(), character.getx() - TILE) && isEqual(sprite.gety(), character.gety())) {
						return true;
					}
				}
				if (dir == RIGHT) {
					if (isEqual(sprite.getx(), character.getx() + TILE) && isEqual(sprite.gety(), character.gety())) {
						return true;
					}
				}
				if (dir == UP) {
					if (isEqual(sprite.getx(), character.getx()) && isEqual(sprite.gety(), character.gety() - TILE)) {
						return true;
					}
				}
				if (dir == DOWN) {
					if (isEqual(sprite.getx(), character.getx()) && isEqual(sprite.gety(), character.gety() + TILE)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * loop through the arraylist and call undo if it has an undo function
	 * 
	 * @param sprites Arraylist of sprites containing every sprite in the current level
	 */
	private void undo(ArrayList<Sprite> sprites) {
		for (Sprite sprite : sprites) {
			if (sprite != null && (sprite instanceof Player)) {
				((Player)sprite).undo();
			}
			if (sprite != null && (sprite instanceof Stone)) {
				((Stone)sprite).undo();
			}
			if (sprite != null && (sprite instanceof TNT)) {
				((TNT)sprite).undo();
			}
			if (sprite != null && (sprite instanceof Ice)) {
				((Ice)sprite).undo();
				((Ice)sprite).stopMoving();
			}
		}
	}
	
	/** since iskeypressed checks if there's key pressed from the last time the method is called the same input cannot be reused
	 * convert the user input into a integer so the same input can be reused
	 * also records the position sprites that has moves that can be undone
	 * 
	 * @param input input from the user
	 * @return returning the corresponding integer to the particular direction of input
	 */
	private int getDir(Input input) {											
		if (input.isKeyPressed(Input.KEY_LEFT)) {								

			for (Sprite sprite : sprites) {										
				if (sprite != null && sprite instanceof Player) {					
					((Player)sprite).recordMoves(sprite.getx(), sprite.gety());
				}
				if (sprite != null && sprite instanceof Stone) {					
					((Stone)sprite).recordMoves(sprite.getx(), sprite.gety());
				}
				if (sprite != null && sprite instanceof TNT) {					
					((TNT)sprite).recordMoves(sprite.getx(), sprite.gety());
				}
				if (sprite != null && sprite instanceof Ice && !((Ice)sprite).isIceMoving()) {					
					((Ice)sprite).recordMoves(sprite.getx(), sprite.gety());
				}
				else if (sprite != null && sprite instanceof Ice && ((Ice)sprite).isIceMoving()) {					
					((Ice)sprite).recordMoves(this.iceX, this.iceY);
				}
			}
			return LEFT;
			
		}
		else if (input.isKeyPressed(Input.KEY_RIGHT)) {
			for (Sprite sprite : sprites) {
				if (sprite != null && sprite instanceof Player) {					
					((Player)sprite).recordMoves(sprite.getx(), sprite.gety());
				}
				if (sprite != null && sprite instanceof Stone) {					
					((Stone)sprite).recordMoves(sprite.getx(), sprite.gety());
				}
				
				if (sprite != null && sprite instanceof TNT) {					
					((TNT)sprite).recordMoves(sprite.getx(), sprite.gety());
				}
				if (sprite != null && sprite instanceof Ice && !((Ice)sprite).isIceMoving()) {					
					((Ice)sprite).recordMoves(sprite.getx(), sprite.gety());
				}
				else if (sprite != null && sprite instanceof Ice && ((Ice)sprite).isIceMoving()) {					
					((Ice)sprite).recordMoves(this.iceX, this.iceY);
				}
			}
				
			return RIGHT;
		}
		else if (input.isKeyPressed(Input.KEY_UP)) {
			for (Sprite sprite : sprites) {
				if (sprite != null && sprite instanceof Player) {					
					((Player)sprite).recordMoves(sprite.getx(), sprite.gety());
				}
				if (sprite != null && sprite instanceof Stone) {					
					((Stone)sprite).recordMoves(sprite.getx(), sprite.gety());
				}
				if (sprite != null && sprite instanceof TNT) {					
					((TNT)sprite).recordMoves(sprite.getx(), sprite.gety());
				}
				if (sprite != null && sprite instanceof Ice && !((Ice)sprite).isIceMoving()) {					
					((Ice)sprite).recordMoves(sprite.getx(), sprite.gety());
				}
				else if (sprite != null && sprite instanceof Ice && ((Ice)sprite).isIceMoving()) {					
					((Ice)sprite).recordMoves(this.iceX, this.iceY);
				}
			}
				
			return UP;
		}
		else if (input.isKeyPressed(Input.KEY_DOWN)) {
			for (Sprite sprite : sprites) {
				if (sprite != null && sprite instanceof Player) {					
					((Player)sprite).recordMoves(sprite.getx(), sprite.gety());
				}
				if (sprite != null && sprite instanceof Stone) {					
					((Stone)sprite).recordMoves(sprite.getx(), sprite.gety());
				}
				if (sprite != null && sprite instanceof TNT) {					
					((TNT)sprite).recordMoves(sprite.getx(), sprite.gety());
				}
				if (sprite != null && sprite instanceof Ice && !((Ice)sprite).isIceMoving()) {					
					((Ice)sprite).recordMoves(sprite.getx(), sprite.gety());
				}
				else if (sprite != null && sprite instanceof Ice && ((Ice)sprite).isIceMoving()) {					
					((Ice)sprite).recordMoves(this.iceX, this.iceY);
				}
			}
				
			return DOWN;
		}
		
		if (input.isKeyPressed(Input.KEY_R)) {
			return RESTART;
		}
		if (input.isKeyPressed(Input.KEY_Z)) {
			return UNDO;
		}
		else if (input.isKeyPressed(Input.KEY_P)) {
			return PREV;
		}
		else if (input.isKeyPressed(Input.KEY_N)) {
			return NEXT;
		}
		return INVALID;
	}
	
	/**
	 * function that compares two floats and return true if their difference is less than a certain threshold (thus equal)
	 * 
	 * @param var1 first float that needs comparing
	 * @param var2
	 * @return true if they are equal else false
	 */
	private boolean isEqual(float var1, float var2) {
		return (Math.abs(var1 - var2)) < EPSILON;
	}	
}
