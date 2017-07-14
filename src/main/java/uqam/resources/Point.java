/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uqam.resources;

/**
 *
 * @author arnaud
 */
public class Point {
    
    private double lng;
    private double lat;
    
    public Point(){
        
    }
    
    public Point(double lng, double lat){
        this.lng = lng;
        this.lat = lat;
    }
    
    public double getLng(){
        return lng;
    }
    
    public double getLat(){
        return lat;
    }
    
}
