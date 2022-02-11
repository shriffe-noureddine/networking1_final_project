/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ex2;

import ex2.packets.packet;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marx Jason
 */
public class testClient {

    int port;
    
    public testClient(int port) {
        this.port = port;
    }
    
    public void sendFile(String path, testClient receiver){
        System.out.println("Sending file");
        
        try(DatagramSocket socket = new DatagramSocket()){
            InetAddress ip = InetAddress.getLocalHost();
            
            FileBreaker breaker = new FileBreaker(path);
            byte[] info = breaker.getInfo().serialize();

            // send info
            DatagramPacket DpSend = new DatagramPacket(info, info.length, ip, receiver.port);
            socket.send(DpSend);
            System.out.printf("Sent: %s\n", breaker.getInfo());
            
            packet p;
            while((p = breaker.nextPacket()) != null){
                byte[] bytes = p.serialize();
                DatagramPacket DpSendData = new DatagramPacket(bytes, bytes.length, ip, receiver.port);
                socket.send(DpSendData);
                System.out.printf("Sent: %s\n", p);
            }
            
        } catch (IOException ex  ) {
            Logger.getLogger(testClient.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    public void receive(){
        Thread thread = new Thread(){
            
            @Override
            public void run() {
               try (DatagramSocket datagSocket = new DatagramSocket(port);) {
                   
                    System.out.println("Awaiting Packets on " + port);
                    byte[] receive = new byte[65535];
                    DatagramPacket DpReceive = null;

                    while(true){
                        DpReceive = new DatagramPacket(receive, receive.length);

                        datagSocket.receive(DpReceive);

                        packet receivedPacket = packet.deserialize(receive);

                        System.out.println("Received: " + receivedPacket);
                    }

                } catch (SocketException ex) {
                    Logger.getLogger(testClient.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(testClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        };
        
        thread.start();
       
    }
    
    
    public static void main(String[] args) {
        testClient c1 = new testClient(1345);
        testClient c2 = new testClient(1567);
        
        c1.receive();
        c2.sendFile("C:\\Users\\Jason\\Downloads\\myTestFile.txt", c1);
    }
    
}
