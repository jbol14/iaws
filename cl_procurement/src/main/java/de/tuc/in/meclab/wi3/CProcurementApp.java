package de.tuc.in.meclab.wi3;

import de.tuc.in.meclab.wi3.webservice.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import de.tuc.in.meclab.wi3.common.*;
import de.tuc.in.meclab.wi3.common.materialstamm.Material;
import de.tuc.in.meclab.wi3.common.xml.CXStream;

/**
 * GBI-Procurement Client
 *
 */
public class CProcurementApp
{
    public static void main( final String[] p_args )
    {
        System.out.println( "GBI-Procurement Client" );
        System.out.println( "Rufe Webservice RawMaterialsWS auf" );
        
        RawMaterialsWSService service = new RawMaterialsWSService();
        RawMaterialsWS raw = service.getRawMaterialsWSPort();
        CXStream xmlConverter = new CXStream();
        
        MarketWSService marketService = new MarketWSService();
        MarketWS market = marketService.getMarketWSPort();
        
        OrderWSService orderService = new OrderWSService();
        OrderWS order = orderService.getOrderWSPort();
        
        //RawMaterials aufrufen
        String rawMaterialsXml = raw.getRawMaterials("LEARN-103", "tlestart1");
        
        //System.out.println(rawMaterialsXml);
        
        List<Material> rawMaterials = (ArrayList<Material>) xmlConverter.fromXML(rawMaterialsXml);
        
        for(Material m : rawMaterials) {
        	System.out.println(m.description() + " " + m.number());
        }
        
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Bitte die gewünschte Teilnummer eingeben");
        String partNumber = scanner.nextLine();
        
        
        
        System.out.println( "Rufe Webservice MarketWS auf" );
        String str = market.getBestOffer(partNumber, "200.00");
        
        System.out.println(str);
        
        System.out.println( "Rufe Webservice OrderWS auf" );
        String result = order.placeOrder("LEARN-103", "tlestart1", str);
        
        System.out.println(result);
    }
}
