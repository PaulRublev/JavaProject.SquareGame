import javax.swing.ImageIcon;

public final class Resources {
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