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
import minecraftbot.Locomotion;
import minecraftbot.Util;
import minecraftbot.logger.LogElement;
import minecraftbot.logger.LogLevel;

/**
 * Allows bot to find and follow paths around the enviroment.
 * @author eZ
 */
public class Navigation {
    
    private final IWorldView world;
    private final Locomotion move;
    private Location targetLocation;
    private final NodeComparator comparator;
    private LinkedList<Node> path;
    private boolean navigating = false, jumping = false;
    private static final float help = 0.5857864376269049f;
    private float movementSpeed = 0.4f;
    private HashMap<int[],int[][]> jumpMap;
  
    public Navigation(IWorldView world, Locomotion move)
    {
        this.world = world;
        this.move = move;
        comparator = new NodeComparator();
        
        
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
    public void navigate(int x, byte y, int z)
    {
        startNavigation(new Location(x, y, z), 0, false);
    }
    
    /**
     * Navigates close to a tree.
     * @param targetLocation Location of the tree. 
     */
    public void navigateToTree(Location targetLocation)
    {
        startNavigation(targetLocation, 1, true);
    }

    private void startNavigation (Location targetLocation, int range, boolean toTree)
    {
        Util.logger.log(LogElement.Navigation, LogLevel.Info, "Computing path.");
        this.targetLocation = targetLocation;
        //todo
        if(!toTree)
        {
            while(!world.getBlock(targetLocation).isEmpty())
            {
                targetLocation = targetLocation.addY(1);
            }
            while(world.getBlockUnder(targetLocation).isEmpty())
            {
                targetLocation = targetLocation.addY(-1);
            }
        }
        //System.out.println("TARGET LOCATION IS "+targetLocation);
        ArrayList<Node> openNodes = new ArrayList<>(), closedNodes = new ArrayList<>();
        path = new LinkedList<>();
        HashMap<Node, Node> cameFrom = new HashMap<>();
        Node current, target = new Node(targetLocation),
                start = new Node(move.getLocation());
        
        openNodes.add(start);
        while (openNodes.size()>0) {            
            current = openNodes.remove(0);
            //System.out.println(current.score);
            
            
            if(current.equals(target)||(current.score-current.g<=range||(toTree&&current.score-current.g<5&&isTreeClose(current,target)))&&!current.equals(start))
            {
                path = new LinkedList<>();

                buildPath(current, start, cameFrom);
                
                break;
            }
            
            closedNodes.add(current);
            for(Node neighbour : neighboursOf(current))
            {
                //System.out.println("++"+neighbour);
                if(closedNodes.contains(neighbour))
                {
                    //System.out.println("CLOSED");
                    continue;
                }
                
                if(!openNodes.contains(neighbour))
                {/*
                    int index = Collections.binarySearch(openNodes, neighbour, comparator);
                    if(index<0)
                    {
                        index = -1*(index+1);
                    }*/
                    
                    int index = 0;
                    for(Node node : openNodes)
                    {
                        if(node.score>neighbour.score)
                            break;
                        index++;
                    }
                    openNodes.add(index, neighbour);
                    //System.out.println("IN");
                    cameFrom.put(neighbour, current);
                }
                /*else
                    System.out.println("OPENED");*/
                
                
            }
            
         //   Collections.sort(openNodes, comparator);
            /*System.out.println("------------------------------------");
            for(Node node : openNodes)
            {
                System.out.println(node);
            }
            System.out.println("------------------------------------"); */           
        }
        
        if(path.isEmpty())
        {
            targetLocation = null;
            Util.logger.log(LogElement.Navigation, LogLevel.Error, "Path computation failed.");
            return;
        }
        
        navigating = true;
        Util.logger.log(LogElement.Navigation, LogLevel.Info, "Navigation started.");
        
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
                        if(n.y>0&& !world.getBlock(n.x+i, n.y+k, n.z+j).isEmpty()
                                &&world.getBlock(n.x+i, n.y+k+1, n.z+j).isEmpty()
                                &&world.getBlock(n.x+i, n.y+k+2, n.z+j).isEmpty())
                        {
                            if(k==0)
                            {
                                if(!world.getBlock(n.x, n.y+k+2, n.z).isEmpty())
                                    continue;
                            }
                            if(i!=0&&j!=0)
                            {
                                if(!world.getBlock(n.x+i, n.y+k+1, n.z).isEmpty()
                                   || !world.getBlock(n.x+i, n.y+k+2, n.z).isEmpty()
                                   || !world.getBlock(n.x, n.y+k+1, n.z+j).isEmpty()
                                   || !world.getBlock(n.x, n.y+k+2, n.z+j).isEmpty())
                                {
                                    break;
                                }
                            }
                            result.add(new Node(n.x+i,n.y+k+1,n.z+j,g,Location.getDistance(targetLocation, new Location(n.x+i, n.y+k+1, n.z+j)),NavPointType.Walk));   
                            break;
                        }
                    }
                    /*
                    if(n.y>0&&world.getBlock(n.x+i, n.y, n.z+j).isEmpty())
                    {
                        if(n.y<256&&world.getBlock(n.x+i,n.y+1,n.z+j).isEmpty())
                        {
                            continue;
                        }
                        else
                        {
                            result.add(new Node(n.x+i,n.y+1,n.z+j,g,Location.getDistance(targetLocation, new Location(n.x+i, n.y+1, n.z+j))));
                        }
                    }
                    else
                    {
                        if(n.y>0&&world.getBlock(n.x+i,n.y-1,n.z+j).isEmpty())
                        {
                            result.add(new Node(n.x+i,n.y,n.z+j,g,Location.getDistance(targetLocation, new Location(n.x+i, n.y, n.z+j))));
                        }
                        else if(n.y>1&&world.getBlock(n.x+i,n.y-2,n.z+j).isEmpty())
                        {
                            result.add(new Node(n.x+i,n.y-1,n.z+j,g,Location.getDistance(targetLocation, new Location(n.x+i, n.y-1, n.z+j))));            
                        }                        
                    }//*/
                }
            }
        }
        //System.out.println("c"+result.size());
        int k = 0;
        boolean canJump;
        for (Map.Entry<int[], int[][]> entry : jumpMap.entrySet()) {
            int[] t = entry.getKey();
            int[][] p = entry.getValue();
            float g = (n.g+t[0]*t[0]+t[1]*t[1]);
            canJump = true;
            if(!world.getBlock(n.x+t[0],n.y-1+k,n.z+t[1]).isEmpty()&&isThereSpaceToJump(n.x+t[0],n.y,n.z+t[1]))
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
        //System.out.println("x"+result.size());
        return result;
    }
    
    private boolean isThereSpaceToJump(int x, int y, int z)
    {
        for (int i = 0; i < 4; i++) {
            if(!world.getBlock(x, y, z).isEmpty())
            {
                return false;
            }
        }
        return true;
    }
    
    public void loopStep()
    {
        if(navigating)
        {
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
            }
            Util.logger.log(LogElement.Navigation, LogLevel.Trace, "CURRENT: "+currentLocation);
            Util.logger.log(LogElement.Navigation, LogLevel.Trace, "NEXT:    "+nextLocation);
            Util.logger.log(LogElement.Navigation, LogLevel.Trace, "TYPE:    "+path.getLast().type);
            switch(node.type)
            {
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
                            //move.setLocation(currentLocation.add(nextLocation.sub(currentLocation).getNormalized().scale(distance)));
                            path.removeLast();
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
                        path.removeLast();
                    }
                    jumping = !jumping;
                    break;
            }
            if(path.isEmpty())
                stop();
        }
    }

    public boolean isNavigating() {
        return navigating;
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
