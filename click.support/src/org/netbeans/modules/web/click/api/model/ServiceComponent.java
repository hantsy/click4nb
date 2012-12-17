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
public interface  ServiceComponent extends ClassNameComponent{
    //Attributs
    //Elements
    public static final String PROP_PROPERTY="property";
    
    List<Property> getPropertyList();
    void addProperty(Property property);
    void removeProperty(Property pro);
}
