
import java.awt.Color;
import java.awt.event.*;
import javax.swing.*;

class Cannon extends JLabel {
	Cannon() {
		super();
	}
	Cannon(ImageIcon i) {
		super(i);
	}
}

class Target extends JButton {
	Target() {
		super();
	}
}

class GameFieldFrame extends JFrame implements KeyListener, ActionListener {
	final int ReloadTime = 2;
	final static int FlyBulletTime = 4600;
	public int borderWidth;
	Cannon cannon;
	Target target;
	Timer tick;
	Timer tack;
	final public static String iconPath = "icon/";
	ImageIcon defaultImage = new ImageIcon(getClass().getResource(
			iconPath + "hold.png"));
	ImageIcon toLeftImage = new ImageIcon(getClass().getResource(
			iconPath + "toLeft.png"));
	ImageIcon toRightImage = new ImageIcon(getClass().getResource(
			iconPath + "toRight.png"));
	ImageIcon reloadImage = new ImageIcon(getClass().getResource(
			iconPath + "reload.png"));
	int reloadSch;
	int mul = 1;
	int sch = 0;
	public boolean toLeft = false;
	
	GameFieldFrame(int x, int y) {
		setBounds(x, y, 350, 450);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(null);
		setResizable(false);
		setVisible(true);
		tick = new Timer(1000, this);
		tick.setActionCommand("reload");
		tack = new Timer(10, this);
		tack.setActionCommand("targetMove");
		addKeyListener(this);
		
		borderWidth = 4;
		target = new Target();
		target.setBounds(getWidth() / 2 - 32, 0, 64, 32);
		target.setBorder(BorderFactory.createMatteBorder(0, borderWidth, borderWidth, borderWidth, Color.black));
		target.setFocusable(false);
		target.setText(String.valueOf(borderWidth / 2));
		target.addActionListener(this);
		add(target);
		tack.start();
		
		cannon = new Cannon(defaultImage);
		cannon.setBounds(getWidth() / 2 - 32, getHeight() - 64 - 40, 64, 64);
		add(cannon);
	}
	
	public void keyReleased(KeyEvent ke) {
		cannon.setIcon(defaultImage);
		mul = 1;
		sch = 0;
	}
	public void keyPressed(KeyEvent ke) {
		switch (ke.getExtendedKeyCode()) {
		case 37:
			cannon.setIcon(toLeftImage);
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
			cannon.setIcon(toRightImage);
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
			cannon.setIcon(reloadImage);
			new Bullet(this);
			repaint();
			reloadSch = ReloadTime;
			tick.start();
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
				cannon.setIcon(defaultImage);
				addKeyListener(this);
				tick.stop();
			}
		}
		if (ae.getActionCommand().equalsIgnoreCase("targetMove")) {
			if (toLeft) {
				if (target.getX() > 0)
					target.setLocation(target.getX() - 1, target.getY());
				else 
					toLeft = false;
			} else {
				if (target.getX() + 64 < getWidth() - 17)
					target.setLocation(target.getX() + 1, target.getY());
				else
					toLeft = true;
			}
		}
		if (ae.getActionCommand().equalsIgnoreCase("@@@")) {
			setVisible(false);
			new GameFieldFrame(getX(), getY());
		}
	}
}

class Bullet extends JLabel implements KeyListener, ActionListener {
	ImageIcon bulImage = new ImageIcon(getClass().getResource(
			GameFieldFrame.iconPath + "bullet.png"));
	GameFieldFrame frame;
	Timer flyTimer;
	int f;
	int i;
	int y;
	int p = 0;
	
	Bullet(GameFieldFrame frame) {
		this.frame = frame;
		setBounds(frame.cannon.getX(), frame.getHeight() - 64 - 105, 64, 64);
		setIcon(bulImage);
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
							if (frame.borderWidth >= 2) {
								frame.borderWidth -= 2;
								frame.target.setBorder(BorderFactory.createMatteBorder(0, frame.borderWidth,
										frame.borderWidth, frame.borderWidth, Color.black));
								frame.target.setText(String.valueOf(frame.borderWidth / 2));
								frame.toLeft = !frame.toLeft;
							} else {
								frame.target.setBackground(Color.black);
								frame.target.setText("@@@");
								frame.tack.stop();
							}
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
		new GameFieldFrame(300, 300);
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





















