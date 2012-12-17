/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.web.click.api.model.impl;

import java.util.List;
import org.netbeans.modules.web.click.api.model.ClickVisitor;
import org.netbeans.modules.web.click.api.model.Header;
import org.netbeans.modules.web.click.api.model.Page;
import org.w3c.dom.Element;

/**
 *
 * @author hantsy
 */
public class PageImpl extends ClickComponentImpl implements Page{

    public PageImpl(ClickModelImpl model, Element element) {
        super(model, element);
    }

    public PageImpl(ClickModelImpl model) {
        this(model, createElementNS(model, ClickQNames.PAGE));
    }

    public void accept(ClickVisitor visitor) {
        visitor.accept(this);
    }

    public String getPath() {
        return super.getAttribute(ClickAttributes.PATH);
    }

    public void setPath(String path) {
        super.setAttribute(PROP_PATH, ClickAttributes.PATH, path);
    }

    public String getClassName() {
        return super.getAttribute(ClickAttributes.CLASSNAME);
    }

    public void setClassName(String cls) {
        super.setAttribute(PROP_CLASSNAME, ClickAttributes.CLASSNAME, cls);
    }

    public List<Header> getHeaderList() {
        return super.getChildren(Header.class);
    }

    public void addHeader(Header header) {
        super.appendChild(PROP_HEADER, header);
    }

    public void removeHeader(Header header) {
        super.removeChild(PROP_HEADER, header);
    }

}
