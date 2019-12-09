package de.tuc.in.meclab.wi3;

import javax.xml.ws.Endpoint;

import de.tuc.in.meclab.wi3.market.MarketWS;

public class CMarketApp
{
    public static void main( final String[] p_args )
    {
    	Endpoint.publish(p_args.length >= 1 ? p_args[0] : "http://localhost:8082/MarketWS", new MarketWS(
    			p_args.length >= 1 ? p_args[0] : "http://localhost:8082/MarketWS")
    	);
        System.out.println( "Webservice running" );
    }
}
