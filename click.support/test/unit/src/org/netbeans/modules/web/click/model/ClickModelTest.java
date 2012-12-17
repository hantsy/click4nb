/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click.model;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.netbeans.modules.web.click.api.model.ClickModel;
import org.netbeans.modules.web.click.api.model.ClickModelFactory;
import org.netbeans.modules.xml.retriever.catalog.Utilities;
import org.netbeans.modules.xml.xam.locator.CatalogModelException;
import org.openide.filesystems.FileAlreadyLockedException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import static org.junit.Assert.*;

/**
 *
 * @author hantsy
 */
public class ClickModelTest {

    public ClickModelTest() {
    }
    private static final java.util.logging.Logger LOGGER;

    static {
        LOGGER = java.util.logging.Logger.getLogger("org.netbeans.modules.web.click.model.ClickModelTest");
        org.netbeans.modules.web.click.model.ClickModelTest.initLoggerHandlers();
    }

    private static final void initLoggerHandlers() {
        java.util.logging.Handler[] handlers = LOGGER.getHandlers();
        boolean hasConsoleHandler = false;
        for (java.util.logging.Handler handler : handlers) {
            if (handler instanceof java.util.logging.ConsoleHandler) {
                hasConsoleHandler = true;
            }
        }
        if (!hasConsoleHandler) {
            LOGGER.addHandler(new java.util.logging.ConsoleHandler());
        }
        LOGGER.setLevel(java.util.logging.Level.FINEST);
    }
    FileSystem fs;
    FileObject fsRoot;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        fs = FileUtil.createMemoryFileSystem();
        fsRoot = fs.getRoot();
    }

    @After
    public void tearDown() {
    }

    public FileObject createEmptyClickFO() {
        LOGGER.log(Level.ALL, "enter  @createModel");

        try {
            FileObject clickFO = fsRoot.createData("click", "xml");
            return clickFO;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    private void writeFile(String content, FileObject file) {
        OutputStream os = null;
        try {
            os = file.getOutputStream();
            os.write(content.getBytes("UTF-8"));
            os.close();
        } catch (FileAlreadyLockedException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                os.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    public ClickModel createEmptyClickModel() {
        FileObject fo = createEmptyClickFO();
        writeFile("<click-app></click-app>", fo);
        return ClickModelFactory.getInstance().createFreshModel(Utilities.getModelSource(fo, true));
    }

    @Test
    public void testEmptyModel() {
        ClickModel model = createEmptyClickModel();
        assertNotNull("Test Click empty Moel", model);
    }
}
