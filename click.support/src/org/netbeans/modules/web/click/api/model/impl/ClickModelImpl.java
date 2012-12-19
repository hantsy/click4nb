/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click.api.model.impl;

import java.util.Set;
import javax.xml.namespace.QName;
import org.netbeans.modules.web.click.api.model.ClickApp;
import org.netbeans.modules.web.click.api.model.ClickComponent;
import org.netbeans.modules.web.click.api.model.ClickComponentFactory;
import org.netbeans.modules.web.click.api.model.ClickModel;
import org.netbeans.modules.xml.xam.ComponentUpdater;
import org.netbeans.modules.xml.xam.ModelSource;
import org.netbeans.modules.xml.xam.dom.AbstractDocumentModel;
import org.w3c.dom.Element;

/**
 *
 * @author hantsy
 */
public class ClickModelImpl extends AbstractDocumentModel<ClickComponent> implements ClickModel {

    private ClickComponent click;
    private ClickComponentFactory factory;

    public ClickModelImpl(ModelSource arg0) {
        super(arg0);
        factory = new ClickComponentFactoryImpl(this);
    }

    @Override
    public ClickComponent createRootComponent(Element root) {
        ClickComponent newClick = getFactory().create(root, null);
        if (newClick != null) {
            click = newClick;
        }
        return click;
    }

    @Override
    protected ComponentUpdater<ClickComponent> getComponentUpdater() {
        return new ClickComponentUpdateVisitor();
    }

    @Override
    public ClickComponent createComponent(ClickComponent parent, Element element) {
        return getFactory().create(element, parent);
    }

    @Override
    public ClickApp getRootComponent() {
        return (ClickApp) click;
    }

    @Override
    public ClickComponentFactory getFactory() {
        return factory;
    }

    @Override
    public Set<QName> getQNames() {
        return ClickQNames.getMappedQNames();
    }

}
