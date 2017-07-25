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
    private static final int DEFAULT_RADIUS = 1000;
    
    @Autowired
    private BixiRepository bixiRepository;

    /**
     * HTTP Method : GET avec parametres
     * 
     * @param min_bixi_dispo
     * @param rayon
     * @param lat
     * @param lng
     * @return List<Bixi>
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<Bixi> get(@RequestParam(value="min_bixi_dispo", defaultValue = "0", required = false) Integer min_bixi_dispo,
            @RequestParam(value="rayon", defaultValue = "1000") Integer rayon, 
            @RequestParam(value="lat", defaultValue = "45.50894093") Double lat, 
            @RequestParam(value="lng", defaultValue = "-73.56863737") Double lng) {
        
        String paramsStmt = " where";
        paramsStmt += " available_bikes >= " + min_bixi_dispo;
        paramsStmt += " and";
        paramsStmt += " ST_Distance(";
        paramsStmt += "     coordinates, ";
        paramsStmt += "     ST_MakePoint(" + lng + "," + lat +" )::geography";
        paramsStmt += ") ";
        paramsStmt += "<= " + rayon;
        
        return bixiRepository.findByParams(paramsStmt);
    }
    
}