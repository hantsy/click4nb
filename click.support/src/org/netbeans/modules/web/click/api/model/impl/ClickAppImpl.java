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

    @Override
    public void accept(ClickVisitor visitor) {
        visitor.accept(this);
    }

    @Override
    public Headers getHeaders() {
        return super.getChild(Headers.class);
    }

    @Override
    public void setHeaders(Headers headers) {
        Collection<Class<? extends ClickComponent>> list = new ArrayList<Class<? extends ClickComponent>>();
        list.add(Pages.class);
        super.setChild(Headers.class, PROP_HEADERS, headers, list);
    }

    @Override
    public List<Pages> getPagesList() {
        return super.getChildren(Pages.class);
    }

    @Override
    public void addPages(Pages pages) {
        Collection<Class<? extends ClickComponent>> list = new ArrayList<Class<? extends ClickComponent>>();
        list.add(Pages.class);
        super.addAfter(PROP_PAGES, pages, list);//appendChild(PROP_PAGES, pages);
    }

    @Override
    public void removePages(Pages pages) {
        super.removeChild(PROP_PAGES, pages);
    }

    @Override
    public Format getFormat() {
        return super.getChild(Format.class);
    }

    @Override
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

    @Override
    public Mode getMode() {
        return super.getChild(Mode.class);
    }

    @Override
    public void setMode(Mode mode) {
        Collection<Class<? extends ClickComponent>> list = new ArrayList<Class<? extends ClickComponent>>();
        list.add(Format.class);
        super.setChild(Mode.class, PROP_MODE, mode, list);
    }

    @Override
    public Controls getControls() {
        return super.getChild(Controls.class);
    }

    @Override
    public void setControls(Controls controls) {
        Collection<Class<? extends ClickComponent>> list = new ArrayList<Class<? extends ClickComponent>>();
        list.add(Mode.class);
        super.setChild(Controls.class, PROP_CONTROLS, controls, list);
    }

    @Override
    public FileUploadService getFileUploadService() {
        return super.getChild(FileUploadService.class);
    }

    @Override
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

    @Override
    public LogService getLogService() {
        return super.getChild(LogService.class);
    }

    @Override
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

    @Override
    public TemplateService getTemplateService() {
        return super.getChild(TemplateService.class);
    }

    @Override
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

    @Override
    public String getCharset() {
        return super.getAttribute(ClickAttributes.CHARSET);
    }

    @Override
    public void setCharse(String charset) {
        super.setAttribute(PROP_CHARSET, ClickAttributes.CHARSET, charset);
    }

    @Override
    public String getLocale() {
        return super.getAttribute(ClickAttributes.LOCALE);
    }

    @Override
    public void setLocale(String locale) {
        super.setAttribute(PROP_LOCALE, ClickAttributes.LOCALE, locale);
    }

    @Override
    public List<PageInterceptor> getPageInterceptorList() {
        return super.getChildren(PageInterceptor.class);
    }

    @Override
    public void addPageInterceptor(PageInterceptor pi) {
        Collection<Class<? extends ClickComponent>> list = new ArrayList<Class<? extends ClickComponent>>();
        list.add(PageInterceptor.class);
        super.addAfter(PROP_PAGE_INTERCEPTOR, pi, list);//appendChild(PROP_PAGES, pages);
    }

    @Override
    public void removePageInterceptor(PageInterceptor pi) {
        super.removeChild(PROP_PAGE_INTERCEPTOR, pi);
    }
}
