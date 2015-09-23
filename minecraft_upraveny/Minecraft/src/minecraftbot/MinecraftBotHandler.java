/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraftbot;

import minecraftbot.packet.in.*;
import minecraftbot.build.Building;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import minecraftbot.entity.EntityHandler;
import minecraftbot.world.WorldHandler;
import minecraftbot.network.MinecraftOutputStream;
import minecraftbot.network.MinecraftInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import minecraftbot.build.AdvancedBuilding;
import minecraftbot.logger.LogElement;
import minecraftbot.logger.LogLevel;
import minecraftbot.messaging.Message;
import minecraftbot.messaging.MessageQueue;
import minecraftbot.navigation.Navigation;
import minecraftbot.packet.in.In03TimeUpdate;
import minecraftbot.packet.in.In05SpawnPosition;
import minecraftbot.packet.in.In30WindowItems;
import minecraftbot.packet.in.In40Disconnect;
import minecraftbot.packet.out.Out00Handshake;
import minecraftbot.packet.out.Out00Login;
import minecraftbot.packet.out.Out01ChatMessage;
import minecraftbot.packet.out.Out06PlayerPositionAndLook;
import minecraftbot.packet.out.Out07PlayerDigging;
import minecraftbot.packet.out.Out08PlayerBlockPlacement;
import minecraftbot.packet.out.Out09HeldItemChange;
import minecraftbot.packet.out.Out0DCloseWindow;
import minecraftbot.packet.out.Out0EClickWindow;
import minecraftbot.packet.out.Out15ClientSettings;
import minecraftbot.packet.out.Out16ClientStatus;
import minecraftbot.packet.out.OutPacket;

/**
 * Class used for creating, connecting and starting bots.
 * Handles main loop, network communications and handling events.
 */
public abstract class MinecraftBotHandler {

    private final byte VERSION = 4;
        
    protected String name;
    
    private MinecraftInputStream in;
    private MinecraftOutputStream out;
    private Socket socket;
    private HashMap<Integer,IInPacketHandler> packetMapIn;
    private HashMap<Integer,OutPacket> packetMapOut;
    private Out08PlayerBlockPlacement blockPlacementPacket;
    private Out15ClientSettings clientSettingsPacket;
    private Out16ClientStatus clientStatusPacket;
    private boolean busy;

    protected int entityId=0;
    protected Locomotion move;
    protected ChatHandler chat;
    protected Mining mining;
    protected Building build;
    protected AdvancedBuilding build2;
    protected GameInfo info;
    protected Navigation navigation;
    protected IInventoryView inventory;
    protected IWorldView world;
    private WorldHandler worldHandler;
    private MessageQueue chatQueue;
    private EntityHandler entityHandler;
    private IInventoryStorage inventoryStorage;
    protected IInventoryHandler inventoryHandler;
    protected PlayerList players;
    protected Collecting collecting;
    
    public static Boolean onGround, initialized;
        
    /**
     * Starts bot.
     * @param name Name of the bot.
     * @param address Server address.
     * @param port Server port.
     */
    public void start(String name, String address, int port)
    {
        this.name = name;
        
        Util.Load();

        
        try {
            connect(name, address, port);

            worldHandler = new WorldHandler();
            
            setHandlers(worldHandler, worldHandler);


        
            loop();
        } catch (IOException | InterruptedException | IllegalAccessException | InstantiationException ex) {
            System.err.println("Could not connect to the server.");
            System.out.println(ex.toString());
            System.exit(0);
        }
        
    }
    
    /**
     * Creates connection to server
     */
    void connect(String name, String address, int port) throws UnknownHostException, IOException, InterruptedException, IllegalAccessException, InstantiationException
    {
        System.out.println(name+" CONNECTING");
        initialized = false;
        socket = new Socket(address, port);
        in = new MinecraftInputStream(socket.getInputStream());
        out = new MinecraftOutputStream(socket.getOutputStream());
        
        
        createOutputPackets();

        //handshake packet
        new Out00Handshake(out).sendMessage(VERSION, address, port);

        //login packet
        new Out00Login(out).sendMessage(name);
        Thread.sleep(100);

    }
    
