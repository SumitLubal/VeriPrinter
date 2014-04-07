import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.sun.org.apache.xml.internal.security.algorithms.Algorithm;

public class Me {
	private float percentage;

	BufferedImage detector(BufferedImage img) {
		// img = rgbtoGray(img);
		System.out.println("In detector");
		int maxLevel = 2;
		int factor = 50;
		int averageValue = 50;
		boolean upflag = false, downflag = false;
		Color point = null;
		int upperX = 0, upperY = 0, downX = 0, downY = 0;
		mainup: for (int j = factor; j < img.getHeight() - 1; j++) {
			for (int i = factor; i < img.getWidth() - 1; i++) {
				point = new Color(img.getRGB(i, j));
				int ave = (point.getRed());
				if (ave < averageValue) { // to chech it as
											// black
					upflag = true;
					// scanPoint(i, j, img, maxLevel, averageValue);

					if (upflag) {
						System.out.println("found upper at" + i + " " + j);
						System.out.println("Colors" + point.getRed() + " "
								+ point.getBlue() + " " + point.getGreen());

						upperX = i;
						upperY = j;
						break mainup;
					}
				}
			}
		}

		maindown: for (int j = img.getHeight() - factor; j > factor; j--) {
			for (int i = img.getWidth() - factor; i > factor; i--) {
				point = new Color(img.getRGB(i, j));
				int ave = (point.getRed() + point.getGreen()) / 2;
				if (ave < averageValue) { // to chech it as
											// black
					downflag = true;
					// scanPoint(i, j, img, maxLevel, averageValue);
					if (downflag) {
						System.out.println("found Down at " + (i-factor) + " " + (j-factor));
						downX = i;
						downY = j;
						break maindown;
					}
				}
			}
		}

		if (upperX > downX) {
			int tmp = upperX;
			upperX = downX;
			downX = tmp;
		}
		System.out.println("found up at " + upperX + " " + upperY);
		System.out.println("found down at " + downX + " " + downY);
		

		if (upperX != 0 && downX != 0) {
			return crop(img, upperX, upperY, Math.abs(downX - upperX),
					Math.abs(downY - upperY));
			
		} else {
			System.out.println("Here is prob");
			return null;
		}
	}


	private boolean scanPoint(int i, int j, BufferedImage img, int maxLevel,
			int average) {
		int cnt = 1;
		boolean foundflag = false;
		try {
			while (maxLevel > 0) {
				if (new Color(img.getRGB(i, j + cnt)).getRed() < average) {
					if (new Color(img.getRGB(i + cnt, j)).getRed() < average) {
						if (new Color(img.getRGB(i, j - cnt)).getRed() < average) {
							if (new Color(img.getRGB(i - cnt, j)).getRed() < average) {
								foundflag = true;
							} else {
								return false;
							}
						} else {
							return false;
						}
					} else {
						return false;
					}
				} else {
					return false;
				}
				maxLevel--;
				cnt++;
			}
		} catch (Exception e) {
			System.out
					.println("I think u are scanning out side of image.. check function scanpoint");
		}
		return foundflag;
	}

	public static BufferedImage crop(BufferedImage src, int x, int y, int w,
			int h) {

		ImageFilter filter = new CropImageFilter(x, y, w, h);
		FilteredImageSource source = new FilteredImageSource(src.getSource(),
				filter);
		Image test = Toolkit.getDefaultToolkit().createImage(source);
		return toBufferedImage(test);
	}

	public static BufferedImage toBufferedImage(Image img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}

		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(img.getWidth(null),
				img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		// Return the buffered image
		return bimage;
	}

	public BufferedImage resize(BufferedImage leftImage, int width, int height) {
		// TODO Auto-generated method stub
		if (leftImage == null)
			return null;
		BufferedImage im = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		Graphics g = im.getGraphics();
		g.drawImage(leftImage, 0, 0, width, height, null);
		g.dispose();
		return im;
	}

	public BufferedImage rgbtoGray(BufferedImage input) {
		BufferedImage image = new BufferedImage(input.getWidth(), input.getHeight(),  
			    BufferedImage.TYPE_BYTE_GRAY);  
			Graphics g = image.getGraphics();  
			g.drawImage(input, 0, 0, null);  
			g.dispose();  
		return image;
	}

	public double compareImages(BufferedImage finalResoluted,
			BufferedImage original) {
		int printedBlue = 0, printedRed = 0, printedGreen = 0;
		int blue = 0, green = 0, red = 0;
		Color pixelOfPrinted, pixelOriginal;

		for (int i = 0; i < finalResoluted.getWidth(); i++) {
			for (int j = 0; j < finalResoluted.getHeight(); j++) {
				pixelOfPrinted = new Color(finalResoluted.getRGB(i, j));
				pixelOriginal = new Color(original.getRGB(i, j));
				printedBlue = printedBlue + pixelOfPrinted.getBlue();
				printedRed = printedRed + pixelOfPrinted.getRed();
				printedGreen = printedGreen + pixelOfPrinted.getGreen();

				green = green + pixelOriginal.getGreen();
				blue = blue + pixelOriginal.getBlue();
				red = red + pixelOriginal.getRed();

			}
		}
		long sumOfPrint = (printedBlue + printedGreen + printedRed);
		long sumOfOriginal = (red + blue + green);
		System.out.println(" original " + sumOfOriginal + " print "
				+ sumOfPrint);
		System.out.println("per = " + (sumOfPrint / sumOfOriginal));
		percentage = ((sumOfPrint * 1.0f) / sumOfOriginal);
		percentage = percentage * 100;
		System.out.println("Printed " + printedRed + " " + printedGreen + " "
				+ printedBlue);
		System.out.println("Original " + red + " " + green + " " + blue);
		return percentage;
	}

	public static void main(String ar[]) throws IOException {
		Me al = new Me();
		String pathToScannedImage = "e:/sent.png";
		String pathToResultImage = "e:/result.png";
		BufferedImage test = ImageIO.read(new File(pathToScannedImage));
		BufferedImage output = al.detector(test);
		if (output == null) {
			System.out.println("Null");
		}
		ImageIO.write(output, "PNG", new File(pathToScannedImage));
	}
}
