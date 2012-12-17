/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.web.click.api.model;

import org.netbeans.modules.xml.xam.Nameable;

/**
 *
 * @author hantsy
 */
public interface  Header extends ClickComponent , Nameable<ClickComponent> {
    //public static final String PROP_NAME="name";
    public static final String PROP_VALUE="value";
    public static final String PROP_TYPE="type";

    String getValue();
    void setValue(String value);

    String getType();
    void setType(String type);
}
