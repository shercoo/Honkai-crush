package triple;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.SwingUtilities;

public class LabelMusic extends Music{
	@Override
	public void init(){
		path = new File("./mis/label.wav");
		down = -5.0f;
		//Get audio input stream
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(path);
			clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			FloatControl gainControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
			//double dbValue = gainControl.getValue();
			gainControl.setValue(-5.0f);
			//clip.loop(Clip.LOOP_CONTINUOUSLY);
			//Thread.sleep(10000);
		}catch(Exception e) {
			e.printStackTrace();
		}

		
 
	}
	public static void main(String[] args) {
		LabelMusic a = new LabelMusic();
		
		a.loop();
		try {
		Thread.sleep(1000);
		} catch(Exception e) {}
	}
}
