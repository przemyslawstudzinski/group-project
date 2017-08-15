package sample;


import java.util.ArrayList;

public class CoordsContainer
{
    public static ArrayList<Coords> coordsList = new ArrayList<Coords>();

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

