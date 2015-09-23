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
public class NavPoint {
    final Location location;
    
    public NavPoint(Location location)
    {
        this.location = location;
    }
}
