/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click.api.model;

import org.netbeans.modules.xml.xam.Named;
import org.netbeans.modules.xml.xam.Reference;

/**
 *
 * @author hantsy
 */
public interface ControlSet extends ClickComponent {

    Reference<ClickApp> getReference();
}
