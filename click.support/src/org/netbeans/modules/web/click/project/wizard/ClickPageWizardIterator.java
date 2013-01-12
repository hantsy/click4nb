/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click.project.wizard;

import java.awt.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.templates.TemplateRegistration;
import org.netbeans.api.templates.TemplateRegistrations;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;


//@NbBundle.Messages({
//    "CTL_Click=Apache Click"
//})
//@TemplateRegistrations(value = {
//    @TemplateRegistration(category = "servlet-types", content = {"../template/Page.java.template"}, folder = "Click", displayName="#CTL_Click")})
public final class ClickPageWizardIterator implements WizardDescriptor.InstantiatingIterator<WizardDescriptor> {

    private int index;
    private WizardDescriptor wizard;
    private ClickPageWizardPanel1 pagePanelData;
    private List<WizardDescriptor.Panel<WizardDescriptor>> panels;
    // private Project project;

    /**
     * Initialize panels representing individual wizard's steps and sets various
     * properties for them influencing wizard appearance.
     */
    private List<WizardDescriptor.Panel<WizardDescriptor>> getPanels() {

//        Sources sources = ProjectUtils.getSources(project);
//        SourceGroup[] groups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);

//        WizardDescriptor.Panel javaPanel;
//        if (groups != null && groups.length > 0) {
//            javaPanel = JavaTemplates.createPackageChooser(project, groups, null);
//        } else {
//            javaPanel = Templates.createSimpleTargetChooser(project, groups, null);
//        }

        if (panels == null) {
            pagePanelData = new ClickPageWizardPanel1(wizard);
            panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
            panels.add(pagePanelData);

            String[] steps = createSteps();
            for (int i = 0; i < panels.size(); i++) {
                Component c = panels.get(i).getComponent();
                if (steps[i] == null) {
                    // Default step name to component name of panel. Mainly
                    // useful for getting the name of the target chooser to
                    // appear in the list of steps.
                    steps[i] = c.getName();
                }
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    // Sets step number of a component
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, new Integer(i)); // NOI18N
                    // Sets steps names for a panel
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps); // NOI18N
                    // Turn on subtitle creation on each step
                    jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE); // NOI18N
                    // Show steps on the left side with the image on the background
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE); // NOI18N
                    // Turn on numbering of all steps
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE); // NOI18N
                    // TODO if using org.openide.dialogs >= 7.8, can use WizardDescriptor.PROP_*:
                }
            }
        }
        return panels;
    }

    @Override
    public Set<?> instantiate() throws IOException {
        return pagePanelData.generateFiles();
    }

    @Override
    public void initialize(WizardDescriptor wizard) {
        this.wizard = wizard;
        //this.project = Templates.getProject(wizard);
    }

    @Override
    public void uninitialize(WizardDescriptor wizard) {
        panels = null;
    }

    @Override
    public WizardDescriptor.Panel<WizardDescriptor> current() {
        return getPanels().get(index);
    }

    @Override
    public String name() {
        return index + 1 + ". from " + getPanels().size();
    }

    @Override
    public boolean hasNext() {
        return index < getPanels().size() - 1;
    }

    @Override
    public boolean hasPrevious() {
        return index > 0;
    }

    @Override
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }

    @Override
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }

    // If nothing unusual changes in the middle of the wizard, simply:
    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }

    // If something changes dynamically (besides moving between panels), e.g.
    // the number of panels changes in response to user input, then uncomment
    // the following and call when needed: fireChangeEvent();
    /*
     private Set<ChangeListener> listeners = new HashSet<ChangeListener>(1); // or can use ChangeSupport in NB 6.0
     public final void addChangeListener(ChangeListener l) {
     synchronized (listeners) {
     listeners.add(l);
     }
     }
     public final void removeChangeListener(ChangeListener l) {
     synchronized (listeners) {
     listeners.remove(l);
     }
     }
     protected final void fireChangeEvent() {
     Iterator<ChangeListener> it;
     synchronized (listeners) {
     it = new HashSet<ChangeListener>(listeners).iterator();
     }
     ChangeEvent ev = new ChangeEvent(this);
     while (it.hasNext()) {
     it.next().stateChanged(ev);
     }
     }
     */
    // You could safely ignore this method. Is is here to keep steps which were
    // there before this wizard was instantiated. It should be better handled
    // by NetBeans Wizard API itself rather than needed to be implemented by a
    // client code.
    private String[] createSteps() {
        String[] beforeSteps = null;
        Object prop = wizard.getProperty("WizardPanel_contentData");
        if (prop != null && prop instanceof String[]) {
            beforeSteps = (String[]) prop;
        }

        if (beforeSteps == null) {
            beforeSteps = new String[0];
        }

        String[] res = new String[(beforeSteps.length - 1) + panels.size()];
        for (int i = 0; i < res.length; i++) {
            if (i < (beforeSteps.length - 1)) {
                res[i] = beforeSteps[i];
            } else {
                res[i] = panels.get(i - beforeSteps.length + 1).getComponent().getName();
            }
        }
        return res;
    }
}
