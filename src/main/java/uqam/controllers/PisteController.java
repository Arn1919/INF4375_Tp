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
    private static final int DEFAULT_RADIUS = 200;

    @Autowired
    PisteRepository pisteRepository;

    /**
     * HTTP Method : GET avec parametres
     *
     * @param rayon
     * @param lat
     * @param lng
     * @return List<Piste>
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<Piste> get(@RequestParam(value = "rayon", defaultValue = "200", required = false) Integer rayon,
            @RequestParam(value = "lat", required = true) Double lat,
            @RequestParam(value = "lng", required = true) Double lng) {
        String paramsStmt = " WHERE";
        paramsStmt += " ST_DWITHIN(ST_TRANSFORM(ST_SetSrid(piste, 2950), 4326)::geography, ";
        paramsStmt += " ST_MakePoint(" + lng + "," + lat + "), " + +rayon + ")";

        return pisteRepository.findByParams(paramsStmt);
    }
}
