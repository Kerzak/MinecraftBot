/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraftbot.world;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import minecraftbot.Id;
import minecraftbot.Util;

/**
 * Cube made of 16x16x16 blocks.
 * @author eZ
 */
public class Cube {
    private final Block[][][] blocks;

    public Cube(byte[] blockInfo, byte[] metaInfo)
    {
        blocks = new Block[16][16][16];
        int p=0;
        for (int y = 0; y < 16; y++) {
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    /*
                    if(blockInfo[p]!=0)
                        System.out.println(""+x+" "+y+" "+z);*/
                    blocks[x][y][z] = new Block(Util.idMap.get((int)blockInfo[p]), (byte)0);//todo meta
                    p++;
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
                    blocks[i][j][k] = new Block();
                }
            }
        }
    }

    public Block getBlock(int x, int y, int z) {
        try{
                return blocks[x][y][z];
        }
        catch(Exception e)
        {
                System.out.println(x);
                System.out.println(y);
                System.out.println(z);    
        }
        return null;
    }
    
    

}
