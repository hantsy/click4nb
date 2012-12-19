/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.click.api.model.ClickApp;
import org.netbeans.modules.web.click.api.model.ClickModel;
import org.netbeans.modules.web.click.api.model.Page;
import org.netbeans.modules.web.click.api.model.Pages;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author hantsy
 */
public class ClickResourceTracker {

    //initialize the logger.
    private static final java.util.logging.Logger log;

    static {
        log = java.util.logging.Logger.getLogger("org.netbeans.modules.web.click.ClickResourceTracker");
        org.netbeans.modules.web.click.ClickResourceTracker.initLoggerHandlers();
    }

    private static final void initLoggerHandlers() {
        java.util.logging.Handler[] handlers = log.getHandlers();
        boolean hasConsoleHandler = false;
        for (java.util.logging.Handler handler : handlers) {
            if (handler instanceof java.util.logging.ConsoleHandler) {
                hasConsoleHandler = true;
            }
        }
        if (!hasConsoleHandler) {
            log.addHandler(new java.util.logging.ConsoleHandler());
        }
        log.setLevel(java.util.logging.Level.FINEST);
    }
    //------------------variables-----------------------
    // "/login.htm"->"/login.jsp" in web folder
    private static BiMap<String, FileObject> pageTemplateByPathMap = null;
    // "/login.htm"->com.example.LoginPage in the source folder.
    private static BiMap<String, FileObject> pageClazzByPathMap = null;
    private static Set<FileObject> orphanPageClazzCache = null;

    //------------------public methods------------------
    public static void initialize(Project project) {
        log.finest("Initialize the Click resource template and page class cache.");
        assert project != null;

        if (pageTemplateByPathMap == null) {
            pageTemplateByPathMap = HashBiMap.create();
        } else {
            pageTemplateByPathMap.clear();
        }

        if (pageClazzByPathMap == null) {
            pageClazzByPathMap = HashBiMap.create();
        } else {
            pageClazzByPathMap.clear();
        }
        
        initializeCache(project);
    }

