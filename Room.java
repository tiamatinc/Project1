/**
 * 
 */
package project1;

/**
 * @author AAnthony
 *
 */
public class Room extends MapObjects {
	private boolean hasBriefcase = false;
	private boolean located = false;
	public Room() {
		super("Room");
		visible = true;
	}
	
	public Room(boolean variant) { // Used for creating the room w/ the briefcase.
		super("Room");
		visible = true;
		hasBriefcase = variant;
	}
	
	@Override
	public String getType() {
		if(hasBriefcase && located) // Delete && to view where the briefcase is!
			return "Briefcase";
		else
			return "Room";
	}

	public boolean hasBriefcase() {
		return hasBriefcase;
	}

	public void setLocated(boolean located) {
		this.located = located;
	}
}
