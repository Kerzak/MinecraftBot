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
public interface IInventoryView {
    public Id heldId();
    public boolean contains(Id id);
    public boolean contains(ItemType type);
    public int amountOf(Id id);
    public int amountOf(ItemType type);
}
