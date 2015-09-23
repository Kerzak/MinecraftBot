/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraft.exception;

/**
 *
 * @author Kerzak
 */
public class MinecraftException extends Exception{
    
    public MinecraftException(ErrorMessage errMsg) {
        super(errMsg.getMessage());
    }
    
}
