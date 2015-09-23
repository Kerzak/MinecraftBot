/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraftbot.packet.out;

import java.io.IOException;
import minecraftbot.network.MinecraftOutputStream;

/**
 *
 * @author eZ
 */
public class Out0EClickWindow extends OutPacket{

    public Out0EClickWindow(MinecraftOutputStream out) {
        super(out);
    }
    
    /***
     * Send to click on a slot in a window
     * @param windowType The id of the window which was clicked. 0 for player inventory.
     * @param slot The clicked slot.
     * @param button The button used in the click.
     * @param actionNumber A unique number for the action, used for transaction handling.
     * @param mode Inventory operation mode.
     * @param slotData Data od the clicked slot.
     */
    public void sendMessage(byte windowType, short slot, byte button, short actionNumber, byte mode, byte[] slotData)
    {
        try {
            out.writeByte((byte)0x0E);
            out.writeByte(windowType);
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
}
