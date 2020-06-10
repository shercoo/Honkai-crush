package triple;

import java.util.*;
import java.applet.*;
import java.awt.*;
import java.io.*;
import java.net.*;

import javax.sound.sampled.*;
import javax.swing.*;


public class Music {
	File path;
	Float down;
	Clip clip;
	
	void init() {
		/*path = new File("D:/eclipse/workspace/Triple/mis/label.wav");
		
		//Get audio input stream
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(path);
			Clip clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			FloatControl gainControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
			double dbValue = gainControl.getValue();
			gainControl.setValue(-5.0f);
			clip.start();
			Thread.sleep(100000);
		}catch(Exception e) {
			e.printStackTrace();
		}*/
	}
	
	void start() { init(); clip.start(); }
	
	void loop() { init(); clip.loop(Clip.LOOP_CONTINUOUSLY);}
	
	void stop() { clip.stop(); }
	
}
