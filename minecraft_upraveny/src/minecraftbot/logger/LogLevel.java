/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraftbot.logger;

/**
 *
 * @author eZ
 */
public enum LogLevel {
    Off(0),
    Error(1),
    Warn(2),
    Info(3),
    Debug(4),
    Trace(5);
    
    public final int level;

    LogLevel(int level)
    {
        this.level = level;
    }    
}
