/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ex2.packets;

import java.util.Objects;

/**
 *
 * @author Marx Jason
 */
public class infoPacket extends packet implements java.io.Serializable{

    private final String fileName;
    
    private final int strLength;
    
    private final int nbOfChunks;

    public String getFileName() {
        return fileName;
    }

    public int getNbOfChunks() {
        return nbOfChunks;
    }

    public int getStrLength() {
        return strLength;
    }
    
    

    public infoPacket(String fileName, int nbOfChunks, int strLength) {
        super();
        this.fileName = fileName;
        this.nbOfChunks = nbOfChunks;
        this.strLength = strLength;
    }


    @Override
    public String toString() {
        return super.toString("NBChunks: %d, file: %s", this.nbOfChunks, this.fileName); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + Objects.hashCode(this.fileName);
        hash = 71 * hash + this.strLength;
        hash = 71 * hash + this.nbOfChunks;
        hash = 71 * hash + super.hashCode();
        return hash;
    }


    
}
