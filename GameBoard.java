package project1;

import java.util.Random;

/**
 * @author AAnthony This class is in charge of keeping and controlling any
 *         changes made to the GameBoard. It holds all of the spawned MapObjects
 *         in a 2-D array, size 9 x 9.
 */
public class GameBoard {
	private static int BOARD_SIZE = 9;
	private MapObjects[][] grid = new MapObjects[BOARD_SIZE][BOARD_SIZE];
	private Random rng = new Random();

	//// Generating the Board ///////////////////////////////////////

	/**
	 * This method generates a new board with the player in the bottom right, 5
	 * randomly spawned in enemies, 3 randomly spawned power-ups, and 1
	 * briefcase spawned in 1 of the 9 rooms.
	 * Also sets the initial field of view for the player.
	 */
	public void generateBoard(int enemies) {
		spawnPlayer();
		spawnRooms();
		spawnEnemies(enemies);
		// spawnEnemies();
		spawnBriefcase();
		spawnPowerUps();

		// Initial Field of View
		if (grid[7][0] != null)
			grid[7][0].setVisible(true);
		if (grid[8][1] != null)
			grid[8][1].setVisible(true);
	}
	
	/**
	 * Alt generateBoard method: used when the player must be spawned with a specific
	 * amount of lives (IE. during loading a file or when the player must be respawned
	 * after death).
	 */
	public void generateBoard(int enemies, int lives) {
		spawnPlayer(lives);
		spawnRooms();
		spawnEnemies(enemies);
		spawnBriefcase();
		spawnPowerUps();

		// Initial Field of View
		if (grid[7][0] != null)
			grid[7][0].setVisible(true);
		if (grid[8][1] != null)
			grid[8][1].setVisible(true);
	}

	/**
	 * Spawns the player in the lower left corner.
	 */
	private void spawnPlayer() {
		grid[8][0] = new Player();
	}

	/**
	 * Spawns the player in the lower left corner with lives set to whatever argument
	 * is passed for the lives parameter when called upon.
	 */
	private void spawnPlayer(int lives) {
		grid[8][0] = new Player();
		((Player) grid[8][0]).setLives(lives);
	}

	/**
	 * Spawns a player argument in the lower left corner spawn point.
	 */
	public void spawnPlayer(Player player) {
		grid[8][0] = player;
	}

	/**
	 * Spawns the rooms in their proper positions.
	 */
	private void spawnRooms() {
		for (int i = 1; i < 8; i += 3) {
			for (int j = 1; j < 8; j += 3) {
				if (grid[i][j] == null)
					grid[i][j] = new Room();
			}
		}
	}

	/**
	 * Spawns multiple enemies; quantity is based on the argument passed to the enemies parameter
	 * when called upon. Generates a randomly generated position on the grid until it can find
	 * a null space for the enemy to be spawned.
	 */
	private void spawnEnemies(int enemies) {
		for (int i = 0; i < enemies;) { // Generates Enemies
			int x = rng.nextInt(8);
			int y = rng.nextInt(8);
			if (x < 6 && y > 2 && grid[x][y] == null) {
				grid[x][y] = new Enemy(x, y);
				++i;
			}
		}
	}

	/**
	 * Randomly generates a location on the board until it finds the location of one of the rooms.
	 * Which ever room it find will be set to have the briefcase/goal objective.
	 */
	private void spawnBriefcase() {
		boolean briefcaseSet = false;
		while (!briefcaseSet) {
			int x = rng.nextInt(8);
			int y = rng.nextInt(8);
			if (getType(x, y).equals("Room")) {
				grid[x][y] = new Room(true);
				briefcaseSet = true;
			}
		}
	}

