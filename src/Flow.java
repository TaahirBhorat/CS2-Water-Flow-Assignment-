//Taahir Bhorat

import java.awt.BorderLayout;
import javax.swing.*;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

public class Flow {
	static long startTime = 0;
	static int frameX;
	static int frameY;
	static FlowPanel fp;

	// start timer
	private static void tick(){
		startTime = System.currentTimeMillis();
	}
	
	// stop timer, return time elapsed in seconds
	private static float tock(){
		return (System.currentTimeMillis() - startTime) / 1000.0f; 
	}
	
	public static void setupGUI(int frameX,int frameY,Terrain landdata) {
		
		Dimension fsize = new Dimension(800, 800);
    	JFrame frame = new JFrame("Terrain Water-Flow");
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.getContentPane().setLayout(new BorderLayout());
    	
      	JPanel g = new JPanel();
        g.setLayout(new BoxLayout(g, BoxLayout.PAGE_AXIS)); 
   
		fp = new FlowPanel(landdata);
		fp.setPreferredSize(new Dimension(frameX,frameY));

		g.addMouseListener(new MouseAdapter(){ public void mousePressed(MouseEvent e) {
			fp.WaterClicked(e.getX(),  e.getY());
		}});

		// to do: add a MouseListener, buttons and ActionListeners on those buttons
	   	
		JPanel b = new JPanel();
	    b.setLayout(new BoxLayout(b, BoxLayout.LINE_AXIS));
	    JButton buttonReset  = new JButton("Reset");
	    JButton buttonPause = new JButton("Pause");
	    JButton buttonPlay  = new JButton("Play");
		JButton buttonEnd = new JButton("End");
		JLabel timestepLabel = new JLabel("  Timestep: ");
		// add the listener to the jbutton to handle the "pressed" event

		buttonEnd.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				// to do ask threads to stop
				frame.dispose();
				System.exit(0);

			}
		});
		buttonPlay.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				fp.start_flow();

			}
		});

		buttonPause.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				fp.stop();

			}
		});
		buttonReset.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				fp.reset();

			}
		});

		g.add(fp);
		b.add(buttonReset);
		b.add(buttonPause);
		b.add(buttonPlay);
		b.add(buttonEnd);
		b.add(timestepLabel);
		g.add(b);
    	
		frame.setSize(frameX, frameY+50);	// a little extra space at the bottom for buttons
      	frame.setLocationRelativeTo(null);  // center window on screen
      	frame.add(g); //add contents to window
        frame.setContentPane(g);
        frame.setVisible(true);
        Thread fpt = new Thread(fp);
        fpt.start();
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {

				timestepLabel.setText(" Timestep: "+ fp.timestep_count);
			}
		}, 2, 100);

	}
	
		
	public static void main(String[] args) {
		Terrain landdata = new Terrain();
		
		// check that number of command line arguments is correct
		if(args.length != 1)
		{
			System.out.println("Incorrect number of command line arguments. Should have form: java -jar flow.java intputfilename");
			System.exit(0);
		}
				
		// landscape information from file supplied as argument
		// 
		landdata.readData(args[0]);
		
		frameX = landdata.getDimX();
		frameY = landdata.getDimY();
		SwingUtilities.invokeLater(()->setupGUI(frameX, frameY, landdata));
		

	}
}
