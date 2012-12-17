/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click.spi.impl;

import java.util.logging.Level;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.web.click.ClickResourceTracker;
import org.netbeans.modules.web.click.api.ClickFileType;
import org.netbeans.modules.web.click.spi.ClickComponentQueryImplementation;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Parameters;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author hantsy
 */
@ServiceProvider(service = org.netbeans.modules.web.click.spi.ClickComponentQueryImplementation.class)
public class ClickComponentQueryImpl implements ClickComponentQueryImplementation {

    public ClickComponentQueryImpl() {
    }
    private static final java.util.logging.Logger LOGGER;

    static {
        LOGGER = java.util.logging.Logger.getLogger("org.netbeans.modules.web.click.spi.impl.ClickComponentQueryImpl");
        org.netbeans.modules.web.click.spi.impl.ClickComponentQueryImpl.initLoggerHandlers();
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

    @Override
    public FileObject[] find(FileObject activatedFileObject, ClickFileType targetFileType) {
        Parameters.notNull("ClickComponentQueryImpl:activeFileObject can be null", activatedFileObject);
        Parameters.notNull("ClickComponentQueryImpl:clickFileType can be null", targetFileType);

        Project project = FileOwnerQuery.getOwner(activatedFileObject);

        String fileNameExt = activatedFileObject.getNameExt();
        LOGGER.log(Level.FINEST, "activatedFile@" + fileNameExt + ",target clickFileType @" + targetFileType);

        switch (targetFileType) {
            case CLASS:
                if (fileNameExt.endsWith(".htm") || fileNameExt.endsWith(".jsp")) {
                    LOGGER.finest("htm->class @");
                    FileObject targetFO = findClassByPage(project, activatedFileObject);
                    if (targetFO == null) {
                        return new FileObject[]{};
                    } else {
                        return new FileObject[]{targetFO};
                    }
                }
//                else if (fileNameExt.endsWith(".properties")) {
//                    return findClassByProperites(project, activatedFileObject);
//                }
                break;
            case TEMPLATE:
                if (fileNameExt.endsWith(".java")) {
                    LOGGER.finest("class->htm @");
                    FileObject[] targetFO = findPageByClass(project, activatedFileObject);
                    return targetFO;
                }
//                else if (fileNameExt.endsWith(".properties")) {
//                    return findPageByProperites(project, activatedFileObject);
//                }
                break;
            case PROPETIES:
                if (fileNameExt.endsWith(".java")) {
                    LOGGER.finest("class->property @");
                    FileObject targetFO = findPropertiesByClass(project, activatedFileObject);
                    if (targetFO == null) {
                        return new FileObject[]{};
                    } else {
                        return new FileObject[]{targetFO};
                    }
                } else if (fileNameExt.endsWith(".htm") || fileNameExt.endsWith(".jsp")) {
                    LOGGER.finest("htm->property @");
                    FileObject targetFO = findPropertiesByPage(project, activatedFileObject);
                    if (targetFO == null) {
                        return new FileObject[]{};
                    } else {
                        return new FileObject[]{targetFO};
                    }
                }
                break;
            default:
                break;
        }
        return null;
    }

    //private methods
    private FileObject findClassByPage(final Project project, FileObject pageFileObject) {
        return ClickResourceTracker.findClassByPath(project, pageFileObject);

    }

    public FileObject[] findPageByClass(Project project, FileObject classFileObject) {
        return ClickResourceTracker.findPathByClass(project, classFileObject);

    }

    private FileObject findPropertiesByClass(Project project, FileObject classFileObject) {

        Sources sources = ProjectUtils.getSources(project);
        SourceGroup[] javaSourceGroups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);

        String classRelativePath = null;
        for (SourceGroup sg : javaSourceGroups) {
            if (FileUtil.isParentOf(sg.getRootFolder(), classFileObject)) {
                classRelativePath = FileUtil.getRelativePath(sg.getRootFolder(), classFileObject);
                break;
            }
        }

        if (classRelativePath == null) {
            return null;
        }

        String proFilePath = classRelativePath.substring(0, classRelativePath.lastIndexOf(".")) + ".properties";

        LOGGER.finest("proFilePath @" + proFilePath);

        SourceGroup[] resourceGroups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_RESOURCES);
        FileObject targetFO = null;
        for (SourceGroup sg : resourceGroups) {
            targetFO = sg.getRootFolder().getFileObject(proFilePath);
            if (targetFO != null) {
                return targetFO;
            }
        }

        if (targetFO == null) {
            javaSourceGroups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
            for (SourceGroup sg : resourceGroups) {
                targetFO = sg.getRootFolder().getFileObject(proFilePath);
                if (targetFO != null) {
                    break;
                }
            }
        }

        return targetFO;
    }

//    private FileObject findClassByProperites(Project project, FileObject propertiesFileObject) {
//        FileObject resouceRoot = WebModuleUtilities.getResourcesRoot(project);
//
//
//        if (!FileUtil.isParentOf(resouceRoot, propertiesFileObject)) {
//            return null;
//
//
//        }
//
//        String relativePath = FileUtil.getRelativePath(resouceRoot, propertiesFileObject);
//        FileObject srcRoot = WebModuleUtilities.getJavaSourcesRoot(project);
//
//
//        return srcRoot.getFileObject(relativePath.substring(0, relativePath.lastIndexOf(".")) + ".java");
//
//
//    }
//
//    private FileObject findPageByProperites(Project project, FileObject activatedFileObject) {
//        FileObject classFO = findClassByProperites(project, activatedFileObject);
//
//
//        if (classFO != null) {
//            return findPageByClass(project, classFO);
//
//
//        }
//        return null;
//
//
//    }
    private FileObject findPropertiesByPage(Project project, FileObject activeFileObject) {
        FileObject classFO = findClassByPage(project, activeFileObject);
        if (classFO != null) {
            return findPropertiesByClass(project, classFO);
        }
        return null;
    }
}
