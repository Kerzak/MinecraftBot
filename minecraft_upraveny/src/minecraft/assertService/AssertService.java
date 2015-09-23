/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraft.assertService;

import minecraft.exception.ErrorMessage;
import minecraft.exception.MinecraftException;
import minecraftbot.Id;

/**
 *
 * @author Kerzak
 */
public class AssertService {
    public void assertInventoryPatternExists(Id[] inventoryPatern) throws MinecraftException {
        if (inventoryPatern == null) throw new MinecraftException(ErrorMessage.M01);
    }
}