	/**
	 * Spawns the power-ups in a randomly generated location. One of each is spawned.
	 * A random number generator generates a random location until a valid, null space is found for
	 * each power up to be spawned.
	 */
	private void spawnPowerUps() {
		boolean ammoSet = false;
		boolean radarSet = false;
		boolean shieldSet = false;
		while (!ammoSet) {
			int x = rng.nextInt(8);
			int y = rng.nextInt(8);
			if (grid[x][y] == null) {
				grid[x][y] = new Ammo();
				ammoSet = true;
			}
		}
		while (!radarSet) {
			int x = rng.nextInt(8);
			int y = rng.nextInt(8);
			if (grid[x][y] == null) {
				grid[x][y] = new Locator();
				radarSet = true;
			}
		}
		while (!shieldSet) {
			int x = rng.nextInt(8);
			int y = rng.nextInt(8);
			if (grid[x][y] == null) {
				grid[x][y] = new Shield();
				shieldSet = true;
			}
		}
	}

	/**
	 * Nullifies all spots on the grid, so as to let the program generate a new
	 * board without error.
	 */
	public void deleteBoard() {
		for (int i = 0; i < grid.length; ++i) {
			for (int j = 0; j < grid.length; ++j) {
				grid[i][j] = null;
			}
		}
	}

	/**
	 * Takes in a Data object argument and runs through the board to save the various elements
	 * that will be used again when loading the board. This includes the player's row, column,
	 * lives, bullets, etc.
	 */
	public Data saveBoard(Data saveData) {
		int enemyCounter = 0;
		Player p = getPlayer();
		saveData.setPlayerLoc(p.getRow(), p.getColumn());
		saveData.setLives(p.getLives());
		saveData.setBullets(p.getBullets());
		saveData.setInvincible(p.isInvincible());
		saveData.setPlayerDirection(p.getDirection());

		for (int i = 0; i < BOARD_SIZE; ++i) {
			for (int j = 0; j < BOARD_SIZE; ++j) {
				if (checkExists(i, j)) {
					String type = grid[i][j].getType();
					switch (type) {
					case "Enemy":
						saveData.setEnemiesLoc(((Enemy) grid[i][j]).getRow(), ((Enemy) grid[i][j]).getColumn(),
								enemyCounter);
						saveData.setEnemiesDirection(((Enemy) grid[i][j]).getDirection(), enemyCounter);
						enemyCounter++;
						break;
					case "Ammo":
						saveData.setAmmoLoc(i, j);
						break;
					case "Locator":
						saveData.setLocatorLoc(i, j);
						break;
					case "Shield":
						saveData.setShieldLoc(i, j);
						break;
					case "Room":
					case "Briefcase":
						if (((Room) grid[i][j]).hasBriefcase())
							saveData.setBriefcaseLoc(i, j);
						break;
					}
				}
			}
		}

		return saveData;
	}

	/**
	 * Takes the Data object passed into the d parameter and generates the board based on the
	 * fields of the Data object.
	 */
	public void loadBoard(Data d) {
		deleteBoard();

		grid[d.getPlayerLoc()[0]][d.getPlayerLoc()[1]] = new Player(d.getPlayerLoc()[0], d.getPlayerLoc()[1],
				d.getLives(), d.getBullets(), d.getInvincible(), d.getPlayerDirection());
		if (d.getAmmoLoc() != null)
			grid[d.getAmmoLoc()[0]][d.getAmmoLoc()[1]] = new Ammo();
		if (d.getShieldLoc() != null)
			grid[d.getShieldLoc()[0]][d.getShieldLoc()[1]] = new Shield();
		if (d.getLocatorLoc() != null)
			grid[d.getLocatorLoc()[0]][d.getLocatorLoc()[1]] = new Locator();

		int enemyCounter = 0;
		for (int i = 0; i < d.getEnemiesLoc().length; i += 2) {
			int row = d.getEnemiesLoc()[i];
			int column = d.getEnemiesLoc()[i + 1];
			char direction = d.getEmemiesDirection()[enemyCounter];
			grid[row][column] = new Enemy(row, column, direction);
			++enemyCounter;
		}

		spawnRooms();
		grid[d.getBriefcaseLoc()[0]][d.getBriefcaseLoc()[1]] = new Room(true);
		if (!scanFor("Locator"))
			((Room) grid[d.getBriefcaseLoc()[0]][d.getBriefcaseLoc()[1]]).setLocated(true);
	}

