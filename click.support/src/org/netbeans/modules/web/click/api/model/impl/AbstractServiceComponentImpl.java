/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click.api.model.impl;

import java.util.List;
import org.netbeans.modules.web.click.api.model.ClassNameComponent;
import org.netbeans.modules.web.click.api.model.Property;
import org.netbeans.modules.web.click.api.model.ServiceComponent;
import org.w3c.dom.Element;

/**
 *
 * @author hantsy
 */
public abstract class AbstractServiceComponentImpl extends ClickComponentImpl implements ServiceComponent {

    public AbstractServiceComponentImpl(ClickModelImpl model, Element element) {
        super(model, element);
    }

    public String getClassName() {
        return super.getAttribute(ClickAttributes.CLASSNAME);
    }

    public void setClassName(String classname) {
        super.setAttribute(PROP_CLASSNAME, ClickAttributes.CLASSNAME, classname);
    }

    public List<Property> getPropertyList() {
        return super.getChildren(Property.class);
    }

    public void addProperty(Property property) {
        super.appendChild(PROP_PROPERTY, property);
    }

    public void removeProperty(Property property) {
        super.removeChild(PROP_PROPERTY, property);
    }
}
