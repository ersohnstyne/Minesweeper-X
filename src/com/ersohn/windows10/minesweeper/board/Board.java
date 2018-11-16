package com.ersohn.windows10.minesweeper.board;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.*;

import javax.naming.CannotProceedException;
import javax.swing.*;
import javax.swing.Timer;

import com.ersohn.windows10.minesweeper.*;
import com.ersohn.windows10.minesweeper.general.ProgramConstants;
import com.ersohn.windows10.minesweeper.mode.Mode;

public class Board {
	SoundEffect snd;
	
	public static boolean EDITOR_MODE = false;
	
	public boolean timestop = true;
	private int totaltime = 0;
	private boolean doOnce = false;
	
	public JFrame brdframe;
	private JPanel cellPanel;
	
	private JLabel timeLabel;
	private JLabel amountLabel;
	
	private JLabel hsLabel;
	
	private JLabel nonExploding;
	private JLabel wExploding;
	
	private JTextField custom;
	
	public Cell[][] cells;
	private int cellID = 0;
	private int xside = (MinesweeperX.horiz);
	private int yside = (MinesweeperX.vert);
	
	private int MINES = (MinesweeperX.totmines);
	public boolean allowMulticlick = false;
	
	public boolean getsFirst = true;
	public Status status;
	
	public int foundmines = 0;
	public boolean doOnceStatus = true;
	
	public void OpenCell(int y, int x) {
		if (!cells[y][x].isMine())
		cells[y][x].checkCell();
	}
	
	public void PreviewOpenSpaces(int y, int x) {
		if (!cells[y][x].isMine() && cells[y][x].getValue() == 0 && cells[y][x].getBtn().isEnabled())
		cells[y][x].checkCell();
	}
	
