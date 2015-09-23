/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraftbot.build;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import java.util.HashSet;

/**
 *
 * @author student
 */
public class BuildResult {
    public final boolean completed;
    public final HashSet<Location> unfinished;

    public BuildResult(boolean completed, HashSet<Location> unfinished) {
        this.completed = completed;
        this.unfinished = unfinished;
    }

    
    
}
