
package minecraftbot.examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import minecraftbot.MinecraftBotHandler;

/**
 * Provides simple text user interface to launch bots.
 * @author eZ
 */
public class Launcher {
    
        public static void main(String[] args) throws IOException
        {
            System.out.println("1. Empty Bot");
            System.out.println("2. Responsive Bot");
            System.out.println("3. Lumberjack");
            System.out.println("4. Builder");
            System.out.println("5. Cleaner");
            System.out.println("6. Commentator");
            System.out.println("Enter number of bot you want to launch:");
            
            String input, address, name="Bot";
            int botId = 0,port=0;
            MinecraftBotHandler bot = null;
            
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            try{
                input = br.readLine();
                botId = Integer.parseInt(input);
            }catch(NumberFormatException e){
                System.err.println("Invalid Format!");
                System.exit(0);
            }
            if(botId<1||botId>6)
            {
                System.err.println("Invalid number!");
                System.exit(0);
            }
            
            System.out.println("Enter server address:");
            address = br.readLine();
            
            System.out.println("Enter server port:");
            try{
                input = br.readLine();
                port = Integer.parseInt(input);
            }catch(NumberFormatException e){
                System.err.println("Invalid Format!");
                System.exit(0);
            }
            
            switch(botId)
            {
                case 1:
                    bot = new EmptyBot();
                    name = "EmptyBot";
                    break;
                case 2:
                    bot = new ResponsiveBot();
                    name = "ResponsiveBot";
                    break;
                case 3:
                    bot = new Lumberjack();
                    name = "Lumberjack";
                    break;
                case 4:
                    bot = new Builder();
                    name = "Builder";
                    break;
                case 5:
                    bot = new Cleaner();
                    name = "Cleaner";
                    break;
                case 6:
                    bot = new Commentator();
                    name = "Commentator";
                    break;
            }
            
            try
            {
                bot.start(name, address, port);
            }
            catch(Exception e)
            {
                System.err.println(e);
            }
        }
}
