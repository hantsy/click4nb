/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click.api.model;

/**
 *
 * @author hantsy
 */
public interface Control extends ClickComponent {

    public static final String PROP_CLASSNAME = "classname";

    String getClassName();
    void setClassName(String clz);
}
