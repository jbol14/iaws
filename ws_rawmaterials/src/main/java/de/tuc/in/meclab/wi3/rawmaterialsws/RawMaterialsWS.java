package de.tuc.in.meclab.wi3.rawmaterialsws;

import de.tuc.in.meclab.wi3.common.materialstamm.Materialstamm;
import de.tuc.in.meclab.wi3.common.xml.CXStream;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import java.util.regex.Pattern;

@WebService( name = "RawMaterialsWS" )
@SOAPBinding( style = Style.RPC )
public class RawMaterialsWS {

	private final String m_deploymentpath;
	private final Materialstamm m_materialstamm = new Materialstamm();

	public RawMaterialsWS()
	{
	    m_deploymentpath = "http://localhost:8081/RawMaterialsWS";
		System.out.println( "Starting WS RawMaterials..." );
		System.out.println( "Deployment path " + m_deploymentpath );
	}

	public RawMaterialsWS( final String p_deploymentpath )
	{
	    m_deploymentpath = p_deploymentpath;
		System.out.println( "Starting WS RawMaterials..." );
		System.out.println( "Deployment path " + m_deploymentpath);
	}

	@WebMethod( action = "getRawMaterials" )
	@WebResult( name = "materials" )
	public String getRawMaterials(
			@WebParam( name = "username" ) final String p_username,
			@WebParam( name = "password" ) final String p_password )
    {

		// check for correct formatted username (gbi-###)
		if ( !Pattern.matches( "[Ll][eE][aA][Rr][nN]-\\d{3}+", p_username ) )
			return "USERNAME_ERROR";

		final String l_material = "*" + p_username.split( "-" )[1];
		System.out.println( "Query for " + l_material );

		try
		{
			// get material and serialize to xml
			return new CXStream().toXML( m_materialstamm.getRawMaterials( p_username, p_password ) );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			return "JCO_ERROR"; // return "JCO_ERROR" string to tell the bpel
								// process to stop
		}
	}

    @WebMethod( action = "getRandomRawMaterial" )
    @WebResult( name = "material" )
    public String getRandomRawMaterials(
            @WebParam( name = "username" ) final String p_username,
            @WebParam( name = "password" ) final String p_password )
    {

        // check for correct formatted username (gbi-###)
        if ( !Pattern.matches( "[lL][eE][aA][rR][nN-\\d{3}+", p_username ) )
            return "USERNAME_ERROR";

        final String l_material = "*" + p_username.split( "-" )[1];
        System.out.println( "Query for " + l_material );

        try
        {
            // get material and serialize to xml
            return new CXStream().toXML( m_materialstamm.getRandomRawMaterial( p_username, p_password ) );
        }
        catch ( Exception e )
        {
            //e.printStackTrace();
            return "JCO_ERROR"; // return "JCO_ERROR" string to tell the bpel
            // process to stop
        }
    }
}
