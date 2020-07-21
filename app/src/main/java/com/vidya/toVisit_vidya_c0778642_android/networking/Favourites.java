package com.vidya.toVisit_vidya_c0778642_android.networking;

public
class Favourites {

    private int _id;
    private String favAddress;
    private String favDate;
    private double favLat;
    private double favLng;
    private boolean favVisited;

    public
    Favourites(int _id, String favAddress, String favDate, double favLat, double favLng, boolean favVisited) {
        this._id        = _id;
        this.favAddress = favAddress;
        this.favDate    = favDate;
        this.favLat     = favLat;
        this.favLng     = favLng;
        this.favVisited = favVisited;
    }
    public
    Favourites(String favAddress, String favDate, double favLat, double favLng, boolean favVisited) {
//        this._id        = _id;
        this.favAddress = favAddress;
        this.favDate    = favDate;
        this.favLat     = favLat;
        this.favLng     = favLng;
        this.favVisited = favVisited;
    }

    public
    Favourites(int id, boolean checked) {
        this._id        = id;
        this.favVisited = checked;

    }

    public
    int get_id() {
        return _id;
    }

    public
    void set_id(int _id) {
        this._id = _id;
    }

    public
    String getFavAddress() {
        return favAddress;
    }

    public
    void setFavAddress(String favAddress) {
        this.favAddress = favAddress;
    }

    public
    String getFavDate() {
        return favDate;
    }

    public
    void setFavDate(String favDate) {
        this.favDate = favDate;
    }

    public
    double getFavLat() {
        return favLat;
    }

    public
    void setFavLat(double favLat) {
        this.favLat = favLat;
    }

    public
    double getFavLng() {
        return favLng;
    }

    public
    void setFavLng(double favLng) {
        this.favLng = favLng;
    }

    public
    boolean isFavVisited() {
        return favVisited;
    }

    public
    void setFavVisited(boolean favVisited) {
        this.favVisited = favVisited;
    }

}