    void setHandlers(WorldHandler worldHandler, IWorldView worldView)
    {
        if(worldHandler!=null)
            this.worldHandler = worldHandler;
        if(worldView!=null)
            world = worldView;
        
        info = new GameInfo(this);
        move = new Locomotion(world,new Out06PlayerPositionAndLook(out), blockPlacementPacket);
        chatQueue = new MessageQueue();
        entityHandler = new EntityHandler(world);
        players = new PlayerList(entityHandler.getPlayers());
        collecting = new Collecting(entityHandler.getStackList(), entityHandler.getVelocities(), move);
        chat = new ChatHandler(new Out01ChatMessage(out));
        inventoryHandler = new InventoryHandler(this, new Out0DCloseWindow(out), new Out09HeldItemChange(out), new Out0EClickWindow(out));
        inventoryStorage = (InventoryHandler)inventoryHandler;
        mining = new Mining(out, this, info, move, inventoryHandler, world,new Out07PlayerDigging(out));
        navigation = new Navigation(world, move,mining);
        inventory = new InventoryInfo((InventoryHandler)inventoryHandler);
        build = new Building(out, blockPlacementPacket, info, move, world, inventoryHandler, inventory,inventoryStorage);
        build2 = new AdvancedBuilding(world, build, navigation, move);

        createInputPacketMap();

    }
    
    /**
     *  Maps packet Ids to correspondening handlers.
     */
    private void createInputPacketMap()
    {
        packetMapIn = new HashMap<>();
        packetMapIn.put(0x00, new In00KeepAlive(out));
        packetMapIn.put(0x01, new In01JoinGame(info));
        packetMapIn.put(0x03, new In03TimeUpdate(info));
        packetMapIn.put(0x05, new In05SpawnPosition(info));
        packetMapIn.put(0x06, new In06UpdateHealth(info));
        packetMapIn.put(0x07, new In07Respawn(info));
        packetMapIn.put(0x08, new In08PlayerPositionAndLook(move, out));
        packetMapIn.put(0x0C, new In0CSpawnPlayer(entityHandler));
        packetMapIn.put(0x0E, new In0ESpawnObject(entityHandler));
        packetMapIn.put(0x12, new In12EntityVelocity(entityHandler));
        packetMapIn.put(0x13, new In13DestroyEntities(entityHandler));
        packetMapIn.put(0x15, new In15EntityRelativeMove(entityHandler));
        packetMapIn.put(0x17, new In17EntityRelativeMoveAndLook(entityHandler));
        packetMapIn.put(0x18, new In18EntityTeleport(entityHandler));
        packetMapIn.put(0x1C, new In1CEntityMetadata(entityHandler));
        packetMapIn.put(0x1F, new In1FSetExperience(info));
        packetMapIn.put(0x22, new In22MultiBlockChange(worldHandler));
        packetMapIn.put(0x23, new In23BlockChange(worldHandler,inventoryStorage));
        packetMapIn.put(0x26, new In26MapChunkBulk(worldHandler, out));
        packetMapIn.put(0x2F, new In2FSetSlot(inventoryStorage));
        packetMapIn.put(0x30, new In30WindowItems(inventoryStorage));
        packetMapIn.put(0x32, new In32ConfirmTransaction(inventoryStorage));
        packetMapIn.put(0x37, new In37Statistics(info));
        packetMapIn.put(0x38, new In38PlayerListItem(info, entityHandler));
        packetMapIn.put(0x39, new In39PlayerAbilities(info));
        packetMapIn.put(0x40, new In40Disconnect());
        

        /*
        packetMapOut = new HashMap<>();
        packetMapOut.put(0x01, new Out01ChatMessage(out));
        packetMapOut.put(0x06, new Out06PlayerPositionAndLook(out));*/
        
    }
    
    private void createOutputPackets()
    {
        clientStatusPacket = new Out16ClientStatus(out);
        clientSettingsPacket = new Out15ClientSettings(out);
        blockPlacementPacket = new Out08PlayerBlockPlacement(out);    }
    
