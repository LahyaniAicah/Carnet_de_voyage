package fr.upjv.carnet_de_voyage;

public class Voyage {
    public int getId() {
        return id;
    }

    private int id;
    private String titre;
    private String dateDebut;
    private String dateFin;

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDateDebut(String dateDebut) {
        this.dateDebut = dateDebut;
    }

    public void setDateFin(String dateFin) {
        this.dateFin = dateFin;
    }





    public Voyage(int id, String titre, String dateDebut, String dateFin) {
        this.id = id;
        this.titre = titre;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;

    }

    public String getTitre() { return titre; }
    public String getDateDebut() { return dateDebut; }
    public String getDateFin() { return dateFin; }

}

