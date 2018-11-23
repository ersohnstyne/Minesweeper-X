package com.ersohn.windows10.minesweeper.board;

import java.awt.*;
import java.awt.event.*;

import javax.swing.JButton;

import com.ersohn.windows10.minesweeper.*;

public class Cell implements ActionListener {
	public int xlocation;
	public int ylocation;
	
	private boolean multipleclick = false;
	private boolean radararea = false;
	
	private JButton btn;
	private Board brd;
	private boolean ready;
	private int value;
	private int id;
	
	private boolean unicodes = true;
	
	public boolean mineflag = false;
	public String flagicon = unicodes ? "\u2691" : "P"; // true = Real Flag; false = Flag with P
	private boolean explodeOnClick = false;
	
	SoundEffect snd;
	
	public void setOrRemoveFlag() {
		if (mineflag) {
			MinesweeperX.changeamount(1);
			btn.setText("");
			mineflag = false;
		} else {
			MinesweeperX.changeamount(-1);
			btn.setText(flagicon);
			mineflag = true;
		}
	}
	
	public void setLocation(int x, int y) {
		this.xlocation = x;
		this.ylocation = y;
	}
	
	public int getLocation(String c) {
		switch (c) {
			case "x": return xlocation;
			case "y": return ylocation;
			default: return 0;
		}
	}
	
	public Cell(Board b) {
		btn = new JButton(); 
		ready = true;
		
		multipleclick = false;
		
		btn.addActionListener(this);
		btn.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getButton() == MouseEvent.BUTTON3 && /*(!MinesweeperX.challengetype.equals("lmb") || MinesweeperX.challengetype.equals("rmb")) &&*/ !brd.timestop) {
					if (brd.status == Status.NONE) {
						if (mineflag) {
							MinesweeperX.changeamount(1);
							btn.setText("");
							mineflag = false;
						} else {
							if (btn.isEnabled()) {
								MinesweeperX.changeamount(-1);
								btn.setText(flagicon);
								mineflag = true;
							}
						}
					}
					
					multipleclick = false;
				} else if (arg0.getButton() == MouseEvent.BUTTON2 /*&& (!MinesweeperX.challengetype.equals("lmb"))*/) {
					radararea = false;
					if (brd.allowMulticlick)
						multiclick(ylocation, xlocation);
				} else if (arg0.getButton() == MouseEvent.BUTTON1) {
					radararea = false;
					if (btn.getText().equals(flagicon) && !radararea) {
						checkCell();
					}
					if (multipleclick && !MinesweeperX.challengetype.equals("lmb")) {
						if (checkForSpaces()) {
							if (brd.allowMulticlick)
								multiclick(ylocation, xlocation);
						}
					}
				}
				
