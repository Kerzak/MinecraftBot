package minecraftbot.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import minecraft.inventory.Slot;

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

    
  public void writeVarInt(int value) {
      //--------------1--------------
//      def pack_varint(val):
        //	total = b''
        //	if val < 0:
        //		val = (1<<32)+val
        //	while val>=0x80:
        //		bits = val&0x7F
        //		val >>= 7
        //		total += struct.pack('B', (0x80|bits))
        //	bits = val&0x7F
        //	total += struct.pack('B', bits)
//	return total
      
      //-----------------2-----------------
//      msb = (1 << 7)
//x = your value here
//
//while x > 127:
//    stream.write(msb | (x & 127))
//    x = x >> 7
//
//stream.write(x)
      System.out.println("In writeVarInt");
      byte msb = (byte) (1 << 7);
      while(value > 127) {
          try {
              writeByte((byte)(msb |(value & 0x7f)));
              System.out.println("VARINT_PART: " + (byte)(msb |(value & 0x7f)));
              value >>>= 7;
          } catch (IOException ex) {
              System.out.println("VARINT EXCEPTION1");
          }
      }
      try {
              writeByte((byte)(msb |(value & 0x7f)));
              System.out.println("VARINT_PART: " + (byte)(msb |(value & 0x7f)));
          } catch (IOException ex) {
              System.out.println("VARINT EXCEPTION2");
          }
              
  }
    
   /**
   * 
   */
  public void writeSignedVarInt(int value) throws IOException {
    // Great trick from http://code.google.com/apis/protocolbuffers/docs/encoding.html#types
      System.out.println("In writeSugnedVarInt, ");
    writeUnsignedVarInt((value << 1) ^ (value >> 31));
  
  }

  /**
   * 
   */
  public void writeUnsignedVarInt(int value) throws IOException {
    while ((value & 0xFFFFFF80) != 0L) {
      writeByte((byte)((value & 0x7F) | 0x80));
      value >>>= 7;
    }
    writeByte((byte)(value & 0x7F));
  }

  public void writeSlotData(Slot slotData) throws IOException {
      if (slotData.getId().getByteValue() == -1) {
          writeShort((short)-1);
      } else {
          writeShort((short)slotData.getId().getByteValue());
          writeByte((byte)slotData.getCount());
          writeByte((byte)0);
          writeShort(slotData.getMeta());
          writeByte((byte)0xff);
          writeByte((byte)0xff);
      }
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
