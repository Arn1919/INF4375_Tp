
package uqam.resources;

/* Libraries */

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.*;

import com.fasterxml.jackson.annotation.*;

@Component
public class Activity {
  private int id;
  private String nom;
  private String description;
  private String arrondissement;
  private List<Date> dates;
  private Lieu lieu;

  // Constructeurs
  public Activity(){

  }

  public Activity(int id, String nom, String description, String arrondissement, List<Date> dates, Lieu lieu) {
    this.id = id;
    this.nom = nom;
    this.description = description;
    this.arrondissement = arrondissement;
    this.dates = dates;
    this.lieu = lieu;
  }

   // Getters
  @JsonProperty public int getId() { return id; }
  @JsonProperty public String getNom() { return nom; }
  @JsonProperty public String getDescription() { return description; }
  @JsonProperty public String getArrondissement() { return arrondissement; }
  @JsonProperty public List<Date> getDates() { return dates; }
  @JsonProperty public Lieu getLieu() { return lieu;}
  // Setters
  @JsonProperty public void setId(int id){ this.id = id; }
  @JsonProperty public void setNom(String nom) { this.nom = nom; }
  @JsonProperty public void setDescription(String description) { this.description = description; }
  @JsonProperty public void setArrondissement(String arrondissement) { this.arrondissement = arrondissement; }
  @JsonProperty public void setDates(List<Date> dates) { this.dates = dates; }
  @JsonProperty public void setLieu(Lieu lieu) { this.lieu = lieu; }

  @Override public String toString() {
    return String.format("«%s» --%s", nom, description, arrondissement, dates, lieu);
  }
}
