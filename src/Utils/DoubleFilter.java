package Utils;

import javax.swing.text.BadLocationException;
import javax.swing.text.AttributeSet;
import javax.swing.text.DocumentFilter;

public class DoubleFilter extends DocumentFilter {
	boolean dotUsed = false;
    public void insertString(DocumentFilter.FilterBypass fb, int offset,
                             String string, AttributeSet attr)
            throws BadLocationException {
        StringBuffer buffer = new StringBuffer(string);
       
        for (int i = buffer.length() - 1; i >= 0; i--) {
        	//System.out.println(buffer);
            char ch = buffer.charAt(i);
            if(ch == '.') {
            	if(dotUsed)
            		buffer.deleteCharAt(i);
            }
            if (!Character.isDigit(ch) && ch!='.') {
                buffer.deleteCharAt(i);
            }
        }
        super.insertString(fb, offset, buffer.toString(), attr);
    }

    public void replace(DocumentFilter.FilterBypass fb,
                        int offset, int length, String string, AttributeSet attr) throws BadLocationException {
    	if(fb.getDocument().getText(0, fb.getDocument().getLength()).contains(".")) {
    		dotUsed = true;
    	} else
    		dotUsed = false;
    	if (length > 0) fb.remove(offset, length);
        insertString(fb, offset, string, attr);
    }
}