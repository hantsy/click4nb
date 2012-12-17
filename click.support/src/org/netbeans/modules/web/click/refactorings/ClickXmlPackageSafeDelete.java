/*
 *
 */
package org.netbeans.modules.web.click.refactorings;

import java.util.List;
import org.netbeans.modules.refactoring.api.AbstractRefactoring;
import org.netbeans.modules.refactoring.api.Problem;
import org.netbeans.modules.refactoring.api.SafeDeleteRefactoring;
import org.netbeans.modules.refactoring.spi.RefactoringElementsBag;

import org.openide.filesystems.FileObject;

/**
 *
 * @author hantsy
 */
public class ClickXmlPackageSafeDelete extends ClickXmlRefactoring {

    private final SafeDeleteRefactoring safeDelete;
    private final List<String> classes;
    private final FileObject folder;

    public ClickXmlPackageSafeDelete(FileObject clickFO, FileObject folder, SafeDeleteRefactoring safeDelete) {
        super(clickFO);
        this.safeDelete = safeDelete;
        this.classes = RefactoringUtil.getRefactoredClasses(safeDelete);
        this.folder = folder;
    }

    @Override
    AbstractRefactoring getRefactoring() {
        return this.safeDelete;
    }

    @Override
    Problem prepare(RefactoringElementsBag bag) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
