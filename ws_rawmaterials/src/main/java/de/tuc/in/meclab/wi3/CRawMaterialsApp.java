package de.tuc.in.meclab.wi3;

import de.tuc.in.meclab.wi3.rawmaterialsws.RawMaterialsWS;

import javax.xml.ws.Endpoint;

/**
 * RawMaterialsWS
 *
 */
public class CRawMaterialsApp
{
    public static void main( final String[] p_args )
    {
        Endpoint.publish(
                p_args.length >= 1 ? p_args[0] : "http://localhost:8081/RawMaterialsWS",
                new RawMaterialsWS(
                        p_args.length >= 1 ? p_args[0] : "http://localhost:8081/RawMaterialsWS"
                ) );
        System.out.println( "RawMaterials Webservice running" );
    }
}
