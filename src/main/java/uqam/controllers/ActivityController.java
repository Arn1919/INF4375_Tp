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
     * @param rayon
     * @param lat
     * @param lng 
    * @param du
     * @param au
     * @return List<Activity>
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<Activity> get(@RequestParam(value = "rayon") Integer rayon,
            @RequestParam(value = "lat") Double lat,
            @RequestParam(value = "lng") Double lng,
            @RequestParam(value = "du") String du,
            @RequestParam(value = "au") String au) {

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
            paramsStmt += "ST_Distance(";
            paramsStmt += "     coordinates, ";
            paramsStmt += "     ST_MakePoint(" + lat + "," + lng + " )::geography";
            paramsStmt += ") ";
            paramsStmt += "<= " + rayon;

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
            paramsStmt += "ST_Distance(";
            paramsStmt += "     coordinates, ";
            paramsStmt += "     ST_MakePoint(" + lng + "," + lat + " )::geography";
            paramsStmt += ") ";
            paramsStmt += "<= " + rayon;
            paramsStmt += " and";
            paramsStmt += " event_date >= " + du;
            paramsStmt += " and";
            paramsStmt += " event_date <= " + au;
        }

        return activityRepository.findByParams(paramsStmt);
    }

    /**
     * HTTP Method : POST
     * 
     * Ajoute une Activity a la BD et retourne le id
     * 
     * @param activity
     * @return String
     */
    @RequestMapping(method = RequestMethod.POST)
    public String post(@RequestBody Activity activity) {
        System.out.println("HERE PUT CONTROLLER");
        int id;
        try {
            id = activityRepository.post(activity);
        } catch (Exception ex) {
            Logger.getLogger(ActivityController.class.getName()).log(Level.SEVERE, null, ex);
            return "ActivityController - post() : An error has occurred.";
        }
        return "Activity id " + id + " - Created Successfully";
    }

    /**
     * HTTP Method : PUT
     * 
     * Ajoute une Activity au id specifie
     * 
     * @param id
     * @param activity
     * @return String
     */
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    public String put(@PathVariable("id") int id, @RequestBody Activity activity) {
        try {
            activityRepository.put(id, activity);
        } catch (Exception ex) {
            Logger.getLogger(ActivityController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "Activity id " + id + " - Updated Successfully";
    }

    /**
     * HTTP Method : DELETE
     * 
     * Supprime une Activity au id specifie
     * 
     * @param id
     * @return String
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
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
    private String getYesterdayString() {
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
    private String getTomorrowString() {
        return "";
    }
}
