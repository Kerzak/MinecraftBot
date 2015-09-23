
package minecraftbot.packet.in;

import minecraftbot.Locomotion;
import minecraftbot.network.MinecraftOutputStream;
import java.io.IOException;
import minecraftbot.network.MinecraftDataInputStream;

/**
 * Input packet handler.
 * refer to http://wiki.vg/Protocol
 */
public class In08PlayerPositionAndLook implements IInPacketHandler{

    private final MinecraftOutputStream out;
    private final Locomotion move;
    
    public In08PlayerPositionAndLook(Locomotion move,  MinecraftOutputStream out)
    {
        this.move = move;
        this.out = out;
    }
    
    @Override
    public void process(MinecraftDataInputStream in) throws IOException {
        double[] location = new double[3];
        float[] rotation = new float[2];
        for (int i = 0; i < 3; i++) {
            location[i] = in.readDouble();
            //System.out.println(location[i]);
        }
        
        location[1]-=1.62;
        rotation[0] = in.readFloat();
        rotation[1] = in.readFloat();
        boolean onGround = in.readBoolean();

        out.write(6);
        out.writeDouble(location[0]);
        out.writeDouble(location[1]);
        out.writeDouble(location[1]+1.62);
        out.writeDouble(location[2]);
        out.writeFloat(rotation[0]);
        out.writeFloat(rotation[1]);
        out.writeBool(onGround);
        out.closePacket();
        
        move.setRotation(rotation);
        move.setLocation(location);
        move.stuck = true;
        
    }
    
}
