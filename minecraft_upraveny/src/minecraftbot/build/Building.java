/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraftbot.build;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import minecraftbot.GameInfo;
import minecraftbot.IInventoryHandler;
import minecraftbot.IInventoryView;
import minecraftbot.IWorldView;
import minecraftbot.Id;
import minecraftbot.Locomotion;
import minecraftbot.Util;
import minecraftbot.logger.LogElement;
import minecraftbot.logger.LogLevel;
import minecraftbot.network.MinecraftOutputStream;

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
    
    public Building(MinecraftOutputStream out, GameInfo info, Locomotion move, IWorldView world, IInventoryHandler inventoryHandler, IInventoryView inventory)
    {
        this.inventoryHandler = inventoryHandler;
        this.inventory = inventory;
        this.out= out;
        this.info = info;
        this.move = move;
        this.world = world;
        // to do blockingBlocks = new HashSet<>();
        buildQueue = new ArrayList<>();
        waitForConfirmation = 0;
    }
    
    public void loopStep()
    {
        if(!buildQueue.isEmpty())
        {
            if(heldChangeRequested)
            {
                if(!heldChangeConfirmed)
                {
                    //System.out.println("HELD CHANGE NOT CONFIRMED");
                    heldChangeConfirmed = inventoryHandler.isConfirmed();
                }
                if(heldChangeConfirmed)
                {
                    //System.out.println("HELD CHANGE IS CONFIRMED");
                    putBlock(buildQueue.get(0));
                    buildQueue.remove(0);
                    heldChangeRequested = false;
                    heldChangeConfirmed = false;
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
                    //System.out.println("REQUESTING "+buildQueue.getFirst().id);
                heldChangeConfirmed = false;
                if(inventory.heldId()!=buildQueue.get(0).id)
                {
                    System.out.println(buildQueue.get(0).id);
                    actionNumber = inventoryHandler.requestHeldChange(buildQueue.get(0).id);
                    
                    //System.out.println("REQUEST RESULT "+actionNumber);
                    if(actionNumber>=0)
                        heldChangeRequested = true;
                    else if(actionNumber==-2)
                        buildQueue.remove(0);
                }
                else
                {
                    //System.out.println("ALREADY HOLDING RIGHT");
                    heldChangeConfirmed = true;
                    heldChangeRequested = true;
                }
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
        Util.logger.log(LogElement.Building, LogLevel.Debug, "Putting block");
        move.lookAt(new Location(plan.x, plan.y, plan.z));
        waitForConfirmation = 0;
        try {
            out.write(10);
            out.writeInt(info.getId());
            out.write(1);      
            out.closePacket();
            
            out.write(8);
            out.writeInt(plan.x);
            out.write(plan.y-1);
            out.writeInt(plan.z);
            
            out.write(1);

            out.write(0);
            out.write(1);
            out.write(1);
            out.write(0);
            out.write(0);
            out.write(255);
            out.write(255);
            
            out.write(1);
            out.write(1);
            out.write(1);
            out.closePacket();
        } catch (IOException ex) {
            System.out.println("IO Error while placing a block.");
            System.exit(0);
        }
    }
    
    public boolean isBuilding()
    {
        return !buildQueue.isEmpty();
    }

    
}
