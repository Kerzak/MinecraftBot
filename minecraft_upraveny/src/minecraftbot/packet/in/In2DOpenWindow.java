/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraftbot.packet.in;

import java.io.IOException;
import minecraft.inventory.InventoryManager;
import minecraftbot.entity.EntityHandler;
import minecraftbot.messaging.MessageQueue;
import minecraftbot.network.MinecraftDataInputStream;

/**
 *
 * @author Kerzak
 */
public class In2DOpenWindow implements IInPacket{
    
    InventoryManager inventoryManager;

    private byte windowId;
    private byte inventoryType;
    private String winTitle;
    private byte numberOfSlots;
    /**
     * only when window type is EntityHorse
     */
    private int entityId;
    
    public In2DOpenWindow (InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }
    
    @Override
    public void process(MinecraftDataInputStream in) throws IOException {
        //System.out.println("PACKET RECIEVED: Open window");
        this.windowId = in.readByte();
        System.out.println("WINDOW ID: " + windowId);
        this.inventoryType = in.readByte();
        System.out.println("window Type: " + this.inventoryType);
        this.winTitle = in.readString();
        System.out.println("WINTITLE: " + winTitle);
        this.numberOfSlots = in.readByte();
        System.out.println("number of slots: " + numberOfSlots);
        if (this.windowId == 11) {
            this.entityId = in.readInt();
        }
        inventoryManager.openWindow(windowId, inventoryType, winTitle, numberOfSlots);
    }
    
    public byte getWindowId() {
        return this.windowId;
    }
    
    public byte getInventoryType() {
        return this.inventoryType;
    }
    
    public byte getNumberOfSlots() {
        return this.numberOfSlots;
    } 
    
    public int getEntityId() {
        //TODO: throw notHorseException
        return this.entityId;
    }
}
