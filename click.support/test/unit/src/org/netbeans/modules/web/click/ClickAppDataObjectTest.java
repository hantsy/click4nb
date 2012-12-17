/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click;

import junit.framework.TestCase;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataObject;

public class ClickAppDataObjectTest extends TestCase {

    public ClickAppDataObjectTest(String testName) {
        super(testName);
    }

    public void testDataObject() throws Exception {
        FileObject root = FileUtil.getConfigRoot();
        FileObject template = root.getFileObject("Templates/Other/ClickAppTemplate.xml");
        assertNotNull("Template file shall be found", template);

        DataObject obj = DataObject.find(template);
        assertEquals("It is our data object", ClickDataObject.class, obj.getClass());
    }
}
