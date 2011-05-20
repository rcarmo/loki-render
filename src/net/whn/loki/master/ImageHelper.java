/**
 *Project: Loki Render - A distributed job queue manager.
 *Version 0.6.2
 *Copyright (C) 2009 Daniel Petersen
 *Created on Oct 20, 2009
 */
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

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author daniel
 */
public class ImageHelper {

    public static boolean compositeTiles(File tileDir, int tileCount,
            File outputFile) {

        long start = System.currentTimeMillis();

        File[] inputFiles = new File[tileCount];
        for (int i = 0; i < tileCount; i++) {
            inputFiles[i] = new File(tileDir, Integer.toString(i) + ".png");
            if (!inputFiles[i].isFile()) {
                log.warning("expected tile file doesn't exist: " +
                        inputFiles[i].getAbsolutePath());
                return false;
            }
        }

        try {
            //grab tile 0
            BufferedImage image = ImageIO.read(inputFiles[0]);

            for (int t = 1; t < tileCount; t++) {
                BufferedImage nextImage = ImageIO.read(inputFiles[t]);
                image = combineImages(image, nextImage);
            }
            ImageIO.write(image, "png", outputFile);

        } catch (IOException ex) {
            log.warning("failed during tile compositing: "  + ex.getMessage());
            return false;
        }
        cleanup(tileDir, inputFiles, tileCount);

        log.fine("composited " + Integer.toString(tileCount) +
                " tiles in (ms): " +
                Long.toString(System.currentTimeMillis() - start));

        return true;
    }

    public static void deleteTileTmpFiles(File lcd, long jID) {
        File tmpDir = new File (lcd, "tmp");
        String prefix = Long.toString(jID) + "-";
        File[] tmpFiles = tmpDir.listFiles();
        for(File tmpTileDir: tmpFiles) {
            if(tmpTileDir.isDirectory() && tmpTileDir.getName().startsWith(prefix)) {
                File[] tileFiles = tmpTileDir.listFiles();
                for(File tilefile: tileFiles) {
                    if(!tilefile.delete()) {
                        log.warning("unable to delete tile file: " +
                                tilefile.getAbsolutePath());
                    }
                }
                if(!tmpTileDir.delete()) {
                    log.warning("unable to delete tile dir: " +
                                tmpTileDir.getAbsolutePath());
                }
            }
        }
    }

    private static void cleanup(File tileDir, File[] inputFiles, int tileCount) {
        for(File f: inputFiles) {
            if(!f.delete()) {
                log.warning("unable to delete tmp tile file: " +
                        f.getAbsolutePath());
            }
        }
        if(!tileDir.delete()) {
            log.warning("unable to delete tmp tile dir: " +
                    tileDir.getAbsolutePath());
        }
    }

    /*PRIVATE*/
    //logging
    private static final String className =
            "net.whn.loki.master.ImageHelper";
    private static final Logger log = Logger.getLogger(className);

    private static BufferedImage combineImages(BufferedImage i1,
            BufferedImage i2) {

        if (i1 == null || i2 == null) {
            log.warning("null parameter");
            return null;
        }

        int width = i1.getWidth();
        int height = i1.getHeight();

        if (width != i2.getWidth() || height != i2.getHeight()) {
            log.warning("image dimensions not equal");
            return null;
        }


        BufferedImage i3 = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = i3.createGraphics();
        g2d.drawImage(i1, null, 0, 0);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                1.0F));
        g2d.drawImage(i2, null, 0, 0);
        g2d.dispose();

        return i3;
    }
}
