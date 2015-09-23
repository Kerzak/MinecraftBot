/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraftbot.entity;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;

/**
 *
 * @author eZ
 */
public class Player extends Entity {

    private final String name;
    
    public Player(String name, int ID,  Location location)
    {
        super(ID, 1, location);
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
    
}