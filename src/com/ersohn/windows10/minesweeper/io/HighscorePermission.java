package com.ersohn.windows10.minesweeper.io;

import java.security.BasicPermission;

public class HighscorePermission extends BasicPermission {
	private static final long serialVersionUID = 1L;

	public HighscorePermission(String name) {
		super(name);
	}
}
