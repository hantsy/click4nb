/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.web.click.editor.completion;

import java.util.logging.Level;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.api.xml.lexer.XMLTokenId;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Sujit Nair (Sujit.Nair@Sun.COM)
 */
public class CompletionContext {

    private static final java.util.logging.Logger LOGGER;

    static {
        LOGGER = java.util.logging.Logger.getLogger("org.netbeans.modules.web.click.editor.completion.CompletionContext");
        org.netbeans.modules.web.click.editor.completion.CompletionContext.initLoggerHandlers();
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
    // private CompletionType completionType = CompletionType.NONE;
    private Document doc;
    private FileObject fileObject;
    private int caretOffset;
    private String typedChars = ""; // NOI18N
    private TokenHierarchy<String> th;
    private Token<XMLTokenId> token;
    private TokenSequence<XMLTokenId> ts;
    boolean valid = false;
    private String tokenText;
    private int tokenOffset;
    private int queryType;
    private String tagName;
    private String attrName;

    public CompletionContext(Document doc, int caretOffset, int queryType) {
        LOGGER.log(Level.FINEST, "CompletionContext constructure@@@ doc @" + doc + ",caretOffset@" + caretOffset + ", queryType@" + queryType);
        this.doc = doc;
        this.fileObject = NbEditorUtilities.getFileObject(doc);
        this.caretOffset = caretOffset;
        this.queryType = queryType;
        initContext();
    }

    private void initContext() {
        try {
            this.th = TokenHierarchy.create(doc.getText(0, doc.getLength()), XMLTokenId.language());
            ts = th.tokenSequence(XMLTokenId.language());
            ts.move(caretOffset);

            boolean lastTokenInDocument = !ts.moveNext();
            if (lastTokenInDocument) {
                // end of the document
                valid = false;
                return;
            }

            token = ts.offsetToken();

            LOGGER.log(Level.FINEST, "token value is @" + token);

            if (token != null && token.length() >= 2 && XMLTokenId.VALUE == token.id()) {
                tokenText = token.text().toString();
                tokenOffset = token.offset(th);
                if (token.length() > 2 && caretOffset > tokenOffset + 1) {
                    typedChars = tokenText.substring(1, caretOffset - tokenOffset).trim();
                } else {
                    typedChars = "";
                }

                LOGGER.log(Level.FINEST, "\n\n>>>>>>>>tokenText @" + tokenText + ", tokenOffset" + tokenOffset + ", typedChars@" + typedChars);

                do {
                    ts.movePrevious();
                } //while (ts.token() == null || XMLTokenId.WS == ts.token().id() || XMLTokenId.OPERATOR == ts.token().id());
                while (ts.token() != null && XMLTokenId.ARGUMENT != ts.token().id());

                token = ts.token();
                if (token != null && token.id() == XMLTokenId.ARGUMENT) {
                    attrName = token.text().toString();
                } else {
                    valid = false;
                    return;
                }

                do {
                    ts.movePrevious();
                } while (ts.token() != null && XMLTokenId.TAG != ts.token().id());// while (ts.token() == null || XMLTokenId.ARGUMENT == ts.token().id() || XMLTokenId.VALUE == ts.token().id() || XMLTokenId.WS == ts.token().id() || XMLTokenId.OPERATOR == ts.token().id());


                token = ts.token();
                if (token != null && token.id() == XMLTokenId.TAG) {
                    tagName = token.text().toString();
                    if (tagName.startsWith("<")) {
                        tagName = tagName.substring(1);
                    }
                } else {
                    valid = false;
                    return;
                }

                valid = true;

            } else {
                valid = false;
                return;
            }

            LOGGER.log(Level.FINEST, "token text#" + tokenText + ", tokenOffset #" + tokenOffset + ", typedChars #" + typedChars);
        } catch (BadLocationException ex) {
            valid = false;
            return;
        }
    }

//    public CompletionType getCompletionType() {
//        return completionType;
//    }
    public String getTypedPrefix() {
        return typedChars;
    }

    public Document getDocument() {
        return this.doc;
    }

    public int getCaretOffset() {
        return caretOffset;
    }

//    public Token<XMLTokenId> getCurrentToken() {
//        return this.token;
//    }
    public int getTokenOffset() {
        return this.tokenOffset;
    }

    public FileObject getFileObject() {
        return fileObject;
    }

    public int getQueryType() {
        return this.queryType;
    }

    public String getTagName() {
        return tagName;
    }

    public String getAttrName() {
        return attrName;
    }

    boolean isValid() {
        return this.valid;
    }

    @Override
    public String toString() {
        StringBuilder toStringBuilder = new StringBuilder();
        toStringBuilder.append(super.toString());
        toStringBuilder.append("\n");
        toStringBuilder.append("\ndoc: ");
        toStringBuilder.append(doc);
        toStringBuilder.append("\nfileObject: ");
        toStringBuilder.append(fileObject);
        toStringBuilder.append("\ncaretOffset: ");
        toStringBuilder.append(caretOffset);
        toStringBuilder.append("\ntypedChars: ");
        toStringBuilder.append(typedChars);
        toStringBuilder.append("\nth: ");
        toStringBuilder.append(th);
        toStringBuilder.append("\ntoken: ");
        toStringBuilder.append(token);
        toStringBuilder.append("\nts: ");
        toStringBuilder.append(ts);
        toStringBuilder.append("\nvalid: ");
        toStringBuilder.append(valid);
        toStringBuilder.append("\ntokenText: ");
        toStringBuilder.append(tokenText);
        toStringBuilder.append("\ntokenOffset: ");
        toStringBuilder.append(tokenOffset);
        toStringBuilder.append("\nqueryType: ");
        toStringBuilder.append(queryType);
        toStringBuilder.append("\ntagName: ");
        toStringBuilder.append(tagName);
        toStringBuilder.append("\nattrName: ");
        toStringBuilder.append(attrName);
        return toStringBuilder.toString();
    }
}

