/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.j2ee.core.Profile;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.project.classpath.ProjectClassPathModifier;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.api.project.libraries.LibraryManager;
import org.netbeans.modules.j2ee.dd.api.common.InitParam;
import org.netbeans.modules.j2ee.dd.api.web.DDProvider;
import org.netbeans.modules.j2ee.dd.api.web.Filter;
import org.netbeans.modules.j2ee.dd.api.web.FilterMapping;
import org.netbeans.modules.j2ee.dd.api.web.Listener;
import org.netbeans.modules.j2ee.dd.api.web.Servlet;
import org.netbeans.modules.j2ee.dd.api.web.ServletMapping;
import org.netbeans.modules.j2ee.dd.api.web.WebApp;
import org.netbeans.modules.j2ee.dd.api.web.WelcomeFileList;
import org.netbeans.modules.web.api.webmodule.ExtenderController;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.spi.webmodule.WebModuleExtender;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileSystem.AtomicAction;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;

/**
 *
 * @author hantsy
 */
public class ClickWebModuleExtender extends WebModuleExtender {

    WebModule wm;
    ExtenderController controller;
    boolean defaultValue;
    ClickConfigurationPanel component;

    public ClickWebModuleExtender(WebModule wm, ExtenderController controller, boolean defaultValue) {
        this.wm = wm;
        this.controller = controller;
        this.defaultValue = defaultValue;
        getComponent();
    }

    @Override
    public void addChangeListener(ChangeListener arg0) {
    }

    @Override
    public void removeChangeListener(ChangeListener arg0) {
    }

