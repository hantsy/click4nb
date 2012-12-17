/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.web.click.api.model.impl;

import org.netbeans.modules.web.click.api.model.ClickVisitor;
import org.netbeans.modules.web.click.api.model.Header;
import org.w3c.dom.Element;

/**
 *
 * @author hantsy
 */
public class HeaderImpl extends ClickComponentImpl.Named implements Header{

    public HeaderImpl(ClickModelImpl model, Element element) {
        super(model, element);
    }

    public HeaderImpl(ClickModelImpl model) {
        super(model, createElementNS(model, ClickQNames.HEADER));
    }

    public void accept(ClickVisitor visitor) {
        visitor.accept(this);
    }

    public String getValue() {
        return super.getAttribute(ClickAttributes.NAME);
    }

    public void setValue(String value) {
        super.setAttribute(NAME_PROPERTY, ClickAttributes.NAME, value);
    }

    public String getType() {
        return super.getAttribute(ClickAttributes.TYPE);
    }

    public void setType(String type) {
        super.setAttribute(PROP_TYPE, ClickAttributes.TYPE, type);
    }


}
