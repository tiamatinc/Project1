/**
 * 
 */
package project1;

/**
 * @author AAnthony The MapObjects class is a superclass that keeps all the
 *         other game elements (Player, Enemy, Power-Ups, etc.) under one
 *         umbrella class, so that they can all be put into the same
 *         multi-dimensional array that is the GameBoard.
 */
public abstract class MapObjects {
	String type = "";
	boolean visible;

	public MapObjects(String type) {
		this.type = type;
		visible = true; // REMEMBER TO CHANGE BACK TO FALSE
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public String getType() {
		return type;
	}
}
