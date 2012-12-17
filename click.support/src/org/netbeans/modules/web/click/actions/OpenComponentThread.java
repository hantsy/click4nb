/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click.actions;

import java.util.logging.Logger;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.web.click.api.ClickComponentQuery;
import org.netbeans.modules.web.click.api.ClickFileType;
import org.netbeans.modules.web.click.editor.ClickEditorUtilities;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author hantsy
 */
public class OpenComponentThread implements Runnable {

    Logger log = Logger.getLogger(OpenComponentThread.class.getName());
    FileObject activeFileObject;
    ClickFileType typeToFind;

    public void findAndOpenFile(FileObject file, ClickFileType type) {
        this.activeFileObject = file;
        this.typeToFind = type;
        RequestProcessor.getDefault().post(this);
    }

    @Override
    public void run() {
        FileObject[] targetFO = ClickComponentQuery.findComponent(activeFileObject, typeToFind);

        if (targetFO == null || targetFO.length == 0) {
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(OpenComponentThread.class, "MSG_FileNotFound"));
        } else if (targetFO.length == 1) {
            openFile(targetFO[0]);
        } else {
            log.finest("find more than one file... and popup a window to select.");
            log.finest("target file size@" + targetFO.length);

            Project project = FileOwnerQuery.getOwner(activeFileObject);
            String[] filePaths = new String[targetFO.length];
            for (int i = 0; i < targetFO.length; i++) {
                filePaths[i] = FileUtil.getRelativePath(project.getProjectDirectory(), targetFO[i]);
            }

            log.finest("files path @@" + filePaths);
            TemplateSelectionPanel panel = new TemplateSelectionPanel(filePaths);
            DialogDescriptor d = new DialogDescriptor(panel, "Select a file to open", true, null);

            FileObject selectedResource = null;
            if (DialogDescriptor.OK_OPTION == DialogDisplayer.getDefault().notify(d)) {
                selectedResource = targetFO[panel.getSelectionIndex()];
                if (selectedResource != null) {
                    openFile(selectedResource);
                }
            }
        }
    }

    private void openFile(FileObject fo) {
        ClickEditorUtilities.openInEditor(fo);
    }
}
