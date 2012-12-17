package org.netbeans.modules.web.click.refactorings;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.netbeans.modules.j2ee.dd.api.web.WebApp;
import org.netbeans.modules.refactoring.api.AbstractRefactoring;
import org.netbeans.modules.refactoring.api.MoveRefactoring;
import org.netbeans.modules.web.click.api.model.ClickModel;
import org.openide.filesystems.FileObject;

/**
 * Handles moving of a class.
 *
 * @author Erno Mononen
 */
public class ClickXmlMove extends BaseRename{
    
    private final MoveRefactoring move;
    private final List<String> classes;
    
    public ClickXmlMove(FileObject clickFO, MoveRefactoring move) {
        super(clickFO);
        this.move = move;
        this.classes = RefactoringUtil.getRefactoredClasses(move);
    }
    
    protected AbstractRefactoring getRefactoring() {
        return move;
    }
    
    protected List<RenameItem> getRenameItems() {
        String pkg = RefactoringUtil.getPackageName(move.getTarget().lookup(URL.class));
        List<RenameItem> result = new ArrayList<RenameItem>();
        for (String clazz : classes) {
            String newName = pkg + "." + RefactoringUtil.unqualify(clazz);
            result.add(new RenameItem(newName, clazz));
        }
        return result;
    }

    @Override
    List<RenameItem> getPackageRenameItems() {
        return Collections.<RenameItem>emptyList();
    }
}
