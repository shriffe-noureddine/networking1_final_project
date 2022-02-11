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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * File transfer Peer is a class that builds a client to send files between each
 * other!
 *
 * @author Marx Jason
 */
public final class FilePeer_finished {


    // Local save path. ALL FOLDERS MUST EXIST!!!
    // DEFAULT path is the users downloads folder.
    private static final Supplier<String> DEFAULT_SAVE_PATH = () -> {
        String home = System.getProperty("user.home");
        return home + "/Downloads/";
    };
    
    // Results in ~18 mb/s speed
    private static final int SEND_INTERVAL = 100;
    
    // similar to window
    private static final int SEND_COUNT = 30;
    
    
    private static final int TIMEOUT_RESEND = 1500; // In milliseconds!
    
    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();
    
    private final static Comparator<AbstractMap.SimpleEntry<Address,packet>> PAIR_COMPARATOR = (s1, s2) -> {
            return s1.getValue().compareTo(s2.getValue());
    };

    // Address of this peer (Is final: [can only be read once set!])
    private final Address address;

    // path to save downloaded files!
    private final String savePath;

    private final DatagramSocket sendSocket;

    private LocalDateTime now;
    
    // cache previous sent packets (to resend if neccesary)
    // structure: Address => { id => packet }
    // example: [ (192.168.178.34:1233, 4) => packet[id: 4] ]
    private final HashMap<Integer, packet> cashedPackets = new HashMap<>();
    
    // cache received info packets, so that we can receive multiple 
    // files from the same client at the same time
    private final HashMap<Integer, FileReconstructor> fileReconstructors = new HashMap<>();
    
    private final PriorityBlockingQueue<AbstractMap.SimpleEntry<Address, packet>> packetsToSend = new PriorityBlockingQueue<>(1, PAIR_COMPARATOR);

    //private FileBreaker fileBreaker = null;
    public Address getAddress() {
        return address;
    }

    public String getSavePath() {
        return savePath;
    }

    /**
     * Create new instance of FilePeer_finished class
     * @param port The port at which to listen for.
     * @throws UnknownHostException 
     * @throws SocketException 
     */
    public FilePeer_finished(int port) throws UnknownHostException, SocketException {
        this(DEFAULT_SAVE_PATH.get(), "", port);
    }
    
    /**
     * Create new instance of FilePeer_finished class
     * @param address The address at which to host the server (default is "localhost")
     * @param port The port at which to listen for.
     * @throws UnknownHostException
     * @throws SocketException 
     */
    public FilePeer_finished(String address, int port) throws UnknownHostException, SocketException {
        this(DEFAULT_SAVE_PATH.get(), address, port);
    }
  
    /**
     * Create new instance of FilePeer_finished class
     * @param savePath The path to save downloaded files to.
     * @param address The address at which to host the server (default is "localhost")
     * @param port The port at which to listen for.
     * @throws UnknownHostException
     * @throws SocketException 
     */
    public FilePeer_finished(String savePath, String address, int port) throws UnknownHostException, SocketException {
        this.address = (address.isEmpty())? new Address(port): new Address(address, port);
        this.savePath = savePath;

        this.sendSocket = new DatagramSocket(port);

        this.receive();
        this.sendPackets();
    }

    /**
     * Awaits the acknowledgment of the given ID of packet!
     *
     * @param id The packet ID to await
     */
    private void awaitPacketAck(int id) {

        while (true) {
            if(!cashedPackets.containsKey(id))
                break;
        }
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

        // send target a info packet containing all the info off the file
        //      the info of a file can be retrieved from the `FileBreaker` class
        //      notice: the `info` is also a packet created within `FileBreaker`!
        this.send(targetAdr, breaker.getInfo());

        // await the acknowledgment of the previous sent packet!
        awaitPacketAck(breaker.getInfo().getID());
        
        packet p;
        // send each packet of file to target!
        while ((p = breaker.nextPacket()) != null) {
            // breaker.nextPacket() => gets next packet to send from file!
            while(packetsToSend.size() > SEND_COUNT){
                // wait until there is a space
                
                //Why?
                // This is to ensure that only a certain amount of packets can be send
                // at the same time, so as to not overload the network capacity.
                // This also ensures that the risk of loosing packets is reduced
                // to a minimum.
            }
            
            // if there is space in the queue, enqueue packet.
            this.queueSend(targetAdr, p);
        }       
        
        breaker.close();   
    }
    
    
    public static final double PROBABILITY = Client1.probability; //0.1;
    public static final int WINDOW_SIZE = Client1.window; // 2
    public static final int TIMER = 30;
    
    public void SendFileWithWindow(Address targetAdr, String file) throws IOException
    {
        
        
        FileBreaker breaker = new FileBreaker(file);
        
        // send target a info packet containing all the info off the file
        //      the info of a file can be retrieved from the `FileBreaker` class
        //      notice: the `info` is also a packet created within `FileBreaker`!
        this.send(targetAdr, breaker.getInfo());

        // await the acknowledgment of the previous sent packet!
        awaitPacketAck(breaker.getInfo().getID());
        
        packet p;
        int lastSendPacket = 0;
        int lastAckedPacket = 0;
        // send each packet of file to target!
        //while()
        
        while ((p = breaker.nextPacket()) != null) 
        {
            // send packets using window
        }       
        
        breaker.close(); 
    }

