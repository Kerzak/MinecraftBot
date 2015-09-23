/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraftbot.world;

import java.util.HashMap;
import minecraftbot.Id;
import minecraftbot.Util;
import minecraftbot.logger.LogElement;
import minecraftbot.logger.LogLevel;

/**
 *
 * @author eZ
 */
public class BlockFactory {

    HashMap<Integer,Block> created;
            
    public BlockFactory()
    {
        created = new HashMap<>();
    }
    
    public Block getBlock(Id id, byte meta)
    {
        int hashcode = id.getValue()*256+meta;
        
        if(!created.containsKey(hashcode))
        {
            Util.logger.log(LogElement.World, LogLevel.Debug, "Created entry in block factory: "+id+":"+meta);
            created.put(hashcode, new Block(id,meta));
        }
        return created.get(id.getValue()*256+meta);
    }
}
