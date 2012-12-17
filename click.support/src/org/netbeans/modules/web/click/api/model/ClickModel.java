/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.web.click.api.model;

import org.netbeans.modules.xml.xam.dom.DocumentModel;

/**
 *
 * @author hantsy
 */
public interface ClickModel extends DocumentModel<ClickComponent> {
    ClickApp getRootComponent();
    ClickComponentFactory getFactory();
}
