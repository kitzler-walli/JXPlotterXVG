package am.fats;

import java.io.IOException;
import java.util.ArrayDeque;

public class SVGPath_Move_Rel extends SVGPath_Command
{
    public SVGPath_Move_Rel()
    {}


    @Override
    public void process(ArrayDeque<String> commandQueue, FileLineWriter gcode, TransformationStack trans) throws IOException
    {
        boolean isFirst = true;
        double y = 0;
        double x = 0;
        while(mIsOk)
        {
            //Read x value
            x = readDouble(commandQueue);

            if(mIsOk)
            {
                y = readDouble(commandQueue);
            }

            if(mIsOk)
            {
                Point2D p = PlotterState.getLogicalPosition();

                //VERY ANNOYING
                //*sometimes* path move commands are actually lineto!
                if(isFirst)
                {
                    //Head up if necessary
                    headUp(gcode);

                    //We have an X and Y double from the command queue
                    GCodeMove move = new GCodeMove(p.x + x, p.y + y);
                    move.setTransformationStack(trans);
                    gcode.writeLine(move.toString());

                    isFirst = false;
                }
                else
                {
                    //Head up if necessary
                    headDown(gcode);

                    //We have an X and Y double from the command queue
                    GCodeLine line = new GCodeLine(p.x + x, p.y + y);
                    line.setTransformationStack(trans);
                    gcode.writeLine(line.toString());
                }
            }

            //Loop until we are not OK (end of data or found another command)
        }
    }
}