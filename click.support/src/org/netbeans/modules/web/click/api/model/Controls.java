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
public interface  Controls extends ClickComponent {

    //Elements
    public static final String PROP_CONTROL="control";
    public static final String PROP_CONTROL_SET="control-set";

    List<Control> getControlList();
    void addControl(Control control);
    void removeControl(Control control);

    List<ControlSet> getControlSetList();
    void addControlSet(ControlSet controlSet);
    void removeControlSet(ControlSet controlSet);
}