				brd.updateRequiredAmount();
			}
			
			public boolean checkForSpaces() {
				return !btn.getText().equals(flagicon) && !isMine();
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {}
			
			@Override
			public void mouseExited(MouseEvent e) {}
			
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3 && !MinesweeperX.challengetype.equals("lmb")) {
					multipleclick = true;
				}
				
				if (multipleclick) {
					radararea = true;
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3 && !MinesweeperX.challengetype.equals("lmb")) {
					multipleclick = false;
				}
				
				if (multipleclick) {
					radararea = false;
				}
			}
		});
		btn.setText("");
		btn.setPreferredSize(new Dimension(8,8));
		btn.setMargin(new Insets(0,0,0,0));
		this.brd = b;
	}
	
	public void multiclick(int y, int x) {
		brd.multiclick(y, x);
	}
	
	public JButton getBtn() {
		return btn;
	}
	
	public int getValue() {
		return value;
	}
	
	public int getID() {
		return id;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public void setID(int id) {
		this.id = id;
	}
	
	public void displayValue(Color c) {
		if (isMine()) {
			if (!btn.getText().equals("P") && !btn.getText().equals("\u2691")) {
				btn.setText("\u2600");
				btn.setBackground(c);
			} else if (isMine()) {
				btn.setBackground(Color.GREEN);
			} else {
				btn.setBackground(Color.YELLOW);
			}
		} else if(value!=0) {
			for (int i = 0; i < 1000; i++) {
				btn.setText(String.valueOf(value));
			}
		}
	}
	
	public int incrementNumber(int currentnumber) {
		int current = currentnumber; current++;
		return current;
	}
	
	public boolean getExplosion() {
		return explodeOnClick;
	}
	
	public void checkCell() {
		if (getBtn().getText().equals(flagicon)) return;
		
		if (btn.isEnabled()) {
			brd.getsFirst = false;
			
			if (MinesweeperX.spaces < 0) {
				brd.reset();
			}
			
			if (brd.timestop && !brd.EDITOR_MODE) {
				int totalattempt = 0;
				boolean doThis = value != 0 || isMine() && MinesweeperX.spaces > 7;
				//brd.moveMines(xlocation, ylocation);
				//brd.resetValues();
				brd.plantMines(xlocation, ylocation);
				brd.setCellValues();
				while (doThis || value != 0 && totalattempt < 100000) {
					brd.reset();
					brd.plantMines(xlocation, ylocation);
					brd.setCellValues();
					doThis = value != 0 && MinesweeperX.spaces > 8;
					totalattempt++;
				}
				brd.startTimer();
			} else if (brd.timestop) {
				brd.setCellValues();
				brd.startTimer();
			}
			
			brd.allowMulticlick = true;
			
			if (isMine() || brd.isDone()) {
				explodeOnClick = true;
				reveal(Color.MAGENTA);
				if (isMine()) {
					MinesweeperX.spaces++;
					brd.stopTimer();
					brd.explosion();
				}
			} else if (value == 0) {
				reveal(Color.BLACK);
				brd.scanForEmptyCells(xlocation, ylocation);
			} else {
				reveal(Color.BLACK);
				if (value == 0) {
					brd.scanForEmptyCells(xlocation, ylocation);
				}
			}
			
			brd.startTimer();
			MinesweeperX.decrementSpaces();
		}
		
		if (MinesweeperX.spaces < 1) {
			brd.finish();
		}
	}
	
	public void openLotSpaces() {
		if (btn.isEnabled() && !btn.getText().equals(flagicon)) {
			MinesweeperX.decrementSpaces();
			reveal(Color.BLACK);
			if (value == 0) {
				brd.scanForEmptyCells(xlocation, ylocation);
			}
		}
	}
	
	public void incrementValue(){
		value++;
	}
	
	public void resetValue() {
		value = 0;
	}
	
	public boolean isChecked(){
		return ready;
	}
	
	public boolean isEmpty(){
		return isChecked() && value==0 /*|| value==1 || value==2 || value==3 || value==4 || value==5 || value==6 || value==7 || value==8*/;
	}
	
	public void reveal(Color c) {
		displayValue(c);
		ready = false;
		btn.setEnabled(false);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (btn.getText().equals(flagicon)) return;
		
		while (value != 0 && brd.timestop && MinesweeperX.spaces >= 8) {
			brd.reset();
		}
		
		if (!btn.getText().equals(flagicon)) {
			if (multipleclick) {
				boolean multiple = brd.multiclick(ylocation, xlocation);
				if (multiple && brd.allowMulticlick)
					checkCell();
			}
			else {
				if (value == 0) {
					/*if (MinesweeperX.getSound) {
						snd = new SoundEffect("openspaces.wav");
						if (value == 0) snd.run();
					}*/
				}
				
				checkCell();
			}
		}
		brd.updateRequiredAmount();
	}
	
	public int setMine() {
		if (!isMine()) {
			setValue(-1);
			return 1;
		}
		return 0;
	}
	
	public void removeMine() {
		if (isMine()) {
			setValue(0);
		}
	}
	
	public boolean isMine() {
		return value == -1;
	}
	
	public void reset() {
		brd.getsFirst = true;
		explodeOnClick = false;
		mineflag = false;
		
		if (MinesweeperX.getSound)
			try { snd.removeClip(); } catch (NullPointerException xnoValue) {}
		
		setValue(0);
		btn.setText("");
		btn.setBackground(null);
		ready = true;
		btn.setEnabled(ready);
	}
}
