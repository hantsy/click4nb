/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click.api.model.impl;

import org.netbeans.modules.web.click.api.model.MenuComponent;
import org.netbeans.modules.web.click.api.model.MenuVisitor;
import org.netbeans.modules.xml.xam.ComponentUpdater;
import org.netbeans.modules.xml.xam.ComponentUpdater.Operation;

/**
 *
 * @author hantsy
 */
public class MenuComponentUpdater extends MenuVisitor.Default implements ComponentUpdater<MenuComponent> {

    MenuComponent target;
    int index;
    Operation operation;

    public MenuComponentUpdater() {
    }

    public void update(MenuComponent target, MenuComponent child, Operation operation) {
        update(target, child, -1, operation);
    }

    public void update(MenuComponent target, MenuComponent child, int index, Operation operation) {
        this.target = target;
        this.index = index;
        this.operation = operation;

        child.accept(this);
    }

     private void insert(String propertyName, MenuComponent component) {
        ((MenuComponentImpl)target).insertAtIndex(propertyName, component, index);
    }

    private void remove(String propertyName, MenuComponent component) {
        ((MenuComponentImpl)target).removeChild(propertyName, component);
    }

    @Override
    public void accept(MenuComponent component) {
        if(target instanceof MenuComponent){
            if(operation == Operation.ADD){
                insert(MenuComponent.PROP_MENU, component);
            }else{
                remove(MenuComponent.PROP_MENU, component);
            }
        }
    }
}
