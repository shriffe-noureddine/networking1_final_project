/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ex2.packets;

public class ackPacket extends packet {

    private final int code;

    private final int ackID;

    public int getCode() {
        return code;
    }

    @Override
    public int getID() {
        return this.ackID;
    }

    public ackPacket(int id, int code) {
        super();
        this.ackID = id;
        this.code = code;
    }   

    public int getAckID() {
        return ackID;
    }

    @Override
    public String toString() {
        return super.toString("code: %d", this.code); 
    }
}
