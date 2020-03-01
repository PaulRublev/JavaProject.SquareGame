
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.*;
import java.util.LinkedList;

import javax.swing.*;

final class Resources {
	private final String iconPath = "icon/";
	final ImageIcon defaultImage;
	final ImageIcon toLeftImage;
	final ImageIcon toRightImage;
	final ImageIcon reloadImage;
	final ImageIcon bulletImage;
	final int SIDE_LENGTH = 64;
	
	Resources() {
		defaultImage = new ImageIcon(getClass().getResource(iconPath + "hold.png"));
		toLeftImage = new ImageIcon(getClass().getResource(iconPath + "toLeft.png"));
		toRightImage = new ImageIcon(getClass().getResource(iconPath + "toRight.png"));
		reloadImage = new ImageIcon(getClass().getResource(iconPath + "reload.png"));
		bulletImage = new ImageIcon(getClass().getResource(iconPath + "bullet.png"));
	}
}

enum CannonState {
	DEFAULT,
	RELOAD,
	TO_LEFT,
	TO_RIGHT;
}

enum StringConstants {
	RUINED("@@@"),
	RELOAD("reload"),
	MOVEMENT("movement");
	
	private String value;
	
	private StringConstants(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return this.value;
	}
}

final class Cannon extends JLabel {
	final private int DOUBLE_ACCELERATION = 25;
	final private int QUADRUPLE_ACCELERATION = 70;
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
	
	public void move(CannonState state, int keyPressedCounter) {
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
}

final class Target extends JButton {
	int waitCounter = 0;
	private int armor;
	boolean toLeft = false;
	
	Target(int armor) {
		super();
		this.armor = armor;
		displayArmor();
		setFocusable(false);
	}
	
	public void getDamage() {
		if (armor >= 0) {
			armor--;
			toLeft = !toLeft;
		}
		displayArmor();
	}
	
	private void displayArmor() {
		if (!isRuined()) {
			setBorder(BorderFactory.createMatteBorder(0, armor * 2, 
					armor * 2, armor * 2, Color.black));
			setText(String.valueOf(armor));	
		} else {
			setText(StringConstants.RUINED.toString());
		}
	}
	
	public boolean isRuined() {
		return armor < 0;
	}
}

final class Bullet extends JLabel {
	Resources imageRes;
	int waitCounter = 0;
	int edge = -2;
	
	Bullet(Resources res) {
		imageRes = res;
		setIcon(imageRes.bulletImage);
	}
}

final class GameFieldFrame extends JFrame implements KeyListener, ActionListener {
	final int RELOAD_TIME = 2;
	final int INITIAL_ARMOR = 2;
	final int RIGHTSIDE_CORRECTION = 17;
	final int DOWNSIDE_CORRECTION = 40;
	final int TARGET_SPEED_REDUCER = 10;
	final int BULLET_SPEED_REDUCER = 5;
	Dimension FieldSize = new Dimension(350, 450);
	public boolean toLeft = false;
	Cannon cannon;
	Target target;
	LinkedList<Bullet> bullets = new LinkedList<Bullet>();
	boolean bulletCtrl = false;
	LevelCompletionable completionListener;
	Timer reloadTimer;
	Timer movementTimer;
	Resources imageRes;
	private int keyPressedCounter = 0;
	
	GameFieldFrame(Point location, Resources res, LevelCompletionable completionListener) {
		this.completionListener = completionListener;
		imageRes = res;
		setLocation(location);
		setSize(FieldSize);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(null);
		setResizable(false);
		setVisible(true);
		reloadTimer = new Timer(RELOAD_TIME * 1000, this);
		reloadTimer.setActionCommand("reload");
		movementTimer = new Timer(1, this);
		movementTimer.setActionCommand("movement");
		addKeyListener(this);
		
		target = new Target(INITIAL_ARMOR);
		target.setBounds(getWidth() / 2 - imageRes.SIDE_LENGTH / 2, 0,
				imageRes.SIDE_LENGTH, imageRes.SIDE_LENGTH / 2);
		target.addActionListener(this);
		add(target);
		movementTimer.start();
		
		cannon = new Cannon(imageRes);
		cannon.setBounds(getWidth() / 2 - imageRes.SIDE_LENGTH / 2, 
				getHeight() - imageRes.SIDE_LENGTH - DOWNSIDE_CORRECTION,
				imageRes.SIDE_LENGTH, imageRes.SIDE_LENGTH);
		add(cannon);
		repaint();
	}
	
	public void keyReleased(KeyEvent ke) {
		if (!bulletCtrl) {
			cannon.setState(CannonState.DEFAULT);
			cannon.accelerator = 1;
			keyPressedCounter = 0;
		}
	}
	
	public void keyPressed(KeyEvent ke) {
		if (!bulletCtrl) {
			cannonControlButtons(ke);
		} else {
			bulletControlButtons(ke);
		}
	}
	
