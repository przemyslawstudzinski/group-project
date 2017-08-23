import java.util.LinkedHashMap;

public class CoordsContainer {
    public static LinkedHashMap<Coords, Long> coordsMap = new LinkedHashMap<Coords, Long>();

    public class Coords
    {
        public int x;
        public int y;

        public Coords(int x, int y)
        {
            this.x = x;
            this.y = y;
        }
    }
}
