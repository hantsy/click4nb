/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click.api.model.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.netbeans.modules.web.click.api.model.MenuComponent;
import org.netbeans.modules.web.click.api.model.MenuModel;
import org.netbeans.modules.web.click.api.model.MenuVisitor;
import org.netbeans.modules.xml.xam.dom.AbstractDocumentComponent;
import org.netbeans.modules.xml.xam.dom.Attribute;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author hantsy
 */
public class MenuComponentImpl extends AbstractDocumentComponent<MenuComponent> implements MenuComponent {

    public MenuComponentImpl(MenuModelImpl model, Element element) {
        super(model, element);
    }

    public MenuComponentImpl(MenuModelImpl model) {
        super(model, createElementNS(model, MenuQNames.MENU));
    }

    static public Element createElementNS(MenuModel model, MenuQNames sq) {
        QName q = sq.getQName();
        return model.getDocument().createElementNS(q.getNamespaceURI(), sq.getQualifiedName());
    }

    public String getLabel() {
        return super.getAttribute(MenuAttributes.LABEL);
    }

    public void setLabel(String value) {
        super.setAttribute(PROP_LABEL, MenuAttributes.LABEL, value);
    }

    public String getPath() {
        return super.getAttribute(MenuAttributes.PATH);
    }

    public void setPath(String path) {
        super.setAttribute(PROP_PATH, MenuAttributes.PATH, path);
    }

    public String getTitle() {
        return super.getAttribute(MenuAttributes.TITLE);
    }

    public void setTitle(String title) {
        super.setAttribute(PROP_TITLE, MenuAttributes.TITLE, title);
    }

    public String getTarget() {
        return super.getAttribute(MenuAttributes.TARGET);
    }

    public void setTartget(String value) {
        super.setAttribute(PROP_TARGET, MenuAttributes.TARGET, value);
    }

    public String getImageSrc() {
        return super.getAttribute(MenuAttributes.IMAGE_SRC);
    }

    public void setIamgeSrc(String value) {
        super.setAttribute(PROP_IMAGE_SRC, MenuAttributes.IMAGE_SRC, value);
    }

    public Boolean isExternal() {
        if (super.getAttribute(MenuAttributes.EXTERNAL) == null) {
            return Boolean.FALSE;
        }
        return Boolean.parseBoolean(super.getAttribute(MenuAttributes.EXTERNAL));
    }

    public void setExternal(Boolean value) {
        super.setAttribute(PROP_EXTERNAL, MenuAttributes.EXTERNAL, value);
    }

    public Boolean isSeparator() {
        if (super.getAttribute(MenuAttributes.SPARATOR) == null) {
            return Boolean.FALSE;
        }
        return Boolean.parseBoolean(super.getAttribute(MenuAttributes.SPARATOR));
    }

    public void setSeparator(Boolean value) {
        super.setAttribute(PROP_SPARATOR, MenuAttributes.SPARATOR, value);
    }

    public String getPages() {
        return super.getAttribute(MenuAttributes.PAGES);
    }

    public void setPages(String value) {
        super.setAttribute(PROP_PAGES, MenuAttributes.PAGES, value);
    }

    public List<MenuComponent> getSubMenus() {
        return super.getChildren(MenuComponent.class);
    }

    public void addSubMenu(MenuComponent menu) {
        super.appendChild(PROP_MENU, menu);
    }

    public void removeSubMenu(MenuComponent menu) {
        super.removeChild(PROP_MENU, menu);
    }

    @Override
    protected void populateChildren(List<MenuComponent> children) {
        NodeList nl = getPeer().getChildNodes();
        if (nl != null) {
            for (int i = 0; i < nl.getLength(); i++) {
                Node n = nl.item(i);
                if (n instanceof Element) {
                    MenuModel model = getModel();
                    MenuComponent comp = model.getFactory().create((Element) n, this);
                    if (comp != null) {
                        children.add(comp);
                    }
                }
            }
        }
    }

    @Override
    protected Object getAttributeValueOf(Attribute arg0, String arg1) {
        return arg1;
    }

    public MenuModelImpl getModel() {
        return (MenuModelImpl) super.getModel();
    }

    public void accept(MenuVisitor visitor) {
        visitor.accept(this);
    }

    public String getId() {
        return super.getAttribute(MenuAttributes.ID);
    }

    public void setId(String id) {
        super.setAttribute(PROP_ID, MenuAttributes.ID, id);
    }

    public String getName() {
        return super.getAttribute(MenuAttributes.NAME);
    }

    public void setName(String name) {
        super.setAttribute(PROP_NAME, MenuAttributes.NAME, name);
    }
}
