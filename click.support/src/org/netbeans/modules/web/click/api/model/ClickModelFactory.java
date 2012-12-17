/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click.api.model;

import org.netbeans.modules.web.click.api.model.impl.ClickModelImpl;
import org.netbeans.modules.xml.xam.AbstractModelFactory;
import org.netbeans.modules.xml.xam.ModelSource;

/**
 *
 * @author hantsy
 */
public class ClickModelFactory extends AbstractModelFactory<ClickModel> {

    private static ClickModelFactory instance;

    private ClickModelFactory() {
    }

    public static ClickModelFactory getInstance() {
        if (instance == null) {
            instance = new ClickModelFactory();
        }

        return instance;
    }

    @Override
    protected ClickModel createModel(ModelSource source) {
        return new ClickModelImpl(source);
    }

    public ClickModel getModel(ModelSource source) {
        return super.getModel(source);
    }

}
