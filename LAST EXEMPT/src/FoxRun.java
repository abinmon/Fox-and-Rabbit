import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.Timer;

public class FoxRun implements ActionListener {
	private static final int DEFAULT_SPEED = 300;
	private JFrame frame;
	private JButton step;
	private JButton quit;
	private JButton start;
	private JButton pause;
	private JButton reset;
	private JButton speed;
	private JButton total;
	private JButton popDet;
	private JButton del;
	private JButton ok;
	private JRadioButton saveTotal;
	private JRadioButton savePop;
	private JComboBox y;
	private boolean running;
	private Simulator s = new Simulator(100, 100);
	private Timer timer;
	private int speedCounter = 1;
	private int currentSpeed;
	private boolean started = false;

	public FoxRun() {
		this.makeFrame();
	}

	private void makeFrame() {

		frame = new JFrame("Simulation Controls");
		Container contentPane = frame.getContentPane();

		ImageIcon a = new ImageIcon("LAST EXEMPT/images/3671866-20.png");
		step = new JButton("Step", a);
		step.addActionListener(this);

		ImageIcon q = new ImageIcon("LAST EXEMPT/images/iconfinder_cancel_3855592.png");
		quit = new JButton("Quit", q);
		quit.addActionListener(this);

		ImageIcon p = new ImageIcon("LAST EXEMPT/images/iconfinder_pause_3855607.png");
		pause = new JButton("Pause", p);
		pause.addActionListener(this);

		ImageIcon r = new ImageIcon(
				"LAST EXEMPT/images/iconfinder_play_button_3855622.png");
		start = new JButton("Start", r);
		start.addActionListener(this);

		ImageIcon g = new ImageIcon("LAST EXEMPT/images/3830966-20.png");
		reset = new JButton("Reset", g);
		reset.addActionListener(this);

		ImageIcon h = new ImageIcon("LAST EXEMPT/images/106219-20.png");
		speed = new JButton("Speed", h);
		speed.addActionListener(this);

		ImageIcon l = new ImageIcon("LAST EXEMPT/images/3017947-20.png");
		total = new JButton("Total", l);
		total.addActionListener(this);

		ImageIcon u = new ImageIcon("LAST EXEMPT/images/3336950-20.png");
		popDet = new JButton("Current Details", u);
		popDet.addActionListener(this);

		ImageIcon w = new ImageIcon("LAST EXEMPT/images/2916078-20.png");
		del = new JButton("Delete", w);
		del.addActionListener(this);

		ok = new JButton("OK");

		ok.setOpaque(true);
		ok.addActionListener(this);

		savePop = new JRadioButton("Population");
		saveTotal = new JRadioButton("Total");

		ButtonGroup group = new ButtonGroup();
		group.add(savePop);
		group.add(saveTotal);
		JLabel save = new JLabel(" Save : ");
		JLabel blank = new JLabel(" ");

		JLabel blank1 = new JLabel("");

		String[] intnum = { "10", "50", "100", "500" };
		y = new JComboBox(intnum);
		y.addActionListener(this);
		JLabel label = new JLabel("Step By");

		JPanel p1 = new JPanel(new GridLayout(2, 4));

		JPanel p2 = new JPanel(new GridLayout(3, 3));
		p1.add(blank);
		p1.add(label);
		p1.add(y);

		p1.add(blank1);
		p1.add(save);
		p1.add(savePop);
		p1.add(saveTotal);

		p1.add(ok);
		p2.add(start);
		p2.add(pause);
		p2.add(quit);
		p2.add(step);
		p2.add(speed);
		p2.add(reset);
		p2.add(del);
		p2.add(total);
		p2.add(popDet);

		contentPane.add(p2, BorderLayout.EAST);
		contentPane.add(p1, BorderLayout.SOUTH);

		frame.pack();
		frame.setLocation(700, 200);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		timer = new Timer(100, this);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == start) {
			speedCounter = 0;
			timer.setDelay(100);
			timer.start();
			s.setTotal();
			started = true;

		} else if (e.getSource() == pause) {
			speedCounter = 0;
			running = false;
			timer.stop();
			started = false;

		} else if (e.getSource() == quit) {
			System.exit(0);
		} else if (e.getSource() == step) {
			speedCounter = 0;
			timer.stop();
			s.simulateOneStep();
			started = false;

		} else if (e.getSource() == timer) {

			s.simulateOneStep();
		} else if (e.getSource() == reset) {
			speedCounter = 0;
			s.setTotal();
			s.reset();
			s.getSimTotal();
			started = false;
		} else if (e.getSource() == speed) {
			if(started == true) {
			speedCounter = speedCounter + 5;

			currentSpeed = DEFAULT_SPEED / speedCounter;
			if (speedCounter >= 25) {
				JFrame r = new JFrame("SPEED!!!");
				Container pane = r.getContentPane();
				JLabel j = new JLabel();

				j.setIcon(new ImageIcon("LAST EXEMPT/images/light.png"));

				if (speedCounter >= 30) {
					j.setIcon(new ImageIcon("LAST EXEMPT/images/space_sign_small_2.gif"));

				}
				JPanel p2 = new JPanel(new FlowLayout(3, 1, 1));
				p2.add(j);
				pane.add(p2, BorderLayout.CENTER);
				r.pack();
				r.setLocation(700, 500);
				r.setVisible(true);

			} else {
				timer.setDelay(currentSpeed);

			}
			}
			else {
				JOptionPane.showMessageDialog(null, "Click The Start Button", "Error", JOptionPane.PLAIN_MESSAGE);
			}
		} else if (e.getSource() == y) {
			speedCounter = 0;
			started = false;
			String r = y.getSelectedItem().toString();
			if (r.equals("10")) {
				s.simulate(10);
			} else if (r.equals("50")) {
				s.simulate(50);
			} else if (r.equals("100")) {
				s.simulate(100);
			} else if (r.equals("500")) {
				s.simulate(500);
			}

		} else if (e.getSource() == total) {
			started = false;
			String a = s.getSimStep() + "    Total: " + s.getSimTotal();
			JOptionPane.showMessageDialog(null, a, "Total Number", JOptionPane.PLAIN_MESSAGE);

		} else if (e.getSource() == del) {
			JOptionPane.showMessageDialog(null, "Stored data has been deleted!", "Deleted!", JOptionPane.PLAIN_MESSAGE);
			try (BufferedWriter bw = new BufferedWriter(new FileWriter("Saved.txt", false))) {
				bw.write("");
				
			} catch (IOException m) {
			
			}

		} else if (e.getSource() == ok) {
			String a1 = s.getSimStep() + " Total: " + s.getSimTotal();
			if (saveTotal.isSelected())
				try (BufferedWriter bw = new BufferedWriter(new FileWriter("Saved.txt", true))) {
					bw.write(a1);
					bw.newLine();
					JOptionPane.showMessageDialog(null, "Saved Total!", " Saved! ", JOptionPane.PLAIN_MESSAGE);
				} catch (IOException m) {
					m.printStackTrace();
				}
			else if (savePop.isSelected()) {
				try (BufferedWriter bw = new BufferedWriter(new FileWriter("Saved.txt", true))) {
					bw.write(s.getStats());
					bw.newLine();
					JOptionPane.showMessageDialog(null, "Saved Population Data", "Saved!", JOptionPane.PLAIN_MESSAGE);
				} catch (IOException m) {
					m.printStackTrace();
				}
			} else {
				JOptionPane.showMessageDialog(null, " Please select what you want to save", "Alert!",
						JOptionPane.PLAIN_MESSAGE);
			}

		} else if (e.getSource() == popDet) {
			String saved = "Current Details : " + s.getStats() + "  " + s.getSimStep();
			JOptionPane.showMessageDialog(null, saved, "Saved Details", JOptionPane.PLAIN_MESSAGE);

		}
	}

}