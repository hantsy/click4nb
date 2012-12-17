/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.web.click.api.model;

/**
 *
 * @author hantsy
 */
public interface PageInterceptor extends ServiceComponent{
    //attributes
    public static final String  PROP_SCOPE="scope";

    String getScope();
    void setScope(String scope);
}
