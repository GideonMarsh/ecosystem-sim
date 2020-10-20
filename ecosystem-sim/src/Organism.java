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
	
	// a basic representation of the organism's surroundings
	// outer array index is y position, inner array index is x position
	private Organism[][] mentalMap;
	
	// Creating an organism from nothing
	public Organism() {
		position = new Position((int) Math.round(Math.random() * 39),(int) Math.round(Math.random() * 39));
		walkingSpeed = 1;
		mentalMap = new Organism[40][40];
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
		mentalMap = new Organism[40][40];
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
	
	public boolean isAPlant() {
		return isAPlant;
	}
	
	public int getOrganismType() {
		return organismType;
	}
	
	// The AI that determines Organism behavior
	public void determineNextAction(ArrayList<Organism> organisms) {
		perceive(organisms);
		
		switch (organismType) {
		case 1:
			
			if (destination != null && destinationObject != null && (mentalMap[destination.getYPosition()][destination.getXPosition()] == null || ! mentalMap[destination.getYPosition()][destination.getXPosition()].equals(destinationObject))) {
				destination = null;
				destinationObject = null;
			}
			for (int i = 0; i < 40; i++) {
				for (int j = 0; j < 40; j++) {
					if (mentalMap[i][j] != null && mentalMap[i][j].getOrganismType() == 2) {
						if (destination == null || Math.abs(i - position.getYPosition()) + Math.abs(j - position.getXPosition()) < Math.abs(destination.getXPosition() - position.getXPosition()) + Math.abs(destination.getYPosition() - position.getYPosition())) {
							destination = new Position(j,i);
							destinationObject = mentalMap[i][j];
						}
					}
				}
			}
			break;
		case 2:
			if (mentalMap[position.getYPosition()][position.getXPosition()] != null) {
				position.setXPosition((int) Math.round(Math.random() * 39));
				position.setYPosition((int) Math.round(Math.random() * 39));
			}
			break;
		}
		
		move();
	}
	
	// Moves the organism towards its destination position as directly as possible
	// Number of squares moved determined by speed
	private void move() {
		if (destination == null || walkingSpeed == 0) return;
		
		// repeat a number of times equal to walking speed
		for (int i = 0; i < walkingSpeed; i++) {
			Position newPosition = new Position(position.getXPosition(), position.getYPosition());
			
			int xDif = destination.getXPosition() - position.getXPosition();
			int yDif = destination.getYPosition() - position.getYPosition();
			
			/*
			boolean preferX;
			
			if (xDif != 0 && yDif != 0) {
				preferX = Math.round(Math.random()) == 0;
			}*/
			
			if (Math.abs(xDif) > Math.abs(yDif)) {
				newPosition.changeXPosition(Integer.signum(xDif));
			}
			else {
				newPosition.changeYPosition(Integer.signum(yDif));
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
	private void perceive(ArrayList<Organism> organisms) {
		mentalMap = new Organism[40][40];
		for (Organism organism : organisms) {
			if (! organism.equals(this)) {
				mentalMap[organism.getPosition().getYPosition()][organism.getPosition().getXPosition()] = organism;
			}
		}
	}
}
