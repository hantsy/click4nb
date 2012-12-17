/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.web.click.api.model;

import org.w3c.dom.Element;

/**
 *
 * @author hantsy
 */
public interface MenuComponentFactory {
    MenuComponent create( Element element, MenuComponent context);
    MenuComponent createMenu(MenuComponent component);
}
