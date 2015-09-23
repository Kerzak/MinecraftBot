
package minecraftbot.examples;

import minecraftbot.MinecraftBotHandler;

public class EmptyBot extends MinecraftBotHandler{

    static private String myName = "EmptyBot";
    private static final String address = "localhost";
    private static final int port = 24114;
    
    

    
    
    public static void main(String[] args) throws Exception {
        EmptyBot bot = new EmptyBot();
        bot.start(myName, address, port);
    }

}
