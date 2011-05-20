/**
 *Project: Loki Render - A distributed job queue manager.
 *Version 0.6.2
 *Copyright (C) 2009 Daniel Petersen
 *Created on Aug 17, 2009
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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import net.whn.loki.common.Config;
import net.whn.loki.common.LokiForm;

/**
 * sends multicast announcement so grunts can discover master's IP address
 * it doesn't require any cleanup since it's just sending UDP, so it should
 * be started as a daemon thread.
 * @author daniel
 */
public class AnnouncerR implements Runnable {

    /**
     *
     * @throws IOException
     */
    public AnnouncerR(Config c, LokiForm dForm) throws IOException {

        cfg = c;
        dummyForm = dForm;
        announceInterval = cfg.getAnnounceInterval();
        InetAddress localMachine = InetAddress.getLocalHost();

        masterInfo = localMachine.getHostName() + ";" + cfg.getLokiVer() +
                ";" + Integer.toString(cfg.getConnectPort()) + ";";

        mSocket = new MulticastSocket(cfg.getMulticastPort());
        mSocket.setTimeToLive(cfg.getMulticastTTL());
        dgramPacketAnnounce = new DatagramPacket(masterInfo.getBytes(),
                masterInfo.length(), cfg.getMulticastAddress(),
                cfg.getMulticastPort());


    }

    @Override
    public void run() {
        int failureCount = 0;

        String masterIP = detectMaster();
        if (masterIP != null) {
            MasterEQCaller.showMessageDialog(dummyForm, "Master detected",
                    "Loki master already running on system '" + masterIP + "'.",
                    JOptionPane.WARNING_MESSAGE);
            log.info("detected master at:" + masterIP);
        }

        while (!Thread.currentThread().isInterrupted()) {
            if (failureCount > 5) {
                //we can't continue if announce is broken
                throw new IllegalStateException();
            }

            try {
                mSocket.send(dgramPacketAnnounce);
                failureCount = 0;

                try {
                    Thread.sleep(announceInterval);
                } catch (InterruptedException ex) {
                    break;  //from the master -> shutdown
                }
            } catch (IOException ex) {
                failureCount++;
            }
        }//end of while
    }

    /*BEGIN PRIVATE*/
    //logging
    private static final String className = "net.whn.loki.master.AnnouncerR";
    private static final Logger log = Logger.getLogger(className);
    final private Config cfg;
    final private LokiForm dummyForm;
    final private int announceInterval;
    final private String masterInfo;
    final private DatagramPacket dgramPacketAnnounce;
    final private MulticastSocket mSocket;

    private String detectMaster() {
        byte[] buf = new byte[256];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        try {
            //throws IOE
            MulticastSocket mSock = new MulticastSocket(cfg.getMulticastPort());

            //throws IOE
            mSock.joinGroup(cfg.getMulticastAddress()); //TODO - throws IO -> quit!
            mSock.setSoTimeout(5000);

            mSock.receive(packet);
        } catch (SocketTimeoutException ex) {
            return null;
        } catch (IOException ex) {
            //TODO
        }
        String remoteMasterInfo = new String(packet.getData());
        StringTokenizer st = new StringTokenizer(remoteMasterInfo, ";");
        return st.nextToken();
    }
}
