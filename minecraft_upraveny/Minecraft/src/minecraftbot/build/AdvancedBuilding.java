/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraftbot.build;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import minecraftbot.IWorldView;
import minecraftbot.Locomotion;
import minecraftbot.Util;
import minecraftbot.navigation.Navigation;

/**
 * Allow building of complex structures.
 */
public class AdvancedBuilding {
    
    private final IWorldView world;
    private final Building build;
    private final Navigation navigation;
    private final Locomotion move;
    
    private final List<BlockPlan> plan;
    private BlockPlan currentPlan;
    
    private boolean navigating=false, building=false, finished=true;
    
    public AdvancedBuilding(IWorldView world, Building build, Navigation navigation, Locomotion move)
    {
        this.world = world;
        this.build = build;
        this.navigation = navigation;
        this.move = move;
        plan = new LinkedList<>();
    }

    /**
     * Builds structure on target location.
     * @param structure Structure to build.
     * @param loc Target Location.
     */
    public void buildStructure(Structure structure, Location loc)
    {
        Location real = Util.blockLocation(loc);
        buildStructure(structure, (int)real.x, (int)real.y, (int)real.z);
    }
    
  
    /**
     * Builds structure on target location.
     * @param structure Structure to build.
     * @param x X coordinate of target location.
     * @param y Y coordinate of target location.
     * @param z Z coordinate of target location.
     */
    public void buildStructure(Structure structure, int x, int y, int z)
    {
        BlockPlanComparator comparator = new BlockPlanComparator(move.getLocation(), new Location(x, y, z));
        List<BlockPlan> blocklist = structure.getBlockPlan(comparator, world, x, y, z);

        plan.addAll(blocklist);
    }
    
    public void update()
    {
        if(!plan.isEmpty())
        {
            if(navigating)
            {
                if(!navigation.isNavigating())
                {
                    navigating = false;
                }
            }
            
            if(building)
            {
                if(!build.isBuilding())
                {
                    building = false;
                    finished = true;
                }
            
            }
            
            if(!building&&!navigating)
            {
                if(finished)
                {
                    currentPlan = plan.get(0);
                    System.out.println("will build "+currentPlan.x+" "+currentPlan.y+" "+currentPlan.z);
                    plan.remove(0);
                    finished = false;
                }
                if(build.checkRange(currentPlan))
                {
                    if(Util.equalBlockLocation(move.getLocation(),currentPlan.getLocation())
                            ||Util.equalBlockLocation(move.getLocation().addY(1),currentPlan.getLocation()))
                    {
                        move.stepAside();
                    }
                    else
                    {
                        building = true;
                        build.block(currentPlan);
                    }
                }
                else
                {
                    navigating = true;
                    navigation.navigate(currentPlan,5);
                }
            }
        }
    }
       
    
    private class BlockPlanComparator implements Comparator<BlockPlan>
    {

        private final Location pLoc;
        private final Location sLoc;
        
        public BlockPlanComparator(Location pLoc, Location sLoc)
        {
            this.pLoc = pLoc;
            this.sLoc = sLoc;
        }
        
        @Override
        public int compare(BlockPlan b1, BlockPlan b2) {
            return (int)((Location.getDistance(b2.getLocation().add(sLoc), pLoc)-Location.getDistance(b1.getLocation().add(sLoc), pLoc))*1000);
        }
        
    }

}
