package project1;

/**
 * @author AAnthony
 * The enemy class holds the blueprints for constructing enemies that will be
 * placed on the GameBoard grid. Extends the MapObjects superclass so it can
 * be included in the GameBoard. Contains a field to determine whether or not the
 * enemy is alive, and whether or not the enemy is visible to the player.
 */
public class Enemy extends MapObjects {
	private int row;
	private int column;
	private boolean alive;
	private boolean hasMoved;
	private char direction;
	
	public Enemy(int row, int column) {
		super("Enemy");
		this.row = row;
		this.column = column;
		alive = true;
		hasMoved = false;
		direction = 's'; // Stationary
	}
	
	public Enemy(int row, int column, char direction) {
		super("Enemy");
		this.row = row;
		this.column = column;
		alive = true;
		hasMoved = false;
		this.direction = direction;
	}


	//// Getters ///////////////////////////////////////
	
	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

	public boolean isAlive() {
		return alive;
	}

	public boolean isVisible() {
		return visible;
	}

	public boolean hasMoved() {
		return hasMoved;
	}
	
	public char getDirection() {
		return direction;
	}

	//// Setters /////////////////////////////////////////
	
	public void setRow(int row) {
		this.row = row;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public void setHasMoved(boolean hasMoved) {
		this.hasMoved = hasMoved;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public void setDirection(char direction) {
		this.direction = direction;
	}
}
