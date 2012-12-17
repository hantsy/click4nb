/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.j2ee.dd.api.web.Servlet;
import org.netbeans.modules.j2ee.dd.api.web.WebApp;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.click.api.model.ClickModel;
import org.netbeans.modules.web.click.api.model.ClickModelFactory;
import org.netbeans.modules.web.click.api.model.MenuModel;
import org.netbeans.modules.web.click.api.model.MenuModelFactory;
import org.netbeans.modules.xml.retriever.catalog.Utilities;
import org.netbeans.modules.xml.xam.ModelSource;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.util.Parameters;

/**
 *
 * @author hantsy
 */
public class ClickConfigUtilities {

    public static Servlet findServlet(WebApp wa, String servletName) {
        Parameters.notNull("", wa);
        return (Servlet) wa.findBeanByName("Servlet", "ServletClass", servletName);
    }

    public static FileObject getClickConfigFile(Project project, String filename) {
        Parameters.notNull("Parameter project can not be null", project);
        FileObject target = null;

        WebModule wm = WebModule.getWebModule(project.getProjectDirectory());

        if (wm == null) {
            return null;
        }

        target = wm.getWebInf().getFileObject(filename);
        if (target != null) {
            return target;
        }

        Sources sources = ProjectUtils.getSources(project);
        SourceGroup[] resourceGroups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_RESOURCES);
        for (SourceGroup sg : resourceGroups) {
            target = sg.getRootFolder().getFileObject(filename);
            if (target != null) {
                return target;
            }
        }

        resourceGroups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        for (SourceGroup sg : resourceGroups) {
            target = sg.getRootFolder().getFileObject(filename);
            if (target != null) {
                return target;
            }
        }

        return target;

    }

//    public static boolean isClickProject(WebModule wm) {
//        if (wm == null) {
//            return false;
//        }
//        try {
//            FileObject webXml = wm.getDeploymentDescriptor();
//            if(webXml!=null){
//            WebApp wa = DDProvider.getDefault().getDDRoot(webXml);
//            if (wa != null) {
//                return (ClickConfigUtilities.findServlet(wa, ClickConstants.CLICK_SERVELT_CLASS) != null)
//                        || (ClickConfigUtilities.findServlet(wa, ClickConstants.SPRING_CLICK_SERVELT_CLASS) != null);
//            }
//            }
//        } catch (IOException ex) {
//            return false;
//        }
//        return false;
//    }
    /**
     * Returns true if the specified classpath contains a class of the given name,
     * false otherwise.
     *
     * @param classpath consists of jar urls and folder urls containing classes
     * @param className the name of the class
     *
     * @return true if the specified classpath contains a class of the given name,
     *         false otherwise.
     *
     * @throws IOException if an I/O error has occurred
     *
     * @since 1.15
     */
    public static boolean containsClass(List<URL> classPath, String className) throws IOException {
        Parameters.notNull("classpath", classPath); // NOI18N
        Parameters.notNull("className", className); // NOI18N

        List<File> diskFiles = new ArrayList<File>();
        for (URL url : classPath) {
            URL archiveURL = FileUtil.getArchiveFile(url);

            if (archiveURL != null) {
                url = archiveURL;
            }

            if ("nbinst".equals(url.getProtocol())) { // NOI18N
                // try to get a file: URL for the nbinst: URL
                FileObject fo = URLMapper.findFileObject(url);
                if (fo != null) {
                    URL localURL = URLMapper.findURL(fo, URLMapper.EXTERNAL);
                    if (localURL != null) {
                        url = localURL;
                    }
                }
            }

            FileObject fo = URLMapper.findFileObject(url);
            if (fo != null) {
                File diskFile = FileUtil.toFile(fo);
                if (diskFile != null) {
                    diskFiles.add(diskFile);
                }
            }
        }

        return containsClass(diskFiles, className);
    }

    /**
     * Returns true if the specified classpath contains a class of the given name,
     * false otherwise.
     *
     * @param classpath consists of jar files and folders containing classes
     * @param className the name of the class
     *
     * @return true if the specified classpath contains a class of the given name,
     *         false otherwise.
     *
     * @throws IOException if an I/O error has occurred
     *
     * @since 1.15
     */
    public static boolean containsClass(Collection<File> classpath, String className) throws IOException {
        Parameters.notNull("classpath", classpath); // NOI18N
        Parameters.notNull("driverClassName", className); // NOI18N
        String classFilePath = className.replace('.', '/') + ".class"; // NOI18N
        for (File file : classpath) {
            if (file.isFile()) {
                JarFile jf = new JarFile(file);
                try {
                    Enumeration entries = jf.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = (JarEntry) entries.nextElement();
                        if (classFilePath.equals(entry.getName())) {
                            return true;
                        }
                    }
                } finally {
                    jf.close();
                }
            } else {
                if (new File(file, classFilePath).exists()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static ClickModel getClickModel(FileObject fo, boolean editable) {
        Parameters.notNull("ClickModel source file object can not be null", fo);
        ModelSource source = Utilities.getModelSource(fo, editable);

        return ClickModelFactory.getInstance().getModel(source);
    }

    public static MenuModel getMenuModel(FileObject fo, boolean editable) {
        Parameters.notNull("MenuModel source file object can not be null", fo);
        ModelSource source = Utilities.getModelSource(fo, editable);

        return MenuModelFactory.getInstance().getModel(source);
    }
}
