/**
 *Project: Loki Render - A distributed job queue manager.
 *Version 0.6.2
 *Copyright (C) 2009 Daniel Petersen
 *Created on Sep 12, 2009
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
package net.whn.loki.CL;

import net.whn.loki.common.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import net.whn.loki.IO.GruntIOHelper;

/**
 *
 * @author daniel
 */
public class CLHelper implements ICommon {

    public static String generateIsSupportedCL(JobType type, String binPath) {
        String isSupportedCL = null;    //TODO
        if (type == JobType.BLENDER) {
            if (binPath == null) {
                return "blender -v";
            } else {
                return binPath + " -v";
            }
        } else {
            log.severe("received unknown job type!");
        }
        return null;
    }

    /**
     * generates command line based on type and values in task
     * @param task
     * @return command line to pass to shell, or null if unknown type
     */
    public static String[] generateTaskCL(String bin, File lBaseDir, Task task)
            throws IOException {
        String[] taskCL = null;
        JobType type = task.getType();
        if (type == JobType.BLENDER) {
            taskCL = blender_generateCL(bin, lBaseDir, task);
        } else {
            log.severe("received unknown job type!");
        }
        return taskCL;
    }

    public static TaskStatus determineTaskReturn(JobType type, String stdout,
            String errout) {
        if (type == JobType.BLENDER) {
            return blender_determineTaskReturn(stdout);
        } else {
            log.severe("received unknown job type!");
            return null;
        }
    }

    /**
     *
     * @param stdout
     * @return full path
     */
    public static String blender_getRenderedFileName(String stdout) {
        //example "Saved: /home/daniel/.loki/tmp/0001.png Time: 00:00.31"
        String[] tokens = stdout.split("\\n");
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].contains("Saved:")) {
                int last = tokens[i].lastIndexOf("Time:") - 1;
                return tokens[i].substring(7, last);
            }
        }
        return null;
    }

    public static String extractBlenderRenderTime(String stdout) {
        String[] tokens = stdout.split(" |\\n");
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].contains("Time:")) {
                return tokens[i + 1];
            }
        }

        return null;
    }

    public static boolean determineBlenderBin(Config cfg)
             {
        boolean exeOK = true;

        String blenderBinStr = cfg.getBlenderBin();
        if (blenderBinStr == null) {
            blenderBinStr = "blender";
        }

        if (!isBlenderExe(blenderBinStr)) { //no good, ask user to find it
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Please select the Blender executable");
            while (true) {
                if (fileChooser.showDialog(null, "Select") ==
                        JFileChooser.APPROVE_OPTION) {
                    blenderBinStr = fileChooser.getSelectedFile().getPath();

                    if (isBlenderExe(blenderBinStr)) {
                        break;
                    } else {
                        String msg = "Loki can't validate\n'" +
                                blenderBinStr + "'\n" +
                                "as a Blender executable. Use it anyway?";
                        int result = JOptionPane.showConfirmDialog(null, msg,
                                "Valid executable?", JOptionPane.YES_NO_OPTION);

                        log.info("can't validate blender executable: " +
                                blenderBinStr);
                        if(result == 0)
                            break;
                    }
                } else {
                    log.info("loki didn't get a blender exe path; exiting.");
                    return false;
                }
            }
        }
        cfg.setBlenderBin(blenderBinStr);
        return exeOK;
    }

    public static boolean isBlenderExe(String blenderBinStr)
             {

        String[] cl = {blenderBinStr, "-v"};
        ProcessHelper pHelper = new ProcessHelper(cl);

        String[] result = pHelper.runProcess();
        if (result[0].contains("Blender")) {
            return true;
        } else {
            log.info("not a valid blender executable: " + blenderBinStr);
            return false;
        }
    }

    /*BEGIN PRIVATE*/
    //logging
    private static final String className = "net.whn.loki.grunt.TaskCLHelper";
    private static final Logger log = Logger.getLogger(className);

    private static String[] blender_generateCL(String blenderBin,
            File lokiBaseDir, Task t) throws IOException {

        File blendFile = new File(lokiBaseDir, "fileCache" +
                File.separator + t.getProjectFileMD5() + ".blend");
        File tmpDirFile = new File(lokiBaseDir, "tmp");
        String[] blenderCL = null;

        if (blendFile.canRead() && tmpDirFile.isDirectory()) {
            
            if (t.isTile()) {
                File script = GruntIOHelper.blender_setupTileScript(tmpDirFile, t);
                    
                blenderCL = new String[9];
                blenderCL[0] = blenderBin;
                blenderCL[1] = "-b";
                blenderCL[2] = blendFile.getCanonicalPath();
                blenderCL[3] = "-P";
                blenderCL[4] = script.getCanonicalPath();
                blenderCL[5] = "-o";
                blenderCL[6] = tmpDirFile.getCanonicalPath() + File.separator;
                blenderCL[7] = "-f";
                blenderCL[8] = Integer.toString(t.getFrame());
                


            } else {    //render the entire frame
                //example 'blender -b file.blend -o render_# -f 1

                blenderCL = new String[7];
                blenderCL[0] = blenderBin;
                blenderCL[1] = "-b";
                blenderCL[2] = blendFile.getCanonicalPath();
                blenderCL[3] = "-o";
                blenderCL[4] = tmpDirFile.getCanonicalPath() + File.separator;
                blenderCL[5] = "-f";
                blenderCL[6] = Integer.toString(t.getFrame());
                

            }
        } else {
            log.severe("problems generating blender CL: " +
                    blendFile.getAbsolutePath() + " " +
                    tmpDirFile.getAbsolutePath());
        }

        return blenderCL;
    }

    private static TaskStatus blender_determineTaskReturn(String stdout) {
        TaskStatus status = null;
        if (stdout.contains("Saved:")) {
            status = TaskStatus.DONE;
        } else {
            status = TaskStatus.FAILED;
        }
        return status;
    }
}
