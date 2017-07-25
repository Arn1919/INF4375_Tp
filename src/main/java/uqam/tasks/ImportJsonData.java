package uqam.tasks;

import uqam.errors.JsonSchemaException;
import uqam.repositories.*;
import uqam.resources.*;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;
import java.io.*;
import java.net.URL;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.client.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.*;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;

@Service
public class ImportJsonData {

    // Coordonnees par defaut : Pavillon PK UQAM
    private static final double DEFAULT_LAT = 45.50894093;
    private static final double DEFAULT_LNG = -73.56863737;
    private static final int DEFAULT_RADIUS = 5000;
    // Acces local - Fichiers JSON
    private static final String ACTIVITY_JSON_FILE_PATH = "src/main/resources/programmation-parcs.json";
    private static final String PISTE_JSON_FILE_PATH = "src/main/resources/reseaucyclable2017juin2017.geojson";
    // Acces Web via URL
    private static final String BIXI_URL = "https://secure.bixi.com/data/stations.json";
    private static final String PISTE_URL = "http://donnees.ville.montreal.qc.ca/dataset/5ea29f40-1b5b-4f34-85b3-7c67088ff536/resource/0dc6612a-be66-406b-b2d9-59c9e1c65ebf/download/reseaucyclable2017juin2017.geojson";
    // Acces local - Fichiers JSON Schema
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

