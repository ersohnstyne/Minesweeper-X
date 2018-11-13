package com.ersohn.windows10.minesweeper.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Hashtable;

import com.ersohn.windows10.minesweeper.MinesweeperX;
import com.ersohn.windows10.minesweeper.mode.Mode;

public class Highscore {
	private String gameName;
	private File highScoreFile;
	
	public Highscore(String gameName, Mode mode) {
		this.gameName = gameName;
		
		AccessController.doPrivileged(new PrivilegedAction<Object>() {
			public Object run() {
				String modedata = "custom";
				switch (mode) {
				case EASY: {
					modedata = "Easy.dat";
					break;
				}
				case MEDIUM: {
					modedata = "Medium.dat";
					break;
				}
				case HARD: {
					modedata = "Hard.dat";
					break;
				}
				default: break;
				}
				String path = MinesweeperX.datapath + File.separator + gameName + File.separator + "Highscores" + File.separator + modedata;
				highScoreFile = new File(path);
				
				return null;
			}
		});
	}
	
	public void deinitialize() {
		if (highScoreFile != null) highScoreFile = null;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setHighScore(int score) throws IOException {
		SecurityManager sm = System.getSecurityManager();
		if (sm != null) sm.checkPermission(new HighscorePermission(gameName));
		
		try {
			AccessController.doPrivileged(new PrivilegedExceptionAction() {
				@Override
				public Object run() throws IOException {
					Hashtable<String, String> scores = null;
					
					try {
						FileInputStream fis = new FileInputStream(highScoreFile);
						ObjectInputStream ois = new ObjectInputStream(fis);
						scores = (Hashtable<String, String>) ois.readObject();
					} catch (Exception xnofile) {}
					
					if (scores == null) scores = new Hashtable<>(score);
					
					scores.put(System.getProperty("user.name"), "" + score);
					FileOutputStream fos = new FileOutputStream(highScoreFile);
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					oos.writeObject(scores);
					oos.close();
					
					return null;
				}
				
			});
		} catch (PrivilegedActionException xhspae) {throw (IOException) xhspae.getException();}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int getHighscore() throws IOException, ClassNotFoundException {
		SecurityManager sm = System.getSecurityManager();
		if (sm != null) sm.checkPermission(new HighscorePermission(gameName));
		
		Integer score = null;
		
		try {
			score = (Integer) AccessController.doPrivileged(new PrivilegedExceptionAction() {
				@Override
				public Object run() throws IOException, ClassNotFoundException {
					Hashtable scores = null;
					FileInputStream fis = new FileInputStream(highScoreFile);
					ObjectInputStream ois = new ObjectInputStream(fis);
					scores = (Hashtable) ois.readObject();
					
					return scores.get(gameName);
				}
			});
		} catch (PrivilegedActionException xhspae) {
			Exception e = xhspae.getException();
			if (e instanceof IOException)
				throw (IOException) e;
			else
				throw (ClassNotFoundException) e;
		}
		
		if (score == null)
			return 360000;
		else
			return score.intValue();
	}
}
