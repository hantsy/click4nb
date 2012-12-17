/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.web.click.api.model.impl;

import java.util.List;
import org.netbeans.modules.web.click.api.model.ClickVisitor;
import org.netbeans.modules.web.click.api.model.Header;
import org.netbeans.modules.web.click.api.model.Headers;
import org.w3c.dom.Element;

/**
 *
 * @author hantsy
 */
public class HeadersImpl extends ClickComponentImpl implements Headers {

    public HeadersImpl(ClickModelImpl model, Element element) {
       super(model, element);
    }

    public HeadersImpl(ClickModelImpl model) {
       super(model, createElementNS(model, ClickQNames.HEADERS));
    }

    public void accept(ClickVisitor visitor) {
        visitor.accept(this);
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
