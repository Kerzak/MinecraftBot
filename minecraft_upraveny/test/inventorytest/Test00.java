/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package inventorytest;


import junit.framework.Assert;
import minecraftbot.IInventoryHandler;
import minecraftbot.IInventoryStorage;
import minecraftbot.IInventoryView;
import minecraftbot.Id;
import minecraftbot.InventoryHandler;
import minecraftbot.InventoryInfo;
import minecraftbot.Util;
import org.junit.Test;

/**
 *
 * @author eZ
 */
public class Test00 {

    //for normal addition 
    @Test
    public void testAdd1Plus1() 
    {
        IInventoryStorage storage;
        IInventoryHandler handler;
        IInventoryView view;
        Util.Load();
        
        /*
        ////////////////////////////
        // init
        
       // storage = new InventoryHandler(null);
//        handler = (IInventoryHandler)storage;
//        view = new InventoryInfo((InventoryHandler)storage);
        
        ////////////////////////////
        
        for (Id id : Util.idMap.values()) {
            Assert.assertFalse(view.contains(id));
            Assert.assertTrue(view.amountOf(id)==0);
            
        }
        */
    }
}
