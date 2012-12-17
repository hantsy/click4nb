/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click.editor.hyperlinks;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.xml.lexer.XMLTokenId;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import org.netbeans.lib.editor.hyperlink.spi.HyperlinkProvider;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.web.click.api.model.impl.ClickAttributes;
import org.netbeans.modules.web.click.editor.ClickEditorUtilities;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author hantsy
 */
public class ClickHyperlinkProvider implements HyperlinkProvider {

    private int startOffset;
    private static final java.util.logging.Logger LOGGER;

    static {
        LOGGER = java.util.logging.Logger.getLogger("org.netbeans.modules.web.click.editor.hyperlinks.ClickHyperlinkProvider");
        org.netbeans.modules.web.click.editor.hyperlinks.ClickHyperlinkProvider.initLoggerHandlers();
    }

    private static final void initLoggerHandlers() {
        java.util.logging.Handler[] handlers = LOGGER.getHandlers();
        boolean hasConsoleHandler = false;
        for (java.util.logging.Handler handler : handlers) {
            if (handler instanceof java.util.logging.ConsoleHandler) {
                hasConsoleHandler = true;
            }
        }
        if (!hasConsoleHandler) {
            LOGGER.addHandler(new java.util.logging.ConsoleHandler());
        }
        LOGGER.setLevel(java.util.logging.Level.FINEST);
    }
    private int endOffset;
    private BaseDocument lastDocument;
    private FileObject fileToOpen;
    private String linkTarget;
    private LINKTYPE linkType;

    public ClickHyperlinkProvider() {
        lastDocument = null;
        this.linkTarget = null;
        this.linkType = LINKTYPE.NONE;
    }

    enum LINKTYPE {

        NONE, PATH, CLASSNAME;
    }

    @Override
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
            TokenHierarchy<String> hi = TokenHierarchy.create(doc.getText(0, doc.getLength()), XMLTokenId.language());
            TokenSequence<XMLTokenId> ts = hi.tokenSequence( XMLTokenId.language());

            ts.move(offset);
            boolean lastTokenInDocument = !ts.moveNext();
            if (lastTokenInDocument) {
                // end of the document
                return false;
            }

            Token<XMLTokenId> attrValueToken = ts.offsetToken();

            do {
                // find '='
                ts.movePrevious();
            } while (ts.token() != null &&
                    ts.token().id() == XMLTokenId.WS); // whitespace

            do {
                // find 'beanclass'
                ts.movePrevious();
            } while (ts.token() != null && ts.token().id() == XMLTokenId.WS); // whitespace

            Token<XMLTokenId> attrToken = ts.token();


            if (null == attrValueToken ||
                    null == attrToken || attrValueToken.length() <= 2) {
                return false;
            }

            if ((ClickAttributes.PATH.getName().equals( // attribute is path
                    attrToken.text().toString()) || ClickAttributes.CLASSNAME.getName().equals( // attribute is "classname"
                    attrToken.text().toString())) &&
                    attrValueToken.id() == XMLTokenId.VALUE // identified must be value of the attribute
                    ) { // identifier must be longer than "" string

                lastDocument = doc;

                startOffset = attrValueToken.offset(hi) + 1;
                endOffset = attrValueToken.offset(hi) + attrValueToken.length() - 1;


                if (startOffset > endOffset) {
                    endOffset = startOffset;
                }

                linkTarget = attrValueToken.text().subSequence(1, attrValueToken.length() - 1).toString();

                if (ClickAttributes.PATH.getName().equals(attrToken.text().toString())) {
                    linkType = LINKTYPE.PATH;
                } else if (ClickAttributes.CLASSNAME.getName().equals(attrToken.text().toString())) {
                    linkType = LINKTYPE.CLASSNAME;
                }

                LOGGER.finest("Hyperlink at: " + startOffset + "-" + endOffset +
                        ": " + linkTarget + ", link type @@@" + linkType);
                return true;
            }

        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
        return false;
    }

    private FileObject findPathFileObject(Document doc, String path) {
        FileObject docFO = NbEditorUtilities.getFileObject(doc);
        WebModule wm = WebModule.getWebModule(docFO);
        if (wm == null) {
            return null;
        }

        FileObject docBase = wm.getDocumentBase();

        return ClickEditorUtilities.findPageByPath(docBase, path);
    }

    private FileObject findClassnameFileObject(Document doc, String classname) {
        FileObject docFO = NbEditorUtilities.getFileObject(doc);
        Project project = FileOwnerQuery.getOwner(docFO);

        String classRelativePath = classname.replaceAll("\\.", "/");
        classRelativePath += ".java";
        FileObject classFO = null;
        for (SourceGroup sg : ProjectUtils.getSources(project).
                getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA)) {

            FileObject rootFO = sg.getRootFolder();
            classFO = rootFO.getFileObject(classRelativePath);
            if (null != classFO) {
                break;
            }
        }

        return classFO;

    }

    @Override
    public int[] getHyperlinkSpan(Document doc, int position) {

        return new int[]{startOffset, endOffset};
    }

    @Override
    public void performClickAction(Document doc, int position) {
        if (linkType == LINKTYPE.PATH) {
            fileToOpen = findPathFileObject(doc, linkTarget);
            if (fileToOpen == null) {
                StatusDisplayer.getDefault().setStatusText("Invalid path :" + linkTarget);
                return;
            }
        } else if (linkType == LINKTYPE.CLASSNAME) {
            fileToOpen = findClassnameFileObject(doc, linkTarget);
            if (fileToOpen == null) {
                StatusDisplayer.getDefault().setStatusText("Class source not found :" + linkTarget);
                return;
            }
        }
        if (fileToOpen != null) {
            ClickEditorUtilities.openInEditor(fileToOpen);
        }
    }
}
