/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraftbot;

import com.sun.corba.se.spi.oa.OADefault;
import minecraftbot.packet.in.In00KeepAlive;
import minecraftbot.packet.in.In13DestroyEntities;
import minecraftbot.packet.in.In01JoinGame;
import minecraftbot.packet.in.In15EntityRelativeMove;
import minecraftbot.packet.in.In08PlayerPositionAndLook;
import minecraftbot.packet.in.In18EntityTeleport;
import minecraftbot.packet.in.In2FSetSlot;
import minecraftbot.packet.in.In0ESpawnObject;
import minecraftbot.packet.in.In32ConfirmTransaction;
import minecraftbot.packet.in.In12EntityVelocity;
import minecraftbot.packet.in.In0CSpawnPlayer;
import minecraftbot.packet.in.IInPacket;
import minecraftbot.packet.in.In22MultiBlockChange;
import minecraftbot.packet.in.In02ChatMessage;
import minecraftbot.packet.in.In26MapChunkBulk;
import minecraftbot.packet.in.In1CEntityMetadata;
import minecraftbot.packet.in.In17EntityRelativeMoveAndLook;
import minecraftbot.packet.in.In23BlockChange;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import minecraft.commands.CommandParser;
import minecraft.crafting.Crafting;
import minecraft.exception.MinecraftException;
import minecraft.inventory.InventoryManager;
import minecraftbot.build.AdvancedBuilding;
import minecraftbot.logger.LogElement;
import minecraftbot.logger.LogLevel;
import minecraftbot.messaging.Message;
import minecraftbot.messaging.MessageQueue;
import minecraftbot.navigation.Navigation;
import minecraftbot.packet.in.In2DOpenWindow;
import minecraftbot.packet.in.In30WindowItems;
import minecraftbot.packet.in.In31WindowProperty;
import minecraftbot.packet.out.Out00Handshake;
import minecraftbot.packet.out.Out00Login;
import minecraftbot.packet.out.Out01ChatMessage;
import minecraftbot.packet.out.Out06PlayerPositionAndLook;
import minecraftbot.packet.out.Out09HeldItemChange;
import minecraftbot.packet.out.Out0DCloseWindow;
import minecraftbot.packet.out.Out0EClickWindow;
import minecraftbot.packet.out.OutPacket;

/**
 *
 * @author eZ
 * @author Kerzak
 */
public abstract class MinecraftBotHandler extends Thread{

    private final byte VERSION = 4;
        
    protected String name;
    
    Out0EClickWindow out0EClickWindow;
    
    In32ConfirmTransaction in32ConfirmTransaction;
    
    private MinecraftInputStream in;
    private MinecraftOutputStream out;
    private Socket socket;
    private HashMap<Integer,IInPacket> packetMapIn;
    private HashMap<Integer,OutPacket> packetMapOut;
    private boolean busy;

    protected int entityId=0;
    protected Locomotion move;
    protected ChatHandler chat;
    protected Digging dig;
    protected Building build;
    protected AdvancedBuilding build2;
    protected GameInfo info;
    protected Navigation navigation;
    protected IInventoryView inventory;
    protected IWorldView world;
    private WorldHandler worldHandler;
    private MessageQueue chatQueue;
    private EntityHandler entityHandler;
    protected IInventoryStorage inventoryStorage;
    protected IInventoryHandler inventoryHandler;
    protected PlayerList players;
    protected Collecting collecting;
    protected Crafting crafting;
    protected InventoryManager inventoryManager;
    protected CommandParser commandParser;
    
    public static Boolean onGround, initialized;
        
    public Locomotion getLocomotion() {
        return this.move;
    }
    
    public ChatHandler getChat() {
        return this.chat;
    }
    
    public Digging getDigging() {
        return this.dig;
    }
    
    public Building getBuilding() {
        return this.build;
    }
    
    public AdvancedBuilding getAdvancedBuilding() {
        return this.build2;
    }
    
    public GameInfo getGameInfo() {
        return this.info;
    }
    
    public Navigation getNavigation() {
        return this.navigation;
    }
    
    public IInventoryView getInventoryView() {
        return this.inventory;
    }
    
    public IWorldView getWorldView() {
        return this.world;
    }
    
    public IInventoryStorage getInventoryStorage() {
        return this.inventoryStorage;
    }
    
    /**
     * Use inventoryManager instead
     * @return 
     */
    @Deprecated
    public IInventoryHandler getInventoryHandler() {
        return this.inventoryHandler;
    }
    
    public PlayerList getPlayerList() {
        return this.players;
    }
    
    public Collecting getCollecting() {
        return this.collecting;
    }
    
    public Crafting getCrafting() {
        return this.crafting;
    }
    
