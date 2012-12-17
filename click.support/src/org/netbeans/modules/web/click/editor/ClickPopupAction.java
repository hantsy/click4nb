/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click.editor;

import java.awt.Dialog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.web.click.wizards.ClickPageWizardPanel1;
import org.netbeans.spi.editor.codegen.CodeGenerator;
import org.netbeans.spi.editor.codegen.CodeGeneratorContextProvider;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;

public class ClickPopupAction implements CodeGenerator {

    JTextComponent textComp;

    /**
     * 
     * @param context containing JTextComponent and possibly other items registered by {@link CodeGeneratorContextProvider}
     */
    private ClickPopupAction(Lookup context) { // Good practice is not to save Lookup outside ctor
        textComp = context.lookup(JTextComponent.class);
    }

    public static class Factory implements CodeGenerator.Factory {

        @Override
        public List<? extends CodeGenerator> create(Lookup context) {
            return Collections.singletonList(new ClickPopupAction(context));
        }
    }

    /**
     * The name which will be inserted inside Insert Code dialog
     */
    @Override
    public String getDisplayName() {
        return "New Page";
    }

    /**
     * This will be invoked when user chooses this Generator from Insert Code
     * dialog
     */
    @Override
    public void invoke() {
        ClickPageWizardDescriptor descriptor = new ClickPageWizardDescriptor();
        Dialog dialog = DialogDisplayer.getDefault().createDialog(descriptor);
        dialog.setVisible(true);
        dialog.toFront();
        if (descriptor.getValue() == WizardDescriptor.FINISH_OPTION) {
            final Set<FileObject> files = descriptor.getPanel().generateFiles();

            if (files != null && !files.isEmpty()) {
                RequestProcessor.getDefault().post(new Runnable() {

                    @Override
                    public void run() {
                        for (FileObject o : files) {
                            ClickEditorUtilities.openInEditor(o);
                        }
                    }
                });
            }
        }
    }

    private class ClickPageWizardDescriptor extends WizardDescriptor {

        ClickPageWizardPanel1 panel;

        public ClickPageWizardDescriptor() {
            this.panel = initPanel();
            List<Panel<WizardDescriptor>> panels =
                    new ArrayList<Panel<WizardDescriptor>>();
            panels.add(panel);

            this.setPanelsAndSettings(new ArrayIterator<WizardDescriptor>(panels), this);

            this.setTitle(getDisplayName());
            this.putProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.FALSE);
            this.putProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
            this.putProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.FALSE);
        }

        private ClickPageWizardPanel1 initPanel() {
            Project project = FileOwnerQuery.getOwner(getFO());
            Sources sources = ProjectUtils.getSources(project);
            SourceGroup[] srcGroups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
            ClickPageWizardPanel1 localPanel = new ClickPageWizardPanel1(project, srcGroups);
            JComponent view = localPanel.getComponent();

            view.putClientProperty(
                    WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, new Integer(0));
             view.putClientProperty(
                    WizardDescriptor.PROP_CONTENT_DATA, new String[]{"New Click Page"});
            view.putClientProperty(
                    WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
            view.putClientProperty(
                    WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
            view.putClientProperty(
                    WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE);

            return localPanel;
        }

        public ClickPageWizardPanel1 getPanel() {
            return this.panel;
        }

        private FileObject getFO() {
            Document doc = textComp.getDocument();
            if (doc != null) {
                return NbEditorUtilities.getFileObject(doc);
            }
            return null;
        }
    }
}
