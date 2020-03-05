import java.awt.Point;

import javax.swing.*;

public class StartGame {
	private static Point initialLocation = new Point(300, 300);
	
	public static void main(String[] args) {
		setLook();
		new RootFrame(initialLocation);
	}
	
	private static void setLook() {
		try {
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.print(e);
		}
	}
}





















