/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click.api.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.netbeans.modules.web.click.api.model.ClickApp;
import org.netbeans.modules.web.click.api.model.ClickComponent;
import org.netbeans.modules.web.click.api.model.ClickVisitor;
import org.netbeans.modules.web.click.api.model.Controls;
import org.netbeans.modules.web.click.api.model.FileUploadService;
import org.netbeans.modules.web.click.api.model.Format;
import org.netbeans.modules.web.click.api.model.Headers;
import org.netbeans.modules.web.click.api.model.LogService;
import org.netbeans.modules.web.click.api.model.Mode;
import org.netbeans.modules.web.click.api.model.PageInterceptor;
import org.netbeans.modules.web.click.api.model.Pages;
import org.netbeans.modules.web.click.api.model.TemplateService;
import org.w3c.dom.Element;

/**
 *
 * @author hantsy
 */
public class ClickAppImpl extends ClickComponentImpl implements ClickApp {

    public ClickAppImpl(ClickModelImpl model, Element element) {
        super(model, element);
    }

    public ClickAppImpl(ClickModelImpl model) {
        this(model, createElementNS(model, ClickQNames.CLICK_APP));
    }

    public void accept(ClickVisitor visitor) {
        visitor.accept(this);
    }

    public Headers getHeaders() {
        return super.getChild(Headers.class);
    }

    public void setHeaders(Headers headers) {
        Collection<Class<? extends ClickComponent>> list = new ArrayList<Class<? extends ClickComponent>>();
        list.add(Pages.class);
        super.setChild(Headers.class, PROP_HEADERS, headers, list);
    }

    public List<Pages> getPagesList() {
        return super.getChildren(Pages.class);
    }

    public void addPages(Pages pages) {
        Collection<Class<? extends ClickComponent>> list = new ArrayList<Class<? extends ClickComponent>>();
        list.add(Pages.class);
        super.addAfter(PROP_PAGES, pages, list);//appendChild(PROP_PAGES, pages);
    }

    public void removePages(Pages pages) {
        super.removeChild(PROP_PAGES, pages);
    }

    public Format getFormat() {
        return super.getChild(Format.class);
    }

    public void setFormat(Format format) {
        if (format == null) {
            if (getFormat() != null) {
                super.removeChild(PROP_FORMAT, getFormat());
            }
        } else {
            Collection<Class<? extends ClickComponent>> list = new ArrayList<Class<? extends ClickComponent>>();
            list.add(Headers.class);
            setChild(Format.class, PROP_FORMAT, format, list);
        }
    }

    public Mode getMode() {
        return super.getChild(Mode.class);
    }

    public void setMode(Mode mode) {
        Collection<Class<? extends ClickComponent>> list = new ArrayList<Class<? extends ClickComponent>>();
        list.add(Format.class);
        super.setChild(Mode.class, PROP_MODE, mode, list);
    }

    public Controls getControls() {
        return super.getChild(Controls.class);
    }

    public void setControls(Controls controls) {
        Collection<Class<? extends ClickComponent>> list = new ArrayList<Class<? extends ClickComponent>>();
        list.add(Mode.class);
        super.setChild(Controls.class, PROP_CONTROLS, controls, list);
    }

    public FileUploadService getFileUploadService() {
        return super.getChild(FileUploadService.class);
    }

    public void setFileUploadService(FileUploadService service) {
        if (service == null) {
            if (getFileUploadService() != null) {
                super.removeChild(PROP_FILE_UPLOAD_SERVICE, getFileUploadService());
            }
        } else {
            Collection<Class<? extends ClickComponent>> list = new ArrayList<Class<? extends ClickComponent>>();
            list.add(Controls.class);
            super.setChild(FileUploadService.class, PROP_FILE_UPLOAD_SERVICE, service, list);
        }
    }

    public LogService getLogService() {
        return super.getChild(LogService.class);
    }

    public void setLogService(LogService service) {
        if (service == null) {
            if (getLogService() != null) {
                super.removeChild(PROP_LOG_SERVICE, getLogService());
            }
        } else {
            Collection<Class<? extends ClickComponent>> list = new ArrayList<Class<? extends ClickComponent>>();
            list.add(FileUploadService.class);
            setChild(LogService.class, PROP_LOG_SERVICE, service, list);
        }
    }

    public TemplateService getTemplateService() {
        return super.getChild(TemplateService.class);
    }

    public void setTemplateService(TemplateService service) {
        if (service == null) {
            if (getTemplateService() != null) {
                super.removeChild(PROP_TEMPLATE_SERVICE, getTemplateService());
            }
        } else {
            Collection<Class<? extends ClickComponent>> list = new ArrayList<Class<? extends ClickComponent>>();
            list.add(LogService.class);
            super.setChild(TemplateService.class, PROP_TEMPLATE_SERVICE, service, list);
        }
    }

    public String getCharset() {
        return super.getAttribute(ClickAttributes.CHARSET);
    }

    public void setCharse(String charset) {
        super.setAttribute(PROP_CHARSET, ClickAttributes.CHARSET, charset);
    }

    public String getLocale() {
        return super.getAttribute(ClickAttributes.LOCALE);
    }

    public void setLocale(String locale) {
        super.setAttribute(PROP_LOCALE, ClickAttributes.LOCALE, locale);
    }

    public List<PageInterceptor> getPageInterceptorList() {
        return super.getChildren(PageInterceptor.class);
    }

    public void addPageInterceptor(PageInterceptor pi) {
        Collection<Class<? extends ClickComponent>> list = new ArrayList<Class<? extends ClickComponent>>();
        list.add(PageInterceptor.class);
        super.addAfter(PROP_PAGE_INTERCEPTOR, pi, list);//appendChild(PROP_PAGES, pages);
    }

    public void removePageInterceptor(PageInterceptor pi) {
        super.removeChild(PROP_PAGE_INTERCEPTOR, pi);
    }
}
