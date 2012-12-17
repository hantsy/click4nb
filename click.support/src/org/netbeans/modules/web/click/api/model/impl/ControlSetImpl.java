/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click.api.model.impl;

import org.netbeans.modules.web.click.ClickConfigUtilities;
import org.netbeans.modules.web.click.api.model.ClickApp;
import org.netbeans.modules.web.click.api.model.ClickComponent;
import org.netbeans.modules.web.click.api.model.ClickModel;
import org.netbeans.modules.web.click.api.model.ClickVisitor;
import org.netbeans.modules.web.click.api.model.ControlSet;
import org.netbeans.modules.xml.xam.AbstractReference;
import org.netbeans.modules.xml.xam.Reference;
import org.openide.filesystems.FileObject;
import org.w3c.dom.Element;

/**
 *
 * @author hantsy
 */
public class ControlSetImpl extends ClickComponentImpl.Named implements ControlSet {

    public ControlSetImpl(ClickModelImpl model, Element element) {
        super(model, element);
    }

    public ControlSetImpl(ClickModelImpl model) {
        this(model, createElementNS(model, ClickQNames.CONTROL_SET));
    }

    public void accept(ClickVisitor visitor) {
        visitor.accept(this);
    }

    public Reference<ClickApp> getReference() {
        return new ModelReference();
    }

    public final class ModelReference extends AbstractReference<ClickApp> {

        public ModelReference() {
            super(ClickApp.class, ControlSetImpl.this, ControlSetImpl.this.getName());
        }

         public ClickApp get() {
            if (getReferenced() == null) {
                FileObject targetFO = getAbsoluteLocation(refString);
                if(targetFO != null) {
                    ClickModel model = ClickConfigUtilities.getClickModel(targetFO, false);
                    if(model != null) {
                        ClickApp root = model.getRootComponent();
                        setReferenced(root);
                    }
                }
            }

            return getReferenced();
        }

        protected ClickComponent getReferencingComponent() {
            return (ClickComponent) super.getParent();
        }

        private FileObject getAbsoluteLocation(String relLocation) {
            FileObject baseFO =
                    ControlSetImpl.this.getModel().getModelSource().getLookup().lookup(FileObject.class);
            if(baseFO == null) {
                return null;
            }

            FileObject targetFO = baseFO.getParent().getFileObject(relLocation);

            return targetFO;
        }
    }
}
