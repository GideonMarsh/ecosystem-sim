
public class Position {
	public int xPosition;
	public int yPosition;
	
	public Position() {
		xPosition = 0;
		yPosition = 0;
	}
	
	public Position(int x, int y) {
		xPosition = x;
		yPosition = y;
	}
	
	public boolean sameAs(Position p) {
		return (xPosition == p.xPosition) && (yPosition == p.yPosition);
	}
	
	// returns true if specified position is at a certain distance or closer 
	public boolean isWithinRange(Position p, int distance) {
		return Math.abs(p.xPosition - xPosition) + Math.abs(p.yPosition - yPosition) <= distance;
	}
	
	// returns the position that is closer to this position
	// if distance equal, returns first position parameter
	// if one position is null, return other position
	// if both positions are null, return null
	public Position closest(Position p1, Position p2) {
		if (p1 == null) return p2;
		if (p2 == null) return p1;
		
		if (Math.abs(p1.xPosition - xPosition) + Math.abs(p1.yPosition - yPosition) <= Math.abs(p2.xPosition - xPosition) + Math.abs(p2.yPosition - yPosition)) {
			return p1;
		}
		return p2;
	}
	
	// returns a random position within a specified distance of this position
	// can not return this position
	public Position randomWithinDistance(int distance) {
		if (distance == 0) return null;
		Position pos;
		do {
			double w = distance * Math.sqrt(Math.random());
			double t = 2 * Math.PI * Math.random();
			double x = w * Math.cos(t);
			double y = w * Math.sin(t);
			pos = new Position((int) Math.round(x + xPosition), (int) Math.round(y + yPosition));
		}
		while (this.sameAs(pos));
		
		while (Math.abs(pos.xPosition - xPosition) + Math.abs(pos.yPosition - yPosition) > distance) {
			if (Math.random() < 0.5) {
				pos.xPosition -= Integer.signum(pos.xPosition - xPosition);
			}
			else {
				pos.yPosition -= Integer.signum(pos.yPosition - yPosition);
			}
		}
		return pos;
	}
	
	public String toString() {
		return xPosition + ", " + yPosition;
	}
}
