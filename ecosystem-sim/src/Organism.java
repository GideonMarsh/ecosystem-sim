
public class Organism {
	private Position position;
	private int walkingSpeed;
	
	// Creating an organism from nothing
	public Organism() {
		position = new Position(0,0);
		walkingSpeed = 1;
	}
	
	// New organisms are created from parent(s) parameters (genes)
	public Organism(Organism parent) {
		
	}
	
	public Position getPosition() {
		return position;
	}
	
	// Moves the organism towards the destination position as directly as possible
	// Number of squares moved determined by speed
	public void move(Position destination) {
		if (walkingSpeed == 0) return;
		
		int xDif = destination.getXPosition() - position.getXPosition();
		int yDif = destination.getYPosition() - position.getYPosition();
		if (Math.abs(xDif) > Math.abs(yDif)) {
			position.changeXPosition(Integer.signum(xDif) * walkingSpeed);
		}
		else {
			position.changeYPosition(Integer.signum(yDif) * walkingSpeed);
		}
	}
}