    /**
     * Enqueue packet to be send.
     * @param targetAddress The address to send packet to.
     * @param packet The packet to send.
     */
    public void queueSend(Address targetAddress, packet packet){
        packetsToSend.add(new AbstractMap.SimpleEntry<>(targetAddress, packet));
    }
    
    /**
     * Send packet to client
     *
     * @param targetAdr The target client to send to!
     * @param packet The packet to send
     */
    private void send(Address targetAdr, packet packet) {
        
        // serialize the packet, so that it can be send!
        byte[] bytes = packet.serialize();

        // set up the datagram (sends to targetAdr IP [127.0.0.1])
        DatagramPacket DpSend = new DatagramPacket(bytes, bytes.length, targetAdr.getIp(), targetAdr.getPort());

        try {
            System.out.printf("[ SENDER ][Source: %s][Destination: %s]\t - %s\n", this.address, targetAdr, packet);

            // add the packet to the cache 
            //      [IF the packet is NOT an acknowledgment packet]
            //          as we do not need to save these packets.
            // we add the packet to the cache by using its ID as the key
            if (!(packet instanceof ackPacket)) {
                this.cashedPackets.put(packet.getID(), packet);
            }

            // finally we send the packet
            this.sendSocket.send(DpSend);
            
 
            Runnable task = () -> {
                
                if(cashedPackets.containsKey(packet.getID())){
                    // packet timed out!
                    System.err.printf("[ SENDER ][TIMEDOUT][Source: %s][Destination: %s][ID: %d]\n", this.address, targetAdr, packet.getID());
                    packetsToSend.add(new AbstractMap.SimpleEntry<>(targetAdr, packet));
                }
                
            };
            
            SCHEDULER.schedule(task, TIMEOUT_RESEND, TimeUnit.MILLISECONDS);
                

        } catch (IOException ex) {
            Logger.getLogger(FilePeer_finished.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
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
            public void run() {
                try {
                    System.out.println("[RECEIVER] - Listening for Packets on " + address);

                    // `receive` is a buffer and is necessary to receive anything
                    byte[] receive = new byte[65535];

                    DatagramPacket DpReceive = null;

                    // We create an endless loop, so as to await the receival of packets
                    while (true) {
                        // We define what we expect to receive!
                        DpReceive = new DatagramPacket(receive, receive.length);

                        // Now wait until a packet arrives 
                        sendSocket.receive(DpReceive);

                        // we just received some data, but that data is serialized, so we 
                        //  must first desirialize it to its original form.
                        packet receivedPacket = packet.deserialize(receive);
                        //System.out.println(receivedPacket);

                        // to make sure the packet we just received is a valid packet
                        //  we send it to the function `validatePacket`
                        validatePacket(receivedPacket, new Address(DpReceive.getAddress(), DpReceive.getPort()));

                        // `FileReconstructor` requires an infoPacket to work,
                        //      as such we need to await an infoPacket and then
                        //      instantiate a new instance of the class `FileReconstructor`
                        //          with the `infoPacket` as an argument
                        if (receivedPacket instanceof infoPacket) {
                            now = LocalDateTime.now();
                            infoPacket p = (infoPacket) receivedPacket;
                            FileReconstructor r = new FileReconstructor(p, getSavePath());
                            r.addListener((info) -> {
                                fileReconstructors.remove(info.getID());
                                Duration t = Duration.between(now, LocalDateTime.now());
                                System.out.printf("\u001b[34m[RECEIVER][FINISHED][Time: %d ms] - %s\u001b[0m\n", t.toMillis(),info);
                                
                                //System.out.println(now);
                                //System.out.printf(t.getSeconds());
                            });
                            
                            fileReconstructors.putIfAbsent(p.getID(), r);
                        }

                        // Once the `FileReconstructor` is set up, we can add packets
                        //      to the class. (Only dataPackets)
                        // Once all the packets have arrived the `FileReconstructor`
                        //      will automatically rebuild the file
                        dataPacket p;
                        if (!fileReconstructors.isEmpty() && 
                                receivedPacket instanceof dataPacket && 
                                fileReconstructors.containsKey((p = (dataPacket) receivedPacket).getInfoPacketID())) 
                        {
                            fileReconstructors.get(p.getInfoPacketID()).addDataPacket((dataPacket) receivedPacket);
                        }

                    }

                } catch (SocketException ex) {
                    Logger.getLogger(FilePeer_finished.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(FilePeer_finished.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        };

        thread.start();
    }

    
    private void sendPackets(){
        Runnable task = () -> {
           
            if(packetsToSend.isEmpty()) return;

            int count = 0;
            AbstractMap.SimpleEntry<Address, packet> p;
            while(count++ < SEND_COUNT && (p = packetsToSend.poll()) != null){
                send(p.getKey(), p.getValue());
            }
        };
        
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleWithFixedDelay(task, 100, SEND_INTERVAL, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Validate the given packet
     *
     * @param packet The packet to be validated
     * @param source The source Address from which the packet came from
     * @return True, If valid; False otherwise!
     */
    private boolean validatePacket(packet packet, Address source) {
        // checks if the given hash is valid
        boolean isOK = packet.hashCode() == packet.getHashCode();
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
                    this.queueSend(source, this.cashedPackets.get(pk.getAckID()));
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

}
