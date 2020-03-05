import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JFrame;

interface LevelCompletionable {
	void onLevelCompletion(Component component);
}

public class RootFrame extends JFrame implements LevelCompletionable {
	private final Dimension fieldSize = new Dimension(350, 450);
	private Component gameFieldFrame;
	private final Resources res = new Resources();
	
	RootFrame(Point location) {
		setLocation(location);
		setSize(fieldSize);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(null);
		setResizable(false);
		setFocusable(false);;
		add(componentInitialization());
		setVisible(true);
	}
	
	private Component componentInitialization() {
		return new GameField(res, this);
	}
	
	public void onLevelCompletion(Component component) {
		gameFieldFrame = component;
		this.remove(gameFieldFrame);
		gameFieldFrame = componentInitialization();
		this.add(gameFieldFrame);
		gameFieldFrame.requestFocus();
		this.repaint();
	}
}
