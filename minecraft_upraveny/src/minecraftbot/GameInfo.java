package minecraftbot;

/**
 * Information about server, parsed from "Join game" packet.
 * @author eZ
 */
public class GameInfo {
    private int id;
    private byte gamemode, dimension, difficulty, maxPlayers;
    private String levelType;
    
    public void loadInfo(int id, byte gamemode, byte dimension, byte difficulty, byte maxPlayers, String levelType)
    {
        this.id = id;
        this.gamemode = gamemode;
        this.dimension = dimension;
        this.difficulty = difficulty;
        this.maxPlayers = maxPlayers;
        this.levelType = levelType;
        
        String sGamemode="unknown", sDifficulty="unknown", sDimension="unknown";
        switch(gamemode)
        {
            case 0:
                sGamemode = "Survival";
                break;
            case 1:
                sGamemode = "Creative";
                break;
            case 2:
                sGamemode = "Adventure";
                break;
        }
        
        switch(dimension)
        {
            case -1:
                sDimension = "Nether";
                break;
            case 0:
                sDimension = "Overworld";
                break;
            case 1:
                sDimension = "End";
                break;
        }
        
        switch(difficulty)
        {
            case 0:
                sDifficulty = "Peaceful";
                break;
            case 1:
                sDifficulty = "Easy";
                break;
            case 2:
                sDifficulty = "Normal";
                break;
            case 3:
                sDifficulty = "Hard";
                break;
        }
        
        System.out.println("Connected to the game:");
        System.out.println("\tBot entity ID:\t"+id);
        System.out.println("\tGamemode:\t"+sGamemode);
        System.out.println("\tDimension:\t"+sDimension);
        System.out.println("\tDifficulty:\t"+sDifficulty);
        System.out.println("\tMax players:\t"+maxPlayers);
        System.out.println("\tLevel type:\t"+levelType);
    }

    /**
     * @return Entity id of bot.
     */
    public int getId() {
        return id;
    }
    
    
}
