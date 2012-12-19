package org.netbeans.modules.web.click.grammar;

import java.beans.FeatureDescriptor;
import java.util.Enumeration;
import org.netbeans.modules.web.click.ClickCatalog;
import org.netbeans.modules.xml.api.model.DTDUtil;
import org.netbeans.modules.xml.api.model.GrammarEnvironment;
import org.netbeans.modules.xml.api.model.GrammarQuery;
import org.netbeans.modules.xml.api.model.GrammarQueryManager;
import org.openide.util.Enumerations;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 *
 * @author hantsy
 */
public class MenuGrammarQueryProvider extends GrammarQueryManager {

    private final GrammarQuery grammar;

    public MenuGrammarQueryProvider() {
        grammar = DTDUtil.parseDTD(true,
                new InputSource(ClickCatalog.MENU_2_2_DTD_LOCAL_URI));
    }

    @Override
    public Enumeration enabled(GrammarEnvironment ctx) {
        Enumeration en = ctx.getDocumentChildren();
        while (en.hasMoreElements()) {
            Node next = (Node) en.nextElement();
            if (next.getNodeType() == Node.ELEMENT_NODE) {
                Element root = (Element) next;
                if ("menu".equals(root.getNodeName())) { // NOI18N
                    return Enumerations.singleton(next);
                }
            }
        }
        return null;
    }

    @Override
    public FeatureDescriptor getDescriptor() {
        return new FeatureDescriptor();
    }

    @Override
    public GrammarQuery getGrammar(GrammarEnvironment env) {
        return grammar;
    }
}
