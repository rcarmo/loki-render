/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.whn.loki.IO;

import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author daniel
 */
public class IOHelperTest {

    public IOHelperTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of zipDirectory method, of class IOHelper.
     */
    @Test
    public void testZipDirectory() {
        File blendFile =
                new File ("/home/daniel/daniel/loki/blendFiles/XYZ123.blend");
        File lokiCfgDir = new File("/home/daniel/.loki");

        MasterIOHelper.addBlendCacheToLokiCache(lokiCfgDir, blendFile);
    }

    /**
     * Test of unzipDirectory method, of class IOHelper.
     */
    @Test
    public void testUnzipDirectory() {
        File zipFile =
                new File ("/home/daniel/.loki/tmp/blendcache.zip");

        File tmpDir = new File("/home/daniel/.loki/tmp/whatup?");

        assertTrue(IOHelper.unzipDirectory(zipFile, tmpDir));
    }
}