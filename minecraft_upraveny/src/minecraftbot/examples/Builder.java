/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraftbot.examples;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import minecraftbot.Id;
import minecraftbot.MinecraftBotHandler;
import minecraftbot.Util;
import minecraftbot.logger.LogElement;
import minecraftbot.logger.LogLevel;
import minecraftbot.logger.Logger;

/**
 * Should be run on Builder Demo map.
 * Collects wood, then builds a simple housing.
 * @author eZ
 */
public class Builder extends MinecraftBotHandler {
    
    String state, prevState;
    Location currentWood;
    
    private static  String myName = "Lumberjack";
    private static final String address = "localhost";
    private static final int port = 49337;    
    
    private static Logger log;
    
    
    @Override
    protected void logic() {
        if(!state.equals(prevState))
            Util.logger.log(LogElement.Custom, LogLevel.Info, "New state: " + state);
        prevState = state;
        switch (state) {
            case "find":
                currentWood = world.getClosestResource(Id.LOG,move.getLocation());
                if(inventory.amountOf(Id.WOOD)>=100)
                {
                    state="gotodig";
                    chat.sendMessage("Enough wood! I'm going to dig a hole for my house!");
                    navigation.navigate(463,(byte)56,363);
                }
                else if (currentWood == null) {
                    if(collecting.isPickableNearby(8))
                    {
                        navigation.navigate(collecting.getNearestStack().getLocation());
                        state = "collect";
                        break;
                    }
                    state = "finished";
                    chat.sendMessage("There's not enough wood for my house :(");
                }
                else if(Location.getDistance(currentWood, move.getLocation()) > 10
                        && collecting.isPickableNearby(6))
                {
                    state="collect";
                    navigation.navigate(collecting.getNearestStack().getLocation());
                } else {
                    state = "navigate";
                    navigation.navigateToTree(currentWood);
                    chat.sendMessage("There it is! @"+currentWood);
                }
                break;
            case "navigate":
                if (!navigation.isNavigating()) {
                    chat.sendMessage("I'm gonna cut it down!");
                    state = "cut";
                    dig.dig(currentWood);
                }
                break;
            case "cut":
                if (!dig.isDigging()) {
                    chat.sendMessage("Where's another one?! Need "+(100-inventory.amountOf(Id.WOOD))+" more!");
                    state = "find";

                }
                break;
            case "collect":
                if(collecting.isPickableNearby(6))
                {
                    if(!navigation.isNavigating())
                        navigation.navigate(collecting.getNearestStack().getLocation());
                }
                else
                {
                    state = "find";
                }
                break;
            case "gotodig":
                if(!navigation.isNavigating())
                {
                    state = "dig";
                    chat.sendMessage("This is where my house will stand!");
                    for (int i = 0; i < 5; i++) {
                        for (int j = 0; j < 5; j++) {
                            // todo dig.dig(460+i, (byte)55,360+j);
                        }
                    }
                }
                break;
            case "dig":
                if(!dig.isDigging())
                {
                    navigation.navigate(459, (byte)56, 359);
                    chat.sendMessage("Floor first!");
                    state = "stepaside";
                }
                break;
            case "stepaside":
                if(!navigation.isNavigating())
                {
                    build.platform(Id.WOOD, 460, 464, (byte)55, 360, 364);
                    state = "floor";
                }
                break;
            case "floor":
                if(!build.isBuilding())
                {
                    state = "stepin";
                    navigation.navigate(463,(byte)56,363);
                }
                break;
            case "stepin":
                if(!navigation.isNavigating())
                {
                    build.wall(Id.WOOD, 460, (byte)56, 360, 464, (byte)58, 360);
                    build.wall(Id.WOOD, 460, (byte)56, 364, 464, (byte)58, 364);
                    build.wall(Id.WOOD, 464, (byte)56, 361, 464, (byte)58, 363);
                    build.wall(Id.WOOD, 460, (byte)56, 361, 460, (byte)58, 363);
                    build.platform(Id.WOOD, 460, 464, (byte)59, 360, 364);
                    state = "build";
                }
                break;
            case "build":
                if(!build.isBuilding())
                {
                    state = "door";
                    dig.dig(462, (byte)56, 360);
                    dig.dig(462, (byte)57, 360);
                }
                break;
            case "door":
                if(!dig.isDigging())
                {
                    state = "finished";
                    chat.sendMessage("My work is done here!");
                }
                break;
        }
    }
    
    @Override
    protected void initialize() {
        state = "fiand";
        chat.sendMessage("Let's cut some trees!");
        log = Util.logger;
        log.defaultLevel = LogLevel.Warn;
        log.add(LogElement.Digging, LogLevel.Debug);
        log.add(LogElement.Inventory, LogLevel.Info);
        log.add(LogElement.Custom, LogLevel.Info);
        log.add(LogElement.PacketId, LogLevel.Trace);
    }
    
    @Override
    protected void readMessage(String sender, String text)  {
        state = text;
    }
    
    public static void main(String[] args) throws Exception {
        Builder bot = new Builder();
        bot.startBot(myName, address, port);
    }
    
}
