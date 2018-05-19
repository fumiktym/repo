import java.awt.Dimension;
import java.util.List;

import javax.swing.JFrame;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;


public class WebcamPanelExample {

	public static void main(String[] args) throws InterruptedException {

		//Webcam webcam = Webcam.getDefault();
		List<Webcam> webcamList = Webcam.getWebcams();
		Webcam webcam = webcamList.get(1);
		Dimension mySize = new Dimension(2000,500);
		Dimension[] sizes = {mySize};
		
		webcam.setCustomViewSizes(sizes);
		//webcam.setViewSize(WebcamResolution.VGA.getSize());
		webcam.setViewSize(mySize);

		WebcamPanel panel = new WebcamPanel(webcam);
		panel.setFPSDisplayed(true);
		panel.setDisplayDebugInfo(true);
		panel.setImageSizeDisplayed(true);
		panel.setMirrored(true);

		JFrame window = new JFrame("Test webcam panel");
		window.add(panel);
		window.setResizable(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		window.setVisible(true);
	}
}
