/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraftbot;

/**
 *
 * @author eZ
 */
public interface IInventoryHandler {
    
    /**
     * Requests inventory handler to put block in main (hand) slot.
     * @param id Id of block to put in hand.
     * @return Number of request. -1 if handler is busy, -2 if theres no such block in inventory.
     */
    public short requestHeldChange(Id id);
    
    /**
     * Requests inventory handler to put block of desired type in main (hand) slot.
     * @param type Type of block to put in hand.
     * @return Number of request. -1 if handler is busy, -2 if theres no such block in inventory.
     */
    public short requestHeldChange(ItemType type);

    /**
     * Checks whether reuqested action was confirmed.
     * Should return true only once.
     * @return 
     */
    public boolean isConfirmed();    
}
