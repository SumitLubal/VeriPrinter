import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class FrameClass extends JFrame implements ActionListener, Runnable {
	static JPanel up, left, right, down;
	ArrayList<File> fileSet;
	JButton open, print, stop, about;
	JProgressBar progressBar;
	static BufferedImage cameraFeed, leftImage, rightImage;
	static BufferedImage printImage;
	Thread printerThread;
	Printer printer;
	int x;
	Me algorithms;
	String path;
	static String args[];
	ExNativeAccessWebcam webcam = new ExNativeAccessWebcam();
	static boolean isPrintDone = false;
	static BufferedImage imageForDownPanel;
	static boolean scanningOperation = true;

	public FrameClass() {
		try {
			// client = new ClientTest();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, JOptionPane.OK_OPTION,
					e.getMessage(), JOptionPane.OK_OPTION);
		}
		objectInitialiser();
		setGUI();
		setSize(800, 600);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		printerThread.start();
	}

	private void objectInitialiser() {
		fileSet = new ArrayList<File>();
		algorithms = new Me();
		up = new JPanel();
		down = new JPanel();
		left = new JPanel();
		right = new JPanel();
		open = new JButton("Open");
		print = new JButton("Print");
		stop = new JButton("Stop");
		about = new JButton("About");
		progressBar = new JProgressBar();
		printerThread = new Thread(this);
		printer = new Printer();
		print.setEnabled(false);
		stop.setEnabled(false);

	}

	private void setGUI() {
		setTitle("Veri Printer");

		up.setLayout(new GridLayout(2, 2));
		down.setLayout(new BorderLayout(20, 20));
		setLayout(new GridLayout(2, 1));

		// adds buttons to leftmost corner ie JPanel
		up.add(open);
		up.add(print);
		up.add(about);
		up.add(stop);
		// down.add(progressBar);

		// defines functions to be calls when we clicck the buttons
		open.addActionListener(this);
		stop.addActionListener(this);
		print.addActionListener(this);
		about.addActionListener(this);

		// adds block or division to frame
		add(up);
		add(left);
		add(right);
		add(down);

		repaint();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (printImage == null) {
			left.getGraphics().drawString(
					"there are " + fileSet.size() + " to print",
					left.getWidth() / 2, left.getHeight() / 2);
		} else {
			left.getGraphics().drawImage(printImage, 0, 0, left.getWidth(),
					left.getHeight(), this);
		}
		if (cameraFeed != null) {
			right.getGraphics().drawImage(cameraFeed, 0, 0, right.getWidth(),
					right.getHeight(), this);
		} else {
			right.getGraphics().drawString("No camera Found!",
					right.getWidth() / 2, right.getHeight() / 2);
		}
		if (imageForDownPanel != null) {
			down.getGraphics().drawImage(imageForDownPanel, 0, 0,
					down.getWidth(), down.getHeight(), this);
		} else {
			down.getGraphics().drawString("Here Will be the scanned Image",
					down.getWidth() / 2, down.getHeight() / 2);
		}

	}

	@Override
	public void update(Graphics gr) {

		Graphics g = right.getGraphics();
		BufferedImage back = new BufferedImage(right.getWidth(),
				right.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics second = back.getGraphics();
		second.setColor(Color.WHITE);
		second.fillRect(0, 0, getWidth(), getHeight());
		second.dispose();
		g.drawImage(back, 0, 0, left.getWidth(), left.getHeight(), this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource().equals(open)) {
			path = (String) JOptionPane.showInputDialog(this,
					"Enter Path Of Image To Print", "Complete Path",
					JOptionPane.PLAIN_MESSAGE, null, null, "");

			/*
			 * JFileChooser openF = new JFileChooser();
			 * openF.showOpenDialog(this); File f1 = openF.getSelectedFile();
			 * path = f1.getAbsolutePath();
			 */
			System.out.println(path);
			File tmp;
			fileSet.clear();
			System.out.println("size of array" + fileSet.size());
			if (path != null) {
				tmp = new File(path);
				try {
					if (tmp.isDirectory()) {
						File[] files = tmp.listFiles();
						for (int i = 0; i < files.length; i++) {
							if ((files[i].getAbsolutePath().contains(".jpg") || files[i]
									.getAbsolutePath().contains(".png"))
									&& !(files[i].getAbsolutePath()
											.contains("printable"))) {
								fileSet.add(files[i]);
							}
						}
						print.setEnabled(true);
						stop.setEnabled(true);
					} else if (tmp.isFile()) {
						if (tmp.getAbsolutePath().contains(".jpg")
								|| tmp.getAbsolutePath().contains(".png")) {
							fileSet.add(tmp);
						}
						print.setEnabled(true);
						stop.setEnabled(true);
					}

				} catch (Exception exception) {
					System.out.println("invalid path");
				}
				System.out.println("files count" + fileSet.size());
			}
		} else if (e.getSource().equals(print)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub

					BufferedImage test = null;
					System.out.println("print");
					if (cameraFeed == null) {
						System.out.println("Camrea feed is null");
					} else {
						int i = 0;
						while (i < fileSet.size()) {
							if (scanningOperation) {
								try {
									// reads image into memory and save it to be
									// printable
									printImage = ImageIO.read(fileSet.get(i));
									path = fileSet.get(i).getAbsolutePath();
									leftImage = printImage;
								} catch (Exception exc) {
									System.out
											.println("unable to read printable image avoid printing");
									leftImage = null;
								}
								isPrintDone = false;
								if (cameraFeed != null && leftImage != null) {
									System.out.println("Printing");
									try {
										printImage = algorithms
												.rgbtoGray(printImage);
										// Resize to make it full screen
										if (printImage.getWidth() > printImage
												.getHeight()) {
											printImage = algorithms.resize(
													printImage, 1024, 705);
										} else {
											printImage = algorithms.resize(
													printImage, 705, 1024);
										}
										System.out.println("Size "
												+ printImage.getWidth() + "*"
												+ printImage.getHeight());
										printImage = printer
												.preProcess(printImage);
										leftImage = printImage;
										String pathToPrintableImage = path
												.substring(0, path.indexOf("."))
												+ "printable.jpg";
										ImageIO.write(printImage, "PNG",
												new File(pathToPrintableImage));
										isPrintDone = printer.run(new File(
												pathToPrintableImage));
										System.out.println("printing"
												+ fileSet.get(
														fileSet.size() - 1)
														.getAbsolutePath()
												+ " of Index "
												+ (fileSet.size() - 1));
										// Thread.sleep(3000);
										if (!isPrintDone) {

										}
										// logic
										// for
										// ignoring
										DTMFGenerater
												.singal(DTMFGenerater.Start);
									} catch (Exception exc) {
										exc.printStackTrace();
									}
								}
								i++;
							}
							System.out.println("Waiting");

							scanningOperation = false;
							try {
								System.out.println("Waiting");
								DTMFGenerater.singal(DTMFGenerater.Stop);
								// test = ImageIO.read(new File("e:/sent.png"));
								Thread.sleep(5000);
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							// gets test image right from camera

							test = cameraFeed;

							try {
								ImageIO.write(cameraFeed, "PNG", new File(
										"e:/cap.jpg"));
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

							imageForDownPanel = cameraFeed;
							BufferedImage finalResoluted = algorithms
									.detector(test/*
												 * algorithms.rgbtoGray(cameraFeed
												 * )
												 */);
							test = finalResoluted;
							BufferedImage original = algorithms.resize(
									leftImage, finalResoluted.getWidth(),
									finalResoluted.getHeight());
							// Gray
							finalResoluted = algorithms
									.rgbtoGray(finalResoluted);
							original = algorithms.rgbtoGray(original);

							Double percentageCompletion = algorithms
									.compareImages(finalResoluted, original);
							imageForDownPanel = finalResoluted;
							if (percentageCompletion < 75.0f) {
								writer(fileSet.get(i).getAbsolutePath()+" Is printed "+ percentageCompletion +" FAILED");
							} else if (percentageCompletion > 100.f) {
								writer(fileSet.get(i).getAbsolutePath()+" Is printed "+ percentageCompletion +"FAILED");
							} else{
								writer(fileSet.get(i).getAbsolutePath()+" Is printed "+ percentageCompletion +"SUCESS");
							}
							DTMFGenerater.singal(DTMFGenerater.Start);
							System.out.println(percentageCompletion);
							isPrintDone = false;
							leftImage = null;
							scanningOperation = true;
						}
						try {
							Thread.sleep(40);
						} catch (Exception es) {
							es.printStackTrace();
						}

						fileSet.clear();
					}
				}

			}).start();
		} else if (e.getSource().equals(stop)) {
			isPrintDone = false;
			printImage = null;
			print.setEnabled(false);
			imageForDownPanel = null;
			fileSet.clear();

		} else if (e.getSource().equals(about)) {
			JOptionPane
					.showMessageDialog(
							this,
							"Created By Group 7.Get more products at - www.skcppl.in/printerproject",
							"Project Info", JOptionPane.OK_OPTION);
		}
	}

	@Override
	public void run() {
		try {
			// webcam.main(args);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while (true) {
			cameraFeed = webcam.capturedImage;
			try {
				Thread.sleep(60);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			repaint();
		}
	}

	public void writer(String data) {

		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter("e:/log.txt", true)));
			out.println(data);
			out.close();
		} catch (IOException e) {
			// exception handling left as an exercise for the reader
		}
	}

	public static void main(String ar[]) {
		args = ar;
		new FrameClass();
	}
}
