/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.web.click.api.model.impl;

import org.netbeans.modules.web.click.api.model.ClickVisitor;
import org.netbeans.modules.web.click.api.model.Control;
import org.w3c.dom.Element;

/**
 *
 * @author hantsy
 */
public class ControlImpl extends ClickComponentImpl implements Control{

    public ControlImpl(ClickModelImpl model, Element element) {
        super(model, element);
    }

    public ControlImpl(ClickModelImpl model) {
        this(model, createElementNS(model, ClickQNames.CONTROL));
    }

    public void accept(ClickVisitor visitor) {
        visitor.accept(this);
    }

    public String getClassName() {
        return super.getAttribute(ClickAttributes.CLASSNAME);
    }

    public void setClassName(String clz) {
        super.setAttribute(PROP_CLASSNAME, ClickAttributes.CLASSNAME, clz);
    }

}
