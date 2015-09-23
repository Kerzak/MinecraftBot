/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraftbot.packet.out;

import minecraftbot.network.MinecraftOutputStream;

/**
 *
 * @author eZ
 */
public class OutPacket {
    
    protected MinecraftOutputStream out;
    
    public OutPacket(MinecraftOutputStream out)
    {
        this.out = out;
    }
    
}
