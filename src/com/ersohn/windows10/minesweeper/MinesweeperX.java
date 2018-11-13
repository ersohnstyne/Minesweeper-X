package com.ersohn.windows10.minesweeper;

import java.io.IOException;

import javax.naming.CannotProceedException;
import javax.swing.SwingUtilities;

import com.ersohn.windows10.minesweeper.board.*;
import com.ersohn.windows10.minesweeper.general.ProgramConstants;
import com.ersohn.windows10.minesweeper.io.Highscore;
import com.ersohn.windows10.minesweeper.mode.Mode;
import com.ersohn.windows10.minesweeper.resources.SplashGame;

public class MinesweeperX {
	//public static final MinesweeperX INSTANCE = new MinesweeperX();
	
	public static Mode currdifficultytype = Mode.EASY;
	
	public static boolean getSound = false;
	
	public static boolean getFlag = false;
	private static int numFlags = 0;
	
	public static int horiz = 9;
	public static int vert = 9;
	public static int totmines = 10;
	
	// Highscores
	private static Highscore hs;
	public static int hsTime = 360000;
	public static String username = System.getProperty("user.name");
	public static String datapath = System.getProperty("user.home") + "\\Documents\\PennyGames Development";
	
	// Stats
	public static int nonExplodingCount;
	public static int clickExplodeCount;
	
	public static int spaces;
	
	public static void main(String[] services) {
		System.out.println(datapath);
		
		try {
			if (services[0].contains("-h") || services[0].contains("--help")) {
				System.out.println(
						  "Usage: java -jar minesweeper-x.jar (" + ProgramConstants.TITLE + ") [options ...]\n"
						+ "-h  --help\tShow parameters\n"
						+ "-s  --sound\tLaunch application with sounds (similar like an Unity Game)\n"
						+ "-b  --board\tGenerate board (-b HEIGHT WIDTH MINES; Default is 9 mines.)\n"
						);
			}
			if (services[0].contains("-s") || services[0].contains("--sound")) {
				try {
					SplashGame sg = new SplashGame();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				getSound = true;
				SwingUtilities.invokeLater(() -> new Board(false).setBoard());
			}
			if (services[0].contains("-b") || services[0].contains("--board")) {
				int custommines = 9;
				int customwidth = 0;
				int customheight = 0;
				
				try {
					try {
						customwidth = Integer.parseInt(services[1]);
						if (customwidth < 5) {System.out.println("Value width too small");}
					} catch (NumberFormatException xwidth) {
						System.out.println("That is not a number of width"); return;
					}
				} catch (IndexOutOfBoundsException xnowidth) {
					System.out.println("Option for width required"); return;
				}
				
				try {
					try {
						customheight = Integer.parseInt(services[2]);
						if (customheight < 5) {System.out.println("Value height too small");}
					} catch (NumberFormatException xheight) {
						System.out.println("That is not a number of height"); return;
					}
				} catch (IndexOutOfBoundsException xnoheight) {
					System.out.println("Option for height required");
				}
				
				try {
					try {
						custommines = Integer.parseInt(services[3]);
						if (custommines < 9) {System.out.println("Value mines too small");}
					} catch (NumberFormatException xmines) {
						System.out.println("That is not a number of mines"); return;
					}
				} catch (IndexOutOfBoundsException xnoheight) {
					
				}
				
				try {
					SplashGame sg = new SplashGame();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Board brd = new Board(false);
				brd.setBoard();
				
				try {
					currdifficultytype = Mode.CUSTOM;
					brd.setCustom(customwidth, customheight, custommines);
				} catch (CannotProceedException xnogenerated) {
					System.out.println(xnogenerated); return;
				}
			}
		} catch (Exception xnoparam) {
			switch (currdifficultytype) {
				case EASY: {
					hs = new Highscore(ProgramConstants.NAME, Mode.EASY);
					try {
						hsTime = hs.getHighscore();
					} catch (ClassNotFoundException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
				case MEDIUM: {
					hs = new Highscore(ProgramConstants.NAME, Mode.MEDIUM);
					try {
						hsTime = hs.getHighscore();
					} catch (ClassNotFoundException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
				case HARD: {
					hs = new Highscore(ProgramConstants.NAME, Mode.HARD);
					try {
						hsTime = hs.getHighscore();
					} catch (ClassNotFoundException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
				default: {
					break;
				}
			}
			
			try {
				SplashGame sg = new SplashGame();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			SwingUtilities.invokeLater(() -> new Board(false).setBoard());
		}
	}
	
	public static void submitHs(int time) {
		hsTime = time;
		switch (currdifficultytype) {
		case EASY: {
			try {
				if (hs == null) hs = new Highscore(ProgramConstants.NAME, Mode.EASY);
				hs.setHighScore(time);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			break;
		}
		case MEDIUM: {
			try {
				if (hs == null) hs = new Highscore(ProgramConstants.NAME, Mode.MEDIUM);
				hs.setHighScore(time);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			break;
		}
		case HARD: {
			try {
				if (hs == null) hs = new Highscore(ProgramConstants.NAME, Mode.HARD);
				hs.setHighScore(time);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			break;
		}
		default:
			break;
		}
	}
	
	public static void updateDifficulty(int horizontal, int vertical, int mines, Mode difficultytype) {
		horiz = horizontal;
		vert = vertical;
		totmines = mines;
		
		currdifficultytype = difficultytype;
		
		hsTime = 360000;
		
		switch (difficultytype) {
		case EASY: {
			if (hs != null) {hs.deinitialize(); hs = null;}
			hs = new Highscore(ProgramConstants.NAME, Mode.EASY);
			try {
				hsTime = hs.getHighscore();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			break;
		}
		case MEDIUM:  {
			if (hs != null) {hs.deinitialize(); hs = null;}
			hs = new Highscore(ProgramConstants.NAME, Mode.MEDIUM);
			try {
				hsTime = hs.getHighscore();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			break;
		}
		case HARD: {
			if (hs != null) {hs.deinitialize(); hs = null;}
			hs = new Highscore(ProgramConstants.NAME, Mode.HARD);
			try {
				hsTime = hs.getHighscore();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			break;
		}
		default: {
			if (hs != null) {hs.deinitialize(); hs = null;}
			break;
		}
		}
		SwingUtilities.invokeLater(() -> new Board(false).setBoard());
	}
	
	public static void decrementSpaces() {
		spaces--;
	}
	
	public static void changeamount(int numberofflags) {
		numFlags -= numberofflags;
	}
	
	public static void resetamount() {
		numFlags = 0;
	}
	
	public static int getRequiredMines() {
		return totmines - numFlags;
	}
}
