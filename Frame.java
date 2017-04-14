package project1;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Frame extends JPanel implements ActionListener, KeyListener
{
	Scanner kb = new Scanner(System.in);
	GameEngine ge = new GameEngine();
	Random rng = new Random();
	char pendingDir = ' ';
	char movingDir = ' ';
	int playerCounter = 0;
	int enemyCounter = 63;
	Timer tm = new Timer(15, this);
	
	public Frame() {
		addKeyListener(this);
		setFocusable(true);
	}
	
	public void paintMap(Graphics g) {
		g.setColor(Color.GREEN);
		g.fillRect(20, 20, 576, 576);
		g.setColor(Color.YELLOW);
		g.fillRect(84, 84, 64, 64);
		g.fillRect(276, 84, 64, 64);
		g.fillRect(468, 84, 64, 64);
		g.fillRect(84, 276, 64, 64);
		g.fillRect(276, 276, 64, 64);
		g.fillRect(468, 276, 64, 64);
		g.fillRect(84, 468, 64, 64);
		g.fillRect(276, 468, 64, 64);
		g.fillRect(468, 468, 64, 64);
	}
	
	public void paintObjects(Graphics g) {
		for(int y = 0; y < 9; y++) {
			for(int x = 0; x < 9; x++) {
				int xPos = 64*x + 20;
				int yPos = 64*y + 20;
				if(ge.getType(y, x) == 'P') {
					g.setColor(Color.PINK);
					if(movingDir == 'u') yPos -= playerCounter;
					if(movingDir == 'd') yPos += playerCounter;
					if(movingDir == 'l') xPos -= playerCounter;
					if(movingDir == 'r') xPos += playerCounter;
					
				}
				if(ge.getType(y, x) == 'E') {
					g.setColor(Color.BLACK);
					char direction = ge.getDirection(y, x);
					if(direction == 'u') yPos += enemyCounter;
					if(direction == 'd') yPos -= enemyCounter;
					if(direction == 'l') xPos += enemyCounter;
					if(direction == 'r') xPos -= enemyCounter;
					//System.out.println(x + ", " + y);
				}
				if(ge.getType(y, x) == 'B') g.setColor(Color.BLUE);
				if(ge.getType(y, x) == 'A') g.setColor(Color.RED);
				if(ge.getType(y, x) == 'L') g.setColor(Color.ORANGE);
				if(ge.getType(y, x) == 'S') g.setColor(Color.GRAY);
				if(ge.getType(y, x) != ' ' && ge.getType(y, x) != 'R') g.fillRect(xPos, yPos, 64, 64);
			}
		}
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		paintMap(g);
		paintObjects(g);
		
		tm.start();
	}
	
	public void reset() {
		pendingDir = ' ';
		movingDir = ' ';
		playerCounter = 0;
		enemyCounter = 63;
	}

	public void actionPerformed(ActionEvent e) 
	{
		// Move Players
		if(playerCounter == 0) {
			if(ge.checkMove(pendingDir) != 's') {
				movingDir = pendingDir;
			}
			else {
				movingDir = 's';
			}
			ge.getPlayer().setDirection(movingDir);
		} 
		playerCounter++;
		if(playerCounter == 63) playerCounter = 0;
		if(playerCounter == 0) {
			ge.movePlayer(movingDir);
			if(nextLevel()) return;
		}
		
		// Move Enemies
		if(enemyCounter == 0) enemyCounter = 63;
		if(enemyCounter == 63) {
			ge.moveEnemies();
			if(gameOver()) return;
		}
		
		repaint();
		//kb.nextLine();
		enemyCounter--;
		System.out.println("Counter: " + playerCounter);
		System.out.println("Direct : " + ge.getPlayer().getDirection());
		if(ge.checkWin()) ge.generateBoard(ge.getPlayer().getLives());
	}
	
	public boolean nextLevel() {
		if(ge.checkWin()) {
			System.out.println("You have found the exit!");
			System.out.println("[Press ENTER to continue]");
			kb.nextLine();
			
			int lives = ge.getLives();
			ge.levelReset();
			ge.increaseLevel();
			ge.generateBoard(lives);
			reset();
			return true;
		}
		else return false;
	}

	public boolean gameOver() {
		if(ge.getLives() == 0) {
			System.out.println("You have lost.");
			System.out.println("[Press ENTER to try again]");
			kb.nextLine();
			ge.gameReset();
			ge.generateBoard();
			reset();
			return true;
		}
		else return false;
	}
	
	public void keyTyped(KeyEvent e) {
		switch(e.getKeyChar()) {
		case 'w':
			pendingDir = 'u';
			break;
		case 'a':
			pendingDir = 'l';
			break;
		case 's':
			pendingDir = 'd';
			break;
		case 'd':
			pendingDir = 'r';
			break;
		}
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
	}
}
