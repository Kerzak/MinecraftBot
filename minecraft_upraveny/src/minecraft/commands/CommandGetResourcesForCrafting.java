/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraft.commands;

import java.util.logging.Level;
import java.util.logging.Logger;
import minecraft.crafting.Crafting;
import minecraft.exception.MinecraftException;
import minecraftbot.ChatHandler;
import minecraftbot.Id;

/**
 *
 * @author Kerzak
 */
public class CommandGetResourcesForCrafting implements Command {
    
    private boolean executed = false;
    
    private boolean busy = false;
    
    private boolean openChest = true;
    
    private Crafting crafting;
    
    private Id id;
    
    private ChatHandler chat;
    
    public CommandGetResourcesForCrafting (Crafting crafting, Id id, ChatHandler chat) {
        this.crafting = crafting;
        this.id = id;
        this.chat = chat;
    }

    @Override
    public boolean isExecuted() {
        return (this.executed);
    }

    @Override
    public void execute() {
        try {
            this.executed = crafting.getResourcesFromChest(id);
            openChest = false;
        } catch (MinecraftException ex) {
            chat.sendMessage(ex.getMessage());
            this.executed = true;
            crafting.sleep(1000);
        }
       
    }
    
}
