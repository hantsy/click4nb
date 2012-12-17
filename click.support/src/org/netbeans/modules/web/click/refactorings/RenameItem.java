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

import org.openide.util.Parameters;
import org.netbeans.modules.refactoring.api.Problem;

/**
 * Encapsulates the new and old name.
 * TODO: needs a better name
 *
 * @author Erno Mononen
 * @author ads 
 */
class RenameItem {

    private final String oldName;
    private final String newName;
    private final Problem myProblem;

    /**
     * Creates a new RenameItem.
     * 
     * @param newName the new fully qualified name for the object being renamed;
     * must not be empty or null.
     * @param oldName the old fully qualified name of the object being renamed;
     * must not be empty or null.
     */
    public RenameItem(String newFqn, String oldFqn) {
        Parameters.notEmpty("newFqn", newFqn); //NO18N
        Parameters.notEmpty("oldFqn", oldFqn); //NO18N
        this.newName = newFqn;
        this.oldName = oldFqn;
        myProblem = null;
    }
    
    public RenameItem(String newFqn, String oldFqn, Problem problem) {
        Parameters.notEmpty("newFqn", newFqn); //NO18N
        Parameters.notEmpty("oldFqn", oldFqn); //NO18N
        this.newName = newFqn;
        this.oldName = oldFqn;
        myProblem = problem;
    }
    
    public String getNewName() {
        return newName;
    }
    
    public String getOldName() {
        return oldName;
    }
    
    public Problem getProblem(){
        return myProblem;
    }
    
}
