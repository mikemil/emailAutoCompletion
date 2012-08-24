package util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

/**
 * A {@link Document} performing auto completion on the inserted text. This
 * document can be used on any {@link JTextComponent}.
 * <p>
 * The completion will only happen for inserts, that is, when characters are
 * typed. If characters are erased, no new completion is suggested until a new
 * character is typed.
 * 
 * @see CompletionService
 * 
 */
public class EmailAddressAutoCompleteDocument extends PlainDocument {

    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;

    private static final String AT_SIGN = "@";
    
    /** Completion service. */
    private CompletionService<?> completionService;

    /** The document owner. */
    private JTextComponent documentOwner;
    
    /** max length allowed for email address */
    private final int maxLength = 255;

    /**
     * Create a new <code>AutoCompletionDocument</code>.
     * 
     * @param service  the service to use when searching for completions
     * @param documentOwner  the document owner
     */
    public EmailAddressAutoCompleteDocument(CompletionService<?> service,
            JTextComponent documentOwner) {
        this.completionService = service;
        this.documentOwner = documentOwner;
    }

    /**
     * Look up the completion string.
     * 
     * @param str   the prefix string to complete
     * @return the completion or <code>null</code> if completion was found.
     */
    protected String complete(String str) {
        Object o = completionService.autoComplete(str);
        return o == null ? null : o.toString();
    }

    public void insertString(int offs, String str, AttributeSet a)
            throws BadLocationException {
        if (str == null || str.length() == 0) {
            return;
        }

        // check for max length first
        if (str == null || ((str.length() + getLength() > maxLength) && maxLength > 0))
			return;
        
        String text = getText(0, offs);        // Current text.
        int atSignIdx = text.indexOf(AT_SIGN); // 1st @ sign
        if (atSignIdx != -1 && offs > atSignIdx) {
        	String fullText = text+str;
        	//prevent entry of 2nd @ sign
        	if (fullText.indexOf(AT_SIGN, atSignIdx+1) != -1) {
        		return;
        	}
        	String address = fullText.substring(fullText.indexOf(AT_SIGN)+1);
        	String completion = complete(address);
        	int length = offs + str.length();
        	if (completion != null && text.length() > 0) {
                super.insertString(offs, completion.substring(address.length()-1), a);
                documentOwner.select(length, getLength());
        	} else {
        		super.insertString(offs, str, a);
        	}
        } else {
            super.insertString(offs, str, a);
        }
    }
}