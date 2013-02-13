package bard.util.ui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

import javax.swing.JTextArea;
import javax.swing.SwingWorker;

public class SwingUtils {
	
	public static JTextArea console() throws IOException {
		// 1. create the pipes
	    PipedInputStream inPipe = new PipedInputStream();
	    PipedInputStream outPipe = new PipedInputStream();
	    // 2. set the System.in and System.out streams
	    System.setIn(inPipe);
	    System.setOut(new PrintStream(new PipedOutputStream(outPipe), true));
	    PrintWriter inWriter = new PrintWriter(new PipedOutputStream(inPipe), true);
	    return console(outPipe, inWriter);
	}

	public static JTextArea console(final InputStream out, final PrintWriter in) {
		final JTextArea area = new JTextArea();

		// handle "System.out"
		new SwingWorker<Void, String>() {
			@Override
			protected Void doInBackground() throws Exception {
				Scanner s = new Scanner(out);
				while (s.hasNextLine())
					publish(s.nextLine() + "\n");
				return null;
			}

			@Override
			protected void process(List<String> chunks) {
				for (String line : chunks)
					area.append(line);
			}
		}.execute();

		// handle "System.in"
		area.addKeyListener(new KeyAdapter() {
			private StringBuffer line = new StringBuffer();

			@Override
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if (c == KeyEvent.VK_ENTER) {
					in.println(line);
					line.setLength(0);
				} else if (c == KeyEvent.VK_BACK_SPACE) {
					line.setLength(line.length() - 1);
				} else if (!Character.isISOControl(c)) {
					line.append(e.getKeyChar());
				}
			}
		});

		return area;
	}
}
