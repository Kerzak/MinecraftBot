/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraftbot.world;

import minecraftbot.Id;
import minecraftbot.Util;
import minecraftbot.logger.LogElement;
import minecraftbot.logger.LogLevel;

/**
 * Cube made of 16x16x16 blocks.
 * @author eZ
 */
public class Cube {
    private final Block[][][] blocks;

    public Cube(byte[] blockInfo, byte[] metaInfo)
    {
        for (int i = 0; i < 2048; i++) {
          //  System.out.println(metaInfo[i]);
        }
        blocks = new Block[16][16][16];
        int p=0, pm=0;
        byte mask = (byte) 0x0f, meta;
        for (int y = 0; y < 16; y++) {
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    /*
                    if(blockInfo[p]!=0)
                        System.out.println(""+x+" "+y+" "+z);*/
                   // System.out.println(pm);
                    try
                    {
                        meta = (byte)(metaInfo[pm]&mask);
                        if(mask<0)
                            meta = (byte)(meta>>4);
                        if(metaInfo[pm]!=0)
                        {
                       //     System.out.println(x+" "+y+" "+z+" - "+p + " is "+blockInfo[p]+" "+meta+"   mask "+mask);                    
                        }
                        blocks[x][y][z] = Util.blockFactory.getBlock(Util.idMap.get((int)blockInfo[p]+(blockInfo[p]<0?256:0)), meta);//todo meta
                    }
                    catch(NullPointerException e)
                    {
                        Util.logger.log(LogElement.World, LogLevel.Error, "Item id "+((int)blockInfo[p]+(blockInfo[p]<0?256:0))+" is not supported yet.");
                        System.out.println(x+" "+y+" "+z+" - "+p + " is "+blockInfo[p]);
                        throw new IllegalAccessError();
                       // System.exit(0);
                    }
                    p++;
                    if(mask!=0x0f)
                    {
                        pm++;
                    }
                    mask = (byte)(mask ^0xff);
                }
            }
        }
    }

    
    public Cube()
    {
        blocks = new Block[16][16][16];
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (int k = 0; k < 16; k++) {
                    blocks[i][j][k] = Util.blockFactory.getBlock(Id.Air, (byte)0);
                }
            }
        }
    }

    public Block getBlock(int x, int y, int z) {
        return blocks[x][y][z];
    }

    void setBlock(int x, int y, int z, int id, byte meta) {
        if(x<0)
            System.out.println("X is "+x);
        if(y<0)
            System.out.println("Y is "+y);
        if(z<0)
            System.out.println("Z is "+z);
        blocks[x][y][z] = Util.blockFactory.getBlock(Util.idMap.get(id), meta);
    }

    
    

}
