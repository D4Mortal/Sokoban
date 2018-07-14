package project2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class for handling loading the level from files
 * built upon project 1 sample provided by the All-seeing and All-knowing Eleanor
 * @author dhao1
 *
 */

public class Loader {
	private static String[][] types;
	
	private static int world_width;
	private static int world_height;
	private static int offset_x;
	private static int offset_y;
	
	private static boolean isCracked = true;
	private static boolean doorClosed = true;
	
	/**
	 * Create the appropriate sprite given a name and location.
	 * @param name	the name of the sprite
	 * @param x		the x position
	 * @param y		the y position
	 * @return		the sprite object
	 */
	private static Sprite createSprite(String name, float x, float y) {
		switch (name) {
			case "wall":
				return new Wall(x, y);
			case "floor":
				return new Floor(x, y);
			case "stone":
				return new Stone(x, y);
			case "target":
				return new Target(x, y);
			case "player":
				return new Player(x, y);
			case "mage":
				return new Mage(x, y);
			case "skeleton":
				return new Skeleton(x, y);
			case "rogue":
				return new Rogue(x, y);
			case "cracked":
				return new Cracked(x, y);
			case "door":
				return new Door(x, y);
			case "ice":
				return new Ice(x, y);
			case "switch":
				return new Switch(x, y);
			case "tnt":
				return new TNT(x, y);	

		}
		return null;
	}
	
	/**
	 * Converts a world coordinate to a tile coordinate,
	 * and returns if that location is a blocked tile
	 * @param x	X position
	 * @param y Y position
	 * @return return true if the location is blocked
	 */
	public static boolean isBlocked(float x, float y) {
		x -= offset_x;
		x /= App.TILE_SIZE;
		y -= offset_y;
		y /= App.TILE_SIZE;
		boolean blocked = false;


		
		// Do bounds checking!
		if (x >= 0 && x < world_width && y >= 0 && y < world_height) {
			if (types[(int)x][(int)y].equals("wall")) {
				blocked = true;
			}
			
			//only check for doors and cracked walls unless they are opened or not destroyed
			if (types[(int)x][(int)y].equals("cracked") && isCracked) {
				blocked = true;
			}
			if (types[(int)x][(int)y].equals("door") && doorClosed) {
				blocked = true;
			}
		}
		return blocked;
	}
	
	public static void removeCrack() {
		isCracked = false;
	}
	public static void openDoor() {
		doorClosed = false;
	}
	public static void closeDoor() {
		doorClosed = true;
	}	
	
	/**
	 * Loads the sprites from a given file.
	 * @param filename
	 * @return
	 */
	public static ArrayList<Sprite> loadSprites(int lvl) {
		String filename = level(lvl);
		ArrayList<Sprite> list = new ArrayList<>();
		isCracked = true;
		doorClosed = true;
		// Open the file
		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			String line;
			
			// Find the world size
			line = reader.readLine();
			String[] parts = line.split(",");
			world_width = Integer.parseInt(parts[0]);
			world_height = Integer.parseInt(parts[1]);
			
			// Create the array of object types
			types = new String[world_width][world_height];
			
			// Calculate the top left of the tiles so that the level is
			// centred
			offset_x = (App.SCREEN_WIDTH - world_width * App.TILE_SIZE) / 2;
			offset_y = (App.SCREEN_HEIGHT - world_height * App.TILE_SIZE) / 2;

			// Loop over every line of the file
			while ((line = reader.readLine()) != null) {
				String name;
				float x, y;
				
				// Split the line into parts
				parts = line.split(",");
				name = parts[0];
				x = Integer.parseInt(parts[1]);
				y = Integer.parseInt(parts[2]);
				types[(int)x][(int)y] = name;
				
				// Adjust for the grid
				x = offset_x + x * App.TILE_SIZE;
				y = offset_y + y * App.TILE_SIZE;
				
				// Create the sprite
				list.add(createSprite(name, x, y));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/** 
	 * function to make loading the level easier
	 * 
	 * @param lvl integer representation of level
	 * @return	corresponding string for the level file
	 */
		
	public static String level(int lvl) {
		if (lvl == 0) {
			return "res/levels/0.lvl";
		}
		else if (lvl == 1) {
			return "res/levels/1.lvl";
		}
		else if (lvl == 2) {
			return "res/levels/2.lvl";
		}
		else if (lvl == 3) {
			return "res/levels/3.lvl";
		}
		else if (lvl == 4) {
			return "res/levels/4.lvl";
		}
		else if (lvl == 5) {
			return "res/levels/5.lvl";
		}
		return "";
	}

}
