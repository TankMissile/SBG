import javax.sound.sampled.*;

import java.io.*;
import java.net.URISyntaxException;

public class Sound {

	/*public static void music(String path)
	{
		AudioPlayer ap = AudioPlayer.player;
		AudioStream bgm;
		AudioData ad;
		ContinuousAudioDataStream loop = null;
		
		try {
			bgm = new AudioStream(new FileInputStream(new File(Sound.class.getResource("music/" + path + ".wav").toURI())));
			ad = bgm.getData();
			loop = new ContinuousAudioDataStream(ad);
		} 
		catch (FileNotFoundException e) { e.printStackTrace(); } 
		catch (IOException e) { e.printStackTrace(); } 
		catch (URISyntaxException e) { e.printStackTrace(); }
		
		ap.start(loop);
	}*/
	
	public static void music(String name){
		try {
			Clip clip = AudioSystem.getClip();
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(Sound.class.getResourceAsStream("music/" + name + ".wav"));
			clip.open(inputStream);
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		} 
		catch (LineUnavailableException e) { e.printStackTrace(); } 
		catch (UnsupportedAudioFileException e) { e.printStackTrace(); } 
		catch (IOException e) { e.printStackTrace(); }
		
	}
}
