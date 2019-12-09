package de.tuc.in.meclab.wi3.webservice;

import de.tuc.in.meclab.wi3.common.order.Order;
import de.tuc.in.meclab.wi3.common.xml.CXStream;
import de.tuc.in.meclab.wi3.order.PurchaseOrder;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService( name="OrderWS" )
@SOAPBinding( style=Style.RPC )
public class OrderWS
{

	private final String m_deploymentpath;
	private final PurchaseOrder m_purchaseorder = new PurchaseOrder();
	private final CXStream m_cxstream = new CXStream();

	public OrderWS()
    {
		m_deploymentpath = "http://localhost:8083/OrderWS";
		System.out.println( "Starting Order WS..." );
		System.out.println( "Deployment path " + m_deploymentpath);
	}

    public OrderWS( final String p_deploymentpath )
    {
        m_deploymentpath = p_deploymentpath;
        System.out.println( "Starting Order WS..." );
        System.out.println( "Deployment path " + m_deploymentpath);
    }

	@WebMethod( action="placeOrder" )
	@WebResult( name="success" )
	public String placeOrder(
			@WebParam( name = "username" ) final String p_username,
			@WebParam( name = "password" ) final String p_password,
			@WebParam( name = "xml" ) final String p_xml) {
		
        return m_purchaseorder.orderMaterials(
                p_username,
                p_password,
                (Order) m_cxstream.fromXML( p_xml )
        );
	}
}


