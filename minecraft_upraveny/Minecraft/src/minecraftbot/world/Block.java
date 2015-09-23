/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraftbot.world;

import minecraftbot.Id;
import minecraftbot.ItemType;
import minecraftbot.Util;

/**
 * 
 * @author eZ
 */
public class Block {
    private Id id;
    private byte meta;
    
    public Block(Id id, byte meta){
//        System.out.print(id);
 //       if(id==null)
 //           throw  new NullPointerException("ID of the block can't be null");
        this.id = id;
        this.meta = meta;
    }

    public Block(int id, byte meta) 
    {
        this(Util.idMap.get(id), meta);
        if(Util.idMap.get(id)==null)
            System.err.println("*WARNING* "+id);
    }
    
    public Block() {
        id = Id.Air;
        meta = 0;
    }

    public Id getId() {
        return id;
    }

    public boolean isEmpty() {
        //System.out.println(id);
        return id==Id.Air||id==Id.None;
    }
    
    public boolean isPassable()
    {
        return isEmpty()
                ||id.getType()==ItemType.Plant
                ||id.getType()==ItemType.Leaves
                ||(id.getType()==ItemType.Door&&meta>=4&&meta!=8);
    }
    
    public boolean isTranslucent()
    {
        return isEmpty();
    }

    public byte getMeta() {
        return meta;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Block)
        {
            Block other = (Block)obj;
                return id.equals(other.id)&&meta==other.getMeta();
                
        }
        else
            return false;
    }

    @Override
    public int hashCode() {
        return id.getValue()*256 + meta;
    }

    @Override
    public String toString() {
        return "BLOCK "+id+":"+meta;
    }
    
    
    
    
}
