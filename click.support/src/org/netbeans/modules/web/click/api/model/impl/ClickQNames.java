/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click.api.model.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import org.netbeans.modules.web.click.api.model.ClickApp;
import org.netbeans.modules.web.click.api.model.Controls;
import org.netbeans.modules.web.click.api.model.Headers;
import org.netbeans.modules.web.click.api.model.PageInterceptor;
import org.netbeans.modules.web.click.api.model.Pages;
import org.netbeans.modules.web.click.api.model.ServiceComponent;

/**
 *
 * @author hantsy
 */
public enum ClickQNames {

    CLICK_APP("click-app"),
    HEADERS(ClickApp.PROP_HEADERS),
    PAGES(ClickApp.PROP_PAGES),
    MODE(ClickApp.PROP_MODE),
    FORMAT(ClickApp.PROP_FORMAT),
    CONTROLS(ClickApp.PROP_CONTROLS),
    FILE_UPLOAD_SERVICE(ClickApp.PROP_FILE_UPLOAD_SERVICE),
    LOG_SERVICE(ClickApp.PROP_LOG_SERVICE),
    TEMPLATE_SERVICE(ClickApp.PROP_TEMPLATE_SERVICE),
    PAGE_INTERCEPTOR(ClickApp.PROP_PAGE_INTERCEPTOR),
    HEADER(Headers.PROP_HEADER),
    PAGE(Pages.PROP_PAGE),
    EXCLUDES(Pages.PROP_EXCLUDES),
    CONTROL(Controls.PROP_CONTROL),
    CONTROL_SET(Controls.PROP_CONTROL_SET),
    PROPERTY(ServiceComponent.PROP_PROPERTY);

    private QName qname;
    private static Set<QName> mappedQNames = new HashSet<QName>();
    static{
        mappedQNames.add(CLICK_APP.getQName());
        mappedQNames.add(HEADERS.getQName());
        mappedQNames.add(HEADER.getQName());
        mappedQNames.add(PAGES.getQName());
        mappedQNames.add(PAGE.getQName());
        mappedQNames.add(EXCLUDES.getQName());
        mappedQNames.add(MODE.getQName());
        mappedQNames.add(FORMAT.getQName());
        mappedQNames.add(CONTROL.getQName());
        mappedQNames.add(CONTROLS.getQName());
        mappedQNames.add(CONTROL_SET.getQName());
        mappedQNames.add(LOG_SERVICE.getQName());
        mappedQNames.add(FILE_UPLOAD_SERVICE.getQName());
        mappedQNames.add(TEMPLATE_SERVICE.getQName());
        mappedQNames.add(PROPERTY.getQName());
        mappedQNames.add(PAGE_INTERCEPTOR.getQName());

    }

    ClickQNames(String localName) {
        this(XMLConstants.NULL_NS_URI, localName, XMLConstants.DEFAULT_NS_PREFIX);
    }

    ClickQNames(String namespace, String localName, String prefix) {
        if (prefix == null) {
            qname = new QName(namespace, localName);
        } else {
            qname = new QName(namespace, localName, prefix);
        }
    }

    public QName getQName() {
        return qname;
    }

    public String getLocalName() {
        return qname.getLocalPart();
    }

    public String getQualifiedName() {
        return qname.getPrefix() + ":" + qname.getLocalPart();
    }

    public static Set<QName> getMappedQNames() {
        return Collections.unmodifiableSet(mappedQNames);
    }
}
