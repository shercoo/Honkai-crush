package triple;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class TripleMusic extends Music{
	public void init(){
		path = new File("./mis/game.wav");
		
		//Get audio input stream
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(path);
			clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			FloatControl gainControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
			double dbValue = gainControl.getValue();
			gainControl.setValue(-5.0f);
			/*clip.loop(Clip.LOOP_CONTINUOUSLY);
			Thread.sleep(10000);*/
		}catch(Exception e) {
			e.printStackTrace();
		}
 
	}
	
	TripleMusic(){
		init();
	}
}
