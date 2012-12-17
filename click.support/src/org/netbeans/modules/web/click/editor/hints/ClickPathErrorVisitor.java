/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click.editor.hints;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position.Bias;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.api.xml.lexer.XMLTokenId;
import org.netbeans.modules.web.click.api.model.ClickApp;
import org.netbeans.modules.web.click.api.model.ClickComponent;
import org.netbeans.modules.web.click.api.model.ClickModel;
import org.netbeans.modules.web.click.api.model.ClickVisitor;
import org.netbeans.modules.web.click.api.model.Controls;
import org.netbeans.modules.web.click.api.model.FileUploadService;
import org.netbeans.modules.web.click.api.model.Format;
import org.netbeans.modules.web.click.api.model.LogService;
import org.netbeans.modules.web.click.api.model.Page;
import org.netbeans.modules.web.click.api.model.Pages;
import org.netbeans.modules.web.click.api.model.TemplateService;
import org.netbeans.modules.web.click.api.model.impl.ClickAttributes;
import org.netbeans.modules.web.click.editor.JavaUtils;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.click.ClickConfigUtilities;
import org.netbeans.modules.web.click.api.model.ClassNameComponent;
import org.netbeans.modules.web.click.api.model.Control;
import org.netbeans.modules.web.click.editor.ClickEditorUtilities;
import org.netbeans.modules.xml.xam.Model;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.Severity;
import org.openide.filesystems.FileObject;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;

/**
 *
 * @author hantsy
 */
public class ClickPathErrorVisitor extends ClickVisitor.Deep {

    private static final java.util.logging.Logger LOGGER;

    static {
        LOGGER = java.util.logging.Logger.getLogger("org.netbeans.modules.web.click.editor.hints.ClickPathErrorVisitor");
//        org.netbeans.modules.web.click.editor.hints.ClickPathErrorVisitor.initLoggerHandlers();
//    }
//
//    private static final void initLoggerHandlers() {
//        java.util.logging.Handler[] handlers = LOGGER.getHandlers();
//        boolean hasConsoleHandler = false;
//        for (java.util.logging.Handler handler : handlers) {
//            if (handler instanceof java.util.logging.ConsoleHandler) {
//                hasConsoleHandler = true;
//            }
//        }
//        if (!hasConsoleHandler) {
//            LOGGER.addHandler(new java.util.logging.ConsoleHandler());
//        }
//        LOGGER.setLevel(java.util.logging.Level.FINEST);
    }
    List<ErrorDescription> errList = null;
    // List<ErrorDescription> clzErrors = null;
    FileObject docFO = null;
    Document document = null;
    TokenHierarchy<String> th;
    TokenSequence<XMLTokenId> ts;
    ClickModel model = null;
    final String WARN_PATH = "Invalid Path";
    final String WARN_CLASS = "Class not found";

    public ClickPathErrorVisitor(Document doc) {
        this.document = doc;
        this.docFO = NbEditorUtilities.getFileObject(document);
        this.model = ClickConfigUtilities.getClickModel(docFO, false);

//        docFO.addFileChangeListener(FileUtil.weakFileChangeListener(
//                new FileChangeAdapter() {
//
//                    @Override
//                    public void fileChanged(FileEvent fe) {
//                        RequestProcessor.getDefault().post(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                refresh();
//                            }
//                        });
//                    }
//                }, docFO));
        // if (errList == null) {
        this.errList = new ArrayList<ErrorDescription>();
        if (model.getState() == Model.State.VALID) {
            scanClickError();
            //      }
        }
    }

    @Override
    public void accept(ClickApp component) {
        visitChild(component);
    }

    @Override
    public void accept(Pages component) {
        visitChild(component);
    }

    @Override
    public void accept(Controls component) {
        visitChild(component);
    }

    public void refresh() {
        if (errList == null) {
            this.errList = new ArrayList<ErrorDescription>();
        } else {
            this.errList.clear();
        }
        scanClickError();
    }

    public List<ErrorDescription> getErrorDescriptions() {
        return this.errList;
    }

