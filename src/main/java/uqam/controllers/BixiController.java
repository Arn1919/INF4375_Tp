package uqam.controllers;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uqam.repositories.BixiRepository;
import uqam.resources.Bixi;

@RestController
@RequestMapping("/stations-bixi")
public class BixiController{


    // Coordonnees par defaut : Pavillon PK UQAM
    private static final double DEFAULT_LAT = 45.50894093;
    private static final double DEFAULT_LNG = -73.56863737;
    private static final int DEFAULT_RADIUS = 5000;
    
    @Autowired
    private BixiRepository bixiRepository;

    @RequestMapping(method = RequestMethod.GET)
    public List<Bixi> get(@RequestParam(defaultValue = "0") Integer min_bixi_dispo, @RequestParam(defaultValue = "5000") Integer rayon, 
            @RequestParam(defaultValue = "45.50894093") Double lat, @RequestParam(defaultValue = "-73.56863737") Double lng) {
        
        String paramsStmt = " where";
        paramsStmt += " availableBikes >= " + min_bixi_dispo;
        paramsStmt += " and";
        paramsStmt += "ST_Distance(";
        paramsStmt += "     coordinates, ";
        paramsStmt += "     ST_MakePoint(" + lat + "," + lng +" )::geography";
        paramsStmt += ") ";
        paramsStmt += "<= " + rayon;
        
        return bixiRepository.findByParams(paramsStmt);
    }
    
}