import javax.swing.JLabel;

enum CannonState {
	DEFAULT,
	RELOAD,
	TO_LEFT,
	TO_RIGHT;
}

public final class Cannon extends JLabel {
	private final int DOUBLE_ACCELERATION = 25;
	private final int QUADRUPLE_ACCELERATION = 70;
	private CannonState state;
	private Resources imageRes;
	int accelerator;
	
	Cannon(Resources res) {
		super();
		imageRes = res;
		state = CannonState.DEFAULT;
		setCannonImage();
		accelerator = 1;
	}
	
	public void setState(CannonState state) {
		this.state = state;
		setCannonImage();
	}
	
	private void setCannonImage() {
		switch (state) {
		case DEFAULT:
			setIcon(imageRes.defaultImage);
			break;
		case RELOAD:
			setIcon(imageRes.reloadImage);
			break;
		case TO_LEFT:
			setIcon(imageRes.toLeftImage);
			break;
		case TO_RIGHT:
			setIcon(imageRes.toRightImage);
			break;
		}
	}
	
	public void move(int keyPressedCounter) {
		int directionSign = 0;
		if (state.equals(CannonState.TO_LEFT)) {
			directionSign = -1;
		} else if (state.equals(CannonState.TO_RIGHT)) {
			directionSign = 1;
		}
		setLocation((getX() + directionSign * accelerator), getY());
		if (keyPressedCounter == DOUBLE_ACCELERATION) {
			accelerator *= 2;
		} else if (keyPressedCounter == QUADRUPLE_ACCELERATION) {
			accelerator *= 2;
		}
	}
	
	public void stay(int x) {
		setLocation(x, getY());
	}
}