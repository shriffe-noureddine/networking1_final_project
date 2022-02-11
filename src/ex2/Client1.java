/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ex2;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Marx Jason
 */
public class Client1 {
    public static int id;
    static int number_of_processes;
    static float probability;
    static int window;
    static String filename;


    static String path = "/Users/nech/Desktop/NetworksProject2/client";
    static ArrayList<FilePeer_finished> client1_peers = new ArrayList<>();
    static ArrayList<Integer> client1_ports = new ArrayList<>();

    public static void main(String[] args) {
        id = Integer.parseInt(args[0]);
        number_of_processes = Integer.parseInt(args[1]);
        filename = args[2];
        probability = Float.parseFloat(args[3]);
        window = Integer.parseInt(args[4]);


        try {
            System.out.println("Client: Sends files");

            // create an arraylist of ports for sender
            for (int i = 0; i < number_of_processes; i++) {
                client1_ports.add(1225 +i);
            }

            // create an arraylist of peers for sender
            for (int i = 0; i < number_of_processes; i++) {
                client1_peers.add(new FilePeer_finished(client1_ports.get(i)));
            }

            // for all clients
            for (int i = 0; i < client1_peers.size(); i++) {
                Set files = ListFilesUsingFilesList(path + (i + 1));

                Logger.getLogger(Sender.class.getName()).log(Level.INFO, "Found {0} files. [Client {1}]", new Object[]{files.size(), i});
                for (Object file : files) {
                    for (int j = 0; j < client1_ports.size(); j++) {

                        client1_peers.get(i).sendFile(new Address(1235 + j), path + (i+1) +"/"+(String)file);
                    }
                }
            }


        } catch (UnknownHostException | SocketException ex) {
            Logger.getLogger(Client2.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        } catch (IOException ex) {
            Logger.getLogger(Client1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Set ListFilesUsingFilesList(String dir) throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get(dir))) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toSet());
        }
    }
}
