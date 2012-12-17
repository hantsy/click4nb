/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.web.click.api.model.impl;

import org.netbeans.modules.web.click.api.model.ClickVisitor;
import org.netbeans.modules.web.click.api.model.Format;
import org.w3c.dom.Element;

/**
 *
 * @author hantsy
 */
public class FormatImpl extends ClickComponentImpl implements Format {

    public FormatImpl(ClickModelImpl model, Element element) {
        super(model, element);
    }
    public FormatImpl(ClickModelImpl model) {
        super(model, createElementNS(model, ClickQNames.FORMAT));
    }

    public void accept(ClickVisitor visitor) {
        visitor.accept(this);
    }

    public String getClassName() {
       return super.getAttribute(ClickAttributes.CLASSNAME);
    }

    public void setClassName(String clsname) {
        super.setAttribute(PROP_CLASSNAME, ClickAttributes.CLASSNAME, clsname);
    }

}
