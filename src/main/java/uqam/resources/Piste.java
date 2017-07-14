package uqam.resources;

import java.util.List;

/**
 *
 * @author arnaud
 */
public class Piste {

    private int id;
    private int typeVoie1;
    private int typeVoie2;
    private int longueur;
    private int nbrVoie;
    private String nomArrVille;
    private List<Point> coordinates;


    public Piste() {

    }
    
    public Piste(int id, int typeVoie1, int typeVoie2, int longueur, int nbrVoie, String nomArrVille, List<Point> coordinates){
        this.id = id;
        this.typeVoie1 = typeVoie1;
        this.typeVoie2 = typeVoie2;
        this.longueur = longueur;
        this.nbrVoie = nbrVoie;
        this.nomArrVille = nomArrVille;
        this.coordinates = coordinates;
    }
    
    // Getters
    public int getId(){ return id; }
    public int getTypeVoie1() { return typeVoie1; }
    public int getTypeVoie2() { return typeVoie2; }
    public int getLongueur() { return longueur; }
    public int getNbreVoie() { return nbrVoie; }
    public String getNomArrVille() { return nomArrVille; }
    public List<Point> getCoordinates(){ return coordinates; }
    
}
