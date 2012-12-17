/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.web.click.api.model;

import java.util.List;
import org.netbeans.modules.xml.xam.dom.DocumentComponent;

/**
 *
 * @author hantsy
 */
public interface MenuComponent extends DocumentComponent<MenuComponent>{

    //Elements
    public static final String PROP_MENU="menu";
    
    MenuModel getModel();
    void accept(MenuVisitor visitor);

     //Attribues
    public static final String PROP_LABEL="label";
    public static final String PROP_PATH="path";
    public static final String PROP_TITLE="title";
    public static final String PROP_TARGET="target";
    public static final String PROP_IMAGE_SRC="imageSrc";
    public static final String PROP_EXTERNAL="external";
    public static final String PROP_SPARATOR="separator";
    public static final String PROP_ROLES="roles";
    public static final String PROP_PAGES="pages";
    public static final String PROP_ID="id";
    public static final String PROP_NAME="name";

    String getId();
    void setId(String id);

    String getName();
    void setName(String name);
    
    String getLabel();
    void setLabel(String value);

    String getPath();
    void setPath(String path);

    String getTitle();
    void setTitle(String title);

    String getTarget();
    void setTartget(String value);

    String getImageSrc();
    void setIamgeSrc(String src);

    Boolean isExternal();
    void setExternal(Boolean value);

    Boolean isSeparator();
    void setSeparator(Boolean value);

    String getPages();
    void setPages(String pages);

    List<MenuComponent> getSubMenus();
    void addSubMenu(MenuComponent menu);
    void removeSubMenu(MenuComponent menu);
}
