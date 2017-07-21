/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uqam.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 *
 * @author arnaud
 */
public class Ligne {
    
    private List<Point> points;
    
    public Ligne(){
        
    }
    
    public Ligne(List<Point> points){
        this.points = points;
    }
    
    @JsonProperty public List<Point> getPoints(){ return points; }
}
