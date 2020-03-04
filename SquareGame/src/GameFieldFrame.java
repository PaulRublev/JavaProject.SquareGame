import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;

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

enum KeyboardFocus {
	BULLET,
	CANNON,
	NOTHING;
}

public final class GameFieldFrame extends JFrame implements KeyListener, ActionListener {
	private final int RELOAD_TIME = 3;
	private final int INITIAL_ARMOR = 2;
	private final int RIGHTSIDE_CORRECTION = 17;
	private final int DOWNSIDE_CORRECTION = 40;
	private final int TARGET_SPEED_REDUCER = 10;
	private final int BULLET_SPEED_REDUCER = 5;
	private final Dimension fieldSize = new Dimension(350, 450);
	private Cannon cannon;
	private Target target;
	private LinkedList<Bullet> bullets = new LinkedList<Bullet>();
	private KeyboardFocus focus = KeyboardFocus.CANNON;
	private LevelCompletionable completionListener;
	private Timer reloadTimer;
	private Timer movementTimer;
	private Resources imageRes;
	private int keyPressedCounter = 0;
	private int frameWidth;
	
	GameFieldFrame(Point location, Resources res, LevelCompletionable completionListener) {
		this.completionListener = completionListener;
		imageRes = res;
		setLocation(location);
		setSize(fieldSize);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(null);
		setResizable(false);
		setVisible(true);
		reloadTimer = new Timer(RELOAD_TIME * 1000, this);
		reloadTimer.setActionCommand(StringConstants.RELOAD.toString());
		movementTimer = new Timer(1, this);
		movementTimer.setActionCommand(StringConstants.MOVEMENT.toString());
		addKeyListener(this);
		frameWidth = getWidth() - imageRes.SIDE_LENGTH - RIGHTSIDE_CORRECTION;

		
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
		if (focus == KeyboardFocus.CANNON) {
			cannon.setState(CannonState.DEFAULT);
			cannon.accelerator = 1;
			keyPressedCounter = 0;
		}
	}
	
	public void keyPressed(KeyEvent ke) {
		if (focus == KeyboardFocus.CANNON) {
			cannonControlButtons(ke);
		} else if (focus == KeyboardFocus.BULLET) {
			bulletControlButtons(ke);
		}
	}
	
	private void cannonControlButtons(KeyEvent ke) {
		switch (ke.getExtendedKeyCode()) {
		case 37:
			if (inThisFrame(cannon)) {
				keyPressedCounter++;
				cannon.setState(CannonState.TO_LEFT);
				cannon.move(keyPressedCounter);
				if (!inThisFrame(cannon)) {
					cannon.setState(CannonState.DEFAULT);
					int maxLeftX = 0;
					cannon.stay(maxLeftX);
				}
			}
			break;
		case 39:
			if (inThisFrame(cannon)) {
				keyPressedCounter++;
				cannon.setState(CannonState.TO_RIGHT);
				cannon.move(keyPressedCounter);
				if (!inThisFrame(cannon)) {
					cannon.setState(CannonState.DEFAULT);
					int maxRightX = frameWidth;
					cannon.stay(maxRightX);
				}
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
			focus = KeyboardFocus.BULLET;
			repaint();
			reloadTimer.start();
			break;
		}
	}
	
	private void bulletControlButtons(KeyEvent ke) {
		final Bullet firstBullet = bullets.peekLast();
		int directionSign = 0;
		switch (ke.getExtendedKeyCode()) {
		case 37:
			if (inThisFrame(firstBullet)) {
				directionSign = -1;
			}
			break;
		case 39:
			if (inThisFrame(firstBullet)) {
				directionSign = 1;
			}
			break;
		}
		firstBullet.move(directionSign);
		if (!inThisFrame(firstBullet)) {
			directionSign *= -1;
			firstBullet.move(directionSign);
		}
	}
	
	public void keyTyped(KeyEvent ke) {
		//required by KeyListener interface, left empty as not used
	}
	
	public void actionPerformed(ActionEvent ae) {
		final String actionEventName = ae.getActionCommand();
		if (actionEventName.equalsIgnoreCase(StringConstants.RELOAD.toString())) {
			focus = KeyboardFocus.CANNON;
			cannon.setState(CannonState.DEFAULT);
			reloadTimer.stop();
		} else if (actionEventName.equalsIgnoreCase(StringConstants.MOVEMENT.toString())) {
			moveTarget();
			if (bullets.size() > 0) {
				moveBullet();
			}
		} else if (actionEventName.equalsIgnoreCase(StringConstants.RUINED.toString()) && target.isRuined()) {
			completionListener.onLevelCompletion(this);
		}
	}
	
	private void moveTarget() {
		if (!target.isRuined() && ++target.waitCounter >= TARGET_SPEED_REDUCER) {
			target.waitCounter = 0;
			target.move();
			if (!inThisFrame(target)) {
				target.toLeft = !target.toLeft;
			}
		}
	}
	
	boolean inThisFrame(JComponent component) {
		return component.getX() >= 0 && component.getX() <= frameWidth;
	}
	
	private void moveBullet() {
		LinkedList<Bullet> toDeleteList = new LinkedList<Bullet>();
		for (Bullet bullet : bullets) {
			if (++bullet.waitCounter >= BULLET_SPEED_REDUCER && bullet.getY() > 1) {
				bullet.waitCounter = 0;
				bullet.setLocation(bullet.getX(), bullet.getY() - 1);
				repaint();
				if (bullet.getY() <= imageRes.SIDE_LENGTH / 2
						&& bullet.distanceToEdge <= imageRes.SIDE_LENGTH / 2) {
					if (bullet.getX() + imageRes.SIDE_LENGTH / 2 + bullet.distanceToEdge > target.getX()
							&& bullet.getX() + imageRes.SIDE_LENGTH / 2 - bullet.distanceToEdge < 
							target.getX() + imageRes.SIDE_LENGTH) {
						remove(bullet);
						toDeleteList.add(bullet);
						target.getDamage();
						repaint();
					}
					bullet.distanceToEdge += 2;
				}
			} else if (bullet.getY() <= 1) {
				remove(bullet);
				toDeleteList.add(bullet);
				repaint();
			}
		}
		if (!bullets.isEmpty()) {
			if (!toDeleteList.isEmpty()) {
				Bullet lastBullet = bullets.peekLast();
				bullets.removeAll(toDeleteList);
				if (bullets.isEmpty() || lastBullet != bullets.peekLast()) {
					focus = KeyboardFocus.NOTHING;
				}
			}
		}
	}
}