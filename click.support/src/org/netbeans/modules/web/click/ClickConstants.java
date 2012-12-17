/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click;

/**
 *
 * @author hantsy
 */
public class ClickConstants {

    public static final String DEFAULT_CLICK_APP_CONFIG_FILE = "click.xml";
    public static final String DEFAULT_MENU_CONFIG_FILE = "menu.xml";

    public static final String LIBRARY_CLICK = "click-framework";
    public static final String LIBRARY_CLICK_MOCK = "click-mock";

    public static final String SPRING_LIBRARY_NAME = "spring-framework";
    public static final String SPRING_CONFIG_LOCATION_PARAM = "contextConfigLocation";
    public static final String SPRING_CONFIG_LOCATION_VALUE = "/WEB-INF/applicationContext.xml";
    public static final String SPRING_CONTEXT_LOADER_CLASS = "org.springframework.web.context.ContextLoaderListener";
    public static final String DEFAULT_SPRING_CONFIG_FILE = "applicationContext.xml";

    public static final String CLICK_SERVLET_NAME = "ClickServlet";
    public static final String CLICK_SERVELT_CLASS = "org.apache.click.ClickServlet";

    public static final String SPRING_CLICK_SERVELT_CLASS = "org.apache.click.extras.spring.SpringClickServlet";
    public static final String DEFAULT_PACKAGE_NAME = "example.page";
    public static final String BASE_TEMPLATES_DIR = "ClickFramework/Templates/";
}
