/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.web.click.api.model.impl;

import org.netbeans.modules.web.click.api.model.ClickVisitor;
import org.netbeans.modules.web.click.api.model.PageInterceptor;
import org.w3c.dom.Element;

/**
 *
 * @author hantsy
 */
public class PageInterceptorImpl extends AbstractServiceComponentImpl implements PageInterceptor{

    public PageInterceptorImpl(ClickModelImpl model, Element element) {
        super(model, element);
    }

    public PageInterceptorImpl(ClickModelImpl model){
        this(model, createElementNS(model, ClickQNames.PAGE_INTERCEPTOR));
    }

    public void accept(ClickVisitor visitor) {
        visitor.accept(this);
    }

    public String getScope() {
        return super.getAttribute(ClickAttributes.SCOPE);
    }

    public void setScope(String scope) {
        super.setAttribute(PROP_SCOPE, ClickAttributes.SCOPE, scope);
    }

}
