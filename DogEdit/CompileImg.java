import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.util.*;

public class CompileImg extends JPanel {
	public void paintComponent(Graphics g) {
		Image img = new ImageIcon("compile.png").getImage();
		g.drawImage(img, 2, 2, this);
	}
}