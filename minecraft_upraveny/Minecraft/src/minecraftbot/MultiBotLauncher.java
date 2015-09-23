/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraftbot;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import minecraftbot.examples.ResponsiveBot;
import minecraftbot.world.WorldHandler;

/**
 *
 * @author eZ
 */
public class MultiBotLauncher {

    static String address = "localhost";
    static int port = 59699;
    
    Map<String, MinecraftBotHandler> bots;
    Map<String, Thread> threads;
    WorldHandler worldHandler;
    IWorldView world;
    
    public MultiBotLauncher()
    {
        bots = new HashMap<>();
        threads = new HashMap<>();
    }
    
    public void add(String name, MinecraftBotHandler handler)
    {
        bots.put(name, handler);
    }
    
    public void remove(String name)
    {
        bots.remove(name);
    }
    
    public void start(String address, int port)
    {
        
        Util.Load();

        for (Map.Entry<String, MinecraftBotHandler> entry : bots.entrySet()) {
            String name = entry.getKey();
            MinecraftBotHandler handler = entry.getValue();
            
            try {
                handler.connect(name, address, port);

                worldHandler = new WorldHandler();
                world = worldHandler;

                handler.setHandlers(worldHandler, world);

                threads.put(name, new Thread(new BotThread(handler)));
                threads.get(name).start();

            } catch (IOException | InterruptedException | IllegalAccessException | InstantiationException ex) {
                System.err.println("Could not connect to the server.");
                System.out.println(ex.toString());
                System.exit(0);
            }
        }
    }
    
    public static void main(String args[])
    {
       MultiBotLauncher launcher = new MultiBotLauncher();
       launcher.add("Reactive", new ResponsiveBot());
       launcher.add("Reactive1", new ResponsiveBot());
       launcher.add("Reactive2", new ResponsiveBot());
       launcher.start(address, port);
    }
    
    class BotThread implements  Runnable
    {
        private MinecraftBotHandler handler;
        
        public BotThread(MinecraftBotHandler handler)
        {
            this.handler = handler;
        }

        @Override
        public void run() {
            try {
                handler.loop();
            } catch (IOException ex) {
                Logger.getLogger(MultiBotLauncher.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(MultiBotLauncher.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                Logger.getLogger(MultiBotLauncher.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(MultiBotLauncher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
    }
}
