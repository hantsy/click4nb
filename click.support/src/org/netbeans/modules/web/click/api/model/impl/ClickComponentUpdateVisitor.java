/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.web.click.api.model.impl;

import org.netbeans.modules.web.click.api.model.ClickApp;
import org.netbeans.modules.web.click.api.model.ClickComponent;
import org.netbeans.modules.web.click.api.model.ClickVisitor;
import org.netbeans.modules.web.click.api.model.Control;
import org.netbeans.modules.web.click.api.model.ControlSet;
import org.netbeans.modules.web.click.api.model.Controls;
import org.netbeans.modules.web.click.api.model.Excludes;
import org.netbeans.modules.web.click.api.model.FileUploadService;
import org.netbeans.modules.web.click.api.model.Format;
import org.netbeans.modules.web.click.api.model.Header;
import org.netbeans.modules.web.click.api.model.Headers;
import org.netbeans.modules.web.click.api.model.LogService;
import org.netbeans.modules.web.click.api.model.Mode;
import org.netbeans.modules.web.click.api.model.Page;
import org.netbeans.modules.web.click.api.model.Pages;
import org.netbeans.modules.web.click.api.model.Property;
import org.netbeans.modules.web.click.api.model.TemplateService;
import org.netbeans.modules.xml.xam.ComponentUpdater;
import org.netbeans.modules.xml.xam.ComponentUpdater.Operation;

/**
 *
 * @author hantsy
 */
class ClickComponentUpdateVisitor extends ClickVisitor.Default implements ComponentUpdater<ClickComponent>{

    ClickComponent target;
    int index;
    Operation operation;

    public ClickComponentUpdateVisitor() {
    }

    public void update(ClickComponent target, ClickComponent child, Operation operation) {
        update(target, child, -1, operation);
    }

    public void update(ClickComponent target, ClickComponent child, int index, Operation operation) {
        this.target=target;
        this.index=index;
        this.operation=operation;

        child.accept(this);
        
    }

     private void insert(String propertyName, ClickComponent component) {
        ((ClickComponentImpl)target).insertAtIndex(propertyName, component, index);
    }

    private void remove(String propertyName, ClickComponent component) {
        ((ClickComponentImpl)target).removeChild(propertyName, component);
    }

    @Override
    public void accept(Headers component) {
        if(target instanceof ClickApp){
            if(operation == Operation.ADD){
                insert(ClickApp.PROP_HEADERS, component);
            }else{
                remove(ClickApp.PROP_HEADERS, component);
            }
        }
    }

    @Override
    public void accept(Header component) {
        if(target instanceof Headers){
            if(operation == Operation.ADD){
                insert(Headers.PROP_HEADER, component);
            }else{
                remove(Headers.PROP_HEADER, component);
            }
        }else if(target instanceof Page){
            if(operation == Operation.ADD){
                insert(Page.PROP_HEADER, component);
            }else{
                remove(Page.PROP_HEADER, component);
            }
        }
    }

    @Override
    public void accept(Pages component) {
        if(target instanceof ClickApp){
            if(operation == Operation.ADD){
                insert(ClickApp.PROP_PAGES, component);
            }else{
                remove(ClickApp.PROP_PAGES, component);
            }
        }
    }

    @Override
    public void accept(Page component) {
        if(target instanceof Pages){
            if(operation == Operation.ADD){
                insert(Pages.PROP_PAGE, component);
            }else{
                remove(Pages.PROP_PAGE, component);
            }
        }
    }

    @Override
    public void accept(Controls component) {
        if(target instanceof ClickApp){
            if(operation == Operation.ADD){
                insert(ClickApp.PROP_CONTROLS, component);
            }else{
                remove(ClickApp.PROP_CONTROLS, component);
            }
        }
    }

    @Override
    public void accept(ControlSet component) {
        if(target instanceof Controls){
            if(operation == Operation.ADD){
                insert(Controls.PROP_CONTROL_SET, component);
            }else{
                remove(Controls.PROP_CONTROL_SET, component);
            }
        }
    }

    @Override
    public void accept(Control component) {
        if(target instanceof Controls){
            if(operation == Operation.ADD){
                insert(Controls.PROP_CONTROL, component);
            }else{
                remove(Controls.PROP_CONTROL, component);
            }
        }
    }

    @Override
    public void accept(Mode component) {
        if(target instanceof ClickApp){
            if(operation == Operation.ADD){
                insert(ClickApp.PROP_MODE, component);
            }else{
                remove(ClickApp.PROP_MODE, component);
            }
        }
    }

    @Override
    public void accept(Format component) {
        if(target instanceof ClickApp){
            if(operation == Operation.ADD){
                insert(ClickApp.PROP_FORMAT, component);
            }else{
                remove(ClickApp.PROP_FORMAT, component);
            }
        }
    }

    @Override
    public void accept(FileUploadService component) {
        if(target instanceof ClickApp){
            if(operation == Operation.ADD){
                insert(ClickApp.PROP_FILE_UPLOAD_SERVICE, component);
            }else{
                remove(ClickApp.PROP_FILE_UPLOAD_SERVICE, component);
            }
        }
    }

    @Override
    public void accept(LogService component) {
        if(target instanceof ClickApp){
            if(operation == Operation.ADD){
                insert(ClickApp.PROP_LOG_SERVICE, component);
            }else{
                remove(ClickApp.PROP_LOG_SERVICE, component);
            }
        }
    }

    @Override
    public void accept(TemplateService component) {
        if(target instanceof ClickApp){
            if(operation == Operation.ADD){
                insert(ClickApp.PROP_TEMPLATE_SERVICE, component);
            }else{
                remove(ClickApp.PROP_TEMPLATE_SERVICE, component);
            }
        }
    }

    @Override
    public void accept(Property component) {
        if(target instanceof TemplateService){
            if(operation == Operation.ADD){
                insert(TemplateService.PROP_PROPERTY, component);
            }else{
                remove(TemplateService.PROP_PROPERTY, component);
            }
        }else if(target instanceof LogService){
            if(operation == Operation.ADD){
                insert(LogService.PROP_PROPERTY, component);
            }else{
                remove(LogService.PROP_PROPERTY, component);
            }
        }
        else if(target instanceof FileUploadService){
            if(operation == Operation.ADD){
                insert(FileUploadService.PROP_PROPERTY, component);
            }else{
                remove(FileUploadService.PROP_PROPERTY, component);
            }
        }
    }

    @Override
    public void accept(Excludes component) {
        if(target instanceof Pages){
            if(operation == Operation.ADD){
                insert(Pages.PROP_EXCLUDES, component);
            }else{
                remove(Pages.PROP_EXCLUDES, component);
            }
        }
    }


}
