/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click.actions;

import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.web.click.api.ClickFileType;
import org.netbeans.modules.web.click.api.ClickProjectQuery;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

@NbBundle.Messages({
    "GotoClassAction=Go to Page Class"
})
@ActionID(id = "org.netbeans.modules.web.click.actions.GotoClassAction", category = "File")
@ActionRegistration(lazy = false, displayName = "#GotoClassAction")
@ActionReferences(value = {
    @ActionReference(path = "Menu/GoTo", position = 693),
    @ActionReference(path = "Editors/text/html/Popup/goto", position = 800),
    @ActionReference(path = "Editors/text/x-jsp/Popup/goto", position = 800)})
public final class GotoClassAction extends CookieAction {

    @Override
    protected boolean enable(Node[] activatedNodes) {
        if (activatedNodes != null && activatedNodes.length == 1) {
            DataObject dataDO = activatedNodes[0].getLookup().lookup(DataObject.class);
            if (dataDO == null) {
                return false;
            }
            FileObject file = dataDO.getPrimaryFile();

            String ext = file.getExt().toLowerCase();
            if (!ext.equals("jsp") && !ext.equals("htm") && !ext.equals("properties")) {
                return false;
            }

            Project project = FileOwnerQuery.getOwner(file);

            if (project != null) {
                return ClickProjectQuery.isClick(project);
            }
        }
        return false;
    }

    @Override
    protected void performAction(Node[] activatedNodes) {
        if (activatedNodes != null && activatedNodes.length == 1) {
            DataObject dataDO = activatedNodes[0].getLookup().lookup(DataObject.class);
            if (dataDO == null) {
                return;
            }
            FileObject file = dataDO.getPrimaryFile();
            if (file != null) {
                new OpenComponentThread().findAndOpenFile(file, ClickFileType.CLASS);
            }
        }
    }

    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    public String getName() {
        return NbBundle.getMessage(GotoClassAction.class, "CTL_GotoClassAction");
    }

    protected Class[] cookieClasses() {
        return new Class[]{EditorCookie.class};
    }

    @Override
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() Javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}

