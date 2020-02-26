
import java.awt.Color;
import java.awt.event.*;

import javax.swing.*;

class FrameField extends JFrame implements KeyListener, ActionListener {
	final int ReloadTime = 2;
	final static int FlyBulletTime = 4600;
	public int borderWidth;
	JLabel destroyer;
	JButton target;
	Timer tick, tack;
	final public static String iconPath = "icon/";
	ImageIcon defaultImage = new ImageIcon(getClass().getResource(
			iconPath + "hold.png"));
	ImageIcon toLeftImage = new ImageIcon(getClass().getResource(
			iconPath + "toLeft.png"));
	ImageIcon toRightImage = new ImageIcon(getClass().getResource(
			iconPath + "toRight.png"));
	ImageIcon reloadImage = new ImageIcon(getClass().getResource(
			iconPath + "reload.png"));
	int reloadSch, mul = 1, sch = 0;
	public boolean toLeft = false;
	
	FrameField(int j, int k) {
		setBounds(j, k, 350, 450);
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
		target = new JButton();
		target.setBounds(getWidth() / 2 - 32, 0, 64, 32);
		target.setBorder(BorderFactory.createMatteBorder(0, borderWidth, borderWidth, borderWidth, Color.black));
		target.setFocusable(false);
		target.setText(String.valueOf(borderWidth / 2));
		target.addActionListener(this);
		add(target);
		tack.start();
		
		destroyer = new JLabel(defaultImage);
		destroyer.setBounds(getWidth() / 2 - 32, getHeight() - 64 - 40, 64, 64);
		add(destroyer);
	}
	
	public void keyReleased(KeyEvent ke) {
		destroyer.setIcon(defaultImage);
		mul = 1;
		sch = 0;
	}
	public void keyPressed(KeyEvent ke) {
		switch (ke.getExtendedKeyCode()) {
		case 37:
			destroyer.setIcon(toLeftImage);
			if (destroyer.getX() > 0) {
				destroyer.setLocation((destroyer.getX() - 1 * mul), destroyer.getY());
				sch++;
				if (sch == 25) 
					mul = 2;
				else if (sch == 70) 
					mul = 4;
			} else {
				destroyer.setLocation(0, destroyer.getY());
			}
			break;
		case 39:
			destroyer.setIcon(toRightImage);
			if (destroyer.getX() < getWidth() - 17 - 64) {
				destroyer.setLocation((destroyer.getX() + 1 * mul), destroyer.getY());
				sch++;
				if (sch == 25) 
					mul = 2;
				else if (sch == 70) 
					mul = 4;
			} else {
				destroyer.setLocation(getWidth() - 17 - 64, destroyer.getY());
			}
			break;
		case 32:
			removeKeyListener(this);
			mul = 1;
			sch = 0;
			destroyer.setIcon(reloadImage);
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
				//System.out.print(r + ", ");
			} else {
				//System.out.print(r - 1);
				destroyer.setIcon(defaultImage);
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
			new FrameField(getX(), getY());
		}
	}
}

class Bullet extends JLabel implements KeyListener, ActionListener {
	ImageIcon bulImage = new ImageIcon(getClass().getResource(
			FrameField.iconPath + "bullet.png"));
	FrameField frame;
	Timer flyTimer;
	int f, i, y, p = 0;
	
	Bullet(FrameField frame) {
		this.frame = frame;
		setBounds(frame.destroyer.getX(), frame.getHeight() - 64 - 105, 64, 64);
		setIcon(bulImage);
		frame.addKeyListener(this);
		frame.add(this);
		flyTimer = new Timer(1, this);
		flyTimer.setActionCommand("fly");
		f = FrameField.FlyBulletTime;
		y = getY();
		i = 0;
		flyTimer.start();
		
	}
	public void keyTyped(KeyEvent e) {
		
	}
	public void keyPressed(KeyEvent e) {
		//System.out.print(" " + e.getExtendedKeyCode() + "!");
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
							//System.out.print("\n@@@");
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
		new FrameField(300, 300);
	}
	
	private static void setLook() {
		try {
			// Set System L&F
			UIManager.setLookAndFeel(
					// UIManager.getCrossPlatformLookAndFeelClassName());
					UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.print(e);
		}
	}

}





















