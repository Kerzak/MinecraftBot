/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraftbot.build;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import java.util.Comparator;
import java.util.List;
import minecraftbot.IWorldView;
import minecraftbot.Id;
import minecraftbot.Locomotion;
import minecraftbot.Util;
import minecraftbot.logger.LogElement;
import minecraftbot.logger.LogLevel;
import minecraftbot.navigation.Navigation;

/**
 *
 * @author eZ
 */
public class AdvancedBuilding {
    
    private final IWorldView world;
    private final Building build;
    private final Navigation navigation;
    private final Locomotion move;
    
    public AdvancedBuilding(IWorldView world, Building build, Navigation navigation, Locomotion move)
    {
        this.world = world;
        this.build = build;
        this.navigation = navigation;
        this.move = move;
    }

    public void buildStructure(Structure structure, Location loc)
    {
        Location real = Util.blockLocation(loc);
        buildStructure(structure, (int)real.x, (int)real.y, (int)real.z);
    }
    
    public void buildStructure(Structure structure, int x, int y, int z)
    {
        BlockPlanComparator comparator = new BlockPlanComparator(move.getLocation(), new Location(x, y, z));
        List<BlockPlan> blocklist = structure.getBlockPlan(comparator, world, x, y, z);
//        System.out.println("\tVYSLEDOK");
        for (BlockPlan bPlan : blocklist) {
            build.block(bPlan, x, y, z);
  //          Util.logger.log(LogElement.Building, LogLevel.Info, bPlan+ "  -  "+dist(bPlan, move.getLocation()));
        }
    }
    
    class BlockPlanComparator implements Comparator<BlockPlan>
    {

        private final Location pLoc;
        private final Location sLoc;
        
        public BlockPlanComparator(Location pLoc, Location sLoc)
        {
            this.pLoc = pLoc;
            this.sLoc = sLoc;
        }
        
        @Override
        public int compare(BlockPlan b1, BlockPlan b2) {/*
            System.out.println("B1 loc "+b1.getLocation().add(sLoc));
            System.out.println("B2 loc "+b2.getLocation().add(sLoc));
            System.out.println("RESULT: "+(int)((Location.getDistance(b1.getLocation().add(sLoc), pLoc)-Location.getDistance(b2.getLocation().add(sLoc), pLoc))*1000));*/
            return (int)((Location.getDistance(b2.getLocation().add(sLoc), pLoc)-Location.getDistance(b1.getLocation().add(sLoc), pLoc))*1000);
            //return (int)((Math.abs(b1.x-loc.x)+Math.abs(b1.y-loc.y)+Math.abs(b1.z-loc.z))-(Math.abs(b2.x-loc.x)+Math.abs(b2.y-loc.y)+Math.abs(b2.z-loc.z)));
        }
        
    }
    
}