    /**
     * Find template path by page class.
     *
     * @param className Page class FQN.
     * @return template file relative path.
     */
//    public static FileObject[] findPathByClass(final Project project, final FileObject classFO) {
//        Parameters.notNull("class File object can not be null", classFO);
//        initializeIfNeeded(project);
//
//        Set<PageElement> pagesInCache = pageClazzByPathMap.get(classFO);
//        String className = pageClassFQNForFileObjct(classFO);
//        WebModule wm = WebModule.getWebModule(project.getProjectDirectory());
//        FileObject webRoot = wm.getDocumentBase();
//
//        if (pagesInCache == null || pagesInCache.isEmpty()) {
//
//            FileObject clickFO = ClickConfigUtilities.getClickConfigFile(project, ClickConstants.DEFAULT_CLICK_APP_CONFIG_FILE);
//            ClickModel clickModel = ClickConfigUtilities.getClickModel(clickFO, false);
//            ClickApp clickModelRoot = clickModel.getRootComponent();
//
//            List<Pages> pagesList = clickModelRoot.getPagesList();
//            if (pagesList != null && !pagesList.isEmpty()) {
//                boolean automap = true;
//                Pages pagesCom = null;
//                String pagesPackage = "";
//                for (int i = 0; i < pagesList.size(); i++) {
//                    pagesCom = pagesList.get(i);
//                    automap = !("false".equals(pagesCom.getAutoMapping()));
//                    pagesPackage = pagesCom.getPackage();
//
//                    List<Page> pageList = pagesCom.getPageList();
//                    Page pageCom = null;
//
//                    //search page mapping config.
//                    for (int j = 0; j < pageList.size(); j++) {
//                        pageCom = pageList.get(j);
//                        if (className.equals(pageCom.getClassName())) {
//                            String pagePath = pageCom.getPath();
//                            FileObject pathFO = pageTemplateFileObjectForPath(webRoot, pagePath);
//
//                            if (pathFO != null) {
//                                PageElement pathClazzPair = new PageElement(pagePath, className);
//                                pageByPathMap.put(pathFO, pathClazzPair);
//                                addToClassMap(project, pathClazzPair);
//                            } else {
//                                //it is a page class, but does not has a template file.
//                            }
//                        }
//                    }
//
//                    //search automaping.
//                    if (automap) {
//                        String targetPackageName = className.substring(0, className.lastIndexOf("."));
//                        String targetSimpleClazzName = className.substring(className.lastIndexOf(".") + 1);
//                        boolean pathClazzPairFound = false;
//
//                        if (targetPackageName.startsWith(pagesPackage)) {
//
//                            String dir = targetPackageName.substring(pagesPackage.length());
//                            FileObject targetTemplatesFolder = null;
//                            log.finest("Get the dir name @" + dir);
//                            if (dir != null && !"".equals(dir)) {
//                                if (dir.contains(".")) {
//                                    dir = dir.replaceAll(".", "/");
//                                    log.finest("Get the dir name(converted to path) @" + dir);
//                                }
//                                targetTemplatesFolder = webRoot.getFileObject(dir);
//                            } else {
//                                //pagesPackage equals targetPackageName
//                                targetTemplatesFolder = webRoot;
//                            }
//
//
//                            if (targetTemplatesFolder != null) {
//                                Enumeration<? extends FileObject> templates = targetTemplatesFolder.getData(false);
//
//                                FileObject template = null;
//                                String templateName = null;
//                                String guessClazzName = null;
//                                String guessPagePath = "";
//
//                                PageElement autoPageClazzPair = null;
//
//
//                                while (templates.hasMoreElements()) {
//                                    template = templates.nextElement();
//
//                                    if ("jsp".equals(template.getExt()) || "htm".equals(template.getExt())) {
//
//                                        templateName = template.getName();
//                                        log.finest("Guess template@" + templateName);
//                                        guessClazzName = computeClassNameByTemplateName(templateName);
//                                        log.finest("Guess classname by template@" + guessClazzName);
//
//                                        if (guessClazzName.equals(targetSimpleClazzName)) {
//
//                                            guessPagePath = FileUtil.getRelativePath(webRoot, template);
//                                            autoPageClazzPair = new PageElement(guessPagePath, guessClazzName);
//                                            pageByPathMap.put(template, autoPageClazzPair);
//                                            addToClassMap(project, autoPageClazzPair);
//                                            pathClazzPairFound = true;
//                                        }
//
//                                        if (!pathClazzPairFound && !guessClazzName.endsWith("Page")) {
//
//                                            guessClazzName = guessClazzName + "Page";
//                                            log.finest("guess clazz name @" + guessClazzName);
//                                            if (guessClazzName.equals(targetSimpleClazzName)) {
//                                                guessPagePath = FileUtil.getRelativePath(webRoot, template);
//                                                autoPageClazzPair = new PageElement(guessPagePath, guessClazzName);
//                                                pageByPathMap.put(template, autoPageClazzPair);
//                                                addToClassMap(project, autoPageClazzPair);
//                                                pathClazzPairFound = true;
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }//if (automap)
//                }
//            }// if (pagesList != null && !pagesList.isEmpty())
//
//
//        }
//
//        //fetch results...
//
//        Set<PageElement> pathClazzPairList = pageClazzByPathMap.get(classFO);
//        List<FileObject> results = new ArrayList<FileObject>();
//        if (pathClazzPairList != null) {
//            for (PageElement p : pathClazzPairList) {
//                results.add(pageTemplateFileObjectForPath(webRoot, p.getPath()));
//            }
//        }
//        return results.toArray(new FileObject[0]);
//    }
    private static FileObject pageTemplateFileObjectForPath(final FileObject webRoot, final String path) {

        FileObject result;
        result = webRoot.getFileObject(path);
        if (result != null) {
            return result;
        } else {
            if (path.endsWith(".htm")) {
                result = webRoot.getFileObject(path.substring(0, path.lastIndexOf(".")) + ".jsp");
            }
        }

        return result;
    }

    private static String pageClazzFQNForFileObjct(final FileObject classFO) {

        JavaSource js = JavaSource.forFileObject(classFO);

        class MyTask implements Task<CompilationController> {

            String result;

            @Override
            public void run(CompilationController cc) throws Exception {
                cc.toPhase(JavaSource.Phase.ELEMENTS_RESOLVED);
                List<? extends TypeElement> elements = cc.getTopLevelElements();
                for (TypeElement type : elements) {
                    if (type.getSimpleName().toString().equals(classFO.getName())) {

                        this.result = type.getQualifiedName().toString();
                    }
                }
            }

            public String className() {
                return result;
            }
        }


        MyTask t = new MyTask();

        try {
            js.runUserActionTask(t, false);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        return t.className();
    }

    private static FileObject pageClazzFileObjectForFQN(final Project project, String className) {
        Sources sources = ProjectUtils.getSources(project);
        SourceGroup[] sg = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        String classRelativePath = className.replaceAll("\\.", "/") + ".java";

        log.log(Level.FINEST, ">classRelativePath @{0}", classRelativePath);

        FileObject result = null;
        for (SourceGroup g : sg) {
            result = g.getRootFolder().getFileObject(classRelativePath);
            if (result != null) {
                break;
            }
        }
        return result;
    }

//    public static FileObject findClassByPath(final Project project, final FileObject pathFO) {
//        log.finest("Calll findClassByPath @@");
//        initializeIfNeeded(project);
//
//        //update cache firstly...if needed.
//        WebModule wm = WebModule.getWebModule(project.getProjectDirectory());
//        FileObject webRoot = wm.getDocumentBase();
//
//        String templatePath = FileUtil.getRelativePath(webRoot, pathFO);
//        log.finest("@template path @" + templatePath);
//
//        PageElement page = pageByPathMap.get(pathFO);
//
//        if (page == null) {
//            log.finest("can not find page value in cache...");
//            FileObject clickFO = ClickConfigUtilities.getClickConfigFile(project, ClickConstants.DEFAULT_CLICK_APP_CONFIG_FILE);
//            ClickModel clickModel = ClickConfigUtilities.getClickModel(clickFO, false);
//            ClickApp clickModelRoot = clickModel.getRootComponent();
//
//            List<Pages> pagesList = clickModelRoot.getPagesList();
//            if (pagesList != null && !pagesList.isEmpty()) {
//                boolean automap = true;
//                Pages pagesCom = null;
//                String pagesPackage = "";
//                for (int i = 0; i < pagesList.size(); i++) {
//                    pagesCom = pagesList.get(i);
//                    automap = !("false".equals(pagesCom.getAutoMapping()));
//                    pagesPackage = pagesCom.getPackage();
//
//                    List<Page> pageList = pagesCom.getPageList();
//                    Page pageCom = null;
//
//                    //search page mapping config.
//                    for (int j = 0; j < pageList.size(); j++) {
//                        pageCom = pageList.get(j);
//                        if (templatePath.equals(pageCom.getPath())) {
//                            String pageClazz = pageCom.getClassName();
//                            FileObject pageClassFO = pageClassFileObjectForFQN(project, pageClazz);
//                            if (pageClassFO != null) {
//                                PageElement pathClazzPair = new PageElement(templatePath, pageClazz);
//                                log.finest("add page elements @" + pathClazzPair);
//                                pageByPathMap.put(pathFO, pathClazzPair);
//                                addToClassMap(project, pathClazzPair);
//                            } else {
//                                //it is page template file, but does not has a page class.
//                            }
//                        }
//                    }
//
//                    //search automaping
//                    if (automap) {
//                        log.finest("process automaping...!");
//                        String targetDirName = "";
//                        String targetClazzName = "";
//
//                        if (templatePath.startsWith("/")) {
//                            templatePath = templatePath.substring(1);
//                        }
//
//                        if (templatePath.contains("/")) {
//                            targetDirName = templatePath.substring(0, templatePath.lastIndexOf("/"));
//                            targetClazzName = templatePath.substring(templatePath.lastIndexOf("/") + 1);
//                        } else {
//                            targetClazzName = templatePath;
//                        }
//
//                        //chop file extension...
//                        targetClazzName = targetClazzName.substring(0, targetClazzName.lastIndexOf("."));
//                        log.finest("target dir name @" + targetDirName + ", target class name @" + targetClazzName);
//
//                        String guessClazzName = computeClassNameByTemplateName(targetClazzName);
//                        String guessPackageDir = "";
//                        if (pagesPackage != null && !"".equals(pagesPackage.trim())) {
//                            guessPackageDir = pagesPackage.replaceAll("\\.", "/");
//                        }
//                        if (targetDirName != null && !"".equals(targetDirName.trim())) {
//                            if (guessPackageDir.length() > 0) {
//                                guessPackageDir += "/";
//                            }
//
//                            guessPackageDir = guessPackageDir + targetDirName;
//                        }
//
//                        log.finest("guess dir name @" + guessPackageDir + ", guess class name @" + guessClazzName);
//
//                        String guessClazzFilePath = guessPackageDir + "/" + guessClazzName + ".java";
//
//                        log.finest("guess class file path @" + guessClazzFilePath);
//
//                        Sources sources = ProjectUtils.getSources(project);
//                        SourceGroup[] sourceGroups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
//
//                        String pageClazz = "";
//                        PageElement pathClazzPair = null;
//                        boolean pathClazzPairFound = false;
//                        for (SourceGroup sg : sourceGroups) {
//                            if (sg.getRootFolder().getFileObject(guessClazzFilePath) != null) {
//                                pageClazz = guessPackageDir + "/" + guessClazzName;
//                                pageClazz = pageClazz.replaceAll("/", ".");
//
//                                pathClazzPair = new PageElement(templatePath, pageClazz);
//                                log.finest("add page elements @" + pathClazzPair);
//                                pageByPathMap.put(pathFO, pathClazzPair);
//                                addToClassMap(project, pathClazzPair);
//                                pathClazzPairFound = true;
//                            }
//
//                            if (!pathClazzPairFound && !guessClazzName.endsWith("Page")) {
//                                guessClazzFilePath = guessPackageDir + "/" + guessClazzName + "Page.java";
//
//                                if (sg.getRootFolder().getFileObject(guessClazzFilePath) != null) {
//                                    pageClazz = guessPackageDir + "/" + guessClazzName + "Page";
//                                    pageClazz = pageClazz.replaceAll("/", ".");
//
//                                    pathClazzPair = new PageElement(templatePath, pageClazz);
//                                    log.finest("add page elements @" + pathClazzPair);
//                                    pageByPathMap.put(pathFO, pathClazzPair);
//                                    addToClassMap(project, pathClazzPair);
//                                    pathClazzPairFound = true;
//                                }
//                            }
//                        }
//                    }//if (automap)
//                }
//            }
//        }
//
//
//        //fetch result
//        PageElement pageElement = pageByPathMap.get(pathFO);
//        if (pageElement != null) {
//            return pageClassFileObjectForFQN(project, pageElement.getClazz());
//        }
//
//        return null;
//    }
    public static boolean isInitialized() {
        return pageClazzByPathMap != null;
    }

    public static void initializeIfNeeded(Project project) {
        if (!isInitialized()) {
            initialize(project);
        }
    }

    //------------------private methods------------------
    private static void buildManualPageMapping(Project project, FileObject webRoot, List<Page> pageList, String pagesPackage) {
        log.finest("starting buildManualPageMapping...");

        if (pageList == null || pageList.isEmpty()) {
            return;
        }

        for (Page page : pageList) {
            pageClazzByPathMap.put(page.getPath(), pageClazzFileObjectForFQN(project, page.getClassName()));
            log.log(Level.FINEST, "Add ''{0}'' -> ''{1}'' to pageClazzByPathMap", new Object[]{page.getPath(), page.getClassName()});
            pageTemplateByPathMap.put(page.getPath(), pageTemplateFileObjectForPath(webRoot, page.getPath()));
        }
    }

    private static void buildAutoPageMapping(Project project, FileObject webRoot, String pagesPackage, List<String> templates) {
        log.log(Level.FINEST, "starting buildAutoPageMapping for package@{0}...", new Object[]{pagesPackage});
        if(pagesPackage==null){
            return;
        }
        
        for (int i = 0; i < templates.size(); i++) {
            String pagePath = templates.get(i);

            String pageTemplate = pagePath;
            if (pagePath.endsWith(".jsp")) {
                pageTemplate = pagePath.substring(0, pagePath.lastIndexOf(".")) + ".htm";
            }

            if (!pageTemplateByPathMap.containsKey(pageTemplate)) {               
                final FileObject pageTemplateFO = pageTemplateFileObjectForPath(webRoot, pageTemplate);            
                log.log(Level.FINEST, "Add ''{0}'' -> ''{1}'' to pageTemplateByPathMap", new Object[]{pageTemplate, pageTemplateFO});
                pageTemplateByPathMap.put(pageTemplate, pageTemplateFO);
            }

            if (!pageClazzByPathMap.containsKey(pageTemplate)) {
                FileObject pageClazz = computePageClazzFileObject(project, pageTemplate, pagesPackage);
                if (pageClazz != null) {
                    pageClazzByPathMap.put(pageTemplate, pageClazz);
                    log.log(Level.FINEST, "Add ''{0}'' -> ''{1}'' to pageClazzByPathMap", new Object[]{pageTemplate, pageClazz});
                }
            }
        }
    }

    private static FileObject computePageClazzFileObject(Project project, String pagePath, String pagesPackage) {
        log.log(Level.FINEST, " Find page class package @{0}, page path @{1}", new Object[]{pagesPackage, pagePath});

        String packagePath = pagesPackage.replaceAll("\\.", "/") + "/";
        String classFQNPath = packagePath;
        String simpleClassName = "";

        if (pagePath.startsWith("/")) {
            pagePath = pagePath.substring(1);
        }

        String pathName = pagePath.substring(0, pagePath.lastIndexOf("."));

        if (pathName.indexOf("/") != -1) {
            packagePath += pathName.substring(0, pathName.lastIndexOf("/")) + "/";
            simpleClassName = pathName.substring(pathName.lastIndexOf("/"));
        } else {
            simpleClassName = pathName;
        }

        simpleClassName = computeClassNameByTemplateName(simpleClassName);
        log.log(Level.FINEST, "class name @{0}", simpleClassName);

        // className = 'org.apache.click.pages.EditCustomer'
        classFQNPath = packagePath + simpleClassName;
        String clazzRelativePath = classFQNPath + ".java";
        log.log(Level.FINEST, "class relative path @{0}", clazzRelativePath);

        SourceGroup[] sourceGroups = ProjectUtils.getSources(project).getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        FileObject rootFolder = null;
        FileObject targetFileObject = null;
        for (SourceGroup group : sourceGroups) {
            rootFolder = group.getRootFolder();
            targetFileObject = rootFolder.getFileObject(clazzRelativePath);

            if (targetFileObject != null) {
                log.log(Level.FINEST, "found target pageClazz object @{0}", targetFileObject);
                return targetFileObject;
            }
        }

        if (!classFQNPath.endsWith("Page")) {
            classFQNPath = classFQNPath + "Page";
            clazzRelativePath = classFQNPath + ".java";
            for (SourceGroup group : sourceGroups) {
                rootFolder = group.getRootFolder();
                targetFileObject = rootFolder.getFileObject(clazzRelativePath);

                if (targetFileObject != null) {
                    log.log(Level.FINEST, "found target pageClazz object @{0}", targetFileObject);
                    return targetFileObject;
                }
            }
        }

        return null;
    }

    private static String computeClassNameByTemplateName(String className) {
        StringTokenizer tokenizer = new StringTokenizer(className, "_-");
        className = "";
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            token = Character.toUpperCase(token.charAt(0)) + token.substring(1);
            className += token;
        }
        return className;
    }
