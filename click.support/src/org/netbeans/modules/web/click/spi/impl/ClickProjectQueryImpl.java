package org.netbeans.modules.web.click.spi.impl;

import org.netbeans.api.project.Project;
import org.netbeans.modules.web.click.ClickConfigUtilities;
import org.netbeans.modules.web.click.ClickConstants;
import org.netbeans.modules.web.click.spi.ClickProjectQueryImplementation;
import org.openide.filesystems.FileObject;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = org.netbeans.modules.web.click.spi.ClickProjectQueryImplementation.class)
public class ClickProjectQueryImpl implements ClickProjectQueryImplementation {

    @Override
    public boolean isClick(Project project) {
        FileObject clickFO = ClickConfigUtilities.getClickConfigFile(project, ClickConstants.DEFAULT_CLICK_APP_CONFIG_FILE);

        return clickFO != null;
    }
}
