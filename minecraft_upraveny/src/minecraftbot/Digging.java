/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraftbot;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import java.io.IOException;
import java.util.LinkedList;
import minecraftbot.logger.LogElement;
import minecraftbot.logger.LogLevel;
import minecraftbot.network.MinecraftOutputStream;

/**
 * Allows bot to dig holes, mine blocks, cut trees etc.
 * @author eZ
 */
public class Digging {
    
    private final MinecraftOutputStream out;
    private final IInventoryHandler inventory;
    private final Locomotion move;
    private final IWorldView world;
    private final GameInfo info;
    private int x, z;
    private byte y;
    private boolean digging, sendFinishedMsg, digFinished, useTool;
    private final LinkedList<DigPlan> digQueue;
    
    public Digging(MinecraftOutputStream out, GameInfo info, Locomotion move, IInventoryHandler inventory, IWorldView world)
    {
        this.out= out;
        this.inventory = inventory;
        this.info = info;
        this.move = move;
        this.world = world;
        digQueue = new LinkedList<>();
    }
    
    /**
     * Digs a block.
     * @param location Location of the block.
     */
    public void dig(Location location)
    {
        int dx = (int)location.x-(location.x<0?1:0);
        byte dy = (byte)location.y;
        int dz = (int)location.z-(location.z<0?1:0);
        digQueue.add(new DigPlan((byte)1, dx, dy, dz));
        //System.out.println("Digging :" + location);
    }
    
    /**
     * Digs a block.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param z Z coordinate.
     */
    public void dig(int x, int y, int z)
    {
        digging=true;
        digFinished=true;
        digQueue.add(new DigPlan((byte)1, x, (byte)y, z));
    }
   
    /**
     * Sends message to server to actually dig planned block.
     */
    private void dig()
    {
        
        ItemType prefType = getPreferedTool(world.getBlock(x, y, z).getId());
        Util.logger.log(LogElement.Digging, LogLevel.Trace, "TO DIG : "+world.getBlock(x, y, z).getId().getType());
        Util.logger.log(LogElement.Digging, LogLevel.Trace, "TOOL   : "+prefType);

        if(prefType!=ItemType.NONE)
        {
            inventory.requestHeldChange(prefType);
        }
        move.lookAt(new Location(x, y, z));
        try {
            out.write(10);
            out.writeInt(info.getId());
            out.write(1);      
            out.closePacket();
            out.write(7);
            out.write(0);
            out.writeInt(x);
            out.write(y);
            out.writeInt(z);
            out.write(0);      
            out.closePacket();
            digging = true;
            digFinished = false;
            sendFinishedMsg = false;
        } catch (IOException ex) {
            System.err.println("IO Error while digging.");
            System.exit(0);
        }
    }
    
    /**
     * Cancels all future plans for digging.
     */
    public void cancelAllDigging()
    {
        digQueue.clear();
    }
    
    public void loopStep()
    {
        if(digging&&!digFinished)
        {
            try {
                move.lookAt(new Location(x, y, z));
                out.write(10);
                out.writeInt(info.getId());
                out.write(1);
                out.closePacket();
                if(!sendFinishedMsg)
                {
                        out.write(7);
                        out.write(2);
                        out.writeInt(x);
                        out.write(y);
                        out.writeInt(z);
                        out.write(0);
                        out.closePacket();
                        sendFinishedMsg = true;
                }
                Util.logger.log(LogElement.Digging, LogLevel.Debug, x+"-"+y+"-"+z+" is "+world.getBlock(x, y, z).getId());
                if(world.getBlock(x, y, z).getId()==Id.AIR)
                {
                        if(digQueue.isEmpty())
                            digging = false;
                        
                        digFinished = true;
                }
            } catch (IOException ex) {
                System.err.println("IO Error in digging loop step");
                System.exit(0);
            }
        }
        else if(!digQueue.isEmpty())
        {
            if(!digging||digFinished)
            {
                digFinished = false;
                DigPlan nextStep = digQueue.removeFirst();
                x = nextStep.x;
                y = nextStep.y;
                z = nextStep.z;
                if(!world.getBlock(x, y, z).isEmpty())
                    dig();
            }
        }
    }

    public void useTools(boolean b)
    {
        useTool = b;
    }

    public boolean isUsingTools() {
        return useTool;
    }
    
    public boolean isDigging() {
        return digging;
    }

    public ItemType getPreferedTool(ItemType toDig)
    {
        switch(toDig)
        {
            case WOOD:
                return ItemType.AXE;
            case GROUND:
                return ItemType.SHOVEL;
            case ICE:
                return ItemType.PICKAXE;
            case METAL:
                return ItemType.PICKAXE;
            case PLANT:
                return ItemType.AXE;
            case RAIL:
                return ItemType.PICKAXE;
            case ROCK:
                return ItemType.PICKAXE;
            default:
                return ItemType.NONE;
            
        }
    }
    
    
    public ItemType getPreferedTool(Id toDig)
    {
        return getPreferedTool(toDig.getType());
    }

        
        
    class DigPlan
    {
        public final int x, z;
        public final byte y, id;
        
        public DigPlan(byte id, int x, byte y, int z)
        {
            this.id = id;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }    
}
