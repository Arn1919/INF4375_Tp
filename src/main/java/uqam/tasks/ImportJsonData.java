package uqam.tasks;

import uqam.repositories.BixiRepository;
import uqam.repositories.ActivityRepository;
import uqam.resources.Lieu;
import uqam.resources.Activity;
import uqam.resources.Bixi;
import uqam.repositories.PisteRepository;
import uqam.resources.Piste;
import uqam.resources.Point;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;
import java.io.*;
import java.net.URL;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.*;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.examples.Utils;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonValidator;

@Service
public class ImportJsonData {

    private static final String ACTIVITY_JSON_FILE_PATH = "src/main/resources/programmation-parcs.json";
    private static final String PISTE_JSON_FILE_PATH = "src/main/resources/reseaucyclable2017juin2017.geojson";
    private static final String BIXI_URL = "https://secure.bixi.com/data/stations.json";
    private static final String PISTE_URL = "http://donnees.ville.montreal.qc.ca/dataset/5ea29f40-1b5b-4f34-85b3-7c67088ff536/resource/0dc6612a-be66-406b-b2d9-59c9e1c65ebf/download/reseaucyclable2017juin2017.geojson";

    private static final String ACTIVITY_JSON_SCHEMA_PATH = "src/main/resources/json_schema/Activity_Schema.json";
    private static final String BIXI_JSON_SCHEMA_PATH = "src/main/resources/json_schema/Bixi_Schema.json";
    private static final String PISTE_JSON_SCHEMA_PATH = "src/main/resources/json_schema/Pistes_Cyclables_Schema.json";

    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private BixiRepository bixiRepository;
    @Autowired
    private PisteRepository pisteRepository;

    /**
     * Trouve tous les objets Activity depuis la source : Fichier local et les
     * insere dans le repositoire
     *
     */
    @PostConstruct
    public void parseActivities() {

        try (FileReader reader = new FileReader(ACTIVITY_JSON_FILE_PATH)) {
            
            Activity test = activityRepository.findById(279);
            List<Activity> testList = activityRepository.findAll();
            // Parser du JsonFile
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(reader);
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

            reader.close();

        } catch (IOException e) {
            System.out.println("FILE NOT FOUND EXCEPTION ERROR: Activity Json or JsonSchema file not found.");
            System.out.println(e);
        } catch (ParseException | NumberFormatException e) {
            System.out.println("EXCEPTION ERROR: ImportJsonData.parseActivities() - ParseException or NumberFormatException");
            System.out.println(e);
        } catch (java.lang.Exception e) {
            System.out.println("EXCEPTION ERROR: ImportJsonData.parseActivities() - java.lang.Exception");
            System.out.println(e);
        }

    }

