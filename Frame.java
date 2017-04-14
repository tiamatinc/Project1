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
	int playerCounter = 63;
	int enemyCounter = 63;
	Timer tm = new Timer(15, this);
	
	public Frame() {
		addKeyListener(this);
		setFocusable(true);
	}
	
	public void paintMap(Graphics g) {
		g.setColor(Color.GREEN);
		g.fillRect(20, 20, 576, 576);
	}
	
	public void paintObjects(Graphics g) {
		for(int y = 0; y < 9; y++) {
			for(int x = 0; x < 9; x++) {
				int xPos = 64*x + 20;
				int yPos = 64*y + 20;
				if(ge.getType(y, x) == 'P') {
					g.setColor(Color.PINK);
					char direction = ge.getDirection(y, x);
					if(direction == 'u') yPos += playerCounter;
					if(direction == 'd') yPos -= playerCounter;
					if(direction == 'l') xPos += playerCounter;
					if(direction == 'r') xPos -= playerCounter;
				}
				if(ge.getType(y, x) == 'E') {
					g.setColor(Color.BLACK);
					char direction = ge.getDirection(y, x);
					if(direction == 'u') yPos += enemyCounter;
					if(direction == 'd') yPos -= enemyCounter;
					if(direction == 'l') xPos += enemyCounter;
					if(direction == 'r') xPos -= enemyCounter;
					System.out.println(x + ", " + y);
				}
				if(ge.getType(y, x) == 'R') g.setColor(Color.YELLOW);
				if(ge.getType(y, x) == 'A') g.setColor(Color.RED);
				if(ge.getType(y, x) == 'L') g.setColor(Color.ORANGE);
				if(ge.getType(y, x) == 'S') g.setColor(Color.GRAY);
				if(ge.getType(y, x) != ' ') g.fillRect(xPos, yPos, 64, 64);
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
	
	public static void main(String[] args)
	{
		Frame panel = new Frame();
		JFrame jf = new JFrame();
		jf.setTitle("Testing");
		jf.setSize(900, 700);
		jf.setResizable(false);
		jf.setVisible(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.add(panel);		
	}

	public void actionPerformed(ActionEvent e) 
	{
		// Move Players
		
		// Move Enemies
		if(enemyCounter == 0) enemyCounter = 63;
		if(enemyCounter == 63) {
			ge.moveEnemies();
		}
		
		repaint();
		//kb.nextLine();
		if(playerCounter != 0) playerCounter--;
		enemyCounter--;
	}

	public void keyTyped(KeyEvent e) {		
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
	}
}
