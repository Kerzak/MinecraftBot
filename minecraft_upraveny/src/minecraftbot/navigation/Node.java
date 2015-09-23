/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraftbot.navigation;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;

/**
 *
 * @author eZ
 */
public class Node {
    
    public final int x, y, z;
    public final float g;
    public final double score;
    public final Location location;
    public final NavPointType type;
    
    public Node(int x, int y, int z, float g, double h, NavPointType type)//, double distance)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.g = g;
        score = g + h;
        this.type = type;
        location = new Location(x+0.5,y,z+0.5);
        //System.out.println(this);
        //System.out.println(score);
        //this.distance = distance;
    }

    public Node(Location l)
    {
        location = l;
        type = NavPointType.KeyPoint;
        x = (int)l.x-(l.x<0?1:0);
        y = (int) l.y;
        z = (int)l.z-(l.z<0?1:0); 
        score = 0;
        g = 0;
    }
        
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Node)
        {
            Node other = (Node)obj;
            if(this.x==other.x&&this.y==other.y&&this.z==other.z)
            {
                if(this.type==NavPointType.KeyPoint||other.type==NavPointType.KeyPoint)
                    return true;
                return other.type==this.type;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Node @ "+x+ ", "+y+", "+z+" ["+type+"] >"+score;
    }

    @Override
    public int hashCode() {
        return x*7+y*17659+z*104543+(type==NavPointType.Jump?0:1);
    }
    
    
}
