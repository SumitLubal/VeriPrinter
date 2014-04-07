import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.imageio.ImageIO;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;

public class Printer {
	final static int PRINTER_IN_UNREAL_STATE = 10;
	final static int PRINTER_BUSY = 11;
	final static int PRINTER_NOT_SET = 12;
	final static int PRINTER_READY = 13;
	int PRINTER_STATE = PRINTER_NOT_SET;
	private File pathToImage;

	public Printer() {
		PRINTER_STATE = PRINTER_NOT_SET;
	}

	// creates two identifiers (black spots locating uppercorner and lower
	// corner of image)
	BufferedImage preProcess(BufferedImage recieved) {
		int increase = 10;
		if (Math.max(recieved.getWidth(), recieved.getHeight()) > 500) {
			increase = (10 * Math
					.max(recieved.getWidth(), recieved.getHeight())) / 500;
			System.out.println("spot weight =" + increase);
		}
		BufferedImage tmp = new BufferedImage(recieved.getWidth()
				+ (increase * 2), recieved.getHeight() + (increase * 2),
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = tmp.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, increase, increase); // fills color from 0,0 to (given
												// width and height) 0+w,0+h
		g.fillRect(tmp.getWidth() - increase, 0, increase, increase);
		g.fillRect(0, tmp.getHeight() - increase, increase, increase);
		g.fillRect(tmp.getWidth() - increase, tmp.getHeight() - increase,
				increase, increase);
		// draws image to printable
		g.drawImage(recieved, increase, increase, tmp.getWidth()
				- (increase * 2), tmp.getHeight() - (increase * 2), null);
		return tmp;
	}

	

	public boolean run(File file) {
		pathToImage = file;
		PRINTER_STATE = PRINTER_BUSY;
		PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		pras.add(new Copies(1));
		PrintService pss[] = PrintServiceLookup.lookupPrintServices(
				DocFlavor.INPUT_STREAM.GIF, pras);
		if (pss.length == 0)
			throw new RuntimeException("No printer services available.");
		// gets printer name from external file
		PrintService ps = pss[0/* Config.getPrinterNumber() */];
		System.out.println("Printing to " + ps);
		DocPrintJob job = ps.createPrintJob();
		Doc doc;
		try {
			doc = new SimpleDoc(new FileInputStream(pathToImage),
					DocFlavor.INPUT_STREAM.JPEG, null);
			job.print(doc, pras);
		} catch (PrintException | FileNotFoundException e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
			PRINTER_STATE = PRINTER_IN_UNREAL_STATE;
			return false;
		}
		PRINTER_STATE = PRINTER_READY;
		return true;
	}

	public class ImagePrintable implements Printable {

		private double x, y, width;

		private int orientation;

		private BufferedImage image;

		public ImagePrintable(PrinterJob printJob, BufferedImage image) {
			PageFormat pageFormat = printJob.defaultPage();
			this.x = pageFormat.getImageableX();
			this.y = pageFormat.getImageableY();
			this.width = pageFormat.getImageableWidth();
			this.orientation = pageFormat.getOrientation();
			this.image = image;
		}

		@Override
		public int print(Graphics g, PageFormat pageFormat, int pageIndex)
				throws PrinterException {
			if (pageIndex == 0) {
				int pWidth = 0;
				int pHeight = 0;
				if (orientation == PageFormat.PORTRAIT) {
					pWidth = (int) Math.min(width, (double) image.getWidth());
					pHeight = pWidth * image.getHeight() / image.getWidth();
				} else {
					pHeight = (int) Math.min(width, (double) image.getHeight());
					pWidth = pHeight * image.getWidth() / image.getHeight();
				}
				g.drawImage(image, (int) x, (int) y, pWidth, pHeight, null);
				return PAGE_EXISTS;
			} else {
				return NO_SUCH_PAGE;
			}
		}

	}

}
