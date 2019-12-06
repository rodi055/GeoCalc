package org.rawad.geocalc;

import javafx.geometry.Point3D;

public final class GeoCalc {
    private static final double SEMIMAJOR_AXIS = 6378137.0;
    private static final double SEMIMINOR_AXIS = 6356752.31424518;

    /**
     * aer2geodetic -  gives geodetic coordinates of a point with az, el, range
     * from an observer at lat0, lon0, h0
     *
     * @param az    azimuth to target [degree]
     * @param el    elevation to target [degree]
     * @param range slant range [meters]
     * @param lat0  Observer geodetic latitude
     * @param lon0  Observer geodetic longitude
     * @param h0    observer altitude above geodetic ellipsoid [meters]
     * @return geodetic
     */
    public static Point3D aer2geodetic(double az, double el, double range, double lat0, double lon0, double h0) {
        Point3D ecefPoint = aer2ecef(az, el, range, lat0, lon0, h0);
        return ecef2geodetic(ecefPoint.getX(), ecefPoint.getY(), ecefPoint.getZ());
    }

    /**
     * ecef2geodetic - convert ECEF (meters) to geodetic coordinates
     *
     * @param x target x ECEF coordinate (meters)
     * @param y target y ECEF coordinate (meters)
     * @param z target z ECEF coordinate (meters)
     * @return geodetic
     */
    public static Point3D ecef2geodetic(double x, double y, double z) {

        double r = Math.sqrt(square(x) + square(y) + square(z));

        double E = Math.sqrt(square(SEMIMAJOR_AXIS) - square(SEMIMINOR_AXIS));

//     eqn. 4a
        double u = Math.sqrt(0.5 * (square(r) - square(E)) + 0.5 *
                Math.sqrt(square(square(r) - square(E)) + 4 * square(E) * square(z)));

        double Q = Math.hypot(x, y);

        double huE = Math.hypot(u, E);

//     eqn. 4b
        double beta;
        try {
            beta = Math.atan(huE / u * z / Math.hypot(x, y));
        } catch (RuntimeException e) {
            if (z >= 0) {
                beta = Math.PI / 2;
            } else {
                beta = -Math.PI / 2;
            }
        }

//     eqn. 13
        double eps = ((SEMIMINOR_AXIS * u - SEMIMAJOR_AXIS * huE + square(E)) *
                Math.sin(beta)) / (SEMIMAJOR_AXIS * huE * 1 / Math.cos(beta) - square(E) * Math.cos(beta));
        beta += eps;
//     %% final output
        double lat = Math.atan(SEMIMAJOR_AXIS / SEMIMINOR_AXIS * Math.tan(beta));
        double lon = Math.atan2(y, x);

//     eqn. 7
        double alt = Math.hypot(z - SEMIMINOR_AXIS * Math.sin(beta), Q - SEMIMAJOR_AXIS * Math.cos(beta));

//     inside ellipsoid?
        boolean inside = square(x) / square(SEMIMAJOR_AXIS) + square(y) / square(SEMIMAJOR_AXIS) +
                square(z) / square(SEMIMINOR_AXIS) < 1;
        if (inside) {
            alt = -alt;
        }

        return new Point3D(Math.toDegrees(lat), Math.toDegrees(lon), (alt));
    }

    /**
     * aer2ecef - converts target azimuth, elevation, range from observer at lat0,lon0,alt0 to ECEF coordinates.
     *
     * @param az    azimuth to target [degree]
     * @param el    elevation to target [degree]
     * @param range slant range [meters]
     * @param lat0  Observer geodetic latitude
     * @param lon0  Observer geodetic longitude
     * @param h0    observer altitude above geodetic ellipsoid [meters]
     * @return ECEF (Earth centered, Earth fixed)  x,y,z [meters]
     */
    public static Point3D aer2ecef(double az, double el, double range, double lat0, double lon0, double h0) {
        Point3D ecefPoint = geodetic2ecef(lat0, lon0, h0);
//     Convert Local Spherical AER to ENU
        Point3D enuPoint = aer2enu(az, el, range);
//     Rotating ENU to ECEF
        Point3D uvwPoint = enu2uvw(enuPoint.getX(), enuPoint.getY(), enuPoint.getZ(), lat0, lon0);
//     Origin + offset from origin equals position in ECEF

        return new Point3D(ecefPoint.getX() + uvwPoint.getX(),
                ecefPoint.getY() + uvwPoint.getY(),
                ecefPoint.getZ() + uvwPoint.getZ());
    }

    /**
     * @param east  target east ENU coordinate (meters)
     * @param north target north ENU coordinate (meters)
     * @param up    target up ENU coordinate (meters)
     * @param lat0  Observer geodetic latitude
     * @param lon0  Observer geodetic longitude
     * @return u v w
     */
    public static Point3D enu2uvw(double east, double north, double up, double lat0, double lon0) {
        double latInRad = Math.toRadians(lat0);
        double lonInRad = Math.toRadians(lon0);
        double t = Math.cos(latInRad) * up - Math.sin(latInRad) * north;
        double w = Math.sin(latInRad) * up + Math.cos(latInRad) * north;
        double u = Math.cos(lonInRad) * t - Math.sin(lonInRad) * east;
        double v = Math.sin(lonInRad) * t + Math.cos(lonInRad) * east;

        return new Point3D(u, v, w);
    }

    /**
     * aer2enu - Azimuth, Elevation, Slant range to target to East, north, Up
     *
     * @param az    azimuth clockwise from north (degrees)
     * @param el    elevation angle above horizon, neglecting aberattions (degrees)
     * @param range slant range [meters]
     * @return enu coordinate (meters)
     */
    public static Point3D aer2enu(double az, double el, double range) {
        az = Math.toRadians(az);
        el = Math.toRadians(el);
        double r = range * Math.cos(el);

        return new Point3D(r * Math.sin(az), r * Math.cos(az), range * Math.sin(el));
    }

    /**
     * geodetic2ecef - point transformation from Geodetic of specified ellipsoid (default WGS-84) to ECEF
     *
     * @param lat0 target geodetic latitude
     * @param lon0 target geodetic longitude
     * @param h0   target altitude above geodetic ellipsoid (meters)
     * @return ECEF (Earth centered, Earth fixed)  x,y,z (meters)
     */
    public static Point3D geodetic2ecef(double lat0, double lon0, double h0) {
        double latInRad = Math.toRadians(lat0);
        double lonInRad = Math.toRadians(lon0);
//     radius of curvature of the prime vertical section
        double N = square(SEMIMAJOR_AXIS) /
                Math.sqrt(square(SEMIMAJOR_AXIS) * square(Math.cos(latInRad)) +
                        square(SEMIMINOR_AXIS) * square(Math.sin(latInRad)));
//     Compute cartesian (geocentric) coordinates given  (curvilinear) geodetic coordinates.
        double x = (N + h0) * Math.cos(latInRad) * Math.cos(lonInRad);
        double y = (N + h0) * Math.cos(latInRad) * Math.sin(lonInRad);
        double z = (N * square(SEMIMINOR_AXIS / SEMIMAJOR_AXIS) + h0) * Math.sin(latInRad);

        return new Point3D(x, y, z);
    }

    private static double square(double num) {
        return Math.pow(num, 2);
    }
}
