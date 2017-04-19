package project1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author AAnthony The GameEngine is in charge of executing code that will
 *         maintain/control the games internal functions, numbers, etc. based on
 *         the inputs given in the UI.
 */
public class GameEngine {
	private boolean debugMode = false;
	private boolean godMode = false;
	private boolean hardMode;
	private int preDebugBullets;
	private int preDebugLives;
	private int level;
	private int enemiesAlive;
	private GameBoard grid = new GameBoard();

	public GameEngine() {
		gameReset();
		generateBoard();
	}
	
	//// Generating Board /////////////////////////////////
	
	/**
	 * Calls to the grid to generate the board.
	 */
	public void generateBoard() {
		grid.generateBoard(enemiesAlive);
	}

	/**
	 * Alternate generateBoard method; generates the board with the players lives
	 * set to the argument passed onto the lives parameter when called.
	 * Used for starting the next level and when loading a file.
	 */
	public void generateBoard(int lives) {
		grid.generateBoard(enemiesAlive, lives);
	}

	//// Player Actions ///////////////////////////////////

	/**
	 * Method in charge of allowing the user to look in a given direction. Has a
	 * char parameter that is used to tell the program which way the user wanted
	 * to look.
	 */
	public String look(char direction) {
		Player player = grid.getPlayer();
		int row = player.getRow();
		int column = player.getColumn();
		boolean valid = false;

		while (!valid) {
			try {
				int lookRow = row;
				int lookColumn = column;
				switch (direction) {
				case 'u':
					if (grid.checkForRooms(row - 1, column))
						return "A room is in the way.";
					lookRow -= 2;
					break;
				case 'd':
					if (grid.checkForRooms(row + 1, column))
						return "A room is in the way.";
					lookRow += 2;
					break;
				case 'l':
					if (grid.checkForRooms(row, column - 1))
						return "A room is in the way.";
					lookColumn -= 2;
					break;
				case 'r':
					if (grid.checkForRooms(row, column + 1))
						return "A room is in the way.";
					lookColumn += 2;
					break;
				default:
					return "";
				}

				if (grid.checkExists(lookRow, lookColumn)) {
					grid.setVisibility(lookRow, lookColumn, true);
					return "a(n) " + grid.getType(lookRow, lookColumn);
				}
				valid = true;
			} catch (ArrayIndexOutOfBoundsException e) {
				return "a wall";
			}
		}
		return "nothing";
	}

	/**
	 * If the player looked in a space that was not a room or out of bounds area, this method
	 * will set the object to be invisible again after the player moves or fires.
	 */
	public void endLook(char direction) {
		Player player = grid.getPlayer();
		int row = player.getRow();
		int column = player.getColumn();
		int lookRow = row;
		int lookColumn = column;

		switch (direction) {
		case 'u':
			lookRow -= 2;
			break;
		case 'd':
			lookRow += 2;
			break;
		case 'l':
			lookColumn -= 2;
			break;
		case 'r':
			lookColumn += 2;
			break;
		}

		String type = grid.getType(lookRow, lookColumn);
		if (grid.checkExists(lookRow, lookColumn) && !type.equals("Room") && !type.equals("Briefcase"))
			grid.setVisibility(lookRow, lookColumn, false);
	}

	/**
	 * Method in charge of allowing the user to shoot in any direction. String
	 * argument determines which direction the user wants to shoot [inputted
	 * from the user in the UI].
	 */
	public String shoot(char direction) {
		String result = grid.shoot(direction);
		grid.disposeOfBodies();
		enemiesAlive = grid.enemyCount();
		return result;
	}
	
	public char checkMove(char direction) {
		if(grid.checkMove(direction)) return direction;
		else return 's';
	}

	/**
	 * Method in charge of moving the player's position on the board based on
	 * the char argument used when called upon.
	 */
	public boolean movePlayer(char direction) {
		boolean valid = grid.movePlayer(direction);
		grid.createFieldOfView();
		return valid;
	}

	//// Internal Functions /////////////////////////////////
	
