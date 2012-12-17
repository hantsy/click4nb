/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click.api.model.impl;

import org.netbeans.modules.web.click.api.model.ClickVisitor;
import org.netbeans.modules.web.click.api.model.FileUploadService;
import org.netbeans.modules.web.click.api.model.TemplateService;
import org.w3c.dom.Element;

/**
 *
 * @author hantsy
 */
public class TemplateServiceImpl extends AbstractServiceComponentImpl implements TemplateService {

    public TemplateServiceImpl(ClickModelImpl model, Element element) {
        super(model, element);
    }

    public TemplateServiceImpl(ClickModelImpl model) {
        super(model, createElementNS(model, ClickQNames.TEMPLATE_SERVICE));
    }

    public void accept(ClickVisitor visitor) {
        visitor.accept(this);
    }
}
