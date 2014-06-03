import javax.sound.sampled.*;

import java.io.*;

public class Sound {
	
	static String currentSong = null;
	static Clip playing;
	
	public static void music(String name){
		if(name.equals(currentSong)) return;
		
		if(playing != null) playing.close();
		
		try {
			playing = AudioSystem.getClip();
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(Sound.class.getResourceAsStream("music/" + name + ".wav"));
			playing.open(inputStream);
			playing.loop(Clip.LOOP_CONTINUOUSLY);
			currentSong = name;
		} 
		catch (LineUnavailableException e) { } 
		catch (UnsupportedAudioFileException e) {  } 
		catch (IOException e) { System.err.println("Audio file not found: music/" + name + ".wav"); }
		
	}
	
	public static void sound(String name){
		try {
			Clip clip = AudioSystem.getClip();
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(Sound.class.getResourceAsStream("sound/" + name + ".wav"));
			clip.open(inputStream);
			clip.start();
			currentSong = name;
		} 
		catch (LineUnavailableException e) { e.printStackTrace(); } 
		catch (UnsupportedAudioFileException e) { e.printStackTrace(); } 
		catch (IOException e) { System.err.println("Audio file not found: music/" + name + ".wav"); }
	}
}
