/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ex2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public final class ChatWindow extends JFrame{

    public interface OnSend {
        void called(String txt);
    }   
    
    private final String name;
    Box box;
    JTextField textField;
    JTextArea textArea;
    JButton button;
    
    public ChatWindow(String name, OnSend onSend) throws HeadlessException {
        
        this.name = name;

        this.setSize(300,300);
        this.setTitle(name);
        this.setBackground(Color.WHITE);
        box = Box.createVerticalBox();

        textField = new JTextField();
        textField.setBackground(Color.white);
        textField.setForeground(Color.black);
        
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        textField.setMinimumSize(new Dimension(Integer.MAX_VALUE, 20));
        
        textArea = new JTextArea();
        textArea.setForeground(Color.white);
        textArea.setBackground(Color.black);
        
        JScrollPane scrollFrame = new JScrollPane(textArea);
        scrollFrame.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        textArea.setAutoscrolls(true);
        
        box.add(scrollFrame);
        box.add(textField);
        
        button = new JButton("SEND");
        button.addActionListener((e) -> {
            onSend.called(textField.getText());
        });
        
        this.add(button,BorderLayout.SOUTH);
        
        button.setBackground(Color.PINK);
        
        this.add(box);
        this.setVisible(true);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public void AddText(String txt){
        textArea.append(txt + "\n");
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
    
}
