/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.web.click.api.model.impl;

import org.netbeans.modules.web.click.api.model.MenuComponent;
import org.netbeans.modules.web.click.api.model.MenuComponentFactory;
import org.netbeans.modules.web.click.api.model.MenuVisitor;
import org.netbeans.modules.xml.xam.dom.AbstractDocumentComponent;
import org.w3c.dom.Element;

/**
 *
 * @author hantsy
 */
public class MenuComponentFactoryImpl implements MenuComponentFactory {
    MenuModelImpl model;

    public MenuComponentFactoryImpl(MenuModelImpl model) {
        this.model=model;
    }

    public MenuComponent createMenu(MenuComponent component) {
        return new MenuComponentImpl(model);
    }

    public MenuComponent create(Element element, MenuComponent context) {
        if (context == null) {
            if (areSameQName(MenuQNames.MENU, element)) {
                return new MenuComponentImpl(model, element);
            } else {
                return null;
            }
        } else {
            return new CreateVisitor().create(element, context);
        }
    }

    public static boolean areSameQName(MenuQNames q, Element e) {
        return q.getQName().equals(AbstractDocumentComponent.getQName(e));
    }

    public static class  CreateVisitor extends MenuVisitor.Default{

        Element element;
        MenuComponent created;

        private MenuComponent create(Element element, MenuComponent context) {
            this.element = element;
            context.accept(this);
            return created;
        }

        @Override
        public void accept(MenuComponent component) {
            if(isElementQName(MenuQNames.MENU)){
                created= new MenuComponentImpl((MenuModelImpl)component.getModel(), element);
            }
        }


        private boolean isElementQName(MenuQNames q) {
            return areSameQName(q, element);
        }
    }

}
