package ktym.nekokansi.main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamMotionDetector;
import com.github.sarxos.webcam.WebcamMotionEvent;
import com.github.sarxos.webcam.WebcamMotionListener;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.ds.buildin.WebcamDefaultDevice;

import ch.qos.logback.classic.gaffer.GafferConfigurator;

/**
 * 動くものを監視し、画像に記録する。
 * 
 * @author fumi
 *
 */
public class NekoKansiMain implements WebcamMotionListener {
	
	// 各種設定
	private static int firstMSec = 30 * 1000; // 起動してから開ける時間 (mili sec)
	private static int intervalMSec = 30*1000; // 撮影間隔、前回の記録から開ける時間（mili sec)
	
	private WebcamMotionDetector detector;
	Webcam webcam;
	
	private long previousTime = 0L; // 前回の記録日時（mSec)
	
	public static void main(String[] args) {
		
		//System.setProperty("logback.debug", "false");
		//Logger LOG = LoggerFactory.getLogger(WebcamDefaultDevice.class);
		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "OFF");
		
		NekoKansiMain obj = new NekoKansiMain();
		
		try {
			obj.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public NekoKansiMain() {
		
		webcam = Webcam.getDefault();
		webcam.setViewSize(WebcamResolution.VGA.getSize());
		
		//webcam.open();
		
		detector = new WebcamMotionDetector(webcam);
		detector.setInterval(500); // one check per 500 ms
		detector.addMotionListener(this);
		detector.start();
	}

	@Override
	public void motionDetected(WebcamMotionEvent arg0) {
		long curTime = System.currentTimeMillis();
		
		// 撮影間隔をチェック
		if(curTime - previousTime < intervalMSec) {
			return;
		}
		previousTime = curTime;
		
		System.out.println("＃＃＃＃＃＃＃　見つけた！ - "+getTimeString());
		// get image
		BufferedImage image = webcam.getImage();

		// save image to PNG file
		
		String file = getTimeString() + ".jpg";
		file = file.replaceAll("[_]", "").replaceAll("[:]", "").replaceAll("T", "_");
		try {
			ImageIO.write(image, "JPG", new File(file));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void start() throws IOException, InterruptedException {
		
		previousTime = Long.MAX_VALUE;
		System.out.println("＃＃＃＃＃＃＃　プログラム開始しました - "+getTimeString());
		Thread.sleep(firstMSec);
		
		System.out.println("＃＃＃＃＃＃＃　監視を開始しました - "+getTimeString());
		
		previousTime = System.currentTimeMillis() - intervalMSec;
		
		detector.start();
		
		int ch;
		do {
			ch = System.in.read();
		} while (ch == 0);
		
		System.out.println("＃＃＃＃＃＃＃　監視を終了しました - "+getTimeString());
	}
	
	private String getTimeString() {
		LocalDateTime time = LocalDateTime.now();
		String text = time.format(DateTimeFormatter.ISO_DATE_TIME);
		return text;
	}

}
