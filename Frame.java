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

public class Frame extends JPanel implements ActionListener, KeyListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1177336518284090270L;
	//// Utilities /////////////////////////////
	private Scanner kb = new Scanner(System.in);
	private GameEngine ge = new GameEngine();
	private Timer tm = new Timer(15, this);

	//// Others ////////////////////////////////
	private boolean pause = false;
	private char pendingDir = ' ';
	private char movingDir = ' ';
	private Bullet bullet = null;
	private BufferedImage gui = null;
	
	//// Counters //////////////////////////////
	private int playerCounter = 0;
	private int enemyCounter = 64;

	public Frame() {
		addKeyListener(this);
		setFocusable(true);
		try {
			gui = ImageIO.read(new File("res/ui.png"));
		} catch (IOException e) {
		}
	}

	public void paintMap(Graphics g) {
		g.drawImage(gui, 0, 0, null);
		/**
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
		*/
	}

	public void paintObjects(Graphics g) {
		BufferedImage sprite = null;
		for (int y = 0; y < 9; y++) {
			for (int x = 0; x < 9; x++) {
				int xPos = 64 * x + 9;
				int yPos = 64 * y + 49;
				if (ge.getType(y, x) == 'P') {
					g.setColor(Color.BLACK);
					String file = "res/dragon_";
					int frame;
					if (movingDir == 'u')
						yPos -= playerCounter;
					if (movingDir == 'd')
						yPos += playerCounter;
					if (movingDir == 'l')
						xPos -= playerCounter;
					if (movingDir == 'r')
						xPos += playerCounter;
					file += (ge.getPlayer().getDirection() + "_");
					frame = (playerCounter / 32) % 2;
					try {
						sprite = ImageIO.read(new File(file + frame + ".png"));
					} catch (IOException e) {
					}
				}
				if (ge.getType(y, x) == 'E') {
					char direction = ge.getDirection(y, x);
					if (direction == 'u')
						yPos += enemyCounter;
					if (direction == 'd')
						yPos -= enemyCounter;
					if (direction == 'l')
						xPos += enemyCounter;
					if (direction == 'r')
						xPos -= enemyCounter;
					try {
						String file = "res/knight_walk_";
						int frame;
						switch (direction) {
						case 'u':
							file += "back_";
							break;
						case 'd':
							file += "front_";
							break;
						case 'l':
							file += "left_";
							break;
						case 'r':
							file += "right_";
							break;
						}
						frame = (playerCounter / 16) % 4;
						if (frame == 3)
							frame = 1;
						sprite = ImageIO.read(new File(file + frame + ".png"));
					} catch (IOException e) {
					}
				}
				if (ge.getType(y, x) == 'B') {
					g.setColor(Color.BLUE);
					g.fillRect(xPos, yPos, 64, 64);
				}
				// Sprite for Briefcase ///////////////////////////////////////////////////////////////////////////////
				if (ge.getType(y, x) == 'A')
					try {
						String file = "res/Gem";
						int frame = (playerCounter / 32) % 2;
						sprite = ImageIO.read(new File(file + frame + ".png"));
					} catch (IOException e) {
					}
				if (ge.getType(y, x) == 'L')
					try {
						String file = "res/Scroll";
						int frame = (playerCounter / 32) % 2;
						sprite = ImageIO.read(new File(file + frame + ".png"));
					} catch (IOException e) {
					}
				if (ge.getType(y, x) == 'S')
					try {
						String file = "res/heart";
						int frame = (playerCounter / 16) % 4;
						if (frame == 3)
							frame = 1;
						sprite = ImageIO.read(new File(file + frame + ".png"));
					} catch (IOException e) {
					}
				if (sprite != null) {
					g.drawImage(sprite, xPos, yPos, null);
					sprite = null;
				}
			}
		}
	}

	public void paintBullet(Graphics g) {
		if (bullet != null) {
			boolean hit = false;
			// g.setColor(Color.RED);
			int x = bullet.getX();
			int y = bullet.getY();
			if (bullet.getDir() == 's')
				hit = true;
			if (x / 16 == 1 && bullet.getDir() == 'l')
				hit = true;
			if (x / 16 == 33 && bullet.getDir() == 'r')
				hit = true;
			if (y / 16 == 1 && bullet.getDir() == 'u')
				hit = true;
			if (y / 16 == 33 && bullet.getDir() == 'd')
				hit = true;
			try {
				BufferedImage sprite = ImageIO.read(new File("res/Fireball1.png"));
				g.drawImage(sprite, x, y, null);
			} catch (IOException e) {
			}
			// Things to do:
			// Collisions for Rooms
			if ((x - 20) / 64 == 1 || (x - 20) / 64 == 4 || (x - 20) / 64 == 7) {
				if ((y - 20) / 64 == 1 || (y - 20) / 64 == 4 || (y - 20) / 64 == 7) {
					hit = true;
				}
			}

			// Collisions for Enemies

			if (hit) {
				ge.shoot(bullet.getDir());
				bullet = null;
			}
		}
	}

	private class Bullet {
		int x, y;
		int velX = 0, velY = 0;
		char dir;

		Bullet(int x, int y, char direction) {
			this.x = x;
			this.y = y;
			dir = direction;
			if (direction == 'u')
				velY -= 16;
			if (direction == 'd')
				velY += 16;
			if (direction == 'l')
				velX -= 16;
			if (direction == 'r')
				velX += 16;
			this.x += (velX * 4);
			this.y += (velY * 4);
		}

		int getX() {
			return x += velX;
		}

		int getY() {
			return y += velY;
		}

		char getDir() {
			return dir;
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		paintMap(g);
		paintBullet(g);
		paintObjects(g);

		tm.start();
	}

	public void reset() {
		pendingDir = ' ';
		movingDir = ' ';
		playerCounter = 0;
		enemyCounter = 64;
	}

	public void actionPerformed(ActionEvent e) {
		if (!pause) {
			// Move Players
			if (playerCounter == 0) {
				if (ge.checkMove(pendingDir) != 's') {
					movingDir = pendingDir;
				} else {
					movingDir = 's';
				}
				ge.getPlayer().setDirection(movingDir);
			}
			playerCounter++;
			if (playerCounter == 64)
				playerCounter = 0;

			// Move Enemies
			if (enemyCounter == 0)
				enemyCounter = 64;
			if (enemyCounter == 64) {
				ge.moveEnemies();
				if (gameOver())
					return;
			}
			if (playerCounter == 0) {
				ge.movePlayer(movingDir);
				// printBoard();
				if (nextLevel())
					return;
			}

			repaint();
			// kb.nextLine();
			enemyCounter--;
			// System.out.println("Counter: " + (playerCounter / 32) % 2);
			///////////////////////////////////////////// TEST
			///////////////////////////////////////////// COUNTER
			///////////////////////////////////////////// PRINT
			// System.out.println("Direct : " + ge.getPlayer().getDirection());

			if (ge.checkWin())
				ge.generateBoard(ge.getPlayer().getLives());
		}
	}

	private void printBoard() {
		System.out.println("\n===========================");
		for (int i = 0; i < 9; ++i) {
			for (int j = 0; j < 9; ++j) {
				if (!ge.checkExists(i, j))
					System.out.print("[ ]");
				else if (ge.isVisible(i, j))
					System.out.print("[" + ge.getType(i, j) + "]");
				else
					System.out.print("[ ]");
			}
			System.out.println("");
		}
		System.out.println("===========================");
		System.out.println("Lives: " + ge.getLives() + "   Bullets: " + ge.getBullets());
		System.out.println("Level: " + ge.getLevel() + "   Enemies: " + ge.getEnemies());
		System.out.println("===========================");
	}

	public boolean nextLevel() {
		if (ge.checkWin()) {
			System.out.println("You have found the exit!");
			System.out.println("[Press ENTER to continue]");
			kb.nextLine();

			int lives = ge.getLives();
			ge.levelReset();
			ge.increaseLevel();
			ge.generateBoard(lives);
			reset();
			return true;
		} else
			return false;
	}

	public boolean gameOver() {
		if (ge.getLives() == 0) {
			System.out.println("You have lost.");
			System.out.println("[Press ENTER to try again]");
			kb.nextLine();
			ge.gameReset();
			ge.generateBoard();
			reset();
			return true;
		} else
			return false;
	}

	public void keyTyped(KeyEvent e) {
		switch (e.getKeyChar()) {
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
		case 'p':
			if (pause)
				pause = false;
			else
				pause = true;
			break;
		case ' ':
			if (bullet == null) {
				Player player = ge.getPlayer();
				int x = 64 * player.getColumn() + 20;
				int y = 64 * player.getRow() + 20;
				char dir = player.getDirection();
				if (dir == 'u')
					y -= playerCounter;
				if (dir == 'd')
					y += playerCounter;
				if (dir == 'l')
					x -= playerCounter;
				if (dir == 'r')
					x += playerCounter;
				bullet = new Bullet(x, y, dir);
			}
			break;
		}
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
	}
}
