package ex2;

import java.io.IOException;

import java.net.*;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;


public final class Peer_chat
{

    private final String name;
    private ChatWindow window;
    
    // this is our own address!
    private final Address address;
    
    // should be used in both sending and recieving packets!
    private final DatagramSocket socket;
    
    // The reciever Thread (must be on another thread than the main thread!)
    private final Thread peer_reciever;
    
    // other peers to send stuff to and get stuff from!!
    //      An Address contains an IP and a Port! 
    // We want a HashSet, since we do not want dupplicates! 
    private final HashSet<Address> peer_addresses = new HashSet<>();
    

    public static void main(String [] args) throws UnknownHostException {

        try {
            Peer_chat jempi = new Peer_chat("jempi", 1234);
            Peer_chat jack = new Peer_chat("jack", 1235);
            Peer_chat tom = new Peer_chat("tom", 1236);
            
            jempi.join(jack.address);
            jempi.join(tom.address);
            
            jack.join(jempi.address);
            jack.join(tom.address);
            
            tom.join(jempi.address);
            tom.join(jack.address);

        } catch (SocketException ex) {
            Logger.getLogger(Peer_chat.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
    
    /**
     * Join a peer to a group chat
     * @param adr The address of the peer to join
     */
    public void join(Address adr){
        peer_addresses.add(adr);
    }
   
    /**
     * Send text to all the connected peers!
     * @param txt The text to send
     */
    private void sendToAll(String txt){
        byte[] txtBytes = txt.getBytes();
        for (Address peer_addresse : peer_addresses) {
            DatagramPacket DpSend = new DatagramPacket(txtBytes, txtBytes.length, peer_addresse.getIp(), peer_addresse.getPort());
            
            try {

                System.out.printf("[ SENDER ][Source: %s][Destination: %s]\t - %s\n", this.address, peer_addresse, txt);
                // Send the packet
                this.socket.send(DpSend);

            } catch (IOException ex) {
                Logger.getLogger(FilePeer_finished.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }


    public Peer_chat(String name, int port) throws SocketException, UnknownHostException
    {
        this.name = name; 
        
        this.socket = new DatagramSocket(port);
        this.address = new Address(port);
        
        this.window = new ChatWindow(name, (txt) -> {
            String result = String.format("@%s - %s", name, txt);
            
            this.window.AddText(result);
            sendToAll(result);
        });
        
        this.peer_reciever = new Thread(){
            
            @Override
            public void run() {
                System.out.println("[RECEIVER] - Listening for Packets on " + address);
                byte[] receive;

                DatagramPacket DpReceive;
                
                // await new packets
                while(true){
                    receive = new byte[65535];
                    DpReceive = new DatagramPacket(receive, receive.length);
                    try {
                        // Now wait until a packet arrives
                        socket.receive(DpReceive);
                        
                        // Convert bytes to String
                        String txt = data(DpReceive.getData()).toString();
                        
                        // {DEBUG} Print the result to console!
                        System.out.printf("\u001b[32m[RECEIVER][Source: %s][Destination: %s] - %s\u001b[0m\n", 
                                new Address(DpReceive.getAddress(), DpReceive.getPort()), address, txt);

                        // Add the recieved text to the window!
                        window.AddText(txt);

                    } catch (IOException ex) {
                        Logger.getLogger(Peer_chat.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
         
        };
        
        this.peer_reciever.start();
    }
    
    // A utility method to convert the byte array 
    // data into a string representation. 
    public static StringBuilder data(byte[] a) 
    { 
        if (a == null) 
            return null; 
        StringBuilder ret = new StringBuilder(); 
        int i = 0; 
        while (a[i] != 0) 
        { 
            ret.append((char) a[i]); 
            i++; 
        } 
        return ret; 
    } 

}