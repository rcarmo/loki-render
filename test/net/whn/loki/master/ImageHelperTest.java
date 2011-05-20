/**
 *This program is free software: you can redistribute it and/or modify
 *it under the terms of the GNU General Public License as published by
 *the Free Software Foundation, either version 3 of the License, or
 *(at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.whn.loki.master;

import java.io.File;
import net.whn.loki.master.ImageHelper;
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
public class ImageHelperTest {

    public ImageHelperTest() {
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
     * Test of compositeTiles method, of class ImageHelper.
     */
    @Test
    public void testCombineTiles() {
        System.out.println("combineTiles");
        File inputDir = new File("/home/daniel/.loki/tmp");
        File outputFile = new File("/home/daniel/.loki/tmp/composite.png");
        if(!ImageHelper.compositeTiles(inputDir, 9, outputFile)) {
            fail();
        }
    }

}