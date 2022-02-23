package Utils;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;

public final class CustomFontProvider {
	
//	final int SIZE_SMALL = 18;
//	final int SIZE_MEDIUM = 28;
//	final int SIZE_LARGE = 38;
//	
	
//	final int SIZE_SMALL = 10;
//	final int SIZE_MEDIUM = 14;
//	final int SIZE_LARGE = 20;
	
	final int SIZE_SMALL = 16;
	final int SIZE_MEDIUM = 20;
	final int SIZE_LARGE = 24;
	
	private static Font customFont;
	
	public CustomFontProvider() {
		try {
		    //create the font to use. Specify the size!
		    customFont = Font.createFont(Font.TRUETYPE_FONT, new File("Fonts\\Jameel Noori Nastaleeq.ttf"));
		    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		    //register the font
		    ge.registerFont(customFont);
		} catch (IOException e) {
		    e.printStackTrace();
		} catch(FontFormatException e) {
		    e.printStackTrace();
		}
	}

	private Font getCustomFont(int type, int size) {
		return customFont.deriveFont(type, size);
//		return new Font("Monospaced", type, size);
//		return new Font("Arial", type, size);
	}
	
	public Font getSmallPlainFont() {
		return getCustomFont(Font.PLAIN, SIZE_SMALL);
	}
	
	public Font getSmallBoldFont() {
		return getCustomFont(Font.BOLD, SIZE_SMALL);
	}
	
	public Font getMediumPlainFont() {
		return getCustomFont(Font.PLAIN, SIZE_MEDIUM);
	}
	
	public Font getMediumBoldFont() {
		return getCustomFont(Font.BOLD, SIZE_MEDIUM);
	}
	
	
	public Font getLargePlainFont() {
		return getCustomFont(Font.PLAIN, SIZE_LARGE);
	}
	
	public Font getLargeBoldFont() {
		return getCustomFont(Font.BOLD, SIZE_LARGE);
	}
}