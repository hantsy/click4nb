/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click.api.model;

import org.netbeans.modules.web.click.api.model.impl.ClickModelImpl;
import org.netbeans.modules.web.click.api.model.impl.MenuModelImpl;
import org.netbeans.modules.xml.xam.AbstractModelFactory;
import org.netbeans.modules.xml.xam.ModelSource;

/**
 *
 * @author hantsy
 */
public class MenuModelFactory extends AbstractModelFactory<MenuModel> {

    private static MenuModelFactory instance;

    private MenuModelFactory() {
    }

    public static MenuModelFactory getInstance() {
        if (instance == null) {
            instance = new MenuModelFactory();
        }

        return instance;
    }

    @Override
    protected MenuModel createModel(ModelSource source) {
        return new MenuModelImpl(source);
    }

    public MenuModel getModel(ModelSource source) {
        return super.getModel(source);
    }
}
