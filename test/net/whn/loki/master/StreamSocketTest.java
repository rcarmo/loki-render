/**
 *Project: Loki Render - A distributed job queue manager.
 *Version 0.7.1
 *Copyright (C) 2014 Daniel Petersen
 *Created on Aug 28, 2009
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

import net.whn.loki.network.BrokerStreamSocket;
import net.whn.loki.network.Hdr;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import net.whn.loki.network.GruntStreamSocket;
import net.whn.loki.common.*;
import net.whn.loki.common.ICommon.HdrType;
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
public class StreamSocketTest {

    public StreamSocketTest() {
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

    @Test
    public void testBadSocketParam() {
        try{
        BrokerStreamSocket bsSock = new BrokerStreamSocket(new Socket());
        fail();
        }
        catch(Exception ex) {
            //success!
        }
    }

    @Test
    public void testDeliverReceive() {
        BrokerStreamSocket bSock = null;

        try {
            Grunt grunt = new Grunt();
            Thread gThread = new Thread(grunt);
            ServerSocket sock = new ServerSocket(4444);
            gThread.start();
            bSock = new BrokerStreamSocket(sock.accept());
            Hdr firstHeader = new Hdr(HdrType.MACHINE_INFO);
            bSock.sendHdr(firstHeader);
            Hdr secondHeader = bSock.receiveDelivery();

            assertEquals(firstHeader.getType(), secondHeader.getType());
            bSock.tryClose();
        }
        catch (Exception ex) {
            fail();
        }
    }
}
class Grunt implements Runnable {

    public void run() {
        try {
        InetAddress mAddress = InetAddress.getByName("localhost");
        GruntStreamSocket gSock = new GruntStreamSocket(mAddress, 4444);
        Hdr h = gSock.receiveDelivery();
        gSock.sendHdr(h);
        gSock.tryClose();
        }
        catch (Exception ex) {
            fail();
        }
    }
}
