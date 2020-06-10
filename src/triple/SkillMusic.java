package triple;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.SwingUtilities;

public class SkillMusic extends Music{
	
	String[] name = new String[4];
	int type;
	@Override
	public void init(){
		path = new File(name[type]);
		down = -5.0f;
		//Get audio input stream
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(path);
			clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			FloatControl gainControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
			//double dbValue = gainControl.getValue();
			if(type != 0 && type != 3)
				gainControl.setValue(-5.0f);
			else if(type != 3)
				gainControl.setValue(-11.0f);
			else 
				gainControl.setValue(-8.0f);
			//clip.loop(Clip.LOOP_CONTINUOUSLY);
			//Thread.sleep(10000);
		}catch(Exception e) {
			e.printStackTrace();
		}

		
 
	}
	
	SkillMusic(int x) {
		type = x;
		name[0] = "./mis/Kiana.wav";
		name[1] = "./mis/yayi.wav";
		name[2] = "./mis/bronya.wav";
		name[3] = "./mis/seele.wav";
	}
	
	SkillMusic(){
		type = 0;
		name[0] = "./mis/Kiana.wav";
		name[1] = "./mis/yayi.wav";
		name[2] = "./mis/bronya.wav";
		name[3] = "./mis/seele.wav";
	}
	
	public static void main(String[] args) {
		SkillMusic a = new SkillMusic();
		
		a.loop();
		try {
		Thread.sleep(2000);
		} catch(Exception e) {}
	}
}
