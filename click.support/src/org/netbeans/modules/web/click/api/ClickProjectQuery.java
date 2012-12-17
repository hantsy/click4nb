package org.netbeans.modules.web.click.api;

import java.util.Collection;
import java.util.logging.Level;
import org.netbeans.api.project.Project;
import org.openide.util.Lookup;
import org.netbeans.modules.web.click.spi.ClickProjectQueryImplementation;

public class ClickProjectQuery {
    private static final java.util.logging.Logger LOGGER;
    static {
        LOGGER = java.util.logging.Logger.getLogger("org.netbeans.modules.web.click.api.ClickProjectQuery");
        org.netbeans.modules.web.click.api.ClickProjectQuery.initLoggerHandlers();
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

    public static boolean isClick(Project project) {
        Collection<? extends ClickProjectQueryImplementation> c =
                Lookup.getDefault().lookupAll(ClickProjectQueryImplementation.class);
        boolean result = false;
        for (ClickProjectQueryImplementation impl : c) {
            result = impl.isClick(project);
            if (result) {
                break;
            }
        }

        LOGGER.log(Level.FINEST, "Click project query@"+ result);

        return result;
    }
}
