/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click;

import java.io.IOException;
import org.netbeans.api.xml.cookies.CheckXMLCookie;
import org.netbeans.api.xml.cookies.ValidateXMLCookie;
import org.netbeans.modules.xml.api.XmlFileEncodingQueryImpl;
import org.netbeans.spi.queries.FileEncodingQueryImplementation;
import org.netbeans.spi.xml.cookies.CheckXMLSupport;
import org.netbeans.spi.xml.cookies.DataObjectAdapters;
import org.netbeans.spi.xml.cookies.ValidateXMLSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.text.DataEditorSupport;
import org.openide.util.NbBundle;
import org.xml.sax.InputSource;

@NbBundle.Messages({
    "ClickDataObject=ClickDataObject"
        ,"CLICK_RESOVLVER_NAME=Click Config File"
})
@DataObject.Registration(
        displayName = "#ClickDataObject", 
        iconBase = "org/netbeans/modules/web/click/resources/click-icon.png", 
        mimeType = "text/x-click-app+xml")
@MIMEResolver.Registration(
        displayName = "#CLICK_RESOVLVER_NAME",
        resource = "ClickResolver.xml")
public class ClickDataObject extends MultiDataObject {

    public ClickDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        CookieSet cookies = getCookieSet();
        cookies.add((Node.Cookie) DataEditorSupport.create(this, getPrimaryEntry(), cookies));
        // Creates Check XML and Validate XML context actions
        InputSource in = DataObjectAdapters.inputSource(this);
        CheckXMLCookie checkCookie = new CheckXMLSupport(in);
        getCookieSet().add(checkCookie);
        ValidateXMLCookie validateCookie = new ValidateXMLSupport(in);
        getCookieSet().add(validateCookie);
        getCookieSet().assign(FileEncodingQueryImplementation.class, XmlFileEncodingQueryImpl.singleton());
    }

    @Override
    protected Node createNodeDelegate() {
        return new DataNode(this, Children.LEAF, getLookup());
    }

    @Override
    public Lookup getLookup() {
        return getCookieSet().getLookup();
    }

//    static ClickCatalog clickCatalog=new ClickCatalog();
//
//     private org.w3c.dom.Document getDomDocument(InputStream inputSource) throws SAXParseException {
//        try {
//            // creating w3c document
//            org.w3c.dom.Document doc = org.netbeans.modules.schema2beans.GraphManager.
//                createXmlDocument(new org.xml.sax.InputSource(inputSource), false, clickCatalog,
//               null);
//            return doc;
//        } catch(Exception e) {
//            //    XXX Change that
//            throw new SAXParseException(e.getMessage(), new org.xml.sax.helpers.LocatorImpl());
//        }
//    }
}
