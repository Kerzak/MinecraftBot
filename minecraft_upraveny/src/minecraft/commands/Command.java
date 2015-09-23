/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraft.commands;

import minecraft.exception.MinecraftException;

/**
 *
 * @author Kerzak
 */
public interface Command {
    
     public boolean isExecuted();
     
     public void execute();
   
}
