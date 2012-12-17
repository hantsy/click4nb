/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click;

import com.sun.source.tree.CompilationUnitTree;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.WeakHashMap;
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
import org.openide.util.Parameters;
import org.openide.util.WeakSet;

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
    } //------------------variables-----------------------
    private static Map<FileObject, PageElement> pageByPathMap = null;//Collections.<String, String>emptyMap();
    private static Map<FileObject, Set<PageElement>> pageByClassMap = null;
    private static Set<FileObject> orphanPageClazzCache = null;

    //------------------public methods------------------
    public static void initialize(Project project) {
        log.finest("Initialize the Click resource template and page class cache.");
        assert project != null;

        if (pageByPathMap == null) {
            pageByPathMap = new WeakHashMap<FileObject, PageElement>();
        } else {
            pageByPathMap.clear();
        }

        if (pageByClassMap == null) {
            pageByClassMap = new WeakHashMap<FileObject, Set<PageElement>>();
        } else {
            pageByClassMap.clear();
        }

        if (orphanPageClazzCache == null) {
            orphanPageClazzCache = new WeakSet<FileObject>();
        } else {
            orphanPageClazzCache.clear();
        }

        WebModule wm = WebModule.getWebModule(project.getProjectDirectory());
        if (wm == null) {
            return;
        }

        FileObject webRoot = wm.getDocumentBase();
        List<String> templatesList = new ArrayList<String>();

        Enumeration<? extends FileObject> resources = webRoot.getData(true);
        while (resources.hasMoreElements()) {
            FileObject resource = resources.nextElement();
            log.finest("Find data resource " + resource.getName() + "(" + resource.getPath() + ") .");
            String relativePath = FileUtil.getRelativePath(webRoot, resource);

            if (relativePath.contains("/WEB-INF") || relativePath.contains("/click")) {
                continue;
            }

            if (("htm".equalsIgnoreCase(resource.getExt()) || "jsp".equalsIgnoreCase(resource.getExt()))) {
                log.finest("Add " + resource.getName() + " to templates list.");
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
            automap = !("false".equals(pages.getAutoMapping()));
            buildManualPageMapping(webRoot, pages, pagesPackage);
            if (automap) {
                buildAutoPageMapping(project, webRoot, pages, pagesPackage, templatesList);
            }
        }
        buildClassMap(project);
    }

    /**
     * Find template path by page class.
     * @param className Page class FQN.
     * @return template file relative path.
     */
    public static FileObject[] findPathByClass(final Project project, final FileObject classFO) {
        Parameters.notNull("class File object can not be null", classFO);
        initializeIfNeeded(project);

        Set<PageElement> pagesInCache = pageByClassMap.get(classFO);
        String className = pageClassFQNForFileObjct(classFO);
        WebModule wm = WebModule.getWebModule(project.getProjectDirectory());
        FileObject webRoot = wm.getDocumentBase();

        if (pagesInCache == null || pagesInCache.isEmpty()) {

            FileObject clickFO = ClickConfigUtilities.getClickConfigFile(project, ClickConstants.DEFAULT_CLICK_APP_CONFIG_FILE);
            ClickModel clickModel = ClickConfigUtilities.getClickModel(clickFO, false);
            ClickApp clickModelRoot = clickModel.getRootComponent();

            List<Pages> pagesList = clickModelRoot.getPagesList();
            if (pagesList != null && !pagesList.isEmpty()) {
                boolean automap = true;
                Pages pagesCom = null;
                String pagesPackage = "";
                for (int i = 0; i < pagesList.size(); i++) {
                    pagesCom = pagesList.get(i);
                    automap = !("false".equals(pagesCom.getAutoMapping()));
                    pagesPackage = pagesCom.getPackage();

                    List<Page> pageList = pagesCom.getPageList();
                    Page pageCom = null;

                    //search page mapping config.
                    for (int j = 0; j < pageList.size(); j++) {
                        pageCom = pageList.get(j);
                        if (className.equals(pageCom.getClassName())) {
                            String pagePath = pageCom.getPath();
                            FileObject pathFO = findWebPageFileObjectByPath(webRoot, pagePath);

                            if (pathFO != null) {
                                PageElement pathClazzPair = new PageElement(pagePath, className);
                                pageByPathMap.put(pathFO, pathClazzPair);
                                addToClassMap(project, pathClazzPair);
                            } else {
                                //it is a page class, but does not has a template file.
                            }
                        }
                    }

                    //search automaping.
                    if (automap) {
                        String targetPackageName = className.substring(0, className.lastIndexOf("."));
                        String targetSimpleClazzName = className.substring(className.lastIndexOf(".") + 1);
                        boolean pathClazzPairFound = false;

                        if (targetPackageName.startsWith(pagesPackage)) {

                            String dir = targetPackageName.substring(pagesPackage.length());
                            FileObject targetTemplatesFolder = null;
                            log.finest("Get the dir name @" + dir);
                            if (dir != null && !"".equals(dir)) {
                                if (dir.contains(".")) {
                                    dir = dir.replaceAll(".", "/");
                                    log.finest("Get the dir name(converted to path) @" + dir);
                                }
                                targetTemplatesFolder = webRoot.getFileObject(dir);
                            } else {
                                //pagesPackage equals targetPackageName
                                targetTemplatesFolder = webRoot;
                            }


                            if (targetTemplatesFolder != null) {
                                Enumeration<? extends FileObject> templates = targetTemplatesFolder.getData(false);

                                FileObject template = null;
                                String templateName = null;
                                String guessClazzName = null;
                                String guessPagePath = "";

                                PageElement autoPageClazzPair = null;


                                while (templates.hasMoreElements()) {
                                    template = templates.nextElement();

                                    if ("jsp".equals(template.getExt()) || "htm".equals(template.getExt())) {

                                        templateName = template.getName();
                                        log.finest("Guess template@" + templateName);
                                        guessClazzName = computeClassNameByTemplateName(templateName);
                                        log.finest("Guess classname by template@" + guessClazzName);

                                        if (guessClazzName.equals(targetSimpleClazzName)) {

                                            guessPagePath = FileUtil.getRelativePath(webRoot, template);
                                            autoPageClazzPair = new PageElement(guessPagePath, guessClazzName);
                                            pageByPathMap.put(template, autoPageClazzPair);
                                            addToClassMap(project, autoPageClazzPair);
                                            pathClazzPairFound = true;
                                        }

                                        if (!pathClazzPairFound && !guessClazzName.endsWith("Page")) {

                                            guessClazzName = guessClazzName + "Page";
                                            log.finest("guess clazz name @" + guessClazzName);
                                            if (guessClazzName.equals(targetSimpleClazzName)) {
                                                guessPagePath = FileUtil.getRelativePath(webRoot, template);
                                                autoPageClazzPair = new PageElement(guessPagePath, guessClazzName);
                                                pageByPathMap.put(template, autoPageClazzPair);
                                                addToClassMap(project, autoPageClazzPair);
                                                pathClazzPairFound = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }//if (automap)
                }
            }// if (pagesList != null && !pagesList.isEmpty())


        }

        //fetch results...

        Set<PageElement> pathClazzPairList = pageByClassMap.get(classFO);
        List<FileObject> results = new ArrayList<FileObject>();
        if (pathClazzPairList != null) {
            for (PageElement p : pathClazzPairList) {
                results.add(findWebPageFileObjectByPath(webRoot, p.getPath()));
            }
        }
        return results.toArray(new FileObject[0]);
    }

    private static FileObject findWebPageFileObjectByPath(final FileObject webRoot, final String path) {

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

    private static String pageClassFQNForFileObjct(final FileObject classFO) {

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

    private static FileObject pageClassFileObjectForFQN(final Project project, String className) {
        Sources sources = ProjectUtils.getSources(project);
        SourceGroup[] sg = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        String classRelativePath = className.replaceAll("\\.", "/") + ".java";

        log.finest(">classRelativePath @" + classRelativePath);

        FileObject result = null;
        for (SourceGroup g : sg) {
            result = g.getRootFolder().getFileObject(classRelativePath);
            if (result != null) {
                break;
            }
        }
        return result;
    }

    public static FileObject findClassByPath(final Project project, final FileObject pathFO) {
        log.finest("Calll findClassByPath @@");
        initializeIfNeeded(project);

        //update cache firstly...if needed.
        WebModule wm = WebModule.getWebModule(project.getProjectDirectory());
        FileObject webRoot = wm.getDocumentBase();

        String templatePath = FileUtil.getRelativePath(webRoot, pathFO);
        log.finest("@template path @" + templatePath);

        PageElement page = pageByPathMap.get(pathFO);
        
        if (page == null) {
            log.finest("can not find page value in cache...");
            FileObject clickFO = ClickConfigUtilities.getClickConfigFile(project, ClickConstants.DEFAULT_CLICK_APP_CONFIG_FILE);
            ClickModel clickModel = ClickConfigUtilities.getClickModel(clickFO, false);
            ClickApp clickModelRoot = clickModel.getRootComponent();

            List<Pages> pagesList = clickModelRoot.getPagesList();
            if (pagesList != null && !pagesList.isEmpty()) {
                boolean automap = true;
                Pages pagesCom = null;
                String pagesPackage = "";
                for (int i = 0; i < pagesList.size(); i++) {
                    pagesCom = pagesList.get(i);
                    automap = !("false".equals(pagesCom.getAutoMapping()));
                    pagesPackage = pagesCom.getPackage();

                    List<Page> pageList = pagesCom.getPageList();
                    Page pageCom = null;

                    //search page mapping config.
                    for (int j = 0; j < pageList.size(); j++) {
                        pageCom = pageList.get(j);
                        if (templatePath.equals(pageCom.getPath())) {
                            String pageClazz = pageCom.getClassName();
                            FileObject pageClassFO = pageClassFileObjectForFQN(project, pageClazz);
                            if (pageClassFO != null) {
                                PageElement pathClazzPair = new PageElement(templatePath, pageClazz);
                                log.finest("add page elements @"+pathClazzPair);
                                pageByPathMap.put(pathFO, pathClazzPair);
                                addToClassMap(project, pathClazzPair);
                            } else {
                                //it is page template file, but does not has a page class.
                            }
                        }
                    }

                    //search automaping
                    if (automap) {
                        log.finest("process automaping...!");
                        String targetDirName = "";
                        String targetClazzName = "";

                        if(templatePath.startsWith("/")){
                            templatePath=templatePath.substring(1);
                        }

                        if (templatePath.contains("/")) {
                            targetDirName = templatePath.substring(0, templatePath.lastIndexOf("/"));
                            targetClazzName = templatePath.substring(templatePath.lastIndexOf("/") + 1);
                        } else {
                            targetClazzName = templatePath;
                        }

                        //chop file extension...
                        targetClazzName=targetClazzName.substring(0, targetClazzName.lastIndexOf("."));
                        log.finest("target dir name @"+targetDirName +", target class name @"+targetClazzName);

                        String guessClazzName = computeClassNameByTemplateName(targetClazzName);
                        String guessPackageDir = "";
                        if (pagesPackage != null && !"".equals(pagesPackage.trim())) {
                            guessPackageDir =pagesPackage.replaceAll("\\.", "/");
                        }
                        if (targetDirName != null && !"".equals(targetDirName.trim())) {
                            if(guessPackageDir.length()>0){
                                guessPackageDir+="/";
                            }

                            guessPackageDir = guessPackageDir + targetDirName;
                        }

                        log.finest("guess dir name @"+guessPackageDir +", guess class name @"+guessClazzName);

                        String guessClazzFilePath = guessPackageDir + "/"+ guessClazzName + ".java";

                        log.finest("guess class file path @"+guessClazzFilePath);

                        Sources sources = ProjectUtils.getSources(project);
                        SourceGroup[] sourceGroups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);

                        String pageClazz = "";
                        PageElement pathClazzPair = null;
                        boolean pathClazzPairFound = false;
                        for (SourceGroup sg : sourceGroups) {
                            if (sg.getRootFolder().getFileObject(guessClazzFilePath) != null) {
                                pageClazz = guessPackageDir + "/" + guessClazzName;
                                pageClazz = pageClazz.replaceAll("/", ".");

                                pathClazzPair = new PageElement(templatePath, pageClazz);
                                log.finest("add page elements @"+pathClazzPair);
                                pageByPathMap.put(pathFO, pathClazzPair);
                                addToClassMap(project, pathClazzPair);
                                pathClazzPairFound = true;
                            }

                            if (!pathClazzPairFound && !guessClazzName.endsWith("Page")) {
                                guessClazzFilePath = guessPackageDir  + "/" + guessClazzName + "Page.java";

                                if (sg.getRootFolder().getFileObject(guessClazzFilePath) != null) {
                                    pageClazz = guessPackageDir + "/" + guessClazzName + "Page";
                                    pageClazz = pageClazz.replaceAll("/", ".");

                                    pathClazzPair = new PageElement(templatePath, pageClazz);
                                    log.finest("add page elements @"+pathClazzPair);
                                    pageByPathMap.put(pathFO, pathClazzPair);
                                    addToClassMap(project, pathClazzPair);
                                    pathClazzPairFound = true;
                                }
                            }
                        }
                    }//if (automap)
                }
            }
        }


        //fetch result
        PageElement pageElement = pageByPathMap.get(pathFO);
        if (pageElement != null) {
            return pageClassFileObjectForFQN(project, pageElement.getClazz());
        }

        return null;
    }

    public static boolean isInitialized() {
        return pageByPathMap != null;
    }

    public static void initializeIfNeeded(Project project) {
        if (!isInitialized()) {
            initialize(project);
        }
    }

    //------------------private methods------------------
    private static void buildManualPageMapping(FileObject webRoot, Pages pages, String pagesPackage) {
        log.finest("starting buildManualPageMapping...");

        List<Page> pageList = pages.getPageList();
        if (pageList == null || pageList.isEmpty()) {
            return;
        }

        for (Page page : pageList) {
            pageByPathMap.put(findWebPageFileObjectByPath(webRoot, page.getPath()), new PageElement(page.getPath(), page.getClassName()));
            log.finest("Add '" + page.getPath() + "' -> '" + page.getClassName() + "' to pageByPathMap");
        }
    }

    private static void buildAutoPageMapping(Project project, FileObject webRoot, Pages pages, String pagesPackage, List<String> templates) {
        log.finest("starting buildAutoPageMapping...");
        for (int i = 0; i
                < templates.size(); i++) {
            String pagePath = templates.get(i);
            FileObject pageFO=findWebPageFileObjectByPath(webRoot, pagePath);
            if (!pageByPathMap.containsKey(pageFO)) {
                String pageClazz = getPageClass(project, pagePath, pagesPackage);
                if (pageClazz != null) {
                    pageByPathMap.put(pageFO, new PageElement(pagePath, pageClazz));
                    log.finest("Add '" + pagePath + "' -> '" + pageClazz + "' to pageByPathMap");
                }
            }
        }
    }

    private static String getPageClass(Project project, String pagePath, String pagesPackage) {
        log.finest(" Find page class package @" + pagesPackage + ", page path @" + pagePath);

        String packageName = pagesPackage + ".";
        String className = "";

        String path = pagePath.substring(0, pagePath.lastIndexOf("."));
        if (path.indexOf("/") != -1) {
            StringTokenizer tokenizer = new StringTokenizer(path, "/");
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if (tokenizer.hasMoreTokens()) {
                    packageName = packageName + token + ".";
                } else {
                    className = token;
                }
            }
        } else {
            className = path;
        }
        className = computeClassNameByTemplateName(className);
        log.finest("class name @"+className);

        // className = 'org.apache.click.pages.EditCustomer'
        className = packageName + className;
        String clazzRelativePath = className.replaceAll("\\.", "/") + ".java";
        log.finest("class relative path @"+clazzRelativePath);

        SourceGroup[] sourceGroups = ProjectUtils.getSources(project).getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        FileObject rootFolder = null;
        FileObject targetFileObject = null;
        for (SourceGroup group : sourceGroups) {
            rootFolder = group.getRootFolder();
            targetFileObject = rootFolder.getFileObject(clazzRelativePath);

            if (targetFileObject != null) {
                return className;
            }
        }

        if (!className.endsWith("Page")) {
            className = className + "Page";
            clazzRelativePath = className.replaceAll("\\.", "/") + ".java";
            for (SourceGroup group : sourceGroups) {
                rootFolder = group.getRootFolder();
                targetFileObject = rootFolder.getFileObject(clazzRelativePath);
                if (targetFileObject != null) {
                    return className;
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

    private static void buildClassMap(final Project project) {
        log.finest("starting buildClassMap...");
        for (Iterator i = pageByPathMap.values().iterator(); i.hasNext();) {
            PageElement page = (PageElement) i.next();
            addToClassMap(project, page);
        }
    }

    private static void addToClassMap(Project project, PageElement page) {
        log.finest("starting addToClassMap...");
        FileObject pageClassFO=pageClassFileObjectForFQN(project, page.getClazz());
        Set<PageElement> value = pageByClassMap.get(pageClassFO);

        if (value == null) {
            value = new HashSet<PageElement>();
            value.add(page);
            pageByClassMap.put(pageClassFO, value);
            log.finest("Add '" + page.getClazz() + "' ->'" + page.getPath() + "@" + page.getClazz() + "'");
        } else {
            log.finest("Add '" + page.getClazz() + "' ->'" + page.getPath() + "@" + page.getClazz() + "'");
            value.add(page);
            log.finest("There are " + value.size() + " mapping to a class, it is problematic at runtime...");
            //TODO Add error maker to Page Class...

        }
    }

    //-----------------inner classes-------------------------
    public static final class PageElement {

        String path;
        String clazz;

        public PageElement(String path, String clazz) {
            this.path = path;
            this.clazz = clazz;
        }

        public String getClazz() {
            return clazz;
        }

        public String getPath() {
            return path;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final PageElement other = (PageElement) obj;
            if ((this.path == null) ? (other.path != null) : !this.path.equals(other.path)) {
                return false;
            }
            if ((this.clazz == null) ? (other.clazz != null) : !this.clazz.equals(other.clazz)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 89 * hash + (this.path != null ? this.path.hashCode() : 0);
            hash = 89 * hash + (this.clazz != null ? this.clazz.hashCode() : 0);
            return hash;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("PageElement [");
            builder.append("clazz=");
            builder.append(clazz);
            builder.append(", path=");
            builder.append(path);
            builder.append("]");
            return builder.toString();
        }

    }
}
