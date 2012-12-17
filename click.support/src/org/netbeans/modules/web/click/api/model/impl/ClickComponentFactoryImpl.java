/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click.api.model.impl;

import org.netbeans.modules.web.click.api.model.ClickApp;
import org.netbeans.modules.web.click.api.model.ClickComponent;
import org.netbeans.modules.web.click.api.model.ClickComponentFactory;
import org.netbeans.modules.web.click.api.model.ClickVisitor;
import org.netbeans.modules.web.click.api.model.Control;
import org.netbeans.modules.web.click.api.model.ControlSet;
import org.netbeans.modules.web.click.api.model.Controls;
import org.netbeans.modules.web.click.api.model.FileUploadService;
import org.netbeans.modules.web.click.api.model.Format;
import org.netbeans.modules.web.click.api.model.Header;
import org.netbeans.modules.web.click.api.model.Headers;
import org.netbeans.modules.web.click.api.model.LogService;
import org.netbeans.modules.web.click.api.model.Mode;
import org.netbeans.modules.web.click.api.model.Page;
import org.netbeans.modules.web.click.api.model.Pages;
import org.netbeans.modules.web.click.api.model.TemplateService;
import org.netbeans.modules.xml.xam.dom.AbstractDocumentComponent;
import org.w3c.dom.Element;

/**
 *
 * @author hantsy
 */
class ClickComponentFactoryImpl implements ClickComponentFactory {

    private ClickModelImpl model;

    public ClickComponentFactoryImpl(ClickModelImpl model) {
        this.model = model;
    }

    public ClickComponent create(Element element, ClickComponent context) {
        if (context == null) {
            if (areSameQName(ClickQNames.CLICK_APP, element)) {
                return new ClickAppImpl(model, element);
            } else {
                return null;
            }
        } else {
            return new CreateVisitor().create(element, context);
        }
    }

    public static boolean areSameQName(ClickQNames q, Element e) {
        return q.getQName().equals(AbstractDocumentComponent.getQName(e));
    }

    public Headers createHeaders() {
        return new HeadersImpl(model);
    }

    public Header createHeader() {
        return new HeaderImpl(model);
    }

    public Pages createPages() {
        return new PagesImpl(model);
    }

    public Page createPage() {
        return new PageImpl(model);
    }

    public Controls createControls() {
        return new ControlsImpl(model);
    }

    public Control createControl() {
        return new ControlImpl(model);
    }

    public ControlSet createControlSet() {
        return new ControlSetImpl(model);
    }

    public Format createFormat() {
        return new FormatImpl(model);
    }

    public Mode createMode() {
        return new ModeImpl(model);
    }

    public TemplateService createTemplateService() {
        return new TemplateServiceImpl(model);
    }

    public FileUploadService createFileUploadService() {
        return new FileUploadServiceImpl(model);
    }

    public LogService createLogService() {
        return new LogServiceImpl(model);
    }

    public static class CreateVisitor extends ClickVisitor.Default{

        Element element;
        ClickComponent created;

        private ClickComponent create(Element element, ClickComponent context) {
            this.element = element;
            context.accept(this);
            return created;
        }

        private boolean isElementQName(ClickQNames q) {
            return areSameQName(q, element);
        }

        @Override
        public void accept(ClickApp component) {
            if(isElementQName(ClickQNames.HEADERS)){
                created = new HeadersImpl((ClickModelImpl)component.getModel(),element);
            }else if(isElementQName(ClickQNames.MODE)){
                created = new ModeImpl((ClickModelImpl)component.getModel(),element);
            }else if(isElementQName(ClickQNames.PAGES)){
                created = new PagesImpl((ClickModelImpl)component.getModel(),element);
            }else if(isElementQName(ClickQNames.CONTROLS)){
                created = new ControlsImpl((ClickModelImpl)component.getModel(),element);
            }else if(isElementQName(ClickQNames.FILE_UPLOAD_SERVICE)){
                created = new FileUploadServiceImpl((ClickModelImpl)component.getModel(),element);
            }else if(isElementQName(ClickQNames.LOG_SERVICE)){
                created = new LogServiceImpl((ClickModelImpl)component.getModel(),element);
            }else if(isElementQName(ClickQNames.TEMPLATE_SERVICE)){
                created = new TemplateServiceImpl((ClickModelImpl)component.getModel(),element);
            }else if(isElementQName(ClickQNames.FORMAT)){
                created = new FormatImpl((ClickModelImpl)component.getModel(),element);
            }
        }

        @Override
        public void accept(Headers component) {
            if(isElementQName(ClickQNames.HEADER)){
                created = new HeaderImpl((ClickModelImpl)component.getModel(),element);
            }
        }


        @Override
        public void accept(Pages component) {
            if(isElementQName(ClickQNames.PAGE)){
                created = new PageImpl((ClickModelImpl)component.getModel(),element);
            }else if(isElementQName(ClickQNames.EXCLUDES)){
                created = new ExcludesImpl((ClickModelImpl)component.getModel(),element);
            }
        }

        @Override
        public void accept(Page component) {
            if(isElementQName(ClickQNames.HEADER)){
                created = new HeaderImpl((ClickModelImpl)component.getModel(),element);
            }
        }

        @Override
        public void accept(Controls component) {
            if(isElementQName(ClickQNames.CONTROL)){
             created = new ControlImpl((ClickModelImpl)component.getModel(),element);
            }else if(isElementQName(ClickQNames.CONTROL_SET)){
             created = new ControlSetImpl((ClickModelImpl)component.getModel(),element);
            }
        }


        @Override
        public void accept(FileUploadService component) {
            if(isElementQName(ClickQNames.PROPERTY)){
                created = new PropertyImpl((ClickModelImpl)component.getModel(),element);
            }
        }

        @Override
        public void accept(LogService component) {
            if(isElementQName(ClickQNames.PROPERTY)){
                created = new PropertyImpl((ClickModelImpl)component.getModel(),element);
            }
        }

        @Override
        public void accept(TemplateService component) {
           if(isElementQName(ClickQNames.PROPERTY)){
                created = new PropertyImpl((ClickModelImpl)component.getModel(),element);
            }
        }

    }
}
