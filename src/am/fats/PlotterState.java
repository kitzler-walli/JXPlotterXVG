package am.fats;

import java.awt.*;

public class PlotterState
{

    protected static boolean sHeadDown = false;
    protected static Point2D sCurrentPosition = new Point2D(0, 0); //Not actually current position
    protected static Point2D sMarkedPosition = new Point2D(0, 0);  //Not actually marked
    protected static Point2D sControlPos = new Point2D(0, 0);      //Not actaully a control
    protected static Point2D sViewBox = new Point2D(200, 200);     //THIS IS THE VIEWBOX! (not used)
    protected static Point2D sLogicalPosition = new Point2D(0, 0); //Not actually logical

    public PlotterState()
    {}

    public static Point2D getPosition()
    {
        return sCurrentPosition;
    }

    public static void setPosition(Point2D p)
    {
        sCurrentPosition.x = p.x;
        sCurrentPosition.y = p.y;
    }

    public static void setLogicalPosition(double x, double y)
    {
        sLogicalPosition.x = x;
        sLogicalPosition.y = y;
    }

    public static Point2D getLogicalPosition()
    {
        return sLogicalPosition;
    }

    public static void setHeadDown()
    {
        sHeadDown = true;
    }

    public static void setHeadUp()
    {
        sHeadDown = false;
    }

    public static boolean isHeadDown()
    {
        return sHeadDown;
    }

    public static void markPosition()
    {
        sMarkedPosition.x = sLogicalPosition.x;
        sMarkedPosition.y = sLogicalPosition.y;
    }

    public static Point2D getMarkedPosition()
    {
        return sMarkedPosition;
    }

    public static void setControlPosition(Point2D point)
    {
        sControlPos.x = point.x;
        sControlPos.y = point.y;
    }

    public static Point2D getViewBox()
    {
        return sViewBox;
    }
}