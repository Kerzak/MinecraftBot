/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraftbot.examples;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import java.util.HashMap;
import java.util.HashSet;
import minecraftbot.Util;
import minecraftbot.world.Block;

/**
 *
 * @author student
 */
public class TestClass {
    public static void main(String[] args)
    {
        Util.Load();
        Block a,b;
        a = new Block(78, (byte)8);
        b = new Block(78, (byte)6);
        HashSet<Block> set =  new HashSet<>();
        set.add(a);
        System.out.println(set.contains(b));
        
    }
}
