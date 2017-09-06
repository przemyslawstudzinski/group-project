package sample;


import java.util.ArrayList;
import java.util.List;

public class ClicksContainer
{
    public static List<Click> clicksList = new ArrayList<Click>();

    public class Click
    {
        public int x;
        public int y;
        long delay;
        boolean isDouble;

        public Click(int x, int y, long delay, boolean isDouble)
        {
            this.x = x;
            this.y = y;
            this.delay = delay;
            this.isDouble = isDouble;
        }
    }
}



