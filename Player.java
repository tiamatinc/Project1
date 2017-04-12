package project1;

/**
 * @author AAnthony
 * This class holds the data used for the construction of a Player object.
 * The fields below are encapsulated within the Player object for the
 * GameEngine to use throughout the duration of the game. The class also
 * extends the superclass MapObjects so that it can be included in the
 * GameBoard grid along with other MapObjects.
 */
public class Player extends MapObjects {
	private int row;
	private int column;
	private int lives;
	private int bullets;
	private int invincible;
	private char direction;
	
	public Player() {
		super("Player");
		row = 8;
		column = 0;
		lives = 3;
		bullets = 1;
		invincible = 0;
		visible = true;
		direction = 'u';
	}
	
	public Player(int row, int column, int lives, int bullets, int invincible, char direction) {
		super("Player");
		this.row = row;
		this.column = column;
		this.lives = lives;
		this.bullets = bullets;
		this.invincible = invincible;
		visible = true;
		this.direction = direction;
	}
	
	//// Getters ///////////////////////////////////////
	
	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

	public int getLives() {
		return lives;
	}

	public int getBullets() {
		return bullets;
	}

	public int isInvincible() {
		return invincible;
	}

	public char getDirection() {
		return direction;
	}

	//// Setters ////////////////////////////////////////
	
	public void setRow(int row) {
		this.row = row;
	}

	public void setColumn(int column) {
		this.column = column;
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
	
	public void setDirection(char direction) {
		this.direction = direction;
	}
}
