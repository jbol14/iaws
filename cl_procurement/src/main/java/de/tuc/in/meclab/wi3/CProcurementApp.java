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
    	boolean isValidPartnumber = false;
    	boolean isValidBudget = false;
    	
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
        String partNumber = "";
        //Sicherstellen, dass Parnumber gültig ist, d.h. es existiert ein Teil in rawMaterials mit dieser Nummer
        while(!isValidPartnumber) {
        	System.out.print("Bitte die gewünschte Teilnummer eingeben: ");
        	partNumber = scanner.nextLine();
        	//Teilnummer suchen
        	for(Material m : rawMaterials) {
        		if(m.number().contentEquals(partNumber)) {
        			isValidPartnumber = true;
        			break;
        		}
        	}
        
        }
        
        String budget = "";
        //Sicherstellen, dass Budget gültig ist, d.h. in Double geparst werden kann        
        while(!isValidBudget) {
        	System.out.print("Bitte Budget eingeben: ");
        	budget = scanner.nextLine();
        	
        	//Falls das Budget mit Komma anggegeben wurde wird das Komma durch Punkt ersetzt damit es geparst werden kann
        	if(budget.contains(",")) {
        		budget = budget.replace(',', '.');
        		
        	}
        	
        	try {
        		Double.parseDouble(budget);
        		isValidBudget = true;
        	}catch(Exception e) {
        		System.out.println("Bitte ein gültiges Budget angeben");
        	}
        	
        }
        
        
        
        System.out.println( "Rufe Webservice MarketWS auf" );
        String str = market.getBestOffer(partNumber, budget);
        
        System.out.println(str); // Test
        
        System.out.println( "Rufe Webservice OrderWS auf" );
        String result = order.placeOrder("LEARN-103", "tlestart1", str);
        
        System.out.println(result);
    }
}
