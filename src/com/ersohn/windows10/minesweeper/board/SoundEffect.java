package com.ersohn.windows10.minesweeper.board;

import java.io.*;
import java.net.URL;
import javax.sound.sampled.*;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class SoundEffect extends JFrame {
	private Clip clip;
	
	SoundEffect(String soundFileName) {
		try {
			URL url = this.getClass().getClassLoader().getResource(soundFileName);
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
			
			clip = AudioSystem.getClip();
			clip.open(audioInputStream);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		if (clip.isRunning()) {
			clip.stop();
		}
		clip.setFramePosition(0);
		clip.start();
	}
	
	public void removeClip() {
		clip.close();
	}
}
