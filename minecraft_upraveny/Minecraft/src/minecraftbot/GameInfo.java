package minecraftbot;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import minecraftbot.logger.LogElement;
import minecraftbot.logger.LogLevel;

/**
 * Information about server, parsed from "Join game" packet.
 */
public class GameInfo {
    private int id;
    private long dayTime, serverTime;
    private byte gameMode, dimension, difficulty, maxPlayers;
    private String levelType;
    
    private float health,foodSaturation;
    private short food;
    
    private float experienceBar;
    private short level, totalExperience;
    
    private Location spawnLocation;
    
    MinecraftBotHandler handler;
    
    public GameInfo(MinecraftBotHandler handler)
    {
        this.handler = handler;
    }
    
    public void loadInfo(int id, byte gamemode, byte dimension, byte difficulty, byte maxPlayers, String levelType)
    {
        this.id = id;
        this.gameMode = gamemode;
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
            default:
                Util.logger.log(LogElement.GameMessage, LogLevel.Error, "Unkown game mode: "+gamemode);
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
            default:
                Util.logger.log(LogElement.GameMessage, LogLevel.Error, "Unkown dimension: "+dimension);
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
            default:
                Util.logger.log(LogElement.GameMessage, LogLevel.Error, "Unkown difficulty: "+difficulty);
                break;
        }
        
        Util.logger.log(LogElement.GameMessage, LogLevel.Info, "Connected to the game:");
        Util.logger.log(LogElement.GameMessage, LogLevel.Info, "\tBot entity ID:\t"+id);
        Util.logger.log(LogElement.GameMessage, LogLevel.Info, "\tGamemode:\t"+sGamemode);
        Util.logger.log(LogElement.GameMessage, LogLevel.Info, "\tDimension:\t"+sDimension);
        Util.logger.log(LogElement.GameMessage, LogLevel.Info, "\tDifficulty:\t"+sDifficulty);
        Util.logger.log(LogElement.GameMessage, LogLevel.Info, "\tMax players:\t"+maxPlayers);
        Util.logger.log(LogElement.GameMessage, LogLevel.Info, "\tLevel type:\t"+levelType);
    }

    public void setTime(long serverTime, long dayTime)
    {
        this.serverTime = serverTime;
        this.dayTime = dayTime;
    }
    
    public void setHealth(float health, short food,float foodSaturation)
    {
        if(health<this.health)
        {
            handler.onDamageTaken(this.health-health);
        }
        if(health<=0)
        {
            handler.onDeath();
        }
        this.health = health;
        this.food = food;
        this.foodSaturation = foodSaturation;
    }
    
    public void respawn(int dimension, byte difficulty, byte gameMode, String levelType)
    {
        this.dimension = (byte)dimension;
        this.difficulty = difficulty;
        this.gameMode = gameMode;
        this.levelType = levelType;
        handler.onRespawn();
    }
    
    public void setSpawnLocation(int x, int y, int z)
    {
        spawnLocation = new Location(x,y,z);
    }
    
    public void setExperience(float experienceBar,short level, short totalExperience)
    {
        this.experienceBar = experienceBar;
        this.level = level;
        this.totalExperience = totalExperience;
    }
    
    /**
     * @return Entity id of bot.
     */
    public int getId() {
        return id;
    }

    /**
     * @return Current time of the day.
     */
    public long getDayTime() {
        return dayTime;
    }

    /**
    * Information abou bots health.
    * Min - 0. Max - 20.
    * @return Health of bot.
     */
    public float getHealth() {
        return health;
    }

    /**
     * @return Number of maximum players allowed on server.
     */
    public byte getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * @return Location around which will players spawn after death.
     */
    public Location getSpawnLocation() {
        return spawnLocation;
    }
    
    
    
}
