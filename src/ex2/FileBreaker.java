package ex2;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import ex2.packets.dataPacket;
import ex2.packets.infoPacket;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marx Jason
 */
public class FileBreaker {
    
    public final static int DEFAULT_SIZE = 61440;
    //public final static int DEFAULT_SIZE = 30720;
    
    private int nmbOfPackets = 0;
    private final int packetsToSend;
    
    private infoPacket info;

    private final File file;
    private final int size;
    
    private InputStream input;
    
    
    public FileBreaker(String path) throws IOException { this(path, DEFAULT_SIZE); }
    public FileBreaker(String path, int size) throws IOException {
        this.file = new File(path);
        this.size = size;
        
        this.input = new FileInputStream(file);
        
        this.packetsToSend = (int)Math.ceil(file.length() / (double)size);
        this.info = new infoPacket(this.file.getName(), this.packetsToSend, size);
    }
    
    private byte[] read() throws IOException{
        byte[] buffer = new byte[size];
        
        ByteArrayOutputStream ous = new ByteArrayOutputStream();
        
        int r = input.read(buffer, 0, size);
        if(r != -1)
            ous.write(buffer, 0, r);
        
        return ous.toByteArray();
    }
    
    public infoPacket getInfo(){
        return this.info;
    }
    
    public dataPacket nextPacket(){
        try {
            byte[] data = read();
            if (data.length != 0){            
                return new dataPacket(this.getInfo().getID(), nmbOfPackets++, data);
            }
             
        } catch (IOException ex) { 
            Logger.getLogger(FileBreaker.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        
        return null;
    }  
    
    public void close() throws IOException {
        this.input.close();
    }
}
