
public class Starter {
	/*this file has been edited*/
	public static void main(final String a[]){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					ExNativeAccessWebcam.main(a);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				FrameClass.main(a);
			}
		}).start();
	}
}
