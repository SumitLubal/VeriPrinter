import java.io.FileInputStream;
import java.io.InputStream;
import sun.audio.*;
public class DTMFGenerater {
	static String Start, Stop, Pause,False;
	static {
		Start = "f:/a.wav";
		Stop = "f:/b.wav";
		Pause = "f:/b.wav";
		False = "f:/c.wav";
	}

	static void singal(String signalName) {
		try {
			InputStream in = new FileInputStream(signalName);
			AudioStream au = new AudioStream(in);
			AudioPlayer.player.start(au);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
