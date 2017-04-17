package project1;

import javax.swing.JFrame;

public class Main {
	
	public static void main(String[] args)
	{
		Frame panel = new Frame();
		JFrame jf = new JFrame();
		jf.setTitle("Testing");
		jf.setSize(1000, 676);
		jf.setResizable(false);
		jf.setVisible(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.add(panel);		
	}
}
