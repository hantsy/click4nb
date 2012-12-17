/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.web.click.api.model;

import org.w3c.dom.Element;

/**
 *
 * @author hantsy
 */
public interface  ClickComponentFactory{

    ClickComponent create(Element element, ClickComponent com);

    Headers createHeaders();
    Header createHeader();
    Pages createPages();
    Page createPage();
    Controls createControls();
    Control createControl();
    ControlSet createControlSet();
    Format createFormat();
    Mode createMode();
    FileUploadService createFileUploadService();
    LogService createLogService();
    TemplateService createTemplateService();

}
