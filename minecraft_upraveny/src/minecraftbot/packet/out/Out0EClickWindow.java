/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraftbot.packet.out;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import minecraft.inventory.Slot;
import minecraftbot.network.MinecraftOutputStream;

/**
 *
 * @author eZ
 */
public class Out0EClickWindow extends OutPacket{
    
    private int actionNumber = 0;

    public Out0EClickWindow(MinecraftOutputStream out) {
        super(out);
    }
    
    public int getActionNumber() {
        return this.actionNumber;
    }
    
    /***
     * Send to click on a slot in a window
     * @param windowId The id of the window which was clicked. 0 for player inventory.
     * @param slot The clicked slot.
     * @param button The button used in the click.
     * @param mode Inventory operation mode.
     * @param slotData Data od the clicked slot.
     */
    public void sendMessage(byte windowId, short slot, byte button, byte mode, byte[] slotData)
    {
        try {
            actionNumber++;
            out.writeByte((byte)0x0E);
            out.writeByte(windowId);
            out.writeShort(slot);
            out.writeByte(button);
            out.writeShort(actionNumber);
            out.writeByte(mode);
            out.write(slotData);
            out.closePacket();
        } catch (IOException ex) {
            System.err.println("IO Exception while requesting window click.");
            System.exit(0);
        }       
    }
    
    public void sendMessage(byte windowId, short slot, byte button, byte mode, Slot slotData)
    {
        try {
            ByteArrayOutputStream bOut = new ByteArrayOutputStream(7);
            DataOutputStream out1 = new DataOutputStream(bOut);
            out1.writeShort((short)slotData.getId().getValue());
            out1.writeByte((byte)slotData.getCount());
            out1.writeByte((byte)0);
            out1.writeByte(slotData.getMeta());
            out1.writeByte((byte)0xff);
            out1.writeByte((byte)0xff);
            actionNumber++;
            System.out.println("windowId: " + windowId + " slot: " + slot + " button: " + button + " action number: " + actionNumber + " mode: " + mode);
            System.out.println("SLOTDATA: ID: " + slotData.getId().getByteValue() + " COUNT: " + slotData.getCount() + " META: " + slotData.getMeta());
            
            out.writeByte((byte)0x0E);
            out.writeByte(windowId);
            out.writeShort(slot);
            out.writeByte(button);
            out.writeShort(actionNumber);
            out.writeByte(mode);
            out.write(bOut.toByteArray());
            out.closePacket();
            
//            //TEMP
//            out.writeByte((byte)0x0F);
//            out.writeByte(windowId);
//            out.writeShort(actionNumber);
//            out.writeBool(false);
//            out.closePacket();
//            //TEMP END
        } catch (IOException ex) {
            System.err.println("IO Exception while requesting window click.");
            System.exit(0);
        }       
    }
    
     public void sendMessage(byte windowId, short slot, byte button, int actionNumber, byte mode, Slot slotData)
    {
        try {
            ByteArrayOutputStream bOut = new ByteArrayOutputStream(7);
            DataOutputStream out1 = new DataOutputStream(bOut);
            out1.writeShort((short)slotData.getId().getValue());
            out1.writeByte((byte)slotData.getCount());
            out1.writeByte((byte)0);
            out1.writeByte(slotData.getMeta());
            out1.writeByte((byte)0xff);
            out1.writeByte((byte)0xff);
            System.out.println("windowId: " + windowId + " slot: " + slot + " button: " + button + " action number: " + actionNumber + " mode: " + mode);
            System.out.println("SLOTDATA: ID: " + slotData.getId().getByteValue() + " COUNT: " + slotData.getCount() + " META: " + slotData.getMeta());
            
            out.writeByte((byte)0x0E);
            out.writeByte(windowId);
            out.writeShort(slot);
            out.writeByte(button);
            out.writeShort(actionNumber);
            out.writeByte(mode);
            out.write(bOut.toByteArray());
            out.closePacket();
        } catch (IOException ex) {
            System.err.println("IO Exception while requesting window click.");
            System.exit(0);
        }            
    }
   
     public void sendMessageForceEmpty(byte windowId, short slot, byte button, byte mode)
    {
        try {
            ByteArrayOutputStream bOut = new ByteArrayOutputStream(2);
            DataOutputStream out1 = new DataOutputStream(bOut);
            out1.writeByte((byte)0xff);
            out1.writeByte((byte)0xff);
            actionNumber++;
            //System.out.println("windowId: " + windowId + " slot: " + slot + " button: " + button + " action number: " + actionNumber + " mode: " + mode);
            //System.out.println("SLOTDATA: ID: " + slotData.getId().getByteValue() + " COUNT: " + slotData.getCount() + " META: " + slotData.getMeta());
            
            out.writeByte((byte)0x0E);
            out.writeByte(windowId);
            out.writeShort(slot);
            out.writeByte(button);
            out.writeShort(actionNumber);
            out.writeByte(mode);
            out.write(bOut.toByteArray());
            out.closePacket();
        } catch (IOException ex) {
            System.err.println("IO Exception while requesting window click.");
            System.exit(0);
        }       
    }
}
