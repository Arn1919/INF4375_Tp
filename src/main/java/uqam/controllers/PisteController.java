/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uqam.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uqam.repositories.PisteRepository;
import uqam.resources.Piste;

/**
 *
 * @author arnaud
 */
@RestController
@RequestMapping("/pistes-cyclables")
public class PisteController {

    // Coordonnees par defaut : Pavillon PK UQAM
    private static final double DEFAULT_LAT = 45.50894093;
    private static final double DEFAULT_LNG = -73.56863737;
    private static final int DEFAULT_RADIUS = 5000;
    
    @Autowired
    PisteRepository pisteRepository;
    
    @RequestMapping(method = RequestMethod.GET)
    public List<Piste> get(@RequestParam(defaultValue = "5000") Integer rayon, 
            @RequestParam(defaultValue = "45.50894093") Double lat, @RequestParam(defaultValue = "-73.56863737") Double lng) {
        String paramsStmt = " where";
        
        // WORKING QUERY 
        paramsStmt += "";
        
        return pisteRepository.findByParams(paramsStmt);
    }
}


