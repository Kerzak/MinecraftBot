

package minecraftbot.build;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import minecraftbot.GameInfo;
import minecraftbot.IInventoryHandler;
import minecraftbot.IInventoryStorage;
import minecraftbot.IInventoryView;
import minecraftbot.IWorldView;
import minecraftbot.Id;
import minecraftbot.Locomotion;
import minecraftbot.Util;
import minecraftbot.logger.LogElement;
import minecraftbot.logger.LogLevel;
import minecraftbot.network.MinecraftOutputStream;
import minecraftbot.packet.out.Out08PlayerBlockPlacement;

/**
 * Allows bot to place blocks in enviroment and build simple structures.
 * @author eZ
 */
public class Building extends Buildable{
    
    private final MinecraftOutputStream out;
    private final IInventoryHandler inventoryHandler;
    private final IInventoryView inventory;
    private final Locomotion move;
    private final IWorldView world;
    private final GameInfo info;
    private short actionNumber;
    private boolean heldChangeRequested, heldChangeConfirmed;
    private byte waitForConfirmation;
    private final List<BlockPlan> buildQueue;
    private final Out08PlayerBlockPlacement blockPlacementPacket;
    private final IInventoryStorage inventoryStorage;
    
    public Building(MinecraftOutputStream out, Out08PlayerBlockPlacement blockPlacementPacket, GameInfo info, Locomotion move, IWorldView world, IInventoryHandler inventoryHandler, IInventoryView inventory, IInventoryStorage inventoryStorage)
    {
        this.inventoryHandler = inventoryHandler;
        this.inventory = inventory;
        this.out= out;
        this.info = info;
        this.move = move;
        this.world = world;
        this.blockPlacementPacket = blockPlacementPacket;
        this.inventoryStorage = inventoryStorage;
        // todo blockingBlocks = new HashSet<>();
        buildQueue = new ArrayList<>();
        waitForConfirmation = 0;
    }
    
    public void update()
    {
        if(!buildQueue.isEmpty())
        {
            if(heldChangeRequested)
            {
                if(!heldChangeConfirmed)
                {
                    //System.out.println("HELD CHANGE NOT CONFIRMED");
                    heldChangeConfirmed = inventoryHandler.isConfirmed(actionNumber);
                }
                if(heldChangeConfirmed)
                {
                    //System.out.println("HELD CHANGE IS CONFIRMED");
                    if(checkRange(buildQueue.get(0)))
                    {
                        putBlock(buildQueue.get(0));
                        buildQueue.remove(0);
                        heldChangeRequested = false;
                        heldChangeConfirmed = false;
                    }
                    else
                    {
                    }
                }
                else
                {
                    waitForConfirmation++;
                    if(waitForConfirmation>100)
                    {
                        Util.logger.log(LogElement.Inventory, LogLevel.Error, "HELD CHANGE NOT CONFIRMED");
                        waitForConfirmation = 0;
                    }
                }
            }
            else
            {
                if(!buildQueue.isEmpty())
                heldChangeConfirmed = false;
                {
                    actionNumber = inventoryHandler.requestHeldChange(buildQueue.get(0).id);
                    System.out.println("REQUEST RESULT "+actionNumber);
                    if(actionNumber>=0)
                        heldChangeRequested = true;
                    else if(actionNumber==-2)
                        buildQueue.remove(0);
                }
               /* else
                {
                    //System.out.println("ALREADY HOLDING RIGHT");
                    heldChangeConfirmed = true;
                    heldChangeRequested = true;
                }*/
            }
        }
    }

    @Override
    protected void addBlock(Id id, int x, int y, int z) {
        buildQueue.add(new BlockPlan(id, x, y, z));
    }
    
    /**
     * Takes planned block and puts it in the world.
     * @param plan Planned block
     */
    private void putBlock(BlockPlan plan)
    {
        inventoryStorage.toBuild(plan);
        Util.logger.log(LogElement.Building, LogLevel.Debug, "Putting block");
        move.lookAt(new Location(plan.x, plan.y, plan.z));
        waitForConfirmation = 0;
        try {
            out.write(10);
            out.writeInt(info.getId());
            out.write(1);      
            out.closePacket();
            
            blockPlacementPacket.sendMessage(plan.x, (byte) (plan.y-1),plan.z,(byte)1,(byte)1,(byte)1,(byte)1);
        } catch (IOException ex) {
            System.out.println("IO Error while placing a block.");
            System.exit(0);
        }
    }
    
    public boolean isBuilding()
    {
        return !buildQueue.isEmpty();
    }

    boolean checkRange(BlockPlan plan)
    {
        return isInRange(new Location(plan.x,plan.y,plan.z));        
    }
    
    public boolean isInRange(Location location) {
        return  Location.getDistance(move.getLocation(), location)<5.5;
    }

    
}
