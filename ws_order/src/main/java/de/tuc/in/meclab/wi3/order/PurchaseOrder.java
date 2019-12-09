package de.tuc.in.meclab.wi3.order;

import com.sap.conn.jco.*;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.monitor.JCoDestinationMonitor;
import de.tuc.in.meclab.wi3.common.loginData.LoginData;
import de.tuc.in.meclab.wi3.common.order.Order;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * PurchaseOrder-Klasse: Führt die Bestellung im SAP per JCO durch.
 */
public class PurchaseOrder
{

    /**
     * Eine Verbindung mit dem SAP-System per JCO herstellen.
     * @param p_username Benutzername (GBI-XXX)
     * @param p_password Passwort
     * @return Gibt ein JCoDestination-Objekt zurück
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private JCoDestination connect( final String p_username, final String p_password ) throws IOException, JCoException
    {
        final Properties l_connectProperties = new Properties();
        l_connectProperties.setProperty( DestinationDataProvider.JCO_ASHOST, LoginData.CONN );
        l_connectProperties.setProperty( DestinationDataProvider.JCO_SYSNR, LoginData.INSTANCE );
        l_connectProperties.setProperty( DestinationDataProvider.JCO_CLIENT, LoginData.MANDANT );
        l_connectProperties.setProperty( DestinationDataProvider.JCO_USER, p_username );
        l_connectProperties.setProperty( DestinationDataProvider.JCO_PASSWD, p_password );
        l_connectProperties.setProperty( DestinationDataProvider.JCO_LANG, LoginData.LANG );
        l_connectProperties.setProperty( DestinationDataProvider.JCO_MSSERV, LoginData.MSSERV );

        final File l_destcfg = File.createTempFile( "ABAP_AS_WITHOUT_POOL", ".jcoDestination", new File( "." ) );

        try ( final FileOutputStream fos = new FileOutputStream( l_destcfg, false ) )
        {
            l_connectProperties.store( fos, "temp" );

            return JCoDestinationManager.getDestination(
                l_destcfg
                        .getAbsolutePath()
                        .replace( ".jcoDestination", "" )
                        .replace( new File( "." ).getAbsolutePath(), "" )
            );

        }
        catch (IOException e)
        {
            throw new IOException( "Unable to write to destination file", e );
        }
        finally
        {
            l_destcfg.delete();
        }
    }

    public String orderMaterials( final String p_username, final String p_password, final Order p_order )
    {

        // check for correct formatted username (gbi-###)
        if ( !Pattern.matches( "[lL][eE][aA][rR][nN]-\\d{3}+", p_username ) )
			return "CHECK_USERNAME_ERROR";

        try
        {
            final JCoDestination l_destination = connect( p_username, p_password );

            final JCoFunction l_function = l_destination.getRepository().getFunction( "BAPI_PO_CREATE" );
            l_function.execute( l_destination );

            final JCoStructure l_po_header = l_function.getImportParameterList().getStructure("PO_HEADER");
            final JCoTable l_po_itmes = l_function.getTableParameterList().getTable("PO_ITEMS");
            final JCoTable l_po_item_schedules = l_function.getTableParameterList().getTable("PO_ITEM_SCHEDULES");

            l_po_header.setValue( "PURCH_ORG",  p_order.material().purchaseorg()/* TODO: hier die zum Material gehoerige Verkaufsorganisation aus p_order einfuegen */);
            l_po_header.setValue( "PUR_GROUP", p_order.material().purchasegroup() /* TODO: hier die zum Material gehoerige Verkaufsgruppe aus p_order einfuegen */ );
            l_po_header.setValue( "VENDOR", p_order.material().vendor() /* TODO: hier die zum Material gehoerige Zulieferernummer aus p_order einfuegen */ );
            l_po_header.setValue( "DOC_TYPE","NB" );

            l_po_itmes.appendRow();
            l_po_itmes.setValue( "PO_ITEM", "10" );
            l_po_itmes.setValue( "PUR_MAT", p_order.material().number() );
            l_po_itmes.setValue( "PLANT", p_order.material().plant() );
            l_po_itmes.setValue( "UNIT", "EA" );
            l_po_itmes.setValue( "NET_PRICE", p_order.material().price()/* TODO: hier die den Preis einer Materialeinheit einfuegen */ );

            l_po_item_schedules.appendRow();
            l_po_item_schedules.setValue( "PO_ITEM","10" );
            l_po_item_schedules.setValue( "DELIV_DATE", new Date() );
            l_po_item_schedules.setValue( "QUANTITY", p_order.quantity()/* TODO: hier die Menge an Materialien einfuegen */ );
            
            l_function.execute( l_destination );
            final JCoTable returnTable = l_function.getTableParameterList().getTable( "RETURN" );

            // Ergebnis-Tabelle nach stdout ausgeben
            for ( int i = 0; i < returnTable.getNumRows(); i++ )
            {
                returnTable.setRow( i );
                System.out.println(
                        returnTable.getString( "TYPE" )
                                + " "
                                + returnTable.getString("MESSAGE" )
                );
            }

            final String l_po_num = l_function.getExportParameterList().getString( "PURCHASEORDER" );
            System.out.println( "PURCHASE NUMBER: " + l_po_num );

            if ( l_po_num == null || l_po_num.equals( "" ) )
    			return "CREATE_PURCHASE_FAILED";
    		else
                // TODO: hier die Bestellnummer (l_po_num) zurueckgeben "Success: Order number ..."
    			return "SUCCESS: Order number " + l_po_num;

        }
        catch (IOException e)
        {
            e.printStackTrace();
            return "IO ERROR: " + e.getMessage();
        }
        catch (JCoException e)
        {
            e.printStackTrace();
            return "JCo ERROR: " + e.getMessage();
        }
    }
}

