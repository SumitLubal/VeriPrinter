import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;


/**
 * @author Administrator
 *
 */
public class Config {
	static String printerName;
	private static Integer printerValue;
	public static void loadParams() {
	    Properties props = new Properties();
	    InputStream is = null;
	 
	    // First try loading from the current directory
	    try {
	        File f = new File("server.properties");
	        is = new FileInputStream( f );
	    }
	    catch ( Exception e ) { is = null; saveParamChanges();}
	    if(is!=null)
	    try {	 
	        // Try loading properties from the file (if found)
	        props.load( is );
	    }
	    catch ( Exception e ) { }
	 
	    printerName = props.getProperty("printerName", "Send To One Note");
	    printerValue = new Integer(props.getProperty("printerValue", "1"));
	}
	public static void saveParamChanges() {
	    try {
	        Properties props = new Properties();
	        printerValue = 1;
	        printerName = "unknown";
	        props.setProperty("printerName", printerName);
	        props.setProperty("printerValue", ""+printerValue);
	        File f = new File("server.properties");
	        OutputStream out = new FileOutputStream( f );
	        props.store(out, "This is an  header comment string. Designed by BMIT College Students. printer value =0 indicates default print operation");
	    }
	    catch (Exception e ) {
	        e.printStackTrace();
	    }
	}
	public static String getPrinterName() {
		loadParams();
		return printerName;
	}
	public static void setPrinterName(String printerName) {
		Config.printerName = printerName;
		saveParamChanges();
	}
	public static int getPrinterNumber() {
		loadParams();
		return printerValue;
	}
	
}
