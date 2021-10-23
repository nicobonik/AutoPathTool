package org.example.lib.framework;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MessageHandler {

    public static ArrayList<Message> messages = new ArrayList<Message>();

    public static JFrame frame = new JFrame();
    private static GridLayout layout;

    public static void initialize() {

        layout = new GridLayout(messages.size(), 1, 0, 0);
        frame.setSize(400, 400);
        frame.setLayout(layout);
        frame.setVisible(true);
    }

    @Deprecated
    public static void sendToConsole(Message m) {
        System.out.print("\r" + m.getMessage());
    }

    public static void addMessage(Message m) {
        messages.add(m);
    }

    public static void update() {
        layout.setRows(messages.size());
        Component[] components = frame.getComponents();
        for (Message message : messages) {
            boolean isAdded = false;
            for (Component component : components) {
                if(message.getContainer().equals(component)) {
                    isAdded = true;
                }
            }
            if(!isAdded) {
                frame.add(message.getContainer());
            }

            message.update();
        }
    }

}