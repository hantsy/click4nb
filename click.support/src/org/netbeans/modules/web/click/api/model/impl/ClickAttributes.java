/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click.api.model.impl;

import org.netbeans.modules.web.click.api.model.ClassNameComponent;
import org.netbeans.modules.web.click.api.model.ClickApp;
import org.netbeans.modules.web.click.api.model.Excludes;
import org.netbeans.modules.web.click.api.model.Header;
import org.netbeans.modules.web.click.api.model.Page;
import org.netbeans.modules.web.click.api.model.PageInterceptor;
import org.netbeans.modules.web.click.api.model.Pages;
import org.netbeans.modules.web.click.api.model.Property;
import org.netbeans.modules.xml.xam.Named;
import org.netbeans.modules.xml.xam.dom.Attribute;

/**
 *
 * @author hantsy
 */
public enum ClickAttributes implements Attribute {

    NAME(Named.NAME_PROPERTY),
    CHARSET(ClickApp.PROP_CHARSET),
    LOCALE(ClickApp.PROP_LOCALE),
    PACKAGE(Pages.PROP_PACKAGE),
    AUTOBINDING(Pages.PROP_AUTOBINDING),
    AUTOMAPPING(Pages.PROP_AUTOMAPPING),
    CLASSNAME(ClassNameComponent.PROP_CLASSNAME),
    PATH(Page.PROP_PATH),
    VALUE(Property.PROP_VALUE),
    TYPE(Header.PROP_TYPE),
    PATTERN(Excludes.PROP_PATTERN),
    SCOPE(PageInterceptor.PROP_SCOPE);
    
    private String name;
    private Class type;

    ClickAttributes(String name) {
        this(name, String.class);
    }

    ClickAttributes(String name, Class type) {
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
