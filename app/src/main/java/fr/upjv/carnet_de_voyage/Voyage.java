package fr.upjv.carnet_de_voyage;

public class Voyage {
    private int id;
    private String titre;
    private String dateDebut;
    private String dateFin;
    private String intervalle;

    public Voyage(int id, String titre, String dateDebut, String dateFin, String intervalle) {
        this.id = id;
        this.titre = titre;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.intervalle = intervalle;
    }

    public String getTitre() { return titre; }
    public String getDateDebut() { return dateDebut; }
    public String getDateFin() { return dateFin; }
    public String getIntervalle() { return intervalle; }
}

