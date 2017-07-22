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

    public Lieu(String nom, double lat, double lng) {
        this.nom = nom;
        this.lat = lat;
        this.lng = lng;
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
