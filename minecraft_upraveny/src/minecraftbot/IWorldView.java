/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraftbot;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import minecraftbot.world.Block;

/**
 *
 * @author eZ
 */
public interface IWorldView {
    public Block getBlock(Location location);
    public Block getBlock(double x, double y, double z);
    public Block getBlockUnder(Location location);
    public Block getBlock(int x, int y, int z);
    public Location getClosestResource(Id resource, Location location);
    
}
