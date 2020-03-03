import javax.swing.JLabel;

public final class Bullet extends JLabel {
	private Resources imageRes;
	int waitCounter = 0;
	int distanceToEdge = 0;
	boolean toRemove = false;
	
	Bullet(Resources res) {
		imageRes = res;
		setIcon(imageRes.bulletImage);
	}
	
	public void move(int directionSign) {
		setLocation((getX() + 2 * directionSign), getY());
	}
}