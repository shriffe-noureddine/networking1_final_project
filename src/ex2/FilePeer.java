/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ex2;

import ex2.packets.ackPacket;
import ex2.packets.dataPacket;
import ex2.packets.infoPacket;
import ex2.packets.packet;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marx Jason
 */
public final class FilePeer {
    // Deafult save path
    private static final String DEFAULT_SAVE_PATH = "C:\\Users\\Jason\\Downloads\\networksProjectDownloads\\target\\";
    
    // Address of this peer (Is final: [can only be read once set!])
    //      contains both ip Address and port
    private final Address address;

    // path to save downloaded files!
    private final String savePath;
    
    // Socket to BOTH send AND receive!
    private final DatagramSocket socket;
    
    // cache previous sent packets (to resend if neccesary)
    // structure: { id => packet }
    // example: [ 4 => packet[id: 4] ]
    private final HashMap<Integer, packet> cashedPackets = new HashMap<>();
    
    
    public Address getAddress() {
        return address;
    }

    public String getSavePath() {
        return savePath;
    }
    
    
    public FilePeer(int port) throws UnknownHostException, SocketException {
        this(DEFAULT_SAVE_PATH, port);
    }

    public FilePeer(String savePath, int port) throws UnknownHostException, SocketException {
        this.address = new Address(port);
        this.savePath = savePath;

        // Socket should be used to both send and receive!
        this.socket = new DatagramSocket(port);

        // start receiving!
        this.receive();
    }
    
    
    /**
     * Sends a given file to the target Address
     *
     * @param targetAdr The Address to send to
     * @param file The file to send
     * @throws IOException
     */
    public void sendFile(Address targetAdr, String file) throws IOException {
        // `FileBreaker` is a helper class that cuts a given file into chunks, 
        //      it also prepares the chunks for transfer
        FileBreaker breaker = new FileBreaker(file);

        // We first need to send the other client a info packet, so that the 
        //     receiver understands what we want to do!
        // we can get a info packet from:
        infoPacket info = breaker.getInfo();
        
        // send info packet to other client!
        //  we can use the function `send()`
        
        
        // THIS IS ONLY IMPOTANT IF WE SEND FROM ANOTHER COMPUTER!
        // important: we NEED to wait for the other client to acknowledge the 
        //              receival of the info packet before we can continue!
        //            If we don't, the next packets sent will be discarded!
        

        // The next part is the need to send all the packets of the file to the other
        //  client!
        //      We can use the `breaker.nextPacket()` to get the next packet to send!
        
    }
    
    /**
     * Send bytes to client
     *
     * @param targetAdr The target client to send to!
     * @param packet The packet to send
     */
    public void send(Address targetAdr, packet packet) {
        System.out.printf("[ SENDER ][Source: %s][Destination: %s]\t - %s\n", this.address, targetAdr, packet);
        
        // serialize the packet, so that it can be send!
        // This can be archieved using `packet.serialize();`

        // set up the datagram (sends to targetAdr)
        // we can use the IP and Port from the `targetAdr`


        // add the packet to the cache 
        //      [IF the packet is NOT an acknowledgment packet]
        //          as we do not need to save these packets
        // we add the packet to the cache by using its ID as the key
        if (!(packet instanceof ackPacket)) {
            this.cashedPackets.put(packet.getID(), packet);
        }

        // finally we send the packet
        //  Send the packet over the `socket`
    }
    
    /**
     * Awaits packets from the given port
     */
    private void receive() {
        // We create a new thread
        //      We do this because both the sender and the receiver systems
        //      must work at the same time
        Thread thread = new Thread() {

            @Override
            
            // This is what will be executed on start()
            public void run() {
                System.out.println("[RECEIVER] - Listening for Packets on " + address);
                
                while(true){
                    // wait packets
                    
                    // An array of bytes can be converted to a packet using:
                    //      packet receivedPacket = packet.deserialize(<bytes>);
                    
                    // A packet might need to be casted to another object, ex:
                    //      infoPacket infoPKT = (infoPacket) receivedPacket;
                    
                    // Important: Packets once received must be validated and 
                    //   acknowledged (use the function validatePacket() once a
                    //   a packet arrived)
                }       
            }
        };

        thread.start();
    }
    
    
    
    /**
     * Validate the given packet
     *
     * @param packet The packet to be validated
     * @param source The source Address from which the packet came from
     * @return True, If valid; False otherwise!
     */
    /*
    private boolean validatePacket(packet packet, Address source) {
        // checks if the given hash is valid
       
        String result = String.format("[RECEIVER][%s][Source: %s][Destination: %s] - %s", (isOK) ? "HASH OK " : "HASH ERR", source, this.address, packet);

        if (isOK) {
            // Prints the result in green, if correct!
            System.out.printf("\u001b[32m%s\u001b[0m\n", result);
        } else {
            System.err.printf("%s\n", result);
        }

        // check wether the packet is an acknowledgment packet or not
        if (packet instanceof ackPacket) {
            // if the packet is incorrect return
            if (!isOK) return false;
            
            ackPacket pk = (ackPacket) packet;
            
            // check what code the ackPacket has
            switch (pk.getCode()) {
                case 200: // 200 => Everything is OK
                    // This removes the cached packet from the cache, 
                    //  since it is no longer required!
                    System.out.printf("\u001B[33m[RECEIVER][  ACK   ][CODE: %d][ID: %d]\u001B[0m\n", pk.getCode(), pk.getAckID());
                    this.cashedPackets.remove(pk.getAckID());
                    break;
                case 301: // 301 => Everything is BAD (Please resend)
                    // This resends the cached packet to the target 
                    this.send(source, this.cashedPackets.get(pk.getAckID()));
                    break;
            }
        } else {
            // if we receive anything other than a acknowledgment packet, we will 
            //  send a ackPacket to tell the client that the chunk was OK or not!
            if (isOK) {
                this.send(source, new ackPacket(packet.getID(), 200));
            } else {
                this.send(source, new ackPacket(packet.getID(), 301));
            }
        }

        return isOK;
    }
    */
}