	//// Moving the Player/Enemies /////////////////////////////////////////////

	/**
	 * This method checks if the player can move to whatever direction is passed into
	 * @param direction.
	 * @return true if valid, false if invalid.
	 */
	public boolean checkMove(char direction) {
		Player player = getPlayer();
		boolean valid = false;
		int row = player.getRow();
		int column = player.getColumn();

		if (checkPlayerInRoom() && direction != 'u')
			return false;

		switch (direction) {
		case 'u':
			valid = checkObstruction(row - 1, column, direction);
			break;
		case 'd':
			valid = checkObstruction(row + 1, column, direction);
			break;
		case 'l':
			valid = checkObstruction(row, column - 1, direction);
			break;
		case 'r':
			valid = checkObstruction(row, column + 1, direction);
			break;
		}
		return valid;
	}
	
	/**
	 * This method is in charge of moving the player based on the direction the user inputted in
	 * the UI. Throughout the process of the method, it deletes the current field of view,
	 * moves the player to its new space (if applicable) and creates the new field of view.
	 */
	public boolean movePlayer(char direction) {
		Player player = getPlayer();
		boolean valid = false;
		int row = player.getRow();
		int column = player.getColumn();

		if (checkPlayerInRoom() && direction != 'u')
			return false;

		deleteFieldOfView();

		switch (direction) {
		case 'u':
			valid = checkObstruction(row - 1, column, direction);
			if (valid) {
				moveObject(row, column, row - 1, column, player, direction);
				spawnRooms();
				return true;
			}
			break;
		case 'd':
			valid = checkObstruction(row + 1, column, direction);
			if (valid) {
				moveObject(row, column, row + 1, column, player, direction);
				return true;
			}
			break;
		case 'l':
			valid = checkObstruction(row, column - 1, direction);
			if (valid) {
				moveObject(row, column, row, column - 1, player, direction);
				return true;
			}
			break;
		case 'r':
			valid = checkObstruction(row, column + 1, direction);
			if (valid) {
				moveObject(row, column, row, column + 1, player, direction);
				return true;
			}
			break;
		}
		return false;
	}

