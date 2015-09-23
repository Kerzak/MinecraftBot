package minecraftbot.world;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import java.util.Collection;
import java.util.HashSet;
import minecraftbot.Id;
import minecraftbot.Util;

/**
 * Chunk, structure made of 16*256*16 blocks.
 * @author eZ
 */
public class Chunk {
    private final boolean groundUp, skylight;
    private final int x, z, pBitMap, aBitMap;
    private final Cube[] cubes;
    private byte[] blocks;
    private byte[] blockmeta;
    
    private int blockCount, ablocks;
    
    public Chunk(int x, int z, int pBitMap, int aBitMap, boolean skylight, boolean groundUp) {
        // Create chunk sections.
        this.x = x;
        this.z = z;
        this.pBitMap = pBitMap;
        this.aBitMap = aBitMap;
        this.groundUp = groundUp;
        this.skylight = skylight;

        cubes = new Cube[16];
        
        blockCount = 0;
        ablocks = 0;
        for (int i = 0; i < 16; i++) {
            if ((pBitMap & (1 << i)) != 0) {
                blockCount++; // "parts of the chunk"
            }
        }

        for (int i = 0; i < 16; i++) {
            if ((pBitMap & (1 << i)) == 1) {
                ablocks++;
            }
        }
        //System.out.println("ChunkCount " + x + "x"+z+" : " + blockCount);

        blockCount = blockCount * 4096;
    }

    public int getBlockCount() {
        return blockCount;
    }
    
    public byte[] load(byte[] data)
    {
        blocks = new byte[blockCount];
        byte[] leftData;
        blockmeta = new byte[blockCount/2];
        int removeable = blockCount;

        if (skylight)
            removeable += (blockCount / 2);

        if (groundUp)
            removeable += 256;
        
        System.arraycopy(data, 0, blocks, 0, blockCount);
        /*System.out.println(""+data.length + " "+blockCount+" "+blockmeta.length);
        System.out.println("To remove   :"+(blockCount + removeable));
        System.out.println("Should left :"+(data.length - (blockCount + removeable)));
        System.out.println();*/
        System.arraycopy(data, blockCount, blockmeta, 0, blockCount/2);
        /*for(int i = 0; i < blocks.length; i++){
            System.out.println("id: " + blocks[i]);
        }*/
        //System.out.println(deCompressed.length);
        //System.out.println("Blocknum" + blocknum + " block num AND removeable: " + (blocknum + removeable));
        leftData = new byte[data.length - (blockCount + removeable)];     
        System.arraycopy(data, (blockCount + removeable), leftData, 0, leftData.length);

        int offset = 0;
        int metaoff = 0;
        
        for (int i = 0; i < 16; i++) {
            if ((pBitMap & (1 << i)) != 0) {

                byte[] temp = new byte[4096];
                byte[] temp2 = new byte[2048];

                System.arraycopy(blocks, offset, temp, 0, 4096);
                System.arraycopy(blockmeta, metaoff, temp2, 0, 2048);
                try
                {
                    cubes[i] = new Cube(temp, temp2);
                }
                catch(IllegalAccessError e)
                {
                    System.out.println(x+" "+i+" "+z);
                    System.exit(0);
                }
                offset += 4096;
                metaoff += 2048;
            }
            else
            {
                //cubes[i] = null;
                cubes[i] = new Cube();
            }
        }
        

        return leftData;
        
    }
    
    public Block getBlock(int x, int y, int z)
    {
//        System.out.println("Chunk : "+this.x+"x"+this.z);
        if(x<0) x+=16;
        if(z<0) z+=16;
//        System.out.println(x+" - "+y+" - "+z);
        if(cubes[y/16] != null)
        {
            return cubes[y/16].getBlock(x, y%16, z);
        }
        else 
            return Util.blockFactory.getBlock(Id.Air, (byte)0);
    }
    

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    Collection<Location> getResourceLocations(Id id) {
        Collection<Location> locs = new HashSet<>();

        for (int h = 0; h<16;h++) {
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++)
                {
                    for (int k = 0; k < 16; k++) {
                        if(cubes[h]!=null&&cubes[h].getBlock(i, j, k).getId().equals(id))
                        {
                          locs.add(Util.blockLocation(new Location(x*16+i,h*16+j,z*16+k)));
                        }
                    }
                }
            }
        }
        
        return locs;
    }

    void setBlock(int x, int y, int z, int id, byte meta) {
        if(cubes[y/16]==null)
            cubes[y/16] = new Cube();
        cubes[y/16].setBlock(x, y%16, z, id, meta);
    }

}
