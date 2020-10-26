import java.awt.Color;
import java.util.ArrayList;

public class Organism {
	private Position position;
	private int walkingSpeed;
	
	// the target of AI behaviors such as eating
	private Organism target;
	
	private int organismType;
	private int foodChainIdentifier;	// 0 = plant, 1 = herbivore, 2 = omnivore, 3 = carnivore
	private boolean isACorpse;
	private boolean isMarkedForRemoval;
	
	private boolean isLarge;			// large organisms take up multiple layers
	
	private double nutrition;
	private PreyValues preyValues;		// the value of eating certain prey
	private double upkeep; 				// nutrition lost each game tick
	private int hungryValue;			// the value at which organism will seek out food
	
	private int reproductionCost;		// the nutrition cost for reproduction
	private int reproductionThreshold;	// the minimum nutrition value to begin reproduction
	private int maxOffspring;			// the maximum amount of offspring this organism can have
	private int numberOfOffspring;		// the current amount of offspring this organism has had
	private int litterSize;				// the number of offspring birthed at one time
	
	// hp values represent relative size as well as health
	private int hp;
	private int maxhp;
	private int maxhpThreshold;		// maximum total hp when an adult
	
	private double age;
	private int maxAge;
	
	// Amount of hp to remove from the target of an attack from this organism
	private int attackPower;
	
	/*
	 *  represents the organism AI's current goals
	 *  
	 *  0 = idle
	 *  1 = locating food
	 *  2 = attempting to reproduce
	 */
	private int currentBehavior;
	
	private Color color;
	
	// New organisms are created from parent(s) parameters (genes)
	public Organism(Organism parent) {
		position = new Position(0,0);
		walkingSpeed = parent.walkingSpeed;
		organismType = parent.organismType;
		foodChainIdentifier = parent.foodChainIdentifier;
		color = parent.color;
		age = 0;
		currentBehavior = 0;
		maxAge = parent.maxAge;
		maxhpThreshold = parent.maxhpThreshold;
		maxhp = maxhpThreshold / 2;
		hp = maxhp;
		preyValues = parent.preyValues.copy();
		upkeep = parent.upkeep;
		hungryValue = parent.hungryValue;
		maxOffspring = parent.maxOffspring;
		litterSize = parent.litterSize;
		numberOfOffspring = 0;
		nutrition = hungryValue * (Math.random() + 0.5);
		attackPower = parent.attackPower;
		reproductionCost = (int) Math.round((100 * upkeep) * litterSize * Math.pow(0.9, litterSize - 1));
		reproductionThreshold = reproductionCost + hungryValue;
		isLarge = parent.isLarge;
	}
	
	// Creates a new organism from nothing of the specified type
	public Organism(int type) {
		position = new Position(0,0);
		age = 0;
		currentBehavior = 0;
		preyValues = new PreyValues();
		numberOfOffspring = 0;
		isLarge = false;

		switch (type) {
		case 2:
			walkingSpeed = 1;
			organismType = type;
			foodChainIdentifier = 1;
			color = new Color(0,0,200);
			maxAge = 10;
			maxhpThreshold = 200;
			
			preyValues.addPreyValue(1, 3.0f);
			preyValues.addPreyValue(4, 2.0f);
			
			upkeep = 4;
			maxOffspring = 3;
			litterSize = 1;
			
			attackPower = 5;
			break;
		
		case 3:
			walkingSpeed = 1;
			organismType = type;
			foodChainIdentifier = 3;
			color = new Color(200,0,0);
			maxAge = 10;
			maxhpThreshold = 200;
			
			preyValues.addPreyValue(2, 6.0f);
			
			upkeep = 4;
			maxOffspring = 2;
			litterSize = 2;
			
			attackPower = 30;
			break;
		
		case 4:
			walkingSpeed = 0;
			organismType = type;
			foodChainIdentifier = 0;
			color = new Color(0,120,0);
			maxAge = 30;
			maxhpThreshold = 500;
			
			preyValues.addPreyValue(0, 1.0f);
			
			upkeep = 10;
			maxOffspring = 10;
			litterSize = 1;
			
			isLarge = true;
			attackPower = 1;
			break;
			
		case 1:
		default:
			walkingSpeed = 0;
			organismType = type;
			foodChainIdentifier = 0;
			color = new Color(0,200,0);
			maxAge = 5;
			maxhpThreshold = 50;
			
			preyValues.addPreyValue(0, 2.0f);
			
			upkeep = 2;
			maxOffspring = 10;
			litterSize = 1;
			
			attackPower = 1;
		}
		maxhp = maxhpThreshold / 2;
		hp = maxhp;
		hungryValue = (int) Math.round(upkeep * 50);
		nutrition = hungryValue * 1.5;
		reproductionCost = (int) Math.round((100 * upkeep) * litterSize * Math.pow(0.9, litterSize - 1));
		reproductionThreshold = reproductionCost + hungryValue;
	}
	
