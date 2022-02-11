/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ex2;

import ex2.packets.infoPacket;

/**
 *
 * @author Marx Jason
 */
public class Events {
    public interface onFileReceived {
        void run(infoPacket info);
    }
}
