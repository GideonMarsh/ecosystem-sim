import java.awt.Color;
import java.util.ArrayList;

public class Organism {
	private Position position;
	private int walkingSpeed;
	
	private int organismType;
	private boolean isAPlant;
	
	private int hp;
	private int maxhp;
	
	private Color color;
	
	private ArrayList<Organism> mentalMap;
	
	// Creating an organism from nothing
	public Organism() {
		position = new Position((int) Math.round(Math.random() * 39),(int) Math.round(Math.random() * 39));
		walkingSpeed = 1;
		mentalMap = new ArrayList<Organism>();
		organismType = 1;
		isAPlant = false;
		color = new Color(0,100,0);
	}
	
	// New organisms are created from parent(s) parameters (genes)
	public Organism(Organism parent) {
		
	}
	
	// Creates a new organism of the specified type
	public Organism(int type) {
		position = new Position((int) Math.round(Math.random() * 39),(int) Math.round(Math.random() * 39));
		walkingSpeed = 1;
		mentalMap = new ArrayList<Organism>();
		organismType = type;
		isAPlant = false;
		if (type == 1) {
			color = new Color(0,0,100);
		}
		else {
			color = new Color(0,100,0);
			isAPlant = true;
		}
	}
	
	public Color getColor() {
		return color;
	}
	
	public Position getPosition() {
		return position;
	}
	
	public void setPosition(Position newPosition){;
		position = newPosition;
	}
	
	public boolean isAPlant() {
		return isAPlant;
	}
	
	public int getOrganismType() {
		return organismType;
	}
	
	// The AI that determines and executes Organism behavior
	// Behavior is split up among helper methods
	public void nextAction(Environment environment) {
		perceive(environment);
		move(environment);
		
	}
	
	private void move(Environment environment) {
		switch (organismType) {
		case 1:
			if (mentalMap == null || walkingSpeed == 0) return;
			
			Position destination = null;
			for (Organism organism : mentalMap) {
				if (organism.getOrganismType() == 2) {
					destination = position.closest(destination,organism.getPosition());
				}
			}
			
			if (destination == null) return;
			
			/*
			 * Organisms move cardinally one square at a time towards their destinations
			 * Their moves are chosen sequentially from the chosenMoves array
			 * If no valid moves are found, organism remains stationary
			 * If the organism is alinged with its destination on one axis, it will first try
			 * to move directly towards its destination, and if it fails it will attempt to
			 * move to the side.
			 * If the organism is not aligned with its destination on an axis, it will first
			 * randomly choose which axis to move along and then try to move along that axis
			 * first and the other axis second; if neither work, then it tries the opposite
			 * direction on the non-chosen axis
			 */
			int xDif, yDif, rand;
			Position[] chosenMoves = new Position[3];
			
			rand = Math.round(Math.random()) == 0 ? 1 : -1;
			
			for (int i = 0; i < walkingSpeed; i++) {
				if (position.sameAs(destination)) break;
				
				xDif = destination.xPosition - position.xPosition;
				yDif = destination.yPosition - position.yPosition;
				
				if (xDif == 0) {
					chosenMoves[0] = new Position(position.xPosition, position.yPosition + Integer.signum(yDif));
					chosenMoves[1] = new Position(position.xPosition + rand, position.yPosition);
					chosenMoves[2] = new Position(position.xPosition - rand, position.yPosition);
				}
				if (yDif == 0) {
					chosenMoves[0] = new Position(position.xPosition + Integer.signum(xDif), position.yPosition);
					chosenMoves[1] = new Position(position.xPosition, position.yPosition + rand);
					chosenMoves[2] = new Position(position.xPosition, position.yPosition - rand);
				}
				if (xDif != 0 && yDif != 0) {
					if (rand == 1) {
						chosenMoves[0] = new Position(position.xPosition, position.yPosition + Integer.signum(yDif));
						chosenMoves[1] = new Position(position.xPosition + Integer.signum(xDif), position.yPosition);
						chosenMoves[2] = new Position(position.xPosition - Integer.signum(xDif), position.yPosition);
					}
					else {
						chosenMoves[0] = new Position(position.xPosition + Integer.signum(xDif), position.yPosition);
						chosenMoves[1] = new Position(position.xPosition, position.yPosition + Integer.signum(yDif));
						chosenMoves[2] = new Position(position.xPosition, position.yPosition - Integer.signum(yDif));
					}
				}
				
				for (int j = 0; j < chosenMoves.length; j++) {
					if (environment.moveOrganism(this, chosenMoves[j])) break;
				}
			}
			break;
		case 2:
			if (mentalMap == null) return;
			for (Organism organism : mentalMap) {
				if (! organism.equals(this) && organism.getPosition().sameAs(position)) {
					Position newPosition;
					do {
						newPosition = new Position((int) Math.round(Math.random() * 39),(int) Math.round(Math.random() * 39));
					}
					while (! environment.moveOrganism(this, newPosition));
					break;
				}
			}
		}
	}
	
	// updates the mental map of the organism
	private void perceive(Environment environment) {
		mentalMap = environment.resolvePerception(this);
	}
}