	public int getOrganismType() {
		return organismType;
	}
	
	public void setOrganismType(int newType) {
		organismType = newType;
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
	
	public int getFoodChainIdentifier() {
		return foodChainIdentifier;
	}
	
	public boolean isACorpse() {
		return isACorpse;
	}
	
	public boolean isLarge() {
		return isLarge;
	}
	
	public boolean isMarkedForRemoval() {
		return isMarkedForRemoval;
	}
	
	public void setAsMarkedForRemoval() {
		isMarkedForRemoval = true;
	}
	
	// Attempts to lower the organism's hp by specified amount
	// If organism hp is less than amount, only remove available hp and kill organism
	// Returns actual hp loss of organism
	public int takeHP(int amount) {
		if (hp > amount) {
			hp -= amount;
			return amount;
		}
		else {
			int hpTaken = hp;
			die();
			return hpTaken;
		}
	}
	
	public void loseHP(int amount) {
		hp -= amount;
		if (hp <= 0) die();
	}
	
	// The AI that determines and executes Organism behavior
	// Behavior is split up among helper methods
	public void nextAction() {
		growOlder();
			
		if (! isACorpse) {
			
			// decide what current behavior of organism should be
			if (nutrition < hungryValue) {
				currentBehavior = 1;
			}
			else {
				// organisms can only reproduce during adulthood
				if (maxhp == maxhpThreshold && numberOfOffspring < maxOffspring) {
					// organisms will only reproduce at regular intervals in their lives
					if (age > (maxAge / 5) + (maxAge * (3.0/5.0) / maxOffspring) * numberOfOffspring) {
						// organisms will only reproduce if they have sufficient nutrition
						if (nutrition < reproductionThreshold) {
							currentBehavior = 1;
						}
						else {
							currentBehavior = 2;
						}
					}
					else {
						currentBehavior = 0;
					}
				}
				else {
					currentBehavior = 0;
				}
			}

			// perform actions according to current behavior
			perceive();
			move();
			if (foodChainIdentifier == 0) photosynthesize();
			else {
				if (currentBehavior == 1 && target != null && position.isWithinRange(target.position, 1)) {
					if (target.foodChainIdentifier == 0 || target.isACorpse) {
						eat(target);
					}
					else {
						target.loseHP(attackPower);
					}
				}
			}
			if (currentBehavior == 2) {
				reproduce();
			}
		}
	}
	
	private void move() {
		if (walkingSpeed != 0) {
			switch (currentBehavior) {

			case 1:
				if (target == null) {
					for (int i = 0; i < walkingSpeed; i++) {
						Environment.getEnvironment().moveOrganism(this, position.randomWithinDistance(1));
					}
					return;
				}
				
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
					if (position.isWithinRange(target.position, 1)) break;
					
					xDif = target.position.xPosition - position.xPosition;
					yDif = target.position.yPosition - position.yPosition;
					
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
						if (Environment.getEnvironment().moveOrganism(this, chosenMoves[j])) break;
					}
				}
				break;
			case 2:
				// if reproduce sexually, look for mate
				break;
			default:
				for (int i = 0; i < walkingSpeed; i++) {
					Environment.getEnvironment().moveOrganism(this, position.randomWithinDistance(1));
				}
			}
		}
	}
	
	private void die() {
		if (foodChainIdentifier == 0 || isACorpse) {
			isMarkedForRemoval = true;
		}
		else {
			isACorpse = true;
			hp = maxhp;
			color = Color.black;
			Environment.getEnvironment().moveToCorpseLayer(this);
		}
	}
	
	private void growOlder() {
		age  = Math.round((age * Environment.YEAR_LENGTH) + 1) / Environment.YEAR_LENGTH;
		if (expendEnergy(upkeep)) return;
		
		// Corpses starve (functionally decompose) at double speed
		if (isACorpse) {if (expendEnergy(upkeep)) return;}
		
		// Organisms can expend nutrition to recover hp slowly over time, if nutrition is high enough
		if (hp < maxhp && nutrition > (hungryValue / 2) + (upkeep * 3)) {
			if (expendEnergy(upkeep)) return;
			hp = Math.min(maxhp, hp + maxhp / 40);
		}
		
		if (! isACorpse && age > maxAge) {
			this.die();
			return;
		}
		if (age < maxAge / 5) {
			int formerMaxhp = maxhp;
			maxhp = (int) Math.round((maxhpThreshold / 2.0) + (maxhpThreshold / 2.0) * (age / (maxAge / 5.0)));
			hp += maxhp - formerMaxhp;
		}
		else {
			if (age >= maxAge * (4.0/5.0)) {
				maxhp = (int) Math.round((maxhpThreshold / 2.0) + (maxhpThreshold / 2.0) * ((maxAge - age) / (maxAge / 5.0)));
				if (hp > maxhp) hp = maxhp;
			}
			else {
				maxhp = maxhpThreshold;
			}
		}
	}
	
	private void reproduce() {
		if (foodChainIdentifier == 0) {
			for (int i = 0; i < litterSize; i++) {
				Organism offspring = new Organism(this);
				
				Environment.getEnvironment().addOrganism(offspring, position.randomWithinDistance(3));
			}
			numberOfOffspring += litterSize;
			expendEnergy(reproductionCost);
		}
		else {
			for (int i = 0; i < litterSize; i++) {
				Organism offspring = new Organism(this);
				
				Position[] birthPositions = new Position[4];
				birthPositions[0] = new Position(position.xPosition + 1, position.yPosition);
				birthPositions[1] = new Position(position.xPosition, position.yPosition + 1);
				birthPositions[2] = new Position(position.xPosition - 1, position.yPosition);
				birthPositions[3] = new Position(position.xPosition, position.yPosition - 1);
				
				for (int j = 0; j < birthPositions.length; j++) {
					if (Environment.getEnvironment().addOrganism(offspring, birthPositions[j])) {
						expendEnergy(reproductionCost / litterSize);
						numberOfOffspring++;
						break;
					}
				}
			}
		}
	}
	
	private void eat(Organism prey) {
		nutrition += prey.takeHP(maxhpThreshold / 5) * preyValues.getPreyValue(prey.getOrganismType());
	}
	
	private void photosynthesize() {
		nutrition += ((hp / 10) * preyValues.getPreyValue(0)) + Math.max(0, maxhpThreshold * (1 - (preyValues.getPreyValue(0)/2)) / 50);
	}
	
	// updates the mental map of the organism
	private void perceive() {
		if (foodChainIdentifier == 0) return;
		ArrayList<Organism> mentalMap = Environment.getEnvironment().resolvePerception(this);
		
		target = null;
		switch (currentBehavior) {
		case 1:
			for (Organism organism : mentalMap) {
				if (preyValues.isPrey(organism.getOrganismType())) {
					if (target == null) target = organism;
					else {
						// if any target is found immediately next to this organism, choose it and stop looking
						if (position.isWithinRange(organism.position, 1)) {
							target = organism;
							break;
						}
						/*
						 * Organism will switch targets only under the following circumstances:
						 * Organism is starving and new target is closer
						 * Organism is not starving and new target is more valuable
						 * Organism is not starving, new target is equally valuable, and new target is closer
						 * 
						 * Additionally, carnivores/omnivores will prioritize corpses over live prey unless they're starving
						 */
						boolean c = position.closerThan(organism.position, target.position);
						boolean ev = preyValues.getPreyValue(target.getOrganismType()) == preyValues.getPreyValue(organism.getOrganismType());
						boolean v = preyValues.getPreyValue(target.getOrganismType()) < preyValues.getPreyValue(organism.getOrganismType());
						boolean s = nutrition <= upkeep * 5;

						if ((c && s) || (!s && (v || (ev && c)))) {
							if (! target.isACorpse || organism.isACorpse || s) {
								target = organism;
							}
						}
					}
				}
			}
			break;
		case 2:
			break;
		default:
		}
	}
	
	// subtracts energy spent from nutrition value
	// if nutrition value reaches 0, all other expenditures come directly from hp
	// returns true if organism dies, false if otherwise
	private boolean expendEnergy(double amount) {
		nutrition -= amount;
		if (nutrition < 0) {
			hp += nutrition;
			nutrition = 0;
		}
		if (hp <= 0) {
			die();
			return true;
		}
		return false;
	}
}
