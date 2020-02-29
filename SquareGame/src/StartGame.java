
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.*;
import java.util.LinkedList;

import javax.swing.*;

final class Resourses {
	private final String iconPath = "icon/";
	final ImageIcon defaultImage;
	final ImageIcon toLeftImage;
	final ImageIcon toRightImage;
	final ImageIcon reloadImage;
	final ImageIcon bulletImage;
	
	Resourses() {
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

final class Cannon extends JLabel {
	private CannonState state;
	private Resourses imageResourses;
	int accelerator;
	
	Cannon(Resourses res) {
		super();
		imageResourses = res;
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
			setIcon(imageResourses.defaultImage);
			break;
		case RELOAD:
			setIcon(imageResourses.reloadImage);
			break;
		case TO_LEFT:
			setIcon(imageResourses.toLeftImage);
			break;
		case TO_RIGHT:
			setIcon(imageResourses.toRightImage);
			break;
		}
	}
	
	public void moving(CannonState state) {
		int directionSign = 0;
		if (state.equals(CannonState.TO_LEFT)) {
			directionSign = -1;
		} else if (state.equals(CannonState.TO_RIGHT)) {
			directionSign = 1;
		}
		setLocation((getX() + directionSign * accelerator), getY());
		GameFieldFrame.keyPressedCounter++;
		if (GameFieldFrame.keyPressedCounter == 25) {
			accelerator = 2;
		} else if (GameFieldFrame.keyPressedCounter == 70) {
			accelerator = 4;
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
		if (armor >= 0) {
			setBorder(BorderFactory.createMatteBorder(0, armor * 2, 
					armor * 2, armor * 2, Color.black));
			setText(String.valueOf(armor));	
		} else {
			setText("@@@");
		}
	}
}

final class Bullet extends JLabel {
	Resourses imageResourses;
	int waitCounter = 0;
	int edge = -2;
	
	Bullet(Resourses res) {
		imageResourses = res;
		setIcon(imageResourses.bulletImage);
	}
}

final class GameFieldFrame extends JFrame implements KeyListener, ActionListener {
	final int RELOAD_TIME = 2;
	final int INITIAL_ARMOR = 2;
	Dimension FieldSize = new Dimension(350, 450);
	Cannon cannon;
	Target target;
	LinkedList<Bullet> bullets;
	boolean bulletCtrl = false;
	Timer reloadTimer;
	Timer movementTimer;
	//int cannonAccelerator = 1;
	static int keyPressedCounter = 0;
	public boolean toLeft = false;
	Resourses imageResourses;
	
	GameFieldFrame(Point location, Resourses res) {
		imageResourses = res;
		setLocation(location);
		setSize(FieldSize);
		bullets = new LinkedList<Bullet>();
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
		target.setBounds(getWidth() / 2 - 32, 0, 64, 32);
		target.addActionListener(this);
		add(target);
		movementTimer.start();
		
		cannon = new Cannon(imageResourses);
		cannon.setBounds(getWidth() / 2 - 32, getHeight() - 64 - 40, 64, 64);
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
				cannon.moving(CannonState.TO_LEFT);
			} else {
				cannon.setLocation(0, cannon.getY());
			}
			break;
		case 39:
			cannon.setState(CannonState.TO_RIGHT);
			if (cannon.getX() < getWidth() - 17 - 64) {
				cannon.moving(CannonState.TO_RIGHT);
			} else {
				cannon.setLocation(getWidth() - 17 - 64, cannon.getY());
			}
			break;
		case 32:
			cannon.accelerator = 1;
			keyPressedCounter = 0;
			cannon.setState(CannonState.RELOAD);
			bullets.add(new Bullet(imageResourses));
			bullets.peekLast().setBounds(cannon.getX(), getHeight() - 64 - 105, 64, 64);
			bulletCtrl = true;
			add(bullets.peekLast());
			repaint();
			reloadTimer.start();
			break;
		}
	}
	
	private void bulletControlButtons(KeyEvent ke) {
		switch (ke.getExtendedKeyCode()) {
		case 37:
			if (bullets.peekLast().getX() > 0) {
				bullets.peekLast().setLocation((bullets.peekLast().getX() - 2),
						bullets.peekLast().getY());
			} else {
				bullets.peekLast().setLocation(0, bullets.peekLast().getY());
			}
			break;
		case 39:
			if (bullets.peekLast().getX() < getWidth() - 17 - 64) {
				bullets.peekLast().setLocation((bullets.peekLast().getX() + 2),
						bullets.peekLast().getY());
			} else {
				bullets.peekLast().setLocation(getWidth() - 17 - 64,
						bullets.peekLast().getY());
			}
			break;
		}
	}
	
	public void keyTyped(KeyEvent ke) {
		//required by KeyListener interface, left empty as not used
	}
	
	public void actionPerformed(ActionEvent ae) {
		if (ae.getActionCommand().equalsIgnoreCase("reload")) {
			bulletCtrl = false;
			cannon.setState(CannonState.DEFAULT);
			reloadTimer.stop();
		}
		if (ae.getActionCommand().equalsIgnoreCase("movement")) {
			if (!target.getText().equalsIgnoreCase("@@@") && ++target.waitCounter >= 10) {
				target.waitCounter = 0;
				if (target.toLeft) {
					if (target.getX() > 0) {
						target.setLocation(target.getX() - 1, target.getY());
					} else {
						target.toLeft = false;
					}
				} else {
					if (target.getX() + 64 < getWidth() - 17) {
						target.setLocation(target.getX() + 1, target.getY());
					} else {
						target.toLeft = true;
					}
				}
			}
			
			if (bullets.size() > 0) {
				boolean removeBullet = false;
				for (Bullet bullet : bullets) {
					if (++bullet.waitCounter >= 5 && bullet.getY() > 1) {
						bullet.waitCounter = 0;
						bullet.setLocation(bullet.getX(), bullet.getY() - 1);
						repaint();
						if (bullet.getY() <= 32 && bullet.edge <= 32) {
							bullet.edge += 2;
							if (bullet.getX() + 32 + bullet.edge > target.getX()
									&& bullet.getX() + 32 - bullet.edge < target.getX() + 64) {
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
		if (ae.getActionCommand().equalsIgnoreCase("@@@")) {
			setVisible(false);
			new GameFieldFrame(getLocation(), imageResourses);
		}
	}
}

public class StartGame {

	public static void main(String[] args) {
		final Point INITIAL_LOCATION = new Point(300, 300);
		setLook();
		new GameFieldFrame(INITIAL_LOCATION, new Resourses());
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





















