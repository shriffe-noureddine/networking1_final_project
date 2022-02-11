/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ex2;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marx Jason
 */
public class Logging {
    private static Queue<String> toLog = new LinkedList<>();
    
    private final static Thread th = new Thread(){
         @Override
        public void run() {
            while (true) {  
                String txt;
                //System.out.println(".run()");
                if((txt = Logging.toLog.poll()) != null)
                    System.out.println(txt);
            }
        }
    };
    
    static{
        //th.start();
    }
    
    static void log(String txt){
        toLog.add(txt);
        System.out.println("added " + txt);
        //th.start();
    }
    
    static void logf(String format, Object ...args){
        Logging.log(String.format(format, args));
    }
}
