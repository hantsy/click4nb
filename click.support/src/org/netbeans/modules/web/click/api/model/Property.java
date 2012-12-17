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
public interface Property extends ClickComponent, Nameable<ClickComponent> {
    public static final String PROP_VALUE="value";

    String getValue();
    void setValue(String value);
}
