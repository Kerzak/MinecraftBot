
package minecraftbot.packet.in;

import minecraftbot.world.Chunk;
import minecraftbot.world.WorldHandler;
import minecraftbot.network.MinecraftOutputStream;
import minecraftbot.network.MinecraftDataInputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 * Input packet handler.
 * refer to http://wiki.vg/Protocol
 */
public class In26MapChunkBulk implements IInPacketHandler{

    private final MinecraftOutputStream out;
    private final WorldHandler world;
    
    public In26MapChunkBulk(WorldHandler world,  MinecraftOutputStream out)
    {
        this.world = world;
        this.out = out;
    }
    
    @Override
    public void process(MinecraftDataInputStream in) throws IOException {
       // System.out.println("chunky");
        short chunkCount = in.readShort();
        int dataLength = in.readInt();
        boolean skyLight = in.readBoolean();
        byte[] data = new byte[dataLength];
        in.readFully(data);
        Inflater inflater = new Inflater();
        inflater.setInput(data);            

        int x, z;
        Chunk[] chunks = new Chunk[chunkCount];

        for(int i = 0; i < chunkCount; i++){
        x = in.readInt();
        z = in.readInt();
        int pmap = (in.readShort() & 0xffff);
        int amap = (in.readShort() & 0xffff);
        chunks[i] = new Chunk(x, z, pmap, amap, skyLight, true);   



        }

        int chunksize = 0;
        int extra = 0;
        //extra stuff we no care about
        //step 2, calculate the size of this buffer
        for(int i = 0; i < chunkCount; i++){
        chunksize += chunks[i].getBlockCount();
        extra += chunks[i].getBlockCount();          
        if(skyLight){
            extra += (chunks[i].getBlockCount() / 2);
        }
            extra += 256;
        }

        data = new byte[chunksize + extra];
        try{
            inflater.inflate(data);
        }catch(DataFormatException dataformatexception){
            throw new IOException("Bad compressed data format");
        }finally{
            inflater.end();
        }          

        for(int i = 0; i < chunkCount; i++){
            data = chunks[i].load(data);//takes what it needs and leaves the rest
            world.addChunk(chunks[i]);
        }
    }
}
