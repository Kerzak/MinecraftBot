
package minecraftbot.network;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author eZ
 */
public class MinecraftDataInputStream extends DataInputStream{
    public MinecraftDataInputStream(InputStream in) {
        super(in);   
    }
    
    public final int readVarInt() throws IOException
    {
        int result=0, multiplier =1;
        byte temp[] = new byte[1];
        while(true)
        {
            read(temp, 0, 1);
            if(temp[0]<0)
            {
                result += (128+temp[0])*multiplier;
            }
            else
            {
                result += temp[0]*multiplier;
                break;
            }
            multiplier*=128;            
        }
        return result;
    }
    
    public final String readString() throws IOException
    {
        int length = readVarInt();
        byte[] b = new byte[length];
        readFully(b, 0, length);
        return new String(b, "UTF-8");        
    }
}
