package project1;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Frame extends JPanel implements ActionListener, KeyListener {
	private static final long serialVersionUID = -1177336518284090270L;
	//// Utilities /////////////////////////////
	private GameEngine ge = new GameEngine();
	private Timer tm = new Timer(15, this);

	//// Others ////////////////////////////////
	private boolean pause = false;
	private char pendingDir = 's';
	private char movingDir = ' ';
	private char animDir = 'u';
	private Bullet bullet = null;
	private BufferedImage gui = null;
	private BufferedImage iconUI = null;
	private boolean found1 = false;
	private boolean found2 = false;
	private int infoBox = -1;

	//// Counters //////////////////////////////
	private int playerCounter = 0;
	private int enemyCounter = 64;

	/**
	 * Constructor for the Frame class - Frame extends the JFrame class and implements
	 * an ActionListener. The Frame is what is control of the GUI portion of the game.
	 */
	public Frame() {
		addKeyListener(this);
		setFocusable(true);
		try {
			gui = ImageIO.read(new File("res/ui.png"));
		} catch (IOException e) {
		}
	}

	//// Painting Methods //////////////////////
	
	/**
	 * @param g
	 * This method paints the objects on the GameBoard.
	 */
	private void paintObjects(Graphics g) {
		BufferedImage sprite = null;
		for (int y = 0; y < 9; y++) {
			for (int x = 0; x < 9; x++) {
				int xPos = 64 * x + 9;
				int yPos = 64 * y + 49;
				if (ge.getType(y, x) == 'P') {
					g.setColor(Color.BLACK);
					String file = "res/dragon_";
					int frame;
					if (ge.getPlayer().getDirection() == 'u')
						yPos -= playerCounter;
					if (ge.getPlayer().getDirection() == 'd')
						yPos += playerCounter;
					if (ge.getPlayer().getDirection() == 'l')
						xPos -= playerCounter;
					if (ge.getPlayer().getDirection() == 'r')
						xPos += playerCounter;
					file += (animDir + "_");
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
					try {
						String file = "res/exit.png";
						if ((playerCounter / 32) % 2 == 1) {
							sprite = ImageIO.read(new File(file));
						}
					} catch (IOException e) { 
					}
				}
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

	/**
	 * @param g
	 * If a bullet is being fired [and thus the variable bullet is not null],
	 * then this method will print the bullet [or in this case, fireball].
	 */
	private void paintBullet(Graphics g) {
		if (bullet != null) {
			boolean hit = false;
			int x = bullet.getX();
			int y = bullet.getY();
			if (bullet.getDir() == 's')
				hit = true;
			if (x / 16 < 1 && bullet.getDir() == 'l')
				hit = true;
			if (x / 16 >= 33 && bullet.getDir() == 'r')
				hit = true;
			if (y / 16 < 3 && bullet.getDir() == 'u')
				hit = true;
			if (y / 16 >= 34 && bullet.getDir() == 'd')
				hit = true;
			try {
				BufferedImage sprite = ImageIO.read(new File("res/Fireball1.png"));
				g.drawImage(sprite, x, y, null);
			} catch (IOException e) {
			}
			// Things to do:
			// Collisions for Rooms
			if ((x - 9) / 64 == 1 || (x - 9) / 64 == 4 || (x - 9) / 64 == 7) {
				if ((y - 49) / 64 == 1 || (y - 49) / 64 == 4 || (y - 49) / 64 == 7) {
					hit = true;
				}
			}

			// Collisions for Enemies
			if (!hit) {
				if (ge.getType((y - 49) / 64, (x - 9) / 64) == 'E')
					hit = true;
			}
			if (hit) {
				ge.shoot(bullet.getDir());
				bullet = null;
			}
		}
	}

	/**
	 * Private class used to keep track of the fireballs x and y position
	 * on the gui.
	 */
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

	/**
	 * @param g
	 * Method in charge of printing the UI portion of the Frame that indicates
	 * the status of the player, level, ammo count, and enemy count.
	 */
	private void paintUI(Graphics g) {
		// Status
		g.setFont(new Font("Eras ITC", Font.BOLD, 28));
		g.drawString("Level: " + ge.getLevel(), 700, 60);
		g.drawString("Lives: " + ge.getLives(), 700, 90);
		g.drawString("Fireballs: " + ge.getBullets(), 700, 120);
		g.drawString("Enemies: " + ge.getEnemies(), 700, 150);
		g.setFont(new Font("Eras ITC", Font.BOLD, 18));
		// Found Icons
		if (!ge.scanFor("Ammo")) {
			try {
				iconUI = ImageIO.read(new File("res/Gem0.png"));
				g.drawImage(iconUI, 650, 460, null);
				if (!found1) {
					found1 = true;
					ge.getPlayer().setBullets(ge.getBullets() + 1);
				}
			} catch (IOException e) {
			}
		}

		if (!ge.scanFor("Locator")) {
			try {
				iconUI = ImageIO.read(new File("res/Scroll0.png"));
				g.drawImage(iconUI, 780, 460, null);
			} catch (IOException e) {
			}
		}

		if (!ge.scanFor("Shield")) {
			try {
				iconUI = ImageIO.read(new File("res/heart1.png"));
				g.drawImage(iconUI, 910, 460, null);
				if (!found2) {
					ge.getPlayer().setLives(ge.getLives() + 1);
					found2 = true;
				}
			} catch (IOException e) {
			}
		}
		// Info box
		g.drawString(getInfo(infoBox), 645, 638);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.drawImage(gui, 0, 0, null);
		paintBullet(g);
		paintObjects(g);
		paintUI(g);

		tm.start();
	}

	//// Game Functions /////////////////////////////////
	
	/**
	 * Resets all variables; used the board needs to be regenerated for
	 * game over or next level.
	 */
	private void reset() {
		pendingDir = ' ';
		movingDir = ' ';
		playerCounter = 0;
		enemyCounter = 64;
		found1 = false;
		found2 = false;
	}

	public void actionPerformed(ActionEvent e) {
		if (!pause) {
			// Move Players
			if (playerCounter == 0) {
				if (ge.checkMove(pendingDir) != 's') {
					movingDir = pendingDir;
					animDir = pendingDir;
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
				if(!ge.moveEnemies()) {
					infoBox = 1;
					pause = true;
				}
				else infoBox = -1;
				if (gameOver())
					return;
			}
			if (playerCounter == 0) {
				ge.movePlayer(movingDir);
				if (nextLevel())
					return;
			}

			repaint();
			enemyCounter--;

			if (ge.checkWin())
				ge.generateBoard(ge.getPlayer().getLives());
		}
	}

	/**
	 * Method to regenerate the board if the exit has been found.
	 */
	private boolean nextLevel() {
		if (ge.checkWin()) {
			infoBox = 0;
			repaint();
			pause = true;
			int lives = ge.getLives();
			ge.levelReset();
			ge.increaseLevel();
			ge.generateBoard(lives);
			reset();
			return true;
		} else
			return false;
	}

	/**
	 * Method to regenerate the board if all lives have been lost.
	 */
	private boolean gameOver() {
		if (ge.getLives() == 0) {
			infoBox = 2;
			repaint();
			pause = true;
			ge.gameReset();
			ge.generateBoard();
			reset();
			return true;
		} else
			return false;
	}

	/**
	 * @param code
	 * @return
	 * Method returns the appropriate message based on whatever int is
	 * passed upon call.
	 */
	private String getInfo(int code) {
		String info = null;
		switch (code) {
		case 0:
			info = "You found the exit! [Press ENTER]"; // Completed Level
			break;
		case 1:
			info = "The knights found you! [Press ENTER]"; // Life is Lost
			break;
		case 2:
			info = "You have been slain... [Press ENTER]";// Game Over
			break;
		case 3:
			info = "[GAME PAUSED]"; // Game Paused
			break;
		case 4:
			info = "Game has been saved.";
			break;
		case 5:
			info = "Game save cancelled.";
			break;
		case 6:
			info = "There was an error saving.. Try again.";
			break;
		case 7:
			info = "File has been loaded.";
			break;
		case 8:
			info = "Cannot load invalid file!";
			break;
		case 9:
			info = "There was an error loading.. Try again.";
			break;
		default:
			info = "";
			break;
		}
		return info;
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
			if (pause) {
				pause = false;
				infoBox = -1;
			} else {
				pause = true;
				infoBox = 3;
			}
			repaint();
			break;
		case ' ':
			if (ge.getBullets() > 0) {
				if (bullet == null) {
					Player player = ge.getPlayer();
					int x = 64 * player.getColumn() + 9;
					int y = 64 * player.getRow() + 49;
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
					ge.getPlayer().setBullets(ge.getBullets() - 1);
				}
			}
			break;
		case '\n':
			pause = false;
			infoBox = -1;
			break;
		case ';':
			if (pause) {
				JFileChooser pickSave = new JFileChooser();
				int status = pickSave.showSaveDialog(null);
				if (status == JFileChooser.APPROVE_OPTION) {
					String result = ge.saveFile(pickSave.getSelectedFile(), this);
					if(result.equalsIgnoreCase("Your game is corrupted and could not be saved...")) {
						infoBox = 6;
					}
					infoBox = 4;
				}
				if (status == JFileChooser.CANCEL_OPTION) {
					infoBox = 5;
				}
			}
			break;
		case 'l':
			if (pause) {
				JFileChooser pickSave = new JFileChooser();
				int status = pickSave.showOpenDialog(null);
				if(status == JFileChooser.APPROVE_OPTION) {
					try {
						FileInputStream fis = new FileInputStream(pickSave.getSelectedFile());
						ObjectInputStream ois = new ObjectInputStream(fis);
						Data ld = (Data) ois.readObject();
						this.pendingDir = ld.getPendingDir();
						this.movingDir = ld.getMovingDir();
						this.animDir = ld.getAnimDir();
						this.found1 = ld.isFound1();
						this.found2 = ld.isFound2();
						this.infoBox = ld.getInfoBox();
						this.playerCounter = ld.getPlayerCounter();
						this.enemyCounter = ld.getEnemyCounter();
						
						ge.loadFile(pickSave.getSelectedFile());
						repaint();
						fis.close();
					} catch (IOException ex) {
						infoBox = 8;
					} catch (ClassNotFoundException ex) {
						infoBox = 9;
					}
				}
			}
			break;
		}
	}

	//// Empty Methods ////////////////////////////////////////////
	
	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
	}
	
	//// Getters //////////////////////////////////////////////////
	
	public char getPendingDir() {
		return pendingDir;
	}

	public char getMovingDir() {
		return movingDir;
	}

	public char getAnimDir() {
		return animDir;
	}

	public boolean isFound1() {
		return found1;
	}

	public boolean isFound2() {
		return found2;
	}

	public int getInfoBox() {
		return infoBox;
	}

	public int getPlayerCounter() {
		return playerCounter;
	}

	public int getEnemyCounter() {
		return enemyCounter;
	}

}
