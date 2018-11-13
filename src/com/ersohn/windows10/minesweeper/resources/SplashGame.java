/*
 * SplashScreen.java
 *
 * Copyright (c) 2003-2017 Sunnyball authors
 * See authors.txt for details.
 *
 * Sunnyball is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 */

package com.ersohn.windows10.minesweeper.resources;

import java.awt.*;
import java.awt.event.*;

import com.ersohn.windows10.minesweeper.general.ProgramConstants;

public class SplashGame extends Frame implements ActionListener {
  static void renderSplashFrame(Graphics2D g, int frame) {
    g.setComposite(AlphaComposite.Clear);
    g.setPaintMode();
    g.setColor(Color.BLACK);
    g.drawString("Loading " + ProgramConstants.TITLE, 120, 150);
  }
  
  public SplashGame() throws NullPointerException {
    super("SplashScreen game");
    setSize(640, 400);
    setLayout(new BorderLayout());
    final SplashScreen splash = SplashScreen.getSplashScreen();
    
    if (splash == null) {
      //System.out.println("SplashScreen.getSplashScreen() returned null");
      throw new NullPointerException("SplashScreen.getSplashScreen() returned null"); // Use throwage instead return
    }
    Graphics2D g = splash.createGraphics();
    if (g == null) {
      //System.out.println("g is null");
      throw new NullPointerException("g is null"); // Use throwage instead return
    }
    
    renderSplashFrame(g, 0);
    splash.update();
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    splash.close();
  }
  
  @Override
  public void actionPerformed(ActionEvent e) {
    // TODO Auto-generated method stub
    
  }
}
