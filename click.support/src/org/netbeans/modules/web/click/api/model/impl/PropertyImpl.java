/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.web.click.api.model.impl;

import org.netbeans.modules.web.click.api.model.ClickVisitor;
import org.netbeans.modules.web.click.api.model.Property;
import org.w3c.dom.Element;

/**
 *
 * @author hantsy
 */
public class PropertyImpl extends ClickComponentImpl.Named implements Property {

    public PropertyImpl(ClickModelImpl model, Element element) {
        super(model, element);
    }

    public PropertyImpl(ClickModelImpl model) {
        this(model, createElementNS(model, ClickQNames.PROPERTY));
    }

    public void accept(ClickVisitor visitor) {
        visitor.accept(this);
    }

    public String getValue() {
        return super.getAttribute(ClickAttributes.VALUE);
    }

    public void setValue(String value) {
        super.setAttribute(PROP_VALUE, ClickAttributes.VALUE, value);
    }

}
