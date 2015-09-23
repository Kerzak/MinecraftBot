
package minecraftbot.network;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author eZ
 */
public class MinecraftInputStream extends InputStream{

    private final InputStream source;
    private byte[] buffer;
    private MinecraftDataInputStream data;
    private int currentID;
    public MinecraftInputStream(InputStream source)
    {
        this.source = source;
    }
    
    public void loadBuffer(int i) throws IOException
    {
        buffer = new byte[i];
        source.read(buffer, 0, i);
        data = new MinecraftDataInputStream(new ByteArrayInputStream(buffer));

    }
    
    public int getCurrentID()
    {
        return currentID;
    }
    
    public int readLength() throws IOException
    {
        int result=0, multiplier =1;
        byte temp[] = new byte[1];
        while(true)
        {
            source.read(temp, 0, 1);
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
    
    public int loadByte() throws IOException
    {
        return data.readByte();        
    }
    
    @Override
    public int available() throws IOException
    {
        return source.available();
    }
    
    @Override
    public int read() throws IOException {
        return 0;
    }

    public MinecraftDataInputStream getDataStream() {
        return data;
    }
    
    
}
