/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ex2.packets;

import ex2.Client1;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marx Jason
 */
public abstract class packet implements java.io.Serializable, Comparable< packet >{
    
    static int PACKETS_OFFSET = Client1.id;
    
    private int hashCode;
    private final Timestamp timeCreated = new Timestamp(System.currentTimeMillis());
    
    private final int id;

    public int getID(){ return id; };

    public int getHashCode() {
        return hashCode;
    }

    public packet() {
        this.id = PACKETS_OFFSET++; 
    }
    
    @Override
    public String toString() {
        return toString("");
    }

    public Timestamp getTimeCreated() {
        return timeCreated;
    }
    
    
    
    public final String toString(String format, Object ...objs) {
        String r = String.format(format, objs);
        return String.format("[%s] { id: %d%s, hash: %s}", this.getClass().getName(), this.getID(), r.isEmpty()?"": ", " + r, hashCode());
    }
    
    public final byte[] serialize() {
        this.hashCode = hashCode();
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(this);
        } catch (IOException ex) {
            Logger.getLogger(packet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return baos.toByteArray();
    }
    
    public static final packet deserialize(byte[] bytes){
        try{
            packet o;
            
            try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(  bytes ) )) {
                o = (packet) ois.readObject();
            }
            return o;
        }catch (IOException | ClassNotFoundException ex){
            Logger.getLogger(packet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + this.id;
        return hash;
    }

    @Override
    public int compareTo(packet o) {
        return Integer.compare(this.getID(), o.getID());
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("[ PACKET ] Destroyed - " + this);
        super.finalize();
    }
    
    
    
}
