/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.web.click.api.model.impl;

import java.awt.MenuContainer;
import java.util.Set;
import java.util.Set;
import javax.xml.namespace.QName;
import org.netbeans.modules.web.click.api.model.MenuComponent;
import org.netbeans.modules.web.click.api.model.MenuComponentFactory;
import org.netbeans.modules.web.click.api.model.MenuModel;
import org.netbeans.modules.xml.xam.ComponentUpdater;
import org.netbeans.modules.xml.xam.ModelSource;
import org.netbeans.modules.xml.xam.dom.AbstractDocumentModel;
import org.w3c.dom.Element;

/**
 *
 * @author hantsy
 */
public class MenuModelImpl extends AbstractDocumentModel<MenuComponent> implements MenuModel{

    private MenuComponent root;
    private MenuComponentFactory factory;
    public MenuModelImpl(ModelSource source) {
        super(source);
        factory=new MenuComponentFactoryImpl(this);
    }

    @Override
    public MenuComponent createRootComponent(Element element) {
        MenuComponent newRoot=factory.create(element, null);
        if(newRoot !=null){
            root = newRoot;
        }
        return root;
    }

    @Override
    protected ComponentUpdater<MenuComponent> getComponentUpdater() {
        return new MenuComponentUpdater();
    }

    public MenuComponent getRootComponent() {
        return root;
    }

    @Override
    public Set<QName> getQNames() {
        return MenuQNames.getMappedQNames();
    }

    public MenuComponent createComponent(MenuComponent parent, Element element) {
        return getFactory().create(element, parent);
    }

    public MenuComponentFactory getFactory() {
        return factory;
    }


}