    /**
     * Reads and handles incoming packets.
     */
    private void processPacket() throws IOException, InstantiationException, IllegalAccessException
    {
        in.loadBuffer(in.readLength());
        int id = in.loadByte();
        
        // <editor-fold  desc="Print packet ID" defaultstate="collapsed">

       // Util.logger.log(LogElement.PacketId, LogLevel.Info, Integer.toHexString(id)+" - ");
        switch(id)
        {
            case 0x00:
                Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Keep Alive");
                break;
            case 0x01:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Join Game");
            break;
            case 0x02:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Chat Message");
            break;
            case 0x03:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Time Update");
            break;
            case 0x04:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Entity Equipment");
            break;
            case 0x05:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Spawn Position");
            break;
            case 0x06:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Update Health");
            break;
            case 0x07:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Respawn");
            break;
            case 0x08:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Player Position And Look");
            break;
            case 0x09:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Held Item Change");
            break;
            case 0x0A:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Use Bed");
            break;
            case 0x0B:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Animation");
            break;
            case 0x0C:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Spawn Player");
            break;
            case 0x0D:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Collect Item");
            break;
            case 0x0E:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Spawn Object");
            break;
            case 0x0F:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Spawn Mob");
            break;
            case 0x10:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Spawn Painting");
            break;
            case 0x11:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Spawn Experience Orb");
            break;
            case 0x12:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Entity Velocity");
            break;
            case 0x13:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Destroy Entities");
            break;
            case 0x14:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Entity");
            break;
            case 0x15:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Entity Relative Move");
            break;
            case 0x16:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Entity Look");
            break;
            case 0x17:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Entity Look and Relative Move");
            break;
            case 0x18:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Entity Teleport");
            break;
            case 0x19:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Entity Head Look");
            break;
            case 0x1A:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Entity Status");
            break;
            case 0x1B:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Attach Entity");
            break;
            case 0x1C:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Entity Metadata");
            break;
            case 0x1D:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Entity Effect");
            break;
            case 0x1E:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Remove Entity Effect");
            break;
            case 0x1F:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Set Experience");
            break;
            case 0x20:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Entity Properties");
            break;
            case 0x21:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Chunk Data");
            break;
            case 0x22:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Multi Block Change");
            break;
            case 0x23:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Block Change");
            break;
            case 0x24:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Block Action");
            break;
            case 0x25:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Block Break Animation");
            break;
            case 0x26:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Map Chunk Bulk");
            break;
            case 0x27:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Explosion");
            break;
            case 0x28:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Effect");
            break;
            case 0x29:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Sound Effect");
            break;
            case 0x2A:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Particle");
            break;
            case 0x2B:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Change Game State");
            break;
            case 0x2C:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Spawn Global Entity");
            break;
            case 0x2D:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Open Window");
            break;
            case 0x2E:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Close Window");
            break;
            case 0x2F:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Set Slot");
            break;
            case 0x30:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Window Items");
            break;
            case 0x31:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Window Property");
            break;
            case 0x32:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Confirm Transaction");
            break;
            case 0x33:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Update Sign");
            break;
            case 0x34:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Maps");
            break;
            case 0x35:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Update Block Entity");
            break;
            case 0x36:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Sign Editor Open");
            break;
            case 0x37:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Statistics");
            break;
            case 0x38:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Player List Item");
                    break;
            case 0x39:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Player Abilities");
            break;
            case 0x3A:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Tab-Complete");
            break;
            case 0x3B:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Scoreboard Objective");
            break;
            case 0x3C:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Update Score");
            break;
            case 0x3D:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Display Scoreboard");
            break;
            case 0x3E:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Teams");
            break;
            case 0x3F:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Plugin Message");
            break;
            case 0x40:
                    Util.logger.log(LogElement.PacketId, LogLevel.Trace, "Disconnect");
            break;
        }
        // </editor-fold>
        
        if(packetMapIn.containsKey(id))
        {
            if(id==8)
                initialized = true;
            packetMapIn.get(id).process(in.getDataStream());
        }
    }
 
    /**
     * Main loop.
     */
    void loop() throws IOException, IllegalAccessException, InstantiationException, InterruptedException
    {
        //before initialization
        while(true)
        {
            if(in.available()>0)
            {
                processPacket();
            }
            if(initialized)
                break;
            Thread.yield();
        }
        move.stuck = false;
        packetMapIn.put(0x02, new In02ChatMessage(chatQueue));
        clientSettingsPacket.sendMessage();
        entityId = info.getId();
        initialize();
        int logicStep=0;
        //after initialization
        while (true)
        {
            while(in.available()>0)
            {
                processPacket();
            }
            if(!chatQueue.isEmpty())
            {
                processMessages();
            }
            if(logicStep==0)
            {
                busy = mining.isMining()||build.isBuilding()||navigation.isNavigating();
                logic();
                move.stuck = false;
            }
            logicStep = logicStep==4? 0:++logicStep;
            mining.update();
            navigation.update();
            move.update(out);
            build.update();
            entityHandler.update();
            inventoryHandler.update();
            build2.update();
            out.flush();
            Thread.sleep(50);
        }
    }

    /**
     * @return Whether the bot is currently performing some action. (buiding, miningging, navigating)
     */
    protected boolean isBusy() {
        return busy;
    }
    
    /**
     * Main event for controlling the bot.
     * Triggers once every 250ms.
     */
    protected void logic()
    {
    }
    
