/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ex2;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

/**
 *
 * @author Marx Jason
 */
 public class Address{
    private final int port;
    private final InetAddress ip;

    public InetAddress getIp() { return ip; }
    public int getPort() { return port; }
    
    public Address(int port) throws UnknownHostException {
        this(InetAddress.getLocalHost(), port);
    }

    public Address(String ip, int port) throws UnknownHostException {
        this(InetAddress.getByName(ip), port);
    }
    
    public Address(InetAddress ip, int port) {
        this.port = port;
        this.ip = ip;
    }

    @Override
    public String toString() {
        return String.format("%s:%d", this.ip.getHostAddress(), this.port);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Address)) return false;
        return this.hashCode() == obj.hashCode();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.port;
        hash = 79 * hash + Objects.hashCode(this.ip);
        return hash;
    }
    
    
    
    
    
}
