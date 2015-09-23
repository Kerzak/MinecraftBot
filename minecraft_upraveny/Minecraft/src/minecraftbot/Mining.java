

package minecraftbot;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import java.io.IOException;
import java.util.LinkedList;
import minecraftbot.logger.LogElement;
import minecraftbot.logger.LogLevel;
import minecraftbot.network.MinecraftOutputStream;
import minecraftbot.packet.out.Out07PlayerDigging;

/**
 * Allows bot to dig holes, mine blocks, cut trees etc.
 */
public class Mining {
    
    private final int TRYTIME = 100;
    
    private final MinecraftOutputStream out;
    private final IInventoryHandler inventory;
    private final Locomotion move;
    private final IWorldView world;
    private final GameInfo info;
    private int x, z, tryCount;
    private byte y;
    private short actionNumber;
    private boolean mining, sendFinishedMsg, miningFinished, useTool=true, waitingForTool=false;
    private final LinkedList<MiningPlan> miningQueue;
    private final MinecraftBotHandler botHandler;
    private Out07PlayerDigging playerDiggingPacket;
    
    public Mining(MinecraftOutputStream out, MinecraftBotHandler botHandler, GameInfo info, Locomotion move, IInventoryHandler inventory, IWorldView world,Out07PlayerDigging playerDiggingPacket)
    {
        this.out= out;
        this.inventory = inventory;
        this.info = info;
        this.move = move;
        this.world = world;
        this.playerDiggingPacket = playerDiggingPacket;
        this.botHandler = botHandler;
        miningQueue = new LinkedList<>();
    }
    
    /**
     * Digs a block.
     * @param location Location of the block.
     */
    public void mine(Location location)
    {
        miningQueue.add(new MiningPlan(Util.blockLocation(location)));
    }
    
    /**
     * Digs a block.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param z Z coordinate.
     */
    public void mine(int x, int y, int z)
    {
        miningQueue.add(new MiningPlan(x, (byte)y, z));
    }
   
    /**
     * Sends message to server to actually mine planned block.
     */
    private void mine()
    {
        move.lookAt(new Location(x, y, z));
        try {
            out.write(10);
            out.writeInt(info.getId());
            out.write(1);      
            out.closePacket();
            playerDiggingPacket.sendMessage((byte)0, x, y, z, (byte)1);
            mining = true;
            miningFinished = false;
            sendFinishedMsg = false;
            tryCount = 0;
        } catch (IOException ex) {
            System.err.println("IO Error while digging.");
            System.exit(0);
        }
    }
    
    /**
     * Cancels all future plans for mineging.
     */
    public void cancelAllMining()
    {
        miningQueue.clear();
    }
    
