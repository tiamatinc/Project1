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
	private int playerCounter = 0;
	private int enemyCounter = 0;
	private boolean found1 = false;
	private boolean found2 = false;
	private int infoBox;
	private char pendingDir;
	private char movingDir;
	private char animDir;
	
	
	public Data(int level, int enemiesAlive, Frame frame) {
		this.level = level;
		enemiesLoc = new int[enemiesAlive*2];
		enemyDirection = new char[enemiesAlive];
		this.playerCounter = frame.getPlayerCounter();
		this.enemyCounter = frame.getEnemyCounter();
		this.found1 = frame.isFound1();
		this.found2 = frame.isFound2();
		this.infoBox = frame.getInfoBox();
		this.pendingDir = frame.getPendingDir();
		this.movingDir = frame.getMovingDir();
		this.animDir = frame.getAnimDir();
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public char[] getEnemyDirection() {
		return enemyDirection;
	}

	public int getPlayerCounter() {
		return playerCounter;
	}

	public int getEnemyCounter() {
		return enemyCounter;
	}

	public boolean isFound1() {
		return found1;
	}

	public boolean isFound2() {
		return found2;
	}

	public int getInfoBox() {
		return infoBox;
	}

	public char getPendingDir() {
		return pendingDir;
	}

	public char getMovingDir() {
		return movingDir;
	}

	public char getAnimDir() {
		return animDir;
	}	
}