    void scanClickError() {

        try {
            th = TokenHierarchy.create(document.getText(0, document.getLength()), XMLTokenId.language());
            ts = th.tokenSequence(XMLTokenId.language());
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
        ClickApp root = model.getRootComponent();
        root.accept(this);
    }

    @Override
    public void accept(final Page component) {
        final String path = component.getPath();

        if (path != null && !"".equals(path)) {
            WebModule wm = WebModule.getWebModule(docFO);
            if (wm == null) {
                return;
            }

            FileObject pageFO = ClickEditorUtilities.findPageByPath(wm.getDocumentBase(), path);
            if (pageFO == null || pageFO.getNameExt().lastIndexOf(".") == -1) {
                try {
                    computeAttributeValuePosition(component, ClickAttributes.PATH.getName());
                    errList.add(ErrorDescriptionFactory.createErrorDescription(Severity.WARNING,
                            WARN_PATH,
                            document,
                            NbDocument.createPosition(document, startPosition, Bias.Forward),
                            NbDocument.createPosition(document, endPosition, Bias.Backward)));
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }

        scanClassnameErrors(component);
    }

    @Override
    public void accept(Control component) {
        scanClassnameErrors(component);
    }

    @Override
    public void accept(Format component) {
        scanClassnameErrors(component);
    }

    @Override
    public void accept(FileUploadService component) {
        scanClassnameErrors(component);
    }

    @Override
    public void accept(LogService component) {
        scanClassnameErrors(component);
    }

    @Override
    public void accept(TemplateService component) {
        scanClassnameErrors(component);
    }
    private int startPosition;
    private int endPosition;

    void computeAttributeValuePosition(ClickComponent component, String attribute) {
        LOGGER.log(Level.FINEST, "@@@ClickPathErrorVisitor@ component@ " + component + ", attribute@ " + attribute);
        int attrPosition = component.findAttributePosition(attribute);
        ts.move(attrPosition);

        LOGGER.log(Level.FINEST, "attribute token text @" + ts.token());
        while (ts.token() == null || ts.token().id() == XMLTokenId.WS || !attribute.equals(ts.token().text().toString())) {
            ts.moveNext();// move to attribute.
        }
        LOGGER.log(Level.FINEST, "move to attribute@attribute token text @" + ts.token());

        ts.moveNext();//move to '=' or ' ' after attribute name

        while (ts.token() == null || ts.token().id() == XMLTokenId.WS || ts.token().id() == XMLTokenId.OPERATOR) {
            ts.moveNext();
        }
        LOGGER.log(Level.FINEST, "move to attribute value @attribute token text @" + ts.token());

        Token<XMLTokenId> valueToken = ts.token();
        if (valueToken != null && valueToken.length() > 2) {
            this.startPosition = valueToken.offset(th) + 1;
            this.endPosition = startPosition + valueToken.length() - 2;
        }
        LOGGER.log(Level.FINEST, "@@@ClickPathErrorVisitor@computeAttributeValuePosition, startPosition@ " + startPosition + ", endPosition@ " + endPosition);
    }

    private void scanClassnameErrors(final ClickComponent component) {
        if (component == null) {
            return;
        }
        String clz = null;
        if (component instanceof ClassNameComponent) {
            clz = ((ClassNameComponent) component).getClassName();
        } 

        final String classname = clz;
        if (classname != null && !"".equals(classname)) {
            JavaSource source = JavaUtils.getJavaSource(docFO);
            try {
                source.runUserActionTask(new Task<CompilationController>() {

                    @Override
                    public void run(CompilationController cc) throws Exception {
                        cc.toPhase(JavaSource.Phase.ELEMENTS_RESOLVED);
                        Elements elements = cc.getElements();
                        if (elements != null) {
                            TypeElement element = elements.getTypeElement(classname.trim());
                            if (element == null) {
                                computeAttributeValuePosition(component, ClickAttributes.CLASSNAME.getName());
                                errList.add(
                                            ErrorDescriptionFactory.createErrorDescription(
                                                Severity.WARNING,
                                                WARN_CLASS,
                                                document,
                                                document.createPosition(startPosition),
                                                document.createPosition( endPosition)
                                                )
                                            );
                            }
                        }
                    }
                }, true);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
