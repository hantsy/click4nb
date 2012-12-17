/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.web.click.api.model.impl;

import org.netbeans.modules.web.click.api.model.ClickVisitor;
import org.netbeans.modules.web.click.api.model.Excludes;
import org.w3c.dom.Element;

/**
 *
 * @author hantsy
 */
public class ExcludesImpl extends ClickComponentImpl implements Excludes {

    public ExcludesImpl(ClickModelImpl model, Element element) {
        super(model, element);
    }

    public ExcludesImpl(ClickModelImpl model) {
        this(model, createElementNS(model, ClickQNames.EXCLUDES));
    }

    public void accept(ClickVisitor visitor) {
        visitor.accept(this);
    }

    public String getPattern() {
        return super.getAttribute(ClickAttributes.PATTERN);
    }

    public void setPattern(String pattern) {
        super.setAttribute(PROP_PATTERN, ClickAttributes.PATTERN, pattern);
    }

}
