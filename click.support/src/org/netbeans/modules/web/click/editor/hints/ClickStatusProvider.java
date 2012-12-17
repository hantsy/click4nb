/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click.editor.hints;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.swing.text.Document;
import org.netbeans.modules.web.click.api.model.ClickModel;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.web.click.ClickConfigUtilities;
import org.netbeans.modules.xml.xam.Model;
import org.netbeans.spi.editor.errorstripe.UpToDateStatus;
import org.netbeans.spi.editor.errorstripe.UpToDateStatusProvider;
import org.netbeans.spi.editor.errorstripe.UpToDateStatusProviderFactory;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.HintsController;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.RequestProcessor;

/**
 *
 * @author hantsy
 */
public class ClickStatusProvider implements UpToDateStatusProviderFactory {

    private static final java.util.logging.Logger LOGGER;

    static {
        LOGGER = java.util.logging.Logger.getLogger("org.netbeans.modules.web.click.editor.hints.ClickStatusProvider");
        org.netbeans.modules.web.click.editor.hints.ClickStatusProvider.initLoggerHandlers();
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

    @Override
    public UpToDateStatusProvider createUpToDateStatusProvider(Document document) {
        LOGGER.log(Level.FINEST, ">>>>>>>createUpToDateStatusProvider<<<<<");
        FileObject docFO = NbEditorUtilities.getFileObject(document);

        if (docFO != null&& "text/x-clickapp+xml".equals(docFO.getMIMEType())
                ) {
            LOGGER.log(Level.FINEST, "@@@create status provider@@@");
            return new StatusCreator(document);
        }
        return null;
    }

    public static class StatusCreator extends UpToDateStatusProvider {

        Document document;
        private ClickModel model;
        private FileChangeListener listener;
        ClickPathErrorVisitor visitor;
        FileObject docFO;

        public StatusCreator(Document doc) {
            this.document = doc;
            init();

            this.listener = new FileChangeAdapter() {

                @Override
                public void fileChanged(FileEvent fe) {
                    RequestProcessor.getDefault().post(new Runnable() {

                        @Override
                        public void run() {
                            init();
                            visitor.refresh();
                            checkHints();
                        }
                    });
                }
            };

            //((ClickModelImpl) model).setAutoSyncActive(true);

            docFO.addFileChangeListener(FileUtil.weakFileChangeListener(listener, docFO));
            RequestProcessor.getDefault().post(new Runnable() {

                @Override
                public void run() {
                    checkHints();
                }
            });

//            checkHints();
        }

        private void init() {
            //this.lastDocument = document;
            this.docFO = NbEditorUtilities.getFileObject(document);
            this.visitor = new ClickPathErrorVisitor(this.document);
            this.model = getModel();
        }

        private void checkHints() {
            LOGGER.log(Level.FINEST, "====start checking errors====");
            HintsController.setErrors(document, "Click XML Errors", findHints());
        }

        private List<ErrorDescription> findHints() {
            List<ErrorDescription> errList = new ArrayList<ErrorDescription>();
            if (!model.getState().equals(Model.State.VALID)) {
                return errList;
            }
            errList = visitor.getErrorDescriptions();
            return errList;
        }

        private ClickModel getModel() {
            FileObject fo = NbEditorUtilities.getFileObject(document);
            if (fo != null) {
                return ClickConfigUtilities.getClickModel(fo, false);
            }
            return null;
        }

        @Override
        public UpToDateStatus getUpToDate() {
            return UpToDateStatus.UP_TO_DATE_OK;
        }
    }
}
