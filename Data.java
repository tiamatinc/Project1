package project1;

import java.io.Serializable;

public class Data implements Serializable {
	private static final long serialVersionUID = -3341494470380457327L;
	private int level;
	private int[] playerLoc = new int[2];
	private int lives = 0;
	private int bullets = 0;
	private int invincible = 0;
	private char playerDirection = 'u';
	private int[] enemiesLoc = null;
	private char[] enemyDirection = null;
	private int[] ammoLoc = null;
	private int[] shieldLoc = null;
	private int[] locatorLoc = null;
	private int[] briefcaseLoc = new int[2];
	// GUI /////////////////////////////////
	Frame frame = null;
	
	
	public Data(int level, int enemiesAlive, Frame frame) {
		this.level = level;
		enemiesLoc = new int[enemiesAlive*2];
		enemyDirection = new char[enemiesAlive];
		this.frame = frame;
	}

	public void setPlayerLoc(int row, int column) {
		playerLoc[0] = row;
		playerLoc[1] = column;
	}
	
	public void setLives(int lives) {
		this.lives = lives;
	}
	
	public void setBullets(int bullets) {
		this.bullets = bullets;
	}
	
	public void setInvincible(int invincible) {
		this.invincible = invincible;
	}
	
	public void setPlayerDirection(char direction) {
		playerDirection = direction;
	}
	
	public void setEnemiesLoc(int row, int column, int enemyIndex) {
		enemiesLoc[enemyIndex * 2] = row;
		enemiesLoc[(enemyIndex * 2) + 1] = column;
	}
	
	public void setEnemiesDirection(char direction, int enemyIndex) {
		enemyDirection[enemyIndex] = direction;
	}
	
	public void setAmmoLoc(int row, int column) {
		ammoLoc = new int[2];
		ammoLoc[0] = row;
		ammoLoc[1] = column;
	}

	public void setShieldLoc(int row, int column) {
		shieldLoc = new int[2];
		shieldLoc[0] = row;
		shieldLoc[1] = column;
	}

	public void setLocatorLoc(int row, int column) {
		locatorLoc = new int[2];
		locatorLoc[0] = row;
		locatorLoc[1] = column;
	}
	
	public void setBriefcaseLoc(int row, int column) {
		briefcaseLoc[0] = row;
		briefcaseLoc[1] = column;
	}

	public int getLevel() {
		return level;
	}
	
	public int[] getPlayerLoc() {
		return playerLoc;
	}

	public int getLives() {
		return lives;
	}

	public int getBullets() {
		return bullets;
	}
	
	public int getInvincible() {
		return invincible;
	}
	
	public char getPlayerDirection() {
		return playerDirection;
	}

	public int[] getEnemiesLoc() {
		return enemiesLoc;
	}
	
	public char[] getEmemiesDirection() {
		return enemyDirection;
	}

	public int[] getAmmoLoc() {
		return ammoLoc;
	}

	public int[] getShieldLoc() {
		return shieldLoc;
	}

	public int[] getLocatorLoc() {
		return locatorLoc;
	}

	public int[] getBriefcaseLoc() {
		return briefcaseLoc;
	}	
}
