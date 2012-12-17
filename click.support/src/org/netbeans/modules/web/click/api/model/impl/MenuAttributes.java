/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.web.click.api.model.impl;

import org.netbeans.modules.web.click.api.model.MenuComponent;
import org.netbeans.modules.xml.xam.dom.Attribute;

/**
 *
 * @author hantsy
 */
public enum  MenuAttributes implements Attribute {
    ID(MenuComponent.PROP_ID),
    NAME(MenuComponent.PROP_NAME),
    LABEL(MenuComponent.PROP_LABEL),
    PATH(MenuComponent.PROP_PATH),
    PAGES(MenuComponent.PROP_PAGES),
    TITLE(MenuComponent.PROP_TITLE),
    TARGET(MenuComponent.PROP_TARGET),
    ROLES(MenuComponent.PROP_ROLES),
    IMAGE_SRC(MenuComponent.PROP_ROLES),
    EXTERNAL(MenuComponent.PROP_EXTERNAL,Boolean.class),
    SPARATOR(MenuComponent.PROP_SPARATOR, Boolean.class)
    ;

    private String name;
    private Class type;

    MenuAttributes(String name) {
        this(name, String.class);
    }

    MenuAttributes(String name, Class type) {
        this.name = name;
        this.type = type;
    }

    public Class getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Class getMemberType() {
        return null;
    }
}
