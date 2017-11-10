package am.fats;

import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileWriter;
import java.io.IOException;

public class SVGParser extends DefaultHandler
{

    protected FileLineWriter mGCode;
    protected TransformationStack mTrans;
    protected SVGElement[] mElements = { new SVGCircle() };

    public void process(String inputSVGFileName, FileLineWriter gcode) throws IOException
    {
        mGCode = gcode;
        mTrans = new TransformationStack();

        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        SAXParser saxParser;
        org.xml.sax.XMLReader svgReader;
        try {
            saxParser = spf.newSAXParser();

            //Open the input SVG
            svgReader = saxParser.getXMLReader();
            try {
                svgReader.setContentHandler(this);
                svgReader.parse(inputSVGFileName);
            } catch (IOException e) {
                System.out.print("Cannot open SVG file "); System.out.println(inputSVGFileName);
                e.printStackTrace();
                return;
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        //We have an open SVG.  Write out the standard header
        mGCode.writeLine("M0"); //Disable screen
        mGCode.writeLine("G21"); //Units in mm
        mGCode.writeLine("G28"); //Reset to origin
        mGCode.writeLine("G90"); //All positions are absolute


        //Finish the gcode
        //Switch off tool
        mGCode.writeLine("G28"); //Reset to origin

        //enable screen
        mGCode.writeLine("M1");
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
    {
        System.out.println(localName);

        CutSpecification spec = findSpecfication(atts);

        //Get any transformations that need to be applied
        String transformationData = atts.getValue("transform");
        if(transformationData.trim().length() > 0)
        {
            TransformationParser tparser = new TransformationParser();
            mTrans = tparser.process(transformationData, mTrans);
        }

        if(spec != null)
        {
            for(SVGElement elem : mElements)
            {
                if(elem.acceptsElement(localName))
                {
                    try {
                        configureTool(spec);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    for(int i=0; i < spec.getRepeat(); i++)
                    {
                        try {
                            elem.process(atts, mGCode, mTrans.clone());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    protected CutSpecification findSpecfication(Attributes atts)
    {
        CutSpecification spec = null;
        String hexcode = atts.getValue("stroke");
        if(hexcode.length() > 0)
        {
            spec = Material.getSpecification(hexcode);
        }
        else
        {
            String style = atts.getValue("style");
            if(style.length() > 0)
            {
                String[] styles = style.split(";");
                for(String harry : styles)
                {
                    String[] nvp = harry.split(":");
                    if(nvp[0].contentEquals("stroke"))
                    {
                        spec = Material.getSpecification(nvp[1]);
                    }
                }
            }
        }
        return spec;
    }

    protected void configureTool(CutSpecification spec) throws IOException {
        GCodeComment comment = new GCodeComment("Setting feed rate");
        mGCode.writeLine(comment.toString());

        GCodeFeed feed = new GCodeFeed(spec.getFeedrate());
        mGCode.writeLine(feed.toString());


        //Set the tool
        Tool.setTool(spec.getTool());

        //Set the power (laser only)
        Tool.setPower(spec.getPower());
    }
}