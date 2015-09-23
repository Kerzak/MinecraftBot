/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraftbot.packet.in;

import java.io.IOException;
import java.util.LinkedList;
import minecraft.inventory.InventoryManager;
import minecraftbot.IInventoryHandler;
import minecraftbot.IInventoryStorage;
import minecraftbot.InventoryHandler;
import minecraftbot.Util;
import minecraftbot.logger.LogElement;
import minecraftbot.logger.LogLevel;
import minecraftbot.network.MinecraftDataInputStream;
import minecraftbot.packet.out.Out0EClickWindow;
import minecraftbot.packet.out.Out0EClickWindowInfo;

/**
 *
 * @author eZ
 */
public class In32ConfirmTransaction implements IInPacket{

    private final IInventoryStorage inventory;
//    private LinkedList<Out0EClickWindowInfo> notYetConfirmedClicks;
//    Out0EClickWindow out0EClickWindow;
    
    public In32ConfirmTransaction(IInventoryStorage inventory)
    {
        this.inventory = inventory;
//        this.notYetConfirmedClicks = new LinkedList<>();
//        this.out0EClickWindow = out0EClickWindow;
    }
    
//    public void newClick(Out0EClickWindowInfo click) {
//        this.notYetConfirmedClicks.addLast(click);
//    }
//    
//    private void confirmTransaction(int actionNumber) {
//        for (int i = 0; i < this.notYetConfirmedClicks.size(); i++) {
//            if(this.notYetConfirmedClicks.get(i).getActionNumber() == actionNumber) this.notYetConfirmedClicks.remove(i);
//        }
//    }
    
//    private void resend(int actionNumber) {
//        for (int i = 0; i < this.notYetConfirmedClicks.size(); i++) {
//            if(this.notYetConfirmedClicks.get(i).getActionNumber() == actionNumber) {
//                Out0EClickWindowInfo click = notYetConfirmedClicks.get(i);
//                out0EClickWindow.sendMessage(click.getWindowId(), click.getSlot(), (byte)0, click.getActionNumber(), (byte)0, click.getSlotData());
//            }
//        }
//    }
    
    @Override
    public void process(MinecraftDataInputStream in) throws IOException {
        System.out.println("PACKET WITH ID 32 RECIEVED");
        byte magic;
        boolean trueMagic;
        magic = in.readByte();
        Util.logger.log(LogElement.Inventory, LogLevel.Trace, "Magic number: "+magic);
        if(magic!=0)
            return;
        short actionNumber;
        actionNumber = (byte)in.readShort();
        trueMagic = in.readBoolean();
        Util.logger.log(LogElement.Inventory, LogLevel.Trace, "Action number: "+actionNumber);
        Util.logger.log(LogElement.Inventory, LogLevel.Trace, "Error: "+!trueMagic);
        System.out.println("PACKET WITH ID 32 RECIEVED, ACTION NUMBER: " + actionNumber + " ACCEPTED: " + trueMagic);
        if(trueMagic)  {
            //inventory.confirmTransaction(actionNumber);
        } 
        else
        {
            Util.logger.log(LogElement.Inventory, LogLevel.Error, "Inventory inconsistent");
            //if(inventory instanceof InventoryHandler)
            //    ((InventoryHandler)inventory).logInventory();
            //System.exit(0);
        }
    }
    
}
