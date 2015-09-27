/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraft.commands;

import java.util.Stack;
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
public class CommandGetPatternsAndCraft implements Command {
    
    private boolean executed = false;
    
    private Crafting crafting;
    
    private Stack patterns;
    
    private Id id;
    
    private ChatHandler chat;
    
    public CommandGetPatternsAndCraft(Id id, Crafting crafting, ChatHandler chat) {
        this.crafting = crafting;
        this.id = id;
        this.chat = chat;
    }

    @Override
    public boolean isExecuted() {
        return this.executed;
    }

    @Override
    public void execute() {
        try {
            patterns = crafting.getWorkbenchCraftingStack(id);
            while(!patterns.isEmpty()) {
                crafting.addCommand((CommandCraftItemWorkbench)patterns.pop());
            }
        } catch (MinecraftException ex) {
            chat.sendMessage(ex.getMessage());
            crafting.cancelCrafting();
        }
    }
    
}
