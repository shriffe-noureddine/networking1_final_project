/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ex2.packets;

import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author Marx Jason
 */
public class dataPacket extends packet implements java.io.Serializable{
    private final byte[] data;

    private final int index;

    private final int infoPacketID;

    public dataPacket(int infoPacket, int index, byte[] data) {
        super();
        this.data = data;
        this.infoPacketID = infoPacket;
        this.index = index;

    }
    
    public byte[] getData(){
        return data;
    }

    public int getIndex() {
        return index;
    }

    public int getInfoPacketID() {
        return infoPacketID;
    }
    
    

    @Override
    public String toString() {
        return super.toString("Idx: %d, infoPacket: %d, String_length: %d", this.index, this.infoPacketID,this.data.length);
    }

    @Override
    public int compareTo(packet o) {
        if(!(o instanceof dataPacket)) return 0;
        return Integer.compare(this.getIndex(), ((dataPacket)o).getIndex());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Arrays.hashCode(this.data);
        hash = 89 * hash + this.index;
        hash = 89 * hash + this.infoPacketID;
        hash = 89 * hash + super.hashCode();
        return hash;
    }

    
    
    
    
}
