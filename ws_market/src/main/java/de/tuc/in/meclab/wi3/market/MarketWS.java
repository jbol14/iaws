package de.tuc.in.meclab.wi3.market;

import de.tuc.in.meclab.wi3.common.materialstamm.Material;
import de.tuc.in.meclab.wi3.common.materialstamm.Materialstamm;
import de.tuc.in.meclab.wi3.common.order.Order;
import de.tuc.in.meclab.wi3.common.xml.CXStream;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@WebService(name = "MarketWS")
@SOAPBinding(style = Style.RPC)
public class MarketWS {
	
	private final String m_deploymentpath;
	
	private Map<String,ArrayList<Material>> materialListe = new HashMap<String,ArrayList<Material>>();
	
	private double low = Double.MAX_VALUE;
	private String sup = "";
	private Material cheapestMat;
	
	
	private CXStream xmlConverter = new CXStream();
	
	public MarketWS() {
		m_deploymentpath = "http://localhost:8082/MarketWS";
		System.out.println("Market Webservice gestartet");
	}
	
	public MarketWS(final String p_deploymentpath) {
		m_deploymentpath = p_deploymentpath;
		System.out.println("Market WS gestartet unter " + m_deploymentpath);
		
	}
	
	@WebMethod(action = "updateRawMaterials")
	@WebResult(name = "success")
	public boolean updateRawMaterials(
			@WebParam(name = "suppliernumber") final String p_suppliernumber,
			@WebParam(name = "materiallist") final String p_materiallist)
	{
		//TODO: p_materiallist von XML in ArrayList<Material> umwandeln
		try {
			ArrayList<Material> angebot = (ArrayList<Material>) xmlConverter.fromXML(p_materiallist);
		
		// Zur Materialliste unter Verwendung der Suppliernummer als Key
			materialListe.put(p_suppliernumber, angebot);
		
			return true; // Damit hier erstmal Ruhe ist :)
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	@WebMethod(action = "getBestOffer")
	@WebResult(name = "return")
	public String getBestOffer(
			@WebParam(name = "partNumber") final String p_partnumber,
			@WebParam(name = "budget") final String p_budget)
	{
		// Budget in double parsen
		double budget = Double.parseDouble(p_budget);
		
		Order order;
		
		if(budget < 0) {
			return "Error";
		}
		
		
		//Über HashMap iterieren und für jeden Supplier das Angebo für die gegebene partNumber finden
		/*materialListe.forEach((key, value) -> {
			for(int i = 0; i < value.size(); i++) {
				if(value.get(i).equals(p_partnumber)) {
					if (value.get(i).price() < this.low) {
						this.low = value.get(i).price();
						this.sup = key;
						this.cheapestMat = value.get(i);
					}
				}
			}
		});
		*/
		double lowestPrice = Double.MAX_VALUE;
		String supplierNumber = "";
		Material mat = new Material("test", "test");
		Iterator it = materialListe.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			//GEfahr
			List<Material> l = (ArrayList<Material>) pair.getValue();
			l.size();
			
			for(int i = 0; i <  l.size(); i++) {
				if (l.get(i).number().equals(p_partnumber)) {
					if(l.get(i).price() < lowestPrice) {
						lowestPrice = l.get(i).price();
						supplierNumber = (String)pair.getKey();
						mat = l.get(i);
					}
				}
			}
		}
		System.out.println(lowestPrice + " " + supplierNumber);
		
		// Zum Günstigsten Preis so viel wie möglich kaufen
		int quantity = 0;
		for(double i = 0d; i<budget-lowestPrice; i += lowestPrice) {
			quantity += 1;
		}
		
		order = new Order(mat, quantity, supplierNumber);
		
		String orderXml = xmlConverter.toXML(order);
		
		return orderXml;
	}
	
	

}