	public ActionListener timeAction = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (!timestop) {
				totaltime++;
				timeLabel.setText("Time: " + totaltime);
				return;
			}
		}
	};
	
	public ActionListener detectGenerator = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				String[] values = custom.getText().split("-");
				String[] widthheight = values[0].split("x");
				try {
					setCustom(Integer.parseInt(widthheight[0]), Integer.parseInt(widthheight[1]), Integer.parseInt(values[1]));
				} catch (ClassNotFoundException | IOException e1) {}
			} catch (CannotProceedException xnogenerated) {
				
			} catch (NumberFormatException xnonumber) {
				
			} catch (IndexOutOfBoundsException xexceed) {
				
			}
		}
	};
	
	private Timer time = new Timer(1000, timeAction);
	
	public void startTimer() {
		if (timestop && !doOnce) {
			timestop = false;
			doOnce = true;
			time = new Timer(1000, timeAction);
			time.start();
		} else {
			if (status == Status.NONE) time.start();
			timestop = false;
		}
	}
	
	public void stopTimer() {
		time.stop();
		timestop = true;
	}
	
	/**
	 * Create board (with editor or classic mode)
	 * @param editor
	 */
	public Board(boolean editor) {
		xside = MinesweeperX.horiz;
		yside = MinesweeperX.vert;
		
		if (xside < 3) xside = 3;
		if (yside < 3) yside = 3;
		
		if (editor) {
			getsFirst = false;
			EDITOR_MODE = true;
		}
		
		cells = new Cell[xside][yside];
		IntStream.range(0, xside).forEach(i -> {
			IntStream.range(0, yside).forEach(j -> cells[i][j] = new Cell(this));
		});
		
		if (!editor) {
			init();
		}
	}
	
	public void setBoard(){
		brdframe = new JFrame();
		brdframe.setResizable(false);
		brdframe.setTitle(ProgramConstants.TITLE);
		brdframe.setIconImage(null);
		brdframe.setSize(new Dimension((yside * 21) * 3, xside * 25));
		
		System.out.println("Width: " + brdframe.getSize().width + "; Height: " + brdframe.getSize().height);
		
		if (brdframe.getSize().width < 567) {
			brdframe.setSize(new Dimension(567, xside * 25));
			if (brdframe.getSize().height < 225) {
				brdframe.setSize(new Dimension(567, 225));
			}
		}
		if (brdframe.getSize().height < 225) {
			brdframe.setSize(new Dimension((yside * 21) * 3, 225));
			if (brdframe.getSize().width < 567) {
				brdframe.setSize(new Dimension(567, 225));
			}
		}
		
		brdframe.getContentPane().setLayout(new GridLayout(1,2));
		brdframe.add(addStatusPanel());
		brdframe.add(addCells());
		brdframe.add(addControlPanel());
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		brdframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		brdframe.setLocation((screenSize.width / 2) - (((yside * 21) * 3) / 2), (screenSize.height / 2) - ((xside * 25) / 2));
		brdframe.setVisible(true);
	}
	
	private JPanel addStatusPanel() {
		JPanel panel = new JPanel();
		
		timeLabel = new JLabel("Time: 0");
		amountLabel = new JLabel("Amount: " + MINES);
		hsLabel = new JLabel("Best time: " + MinesweeperX.hsTime);
		nonExploding = new JLabel("All open w/o explosion: " + MinesweeperX.nonExplodingCount);
		wExploding = new JLabel("Times exploded: " + MinesweeperX.clickExplodeCount);
		
		// XXX Set this text to highscore
		if (MinesweeperX.hsTime >= 360000) {
			hsLabel.setText("Best time: ------");
		}
		
		panel.add(timeLabel);
		panel.add(amountLabel);
		panel.add(hsLabel);
		panel.add(nonExploding);
		panel.add(wExploding);
		
		return panel;
	}
	
	public void updateRequiredAmount() {
		if (Status.NONEXPLODING != status)
		amountLabel.setText("Amount: " + MinesweeperX.getRequiredMines());
	}
	
	private JPanel addControlPanel() {
		JPanel panel = new JPanel(new GridLayout(4, 3));
		
		JButton reset = new JButton("Reset");
		reset.addActionListener(listener -> reset());
		
		JButton easy = new JButton("Easy");
		easy.addActionListener(listener -> {
			try {
				setEasy();
			} catch (ClassNotFoundException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		JButton med = new JButton("Medium");
		med.addActionListener(listener -> {
			try {
				setMed();
			} catch (ClassNotFoundException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		JButton hard = new JButton("Hard");
		hard.addActionListener(listener -> {
			try {
				setHard();
			} catch (ClassNotFoundException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		
		custom = new JTextField("9x9-10");
		JButton custombtn = new JButton("Generate");
		custombtn.addActionListener(detectGenerator);
		custombtn.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {}
			
			@Override
			public void keyPressed(KeyEvent e) {}
		});
		
		panel.add(reset);
		panel.add(easy);
		panel.add(med);
		panel.add(hard);
		panel.add(custom);
		panel.add(custombtn);
		
		return panel;
	}

	public JPanel addCells(){
		xside = MinesweeperX.horiz;
		yside = MinesweeperX.vert;
		
		if (xside < 3) xside = 3;
		if (yside < 3) yside = 3;
		
		cellPanel = new JPanel(new GridLayout(xside, yside));
		forEach(cell -> cellPanel.add(cell.getBtn()));
		return cellPanel;
	}

	public void plantMines(){
		if (MinesweeperX.getSound) {
			Random randomsnd = new Random();
			String[] boomsnd = {"explode1.wav", "explode2.wav", "explode3.wav", "explode4.wav"};
			snd = new SoundEffect(boomsnd[randomsnd.nextInt(3)]);
		}
		
		Random random = new Random();
		int counter = 0;
		while (counter != MINES) {
			int xloc = random.nextInt(MinesweeperX.horiz);
			int yloc = random.nextInt(MinesweeperX.vert);
			
			while (cells[xloc][yloc].isMine()) {
				xloc = random.nextInt(MinesweeperX.horiz);
				yloc = random.nextInt(MinesweeperX.vert);
			}
			
			counter += cells[xloc][yloc].setMine();
		}
		MinesweeperX.spaces = (MinesweeperX.horiz * MinesweeperX.vert - counter);
	}
	
	public void setMines(int x, int y) {
		if (MinesweeperX.getSound) {
			Random randomsnd = new Random();
			String[] boomsnd = {"explode1.wav", "explode2.wav", "explode3.wav", "explode4.wav"};
			snd = new SoundEffect(boomsnd[randomsnd.nextInt(3)]);
		}
		cells[x][y].setMine();
	}
	
	/**Choose rendom places for mines*/
	public ArrayList<Integer> generateMinesLocation(int q){
		ArrayList<Integer> loc = new ArrayList<Integer>();
		int random;
		for(int i = 0; i<q;){
			random = (int)(Math.random()* (xside*yside));
			if(!loc.contains(random)){
				loc.add(random);
				i++;
			}
		}
		return loc;
	}
	// MOST IMPORTANT PART/////////////////////////////////////////////////////
	/**This method count number of mines around particular cell and set its value*/
	public void setCellValues(){
		// cells[i][j].incrementValue();
		for(int i = 0; i<xside; i++){
			for(int j = 0; j<yside; j++){
				cells[i][j].setLocation(j, i);
				if(cells[i][j].getValue() != -1){
					try { if(j>=1 && cells[i][j-1].getValue() == -1) { cells[i][j].incrementValue(); } } catch (IndexOutOfBoundsException xmax) {}
					try { if(j<= yside && cells[i][j+1].getValue() == -1) { cells[i][j].incrementValue(); } } catch (IndexOutOfBoundsException xmax) {}
					try { if(i>=1 && cells[i-1][j].getValue() == -1) { cells[i][j].incrementValue(); } } catch (IndexOutOfBoundsException xmax) {}
					try { if(i<= xside && cells[i+1][j].getValue() == -1) { cells[i][j].incrementValue(); } } catch (IndexOutOfBoundsException xmax) {}
					try { if(i>=1 && j>= 1 && cells[i-1][j-1].getValue() == -1) { cells[i][j].incrementValue(); } } catch (IndexOutOfBoundsException xmax) {}
					try { if(i<= xside && j<= yside && cells[i+1][j+1].getValue() == -1) { cells[i][j].incrementValue(); } } catch (IndexOutOfBoundsException xmax) {}
					try { if(i>=1 && j<= yside && cells[i-1][j+1].getValue() == -1) { cells[i][j].incrementValue(); } } catch (IndexOutOfBoundsException xmax) {}
					try { if(i<= xside && j>= 1 && cells[i+1][j-1].getValue() == -1) { cells[i][j].incrementValue(); } } catch (IndexOutOfBoundsException xmax) {}
				}
			}
		}
	}
	
	/**This method starts chain reaction. When user click on particular cell, if cell is empty (value = 0) this
	method look for other empty cells next to activated one. If finds one, it call checkCell and in effect,
	start next scan on its closest area.
	 */
	public void scanForEmptyCells(int y, int x){
		try {
			while (cells[x-1][y-1].getBtn().isEnabled()) cells[x-1][y-1].openLotSpaces();
		} catch (IndexOutOfBoundsException xmax) {
			//xmax.printStackTrace();
		}
		try {
			while (cells[x][y-1].getBtn().isEnabled()) cells[x][y-1].openLotSpaces();
		} catch (IndexOutOfBoundsException xmax) {
			//xmax.printStackTrace();
		}
		try {
			while (cells[x+1][y-1].getBtn().isEnabled()) cells[x+1][y-1].openLotSpaces();
		} catch (IndexOutOfBoundsException xmax) {
			//xmax.printStackTrace();
		}
		try {
			while (cells[x-1][y+1].getBtn().isEnabled()) cells[x-1][y+1].openLotSpaces();
		} catch (IndexOutOfBoundsException xmax) {
			//xmax.printStackTrace();
		}
		try {
			while (cells[x][y+1].getBtn().isEnabled()) cells[x][y+1].openLotSpaces();
		} catch (IndexOutOfBoundsException xmax) {
			//xmax.printStackTrace();
		}
		try {
			while (cells[x+1][y+1].getBtn().isEnabled()) cells[x+1][y+1].openLotSpaces();
		} catch (IndexOutOfBoundsException xmax) {
			//xmax.printStackTrace();
		}
		try {
			while (cells[x-1][y].getBtn().isEnabled()) cells[x-1][y].openLotSpaces();
		} catch (IndexOutOfBoundsException xmax) {
			//xmax.printStackTrace();
		}
		try {
			while (cells[x+1][y].getBtn().isEnabled()) cells[x+1][y].openLotSpaces();
		} catch (IndexOutOfBoundsException xmax) {
			//xmax.printStackTrace();
		}
	}
	
	/**Multiclick is impressive, but may only use by Detector.
	 */
	public boolean multiclick(int x, int y) {
		if (cells[x][y].getBtn().isEnabled()) return false;
		
		int numflag = 0;
		int nummines = 0;
		
		// Radar mode
		try {if (!cells[x][y].getBtn().isEnabled()) {
			if (cells[x][y].getValue() > 0) {
				if (!cells[x][y].getBtn().getText().equals(cells[x][y].flagicon)) {
					try { if (cells[x-1][y-1].isMine()) { nummines++; } if (cells[x-1][y-1].getBtn().getText().equals(cells[x-1][y-1].flagicon)) {numflag++;} } catch (IndexOutOfBoundsException xmax) {}
					try { if (cells[x][y-1].isMine()) { nummines++; } if (cells[x][y-1].getBtn().getText().equals(cells[x][y-1].flagicon)) {numflag++;} } catch (IndexOutOfBoundsException xmax) {}
					try { if (cells[x+1][y-1].isMine()) { nummines++; } if (cells[x+1][y-1].getBtn().getText().equals(cells[x+1][y-1].flagicon)) {numflag++;} } catch (IndexOutOfBoundsException xmax) {}
					
					try { if (cells[x-1][y].isMine()) { nummines++; } if (cells[x-1][y].getBtn().getText().equals(cells[x-1][y].flagicon)) {numflag++;} } catch (IndexOutOfBoundsException xmax) {}
					try { if (cells[x+1][y].isMine()) { nummines++; } if (cells[x+1][y].getBtn().getText().equals(cells[x+1][y].flagicon)) {numflag++;} } catch (IndexOutOfBoundsException xmax) {}
					
					try { if (cells[x-1][y+1].isMine()) { nummines++; } if (cells[x-1][y+1].getBtn().getText().equals(cells[x-1][y+1].flagicon)) {numflag++;} } catch (IndexOutOfBoundsException xmax) {}
					try { if (cells[x][y+1].isMine()) { nummines++; } if (cells[x][y+1].getBtn().getText().equals(cells[x][y+1].flagicon)) {numflag++;} } catch (IndexOutOfBoundsException xmax) {}
					try { if (cells[x+1][y+1].isMine()) { nummines++; } if (cells[x+1][y+1].getBtn().getText().equals(cells[x+1][y+1].flagicon)) {numflag++;} } catch (IndexOutOfBoundsException xmax) {}
				}
			}
		}
		} catch (IndexOutOfBoundsException xmax) {}
		
		if (numflag != nummines) { return false; }
		
		// Reveal mode
		// Left bar
		try {if (!cells[x-1][y-1].getBtn().getText().equals("P") && !cells[x-1][y-1].getBtn().getText().equals("\u2691")) {cells[x-1][y-1].checkCell();}} catch (IndexOutOfBoundsException xmax) {}
		try {if (!cells[x][y-1].getBtn().getText().equals("P") && !cells[x][y-1].getBtn().getText().equals("\u2691"))     {cells[x][y-1].checkCell();}} catch (IndexOutOfBoundsException xmax) {}
		try {if (!cells[x+1][y-1].getBtn().getText().equals("P") && !cells[x+1][y-1].getBtn().getText().equals("\u2691")) {cells[x+1][y-1].checkCell();}} catch (IndexOutOfBoundsException xmax) {}
		
		// Center bar
		try {if (!cells[x-1][y].getBtn().getText().equals("P") && !cells[x-1][y].getBtn().getText().equals("\u2691")) {cells[x-1][y].checkCell();}} catch (IndexOutOfBoundsException xmax) {}
		//try {if (!cells[x][y].getBtn().getText().equals("P") && !cells[x][y].getBtn().getText().equals("\u2691"))   {cells[x][y].checkCell();}} catch (IndexOutOfBoundsException xmax) {}
		try {if (!cells[x+1][y].getBtn().getText().equals("P") && !cells[x+1][y].getBtn().getText().equals("\u2691")) {cells[x+1][y].checkCell();}} catch (IndexOutOfBoundsException xmax) {}
		
		// Right bar
		try {if (!cells[x+1][y+1].getBtn().getText().equals("P") && !cells[x-1][y+1].getBtn().getText().equals("\u2691")) {cells[x-1][y+1].checkCell();}} catch (IndexOutOfBoundsException xmax) {}
		try {if (!cells[x][y+1].getBtn().getText().equals("P") && !cells[x][y+1].getBtn().getText().equals("\u2691"))     {cells[x][y+1].checkCell();}} catch (IndexOutOfBoundsException xmax) {}
		try {if (!cells[x-1][y+1].getBtn().getText().equals("P") && !cells[x+1][y+1].getBtn().getText().equals("\u2691")) {cells[x+1][y+1].checkCell();}} catch (IndexOutOfBoundsException xmax) {}
		
		return true;
	}
	//////////////////////////////////////////////////////////////////////////////////
	public int getID(){
		int id = cellID;
		cellID++;
		return id;
	}

	public Cell getCell(int id){
		for(Cell[] a : cells){
			for(Cell b : a){
				if(b.getID() == id) return b;

			}
		}
		return null;
	}
	
	public void explosion(){
		if (doOnceStatus) {
			doOnceStatus = false;
			MinesweeperX.clickExplodeCount++;
			wExploding.setText("Times exploded: " + MinesweeperX.clickExplodeCount);
			if (MinesweeperX.getSound) {
				
				snd.run();
				//try {Thread.sleep(10 + random.nextInt(40));} catch (InterruptedException xnosmooth) {}
			}
			
			for(Cell[] a : cells){
				for(Cell b : a){
					if (b.isMine()) {
						stopTimer();
						if (!b.getExplosion())
							b.reveal(Color.RED);
							
						if (b.getBtn().getText().equals(b.flagicon))
							foundmines = b.incrementNumber(foundmines);
					} else if (b.isChecked()) {
						b.getBtn().setBackground(Color.BLUE);
						b.getBtn().setEnabled(false);
						if (b.getBtn().getText().equals(b.flagicon)) b.getBtn().setBackground(Color.CYAN);
					}
				}
			}
			stopTimer();
			status = Status.EXPLODED;
			showDialog();
		}
	}
	
	public void finish() {
		if (doOnceStatus) {
			doOnceStatus = false;
			MinesweeperX.nonExplodingCount++;
			nonExploding.setText("All open w/o explosion: " + MinesweeperX.nonExplodingCount);
			
			amountLabel.setText("Amount: 0");
			
			if (totaltime < MinesweeperX.hsTime) {
				if (!EDITOR_MODE) {
					MinesweeperX.hsTime = totaltime;
					MinesweeperX.submitHs(MinesweeperX.hsTime);
					
					// XXX Set this text to highscore
					hsLabel.setText("Best time: " + MinesweeperX.hsTime);
				}
			}
			
			for(Cell[] a : cells){
				for(Cell b : a){
					if (b.isMine()) {
						stopTimer();
						b.getBtn().setEnabled(false);
						b.getBtn().setBackground(Color.GREEN);
						b.getBtn().setText(b.flagicon);
					} else if (b.isChecked()) {
						b.getBtn().setEnabled(false);
					}
				}
			}
			
			stopTimer();
			status = Status.NONEXPLODING;
			showDialog();
		}
	}
	
	private void showDialog() {
		switch (status) {
		case NONEXPLODING: JOptionPane.showMessageDialog(brdframe, "Your time: " + totaltime + "\nBest: " + MinesweeperX.hsTime, "CONGRATULATIONS!", JOptionPane.PLAIN_MESSAGE); break;
		case EXPLODED: JOptionPane.showMessageDialog(brdframe, "You have found " + foundmines + (foundmines == 1 ? " Mine" : " Mines"), "EXPLODED!", JOptionPane.ERROR_MESSAGE); break;
		default: break;
		}
		stopTimer();
	}
	
	private void forEach(Consumer<Cell> consumer) {
		Stream.of(cells).forEach(row -> Stream.of(row).forEach(consumer));
	}
	
	private void init() {
		allowMulticlick = false;
		doOnceStatus = true;
		
		if (!EDITOR_MODE) {
			plantMines();
			//setCellValues();
		}
	}
	
	public boolean isDone() {
		int[] result = new int[1];
		forEach(cell -> { if (cell.isEmpty()) { result[0]++; }});
		return result[0] == MINES || MinesweeperX.spaces == 1;
	}
	
	public void reset() {
		foundmines = 0;
		status = Status.NONE;
		if (MinesweeperX.getSound)
		try { snd.removeClip(); } catch (NullPointerException xnoValue) {}
		
		forEach(cell -> cell.reset());
		totaltime = 0;
		timestop = true;
		MinesweeperX.resetamount();
		timeLabel.setText("Time: 0");
		amountLabel.setText("Amount: " + MinesweeperX.getRequiredMines());
		init();
	}
	
	public void setEasy() throws ClassNotFoundException, IOException {
		MinesweeperX.resetamount();
		foundmines = 0;
		status = Status.NONE;
		if (MinesweeperX.getSound)
		try { snd.removeClip(); } catch (NullPointerException xnoValue) {}
		
		stopTimer();
		brdframe.setVisible(false);
		
		forEach(cell -> cell.reset());
		
		cells = null;
		cellPanel = null;
		
		xside = 9;
		yside = 9;
		MINES = 10;
		
		MinesweeperX.spaces = 71;
		
		brdframe = null;
		MinesweeperX.updateDifficulty(xside, yside, MINES, Mode.EASY);
	}
	
	public void setMed() throws ClassNotFoundException, IOException {
		MinesweeperX.resetamount();
		foundmines = 0;
		status = Status.NONE;
		if (MinesweeperX.getSound)
			try { snd.removeClip(); } catch (NullPointerException xnoValue) {}
		
		stopTimer();
		brdframe.setVisible(false);
		
		forEach(cell -> cell.reset());
		
		cells = null;
		cellPanel = null;
		
		xside = 16;
		yside = 16;
		MINES = 40;
		
		MinesweeperX.spaces = 216;
		
		brdframe = null;
		MinesweeperX.updateDifficulty(xside, yside, MINES, Mode.MEDIUM);
	}
	
	public void setHard() throws ClassNotFoundException, IOException {
		MinesweeperX.resetamount();
		foundmines = 0;
		status = Status.NONE;
		if (MinesweeperX.getSound)
			try { snd.removeClip(); } catch (NullPointerException xnoValue) {}
		
		stopTimer();
		brdframe.setVisible(false);
		
		forEach(cell -> cell.reset());
		
		cells = null;
		cellPanel = null;
		
		xside = 16;
		yside = 30;
		MINES = 99;
		
		MinesweeperX.spaces = 471;
		
		brdframe = null;
		MinesweeperX.updateDifficulty(xside, yside, MINES, Mode.HARD);
	}
	
	/**Customize some boards
	 * 
	 * @param width
	 * @param height
	 * @param mines
	 * @throws CannotProceedException
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public void setCustom(int width, int height, int mines) throws CannotProceedException, ClassNotFoundException, IOException {
		foundmines = 0;
		status = Status.NONE;
		// Check to free up some space.
		System.out.println("HxW: " + width + "x" + height + "; Request for " + mines + " Mines; cell^2: " + (width * height));
		int calculator = (width * height);
		if (calculator <= mines - 30) {
			throw new CannotProceedException("Can't generate up to " + mines + " mines in " + height + "x" + width + ".");
		}
		
		// Check to make with using small or large board
		if (width < 5) {throw new CannotProceedException("Can't generate under 3 blocks height.");}
		if (height < 5) {throw new CannotProceedException("Can't generate under 8 blocks wide.");}
		
		if (width > 24) {throw new CannotProceedException("Can't generate over 24 blocks height.");}
		if (height > 30) {throw new CannotProceedException("Can't generate over 30 blocks wide.");}
		
		// Now, customize some boards
		MinesweeperX.resetamount();
		if (MinesweeperX.getSound)
			try { snd.removeClip(); } catch (NullPointerException xnoValue) {}
		
		stopTimer();
		brdframe.setVisible(false);
		
		forEach(cell -> cell.reset());
		
		cells = null;
		cellPanel = null;
		
		xside = width;
		yside = height;
		MINES = mines;
		
		MinesweeperX.spaces = ((width * height) - mines);
		
		brdframe = null;
		MinesweeperX.updateDifficulty(xside, yside, MINES, Mode.CUSTOM);
	}
	
	void reveal(Color color) {
		forEach(cell -> cell.reveal(color));
	}
}
