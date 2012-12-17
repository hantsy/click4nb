/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.web.click.api.model;

/**
 *
 * @author hantsy
 */
public interface Mode extends ClickComponent{
    public static final String PROP_VALUE="value";

    String getValue();
    void setValue(String value);

}
