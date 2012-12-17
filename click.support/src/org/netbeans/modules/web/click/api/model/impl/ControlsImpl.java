/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.web.click.api.model.impl;

import java.util.List;
import org.netbeans.modules.web.click.api.model.ClickVisitor;
import org.netbeans.modules.web.click.api.model.Control;
import org.netbeans.modules.web.click.api.model.ControlSet;
import org.netbeans.modules.web.click.api.model.Controls;
import org.w3c.dom.Element;

/**
 *
 * @author hantsy
 */
public class ControlsImpl extends ClickComponentImpl implements Controls {

    public ControlsImpl(ClickModelImpl model, Element element) {
        super(model, element);
    }

    public ControlsImpl(ClickModelImpl model) {
        super(model, createElementNS(model, ClickQNames.CONTROLS));
    }
    
    public void accept(ClickVisitor visitor) {
        visitor.accept(this);
    }

    public List<Control> getControlList() {
        return super.getChildren(Control.class);
    }

    public void addControl(Control control) {
        super.appendChild(PROP_CONTROL, control);
    }

    public void removeControl(Control control) {
        super.removeChild(PROP_CONTROL, control);
    }

    public List<ControlSet> getControlSetList() {
        return super.getChildren(ControlSet.class);
    }

    public void addControlSet(ControlSet controlSet) {
        super.appendChild(PROP_CONTROL_SET, controlSet);
    }

    public void removeControlSet(ControlSet controlSet) {
        super.removeChild(PROP_CONTROL_SET, controlSet);
    }

}