    @Override
    public JComponent getComponent() {
        if (component == null) {
            component = new ClickConfigurationPanel(defaultValue);
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public void update() {
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public Set<FileObject> extend(WebModule wm) {

        Set<FileObject> filesToOpen = new HashSet<FileObject>();
        FileSystem fs;
        try {
            if (null != component.getSelectedLibrary()) {
                //final LibraryManager libManager = LibraryManager.getDefault();
                List<Library> libs = new ArrayList<Library>();
                //Library clickLib = libManager.getLibrary(ClickConstants.LIBRARY_CLICK);
                //Library clickMockLib = libManager.getLibrary(ClickConstants.LIBRARY_CLICK_MOCK);
                //libs.add(clickLib);
                libs.add(component.getSelectedLibrary());

                if (component.supportSpring()) {
                    libs.add(LibraryManager.getDefault().getLibrary(ClickConstants.SPRING_LIBRARY_NAME));
                }

                Project project = FileOwnerQuery.getOwner(wm.getDocumentBase());
                SourceGroup[] groups = ProjectUtils.getSources(project).getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
                assert groups.length > 0;

                ProjectClassPathModifier.addLibraries(libs.toArray(new Library[libs.size()]), groups[0].getRootFolder(), ClassPath.COMPILE);
            }

            FileObject webInf = wm.getWebInf();
            if (webInf == null) {
                webInf = FileUtil.createData(wm.getDocumentBase(), "WEB-INF");
            }
            fs = webInf.getFileSystem();
            fs.runAtomicAction(new ClickFrameworkEnabler(wm, filesToOpen));
        } catch (FileStateInvalidException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        return filesToOpen;
    }

    private class ClickFrameworkEnabler implements AtomicAction {

        WebModule wm;
        Set<FileObject> filesToOpen;
        private static final String RESOURCE_FOLDER = "org/netbeans/modules/j2ee/common/dd/resources/"; //NOI18N

        public ClickFrameworkEnabler(WebModule wm, Set<FileObject> filesToOpen) {
            this.wm = wm;
            this.filesToOpen = filesToOpen;
        }

        public void run() throws IOException {

            FileObject dd = wm.getDeploymentDescriptor();

            // in servlet 3.0, the web.xml file is optional.
            // create a web.xml by force.
            if (dd == null) {
                dd = createWebXml(wm.getJ2eeProfile(), true, wm.getWebInf());
            }

            WebApp webApp = DDProvider.getDefault().getDDRoot(dd);

            String defaultPagesPkg = component.getPagesPackage();
            String basePkgName = ClickConstants.DEFAULT_PACKAGE_NAME;
            if (defaultPagesPkg != null && defaultPagesPkg.trim().length() > 0) {
                basePkgName = defaultPagesPkg;
            }

            Map<String, String> replacements = new HashMap<String, String>();
            replacements.put("package", basePkgName);
            replacements.put("mode", component.getMode());

            FileObject webRoot = wm.getDocumentBase();
            FileObject webInf = wm.getWebInf();

            Project project = FileOwnerQuery.getOwner(dd);
            assert project != null;
            Sources sources = ProjectUtils.getSources(project);
            SourceGroup[] groups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
            assert groups.length >= 1;

            FileObject basePkg = FileUtil.createFolder(groups[0].getRootFolder(), basePkgName.replaceAll("\\.", "/"));

            DataObject homeDO = DataObject.find(FileUtil.getConfigFile(ClickConstants.BASE_TEMPLATES_DIR + "Home.java"));
            homeDO.createFromTemplate(DataFolder.findFolder(basePkg), null, replacements);

            DataObject homePageDO = DataObject.find(FileUtil.getConfigFile(ClickConstants.BASE_TEMPLATES_DIR + "home.htm"));
            homePageDO.createFromTemplate(DataFolder.findFolder(webRoot), null, replacements);

            DataObject redirectDO = DataObject.find(FileUtil.getConfigFile(ClickConstants.BASE_TEMPLATES_DIR + "redirect.html"));
            redirectDO.createFromTemplate(DataFolder.findFolder(webRoot), null, replacements);

            DataObject styleCssDO = DataObject.find(FileUtil.getConfigFile(ClickConstants.BASE_TEMPLATES_DIR + "style.css"));
            styleCssDO.createFromTemplate(DataFolder.findFolder(webRoot), null, replacements);

            DataObject clickDO = DataObject.find(FileUtil.getConfigFile(ClickConstants.BASE_TEMPLATES_DIR + ClickConstants.DEFAULT_CLICK_APP_CONFIG_FILE));
            clickDO.createFromTemplate(DataFolder.findFolder(webInf), null, replacements);

            DataObject menuDO = DataObject.find(FileUtil.getConfigFile(ClickConstants.BASE_TEMPLATES_DIR + ClickConstants.DEFAULT_MENU_CONFIG_FILE));
            menuDO.createFromTemplate(DataFolder.findFolder(webInf), null, replacements);

            if (component.supportSpring()) {
                DataObject clickSpringDO = DataObject.find(FileUtil.getConfigFile(ClickConstants.BASE_TEMPLATES_DIR + ClickConstants.DEFAULT_SPRING_CONFIG_FILE));
                clickSpringDO.createFromTemplate(DataFolder.findFolder(webInf), null, replacements);
            }

            String servletClass = ClickConstants.CLICK_SERVELT_CLASS;

            if (component.supportSpring()) {
                servletClass = ClickConstants.SPRING_CLICK_SERVELT_CLASS;
                writeSpringSupport(webApp);
            }

            writeClickServletToWebApp(webApp, servletClass);
            if (component.compressionFilterEnabled()) {
                writeCompressionFilterToWebApp(webApp);
            }
            if (component.perfermanceFilterEnabled()) {
                writePerformanceFilterToWebApp(webApp);
            }

            WelcomeFileList welcomeFileList = webApp.getSingleWelcomeFileList();
            if (welcomeFileList == null) {
                try {
                    welcomeFileList = (WelcomeFileList) webApp.createBean("WelcomeFileList"); //NOI18N
                } catch (ClassNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                }
                webApp.setWelcomeFileList(welcomeFileList);
            }
            welcomeFileList.addWelcomeFile("redirect.html");
            welcomeFileList.setWelcomeFile(0, "redirect.html");

            webApp.write(dd);
        }

        private void writeSpringSupport(WebApp webApp) {
            try {
                InitParam param = (InitParam) webApp.createBean("InitParam");
                param.setParamName(ClickConstants.SPRING_CONFIG_LOCATION_PARAM);
                param.setParamValue(ClickConstants.SPRING_CONFIG_LOCATION_VALUE);
                webApp.addContextParam(param);

                Listener listener = (Listener) webApp.createBean("Listener");
                listener.setListenerClass(ClickConstants.SPRING_CONTEXT_LOADER_CLASS);
                webApp.addListener(listener);
            } catch (ClassNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        private void writeClickServletToWebApp(WebApp webApp, String servletClass) {
            Servlet clickServlet = null;
            try {
                clickServlet = (Servlet) webApp.createBean("Servlet");
            } catch (ClassNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
            clickServlet.setServletName(ClickConstants.CLICK_SERVLET_NAME);
            clickServlet.setServletClass(servletClass);
            clickServlet.setLoadOnStartup(BigInteger.ZERO);
            webApp.addServlet(clickServlet);

            ServletMapping clickServletMapping = null;
            try {
                clickServletMapping = (ServletMapping) webApp.createBean("ServletMapping");
            } catch (ClassNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
            clickServletMapping.setServletName(ClickConstants.CLICK_SERVLET_NAME);
            clickServletMapping.setUrlPattern("*.htm");
            webApp.addServletMapping(clickServletMapping);
        }

        public void writePerformanceFilterToWebApp(WebApp webApp) {
            final String filterName = "PerformanceFilter";
            final String filterClass = "org.apache.click.extras.filter.PerformanceFilter";

            try {
                Filter filter = (Filter) webApp.createBean("Filter");
                filter.setFilterName(filterName);
                filter.setFilterClass(filterClass);

                InitParam param = (InitParam) webApp.createBean("InitParam");
                param.setParamName("cachable-paths");
                param.setParamValue("/assets/*");

                filter.addInitParam(param);
                webApp.addFilter(filter);

                FilterMapping mapping = (FilterMapping) webApp.createBean("FilterMapping");
                mapping.setFilterName(filterName);
                mapping.setServletName(ClickConstants.CLICK_SERVLET_NAME);
                webApp.addFilterMapping(mapping);

                mapping = (FilterMapping) webApp.createBean("FilterMapping");
                mapping.setFilterName(filterName);
                mapping.setUrlPattern("*.css");
                webApp.addFilterMapping(mapping);

                mapping = (FilterMapping) webApp.createBean("FilterMapping");
                mapping.setFilterName(filterName);
                mapping.setUrlPattern("*.js");
                webApp.addFilterMapping(mapping);

                mapping = (FilterMapping) webApp.createBean("FilterMapping");
                mapping.setFilterName(filterName);
                mapping.setUrlPattern("*.png");
                webApp.addFilterMapping(mapping);

                mapping = (FilterMapping) webApp.createBean("FilterMapping");
                mapping.setFilterName(filterName);
                mapping.setUrlPattern("*.jpg");
                webApp.addFilterMapping(mapping);


            } catch (ClassNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }


        }

        public void writeCompressionFilterToWebApp(WebApp webApp) {
            final String filterName = "CompressionFilter";
            final String filterClass = "org.apache.click.extras.filter.CompressionFilter";

            try {
                Filter filter = (Filter) webApp.createBean("Filter");
                filter.setFilterName(filterName);
                filter.setFilterClass(filterClass);
                webApp.addFilter(filter);

                FilterMapping mapping = (FilterMapping) webApp.createBean("FilterMapping");
                mapping.setFilterName(filterName);
                mapping.setServletName(ClickConstants.CLICK_SERVLET_NAME);
                webApp.addFilterMapping(mapping);
            } catch (ClassNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }

        }

        private FileObject createWebXml(Profile j2eeProfile, boolean webXmlRequired, FileObject dir) throws IOException {
            String template = null;
            if ((Profile.JAVA_EE_6_FULL == j2eeProfile || Profile.JAVA_EE_6_WEB == j2eeProfile) && webXmlRequired) {
                template = "web-3.0.xml"; //NOI18N
            } else if (Profile.JAVA_EE_5 == j2eeProfile) {
                template = "web-2.5.xml"; //NOI18N
            } else if (Profile.J2EE_14 == j2eeProfile) {
                template = "web-2.4.xml"; //NOI18N
            } else if (Profile.J2EE_13 == j2eeProfile) {
                template = "web-2.3.xml"; //NOI18N
            }

            if (template == null) {
                return null;
            }

            MakeFileCopy action = new MakeFileCopy(RESOURCE_FOLDER + template, dir, "web.xml");
            FileUtil.runAtomicAction(action);
            if (action.getException() != null) {
                throw action.getException();
            } else {
                return action.getResult();
            }
        }
    }
    // -------------------------------------------------------------------------

    private static class MakeFileCopy implements Runnable {

        private String fromFile;
        private FileObject toDir;
        private String toFile;
        private IOException exception;
        private FileObject result;

        MakeFileCopy(String fromFile, FileObject toDir, String toFile) {
            this.fromFile = fromFile;
            this.toDir = toDir;
            this.toFile = toFile;
        }

        IOException getException() {
            return exception;
        }

        FileObject getResult() {
            return result;
        }

        public void run() {
            try {
                // PENDING : should be easier to define in layer and copy related FileObject (doesn't require systemClassLoader)
                if (toDir.getFileObject(toFile) != null) {
                    throw new IllegalStateException("file " + toFile + " already exists in " + toDir);
                }
                FileObject xml = FileUtil.createData(toDir, toFile);
                String content = readResource(Thread.currentThread().getContextClassLoader().getResourceAsStream(fromFile));
                if (content != null) {
                    FileLock lock = xml.lock();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(xml.getOutputStream(lock)));
                    try {
                        bw.write(content);
                    } finally {
                        bw.close();
                        lock.releaseLock();
                    }
                }
                result = xml;
            } catch (IOException e) {
                exception = e;
            }
        }

        private String readResource(InputStream is) throws IOException {
            StringBuilder sb = new StringBuilder();
            String lineSep = System.getProperty("line.separator"); // NOI18N
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            try {
                String line = br.readLine();
                while (line != null) {
                    sb.append(line);
                    sb.append(lineSep);
                    line = br.readLine();
                }
            } finally {
                br.close();
            }
            return sb.toString();
        }
    }
}
