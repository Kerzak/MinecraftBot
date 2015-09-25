/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraft.exception;

/**
 *
 * @author Kerzak
 */
public enum ErrorMessage {
    /** This item has no inventory pattern, use workbench pattern. */
    M01("This item has no inventory pattern, use workbench pattern."),
    /** Chest is not open. */
    M02("Chest is not open."),
    /** No such item in chest. */
    M03("No such item in chest."),
    /** My inventory is full */
    M04("My inventory is full"),
    /** I do not have such item */
    M05("I do not have such item."),
    /** No pattern exists for this item */
    M06("No pattern exists for this item"),
    /** Not enough resources in chest to craft this item */
    M07("Not enough resources in chest to craft this item"),
    /** My inventory is empty */
    M08("My inventory is empty");
    
    
    private String message;
    
    ErrorMessage(String msg) {
        this.message = msg;
    }
    
    public String getMessage() {
        return this.message;
    }
}
