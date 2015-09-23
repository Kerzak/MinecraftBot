/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraftbot.packet.out;

import java.io.IOException;
import java.security.InvalidParameterException;
import minecraftbot.network.MinecraftOutputStream;

/**
 *
 * @author eZ
 */
public class Out09HeldItemChange extends OutPacket{

    public Out09HeldItemChange(MinecraftOutputStream out) {
        super(out);
    }
    
    /**
     * Sent to change the player's slot selection.
     * @param slot The slot which the player has selected (0–8)
     */
    public void sendMessage(int slot)
    {
        if(slot<0||slot>8)
            throw  new InvalidParameterException("Slot number must be in range <0,8>, is "+slot );
        try {

            out.writeByte((byte)9);
            out.writeShort(slot);
            out.closePacket();
        } catch (IOException ex) {
            System.err.println("Error while changing held item.");
            System.exit(0);
        }        
    }
}
