package fr.upjv.carnet_de_voyage.models;

public class Position {
    private double latitude;
    private double longitude;
    private String datetime;
    private String voyageId; // lien vers le document Firestore

    public Position() {}

    public Position(double latitude, double longitude, String datetime, String voyageId) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.datetime = datetime;
        this.voyageId = voyageId;
    }

    // Getters et setters
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getDatetime() { return datetime; }
    public void setDatetime(String datetime) { this.datetime = datetime; }

    public String getVoyageId() { return voyageId; }
    public void setVoyageId(String voyageId) { this.voyageId = voyageId; }
}
