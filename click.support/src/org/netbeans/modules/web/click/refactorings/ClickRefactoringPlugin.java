/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click.refactorings;

import java.util.List;
import org.netbeans.modules.refactoring.api.Problem;
import org.netbeans.modules.refactoring.spi.RefactoringElementsBag;
import org.netbeans.modules.refactoring.spi.RefactoringPlugin;

/**
 *
 * @author hantsy
 */
public class ClickRefactoringPlugin implements RefactoringPlugin {

    private static final java.util.logging.Logger LOGGER;

    static {
        LOGGER = java.util.logging.Logger.getLogger("org.netbeans.modules.web.click.refactorings.ClickRenameRefactoringPlugin");
        org.netbeans.modules.web.click.refactorings.ClickRefactoringPlugin.initLoggerHandlers();
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
    List<ClickRefactoring> refactorings;

    public ClickRefactoringPlugin(List<ClickRefactoring> refactorings) {
        this.refactorings = refactorings;
    }

    public Problem preCheck() {
        Problem result = null;
        for (ClickRefactoring each : refactorings) {
            result = RefactoringUtil.addToEnd(each.preCheck(), result);
        }
        return result;
    }

    @Override
    public Problem checkParameters() {
        return null;
    }

    @Override
    public Problem fastCheckParameters() {
        return null;
    }

    @Override
    public void cancelRequest() {
    }

    @Override
    public Problem prepare(RefactoringElementsBag bag) {
       // bag.registerTransaction(new ModificationTransaction(model));
        Problem result = null;
        for (ClickRefactoring each : refactorings) {
            result = RefactoringUtil.addToEnd(each.doPrepare(bag), result);
        }
        return result;
    }

   
}