//    private static void buildClassMap(final Project project) {
//        log.finest("starting buildClassMap...");
//        for (Iterator i = pageByPathMap.values().iterator(); i.hasNext();) {
//            PageElement page = (PageElement) i.next();
//            addToClassMap(project, page);
//        }
//    }
//    private static void addToClassMap(Project project, PageElement page) {
//        log.finest("starting addToClassMap...");
//        FileObject pageClassFO = pageClassFileObjectForFQN(project, page.getClazz());
//        Set<PageElement> value = pageClazzByPathMap.get(pageClassFO);
//
//        if (value == null) {
//            value = new HashSet<PageElement>();
//            value.add(page);
//            pageClazzByPathMap.put(pageClassFO, value);
//            log.finest("Add '" + page.getClazz() + "' ->'" + page.getPath() + "@" + page.getClazz() + "'");
//        } else {
//            log.finest("Add '" + page.getClazz() + "' ->'" + page.getPath() + "@" + page.getClazz() + "'");
//            value.add(page);
//            log.finest("There are " + value.size() + " mapping to a class, it is problematic at runtime...");
//            //TODO Add error maker to Page Class...
//        }
//    }

    public static FileObject findPathByClass(Project project, FileObject classFileObject) {
        log.log(Level.FINEST, "found pageTempalteFO by pageClazz object @{0}", classFileObject);
        BiMap<FileObject, String> _pathByPageClazzMap = pageClazzByPathMap.inverse();
        if(!_pathByPageClazzMap.containsKey(classFileObject)){
            initializeCache(project);
        }
        
        String pageTemplate = _pathByPageClazzMap.get(classFileObject);
        return pageTemplateByPathMap.get(pageTemplate);
    }

    public static FileObject findClassByPath(Project project, FileObject pageFileObject) {
        log.log(Level.FINEST, "found pageClazzFO by pageTemplate object @{0}", pageFileObject);
        BiMap<FileObject, String> _pathByPageTempateMap = pageTemplateByPathMap.inverse();
        
        if(!_pathByPageTempateMap.containsKey(pageFileObject)){
            initializeCache(project);
        }
        
        String pageTemplate = _pathByPageTempateMap.get(pageFileObject);
        return pageClazzByPathMap.get(pageTemplate);
    }

    public static void initializeCache(Project project) {
        log.log(Level.FINEST, "@ call initilizeCache @");      
        WebModule wm = WebModule.getWebModule(project.getProjectDirectory());
        if (wm == null) {
            return;
        }
        
        List<String> templatesList = new ArrayList<String>();
        
        FileObject webRoot = wm.getDocumentBase();     
        Enumeration<? extends FileObject> resources = webRoot.getData(true);
        while (resources.hasMoreElements()) {
            FileObject resource = resources.nextElement();

            log.log(Level.FINEST, "Find data resource {0}({1}) .", new Object[]{resource.getName(), resource.getPath()});

            String relativePath = FileUtil.getRelativePath(webRoot, resource);

            log.log(Level.FINEST, "scanning relative path @{0}", relativePath);

            if (relativePath.startsWith("WEB-INF") || relativePath.startsWith("click")|| relativePath.startsWith("META-INF")) {
                continue;
            }

            if (("htm".equalsIgnoreCase(resource.getExt()) || "jsp".equalsIgnoreCase(resource.getExt()))) {
                log.log(Level.FINEST, "Add {0} to templates list.", resource.getName());
                templatesList.add(FileUtil.getRelativePath(webRoot, resource));
            }

        }
        
        FileObject clickFO = ClickConfigUtilities.getClickConfigFile(project, ClickConstants.DEFAULT_CLICK_APP_CONFIG_FILE);
        ClickModel clickModel = ClickConfigUtilities.getClickModel(clickFO, false);
        ClickApp clickRoot = clickModel.getRootComponent();
        List<Pages> pagesList = clickRoot.getPagesList();
        
        boolean automap = true;
        Pages pages;
        String pagesPackage;
        
        for (int i = 0; i < pagesList.size(); i++) {
            pages = pagesList.get(i);
            pagesPackage = pages.getPackage();

            if (pages.getAutoMapping() != null) {
                automap = Boolean.parseBoolean(pages.getAutoMapping());
            }

            buildManualPageMapping(project, webRoot, pages.getPageList(), pagesPackage);

            if (automap) {
                buildAutoPageMapping(project, webRoot, pagesPackage, templatesList);
            }
            automap = true;
        }    
    }
}
