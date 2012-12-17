/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.web.click.refactorings;

import java.util.Collections;
import java.util.List;
import org.netbeans.modules.j2ee.dd.api.web.WebApp;
import org.netbeans.modules.refactoring.api.AbstractRefactoring;
import org.netbeans.modules.refactoring.api.Problem;
import org.netbeans.modules.refactoring.api.RenameRefactoring;
import org.netbeans.modules.refactoring.spi.RefactoringElementsBag;
import org.netbeans.modules.web.click.api.model.ClickModel;
import org.openide.filesystems.FileObject;

/**
 * Handles renaming of classes specified in web.xml.
 *
 * @author Erno Mononen
 */
public class ClickXmlRename extends BaseRename{
    
    private final String oldFqn;
    private final RenameRefactoring rename;

    public ClickXmlRename( FileObject clickFO,  String oldFqn, RenameRefactoring rename ) {
        super(clickFO);
        this.oldFqn = oldFqn;
        this.rename = rename;
    }
    
    protected List<RenameItem> getRenameItems() {
        return Collections.<RenameItem>singletonList(new RenameItem(getNewFQN(), oldFqn));
    }

    private String getNewFQN(){
        return RefactoringUtil.renameClass(oldFqn, rename.getNewName());
    }

    protected AbstractRefactoring getRefactoring() {
        return rename;
    }

    @Override
    List<RenameItem> getPackageRenameItems() {
        return Collections.<RenameItem>emptyList();
    }


    
}