    public void update()
    {
        if(mining&&!miningFinished&&!waitingForTool)
        {
            try {
                tryCount++;
                
                move.lookAt(new Location(x, y, z));
                out.write(10);
                out.writeInt(info.getId());
                out.write(1);
                out.closePacket();
                if(!sendFinishedMsg)
                {
                        playerDiggingPacket.sendMessage((byte)2, x, y, z, (byte)1);
                        sendFinishedMsg = true;
                }
                Util.logger.log(LogElement.Mining, LogLevel.Debug, x+"-"+y+"-"+z+" is "+world.getBlock(x, y, z).getId());
                if(world.getBlock(x, y, z).getId()==Id.Air)
                {
                    Util.logger.log(LogElement.Mining, LogLevel.Trace, "Yaay.");
                    botHandler.onMiningSuccess(new Location(x,y,z));
                    if(miningQueue.isEmpty())
                        mining = false;

                    miningFinished = true;
                }
                else
                {
                    Util.logger.log(LogElement.Mining, LogLevel.Trace, "Not finished yet.");
                    if(tryCount>TRYTIME)
                    {
                        botHandler.onMiningFail(new Location(x,y,z), world.getBlock(x, y, z).getId());
                        if(miningQueue.isEmpty())
                            mining = false;

                        miningFinished = true;                    }
                }
                Util.logger.log(LogElement.Mining, LogLevel.Trace, "Dig finished : " +miningFinished);
            } catch (IOException ex) {
                System.err.println("IO Error in digging loop step");
                System.exit(0);
            }
        }
        else if(!miningQueue.isEmpty()&&!waitingForTool)
        {
            Util.logger.log(LogElement.Mining, LogLevel.Trace, "What to dig next?");
           // if(!mining||digFinished)
            {
                miningFinished = false;
                MiningPlan nextStep = miningQueue.getFirst();
                x = nextStep.x;
                y = nextStep.y;
                z = nextStep.z;
                if(!world.getBlock(x, y, z).isEmpty())
                {
                    waitingForTool = false;
                    if(useTool)
                    {
                        ItemType prefType = getPreferedTool(world.getBlock(x, y, z).getId());
                        Util.logger.log(LogElement.Mining, LogLevel.Trace, "TO DIG : "+world.getBlock(x, y, z).getId().getType());
                        Util.logger.log(LogElement.Mining, LogLevel.Trace, "TOOL   : "+prefType);

                        if(prefType!=ItemType.None)
                        {
                            actionNumber = inventory.requestHeldChange(prefType);
                            Util.logger.log(LogElement.Mining, LogLevel.Debug, "Requesting tool "+prefType+ " > "+actionNumber);
                        }
                        
                        if(actionNumber>=0)
                            waitingForTool = true;
                    }
                    if(!waitingForTool)
                    {
                        Util.logger.log(LogElement.Mining, LogLevel.Trace, "Lets dig without a tool.");
                        miningQueue.removeFirst();
                        mine();
                    }
                }
                else
                {
                    Util.logger.log(LogElement.Mining, LogLevel.Debug, "Tried to dig empty block @"+miningQueue.getFirst());
                    miningQueue.removeFirst();
                }
            }
        }
        else if(waitingForTool)
        {
            Util.logger.log(LogElement.Mining, LogLevel.Trace, "Waiting for tool");
            waitingForTool = !inventory.isConfirmed(actionNumber);
            if(!waitingForTool){
                miningQueue.removeFirst();
                Util.logger.log(LogElement.Mining, LogLevel.Trace, "We got the tool!.");
                if(checkRange())
                {
                    mine();
                }
                else
                {
                    botHandler.onOutOfRangeToMine(new Location(x,y,z));
                }
            }
        }
    }

    /**
     * Tells bot to use or stop using tools for mining.
     * @param b Whether bot should use tools for mining.
     */
    public void useTools(boolean b)
    {
        useTool = b;
    }

    /**
     * @return Whether bot is using tools.
     */
    public boolean isUsingTools() {
        return useTool;
    }
    
    /**
     * @return Whether bot is currenty mining.
     */
    public boolean isMining() {
        return !miningQueue.isEmpty()||mining;
    }

    /**
     * @param toMine Item type to mine.
     * @return Type of best item to use for mining.
     */
    public ItemType getPreferedTool(ItemType toMine)
    {
        switch(toMine)
        {
            case Wood:
                return ItemType.Axe;
            case Ground:
                return ItemType.Shovel;
            case Ice:
                return ItemType.Pickaxe;
            case Metal:
                return ItemType.Pickaxe;
            case Plant:
                return ItemType.Axe;
            case Rail:
                return ItemType.Pickaxe;
            case Rock:
                return ItemType.Pickaxe;
            default:
                return ItemType.None;
            
        }
    }
    
    /**
     * @param toMine Item id to mine.
     * @return Type of best item to use for mining.
     */
    public ItemType getPreferedTool(Id toMine)
    {
        return getPreferedTool(toMine.getType());
    }

    /**
     * @return Whether bot is in range to mine.
     */
    private boolean checkRange() {
        return  Location.getDistance(move.getLocation(), new Location(x,y,z))<5.5;
    }


        
    /**
     * Represents future plan for mining a block.
     */
    class MiningPlan
    {
        public final int x, z;
        public final byte y;
        
        public MiningPlan(int x, byte y, int z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        private MiningPlan(Location location) {
            this.x = (int)location.x;
            this.y = (byte)location.y;
            this.z = (int)location.z;
        }

        @Override
        public String toString() {
            return ("["+x+","+y+","+z+"]");
        }
        
        
    }    
}
