/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraftbot.packet.out;
import minecraftbot.network.MinecraftOutputStream;
/**
 *
 * @author Kerzak
 * 
 */
public class Out02UseEntity extends OutPacket {
    
    public Out02UseEntity(MinecraftOutputStream out) {
        super(out);
    }
    
    /**
     * 
     * @param target
     * @param mouse 0: interact, 1: attack, 2: interact at
     */
    public void sendMessage(int target, int mouse) {
        try {
            out.writeInt(target);
            out.writeInt(mouse);
        } catch (Exception e) {
            System.err.println("Error while using entity.");
        }
    }
    
    /**
     * 
     * Only if type is interact as(right click) => mouse == 2
     * 
     * @param target
     * @param mouse 0: interact, 1: attack, 2: interact at
     * @param x
     * @param y
     * @param z 
     */
    public void sendMessage(int target, float x, float y, float z) {
        try {
            out.writeInt(target);
            out.writeInt(2);
            out.writeFloat(x);
            out.writeFloat(y);
            out.writeFloat(z);
            out.closePacket();
        } catch (Exception e) {
            System.err.println("Error while using entity.");
        }
    }
    
}
