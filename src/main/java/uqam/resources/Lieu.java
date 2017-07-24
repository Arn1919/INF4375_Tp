package uqam.resources;

import org.springframework.stereotype.*;

import com.fasterxml.jackson.annotation.*;

@Component
public class Lieu {

    private String nom;
    private double lat;
    private double lng;

    // Constructeurs
    public Lieu() {

    }

    public Lieu(String nom, double lng, double lat) {
        this.nom = nom;
        this.lng = lng;
        this.lat = lat;
    }

    // Getters
    @JsonProperty
    public String getNom() {
        return nom;
    }

    @JsonProperty
    public double getLat() {
        return lat;
    }

    @JsonProperty
    public double getLng() {
        return lng;
    }

    @Override
    public String toString() {
        return String.format("«%s» --%s", nom, lat);
    }
}
