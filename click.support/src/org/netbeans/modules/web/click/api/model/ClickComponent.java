/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.web.click.api.model;

import org.netbeans.modules.xml.xam.dom.DocumentComponent;

/**
 *
 * @author hantsy
 */
public interface ClickComponent extends DocumentComponent<ClickComponent>{

    ClickModel getModel();
    void accept(ClickVisitor visitor);
}
