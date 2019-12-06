import javafx.geometry.Point3D;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rawad.geocalc.GeoCalc;

class GeoCalcTests {
    private static final double DELTA = 0.0000001;

    @Test
    void testAer2Geodetic() {
        Point3D expectedResult = new Point3D(34.24598189, 32.23743112, 801.02382184);
        Point3D actualResult = GeoCalc.aer2geodetic(270, 0, 91440, 34.25, 33.23, 146.304);
        System.out.println("Expected: " + expectedResult);
        System.out.println("Result  : " + actualResult);
        Assertions.assertEquals(expectedResult.getX(), actualResult.getX(), DELTA);
        Assertions.assertEquals(expectedResult.getY(), actualResult.getY(), DELTA);
        Assertions.assertEquals(expectedResult.getZ(), actualResult.getZ(), DELTA);
    }

    @Test
    void testAer2Ecef() {
        Point3D expectedResult = new Point3D(4464888.640990304, 2815765.744895992, 3569485.1750017917);
        Point3D actualResult = GeoCalc.aer2ecef(270, 0, 91440, 34.25, 33.23, 146.304);
        System.out.println("Expected: " + expectedResult);
        System.out.println("Result  : " + actualResult);
        Assertions.assertEquals(expectedResult.getX(), actualResult.getX());
        Assertions.assertEquals(expectedResult.getX(), actualResult.getX(), DELTA);
        Assertions.assertEquals(expectedResult.getY(), actualResult.getY(), DELTA);
        Assertions.assertEquals(expectedResult.getZ(), actualResult.getZ(), DELTA);
    }

    @Test
    void testEcef2Geodetic() {
        Point3D expectedResult = new Point3D(34.24598189, 32.23743112, 801.02382184);
        Point3D actualResult = GeoCalc.ecef2geodetic(4464888.640990304, 2815765.744895992, 3569485.1750017917);
        System.out.println("Expected: " + expectedResult);
        System.out.println("Result  : " + actualResult);
        Assertions.assertEquals(expectedResult.getX(), actualResult.getX(), DELTA);
        Assertions.assertEquals(expectedResult.getY(), actualResult.getY(), DELTA);
        Assertions.assertEquals(expectedResult.getZ(), actualResult.getZ(), DELTA);
    }

    @Test
    void testGeodetic2Ecef() {
        Point3D expectedResult = new Point3D(4414779.404204623, 2892253.247069592, 3569485.1750017917);
        Point3D actualResult = GeoCalc.geodetic2ecef(34.25, 33.23, 146.304);
        System.out.println("Expected: " + expectedResult);
        System.out.println("Result  : " + actualResult);
        Assertions.assertEquals(expectedResult.getX(), actualResult.getX(), DELTA);
        Assertions.assertEquals(expectedResult.getY(), actualResult.getY(), DELTA);
        Assertions.assertEquals(expectedResult.getZ(), actualResult.getZ(), DELTA);
    }

    @Test
    void testAer2Enu() {
        Point3D expectedResult = new Point3D(-91440, 0, 0);
        Point3D actualResult = GeoCalc.aer2enu(270, 0, 91440);
        System.out.println("Expected: " + expectedResult);
        System.out.println("Result  : " + actualResult);
        Assertions.assertEquals(expectedResult.getX(), actualResult.getX(), DELTA);
        Assertions.assertEquals(expectedResult.getY(), actualResult.getY(), DELTA);
        Assertions.assertEquals(expectedResult.getZ(), actualResult.getZ(), DELTA);
    }

    @Test
    void testEnu2Uvw() {
        Point3D expectedResult = new Point3D(50109.23678568162, -76487.5021736, 0.0);
        Point3D actualResult = GeoCalc.enu2uvw(-91440, 0, 0, 34.25, 33.23);
        System.out.println("Expected: " + expectedResult);
        System.out.println("Result  : " + actualResult);
        Assertions.assertEquals(expectedResult.getX(), actualResult.getX(), DELTA);
        Assertions.assertEquals(expectedResult.getY(), actualResult.getY(), DELTA);
        Assertions.assertEquals(expectedResult.getZ(), actualResult.getZ(), DELTA);
    }
}