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

/**
 *
 * @author hantsy
 */
public enum MenuQNames {
    MENU("menu");

    private QName qname;
    private static Set<QName> mappedQNames = new HashSet<QName>();
    static{
        mappedQNames.add(MENU.getQName());
    }

     MenuQNames(String localName) {
        this(XMLConstants.NULL_NS_URI, localName, XMLConstants.DEFAULT_NS_PREFIX);
    }

    MenuQNames(String namespace, String localName, String prefix) {
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