    /**
     * Event for bot initialization.
     * Triggers after receiving information about bot location for first time.
     */
    protected void initialize()
    {
        Util.logger.log(LogElement.GameMessage, LogLevel.Info, "Bot initialized.");
    }
    
    /**
     * called when chat message is received
     * @param sender name of the sender
     * @param text message text
     */
    protected void readMessage(String sender, String text) 
    {
        Util.logger.log(LogElement.GameMessage, LogLevel.Info, sender +": "+text);
    }

    /**
     * Triggers when new player connects to the server
     * @param player  name of the player
     */
    protected void onPlayerConnect(String player) {
        Util.logger.log(LogElement.GameMessage, LogLevel.Info, "New player connected: "+player);
    }

    /**
     * Triggers when new player connects to the server.
     * @param player  name of the player
     */
    protected void onPlayerDisconnect(String player) {
        Util.logger.log(LogElement.GameMessage, LogLevel.Info, "Player disconnected: "+player);
    }

    /**
     * Triggers whenever bot takes damage.
     * @param amount amount of damage taken.
     */
    protected void onDamageTaken(float amount)
    {
        Util.logger.log(LogElement.Custom, LogLevel.Info, "You have taken "+ amount+" damage. "+info.getHealth());
    }

    /**
     * Triggers when bot dies.
     */
    protected void onDeath()
    {
        Util.logger.log(LogElement.Custom, LogLevel.Info, "You have died.");
    }

    /**
     * Triggers when bot revives after death.
     */
    protected void onRespawn()
    {
        Util.logger.log(LogElement.Custom, LogLevel.Info, "You have respawned.");
    }
    
    /**
     * Triggers when a player is killed by another player.
     * @param player  name of the player killed.
     * @param attacker name of the killer.
     */
    protected void onPlayerKilled(String player, String attacker) {
        Util.logger.log(LogElement.GameMessage, LogLevel.Info, "Player "+player+" was slain by "+attacker +".");
    }

    /**
     * Triggers when you are killed by another player.
     * @param attacker name of the killer.
     */
    protected void onDeathByAttack(String attacker) {
        Util.logger.log(LogElement.GameMessage, LogLevel.Info, "You have been slain by "+attacker +".");
    }
    
    /**
     * Triggers when bot succesfully mines a block.
     * @param location Location of mined block.
     */
    public void onMiningSuccess(Location location)
    {
        
    }
    
    /**
     * Called when bot fails to mining a block.
     * @param location Location of the block.
     * @param id Id of the block.
     */
    public void onMiningFail(Location location, Id id)
    {
        Util.logger.log(LogElement.Mining, LogLevel.Warn, "Failed to mine block "+id+" @"+location);
    }
    
    /**
     * Triggers when bot picks up an item.
     * @param id Id of the item.
     * @param count Number of items picked up.
     */
    public void onItemPickUp(Id id, int count)
    {
        Util.logger.log(LogElement.Custom, LogLevel.Debug, "Picked up "+count+" of "+id);
    }
    
    /**
     * Triggers when bot crafts an item.
     * @param id Item id.
     * @param count Number of items picked up.
     */
    public void onItemCraft(Id id, int count)
    {
        Util.logger.log(LogElement.Custom, LogLevel.Debug, "Picked up "+count+" of "+id);
    }
    
    /**
     * Triggers when bot tries to mine a block
    that is too far away.
     * @param location Location of the block.
     */
    public void onOutOfRangeToMine(Location location)
    {
        Util.logger.log(LogElement.Mining, LogLevel.Error, "Bot is too far away to mine @"+location);
    }
    
    /***
     * Requests respawn.
     */
    protected void requestRespawn()
    {
        clientStatusPacket.respawn();
    }
    
    
    /**
     * Triggers when bot finishes building a simple structure.
     * @param already Set of locations of blocks that were already build before.
     */
    protected void buildingFinished(HashSet<Location> already)
    {
        
    }

    /**
     * Proceses game char messages.
     */
    private void processMessages() {
        Message msg = chatQueue.getMessage();
        switch(msg.getEvent())
        {
            case ChatMessage:
                if(!msg.getValue("player").equals(name))
                    readMessage(msg.getValue("player"), msg.getValue("text"));
                break;
            case PlayerJoined:
                onPlayerConnect(msg.getValue("player"));
                break;
            case PlayerLeft:
                onPlayerDisconnect(msg.getValue("player"));
                break;       
            case PlayerAttackDeath:
                if(!msg.getValue("player").equals(name))
                    onPlayerKilled(msg.getValue("player"),msg.getValue("player"));
                else 
                    onDeathByAttack(msg.getValue("attacker"));
                break;       
        }
    }

}