    /**
     * Trouve tous les objets Bixi depuis la source : URL et les insere dans le
     * repositoire
     *
     */
    //@Scheduled(cron = "0 */10 * * * *") // à toutes les 2 secondes.
    //@PostConstruct
    public void parseBixies() {
        try {
            RandomStation station = new RestTemplate().getForObject(BIXI_URL, RandomStation.class);
            station.randomBixiList.stream()
                    .map(this::asBixi)
                    .forEach((bixi) -> {
                        try {
                            bixiRepository.insert(bixi);
                        } catch (Exception ex) {
                            Logger.getLogger(ImportJsonData.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
        } catch (ResourceAccessException e) {
            System.out.println("EXCEPTION ERROR: Error when accessing Bixi json URL");
            System.out.println(e);
        } catch (java.lang.Exception e) {
            System.out.println("EXCEPTION ERROR: ImportJsonData.parseBixies() - java.lang.Exception");
            System.out.println(e);
        }

    }

    /**
     * Trouve toutes les objets Piste depuis la source : URL et les insere dans
     * le repositoire
     *
     */
    //@Scheduled(cron = "0 0 0 0 */6 *")
    //@PostConstruct
    public void parsePistes() {
        try (FileReader reader = new FileReader(PISTE_JSON_FILE_PATH)) {

            // Parser du JsonFile
            JSONParser parser = new JSONParser();
            JSONObject myObject = (JSONObject) parser.parse(reader);
            JSONObject feature;
            JSONObject properties;
            JSONObject geometry;
            JSONArray myArray = (JSONArray) myObject.get("features");
            JSONArray coordinatesFirstArray;
            JSONArray coordinatesSecondArray;
            JSONArray coordinatesFirstPoint;
            JSONArray coordinatesSecondPoint;
            List<Point> listePoints;
            Piste piste;

            // Boucle a travers tableau d objet json, cree activite et insere dans repertoire
            for (int i = 0; i < myArray.size(); i++) {
                feature = (JSONObject) myArray.get(i);
                properties = (JSONObject) feature.get("properties");
                geometry = (JSONObject) feature.get("geometry");
                coordinatesFirstArray = (JSONArray) geometry.get("coordinates");
                coordinatesSecondArray = (JSONArray) coordinatesFirstArray.get(0);
                coordinatesFirstPoint = (JSONArray) coordinatesSecondArray.get(0);
                coordinatesSecondPoint = (JSONArray) coordinatesSecondArray.get(1);
                listePoints = new ArrayList();
                listePoints.add(new Point(((Double)coordinatesFirstPoint.get(0)).doubleValue(), (Double) coordinatesFirstPoint.get(1)));
                listePoints.add(new Point((Double) coordinatesSecondPoint.get(0), (Double) coordinatesSecondPoint.get(1)));                
                
                piste = new Piste((int)(double)properties.get("ID"),
                        (int)(double) properties.get("TYPE_VOIE"),
                        (int)(double) properties.get("TYPE_VOIE2"),
                        (int)(double) properties.get("LONGUEUR"),
                        (int)(double) properties.get("NBR_VOIE"),
                        (String) properties.get("NOM_ARR_VI"),
                        listePoints
                );

                pisteRepository.insert(piste);
            }

            reader.close();

        } catch (IOException e) {
            System.out.println("FILE NOT FOUND EXCEPTION ERROR: Piste Json or JsonSchema file not found.");
            System.out.println(e);
        } catch (ParseException | NumberFormatException e) {
            System.out.println("EXCEPTION ERROR: ImportJsonData.parsePistes() - ParseException or NumberFormatException");
            System.out.println(e);
        } catch (java.lang.Exception e) {
            System.out.println("EXCEPTION ERROR: ImportJsonData.parsePistes() - java.lang.Exception");
            System.out.println(e);
        }
    }

    /**
     * Transforme un randomBixi en Bixi
     *
     * @param b
     * @return Bixi
     */
    private Bixi asBixi(RandomBixi b) {

        return new Bixi(
                b.id,
                b.stationName,
                b.stationId,
                b.stationState,
                b.stationIsBlocked,
                b.stationUnderMaintenance,
                b.stationOutOforder,
                b.millisLastUpdate,
                b.millisLastServerCommunication,
                b.bk,
                b.bl,
                b.lat,
                b.lng,
                b.availableTerminals,
                b.unavailableTerminals,
                b.availableBikes,
                b.unavailableBikes
        );
    }

    /**
     * Retourne si le fichier json est valide selon le schema
     *
     * @return boolean
     */
    @SuppressWarnings("ConvertToTryWithResources")
    private boolean jsonFileIsValid(final String jsonFilePath, final String jsonSchemaPath) throws IOException, ProcessingException {

        FileReader jsonReader = new FileReader(jsonFilePath);
        FileReader schemaReader = new FileReader(jsonSchemaPath);
        ObjectMapper mapperJson = new ObjectMapper();
        ObjectMapper mapperSchema = new ObjectMapper();
        final JsonNode activityJson = mapperJson.readTree(jsonReader);
        final JsonNode activityJsonSchema = mapperSchema.readTree(schemaReader);
        final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        final JsonSchema schema = factory.getJsonSchema(activityJsonSchema);
        ProcessingReport report = schema.validate(activityJson);
        jsonReader.close();
        schemaReader.close();
        return report.isSuccess();
    }

}

////////////////////////////////////////////////////////////////////
//                     
//                             BIXI
//
////////////////////////////////////////////////////////////////////
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
    String stationId;
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
    boolean bk;
    @JsonProperty("bl")
    boolean bl;
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