	private void cannonControlButtons(KeyEvent ke) {
		switch (ke.getExtendedKeyCode()) {
		case 37:
			cannon.setState(CannonState.TO_LEFT);
			if (cannon.getX() > 0) {
				keyPressedCounter++;
				cannon.move(CannonState.TO_LEFT, keyPressedCounter);
			} else {
				cannon.setLocation(0, cannon.getY());
			}
			break;
		case 39:
			cannon.setState(CannonState.TO_RIGHT);
			if (cannon.getX() < getWidth() - RIGHTSIDE_CORRECTION - imageRes.SIDE_LENGTH) {
				keyPressedCounter++;
				cannon.move(CannonState.TO_RIGHT, keyPressedCounter);
			} else {
				cannon.setLocation(getWidth() - RIGHTSIDE_CORRECTION - imageRes.SIDE_LENGTH,
						cannon.getY());
			}
			break;
		case 32:
			cannon.accelerator = 1;
			keyPressedCounter = 0;
			cannon.setState(CannonState.RELOAD);
			final Bullet bullet = new Bullet(imageRes);
			bullet.setBounds(cannon.getX(), cannon.getY() - imageRes.SIDE_LENGTH,
					imageRes.SIDE_LENGTH, imageRes.SIDE_LENGTH);
			add(bullet);
			bullets.add(bullet);
			bulletCtrl = true;
			repaint();
			reloadTimer.start();
			break;
		}
	}
	
	private void bulletControlButtons(KeyEvent ke) {
		final Bullet firstBullet = bullets.peekLast();
		switch (ke.getExtendedKeyCode()) {
		case 37:
			if (firstBullet.getX() > 0) {
				firstBullet.setLocation((firstBullet.getX() - 2),
						firstBullet.getY());
			} else {
				firstBullet.setLocation(0, firstBullet.getY());
			}
			break;
		case 39:
			if (firstBullet.getX() < getWidth() - RIGHTSIDE_CORRECTION - imageRes.SIDE_LENGTH) {
				firstBullet.setLocation((firstBullet.getX() + 2), firstBullet.getY());
			} else {
				firstBullet.setLocation(getWidth() - RIGHTSIDE_CORRECTION - imageRes.SIDE_LENGTH,
						firstBullet.getY());
			}
			break;
		}
	}
	
	public void keyTyped(KeyEvent ke) {
		//required by KeyListener interface, left empty as not used
	}
	
	public void actionPerformed(ActionEvent ae) {
		final String actionEventName = ae.getActionCommand();
		if (actionEventName.equalsIgnoreCase(StringConstants.RELOAD.toString())) {
			bulletCtrl = false;
			cannon.setState(CannonState.DEFAULT);
			reloadTimer.stop();
		} else if (actionEventName.equalsIgnoreCase(StringConstants.MOVEMENT.toString())) {
			targetMove();
			if (bullets.size() > 0) {
				bulletMove();
			}
		} else if (actionEventName.equalsIgnoreCase(StringConstants.RUINED.toString()) && target.isRuined()) {
			completionListener.onLevelCompletion(this);
		}
	}
	
	private void targetMove() {
		if (!target.isRuined() && ++target.waitCounter >= TARGET_SPEED_REDUCER) {
			target.waitCounter = 0;
			if (target.toLeft) {
				if (target.getX() > 0) {
					target.setLocation(target.getX() - 1, target.getY());
				} else {
					target.toLeft = false;
				}
			} else {
				if (target.getX() + imageRes.SIDE_LENGTH < getWidth() - RIGHTSIDE_CORRECTION) {
					target.setLocation(target.getX() + 1, target.getY());
				} else {
					target.toLeft = true;
				}
			}
		}
	}
	
	private void bulletMove() {
		boolean removeBullet = false;
		for (Bullet bullet : bullets) {
			if (++bullet.waitCounter >= BULLET_SPEED_REDUCER && bullet.getY() > 1) {
				bullet.waitCounter = 0;
				bullet.setLocation(bullet.getX(), bullet.getY() - 1);
				repaint();
				if (bullet.getY() <= imageRes.SIDE_LENGTH / 2
						&& bullet.edge <= imageRes.SIDE_LENGTH / 2) {
					bullet.edge += 2;
					if (bullet.getX() + imageRes.SIDE_LENGTH / 2 + bullet.edge > target.getX()
							&& bullet.getX() + imageRes.SIDE_LENGTH / 2 - bullet.edge < target.getX() + imageRes.SIDE_LENGTH) {
						remove(bullet);
						removeBullet = true;
						target.getDamage();
						repaint();
					}
				}
			} else if (bullet.getY() <= 1) {
				remove(bullet);
				removeBullet = true;
				repaint();
			}
		}
		if (removeBullet) {
			bullets.remove();
		}
	}
}

interface LevelCompletionable {
	void onLevelCompletion(JFrame frame);
}

public class StartGame implements LevelCompletionable {
	private static Point initialLocation = new Point(300, 300);
	private static JFrame gameFieldFrame;
	private static StartGame startGame = new StartGame();
	private static Resources res = new Resources();
	
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





















