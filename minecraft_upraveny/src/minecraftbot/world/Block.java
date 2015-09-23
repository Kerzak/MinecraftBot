/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraftbot.world;

import minecraftbot.Id;
import minecraftbot.Util;

/**
 * 
 * @author eZ
 */
public class Block {
    private Id id;
    private byte meta;
    
    public Block(Id id, byte meta){
        //System.out.print(id);
        this.id = id;
        this.meta = meta;
    }

    public Block(int id, byte meta) 
    {
        this(Util.idMap.get(id), meta);
    }
    
    public Block() {
        id = Id.AIR;
        meta = 0;
    }

    public Id getId() {
        return id;
    }

    public boolean isEmpty() {
        //System.out.println(id);
        return id==Id.AIR;
    }

    public byte getMeta() {
        return meta;
    }
    
    public void set(Id id, byte meta){
        this.id = id;
        this.meta = meta;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Block)
        {
            Block other = (Block)obj;
            if(id.getValue()>255&&other.getId().getValue()>255)
                return id.equals(other.id);
            else
                return id.equals(other.id)&&meta==other.getMeta();
                
        }
        else
            return false;
    }

    @Override
    public int hashCode() {
        return id.getValue();
    }

    @Override
    public String toString() {
        return "BLOCK "+id+":"+meta;
    }
    
    
    
    
}
