/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click.api.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.netbeans.modules.web.click.api.model.ClickComponent;
import org.netbeans.modules.web.click.api.model.ClickVisitor;
import org.netbeans.modules.web.click.api.model.Excludes;
import org.netbeans.modules.web.click.api.model.Page;
import org.netbeans.modules.web.click.api.model.Pages;
import org.w3c.dom.Element;

/**
 *
 * @author hantsy
 */
public class PagesImpl extends ClickComponentImpl implements Pages {

    public PagesImpl(ClickModelImpl model, Element element) {
        super(model, element);
    }

    public PagesImpl(ClickModelImpl model) {
        super(model, createElementNS(model, ClickQNames.PAGES));
    }

    public void accept(ClickVisitor visitor) {
        visitor.accept(this);
    }

    public String getAutoBinding() {
        return super.getAttribute(ClickAttributes.AUTOBINDING);
    }

    public void setAutoBinding(String value) {
        super.setAttribute(PROP_AUTOBINDING, ClickAttributes.AUTOBINDING, value);
    }

    public String getAutoMapping() {
        return super.getAttribute(ClickAttributes.AUTOMAPPING);
    }

    public void setAutoMapping(String value) {
        super.setAttribute(PROP_AUTOMAPPING, ClickAttributes.AUTOMAPPING, value);
    }

    public String getPackage() {
        return super.getAttribute(ClickAttributes.PACKAGE);
    }

    public void setPackage(String pkg) {
        super.setAttribute(PROP_PACKAGE, ClickAttributes.PACKAGE, pkg);
    }

    public List<Page> getPageList() {
        return super.getChildren(Page.class);
    }

    public void addPage(Page page) {
        super.appendChild(PROP_PAGE, page);
    }

    public void removePage(Page page) {
        super.removeChild(PROP_PAGE, page);
    }

    public List<Excludes> getExcludesList() {
        return super.getChildren(Excludes.class);
    }

    public void addExcludes(Excludes excludes) {
        Collection<Class<? extends ClickComponent>> list = new ArrayList<Class<? extends ClickComponent>>();
        list.add(Excludes.class);
        list.add(Page.class);
        super.addAfter(PROP_EXCLUDES, excludes, list);//appendChild(PROP_EXCLUDES, excludes);
    }

    public void removeExcludes(Excludes excludes) {
        super.removeChild(PROP_EXCLUDES, excludes);
    }
}