	/**
	 * After the player's choice of movement, this method moves the Enemies based
	 * on their difficulty and scenario [IE. random movement vs. chasing the
	 * user (for hard mode)].
	 */
	public boolean moveEnemies() {
		Player player = grid.getPlayer();
		grid.moveEnemies(hardMode);
		
		if(grid.scanFor("Player")) {
			grid.createFieldOfView();
		}
		
		if(debugMode || godMode)
			grid.debugVisibility();
		
		boolean alive = grid.scanFor("Player");
		checkDeath(player);
		return alive;
	}
	
	/**
	 * At the end of each turn, if the player is not in god mode,
	 * this method will lower the player's shield counter by 1.
	 */
	public void endTurnFunctions() {
		if(!godMode)
			grid.lowerShieldCounter();
	}

	
	/**
	 * Calls to the GameBoard's deleteBoard() method.
	 */
	public void levelReset() {
		grid.deleteBoard();
	}

	/**
	 * Deletes the existing board, and resets the game's level and enemiesAlive fields
	 * to their initial values (used for starting fresh/new games).
	 */
	public void gameReset() {
		levelReset();
		level = 1;
		enemiesAlive = level + 5;
	}

	/**
	 * Increases the level field by one and sets the enemies to the level + 5.
	 * Used to increase the level for when the user beats a level.
	 */
	public void increaseLevel() {
		level++;
		enemiesAlive = level + 5;
	}
	
	//// Saving and Loading  /////////////////////////////////
	
	/**
	 * This method is in charge of writing the game's current status into a .dat
	 * file with a name specified by the user.
	 */
	public String saveFile(File file, Frame frame) {
		Data saveData = new Data(level, enemiesAlive, frame);
		saveData = grid.saveBoard(saveData);
	
		try {
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(saveData);
			fos.close();
		} catch (IOException e) {
			return "Your game is corrupted and could not be saved...";
		}
		return file.getName() + " has been saved!";
	}

	/**
	 * This method is in charge of reading and loading any selected save data.
	 */
	public String loadFile(File file) {
		if (file.getName().toLowerCase().charAt(0) == 'b') {
			return "back";
		}
		try {
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Data loadedData = (Data) ois.readObject();
	
			level = loadedData.getLevel();
			enemiesAlive = (loadedData.getEnemiesLoc().length) / 2;
			grid.loadBoard(loadedData);
			fis.close();
			return "";
		} catch (IOException e) {
			return "This file does not exist!";
		} catch (ClassNotFoundException e) {
			return "This is not a proper save file!";
		}
	}

	//// Debug and God Toggling /////////////////////////////////
	
	/**
	 * Method used to toggle debug mode. Calls various methods to modify the
	 * game state to execute debug mode.
	 */
	public void executeDebug() {
		if(!debugMode) {
			grid.debugVisibility();
			preDebugBullets = grid.getBullets();
			preDebugLives = grid.getLives();
			grid.setDebugPlayer();
			debugMode = true;
		}
		else {
			grid.undoDebugVisibility();
			Player currentPlayer = grid.getPlayer();
			int row = currentPlayer.getRow();
			int column = currentPlayer.getColumn();
			char direction = currentPlayer.getDirection();
			grid.undoDebugPlayer(row, column, direction, preDebugBullets, preDebugLives);
			debugMode = false;
		}
	}
	
	/**
	 * Acts in the same respect as executeDebug, except this method also increases the players
	 * invincibility, to avoid death.
	 */
	public void executeGod() {
		if(!godMode) {
			grid.debugVisibility();
			preDebugBullets = grid.getBullets();
			preDebugLives = grid.getLives();
			grid.setDebugPlayer();
			grid.setInvincibility(true);
			godMode = true;
		}
		else {
			grid.undoDebugVisibility();
			Player currentPlayer = grid.getPlayer();
			int row = currentPlayer.getRow();
			int column = currentPlayer.getColumn();
			char direction = currentPlayer.getDirection();
			grid.undoDebugPlayer(row, column, direction, preDebugBullets, preDebugLives);
			grid.setInvincibility(false);
			godMode = false;
		}
	}
	
