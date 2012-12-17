/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click.refactorings;

import java.io.IOException;
import java.util.logging.Level;
import javax.swing.text.Position.Bias;
import javax.swing.text.StyledDocument;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.api.xml.lexer.XMLTokenId;
import org.netbeans.modules.refactoring.api.AbstractRefactoring;
import org.netbeans.modules.refactoring.api.Problem;
import org.netbeans.modules.refactoring.spi.RefactoringElementsBag;
import org.netbeans.modules.refactoring.spi.SimpleRefactoringElementImplementation;
import org.netbeans.modules.web.click.api.model.ClickModel;
import org.netbeans.modules.web.click.api.model.ClickModelFactory;
import org.netbeans.modules.web.click.api.model.impl.ClickAttributes;
import org.netbeans.modules.web.click.api.model.impl.ClickComponentImpl;
import org.netbeans.modules.xml.retriever.catalog.Utilities;
import org.netbeans.modules.xml.xam.Model.State;
import org.netbeans.modules.xml.xam.locator.CatalogModelException;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.text.CloneableEditorSupport;
import org.openide.text.PositionBounds;
import org.openide.text.PositionRef;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.Parameters;

/**
 *
 * @author hantsy
 */
public abstract class ClickXmlRefactoring implements ClickRefactoring {

    private static final java.util.logging.Logger LOGGER;

    static {
        LOGGER = java.util.logging.Logger.getLogger("org.netbeans.modules.web.click.refactorings.ClickXmlRefactoring");
        org.netbeans.modules.web.click.refactorings.ClickXmlRefactoring.initLoggerHandlers();
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
    FileObject clickFO;
    ClickModel clickModel;
    TokenHierarchy<String> th;
    TokenSequence<XMLTokenId> ts;
    CloneableEditorSupport editorSupport;

    @SuppressWarnings("unchecked")
    public ClickXmlRefactoring(FileObject fo)  {
        Parameters.notNull("Parameter fo can not be null", fo);
        try {
            this.clickModel = ClickModelFactory.getInstance().createFreshModel(Utilities.createModelSource(fo, true));
        } catch (CatalogModelException ex) {
            Exceptions.printStackTrace(ex);
        }
        //((ClickModelImpl) clickModel).setAutoSyncActive(true);
        //this.clickModel.startTransaction();
        this.clickFO = fo;
        try {
            th = TokenHierarchy.create(clickFO.asText(), XMLTokenId.language());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        ts = (TokenSequence<XMLTokenId>) th.tokenSequence();
        editorSupport = findCloneableEditorSupport();
    }

    public PositionBounds createClassnamePosistionBounds(ClickComponentImpl component) {
        return createPosistionBounds(component, ClickAttributes.CLASSNAME.getName());
    }

    public PositionBounds createPosistionBounds(ClickComponentImpl component, String attributeName) {
        LOGGER.log(Level.FINEST, "@component@" + component + "@attributeName@" + attributeName);
        PositionBounds bounds = null;
        int attrPosition = component.findAttributePosition(attributeName);
        Token<XMLTokenId> token = null;
        ts.move(attrPosition);
        LOGGER.log(Level.FINEST, "current token @" + token);
        while (null == ts.token() || XMLTokenId.WS.equals(ts.token().id()) || XMLTokenId.OPERATOR.equals(ts.token().id()) || !XMLTokenId.VALUE.equals(ts.token().id())) {
            ts.moveNext();
        }
        token = ts.token();
        if (token != null && token.length() > 2) {
            int offset = token.offset(th) + 1;
            int endOffset = offset + token.length() - 1;

            PositionRef startPosition = editorSupport.createPositionRef(offset, Bias.Forward);
            PositionRef endPosition = editorSupport.createPositionRef(endOffset, Bias.Backward);
            bounds = new PositionBounds(startPosition, endPosition);

            LOGGER.log(Level.FINEST, "current token @" + token + ", offset@" + offset + ", endOffset@" + endOffset);
        }
        return bounds;
    }

    protected StyledDocument getDocument() {
        StyledDocument doc = null;
        try {
            DataObject clickDO = DataObject.find(clickFO);
            if (clickDO != null) {
                EditorCookie cookie = clickDO.getCookie(EditorCookie.class);
                if (cookie != null) {
                    doc = cookie.getDocument();
                }
            }
        } catch (DataObjectNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
        return doc;
    }

    // adapted from JSFEditorUtilities
    private CloneableEditorSupport findCloneableEditorSupport() {
        DataObject clickDO = null;
        try {
            clickDO = DataObject.find(clickFO);
        } catch (DataObjectNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
        Node.Cookie obj = clickDO.getCookie(org.openide.cookies.OpenCookie.class);
        if (obj instanceof CloneableEditorSupport) {
            return (CloneableEditorSupport) obj;
        }
        obj = clickDO.getCookie(org.openide.cookies.EditorCookie.class);
        if (obj instanceof CloneableEditorSupport) {
            return (CloneableEditorSupport) obj;
        }
        return null;
    }

    abstract AbstractRefactoring getRefactoring();

    @Override
    public Problem preCheck() {
        if (clickModel.getState() == State.NOT_WELL_FORMED) {
            return new Problem(false, "Invalid Click XML");
        }
        return null;
    }

    abstract Problem prepare(RefactoringElementsBag bag);

    @Override
    public Problem doPrepare(RefactoringElementsBag bag) {
        bag.registerTransaction(new ModificationTransaction(this.clickModel));
        return prepare(bag);
    }

    protected abstract static class ClickRefactoringElement extends SimpleRefactoringElementImplementation {

        protected final ClickModel clickModel;
        protected final FileObject clickFO;

        public ClickRefactoringElement(FileObject clickFO, ClickModel clickApp) {
            this.clickModel = clickApp;
            this.clickFO = clickFO;
        }

        public void performChange() {
        }

        public Lookup getLookup() {
            return Lookup.EMPTY;
        }

        public FileObject getParentFile() {
            return clickFO;
        }

        public String getText() {
            return getDisplayText();
        }

        @Override
        public void undoChange() {
        }
    }
}
