package fr.upjv.carnet_de_voyage.models;

public class Voyage {

    private String id; // Firestore utilise une String comme identifiant// Firebase supporte mieux Integer que int
    private String titre;
    private String dateDebut;
    private String dateFin;
    private String user;

    // 🔹 Constructeur sans argument (obligatoire pour Firebase)
    public Voyage() {// obligatoire pour Firestore
    }

    // 🔹 Constructeur principal
    public Voyage(String id, String titre, String dateDebut, String dateFin) {
        this.id = id;
        this.titre = titre;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    // 🔹 Getters et setters
    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(String dateDebut) {
        this.dateDebut = dateDebut;
    }

    public String getDateFin() {
        return dateFin;
    }

    public void setDateFin(String dateFin) {
        this.dateFin = dateFin;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
