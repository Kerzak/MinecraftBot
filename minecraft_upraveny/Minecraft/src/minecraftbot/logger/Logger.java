
package minecraftbot.logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Tool for logging bots activity.
 */
public class Logger {
    
    private Map<LogElement,LogLevel> elements;
    public LogLevel defaultLevel;
    
    public Logger()
    {
        elements = new HashMap<>();
        defaultLevel = LogLevel.Info;
    }
    
    public void log(LogElement element, LogLevel level, String text)
    {
        
        if((!elements.containsKey(element)&&!elements.containsKey(LogElement.All))&&level.level>defaultLevel.level)
            return;

        if(level.level>defaultLevel.level&&(!elements.containsKey(element)||level.level>elements.get(element).level)) {
            return;
        }
        
        System.out.println(element+" - " + level + ": " + text);
    }
    
    public void log(LogElement element, LogLevel level, Object o)
    {
        log(element, level, o.toString());
    }
    
    public void add(LogElement element, LogLevel level)
    {
        elements.put(element, level);
    }
}
