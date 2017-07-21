package uqam.controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uqam.repositories.ActivityRepository;
import uqam.resources.Activity;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author arnaud
 */
@RestController
@RequestMapping("/activites-375e")
public class ActivityController {

    // Coordonnees par defaut : Pavillon PK UQAM
    private static final double DEFAULT_LAT = 45.50894093;
    private static final double DEFAULT_LNG = -73.56863737;
    private static final int DEFAULT_RADIUS = 5000;

    @Autowired
    private ActivityRepository activityRepository;

    /**
     * HTTP Method : GET avec parametres
     * 
     * Retourne la liste d'activites correspondant aux parametres
     * 
     * Valeurs par defaut:
     *  -rayon  : DEFAULT_RAYON
     *  -lat    : DEFAULT_LAT
     *  -lng    : DEFAULT_LNG
     *  -du     : Date de la veille
     *  -au     : Date du lendemain
     *
     * @param rayon
     * @param lat
     * @param lng
     * @param du
     * @param au
     * @return List<Activity>
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<Activity> get(@RequestParam Integer rayon, @RequestParam Double lat, @RequestParam Double lng,
            @RequestParam String du, @RequestParam String au) {

        du = getYesterdayString();
        
        
        String paramsStmt = "where ";

        if (rayon == null && lat == null && lng == null && (du != null || au != null)) {
            if (du == null) {
                du = getYesterdayString();
            }
            if (au == null) {
                au = getTomorrowString();
            }
            paramsStmt += " event_date >= " + du;
            paramsStmt += " and";
            paramsStmt += " event_date <= " + au;
        } else if ((rayon != null || lat != null || lng != null) && du == null && au == null) {
            if (rayon == null) {
                rayon = DEFAULT_RADIUS;
            }
            if (lat == null) {
                lat = DEFAULT_LAT;
            }
            if (lng == null) {
                lng = DEFAULT_LNG;
            }
            paramsStmt += " ST_DWithin(coordinates, ST_MakePoint(" + lat + ", " + lng + ") , " + rayon + ")";

        } else if ((rayon != null || lat != null || lng != null) && (du != null || au != null)) {
            if (rayon == null) {
                rayon = DEFAULT_RADIUS;
            }
            if (lat == null) {
                lat = DEFAULT_LAT;
            }
            if (lng == null) {
                lng = DEFAULT_LNG;
            }
            if (du == null) {
                du = getYesterdayString();
            }
            if (au == null) {
                au = getTomorrowString();
            }
            paramsStmt += " ST_DWithin(coordinates, ST_MakePoint(" + lat + ", " + lng + ") , " + rayon + ")";
            paramsStmt += " and";
            paramsStmt += " event_date >= " + du;
            paramsStmt += " and";
            paramsStmt += " event_date <= " + au;
        }

        return activityRepository.findByParams(paramsStmt);
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public String post(@RequestBody Activity activity) {
        int id = -1;
        try {
            id = activityRepository.post(activity);
        } catch (Exception ex) {
            Logger.getLogger(ActivityController.class.getName()).log(Level.SEVERE, null, ex);
            return "ActivityController - post() : An error has occurred.";
        }
        return "Activity id " + id + " - Created Successfully";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public String put(@PathVariable("id") int id, @RequestBody Activity activity) {
        try {
            activityRepository.put(id, activity);
        } catch (Exception ex) {
            Logger.getLogger(ActivityController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "Activity id " + id + " - Updated Successfully" ;
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("id") int id) {
        activityRepository.delete(id);
        return "Activity id  " + id + " - Deleted Successfully";
    }

    /**
     * Retourne la date d'hier en String
     * 
     * Format date String : 'yyyy-mm-dd'
     * 
     * @return String
     */
    private String getYesterdayString(){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date date = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }
    
    /**
     * Retourne la date de demain en String
     * 
     * Format date String : 'yyyy-mm-dd'
     * 
     * @return String
     */
    private String getTomorrowString(){
        return "";
    }
}
