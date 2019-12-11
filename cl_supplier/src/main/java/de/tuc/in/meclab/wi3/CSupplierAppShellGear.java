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
public class CSupplierAppShellGear {

    public static void main( final String[] p_args )
    {
    	
        System.out.println( "Erzeuge Materalliste und schicke sie an den MarketWS" );
        
        // Initialisieren
        RawMaterialsWSService service = new RawMaterialsWSService();
        RawMaterialsWS raw = service.getRawMaterialsWSPort();
        CXStream xmlConverter = new CXStream();
        
        MarketWSService marketService = new MarketWSService();
        MarketWS market = marketService.getMarketWSPort();
        
        String suppliernumber = "0000123103";
        double price;
        
        Scanner scanner = new Scanner(System.in); // Scanner erst ganz am Ende schließen, schließt auch System.in
        
        // Materialliste vom WS holen
        System.out.println("Materialliste vom Webservice empfangen");
        
        List<Material> rawMaterialsList = new ArrayList<Material>();
        String fromWS = raw.getRawMaterials("LEARN-103", "tlestart1");
        
        //XML in ArrayList<Material> umwandeln
        rawMaterialsList = (List<Material>) xmlConverter.fromXML(fromWS); 
        
        
        // Alle Materialien in der Liste durchgehen und einen gültigen Preis zw. 10,00 und 50,00 festlegen
        for(int i = 1; i < rawMaterialsList.size();++i) {

        	System.out.println(rawMaterialsList.get(i-1).description() + " " + rawMaterialsList.get(i-1).number());
        	
        	//Preis einsetzen
        	System.out.print("neuer Preis: ");
        	try {
        		String input = scanner.nextLine();
        		
        		//Prüfen, ob String in Double gecastet werden kann
        		price = getValidDouble(input);
        		
        		if(checkPrice(price)) {
            		rawMaterialsList.get(i).price(price);
            	} else {
            		i--;
            	}
        	}catch(Exception e) {
        		System.out.println("Bitte gültigen Wert zwischen 10,00 und 50,00 eingeben");
        		i--;
        		e = null;
        	}
        	
        }
        
        String toWS = xmlConverter.toXML(rawMaterialsList); // In XML umwandeln
        
        boolean result = market.updateRawMaterials(suppliernumber, toWS);
        
        System.out.println(result);
        
        scanner.close();
    }
    
    //Helfermethoden

    private static boolean checkPrice(double price){

    	if (10 <= price  && price <= 50) {
    		return true;
    	}else {
    		return false;
    	}
      	
    	
    }
    // Prüft, ob Suppliernummer 
    private static boolean validSuppliernumber(String supplierNumber) {
    	if(supplierNumber.length() == 10) {
    		return true;
    	}else {
    		return false;
    	}
    }
    
    private static double getValidDouble(String userInput) {
    	try {
    		// Wir wollen eine Zahl mit 2 Nackommastellen
    		if(userInput.length() > 5) {
    			return 0.00;
    		}
    		/*
    		 * Falls Eingabe länger als 3 Zeichen ist, ist bei gültigen Eingaben ein Komma oder Punkt an dritter Stelle
    		 * Um in einen Double zu parsen wird ein etwaiges Komma in einen Punkt unmgewandelt
    		 */
    		if(userInput.length() >= 3) {
    			if(userInput.charAt(2) == (',')) {
    				userInput = userInput.replace(',', '.');
    			}
    		}
    		double value = Double.parseDouble(userInput);
    		return value;
    	}catch(Exception e) {
    		/*
    		 * Falls eine Exception auftritt weil z.B. eine falsche Eingabe erfolgt wird der Wert 0.00 zurückgegeben
    		 * dieser liegt außerhalb des gültigen Wertebereichs und fliegt bei der späteren Prüfung raus, weshalb der Nutzer aufgefordert wird, für dieses Produkt einen gültigen Wert einzugeben.
    		 */
    		return 0.00;
    	}
    	
    }
    
}
