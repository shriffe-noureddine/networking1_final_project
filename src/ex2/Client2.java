/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ex2;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client2 {
    static ArrayList<FilePeer_finished> client2_peers = new ArrayList<>();

    static String address = "localhost";
    static String path = "/Users/nech/Desktop/NetworksProject2/Client";

    public static void main(String[] args) {
        System.out.println("Client: Receives files");

        try {
for(int i = 0; i < 10; i++){
                client2_peers.add(new FilePeer_finished(path + (i + 1) + "/receivedFiles/", address, 1235 + i));
            }

        } catch (UnknownHostException | SocketException ex) {
            Logger.getLogger(Client2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
