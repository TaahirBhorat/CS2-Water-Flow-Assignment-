//Taahir Bhorat
import java.awt.*;
import javax.swing.*;

public class FlowPanel extends JPanel implements Runnable {
	Terrain land;
	//2d array of type Waterthread responsible of tracking changes in water depth
	WaterThread[][] WaterDepth;
	int one = 1;
	int zero = 00;
	volatile boolean paused = true;
	volatile int timestep_count = 0;


	FlowPanel(Terrain terrain) {
		land=terrain;


		//Create WaterDepth Map overlayed on the terrain Greyscale
		WaterDepth = new WaterThread[land.getDimX()][land.getDimY()];
		for(int i=0; i<land.getDimX(); i++)
			for(int j=0; j<land.getDimY(); j++)
				WaterDepth[i][j] = new WaterThread(i , j);


	}
		
	//Terrain and water painting
	@Override
    protected void paintComponent(Graphics g) {
		int width = getWidth();
		int height = getHeight();
		super.paintComponent(g);
		// draw the landscape in greyscale as an image
		if (land.getImage() != null)
			g.drawImage(land.getImage(), 0, 0, null);

		for(int i = 0; i< land.getDimX(); i++) {
			for (int j = 0; j < land.getDimY(); j++) {
				if (WaterDepth[i][j].Depth == 5) {
					g.setColor(Color.blue);
					g.fillRect(i, j, 4, 4);
				}
				if (WaterDepth[i][j].Depth == 4) {
					g.setColor(Color.blue);
					g.fillRect(i, j, 3, 3);
				}
				if (WaterDepth[i][j].Depth <= 3 && WaterDepth[i][j].Depth>0) {
					g.setColor(Color.blue);
					g.fillRect(i, j, 2, 2);
				}

			}
		}

	}


	public synchronized void WaterClicked(int I, int J){
		// when clicked add one block of water with a depth of 5
		for(int i = I-3; i <= I+3; i++)
			for(int j = J-3; j <= J+3; j++)
				WaterDepth[I][J].Depth = 5;

		repaint();
	}

	public synchronized void WaterFlow(int I, int J){

		if(WaterDepth[I][J].Depth > 0)
		{
			//instantiate Searching i,j matrix coordinates
			float Height_Total = WaterDepth[I][J].Depth + land.height[I][J];
			int I_flow = 0;
			int J_flow = 0;
			//check neighboring points Height
			for(int i = I-1; i < I+1; i++)
				for(int j = J-1; j < J+1; j++){

					float Height_Total_new = land.height[i][j] + WaterDepth[i][j].Depth;

					//If the neighbour is lower than the current position
					if( Height_Total_new <= Height_Total){
						Height_Total = Height_Total_new;
						I_flow = i;
						J_flow = j;
					}
				}
			//make sure that the water is not on the edge
			if( I_flow != 0 && J_flow != 0  && I_flow != land.getDimX() && J_flow != land.getDimY() ){
				WaterDepth[I][J].Depth -= 1;
				WaterDepth[I_flow][J_flow].Depth += 1;
			//if it is on the edge delete it
			}
			else { // else just move the points
				WaterDepth[I][J].Depth = 0;
			}
			repaint();
		}
	}

//for the threads to flow water
	public void Refresh_terrain(int Thread_start, int Thread_end) {
		int[] flow = {5,5};
		while(true)
			if(!paused){
				for(int i = Thread_start; i < Thread_end; i++){
					land.getPermute(i, flow);

					WaterFlow(flow[0], flow[1]);
				}
				timestep_count++;
			}
	}
	public void start_flow(){paused = false;}
	public void stop(){paused = true;}

	public void reset(){
		for(int x=0; x<land.getDimX(); x++)
			for(int y=0; y<land.getDimY(); y++)
				WaterDepth[x][y].Depth = 0;
		timestep_count =0;
		paused = true;
		repaint();
	}
	public void end() {
		paused = true;
		repaint();
	}
	public synchronized void run() {

		//create 4 threads responsible for managing 4 quadrants of the terrain Water Adding and Flow
		Thread quar1 = new Thread(){
			public synchronized void run(){Refresh_terrain(0, (int)(0.5*land.permute.size()) );}
		};
		Thread quar2 = new Thread(){
			public synchronized void run(){Refresh_terrain((int)(0.25*land.permute.size()), (int)(0.5*land.permute.size()));}
		};
		Thread quar3 = new Thread(){
			public synchronized void run(){Refresh_terrain((int)(0.5*land.permute.size()), (int)(0.75*land.permute.size()));}
		};
		Thread quar4 = new Thread(){
			public synchronized void run(){Refresh_terrain((int)(0.75*land.permute.size()),(int)(1*land.permute.size()));}
		};
		//Start all 4 threads to run over respective parts of terrain
		quar1.start();
		quar2.start();
		quar3.start();
		quar4.start();

	}

}