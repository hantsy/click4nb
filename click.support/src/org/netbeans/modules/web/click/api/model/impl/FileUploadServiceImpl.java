/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click.api.model.impl;

import org.netbeans.modules.web.click.api.model.ClickVisitor;
import org.netbeans.modules.web.click.api.model.FileUploadService;
import org.w3c.dom.Element;

/**
 *
 * @author hantsy
 */
public class FileUploadServiceImpl extends AbstractServiceComponentImpl implements FileUploadService {

    public FileUploadServiceImpl(ClickModelImpl model, Element element) {
        super(model, element);
    }

    public FileUploadServiceImpl(ClickModelImpl model) {
        super(model, createElementNS(model, ClickQNames.FILE_UPLOAD_SERVICE));
    }

    public void accept(ClickVisitor visitor) {
        visitor.accept(this);
    }
}