    public InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }
    
    
    /**
     * Starts bot
     * @param name name of our bot
     * @param address address of server
     * @param port port of server
     */
    public void startBot(String name, String address, int port)
    {
        this.name = name;
        
        initialized = false;
        Util.Load();

        try {
            connect(address, port);
            Thread.sleep(250);
            //loop();
        } catch (IOException | InterruptedException | IllegalAccessException | InstantiationException ex) {
            System.err.println("Could not connect to the server.");
            System.out.println(ex.toString());
            System.exit(0);
        }
        
    }
    
    /**
     * Creates connection to server
     */
    private void connect(String address, int port) throws UnknownHostException, IOException, InterruptedException, IllegalAccessException, InstantiationException
    {
        socket = new Socket(address, port);
        in = new MinecraftInputStream(socket.getInputStream());
        out = new MinecraftOutputStream(socket.getOutputStream());
        
        out0EClickWindow = new Out0EClickWindow(out);
        
        worldHandler = new WorldHandler();
        world = worldHandler;
        info = new GameInfo();
        move = new Locomotion(world,new Out06PlayerPositionAndLook(out));
        chatQueue = new MessageQueue();
        entityHandler = new EntityHandler(world);
        players = new PlayerList(entityHandler.getPlayers());
        collecting = new Collecting(entityHandler.getStackList(), entityHandler.getVelocities(), move);
        navigation = new Navigation(world, move);
        chat = new ChatHandler(new Out01ChatMessage(out));
        inventoryHandler = new InventoryHandler(new Out0DCloseWindow(out), new Out09HeldItemChange(out), out0EClickWindow);
        inventoryStorage = (InventoryHandler)inventoryHandler;
        dig = new Digging(out, info, move, inventoryHandler, world);
        inventory = new InventoryInfo((InventoryHandler)inventoryHandler);
        inventoryManager = new InventoryManager(out, (InventoryHandler)inventoryHandler, out0EClickWindow, new Out0DCloseWindow(out), new Out09HeldItemChange(out), in32ConfirmTransaction);
        build = new Building(out, info, move, world, inventoryHandler, inventory);
        build2 = new AdvancedBuilding(world, build, navigation, move);
        crafting = new Crafting(out, move, inventoryManager, chat);
        commandParser = new CommandParser(crafting, chat, inventoryManager);

        createPacketMap();

        //handshake packet
        new Out00Handshake(out).sendMessage(VERSION, address, port);
        
        //login packet
        new Out00Login(out).sendMessage(name);

    }
    
    /**
     *  Maps packet IDs to correspondent handler classes.
     */
    private void createPacketMap()
    {
        packetMapIn = new HashMap<>();
        packetMapIn.put(0x00, new In00KeepAlive(out));
        packetMapIn.put(0x01, new In01JoinGame(info));
        packetMapIn.put(0x08, new In08PlayerPositionAndLook(move, out));
        packetMapIn.put(0x0C, new In0CSpawnPlayer(entityHandler));
        packetMapIn.put(0x0E, new In0ESpawnObject(entityHandler));
        packetMapIn.put(0x12, new In12EntityVelocity(entityHandler));
        packetMapIn.put(0x13, new In13DestroyEntities(entityHandler));
        packetMapIn.put(0x15, new In15EntityRelativeMove(entityHandler));
        packetMapIn.put(0x17, new In17EntityRelativeMoveAndLook(entityHandler));
        packetMapIn.put(0x18, new In18EntityTeleport(entityHandler));
        packetMapIn.put(0x1C, new In1CEntityMetadata(entityHandler));
        packetMapIn.put(0x22, new In22MultiBlockChange(worldHandler));
        packetMapIn.put(0x23, new In23BlockChange(worldHandler));
        packetMapIn.put(0x26, new In26MapChunkBulk(worldHandler, out));
        packetMapIn.put(0x2D, new In2DOpenWindow(inventoryManager));
        packetMapIn.put(0x2F, new In2FSetSlot(inventoryManager, inventoryStorage));
        packetMapIn.put(0x30, new In30WindowItems(inventoryManager));
        packetMapIn.put(0x31, new In31WindowProperty());
        packetMapIn.put(0x32, new In32ConfirmTransaction(inventoryStorage));
        
        /*
        packetMapOut = new HashMap<>();
        packetMapOut.put(0x01, new Out01ChatMessage(out));
        packetMapOut.put(0x06, new Out06PlayerPositionAndLook(out));*/
        
    }
    
    /**
     * reads and handles incoming packets  
     */
    private void processPacket() throws IOException, InstantiationException, IllegalAccessException
    {
        in.loadBuffer(in.readLength());
        int id = in.loadByte();
        //System.out.println("PACKET WITH ID " + Integer.toHexString(id) + " RECIEVED");
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
     * main loop
     */
    private void loop() throws IOException, IllegalAccessException, InstantiationException, InterruptedException
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
                busy = dig.isDigging()||build.isBuilding()||navigation.isNavigating();
                logic();
                move.stuck = false;
            }
            logicStep = logicStep==4? 0:++logicStep;
            dig.loopStep();
            navigation.loopStep();
            move.loopStep(out);
            build.loopStep();
            entityHandler.loopStep();
            try {
                crafting.loopStep();
            } catch(MinecraftException e) {
                System.out.println(e.getMessage());
                chat.sendMessage(e.getMessage());
            }
            out.flush();
            Thread.sleep(50);
        }
    }

    /**
     * @return Whether the bot is currently performing some action. (buiding, digging, navigating)
     */
    protected boolean isBusy() {
        return busy;
    }
    
    /**
     * main method for controlling the bot
     * called once every 250ms
     */
    protected abstract void logic();
    
    /**
     * method for bots initialization
     * called after receiving information about bot location for first time
     */
    protected abstract void initialize();
    
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
     * called when new player connects to the server
     * @param player  name of the player
     */
    protected void onPlayerConnect(String player) {
        Util.logger.log(LogElement.GameMessage, LogLevel.Info, "New player connected: "+player);
    }

    /**
     * called when new player connects to the server
     * @param player  name of the player
     */
    protected void onPlayerDisconnect(String player) {
        Util.logger.log(LogElement.GameMessage, LogLevel.Info, "Player disconnected: "+player);
    }
    
    protected void buildingFinished(HashSet<Location> already)
    {
        
    }

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
                break;        }
    }
    
    @Override
    public void run() {
        try {
            loop();
        } catch (IOException ex) {
            Logger.getLogger(MinecraftBotHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(MinecraftBotHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(MinecraftBotHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(MinecraftBotHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
