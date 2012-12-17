/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click.editor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.netbeans.modules.web.click.ClickConfigUtilities;
import org.netbeans.modules.web.click.api.model.ClickModel;
import org.netbeans.modules.web.click.api.model.impl.ClickModelImpl;
import org.netbeans.modules.web.click.editor.hints.ClickPathErrorVisitor;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.Severity;
import org.netbeans.spi.tasklist.FileTaskScanner;
import org.netbeans.spi.tasklist.Task;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 *
 * @author hantsy
 */
public class TaskListBridge extends FileTaskScanner {

    private static final String TASKLIST_ERROR = "nb-tasklist-error"; //NOI18N
    private static final String TASKLIST_WARNING = "nb-tasklist-warning"; //NOI18N
    private static final java.util.logging.Logger LOGGER;

    static {
        LOGGER = java.util.logging.Logger.getLogger("org.netbeans.modules.web.click.editor.TaskListBridge");
        org.netbeans.modules.web.click.editor.TaskListBridge.initLoggerHandlers();
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

    public TaskListBridge() {
        super(NbBundle.getMessage(TaskListBridge.class, "LBL_TaskList_DisplayName"),
                NbBundle.getMessage(TaskListBridge.class, "LBL_TaskList_Desc"),
                null);
        LOGGER.log(Level.FINEST, "@@@@Enter TaskListBridge constructure");
    }

    @Override
    public List<? extends Task> scan(FileObject resource) {
        if (resource != null && "text/x-clickapp+xml".equals(resource.getMIMEType())) {
            LOGGER.log(Level.FINEST, "scan click.xml");
            List<Task> tasks = new ArrayList<Task>();
            ClickModel model = ClickConfigUtilities.getClickModel(resource, false);
            List<ErrorDescription> errs = new ClickPathErrorVisitor(((ClickModelImpl) model).getBaseDocument()).getErrorDescriptions();
            for (ErrorDescription error : errs) {
                try {
                    Task task = Task.create(resource,
                            severityToTaskListString(error.getSeverity()),
                            error.getDescription(),
                            error.getRange().getBegin().getLine() + 1);
                    LOGGER.log(Level.FINEST, "create task@" + task);
                    tasks.add(task);
                } catch (IOException e) {
                    LOGGER.log(Level.FINEST, "Error while converting errors to tasklist", e);
                }
            }
            return tasks;
        }
        return Collections.<Task>emptyList();
    }

    public void attach(Callback callback) {
        LOGGER.log(Level.FINEST, "@@attach");

    }

    @Override
    public void notifyPrepare() {
        LOGGER.log(Level.FINEST, "notifyPrepare");
    }

    /** @todo add description */
    @Override
    public void notifyFinish() {
        LOGGER.log(Level.FINEST, "notifyFinish");

    }

    private static String severityToTaskListString(Severity severity) {
        if (severity == Severity.ERROR) {
            return TASKLIST_ERROR;
        }
        return TASKLIST_WARNING;
    }
}
