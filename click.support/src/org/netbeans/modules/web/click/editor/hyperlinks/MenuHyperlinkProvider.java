/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click.editor.hyperlinks;

import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.api.xml.lexer.XMLTokenId;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import org.netbeans.lib.editor.hyperlink.spi.HyperlinkProvider;
import org.netbeans.modules.web.click.api.model.impl.MenuAttributes;
import org.netbeans.modules.web.click.editor.ClickEditorUtilities;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author hantsy
 */
public class MenuHyperlinkProvider implements HyperlinkProvider {

    private static final Logger log = Logger.getLogger(MenuHyperlinkProvider.class.getName());
    BaseDocument lastDocument;
    int startOffset;
    int endOffset;
    String path;

    public MenuHyperlinkProvider() {
        lastDocument = null;
    }

    public boolean isHyperlinkPoint(Document document, int offset) {

        if (!(document instanceof BaseDocument)) {
            return false;
        }

        BaseDocument doc = (BaseDocument) document;
        JTextComponent target = Utilities.getFocusedComponent();

        if ((target == null) || (target.getDocument() != doc)) {
            return false;
        }

        try {
            TokenHierarchy hi = TokenHierarchy.create(doc.getText(0, doc.getLength()), XMLTokenId.language());
            @SuppressWarnings("unchecked")
            TokenSequence<XMLTokenId> ts = hi.tokenSequence();

            ts.move(offset);
            boolean lastTokenInDocument = !ts.moveNext();
            if (lastTokenInDocument) {
                // end of the document
                return false;
            }

            Token<XMLTokenId> pathValueToken = ts.offsetToken();

            do {
                // find '='
                ts.movePrevious();
            } while (ts.token() != null &&
                    ts.token().id() == XMLTokenId.WS); // whitespace

            do {
                // find 'beanclass'
                ts.movePrevious();
            } while (ts.token() != null && ts.token().id() == XMLTokenId.WS); // whitespace

            Token<XMLTokenId> pathAttrToken = ts.token();


            if (null != pathValueToken &&
                    null != pathAttrToken &&
                    MenuAttributes.PATH.getName().equals( // attribute must be "beanclass"
                    pathAttrToken.text().toString()) &&
                    pathValueToken.id() == XMLTokenId.VALUE && // identified must be value of the attribute
                    pathValueToken.length() > 2) { // identifier must be longer than "" string

                lastDocument = doc;

                startOffset = pathValueToken.offset(hi) + 1;
                endOffset = pathValueToken.offset(hi) + pathValueToken.length() - 1;


                if (startOffset > endOffset) {
                    endOffset = startOffset;
                }

                path = pathValueToken.text().
                        subSequence(1, pathValueToken.length() - 1).toString();

                log.finest("Hyperlink at: " + startOffset + "-" + endOffset +
                        ": " + path);
                return true;
            }

        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }

        return false;
    }

    public int[] getHyperlinkSpan(Document docment, int index) {
        return new int[]{startOffset, endOffset};
    }

    public void performClickAction(Document docment, int index) {
        FileObject docFO = NbEditorUtilities.getFileObject(docment);
        WebModule wm = WebModule.getWebModule(docFO);
        if (wm == null) {
            return;
        }
        FileObject targetFO = ClickEditorUtilities.findPageByPath(wm.getDocumentBase(), path);
        if (targetFO == null) {
            StatusDisplayer.getDefault().setStatusText("Invalid path :" + path);
            return;
        }
        ClickEditorUtilities.openInEditor(targetFO);

    }
}
