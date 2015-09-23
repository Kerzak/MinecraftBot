/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraftbot.navigation;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import minecraftbot.ILocated;
import minecraftbot.IWorldView;
import minecraftbot.ItemType;
import minecraftbot.Locomotion;
import minecraftbot.Mining;
import minecraftbot.Util;
import minecraftbot.logger.LogElement;
import minecraftbot.logger.LogLevel;

/**
 * Allows bot to find and follow paths around the enviroment.
 * @author eZ
 */
public class Navigation {
    
    private final IWorldView world;
    private Thread calculatingThread;
    private NavigationState state;
    private final Locomotion move;
    private final Mining mining;
    private Location targetLocation;
    private final NodeComparator comparator;
    private LinkedList<Node> path;
    private boolean navigating = false, jumping = false, success = true;
    private static final float help = 0.5857864376269049f;
    private final float movementSpeed = 0.4f;
    private final HashMap<int[],int[][]> jumpMap;
  
    public Navigation(IWorldView world, Locomotion move, Mining mining)
    {
        this.world = world;
        this.move = move;
        this.mining = mining;
        comparator = new NodeComparator();
        
        state = NavigationState.Idle;
        
        jumpMap = new HashMap<>();
        
        jumpMap.put(new int[]{0,2}, new int[][]{{0,1}});
        jumpMap.put(new int[]{1,2}, new int[][]{{0,1},{1,1}});
        jumpMap.put(new int[]{2,1}, new int[][]{{1,0},{1,1}});
        jumpMap.put(new int[]{2,0}, new int[][]{{1,0}});
        jumpMap.put(new int[]{2,-1}, new int[][]{{1,0},{1,-1}});
        jumpMap.put(new int[]{1,-2}, new int[][]{{0,-1},{1,-1}});
        jumpMap.put(new int[]{0,-2}, new int[][]{{0,-1}});
        jumpMap.put(new int[]{-1,-2}, new int[][]{{0,-1},{-1,-1}});
        jumpMap.put(new int[]{-2,-1}, new int[][]{{-1,-1},{-1,0}});
        jumpMap.put(new int[]{-2,0}, new int[][]{{-1,0}});
        jumpMap.put(new int[]{-2,1}, new int[][]{{-1,1},{-1,0}});
        jumpMap.put(new int[]{-1,2}, new int[][]{{-1,1},{0,1}});

        jumpMap.put(new int[]{0,3}, new int[][]{{0,1},{0,2}});
        jumpMap.put(new int[]{0,-3}, new int[][]{{0,-1},{0,-2}});
        jumpMap.put(new int[]{3,0}, new int[][]{{1,0},{2,0}});
        jumpMap.put(new int[]{-3,0}, new int[][]{{-1,0},{-2,0}});

        jumpMap.put(new int[]{1,3}, new int[][]{{0,1},{0,2},{1,1},{1,2}});
        jumpMap.put(new int[]{1,-3}, new int[][]{{0,-1},{0,-2},{1,-1},{1,-2}});
        jumpMap.put(new int[]{3,1}, new int[][]{{1,0},{2,0},{1,1},{2,1}});
        jumpMap.put(new int[]{-3,1}, new int[][]{{-1,0},{-2,0},{-1,1},{-2,1}});
        
        jumpMap.put(new int[]{-1,3}, new int[][]{{0,1},{0,2},{-1,1},{-1,2}});
        jumpMap.put(new int[]{-1,-3}, new int[][]{{0,-1},{0,-2},{-1,-1},{-1,-2}});
        jumpMap.put(new int[]{3,-1}, new int[][]{{1,0},{2,0},{1,-1},{2,-1}});
        jumpMap.put(new int[]{-3,-1}, new int[][]{{-1,0},{-2,0},{-1,-1},{-2,-1}});
        
        

    }
    
    /**
     * Stops the navigation.
     */
    public void stop()
    {
        navigating = false;
    }
    
