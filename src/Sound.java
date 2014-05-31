import javax.sound.sampled.*;

import java.io.*;

public class Sound {
	
	static String currentSong = null;
	
	public static void music(String name){
		try {
			Clip clip = AudioSystem.getClip();
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(Sound.class.getResourceAsStream("music/" + name + ".wav"));
			clip.open(inputStream);
			clip.loop(Clip.LOOP_CONTINUOUSLY);
			currentSong = name;
		} 
		catch (LineUnavailableException e) { e.printStackTrace(); } 
		catch (UnsupportedAudioFileException e) { e.printStackTrace(); } 
		catch (IOException e) { System.err.println("Audio file not found: music/" + name + ".wav"); }
		
	}
}
