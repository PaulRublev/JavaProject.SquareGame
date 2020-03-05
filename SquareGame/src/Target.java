import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;

public final class Target extends JButton {
	int waitCounter = 0;
	private int armor;
	boolean toLeft = true;
	private int directionSign;
	
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
		String targetCondition;
		if (!isRuined()) {
			setBorder(BorderFactory.createMatteBorder(0, armor * 2, 
					armor * 2, armor * 2, Color.black));
			targetCondition = String.valueOf(armor);	
		} else {
			targetCondition = StringConstants.RUINED.toString();
		}
		setText(targetCondition);
	}
	
	public boolean isRuined() {
		return armor < 0;
	}
	
	public void move() {
		if (toLeft) {
			directionSign = -1;
		} else {
			directionSign = 1;
		}
		setLocation(getX() + 1 * directionSign, getY());
	}
}