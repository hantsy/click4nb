package org.netbeans.modules.web.click.refactorings;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.refactoring.api.AbstractRefactoring;
import org.netbeans.modules.refactoring.api.MoveRefactoring;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

public class ClickXmlFolderMove extends BaseRename {

    private final MoveRefactoring move;
    private final FileObject folder;

    public ClickXmlFolderMove(FileObject clickFO, FileObject folder, MoveRefactoring move) {
        super(clickFO);
        this.folder = folder;
        this.move = move;
    }

    protected List<RenameItem> getRenameItems() {
        List<RenameItem> result = new ArrayList<RenameItem>();
        List<FileObject> fos = new ArrayList<FileObject>();
        RefactoringUtil.collectChildren(folder, fos);
        for (FileObject each : fos) {
            // #142870 -- skip package-info, it is not needed in web.xml refactoring
            if (RefactoringUtil.isPackageInfo(each)) {
                continue;
            }
            String oldFqn = RefactoringUtil.getQualifiedName(each);
            String targetPackageName = getTargetPackageName(each.getParent());
            String oldUnqualifiedName = RefactoringUtil.unqualify(oldFqn);
            String newFqn = targetPackageName.length() == 0 ? oldUnqualifiedName : targetPackageName + "." + oldUnqualifiedName;
            result.add(new RenameItem(newFqn, oldFqn));
        }
        return result;
    }

    @Override
    List<RenameItem> getPackageRenameItems() {
        List<RenameItem> result = new ArrayList<RenameItem>();
        List<FileObject> fos = new ArrayList<FileObject>();
        RefactoringUtil.collectFolders(folder, fos);
        result.add(new RenameItem(getTargetPackageName(folder), RefactoringUtil.getPackageName(folder)));
        for (FileObject each : fos) {
            String oldPackageName = RefactoringUtil.getPackageName(each);
            String targetPackageName = getTargetPackageName(each);
            result.add(new RenameItem(targetPackageName, oldPackageName));
        }
        return result;
    }

    private String getTargetPackageName(FileObject fo) {
        String newPackageName = RefactoringUtil.getPackageName(move.getTarget().lookup(URL.class));
        String postfix = FileUtil.getRelativePath(this.folder.getParent(), fo).replace('/', '.');

        if (newPackageName.length() == 0) {
            return postfix;
        }
        if (postfix.length() == 0) {
            return newPackageName;
        }
        return newPackageName + "." + postfix;
    }

    @Override
    protected AbstractRefactoring getRefactoring() {
        return move;
    }
}
