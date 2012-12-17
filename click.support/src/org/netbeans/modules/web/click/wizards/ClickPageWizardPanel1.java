/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click.wizards;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.modules.web.click.ClickConfigUtilities;
import org.netbeans.modules.web.click.ClickConstants;
import org.netbeans.modules.web.click.api.model.ClickApp;
import org.netbeans.modules.web.click.api.model.ClickComponentFactory;
import org.netbeans.modules.web.click.api.model.ClickModel;
import org.netbeans.modules.web.click.api.model.Page;
import org.netbeans.modules.web.click.api.model.Pages;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.click.api.ClickProjectQuery;
import org.netbeans.modules.web.click.api.model.ClickModelFactory;
import org.netbeans.modules.xml.retriever.catalog.Utilities;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

public class ClickPageWizardPanel1 implements WizardDescriptor.Panel<WizardDescriptor>, WizardDescriptor.FinishablePanel<WizardDescriptor>, ChangeListener {

    private final static Logger log = Logger.getLogger(ClickPageWizardPanel1.class.getName());
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private ClickPageVisualPanel1 component;
    private Project project;
    private SourceGroup[] groups;
    private WizardDescriptor wizard;

    public ClickPageWizardPanel1(Project project, SourceGroup[] groups) {
        this.project = project;
        this.groups = groups;

    }

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    public ClickPageVisualPanel1 getComponent() {

        if (component == null) {
            component = new ClickPageVisualPanel1(project, groups);
            component.addChangeListener(this);
        }

        return component;
    }

    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx(SampleWizardPanel1.class);
    }

    public String getPagePath() {
        String templateFilePath = component.getTemplateFilePath();

        int extIndex = templateFilePath.lastIndexOf(".");
        if (extIndex != -1) {
            templateFilePath = templateFilePath.substring(0, extIndex) + ".htm";
        } else {
            templateFilePath = templateFilePath + ".htm";
        }
        return templateFilePath;
    }

    public Set<FileObject> generateFiles() {
        final String pageClassFqn = component.getPackageName() + "." + component.getPageClassName();
        final String templatePagePath = getPagePath();

        Set<FileObject> files = new HashSet<FileObject>();
        WebModule wm = WebModule.getWebModule(project.getProjectDirectory());
        FileObject webRoot = wm.getDocumentBase();
        FileObject pkgFO = null;
        try {
            pkgFO = FileUtil.createFolder(component.getRootFolder(), component.getPackageFileName());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        String templateFolder = ClickConstants.BASE_TEMPLATES_DIR;
        Map<String, String> templateProperties = new HashMap<String, String>();
        templateProperties.put("package", component.getPackageName());
        templateProperties.put("classname", component.getPageClassName());
        templateProperties.put("superclass", component.getPageClassSuperClassName());

        if ("Simple Form".equals(component.getTemplateType())) {
            templateProperties.put("template", "simple-form");
        } else {
            templateProperties.put("template", "blank");
        }
        // if (component.requireCreatePageClass()) {
        FileObject templateFolderFO = FileUtil.getConfigFile(templateFolder);
        DataObject classNameDO = null;
        DataObject targetPageClassDO = null;

        DataFolder targetFolderDF = DataFolder.findFolder(pkgFO);
        try {
            classNameDO = DataObject.find(templateFolderFO.getFileObject("Page.java"));

        } catch (DataObjectNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
        try {
            targetPageClassDO = classNameDO.createFromTemplate(targetFolderDF, component.getPageClassName(), templateProperties);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        files.add(targetPageClassDO.getPrimaryFile());

        if (component.requireCreateTemplateFile()) {

            String templateName = "Page.htm";
            String filePath = component.getTemplateFilePath();

            if (filePath.endsWith(".jsp")) {
                templateName = "Page.jsp";
            }

            if (filePath.endsWith(".htm") || filePath.endsWith(".jsp")) {
                filePath = filePath.substring(0, filePath.lastIndexOf("."));
            }

            DataObject pageTempalteDO = null;
            try {
                pageTempalteDO = DataObject.find(templateFolderFO.getFileObject(templateName));
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
            DataObject targetPageDO = null;
            try {
                targetPageDO = pageTempalteDO.createFromTemplate(DataFolder.findFolder(webRoot), filePath, templateProperties);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            files.add(targetPageDO.getPrimaryFile());
        }

        if (component.requireCreateTemplateFile() && component.requireAddMappingToClickXML()) {
            FileObject clickFO = ClickConfigUtilities.getClickConfigFile(project, ClickConstants.DEFAULT_CLICK_APP_CONFIG_FILE);
            if (clickFO != null) {

                ClickModel model = ClickModelFactory.getInstance().createFreshModel(Utilities.getModelSource(clickFO, true));
                ClickComponentFactory factory = model.getFactory();
                model.startTransaction();
                ClickApp root = model.getRootComponent();

                List<Pages> pagesList = root.getPagesList();
                Pages targetPages = null;
                if (pagesList != null) {
                    for (Pages pagesCom : pagesList) {
                        if (pagesCom.getPackage() == null || pagesCom.getPackage().equals(component.getPackageName()) || component.getPackageName().startsWith(pagesCom.getPackage() + ".")) {
                            targetPages = pagesCom;
                            break;
                        }
                    }
                }

                if (targetPages == null) {
                    targetPages = factory.createPages();
                    root.addPages(targetPages);
                }

                Page newPage = factory.createPage();
                newPage.setClassName(pageClassFqn);
                newPage.setPath(templatePagePath);

                targetPages.addPage(newPage);

                model.endTransaction();
            }
        }
        return files;
    }

    public boolean isValid() {
        setErrorMessage(null);
        setInfoMessage(null);
        if (component.getPageClassName() == null || "".equals(component.getPageClassName())) {
            setErrorMessage("INFO_JavaTargetChooser_ProvideClassName");
            return false;
        } else if (!isValidTypeIdentifier(component.getPageClassName())) {
            setErrorMessage("ERR_JavaTargetChooser_InvalidClass");
            return false;
        } else if (!isValidPackageName(component.getPackageName())) {
            setErrorMessage("ERR_JavaTargetChooser_InvalidPackage");
            return false;
        } else if (!isValidPackage(component.getRootFolder(), component.getPackageName())) {
            setErrorMessage("ERR_JavaTargetChooser_InvalidFolder");
            return false;
        } else if (existClass()) {
            setErrorMessage("INFO_PageClassExist");
            return false;
        } else if (component.getPageClassSuperClassName() == null || "".equals(component.getPageClassSuperClassName().trim())) {
            setErrorMessage("INFO_JavaTargetChooser_SuperClassName");
            return false;
        } else if (component.requireCreateTemplateFile() && (component.getTemplateFileName() == null || "".equals(component.getTemplateFileName().trim()))) {
            setErrorMessage("INFO_JavaTargetChooser_TemplateFilename");
            return false;
        }

// If it is always OK to press Next or Finish, then:
        return true;
        // If it depends on some condition (form filled out...), then:
        // return someCondition();
        // and when this condition changes (last form field filled in...) then:
        // fireChangeEvent();
        // and uncomment the complicated stuff below.
    }

    public boolean existClass() {
        String packageFileName = component.getPackageFileName();
        if (packageFileName != null && packageFileName.trim().length() > 0 && !packageFileName.endsWith("/")) {
            packageFileName += "/";
        }

        return component.getRootFolder().getFileObject(packageFileName + component.getPageClassName() + ".java") != null;
    }

    public final void addChangeListener(ChangeListener l) {
        changeSupport.addChangeListener(l);
    }

    public final void removeChangeListener(ChangeListener l) {
        changeSupport.removeChangeListener(l);
    }
    private ChangeSupport changeSupport = new ChangeSupport(this);

    /*
    private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1); // or can use ChangeSupport in NB 6.0
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
    // You can use a settings object to keep track of state. Normally the
    // settings object will be the WizardDescriptor, so you can use
    // WizardDescriptor.getProperty & putProperty to store information entered
    // by the user.
    public void readSettings(WizardDescriptor settings) {
        this.wizard = settings;

        if (component != null) {
            component.initValues(null);

            Object substitute = component.getClientProperty("NewFileWizard_Title"); // NOI18N
            if (substitute != null) {
                wizard.putProperty("NewFileWizard_Title", substitute); // NOI18N
            }

            if (!ClickProjectQuery.isClick(project)) {
                setErrorMessage("ERR_NOT_CLICK_PROJECT");
            }

        }

    }

    public void storeSettings(WizardDescriptor settings) {
        Object value = wizard.getValue();
        if (WizardDescriptor.PREVIOUS_OPTION.equals(value) || WizardDescriptor.CANCEL_OPTION.equals(value)
                || WizardDescriptor.CLOSED_OPTION.equals(value)) {
            return;
        }

        wizard.putProperty("NewFileWizard_Title", null); // NOI18N

    }

    public void stateChanged(ChangeEvent e) {
        changeSupport.fireChange();
    }

    public boolean isFinishPanel() {
        if (isValid()) {
            return true;
        }

        return false;
    }

// Private methods ---------------------------------------------------------
    private void setErrorMessage(String key) {
        if (key == null) {
            wizard.getNotificationLineSupport().clearMessages();
        } else {
            wizard.getNotificationLineSupport().setErrorMessage(NbBundle.getMessage(ClickPageWizardPanel1.class, key));
        }
    }

    private void setInfoMessage(String key) {
        if (key == null) {
            wizard.getNotificationLineSupport().clearMessages();
        } else {
            wizard.getNotificationLineSupport().setInformationMessage(NbBundle.getMessage(ClickPageWizardPanel1.class, key));
        }
    }

// Nice copy of useful methods (Taken from JavaModule)
    static boolean isValidPackageName(String str) {
        if (str.length() > 0 && str.charAt(0) == '.') {
            return false;
        }

        StringTokenizer tukac = new StringTokenizer(str, ".");
        while (tukac.hasMoreTokens()) {
            String token = tukac.nextToken();
            if ("".equals(token)) {
                return false;
            }

            if (!org.openide.util.Utilities.isJavaIdentifier(token)) {
                return false;
            }

        }
        return true;
    }

    private static boolean isValidPackage(FileObject root, final String path) {
        //May be null when nothing selected in the GUI.
        if (root == null) {
            return false;
        }

        if (path == null) {
            return false;
        }

        final StringTokenizer tk = new StringTokenizer(path, ".");   //NOI18N
        while (tk.hasMoreTokens()) {
            root = root.getFileObject(tk.nextToken());
            if (root == null) {
                return true;
            } else if (root.isData()) {
                return false;
            }

        }
        return true;
    }

    static boolean isValidTypeIdentifier(String ident) {
        if (ident == null || "".equals(ident) || !org.openide.util.Utilities.isJavaIdentifier(ident)) {
            return false;
        } else {
            return true;
        }

    }

    private static boolean existFileName(FileObject targetFolder, String relFileName) {
        boolean result = false;
        File fileForTargetFolder = FileUtil.toFile(targetFolder);
        if (fileForTargetFolder.exists()) {
            result = new File(fileForTargetFolder, relFileName).exists();
        } else {
            result = targetFolder.getFileObject(relFileName) != null;
        }

        return result;
    }
}

