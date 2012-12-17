/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.web.click.api.model.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.netbeans.modules.web.click.api.model.ClickComponent;
import org.netbeans.modules.web.click.api.model.ClickModel;
import org.netbeans.modules.xml.xam.Nameable;
import org.netbeans.modules.xml.xam.dom.AbstractDocumentComponent;
import org.netbeans.modules.xml.xam.dom.Attribute;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author hantsy
 */
public abstract class ClickComponentImpl extends AbstractDocumentComponent<ClickComponent> implements ClickComponent{

    public ClickComponentImpl(ClickModelImpl model, Element element) {
        super(model, element);
    }

    public ClickModelImpl getModel(){
        return (ClickModelImpl)super.getModel();
    }

    static public Element createElementNS(ClickModel model, ClickQNames sq) {
        QName q = sq.getQName();
        return model.getDocument().createElementNS(q.getNamespaceURI(), sq.getQualifiedName());
    }

    protected Object getAttributeValueOf(Attribute attr, String stringValue) {
        return stringValue;
    }

    protected void populateChildren(List<ClickComponent> children) {
        NodeList nl = getPeer().getChildNodes();
        if(nl != null) {
            for(int i = 0; i < nl.getLength(); i++) {
                Node n = nl.item(i);
                if(n instanceof Element) {
                    ClickModel model = getModel();
                    ClickComponent comp = model.getFactory().create((Element)n, this);
                    if(comp != null) {
                        children.add(comp);
                    }
                }
            }
        }
    }


    public static abstract class Named extends ClickComponentImpl
            implements Nameable<ClickComponent> {
        public Named(ClickModelImpl model, Element element) {
            super(model, element);
        }

        public String getName() {
            return super.getAttribute(ClickAttributes.NAME);
        }

        public void setName(String name) {
            super.setAttribute(Nameable.NAME_PROPERTY, ClickAttributes.NAME, name);
        }
    }



}
