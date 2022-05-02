class Distance {
    public static double between(Location a, Location b) {
        final double radius = 6371;

        double lat_diff = (a.latitude - b.latitude);
        double long_diff = (a.longitude - b.longitude);

        double a1 = Math.pow(Math.sin(lat_diff / 2), 2) + Math.pow(Math.sin(long_diff / 2), 2) * Math.cos(a.latitude)
                * Math.cos(b.latitude);
        return radius * 2 * Math.asin(Math.sqrt(a1));
    }
}
