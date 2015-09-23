package minecraftbot.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author eZ
 */
public class MinecraftOutputStream extends OutputStream{

    OutputStream source;
    DataOutputStream data;
    ByteArrayOutputStream buffer;
    int length;
    
    public MinecraftOutputStream(OutputStream source)
    {
        this.source = source;
        length = 0;
        buffer = new ByteArrayOutputStream();
        data = new DataOutputStream(buffer);
    }

    public void writeString(String str) throws IOException
    {
        byte[] stringBuffer = str.getBytes("UTF-8");
        data.write(stringBuffer.length);
        data.write(stringBuffer);
    }
    
    public void writeShort(int s) throws IOException
    {
        data.writeShort(s);
    }
    
    public void writeDouble(double d) throws IOException
    {
        data.writeDouble(d);
    }
    
    public void writeFloat(float f) throws IOException
    {
        data.writeFloat(f);
    }
    
    public void writeInt(int v) throws IOException
    {
        data.writeInt(v);
    }
    
    public void writeBool(boolean b) throws IOException
    {
        data.writeBoolean(b);
    }
    
    public void writeByte(Byte b) throws IOException
    {
        data.write((byte)b);
    }
    
    @Override
    public void write(int b) throws IOException {
        data.write(b);      
    }

    @Override
    public void write(byte[] b) throws IOException {
        data.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        data.write(b, off, len);
    }

    
    
    public void closePacket() throws IOException
    {
        source.write(buffer.size());
        source.write(buffer.toByteArray());
        buffer.reset();
    }
    
    @Override
    public void flush() throws IOException {
        source.flush();
    }

    
  
    
}
