
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
}
