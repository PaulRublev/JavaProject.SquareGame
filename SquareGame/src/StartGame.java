
import java.awt.Color;
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

enum Direction {
	DEFAULT("default"),
	RELOAD("reload"),
	TO_LEFT("toLeft"),
	TO_RIGHT("toRight");
	
	private String direction;
    Direction(String direction){
        this.direction = direction;
    }
    public String getDirection(){ return direction;}
}

class Cannon extends JLabel {
	private Direction direction;
	private Resourses imageResourses;
	
	Cannon(Resourses res) {
		super();
		imageResourses = res;
		direction = Direction.DEFAULT;
		setCannonImage();
	}
	
	public void setDirection(Direction direction) {
		this.direction = direction;
		setCannonImage();
	}
	
	private void setCannonImage() {
		switch (direction.getDirection()) {
		case "default":
			setIcon(imageResourses.defaultImage);
			break;
		case "reload":
			setIcon(imageResourses.reloadImage);
			break;
		case "toLeft":
			setIcon(imageResourses.toLeftImage);
			break;
		case "toRight":
			setIcon(imageResourses.toRightImage);
			break;
		}
	}
}

class Target extends JButton {
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

class GameFieldFrame extends JFrame implements KeyListener, ActionListener {
	final int ReloadTime = 2;
	final static int FlyBulletTime = 4600;
	final int initialArmor = 2;
	Cannon cannon;
	Target target;
	Timer reloadTimer;
	Timer targetMoveTimer;
	int reloadSch;
	int mul = 1;
	int sch = 0;
	public boolean toLeft = false;
	Resourses imageResourses;
	
	GameFieldFrame(int x, int y, Resourses res) {
		imageResourses = res;
		setBounds(x, y, 350, 450);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(null);
		setResizable(false);
		setVisible(true);
		reloadTimer = new Timer(1000, this);
		reloadTimer.setActionCommand("reload");
		targetMoveTimer = new Timer(10, this);
		targetMoveTimer.setActionCommand("targetMove");
		addKeyListener(this);
		
		target = new Target(initialArmor);
		target.setBounds(getWidth() / 2 - 32, 0, 64, 32);
		target.addActionListener(this);
		add(target);
		targetMoveTimer.start();
		
		cannon = new Cannon(imageResourses);
		cannon.setBounds(getWidth() / 2 - 32, getHeight() - 64 - 40, 64, 64);
		cannon.setDirection(Direction.DEFAULT);
		add(cannon);
		repaint();
	}
	
	public void keyReleased(KeyEvent ke) {
		cannon.setIcon(imageResourses.defaultImage);
		mul = 1;
		sch = 0;
	}
	public void keyPressed(KeyEvent ke) {
		switch (ke.getExtendedKeyCode()) {
		case 37:
			cannon.setDirection(Direction.TO_LEFT);
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
			cannon.setDirection(Direction.TO_RIGHT);
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
			removeKeyListener(this);
			mul = 1;
			sch = 0;
			cannon.setDirection(Direction.RELOAD);
			new Bullet(this, imageResourses);
			repaint();
			reloadSch = ReloadTime;
			reloadTimer.start();
			break;
		}
	}
	public void keyTyped(KeyEvent ke) {
		
	}
	public void actionPerformed(ActionEvent ae) {
		if (ae.getActionCommand().equalsIgnoreCase("reload")) {
			if (reloadSch > 1) {
				reloadSch--;
			} else {
				cannon.setDirection(Direction.DEFAULT);
				addKeyListener(this);
				reloadTimer.stop();
			}
		}
		if (ae.getActionCommand().equalsIgnoreCase("targetMove")) {
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
		if (ae.getActionCommand().equalsIgnoreCase("@@@")) {
			setVisible(false);
			new GameFieldFrame(getX(), getY(), imageResourses);
		}
	}
}

class Bullet extends JLabel implements KeyListener, ActionListener {
	GameFieldFrame frame;
	Resourses imageResourses;
	Timer flyTimer;
	int f;
	int i;
	int y;
	int p = 0;
	
	Bullet(GameFieldFrame frame, Resourses res) {
		this.frame = frame;
		imageResourses = res;
		setBounds(frame.cannon.getX(), frame.getHeight() - 64 - 105, 64, 64);
		setIcon(imageResourses.bulletImage);
		frame.addKeyListener(this);
		frame.add(this);
		flyTimer = new Timer(1, this);
		flyTimer.setActionCommand("fly");
		f = GameFieldFrame.FlyBulletTime;
		y = getY();
		i = 0;
		flyTimer.start();
		
	}
	public void keyTyped(KeyEvent e) {
		
	}
	public void keyPressed(KeyEvent e) {
		switch (e.getExtendedKeyCode()) {
		case 37:
			if (getX() > 0) {
				setLocation((getX() - 1), getY());
			} else {
				setLocation(0, getY());
			}
			break;
		case 39:
			if (getX() < frame.getWidth() - 17 - 64) {
				setLocation((getX() + 1), getY());
			} else {
				setLocation(frame.getWidth() - 17 - 64, getY());
			}
			break;
		}
	}
	public void keyReleased(KeyEvent e) {
		
	}

	public void actionPerformed(ActionEvent ae) {
		if (ae.getActionCommand().equalsIgnoreCase("fly")) {
			if (f == 4000)
				frame.removeKeyListener(this);
			if (f > 1) {
				f--;
				i++;
				if (i >= 14 && y > 1) {
					i = 0;
					y--;
					setLocation(getX(), y);
					if (getY() <= 32 && p <= 32) {
						if (getX() + 32 + p > frame.target.getX() &&
								getX() + 32 - p < frame.target.getX() + 64) {
							flyTimer.stop();
							frame.remove(this);
							frame.target.getDamage();
							if (frame.target.getText().equalsIgnoreCase("@@@"))
								frame.targetMoveTimer.stop();
							frame.repaint();
						}
						p += 2;
					}
				}
			} else {
				frame.remove(this);
				frame.repaint();
				flyTimer.stop();
			}
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





















