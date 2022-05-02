class Location {
    int id;
    double latitude, longitude;

    public Location(int id, double latitude, double longitude) {
        this.id = id;
        this.latitude = Math.toRadians(latitude);
        this.longitude = Math.toRadians(longitude);
    }


    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
