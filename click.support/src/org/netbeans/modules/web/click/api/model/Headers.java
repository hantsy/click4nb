/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.web.click.api.model;

import java.util.List;

/**
 *
 * @author hantsy
 */
public interface Headers extends ClickComponent{

    //Elements
    public static final String PROP_HEADER="header";

    List<Header> getHeaderList();
    void addHeader(Header header);
    void removeHeader(Header header);

}
