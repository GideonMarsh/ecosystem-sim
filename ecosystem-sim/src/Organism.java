import java.awt.Color;
import java.util.ArrayList;

public class Organism {
	private Position position;
	private int walkingSpeed;
	
	private Position destination;
	private Organism destinationObject;
	
	private int organismType;
	private boolean isAPlant;
	
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
	public void nextAction(Environment environment) {
		perceive(environment);
		
		switch (organismType) {
		case 1:
			if (mentalMap == null) return;
			
			/*
			if (destination != null && destinationObject != null && (mentalMap[destination.yPosition][destination.xPosition] == null || ! mentalMap[destination.yPosition][destination.xPosition].equals(destinationObject))) {
				destination = null;
				destinationObject = null;
			}
			for (int i = 0; i < 40; i++) {
				for (int j = 0; j < 40; j++) {
					if (mentalMap[i][j] != null && mentalMap[i][j].getOrganismType() == 2) {
						if (destination == null || Math.abs(i - position.yPosition) + Math.abs(j - position.xPosition) < Math.abs(destination.xPosition - position.xPosition) + Math.abs(destination.yPosition - position.yPosition)) {
							destination = new Position(j,i);
							destinationObject = mentalMap[i][j];
						}
					}
				}
			}
			*/
			break;
		case 2:
			if (mentalMap == null) return;
			for (Organism organism : mentalMap) {
				if (/*! organism.equals(this) && */organism.getPosition().sameAs(position)) {
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
	
	// Moves the organism towards its destination position as directly as possible
	// Number of squares moved determined by speed
	private void move() {
		if (destination == null || walkingSpeed == 0) return;
		
		// repeat a number of times equal to walking speed
		for (int i = 0; i < walkingSpeed; i++) {
			Position newPosition = new Position(position.xPosition, position.yPosition);
			
			int xDif = destination.xPosition - position.xPosition;
			int yDif = destination.yPosition - position.yPosition;
			
			/*
			boolean preferX;
			
			if (xDif != 0 && yDif != 0) {
				preferX = Math.round(Math.random()) == 0;
			}*/
			
			if (Math.abs(xDif) > Math.abs(yDif)) {
				newPosition.xPosition += (Integer.signum(xDif));
			}
			else {
				newPosition.yPosition += (Integer.signum(yDif));
			}
			
			position = newPosition;
			
			if (position.sameAs(destination)) {
				destination = null;
				destinationObject = null;
				return;
			}
		}
	}
	
	// updates the mental map of the organism
	private void perceive(Environment environment) {
		mentalMap = environment.resolvePerception(this);
	}
}