	/**
	 * Sets any object to the immediate up, down, left, or right of the player to be invisible, unless
	 * it is an object that is meant to stay visible (Rooms).
	 */
	private void deleteFieldOfView() {
		Player player = getPlayer();
		int row = player.getRow();
		int col = player.getColumn();

		try {
			if (checkExists(row - 1, col) && !(grid[row - 1][col].getType().equals("Room")
					|| grid[row - 1][col].getType().equals("Briefcase")))
				grid[row - 1][col].setVisible(false);
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		try {
			if (checkExists(row + 1, col) && !(grid[row + 1][col].getType().equals("Room")
					|| grid[row + 1][col].getType().equals("Briefcase")))
				grid[row + 1][col].setVisible(false);
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		try {
			if (checkExists(row, col - 1) && !(grid[row][col - 1].getType().equals("Room")
					|| grid[row][col - 1].getType().equals("Briefcase")))
				grid[row][col - 1].setVisible(false);
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		try {
			if (checkExists(row, col + 1) && !(grid[row][col + 1].getType().equals("Room")
					|| grid[row][col + 1].getType().equals("Briefcase")))
				grid[row][col + 1].setVisible(false);
		} catch (ArrayIndexOutOfBoundsException e) {
		}
	}

	/**
	 * Sets the objects in the spaces to the player's immediate up, down, left, and right to
	 * visible.
	 */
	public void createFieldOfView() {
		Player player = getPlayer();
		int row = player.getRow();
		int col = player.getColumn();

		if (!checkPlayerInRoom()) {
			try {
				if (checkExists(row - 1, col))
					grid[row - 1][col].setVisible(true);
			} catch (ArrayIndexOutOfBoundsException e) {
			}

			try {
				if (checkExists(row + 1, col))
					grid[row + 1][col].setVisible(true);
			} catch (ArrayIndexOutOfBoundsException e) {
			}

			try {
				if (checkExists(row, col - 1))
					grid[row][col - 1].setVisible(true);
			} catch (ArrayIndexOutOfBoundsException e) {
			}

			try {
				if (checkExists(row, col + 1))
					grid[row][col + 1].setVisible(true);
			} catch (ArrayIndexOutOfBoundsException e) {
			}
		}
	}

	/**
	 * This is the main method in charge of moving the enemies. Runs through the board by each row,
	 * left to right, and moves any unmoved enemies. If a valid move does not exist for that enemy, then
	 * the code skips over said enemy, to avoid crashing the game.
	 */
	public void moveEnemies(boolean hardMode) {
		int row = 0;
		int column = 0;
		int newRow = 0;
		int newColumn = 0;
		boolean valid = false;
		char direction = 's';

		for (MapObjects[] m : grid) {
			for (MapObjects o : m) {
				if (o != null && o.getType().equals("Enemy") && !((Enemy) o).hasMoved()) {
					row = ((Enemy) o).getRow();
					column = ((Enemy) o).getColumn();
					if (validMoveExists(row, column)) {
						char spotted = scanAreaAroundEnemy(row, column, hardMode);
						if (spotted != 'n' && getPlayer().isInvincible() == 0) {
							switch (spotted) {
							case 'u':
								newRow = row - 1;
								newColumn = column;
								direction = 'u';
								break;
							case 'd':
								newRow = row + 1;
								newColumn = column;
								direction = 'd';
								break;
							case 'l':
								newRow = row;
								newColumn = column - 1;
								direction = 'l';
								break;
							case 'r':
								newRow = row;
								newColumn = column + 1;
								direction = 'r';
								break;
							}
						} else {
							while (!valid) {
								newRow = row;
								newColumn = column;
								switch (rng.nextInt(4)) {
								case 0:
									newRow++;
									direction = 'd';
									break;
								case 1:
									newRow--;
									direction = 'u';
									break;
								case 2:
									newColumn++;
									direction = 'r';
									break;
								case 3:
									newColumn--;
									direction = 'l';
									break;
								}
								valid = checkObstruction(newRow, newColumn);
							}
						}
						moveObject(row, column, newRow, newColumn, o, direction);
						valid = false;
					}
				}
			}
		}

		for (MapObjects[] m : grid) {
			for (MapObjects o : m) {
				if (o != null && o.getType().equals("Enemy"))
					((Enemy) o).setHasMoved(false);
			}
		}
	}

	/**
	 * First the method checks the spaces around the enemy to see if an enemy is there to
	 * kill. If not, and if the game is in hard mode, then the method will check if the player is 
	 * in the same row/column of the enemy, and if so, check which direction to head in order
	 * to follow the player.
	 * The returned char represents the direction the enemy should move. 'n' means the
	 * player has NOT been spotted.
	 */
	private char scanAreaAroundEnemy(int row, int col, boolean hardMode) {
		if (scanFor("Player")) {
			try {
				if (checkExists(row - 1, col) && grid[row - 1][col].getType().equals("Player")) {
					if (((row - 1) == 1 || (row - 1) == 4 || (row - 1) == 7) && (col == 1 || col == 4 || col == 7))
						return 'n';
					else
						return 'u';
				}
			} catch (ArrayIndexOutOfBoundsException e) {
			}

			try {
				if (checkExists(row + 1, col) && grid[row + 1][col].getType().equals("Player")) {
					if (((row + 1) == 1 || (row + 1) == 4 || (row + 1) == 7) && (col == 1 || col == 4 || col == 7))
						return 'n';
					else
						return 'd';
				}
			} catch (ArrayIndexOutOfBoundsException e) {
			}

			try {
				if (checkExists(row, col - 1) && grid[row][col - 1].getType().equals("Player")) {
					if ((row == 1 || row == 4 || row == 7) && ((col - 1) == 1 || (col - 1) == 4 || (col - 1) == 7))
						return 'n';
					else
						return 'l';
				}
			} catch (ArrayIndexOutOfBoundsException e) {
			}

			try {
				if (checkExists(row, col + 1) && grid[row][col + 1].getType().equals("Player")) {
					if ((row == 1 || row == 4 || row == 7) && ((col + 1) == 1 || (col + 1) == 4 || (col + 1) == 7))
						return 'n';
					else
						return 'r';
				}
			} catch (ArrayIndexOutOfBoundsException e) {
			}
		}
		if (hardMode && scanFor("Player")) {
			try {
				if (col == getPlayer().getColumn()) {
					for (int testRow = row; testRow >= 0; --testRow) {
						if (!checkExists(row - 1, col)) {
							if (this.grid[testRow][col] instanceof Player) {
								return 'u';
							}
							if (this.grid[testRow][col] instanceof Room) {
								return 'n';
							}
						}
					}
				}
			} catch (ArrayIndexOutOfBoundsException e) {
			}

			try {
				if (col == getPlayer().getColumn()) {
					for (int testRow = row; testRow <= grid.length; ++testRow) {
						if (!checkExists(row + 1, col)) {
							if (this.grid[testRow][col] instanceof Player) {
								return 'd';
							}
							if (this.grid[testRow][col] instanceof Room) {
								return 'n';
							}
						}
					}
				}
			} catch (ArrayIndexOutOfBoundsException e) {
			}

			try {
				if (row == getPlayer().getRow()) {
					for (int testCol = col; testCol >= 0; --testCol) {
						if (!checkExists(row, col - 1)) {
							if (this.grid[row][testCol] instanceof Player) {
								return 'l';
							}
							if (this.grid[row][testCol] instanceof Room) {
								return 'n';
							}
						}
					}
				}
			} catch (ArrayIndexOutOfBoundsException e) {
			}

			try {
				if (row == getPlayer().getRow()) {
					for (int testCol = col; testCol <= grid.length; ++testCol) {
						if (!checkExists(row, col + 1)) {
							if (this.grid[row][testCol] instanceof Player) {
								return 'r';
							}
							if (this.grid[row][testCol] instanceof Room) {
								return 'n';
							}
						}
					}
				}
			} catch (ArrayIndexOutOfBoundsException e) {
			}
		}
		return 'n';
	}

	/**
	 * Checks if the enemy has a valid move (up, down, left, right). Returns true if 
	 * a valid move exists; else it returns false.
	 */
	private boolean validMoveExists(int row, int col) {
		try {
			if (!checkExists(row - 1, col))
				return true;
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		try {
			if (!checkExists(row + 1, col))
				return true;
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		try {
			if (!checkExists(row, col - 1))
				return true;
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		try {
			if (!checkExists(row, col + 1))
				return true;
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		return false;
	}

	/**
	 * Checks if the player is currently occupying a room. Used in instances where the player
	 * should be treated as a room, such as if the enemy is trying to kill the player from the
	 * right, but the player is in a room and should not be killed through the room's walls.
	 */
	private boolean checkPlayerInRoom() {
		for (int i = 1; i < 8; i += 3) {
			for (int j = 1; j < 8; j += 3) {
				if (grid[i][j].getType().equals("Player"))
					return true;
			}
		}
		return false;
	}

	/**
	 * Used to check if the randomly generated space the enemy is trying to move to contains any obstructions.
	 */
	private boolean checkObstruction(int row, int column) {
		try {
			if (grid[row][column] != null)
				return false;
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
		return true;
	}

	/**
	 * Alternative checkObstruction method used when moving the player. This determines if the move is
	 * valid, and based on any objects the player may be overwriting (power-ups or a room), will do 
	 * the appropriate actions to alter the game state.
	 */
	private boolean checkObstruction(int row, int column, char direction) {
		try {
			if (grid[row][column] != null) {
				switch (grid[row][column].getType()) {
				case "Locator":
					for (MapObjects[] m : grid) {
						for (MapObjects o : m) {
							if (o != null && o.getType().equals("Room") && ((Room) o).hasBriefcase())
								((Room) o).setLocated(true);
						}
					}
					break;
				case "Room":
				case "Briefcase":
					if (direction != 'd')
						return false;
					break;
				case "Enemy":
					return false;
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
		return true;
	}

	/**
	 * Takes the object in the oldRow and oldCol space and moves it to the newRow and newCol space.
	 * This is done by setting the old space to null and setting the new space to the MapObject passed.
	 * For the sake of the GUI, the direction of the player/enemy is passed to be set after moving the
	 * object.
	 */
	private void moveObject(int oldRow, int oldCol, int newRow, int newCol, MapObjects o, char direction) {
		String type = o.getType();
		if (type.equals("Player")) {
			((Player) o).setRow(newRow);
			((Player) o).setColumn(newCol);
			((Player) o).setDirection(direction);
	
		} else if (type.equals("Enemy")) {
			((Enemy) o).setRow(newRow);
			((Enemy) o).setColumn(newCol);
			((Enemy) o).setHasMoved(true);
			((Enemy) o).setDirection(direction);
		}
		grid[newRow][newCol] = o;
		grid[oldRow][oldCol] = null;
		if (type.equals("Enemy") && !scanFor("Player")) {
			grid[newRow][newCol].setVisible(false);
		}
	}

	/**
	 * At the end of every turn, this method lowers the player's shield counter by one.
	 */
	public void lowerShieldCounter() {
		for (MapObjects[] m : grid) {
			for (MapObjects o : m) {
				if (o != null && o.getType().equals("Player") && ((Player) o).isInvincible() != 0)
					((Player) o).setInvincible(((Player) o).isInvincible() - 1);
			}
		}
	}

	//// Firing/Shooting ////////////////////////////////////
	
	/**
	 * Method in charge of firing the player's gun in the specified direction.
	 */
	public String shoot(char direction) {
		Player player = getPlayer();
		switch (direction) {
		case 'u':
			return shootUp(player);
		case 'd':
			return shootDown(player);
		case 'l':
			return shootLeft(player);
		case 'r':
			return shootRight(player);
		}
		return "";
	}

	/**
	 * Method run if the player decides to shoot up. Checks for enemies in the appropriate spaces.
	 */
	private String shootUp(Player player) {
		int col = player.getColumn();
		for (int row = player.getRow(); row >= 0; --row) {
			if (this.grid[row][col] instanceof Enemy) {
				Enemy e = (Enemy) grid[row][col];
				if (e.isAlive()) {
					e.setAlive(false);
					// Enemy is now dead.
					return "Hit enemy at [" + row + "][" + col + "].";
				}
			}
			if (this.grid[row][col] instanceof Room) {
				return "The bullet hit the room.";
			}
		}
		return "The bullet hit the wall.";
	}

	/**
	 * Functions similar to the shootUp method, but for the downward direction.
	 */
	private String shootDown(Player player) {
		int col = player.getColumn();
		for (int row = player.getRow(); row < grid.length; ++row) {
			if (this.grid[row][col] instanceof Enemy) {
				Enemy e = (Enemy) grid[row][col];
				if (e.isAlive()) {
					e.setAlive(false);
					// Enemy is now dead.
					return "Hit enemy at [" + row + "][" + col + "].";
				}
			}
			if (this.grid[row][col] instanceof Room) {
				return "The bullet hit the room.";
			}
		}
		return "The bullet hit the wall.";
	}

	/**
	 * Functions similar to the shootUp method, but for the left direction.
	 */
	private String shootLeft(Player player) {
		int row = player.getRow();
		for (int col = player.getColumn(); col >= 0; --col) {
			if (this.grid[row][col] instanceof Enemy) {
				Enemy e = (Enemy) grid[row][col];
				if (e.isAlive()) {
					e.setAlive(false);
					// Enemy is now dead.
					return "Hit enemy at [" + row + "][" + col + "].";
				}
			}
			if (this.grid[row][col] instanceof Room) {
				return "The bullet hit the room.";
			}
		}
		return "The bullet hit the wall.";
	}

	/**
	 * Functions similar to the shootUp method, but in the right direction.
	 */
	private String shootRight(Player player) {
		int row = player.getRow();
		for (int col = player.getColumn(); col < grid.length; ++col) {
			if (this.grid[row][col] instanceof Enemy) {
				Enemy e = (Enemy) grid[row][col];
				if (e.isAlive()) {
					e.setAlive(false);
					// Enemy is now dead.
					return "Hit enemy at [" + row + "][" + col + "].";
				}
			}
			if (this.grid[row][col] instanceof Room) {
				return "The bullet hit the room.";
			}
		}
		return "The bullet hit the wall.";
	}

	/**
	 * If any enemies were set to be dead (alive = false), then they are erased from the board.
	 */
	public void disposeOfBodies() {
		for (MapObjects[] m : grid) {
			for (MapObjects o : m) {
				if (o != null && o.getType().equals("Enemy") && !((Enemy) o).isAlive()) {
					grid[((Enemy) o).getRow()][((Enemy) o).getColumn()] = null;
				}
			}
		}
	}

	//// Checks, Getters, Setters ////////////////////////////////////

	/**
	 * If the enemy is on the player's spawn point during the time of death or being unstuck,
	 * it is moved.
	 */
	public void moveEnemyFromSpawn() {
		if(grid[8][0] != null && grid[8][0].getType().equals("Enemy")) {
			grid[8][0] = null;
			spawnEnemies(1);
		}
	}

	/**
	 * Sets all objects on the board to visible and sets the room with the briefcase to located, so
	 * the player can see everything on the board.
	 */
	public void debugVisibility() {
		for (MapObjects[] m : grid) {
			for (MapObjects o : m) {
				if (o != null)
					o.setVisible(true);
			}
		}
		for (MapObjects[] m : grid) {
			for (MapObjects o : m) {
				if (o != null && o.getType().equals("Room") && ((Room) o).hasBriefcase())
					((Room) o).setLocated(true);
			}
		}
	}

	/**
	 * The opposite of the above method. Used to hide all that were hidden before activating debug.
	 * If the locator has been found, the briefcase room will remain located.
	 */
	public void undoDebugVisibility() {
		for (MapObjects[] m : grid) {
			for (MapObjects o : m) {
				if (o != null && !o.getType().equals("Player") && !o.getType().equals("Room")
						&& !o.getType().equals("Briefcase"))
					o.setVisible(false);
			}
		}
	
		if (scanFor("Locator")) {
			for (MapObjects[] m : grid) {
				for (MapObjects o : m) {
					if (o != null && o.getType().equals("Briefcase"))
						((Room) o).setLocated(false);
				}
			}
		}
	}

	/**
	 * Find the player on the board and gives him/her the proper amount of bullets for
	 * debug mode.
	 */
	public void setDebugPlayer() {
		for (MapObjects[] m : grid) {
			for (MapObjects o : m) {
				if (o != null && o.getType().equals("Player")) {
					((Player) o).setBullets(42);
					((Player) o).setLives(42);
				}
			}
		}
	}

	/**
	 * When the player exits debug mode, this method undoes any changes and reverts the player
	 * back to the state he/she was in before debug was activated. However, the row and column
	 * (the position of the player) will not change in the undo process; he/she will stay in whatever
	 * spot he/she is currently in.
	 */
	public void undoDebugPlayer(int row, int col, char direction, int bullets, int lives) {
		((Player) grid[row][col]).setRow(row);
		((Player) grid[row][col]).setColumn(col);
		((Player) grid[row][col]).setDirection(direction);
		((Player) grid[row][col]).setBullets(bullets);
		((Player) grid[row][col]).setLives(lives);
	}

	/**
	 * Method used to get the player out if enemies trap him/her, eliminating any valid moves.
	 * The player will be spawned back at the start with one less life. Enemies will also be moved
	 * if in the way to avoid consecutive deaths/stuck situations.
	 */
	public void unstuck() {
		Player player = getPlayer();
		if(player.getRow() == 8 && player.getColumn() == 0) {
			if(checkExists(7, 0) && grid[7][0].getType().equals("Enemy")) {
				grid[7][0] = null;
				spawnEnemies(1);
			}
			if(checkExists(8, 1) && grid[8][1].getType().equals("Enemy")) {
				grid[8][1] = null;
				spawnEnemies(1);
			}
		}
		
		moveEnemyFromSpawn();
		
		grid[player.getRow()][player.getColumn()] = null;
		player.setRow(8);
		player.setColumn(0);
		grid[8][0] = player;
		getPlayer().setLives(player.getLives()-1);
	}

	/**
	 * Scans through the board looking for an object of whatever String argument is
	 * passed for the type parameter. If found, the method returns true; otherwise, it
	 * returns false.
	 */
	public boolean scanFor(String type) {
		for (MapObjects[] m : grid) {
			for (MapObjects o : m) {
				if (o != null && o.getType().equals(type)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks if an object exists in grid[i][j].
	 */
	public boolean checkExists(int i, int j) {
		if (grid[i][j] == null)
			return false;
		else
			return true;
	}

	/**
	 * Used in the process of looking to make sure a room is not in the way of the player's
	 * line of sight.
	 */
	public boolean checkForRooms(int i, int j) {
		try {
			if (checkExists(i, j) && (grid[i][j].getType().equals("Room") || grid[i][j].getType().equals("Briefcase")))
				return true;
			else
				return false;
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}

	/**
	 * Checks if the briefcase is still on the board. If the player is occupying the space where the briefcase
	 * is, then the player logically has beaten the level, and thus the method will return true. Therefore,
	 * if the method can find any instance in the board that is of type Briefcase or is a room that has the briefcase,
	 * then the player has not won, and the method will return false.
	 */
	public boolean checkWin() {
		boolean win = true;
		for (int i = 1; i < 8; i += 3) {
			for (int j = 1; j < 8; j += 3) {
				if (grid[i][j].getType().equals("Room") && ((Room) grid[i][j]).hasBriefcase())
					win = false;
				if (grid[i][j].getType().equals("Briefcase"))
					win = false;
			}
		}
		return win;
	}

	/**
	 * Scans the board and checks if there is at least one visible enemy.
	 */
	public boolean enemyIsVisible() {
		for (MapObjects[] m : grid) {
			for (MapObjects o : m) {
				if (o != null && o.getType().equals("Enemy") && o.isVisible())
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Scans the board to count how many enemies there are. Used when generating the
	 * Data object to save the game.
	 */
	public int enemyCount() {
		int enemyCounter = 0;
		for (MapObjects[] m : grid) {
			for (MapObjects o : m) {
				if (o != null && o.getType().equals("Enemy"))
					enemyCounter++;
			}
		}
		return enemyCounter;
	}

	/**
	 * Finds the player on the board and returns said Player object.
	 */
	public Player getPlayer() {
		Player player = null;
		for (MapObjects[] m : grid) {
			for (MapObjects o : m) {
				if (o != null && o.getType().equals("Player"))
					player = (Player) o;
			}
		}

		return player;
	}

	//// Getters and Setters //////////////////////////////////////
	
	public int getBullets() {
		return getPlayer().getBullets();
	}

	public int getLives() {
		return getPlayer().getLives();
	}

	public String getType(int i, int j) {
		if (grid[i][j] == null)
			return " ";
		else
			return grid[i][j].getType();
	}

	public MapObjects getUnit(int row, int col) {
		return grid[row][col];
	}

	public boolean isVisible(int i, int j) {
		return grid[i][j].isVisible();
	}

	/**
	 * Sets the visibility of an object on the board, based on whether or not
	 * the user can see it.
	 */
	public void setVisibility(int row, int column, boolean visible) {
		grid[row][column].setVisible(visible);
	}

	public void setInvincibility(boolean on) {
		if (on) {
			for (MapObjects[] m : grid) {
				for (MapObjects o : m) {
					if (o != null && o.getType().equals("Player")) {
						((Player) o).setInvincible(42);
					}
				}
			}
		} else {
			for (MapObjects[] m : grid) {
				for (MapObjects o : m) {
					if (o != null && o.getType().equals("Player")) {
						((Player) o).setInvincible(0);
					}
				}
			}
		}
	}
}