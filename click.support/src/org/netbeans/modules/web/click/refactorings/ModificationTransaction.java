/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.web.click.refactorings;

import org.netbeans.modules.refactoring.spi.Transaction;
import org.netbeans.modules.web.click.api.model.ClickModel;

/**
 *
 * @author hantsy
 */
class ModificationTransaction implements Transaction{

    ClickModel clickModel;
    public ModificationTransaction(ClickModel clickModel) {
        this.clickModel = clickModel;
        this.clickModel.startTransaction();
    }

    @Override
    public void commit() {
        clickModel.endTransaction();
    }

    @Override
    public void rollback() {
        clickModel.endTransaction();
    }

}
