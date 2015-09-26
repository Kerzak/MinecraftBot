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
public class CommandCraftItemFromResources implements Command {

    private boolean executed = false;
    
    private Crafting crafting;
    
    private Id id;
    
    private boolean openWorkbench = true;
    
    private ChatHandler chat;
    
    public CommandCraftItemFromResources(Crafting crafting, Id id, ChatHandler chat) {
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
            this.executed = crafting.craftInWorkbench(id, openWorkbench);
        } catch (MinecraftException ex) {
            chat.sendMessage(ex.getMessage());
            this.executed = true;
            crafting.sleep(1000);
        }
        openWorkbench = false;
    }
    
}
