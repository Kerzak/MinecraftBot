/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraftbot.examples;

import minecraftbot.MinecraftBotHandler;
import org.w3c.dom.NameList;

/**
 *
 * @author eZ
 */
public class MultiBot extends MinecraftBotHandler implements Runnable{


    private String name = "EmptyBot";
    /*static private String address = "localhost";
    static private int port = 51236;*/
    static private String address = "localhost";
    static private int port = 24114;
    private static final int BOTCOUNT = 6;
    
    @Override
    protected void logic() {
        move.jump();
    }

    @Override
    protected void initialize() {
        chat.sendMessage("Let's jump!");
    }

    @Override
    public void run() {
        startBot(name, address, port);
    }
    
    public MultiBot(String name)
    {
        this.name = name;
    }
    
    public static void main(String[] args) throws Exception {
        Thread[] threads;
        threads = new Thread[BOTCOUNT];
        System.out.println("Initializing");
        for (int i = 0; i < BOTCOUNT; i++) {
            threads[i] = new Thread(new MultiBot("BOT"+i));
        }
        System.out.println("Completed. Connecting...");
        for (int i = 0; i < BOTCOUNT; i++) {
            threads[i].start();
            System.out.println("\tBOT"+i+" connected!");
        }
    }

}
