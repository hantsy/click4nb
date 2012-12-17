/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.web.click.refactorings;

import org.netbeans.modules.refactoring.api.Problem;
import org.netbeans.modules.refactoring.spi.RefactoringElementsBag;

/**
 *
 * @author hantsy
 */
interface ClickRefactoring {

    Problem doPrepare(RefactoringElementsBag refactoringElementsBag);

    Problem preCheck();

}