    /**
     * Navigates to location.
     * @param targetLocation Target location.
     */
    public void navigate (Location targetLocation)
    {
        navigate(targetLocation, 0);
    }
    
    /**
     * Navigates to location of target.
     * @param target Target.
     */
    public void navigate (ILocated target)
    {
        navigate(target.getLocation(), 0);
    }    
    
    /**
     * Navigates to location that is close to target location.
     * @param targetLocation Target location.
     * @param range How close bot has to be.
     */
    public void navigate(Location targetLocation, int range)
    {
        startNavigation(targetLocation, range, false);
    }
    
    /**
     * Navigates close to the target.
     * @param target Target.
     * @param range How close bot has to be.
     */
    public void navigate(ILocated target, int range)
    {
        startNavigation(target.getLocation(), range, false);
    }
    
    /**
     * Navigates to location that is close to target location.
     * @param x X coordinate of target location.
     * @param y Y coordinate of target location.
     * @param z Z coordinate of target location.
     * @param range How close bot has to be.
     */
    public void navigate(int x, byte y, int z, int range)
    {
        startNavigation(new Location(x, y, z), range, false);
    }
    
    /**
    * Navigates to location that is close to target location.
     * @param x X coordinate of target location.
     * @param y Y coordinate of target location.
     * @param z Z coordinate of target location.
     */
    public void navigate(int x, int y, int z)
    {
        startNavigation(new Location(x, y, z), 0, false);
    }
    
    /**
     * Navigates close to a tree.
     * @param targetLocation Location of the tree. 
     */
    public void navigateToTree(Location targetLocation)
    {
        startNavigation(targetLocation, 2, true);
    }
    
    /**
     * Navigates close to a tree.
     * @param target Location of the tree. 
     */
    public void navigateToTree(ILocated target)
    {
        startNavigation(target.getLocation(), 2, true);
    }

