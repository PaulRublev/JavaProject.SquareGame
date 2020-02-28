
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.*;
import javax.swing.*;

class Resourses {
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

class Cannon extends JLabel {
	private CannonState direction;
	private Resourses imageResourses;
	
	Cannon(Resourses res) {
		super();
		imageResourses = res;
		direction = CannonState.DEFAULT;
		setCannonImage();
	}
	
	public void setState(CannonState direction) {
		this.direction = direction;
		setCannonImage();
	}
	
	private void setCannonImage() {
		switch (direction) {
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
}

class Target extends JButton {
	int waitCounter = 0;
	int armor;
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

class Bullet extends JLabel {
	Resourses imageResourses;
	boolean exist = false;
	int waitCounter = 0;
	int edge = 0;
	
	Bullet(Resourses res) {
		imageResourses = res;
		setIcon(imageResourses.bulletImage);
		exist = true;
	}
}

class GameFieldFrame extends JFrame implements KeyListener, ActionListener {
	final int ReloadTime = 2;
	final int initialArmor = 2;
	Dimension FieldSize = new Dimension(350, 450);
	Cannon cannon;
	Target target;
	Bullet[] bullets;
	int bulletNumber = 0;
	boolean bulletCtrl = false;
	Timer reloadTimer;
	Timer movementTimer;
	int mul = 1;
	int sch = 0;
	public boolean toLeft = false;
	Resourses imageResourses;
	
	GameFieldFrame(int x, int y, Resourses res) {
		imageResourses = res;
		setLocation(x, y);
		setSize(FieldSize);
		bullets = new Bullet[6];
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(null);
		setResizable(false);
		setVisible(true);
		reloadTimer = new Timer(ReloadTime * 1000, this);
		reloadTimer.setActionCommand("reload");
		movementTimer = new Timer(1, this);
		movementTimer.setActionCommand("movement");
		addKeyListener(this);
		
		target = new Target(initialArmor);
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
			mul = 1;
			sch = 0;
		}
	}
	
	public void keyPressed(KeyEvent ke) {
		if (!bulletCtrl)
		switch (ke.getExtendedKeyCode()) {
		case 37:
			cannon.setState(CannonState.TO_LEFT);
			if (cannon.getX() > 0) {
				cannon.setLocation((cannon.getX() - 1 * mul), cannon.getY());
				sch++;
				if (sch == 25) 
					mul = 2;
				else if (sch == 70) 
					mul = 4;
			} else {
				cannon.setLocation(0, cannon.getY());
			}
			break;
		case 39:
			cannon.setState(CannonState.TO_RIGHT);
			if (cannon.getX() < getWidth() - 17 - 64) {
				cannon.setLocation((cannon.getX() + 1 * mul), cannon.getY());
				sch++;
				if (sch == 25) 
					mul = 2;
				else if (sch == 70) 
					mul = 4;
			} else {
				cannon.setLocation(getWidth() - 17 - 64, cannon.getY());
			}
			break;
		case 32:
			mul = 1;
			sch = 0;
			cannon.setState(CannonState.RELOAD);
			bullets[bulletNumber] = new Bullet(imageResourses);
			bullets[bulletNumber].setBounds(cannon.getX(), getHeight() - 64 - 105, 64, 64);
			bulletCtrl = true;
			add(bullets[bulletNumber]);
			repaint();
			reloadTimer.start();
			break;
		} else {
			switch (ke.getExtendedKeyCode()) {
			case 37:
				if (bullets[bulletNumber].getX() > 0) {
					bullets[bulletNumber].setLocation((bullets[bulletNumber].getX() - 2),
							bullets[bulletNumber].getY());
				} else {
					bullets[bulletNumber].setLocation(0, bullets[bulletNumber].getY());
				}
				break;
			case 39:
				if (bullets[bulletNumber].getX() < getWidth() - 17 - 64) {
					bullets[bulletNumber].setLocation((bullets[bulletNumber].getX() + 2),
							bullets[bulletNumber].getY());
				} else {
					bullets[bulletNumber].setLocation(getWidth() - 17 - 64,
							bullets[bulletNumber].getY());
				}
				break;
			}
		}
	}
	
	public void keyTyped(KeyEvent ke) {
		//required by KeyListener interface, left empty as not used
	}
	
	public void actionPerformed(ActionEvent ae) {
		if (ae.getActionCommand().equalsIgnoreCase("reload")) {
			bulletCtrl = false;
			if (bulletNumber == 5)
				bulletNumber = 0;
			else
				bulletNumber++;
			cannon.setState(CannonState.DEFAULT);
			reloadTimer.stop();
		}
		if (ae.getActionCommand().equalsIgnoreCase("movement")) {
			if (!target.getText().equalsIgnoreCase("@@@") && ++target.waitCounter >= 10) {
				target.waitCounter = 0;
				if (target.toLeft) {
					if (target.getX() > 0)
						target.setLocation(target.getX() - 1, target.getY());
					else 
						target.toLeft = false;
				} else {
					if (target.getX() + 64 < getWidth() - 17)
						target.setLocation(target.getX() + 1, target.getY());
					else
						target.toLeft = true;
				}
			}
			
			for (Bullet bullet : bullets) {
				if (bullet != null && bullet.exist) {
					if (++bullet.waitCounter >= 4 && bullet.getY() > 1) {
						bullet.waitCounter = 0;
						bullet.setLocation(bullet.getX(), bullet.getY() - 1);
						repaint();
						if (bullet.getY() <= 32 && bullet.edge <= 32) {
							if (bullet.getX() + 32 + bullet.edge > target.getX() &&
									bullet.getX() + 32 - bullet.edge < target.getX() + 64) {
								remove(bullet);
								bullet.exist = false;
								target.getDamage();
								repaint();
							}
							bullet.edge += 2;
						}
					} else if (bullet.getY() <= 1) {
						remove(bullet);
						bullet.exist = false;
						repaint();
					}
				}
			}
		}
		if (ae.getActionCommand().equalsIgnoreCase("@@@")) {
			setVisible(false);
			new GameFieldFrame(getX(), getY(), imageResourses);
		}
	}
}

public class StartGame {

	public static void main(String[] args) {
		setLook();
		new GameFieldFrame(300, 300, new Resourses());
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





















