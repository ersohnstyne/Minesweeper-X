package com.ersohn.windows10.minesweeper.io;

import java.io.*;

public class HighscoreEntry implements Serializable
{
	private static final long serialVersionUID = 2L;
	
	private int _dateTime;
	
	private String _name;
	
	/**
	 * Instantiates a new highscore entry.
	 *
	 * @param datetime the score
	 * @param name the nickname of the player
	 */
	public HighscoreEntry(int dateTime, String name)
	{
		setDateTime(dateTime);
		setName(name);
	}
	
	/**
	 * Sets the name.
	 *
	 * @param _name the new name
	 */
	public void setName(String _name)
	{
		this._name = _name;
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName()
	{
		switch(this._name.length()) {
		case 0: return "   ";
		case 1: return this._name + "  ";
		case 2: return this._name + " ";
		default: return this._name;
		}
	}
	
	/**
	 * Sets the score.
	 *
	 * @param _dateTime the new score
	 */
	public void setDateTime(int dateTime)
	{
		this._dateTime = dateTime;
	}
	
	/**
	 * Gets the date time.
	 *
	 * @return the date time
	 */
	public int getDateTime()
	{
		return this._dateTime;
	}
	
	/**
	 * Depending on which entry is higher/lower than the other
	 *
	 * @param h the other highscore entry
	 * @return -1 / 0 / 1, depending on which entry is higher/lower than the other
	 */
	public int compareTo(HighscoreEntry h)
	{
		return new Integer(this._dateTime).compareTo(h.getDateTime());
	}
	
}