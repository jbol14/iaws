package de.tuc.in.meclab.wi3;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import de.tuc.in.meclab.wi3.common.materialstamm.Materialstamm;
import de.tuc.in.meclab.wi3.common.materialstamm.Material;
import de.tuc.in.meclab.wi3.common.xml.CXStream;
import de.tuc.in.meclab.wi3.webservice.RawMaterialsWS;
import de.tuc.in.meclab.wi3.webservice.RawMaterialsWSService;
import de.tuc.in.meclab.wi3.webservice.MarketWS;
import de.tuc.in.meclab.wi3.webservice.MarketWSService;



/**
 * Supplier Client
 *
 */
public class CSupplierApp {

    public static void main( final String[] p_args )
    {
    	
        System.out.println( "Erzeuge Materalliste und schicke sie an den MarketWS" );
        
        // Initialisieren
        RawMaterialsWSService service = new RawMaterialsWSService();
        RawMaterialsWS raw = service.getRawMaterialsWSPort();
        CXStream xmlConverter = new CXStream();
        
        MarketWSService marketService = new MarketWSService();
        MarketWS market = marketService.getMarketWSPort();        
        // Materialliste vom WS holen
        System.out.println("Materialliste vom Webservice empfangen");
        
        List<Material> rawMaterialsList = new ArrayList<Material>();
        String fromWS = raw.getRawMaterials("LEARN-103", "tlestart1");
        
        //XML in ArrayList<Material> umwandeln
        rawMaterialsList = (List<Material>) xmlConverter.fromXML(fromWS); 
        
        Scanner scanner = new Scanner(System.in); // Scanner erst ganz am Ende schließen, schließt auch System.in
        System.out.println("Bitte Suppliernummer angeben:");
        String suppliernumber = scanner.nextLine();
        for(int i = 1; i < rawMaterialsList.size();++i) {

        	System.out.println(rawMaterialsList.get(i-1).description());
        	
        	//Preis einsetzen
        	System.out.print("neuer Preis: ");
        	
        	double price = scanner.nextDouble();
        	
        	if(checkPrice(price)) {
        		rawMaterialsList.get(i).price(price);
        	} else {
        		i--;
        	}
        }
        
        //TODO: Bearbeitete Liste an MarketWS senden
        
        String toWS = xmlConverter.toXML(rawMaterialsList); // In XML umwandeln
        
        boolean result = market.updateRawMaterials(suppliernumber, toWS);
        
        System.out.println(result);
        
        scanner.close();
    }
    
    private static boolean checkPrice(double price){

    	if (10 <= price  && price <= 50) {
    		return true;
    	}else {
    		return false;
    	}
      	
    	
    }
    
}