	/**
	 * Calls to the GameBoard method unstuck().
	 */
	public void unstuck() {
		grid.unstuck();
	}

	//// Check Methods /////////////////////////////////
	
	/**
	 * Checks whether or not an enemy has overwritten the players space. If the player
	 * does not exist on the board, then he/she is dead and thus respawned in his/her init
	 * position with one less life.
	 * If an enemy is in the player's spawn point, it will be respawned elsewhere.
	 */
	public void checkDeath(Player player) {
		boolean alive = grid.scanFor("Player");
		if (!alive) {
			grid.moveEnemyFromSpawn();
			player.setLives(player.getLives() - 1);
			player.setRow(8);
			player.setColumn(0);
			grid.spawnPlayer(player);
		}
	}

	/**
	 * This method checks whether or not he/she has found the briefcase,
	 * whenever the player has entered one of the nine rooms.
	 */
	public boolean checkWin() {
		return grid.checkWin();
	}

	/**
	 * Method used in the GUI GameFrame class. Determines which mode the player is in, so as
	 * to determine which radio button will be selected.
	 */
	public boolean checkMode(String mode) {
		switch(mode) {
		case "Play":
			if(!debugMode && !godMode)
				return true;
			else
				return false;
		case "Debug":
			if(debugMode && !godMode)
				return true;
			else
				return false;
		case "God":
			if(godMode && !debugMode)
				return true;
			else 
				return false;
		}
		return false; //Obligatory return. Code will never reach here.
	}

	/**
	 * Calls the GameBoard's checkExists() method.
	 */
	public boolean checkExists(int i, int j) {
		return grid.checkExists(i, j);
	}

	/**
	 * Used when the UI is displaying the board. If the player is still invincible, it
	 * will return true, which will show the message to the player after a move is done.
	 * If the player is not invincible, it will return false, and the message will not be 
	 * shown.
	 */
	public boolean checkInvincibility() {
		if(godMode)
			return false;
		else if(!(getInvincibility() <= 2))
			return true;
		else return false;
	}

	/**
	 * Calls to the GameBoard's scanFor() method.
	 */
	public boolean scanFor(String type) {
		return grid.scanFor(type);
	}
	
	/**
	 * Checks if there is an enemy that is visible.
	 * Used in the GameFrame's generate text method for certain cases.
	 */
	public boolean enemyIsVisible() {
		return grid.enemyIsVisible();
	}

	//// Getters and Setters //////////////////////////////
	
	public boolean isVisible(int i, int j) {
		return grid.isVisible(i, j);
	}

	public char getType(int i, int j) {
		String s = grid.getType(i, j);
		return s.charAt(0);
	}

	public Player getPlayer() {
		return grid.getPlayer();
	}

	public int getLives() {
		return grid.getLives();
	}

	public int getBullets() {
		return grid.getBullets();
	}

	public int getLevel() {
		return level;
	}

	public int getEnemies() {
		return enemiesAlive;
	}
	
	public char getDirection(int row, int col) {
		MapObjects o = grid.getUnit(row, col);
		if(o.getType().equals("Player"))
			return ((Player)o).getDirection();
		else {
			return ((Enemy)o).getDirection();
		}
	}

	public boolean getHardMode() {
		return hardMode;
	}

	public int getInvincibility() {
		return grid.getPlayer().isInvincible();
	}

	public boolean getDebugMode() {
		return debugMode;
	}

	public boolean getGodMode() {
		return godMode;
	}

	public void setPreDebugBullets(int bullets) {
		preDebugBullets = bullets;
	}

	public void setPreDebugLives(int lives) {
		preDebugLives = lives;
	}

	public void setHardMode(boolean hardMode) {
		this.hardMode = hardMode;
	}

	public void setDebugMode(boolean mode) {
		debugMode = mode;
	}

	public void setGodMode(boolean mode) {
		godMode = mode;
	}
}
