package uqam.tasks;

import uqam.repositories.BixiRepository;
import uqam.repositories.ActivityRepository;
import uqam.resources.Lieu;
import uqam.resources.Activity;
import uqam.resources.Bixi;

import java.util.*;
import java.io.*;

import com.fasterxml.jackson.annotation.*;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.web.client.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Service
public class ImportJsonData {

    private static final String ACTIVITY_JSON_FILE_PATH = "src/main/resources/programmation-parcs.json";
    private static final String BIXI_URL = "https://secure.bixi.com/data/stations.json";

    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private BixiRepository bixiRepository;

    @PostConstruct
    public void parseActivities() throws Exception {
        try {
            // Parser du JsonFile
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(ACTIVITY_JSON_FILE_PATH));
            // Array of JsonObject , JsonObject activity and JsonObject Lieu
            JSONArray myArray = (JSONArray) obj;
            JSONObject myObject;
            JSONObject myObjectLieu;
            Activity activity;

            // Boucle a travers tableau d objet json, cree activite et insere dans repertoire
            for (int i = 0; i < myArray.size(); i++) {
                myObject = (JSONObject) myArray.get(i);
                myObjectLieu = (JSONObject) myObject.get("lieu");
                if (myObjectLieu.containsKey("lat") && myObjectLieu.containsKey("lng")) {
                    activity = new Activity(Integer.parseInt((String) myObject.get("id")), 
                            (String) myObject.get("nom"), 
                            (String) myObject.get("description"), 
                            (String) myObject.get("arrondissement"), 
                            (ArrayList<String>) myObject.get("dates"), 
                            new Lieu((String) myObjectLieu.get("nom"), 
                                    (double) myObjectLieu.get("lat"), 
                                    (double) myObjectLieu.get("lng"))
                    );
                } else {
                    activity = new Activity(Integer.parseInt((String) myObject.get("id")),
                            (String) myObject.get("nom"),
                            (String) myObject.get("description"),
                            (String) myObject.get("arrondissement"),
                            (ArrayList<String>) myObject.get("dates"),
                            new Lieu((String) myObjectLieu.get("nom"))
                    );
                }
                activityRepository.insert(activity);
            }

        } catch (IOException e) {
            System.out.println("FILE NOT FOUND EXCEPTION ERROR: Activity Json file not found.");
        } catch (ParseException | NumberFormatException e) {
            System.out.println("EXCEPTION ERROR: Exception thrown in Activity Json Parser");
            e.getMessage();
        }
    }

    //@Scheduled(cron = "*/2 * * * * ?") // à toutes les 2 secondes.
    public void parseBixies() {
        try {

            RandomStation station = new RestTemplate().getForObject(BIXI_URL, RandomStation.class);
            System.out.println("TEST HERE: " + asBixi(station.randomBixiList.get(0)).toString());
            station.randomBixiList.stream()
                    .map(this::asBixi)
                    .forEach(bixiRepository::insert);
        } catch (Exception e) {
            System.out.println("EXCEPTION ERROR: Exception thrown in Bixies Json Parser");
            e.getMessage();
        }

    }

    private Bixi asBixi(RandomBixi b) {

        return new Bixi(b.id,
                b.stationName,
                b.stationId,
                b.stationState,
                b.stationIsBlocked,
                b.stationUnderMaintenance,
                b.stationOutOforder,
                b.millisLastUpdate,
                b.millisLastServerCommunication,
                b.lat,
                b.lng,
                b.availableTerminals,
                b.unavailableTerminals,
                b.availableBikes,
                b.unavailableBikes);
    }

}

class RandomStation {

    @JsonProperty("stations")
    List<RandomBixi> randomBixiList;
    @JsonProperty("schemeSuspended")
    boolean schemeSuspended;
    @JsonProperty("timestamp")
    long timestamp;
}

class RandomBixi {

    @JsonProperty("id")
    int id;
    @JsonProperty("s")
    String stationName;
    @JsonProperty("n")
    int stationId;
    @JsonProperty("st")
    int stationState;
    @JsonProperty("b")
    boolean stationIsBlocked;
    @JsonProperty("su")
    boolean stationUnderMaintenance;
    @JsonProperty("m")
    boolean stationOutOforder;
    @JsonProperty("lu")
    long millisLastUpdate;
    @JsonProperty("lc")
    long millisLastServerCommunication;
    @JsonProperty("bk")
    boolean usageFutur1;
    @JsonProperty("bl")
    boolean usageFutur2;
    @JsonProperty("la")
    double lat;
    @JsonProperty("lo")
    double lng;
    @JsonProperty("da")
    int availableTerminals;
    @JsonProperty("dx")
    int unavailableTerminals;
    @JsonProperty("ba")
    int availableBikes;
    @JsonProperty("bx")
    int unavailableBikes;
}