        // Validation du json par le schema
        try (FileReader reader = new FileReader(ACTIVITY_JSON_FILE_PATH)) {
            // Valide le json selon JsonSchema
            ProcessingReport validationSchema = jsonFileLocalIsValid(ACTIVITY_JSON_FILE_PATH, ACTIVITY_JSON_SCHEMA_PATH);
            if (validationSchema.isSuccess()) {

                JSONParser parser = new JSONParser();
                Object obj = parser.parse(reader);
                JSONArray myArray = (JSONArray) obj;
                JSONObject myObject;
                JSONObject myObjectLieu;
                ArrayList<String> arrayDatesString;
                List<Date> listDates;
                Activity activity;

                // Boucle a travers tableau d objet json, cree activite et insere dans repertoire
                for (int i = 0; i < myArray.size(); i++) {
                    myObject = (JSONObject) myArray.get(i);
                    myObjectLieu = (JSONObject) myObject.get("lieu");
                    arrayDatesString = (ArrayList<String>) myObject.get("dates");
                    listDates = new ArrayList<>();
                    // Initialise la liste de Dates
                    for (String s : arrayDatesString) {
                        Calendar calendar = javax.xml.bind.DatatypeConverter.parseDateTime(s);
                        listDates.add(new Date(calendar.getTimeInMillis()));
                    }
                    // Construit une activite avec ou sans coordonnees
                    if (myObjectLieu.containsKey("lat") && myObjectLieu.containsKey("lng")) {
                        activity = new Activity(Integer.parseInt((String) myObject.get("id")),
                                (String) myObject.get("nom"),
                                (String) myObject.get("description"),
                                (String) myObject.get("arrondissement"),
                                listDates,
                                new Lieu((String) myObjectLieu.get("nom"),
                                        (double) myObjectLieu.get("lng"),
                                        (double) myObjectLieu.get("lat")
                                )
                        );
                    } else { // N'a pas de coordonnees
                        activity = new Activity(Integer.parseInt((String) myObject.get("id")),
                                (String) myObject.get("nom"),
                                (String) myObject.get("description"),
                                (String) myObject.get("arrondissement"),
                                listDates,
                                new Lieu((String) myObjectLieu.get("nom"),
                                        DEFAULT_LNG,
                                        DEFAULT_LAT
                                )
                        );
                    }
                    activityRepository.insert(activity);
                }
                reader.close();
            } else {
                reader.close();
                throw new JsonSchemaException(validationSchema.toString());
            }

        } catch (IOException e) {
            System.out.println("FILE NOT FOUND EXCEPTION ERROR: Activity Json or JsonSchema file not found.");
            System.out.println(e);
        } catch (JsonSchemaException e) {
            System.out.println("EXCEPTION ERROR: ImportJsonData.parseActivities() - Le format Json ne respecte pas le schema");
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
    @Scheduled(cron = "* */10 * * * ?") // à toutes les 2 secondes.
    @PostConstruct
    public void parseBixies() {
        try {
            // Valide le json selon JsonSchema
            ProcessingReport validationSchema = jsonFileWebIsValid(new URL(BIXI_URL), BIXI_JSON_SCHEMA_PATH);
            if (validationSchema.isSuccess()) {
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
            } else {
                throw new JsonSchemaException(validationSchema.toString());
            }
        } catch (ResourceAccessException e) {
            System.out.println("EXCEPTION ERROR: ImportJsonData.parseBixies() - Error when accessing Bixi json URL");
            System.out.println(e);
        } catch (JsonSchemaException e) {
            System.out.println("EXCEPTION ERROR: ImportJsonData.parseBixies() - Le format Json ne respecte pas le schema");
            System.out.println(e);
        } catch (IOException | ProcessingException | RestClientException e) {
            System.out.println("EXCEPTION ERROR: ImportJsonData.parseBixies() - java.lang.Exception");
            System.out.println(e);
        }

    }

    /**
     * Trouve toutes les objets Piste depuis la source : URL et les insere dans
     * le repositoire
     *
     */
    @Scheduled(cron = "* * * * */6 ?")
    @PostConstruct
    public void parsePistes() {
        // Telecharge localement le fichier a l'URL specifier
        try {
            telechargerFichierPiste(PISTE_URL, PISTE_JSON_FILE_PATH);
        } catch (IOException e) {
            System.out.println("FILE NOT FOUND EXCEPTION ERROR: Piste Json or JsonSchema file not found.");
            System.out.println(e);
        }
        // Commence la lecture du fichier JSON format GEOJSON
        try (FileReader reader = new FileReader(PISTE_JSON_FILE_PATH)) {

            // Valide le json selon JsonSchema
            ProcessingReport validationSchema = jsonFileLocalIsValid(PISTE_JSON_FILE_PATH, PISTE_JSON_SCHEMA_PATH);
            if (validationSchema.isSuccess()) {
                // Parser du JsonFile
                JSONParser parser = new JSONParser();
                JSONObject myObject = (JSONObject) parser.parse(reader);
                JSONObject feature;
                JSONObject properties;
                JSONObject geometry;
                JSONArray features = (JSONArray) myObject.get("features");
                JSONArray multiLinesStringArray;
                JSONArray lineStringArray;
                JSONArray pointArray;
                Piste piste;
                String multiLineString;
                // Boucle a travers tableau d objet json, cree activite et insere dans repertoire
                for (int i = 0; i < features.size(); i++) {
                    // Chargement du feature
                    feature = (JSONObject) features.get(i);
                    properties = (JSONObject) feature.get("properties");
                    geometry = (JSONObject) feature.get("geometry");
                    multiLinesStringArray = (JSONArray) geometry.get("coordinates");

                    // Construction de la string Multilinestring
                    multiLineString = "";
                    multiLineString += "MULTILINESTRING(";
                    for (int j = 0; j < multiLinesStringArray.size(); j++) {

                        multiLineString += "("; // Debut LineString
                        lineStringArray = (JSONArray) multiLinesStringArray.get(j);

                        for (int k = 0; k < lineStringArray.size(); k++) {

                            pointArray = (JSONArray) lineStringArray.get(k);
                            multiLineString += (double) pointArray.get(0);
                            multiLineString += " ";
                            multiLineString += (double) pointArray.get(1);

                            // N'est pas le dernier point du array ??
                            if (k != lineStringArray.size() - 1) {
                                multiLineString += ", ";
                            }
                        }
                        multiLineString += ")"; // Fin LineString
                        // N'est pas la derniere linestring du array??
                        if (j != multiLinesStringArray.size() - 1) {
                            multiLineString += ", ";
                        }
                    }
                    multiLineString += ")"; // Termine le multilinestring

                    piste = new Piste((int) (double) properties.get("ID"),
                            (int) (double) properties.get("TYPE_VOIE"),
                            (int) (double) properties.get("TYPE_VOIE2"),
                            (int) (double) properties.get("LONGUEUR"),
                            (int) (double) properties.get("NBR_VOIE"),
                            (String) properties.get("NOM_ARR_VI"),
                            multiLineString
                    );
                    pisteRepository.insert(piste);

                }
                reader.close();
            } else {
                reader.close();
                throw new JsonSchemaException(validationSchema.toString());
            }

        } catch (IOException e) {
            System.out.println("FILE NOT FOUND EXCEPTION ERROR: Piste Json or JsonSchema file not found.");
            System.out.println(e);
        } catch (ParseException | NumberFormatException e) {
            System.out.println("EXCEPTION ERROR: ImportJsonData.parsePistes() - ParseException or NumberFormatException");
            System.out.println(e);
        } catch (JsonSchemaException e) {
            System.out.println("EXCEPTION ERROR: ImportJsonData.parsePistes() - Le format Json ne respecte pas le schema");
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
                b.lng,
                b.lat,
                b.availableTerminals,
                b.unavailableTerminals,
                b.availableBikes,
                b.unavailableBikes
        );
    }

    /**
     * Retourne le rapport du fichier json a l'URL si valide selon le schema
     * local
     *
     * @return ProcessingReport
     */
    private ProcessingReport jsonFileWebIsValid(final URL jsonFilePath, final String jsonSchemaPath) throws IOException, ProcessingException {

        FileReader schemaReader = new FileReader(jsonSchemaPath);
        ObjectMapper mapperJson = new ObjectMapper();
        ObjectMapper mapperSchema = new ObjectMapper();
        final JsonNode jsonNode = mapperJson.readTree(jsonFilePath);
        final JsonNode jsonSchemaNode = mapperSchema.readTree(schemaReader);
        final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        final JsonSchema schema = factory.getJsonSchema(jsonSchemaNode);
        ProcessingReport report = schema.validate(jsonNode);
        schemaReader.close();
        return report;
    }

    /**
     * Retourne le rapport du fichier stocke localement json est valide selon le
     * schema
     *
     * @return ProcessingReport
     */
    private ProcessingReport jsonFileLocalIsValid(final String jsonFilePath, final String jsonSchemaPath) throws IOException, ProcessingException {

        FileReader jsonReader = new FileReader(jsonFilePath);
        FileReader schemaReader = new FileReader(jsonSchemaPath);
        ObjectMapper mapperJson = new ObjectMapper();
        ObjectMapper mapperSchema = new ObjectMapper();
        final JsonNode jsonNode = mapperJson.readTree(jsonReader);
        final JsonNode jsonSchemaNode = mapperSchema.readTree(schemaReader);
        final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        final JsonSchema schema = factory.getJsonSchema(jsonSchemaNode);
        ProcessingReport report = schema.validate(jsonNode);
        jsonReader.close();
        schemaReader.close();
        return report;
    }

    /**
     * Telecharge le fichier localement au cheminement specifie - Si le fichier
     * existe deja, il efface le fichier
     *
     * @param url
     * @throws IOException
     */
    private void telechargerFichierPiste(String url, String pathLocal) throws IOException {
        Path path = FileSystems.getDefault().getPath(pathLocal);
        Files.deleteIfExists(path);
        InputStream in = new URL(url).openStream();
        try {
            Files.copy(in, Paths.get(pathLocal));
            in.close();
        } catch (IOException e) {
            in.close();
            throw new IOException();
        }

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
