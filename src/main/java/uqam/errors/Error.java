/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uqam.errors;

/**
 *
 * @author arnaud
 */
public class Error  extends Exception{
    
    private int code;
    private String message;

    public Error(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    // Getters
    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
    
    // Setters
    public void setCode(int code) { 
        this.code = code; 
    }
    
    public void setMessage(String message){
        this.message = message;
    }
    

}
