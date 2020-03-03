
import java.awt.Point;

import javax.swing.*;

interface LevelCompletionable {
	void onLevelCompletion(JFrame frame);
}

public class StartGame implements LevelCompletionable {
	private static Point initialLocation = new Point(300, 300);
	private static JFrame gameFieldFrame;
	private final static StartGame startGame = new StartGame();
	private final static Resources res = new Resources();
	
	public static void main(String[] args) {
		setLook();
		gameFieldFrame = new GameFieldFrame(initialLocation, res, startGame);
	}
	
	private static void setLook() {
		try {
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.print(e);
		}
	}
	
	public void onLevelCompletion(JFrame frame) {
		gameFieldFrame = frame;
		initialLocation = gameFieldFrame.getLocation();
		gameFieldFrame.dispose();
		gameFieldFrame = new GameFieldFrame(initialLocation, res, this);
	}
}





















