package de.tuc.in.meclab.wi3;

import de.tuc.in.meclab.wi3.webservice.OrderWS;

import javax.xml.ws.Endpoint;

public class COrderApp
{
    public static void main( final String[] p_args )
    {
        Endpoint.publish(
                p_args.length >= 1 ? p_args[0] : "http://localhost:8083/OrderWS",
                new OrderWS(
                        p_args.length >= 1 ? p_args[0] : "http://localhost:8083/OrderWS"
                ) );
        System.out.println( "Order Webservice running." );
    }
}
