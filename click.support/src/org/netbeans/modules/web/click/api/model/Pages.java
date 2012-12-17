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
public interface  Pages extends ClickComponent {
    //Attributes
    public static final String PROP_AUTOBINDING="autobinding";
    public static final String PROP_AUTOMAPPING="automapping";
    public static final String PROP_PACKAGE="package";

    //Elements
    public static final String PROP_PAGE = "page";
    public static final String PROP_EXCLUDES = "excludes";
    
    String getAutoBinding();
    void setAutoBinding(String value);

    String getAutoMapping();
    void setAutoMapping(String value);

    String getPackage();
    void setPackage(String pkg);

    List<Page> getPageList();
    void addPage(Page page);
    void removePage(Page page);

    List<Excludes> getExcludesList();
    void addExcludes(Excludes excludes);
    void removeExcludes(Excludes excludes);
}
