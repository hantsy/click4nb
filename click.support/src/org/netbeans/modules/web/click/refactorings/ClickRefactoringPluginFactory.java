/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click.refactorings;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.TreePath;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.Element;
import org.netbeans.api.fileinfo.NonRecursiveFolder;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.refactoring.api.AbstractRefactoring;
import org.netbeans.modules.refactoring.api.MoveRefactoring;
import org.netbeans.modules.refactoring.api.RenameRefactoring;
import org.netbeans.modules.refactoring.api.SafeDeleteRefactoring;
import org.netbeans.modules.refactoring.api.WhereUsedQuery;
import org.netbeans.modules.refactoring.spi.RefactoringPlugin;
import org.netbeans.modules.refactoring.spi.RefactoringPluginFactory;
import org.netbeans.modules.web.click.ClickConfigUtilities;
import org.netbeans.modules.web.click.ClickConstants;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author hantsy
 */
@org.openide.util.lookup.ServiceProvider(service = org.netbeans.modules.refactoring.spi.RefactoringPluginFactory.class)
public class ClickRefactoringPluginFactory implements RefactoringPluginFactory {

    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger("org.netbeans.modules.web.click.refactorings.ClickRefactoringPluginFactory");

    public ClickRefactoringPluginFactory() {
    }

    @Override
    public RefactoringPlugin createInstance(AbstractRefactoring refactoring) {

        final Lookup lookup = refactoring.getRefactoringSource();
        FileObject sourceFO = lookup.lookup(FileObject.class);
        NonRecursiveFolder pkg = lookup.lookup(NonRecursiveFolder.class);

        TreePathHandle handle = resolveTreePathHandle(refactoring);

        boolean javaPackage = pkg != null && RefactoringUtil.isOnSourceClasspath(pkg.getFolder());
        boolean folder = sourceFO != null && sourceFO.isFolder();

        if (sourceFO == null) {
            if (handle != null) {
                sourceFO = handle.getFileObject();
            } else if (pkg != null) {
                sourceFO = pkg.getFolder();
            }
        }

        if (sourceFO == null) {
            return null;
        }

        boolean javaFile = sourceFO != null && RefactoringUtil.isJavaFile(sourceFO);
        //web pages and folder does not paticipate into refactorings.

        Project project = FileOwnerQuery.getOwner(sourceFO);
        if (project == null) {
            return null;
        }

        FileObject clickFO = ClickConfigUtilities.getClickConfigFile(project, ClickConstants.DEFAULT_CLICK_APP_CONFIG_FILE);
        if (clickFO == null) {
            return null;
        }

   
        String clazz = resolveClass(handle);

        // if we have a java file, the class name should be resolvable
        // unless it is an empty java file - see #130933
        if (javaFile && clazz == null) {
            LOGGER.fine("Could not resolve the class for: " + sourceFO + ", possibly an empty Java file");
            return null;
        }

        List<ClickRefactoring> refactorings = new ArrayList<ClickRefactoring>();

        if (refactoring instanceof RenameRefactoring) {
            RenameRefactoring rename = (RenameRefactoring) refactoring;
            if (javaPackage || folder) {
                refactorings.add(new ClickXmlPackageRename(clickFO,  sourceFO, rename));
            } else if (javaFile) {
                refactorings.add(new ClickXmlRename(clickFO,  clazz, rename));
            }
        }

        if (refactoring instanceof WhereUsedQuery && javaFile) {
            WhereUsedQuery whereUsedQuery = (WhereUsedQuery) refactoring;
            refactorings.add(new ClickXmlWhereUsed(clickFO, clazz, whereUsedQuery));
        }

        if (refactoring instanceof SafeDeleteRefactoring) {
            SafeDeleteRefactoring safeDelete = (SafeDeleteRefactoring) refactoring;
            if (javaFile) {    
                refactorings.add(new ClickXmlSafeDelete(clickFO, clazz, safeDelete));
//            } else if (javaPackage || folder) {
//                refactorings.add(new ClickXmlPackageSafeDelete(clickFO,
//                        sourceFO,  safeDelete));
            }
        }

        if (refactoring instanceof MoveRefactoring) {
            MoveRefactoring move = (MoveRefactoring) refactoring;
            if (javaFile) {
                refactorings.add(new ClickXmlMove(clickFO,  move));
            } else if (folder) {
                refactorings.add(new ClickXmlFolderMove(clickFO,  sourceFO, move));
            }
        }

        return refactorings.isEmpty() ? null : new ClickRefactoringPlugin(refactorings);
    }

    private TreePathHandle resolveTreePathHandle(final AbstractRefactoring refactoring) {

        TreePathHandle tph = refactoring.getRefactoringSource().lookup(TreePathHandle.class);
        if (tph != null) {
            return tph;
        }

        FileObject sourceFO = refactoring.getRefactoringSource().lookup(FileObject.class);
        if (sourceFO == null || !RefactoringUtil.isJavaFile(sourceFO)) {
            return null;
        }
        final TreePathHandle[] result = new TreePathHandle[1];
        try {

            JavaSource source = JavaSource.forFileObject(sourceFO);
            source.runUserActionTask(new CancellableTask<CompilationController>() {

                @Override
                public void cancel() {
                }

                @Override
                public void run(CompilationController co) throws Exception {
                    co.toPhase(JavaSource.Phase.RESOLVED);
                    CompilationUnitTree cut = co.getCompilationUnit();
                    if (cut.getTypeDecls().isEmpty()) {
                        return;
                    }
                    result[0] = TreePathHandle.create(TreePath.getPath(cut, cut.getTypeDecls().get(0)), co);
                }
            }, true);
        } catch (IOException ioe) {
            Exceptions.printStackTrace(ioe);
        }
        return result[0];
    }

    /**
     * @return the fully qualified name of the class that the given
     * TreePathHandle represents or null if the FQN could not be resolved.
     */
    private String resolveClass(final TreePathHandle treePathHandle) {
        if (treePathHandle == null) {
            return null;
        }
        final String[] result = new String[1];

        try {
            JavaSource source = JavaSource.forFileObject(treePathHandle.getFileObject());
            source.runUserActionTask(new CancellableTask<CompilationController>() {

                @Override
                public void cancel() {
                }

                @Override
                public void run(CompilationController parameter) throws Exception {
                    parameter.toPhase(JavaSource.Phase.RESOLVED);
                    Element element = treePathHandle.resolveElement(parameter);
                    // Fix for IZ159330 - NullPointerException at org.netbeans.modules.web.refactoring.WebRefactoringFactory$2.run
                    if (element == null) {
                        result[0] = null;
                    } else {
                        result[0] = element.asType().toString();
                    }
                }
            }, true);
        } catch (IOException ioe) {
            Exceptions.printStackTrace(ioe);
        }
        return result[0];
    }
}
