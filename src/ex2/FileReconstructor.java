/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ex2;

import ex2.packets.dataPacket;
import ex2.packets.infoPacket;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marx Jason
 */
public class FileReconstructor {
    
    public interface onFileReceived {
        void run(infoPacket info);
    }
    
    private final Set<onFileReceived> events_onFileReceived = new HashSet<>();
    
    
    
    private final static int PREFFERED_CHUNK_SIZE = 1048576; // 1 MB
        
    private final int chunk_Save_Interval;
    
    private final File resultFile;
    private final infoPacket info;
    
    private final FileOutputStream writer;
    
    private int receivedPackets = 0;
    private int packetPos = 0;
    
    private final PriorityBlockingQueue<dataPacket> packets = new PriorityBlockingQueue<>();
    
    public FileReconstructor(infoPacket info, String savePath) throws IOException {
        this.chunk_Save_Interval =  ((PREFFERED_CHUNK_SIZE) / info.getStrLength()); // i want to write 1Mb data at a time
        System.out.printf("\u001B[35m[ WRITER ] Will write every [%d] packets (Total: [%d MB])\u001B[0m\n", chunk_Save_Interval, PREFFERED_CHUNK_SIZE/(1024*1024));
        
        this.info = info;
        this.resultFile = new File(savePath + info.getFileName());
        
        this.writer = new FileOutputStream(this.resultFile);
    }
    
    /**
     * Get the infoPacket
     * @return The info packet
     */
    public infoPacket getInfo() {
        return info;
    }
    
    /**
     * Adds packet to file reconstruction
     * @param data 
     */
    public void addDataPacket(dataPacket data){
        if(data == null) return;
        
        if(!packets.add(data)) return;
        
        if(packets.size() >= chunk_Save_Interval){
            writeChunks();
        }
        
        if(++receivedPackets == this.info.getNbOfChunks()){
            System.out.printf("\u001B[35m[ WRITER ] Recieved all Packets\u001B[0m\n");
            reconstruct(this.packets.toArray(new dataPacket[this.packets.size()]));
        }
        
    }
    
    
    public void addListener(onFileReceived func){
        events_onFileReceived.add(func);
    }
    
    public void removeListener(onFileReceived func){
        events_onFileReceived.remove(func);
    }
    
    
    private void executeEventListeners(){
        events_onFileReceived.forEach((fileReceived) -> {
            fileReceived.run(getInfo());
        });
    }
    
    private void writeChunks(){
        if(packets.peek().getIndex() != packetPos){
            System.out.printf("\u001B[35m[ WRITER ] Awaiting packet [ID: %d]\u001B[0m\n", packetPos);
            return;
        }
        
        List<dataPacket> pacs = new ArrayList<>();
        
        while(!packets.isEmpty() && packets.peek().getIndex() == packetPos){
            
            // write to file until no chunks left
            pacs.add(packets.poll());
            packetPos++;
        }
        
        reconstruct(pacs.toArray(new dataPacket[pacs.size()]));
    }
    
    public void reconstruct(dataPacket ...dataPacks) {
        System.out.printf("\u001B[35m[ WRITER ] Writing data to file [%s]\u001B[0m\n", resultFile.getAbsolutePath());
        
        try {
           
            for (dataPacket packet : dataPacks) {
                System.out.printf("\u001B[35m[ WRITER ][%s] Writing chunk to file\u001B[0m\n", packet.getIndex());
                writer.write(packet.getData(), 0, packet.getData().length);
            }
            
            writer.flush();
            
            System.out.printf("\u001B[35m[ WRITER ] Wrote data to file [%s]\u001B[0m\n", resultFile.getAbsolutePath());
            
            if(receivedPackets == this.info.getNbOfChunks()){
                writer.close();
                executeEventListeners();
            }
            
        } catch (IOException ex) {
            Logger.getLogger(FileReconstructor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
}
