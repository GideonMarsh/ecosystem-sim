
public class Position {
	private int xPosition;
	private int yPosition;
	
	public Position() {
		xPosition = 0;
		yPosition = 0;
	}
	
	public Position(int x, int y) {
		xPosition = x;
		yPosition = y;
	}
	
	public void setXPosition(int newXPos) {
		xPosition = newXPos;
	}
	
	public void setYPosition(int newYPos) {
		yPosition = newYPos;
	}
	
	public int getXPosition() {
		return xPosition;
	}
	
	public int getYPosition() {
		return yPosition;
	}
	
	public void changeXPosition(int change) {
		xPosition += change;
	}
	
	public void changeYPosition(int change) {
		yPosition += change;
	}
}