    private void startNavigation (final Location targetLocation, final int range, final boolean toTree)
    {
        navigating = false;
        if(state == NavigationState.Calculating)
        {
            calculatingThread.stop();
        }
        
        
        calculatingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                calculatePath(targetLocation, range, toTree);
            }
            }
        );
        
        calculatingThread.start();
    }
    
    private void calculatePath (Location targetLocation, int range, boolean toTree)
    {
        state = NavigationState.Calculating;
        Util.logger.log(LogElement.Navigation, LogLevel.Info, "Computing path.");
        this.targetLocation = targetLocation;
        if(!toTree)
        {
            while(!world.getBlock(targetLocation).isPassable())
            {
                targetLocation = targetLocation.addY(1);
            }
            while(world.getBlockUnder(targetLocation).isPassable())
            {
                targetLocation = targetLocation.addY(-1);
            }
        }
        ArrayList<Node> openNodes = new ArrayList<>(), closedNodes = new ArrayList<>();
        path = new LinkedList<>();
        HashMap<Node, Node> cameFrom = new HashMap<>();
        Node current, target = new Node(targetLocation),
                start = new Node(move.getLocation());
        
        openNodes.add(start);
        while (openNodes.size()>0) {            
            current = openNodes.remove(0);            
            
            if(current.equals(target)||(current.score-current.g<=range||(toTree&&current.score-current.g<5&&isTreeClose(current,target)))&&!current.equals(start))
            {
                path = new LinkedList<>();

                buildPath(current, start, cameFrom);
                
                break;
            }
            
            closedNodes.add(current);
            for(Node neighbour : neighboursOf(current))
            {
                if(closedNodes.contains(neighbour))
                {
                    continue;
                }
                
                if(!openNodes.contains(neighbour))
                {
                    
                    int index = 0;
                    for(Node node : openNodes)
                    {
                        if(node.score>neighbour.score)
                            break;
                        index++;
                    }
                    openNodes.add(index, neighbour);
                    cameFrom.put(neighbour, current);
                }
                
                
            }
                 
        }
        
        if(path.isEmpty())
        {
            targetLocation = null;
            success = false;
            Util.logger.log(LogElement.Navigation, LogLevel.Error, "Path computation failed.");
            return;
        }
        
        Util.logger.log(LogElement.Navigation, LogLevel.Info, "Computation completed.");
        success = true;
        
        for(Node n : path)
        {
           // System.out.println(n);
        }
        
        state = NavigationState.Finished;
    }
   
    /**
     * @param n Examined node.
     * @return All neighbour nodes where bot can move.
     */
    public ArrayList<Node> neighboursOf(Node n)
    {
        ArrayList<Node> result = new ArrayList<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                float g = n.g+i*i+j*j-i*j*i*j*help;
                
                if(i!=0||j!=0)
                {
                    for(int k = 0;k>-3;k--)
                    {
                        if(n.y+k<0)
                            continue;
                        if(n.y>0&& !world.getBlock(n.x+i, n.y+k, n.z+j).isPassable()
                                &&world.getBlock(n.x+i, n.y+k+1, n.z+j).isPassable()
                                &&world.getBlock(n.x+i, n.y+k+2, n.z+j).isPassable())
                        {
                            if(k==0)
                            {
                                if(!world.getBlock(n.x, n.y+k+2, n.z).isPassable())
                                    continue;
                            }
                            if(i!=0&&j!=0)
                            {
                                if(!world.getBlock(n.x+i, n.y+k+1, n.z).isPassable()
                                   || !world.getBlock(n.x+i, n.y+k+2, n.z).isPassable()
                                   || !world.getBlock(n.x, n.y+k+1, n.z+j).isPassable()
                                   || !world.getBlock(n.x, n.y+k+2, n.z+j).isPassable())
                                {
                                    break;
                                }
                            }
                            result.add(new Node(n.x+i,n.y+k+1,n.z+j,g,Location.getDistance(targetLocation, new Location(n.x+i, n.y+k+1, n.z+j)),NavPointType.Walk));   
                            break;
                        }
                    }
                }
            }
        }
        int k = 0;
        boolean canJump;
        for (Map.Entry<int[], int[][]> entry : jumpMap.entrySet()) {
            int[] t = entry.getKey();
            int[][] p = entry.getValue();
            float g = (n.g+t[0]*t[0]+t[1]*t[1]);
            canJump = true;
            if(!world.getBlock(n.x+t[0],n.y-1+k,n.z+t[1]).isPassable()&&isThereSpaceToJump(n.x+t[0],n.y,n.z+t[1]))
            {
                for (int[] j : p) {
                    if(!isThereSpaceToJump(n.x+j[0],n.y+k,n.z+j[1]))
                    {
                        canJump = false;
                    }
                }
            }
            else 
                canJump = false;
            
            if(canJump)
            {
                result.add(new Node(n.x+t[0],n.y,n.z+t[1],g,Location.getDistance(targetLocation, new Location(n.x+t[0], n.y+k, n.z+t[1])),NavPointType.Jump));
            }
            
        }
        for (int[] o : new  int[][]{{0,1},{0,-1},{1,0},{-1,0}})
        {
            for (k = 0; k < 2; k++) {
                if(world.getBlock(n.x+o[0], n.y+k, n.z+o[1]).getId().getType().equals(ItemType.Door))
                {
                    float g = n.g+o[0]*o[0]+o[1]*o[1]+help;
                    result.add(new Node(n.x+o[0], n.y+k, n.z+o[1],g,Location.getDistance(targetLocation, new Location(n.x+o[0], n.y+k, n.z+o[1])),NavPointType.Door));
                }
            }
        }
        return result;
    }
    
    private boolean isThereSpaceToJump(int x, int y, int z)
    {
        for (int i = 0; i < 4; i++) {
            if(!world.getBlock(x, y, z).isPassable())
            {
                return false;
            }
        }
        return true;
    }
    
    public void update()
    {
        if(state==NavigationState.Finished)
        {
            navigating = true;
            state = NavigationState.Idle;
        }
        
        if(navigating)
        {
            if(path.isEmpty())
            {
                stop();
                return;
            }
            move.lookAt(targetLocation);

            Node node = path.getLast();
            Location nextLocation = node.location, currentLocation = move.getLocation();
            double distance = Location.getDistance(nextLocation, currentLocation);
            if(distance<0.01)
            {
                path.removeLast();
                if(path.isEmpty())
                {
                    stop();
                    return;
                }
                nextLocation = path.getLast().location;
                for (int x = -1; x < 2; x++) {
                    for (int z = -1; z < 2; z++) {
                        for (int y = 0; y < 3; y++) {
                            if(world.getBlock(nextLocation.addXYZ(x,y,z)).getId().getType().equals(ItemType.Leaves))
                            {
                                mining.mine(nextLocation.addXYZ(x,y,z));
                            }
                        }
                    }
                }
            }
            if(!mining.isMining()&&nextLocation.y>move.getLocation().y)
            {
                for (int x = -1; x < 2; x++) {
                    for (int z = -1; z < 2; z++) {
                        if(world.getBlock(move.getLocation().addXYZ(x,2,z)).getId().getType().equals(ItemType.Leaves))
                        {
                            mining.mine(move.getLocation().addXYZ(x,2,z));
                        }
                    }
                }
            }
            if(mining.isMining())
            {
                Util.logger.log(LogElement.Navigation, LogLevel.Trace, "Mining, skipping step.");
                return;
            }
            Util.logger.log(LogElement.Navigation, LogLevel.Trace, "CURRENT: "+currentLocation);
            Util.logger.log(LogElement.Navigation, LogLevel.Trace, "NEXT:    "+nextLocation);
            Util.logger.log(LogElement.Navigation, LogLevel.Trace, "TYPE:    "+path.getLast().type);
            switch(node.type)
            {
                case Door:
                    move.openDoor(node.location);
                case Walk:
                    Location moveBy = new Location(nextLocation.x-currentLocation.x, 0, nextLocation.z-currentLocation.z);
                    distance = moveBy.getLength();
                    moveBy = moveBy.getNormalized().scale(movementSpeed);
                    if(currentLocation.y<nextLocation.y)
                    {
                        move.jump();
                    }
                    
                    {
                        if(distance>movementSpeed)
                        {
                            Util.logger.log(LogElement.Navigation, LogLevel.Trace, "MOVE BY: "+moveBy);
                            move.setLocation(currentLocation.add(moveBy));
                            Util.logger.log(LogElement.Navigation, LogLevel.Trace, "NEW:     "+move.getLocation());
                        }
                        else
                        {
                            distance = movementSpeed - distance;
                            move.setLocation(nextLocation);
                        }
                    }
                    break;
                case Jump:
                    if(!jumping)
                    {
                        move.setLocation(currentLocation.add(targetLocation.sub(currentLocation).scale(0.5).addY(1)));
                    }
                    else
                    {
                        move.setLocation(nextLocation);
                        //path.removeLast();
                    }
                    jumping = !jumping;
                    break;
            }
            if(path.isEmpty())
                stop();
        }
    }

    public boolean isNavigating() {
        return navigating|state==NavigationState.Calculating;
    }
    
    /**
     * Indicates whether last path computation was successful or not.
     * @return Succes of last query.
     */
    public boolean wasSuccessful()
    {
        return success;
    }

    /**
     * Checks whether there is tree nearby.
     * @param from Curent node.
     * @param tree Node of tree.
     */
    private boolean isTreeClose(Node from, Node tree)
    {        return (from.x-tree.x)*(from.x-tree.x)<=1
            &&(from.z-tree.z)*(from.z-tree.z)<=1;
    }
    
    private void buildPath(Node currentNode, Node start, HashMap<Node, Node> cameFrom) {
        path.addLast(currentNode);
        if(currentNode.equals(start))
            return;
        buildPath(cameFrom.get(currentNode), start, cameFrom);
    }

}

class NodeComparator implements Comparator
{

    @Override
    public int compare(Object o1, Object o2) {
        return (int)(((Node)o1).score - ((Node)o2).score);
    }
    
}
