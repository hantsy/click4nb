/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click.editor.completion;

import java.util.logging.Level;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;

/**
 *
 * @author hantsy
 */
public class ClickCompletionProvider implements CompletionProvider {

    private static final java.util.logging.Logger LOGGER;

    static {
        LOGGER = java.util.logging.Logger.getLogger("org.netbeans.modules.web.click.editor.completion.ClickCompletionProvider");
        org.netbeans.modules.web.click.editor.completion.ClickCompletionProvider.initLoggerHandlers();
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

    public CompletionTask createTask(int queryType, JTextComponent component) {
        LOGGER.log(Level.FINEST, "@createTask for completion@");
        if ((queryType & COMPLETION_QUERY_TYPE) == COMPLETION_QUERY_TYPE) {
            LOGGER.log(Level.FINEST, "@create AsyncCompletionTask@");
            return new AsyncCompletionTask(new ClickConfigCompletionQuery(queryType), component);
        }

        return null;
    }

    public int getAutoQueryTypes(JTextComponent component, String typedText) {
        return 0;
    }

    class ClickConfigCompletionQuery extends AsyncCompletionQuery {

        int queryType;
        JTextComponent component;
        int caretOffset;
        private volatile Completor completor;

        public ClickConfigCompletionQuery(int type) {
            queryType = type;
        }

        @Override
        protected void preQueryUpdate(JTextComponent component) {
            this.component = component;
        }

        @Override
        protected void prepareQuery(JTextComponent component) {
            this.component = component;
        }

        @Override
        protected void query(CompletionResultSet resultSet, Document doc, int caretOffset) {
            if (doc == null || caretOffset == -1) {
                return;
            }
            CompletionContext context = new CompletionContext(doc, caretOffset, queryType);

            LOGGER.log(Level.FINEST, "completion context @" + context);

            if (!context.isValid()) {
                resultSet.finish();
                return;
            }

            completor = CompletorRegistry.getDefault().getCompletor(context);
            if (completor != null) {
                CompletionResult result = completor.complete(context);
                populateResultSet(resultSet, result);
            }

            resultSet.finish();
        }

        @Override
        protected boolean canFilter(JTextComponent component) {
            if (completor == null) {
                return false;
            }

            boolean retVal = completor.canFilter(new CompletionContext(component.getDocument(),
                    component.getCaretPosition(), queryType));
            if (!retVal) {
                completor.cancel();
            }

            return retVal;
        }

        @Override
        protected void filter(CompletionResultSet resultSet) {
            CompletionContext context = new CompletionContext(component.getDocument(),
                    component.getCaretPosition(), queryType);
            CompletionResult result = completor.filter(context);
            populateResultSet(resultSet, result);
            resultSet.finish();
        }

        private void populateResultSet(CompletionResultSet resultSet, CompletionResult result) {
            if (result == CompletionResult.NONE) {
                return;
            }

            resultSet.addAllItems(result.getItems());
            if (completor.getAnchorOffset() != -1) {
                resultSet.setAnchorOffset(completor.getAnchorOffset());
            }

            if (result.hasAdditionalItems()) {
                resultSet.setHasAdditionalItems(true);
                resultSet.setHasAdditionalItemsText(result.getAdditionalItemsText());
            }
        }
    }
}
