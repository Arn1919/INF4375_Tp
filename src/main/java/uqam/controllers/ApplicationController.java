/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uqam.controllers;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uqam.repositories.BixiRepository;
import uqam.resources.Bixi;

/**
 *
 * @author arnaud
 */
@ComponentScan
public class ApplicationController {
    
    @Autowired
    private BixiRepository bixiRepository;
    
    
    /**
     * 
     * @param model
     * @return String
     */
    @RequestMapping("/")
    public String home(Map<String, Object> model) {
        model.put("name", "Arnaud");
        return "home";
    }
    
    
    /**
     * 
     * 
     * @param id
     * @param model
     * @return String
     */
    @RequestMapping(value = "/station-bixi/{id}", method = RequestMethod.GET)
    public String bixiById(@PathVariable("id") int id, Map<String, Object> model){
        Bixi bixi = bixiRepository.findById(id);
        return "bixi";
    }
    
    /**
     * 
     * 
     * @param model
     * @return String
     */
    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public String errorPage(Map<String, Object> model){
        return "error";
    }
    
    
    
}
