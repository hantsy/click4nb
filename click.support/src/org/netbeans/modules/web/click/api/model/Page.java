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
public interface  Page  extends ClassNameComponent{
    //Attributes
    public static final String PROP_PATH="path";
  
    //Elements
    public static final String PROP_HEADER="header";

    String getPath();
    void setPath(String path);

    List<Header> getHeaderList();
    void addHeader(Header header);
    void removeHeader(Header header);
}
