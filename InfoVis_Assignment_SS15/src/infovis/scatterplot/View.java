package infovis.scatterplot;

import infovis.debug.Debug;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

public class View extends JPanel {
	     private Model model = null;
	     private Rectangle2D markerRectangle = new Rectangle2D.Double(0,0,0,0); 

		 public Rectangle2D getMarkerRectangle() {
			return markerRectangle;
		}
		 
		@Override
		public void paint(Graphics g) {

			Graphics2D g2D = (Graphics2D) g;
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			g2D.clearRect(0, 0, getWidth(), getHeight());
			
			double numberOfLabels = model.getLabels().size();
			double dimGrid = 100.0;
			Rectangle2D scatterRect = new Rectangle2D.Double(10.0, 10.0, numberOfLabels*dimGrid, numberOfLabels*dimGrid);
			
			g2D.setColor(Color.BLACK);
			g2D.draw(scatterRect);
			
			//TODO Label beschriften
			
			double x_begin, x_end, y_begin, y_end;
			for(int i = 1; i <= numberOfLabels; i++) {
				//vertical line
				x_begin = i * dimGrid + scatterRect.getX();
				y_begin = scatterRect.getY();
				x_end = x_begin;
				y_end = scatterRect.getMaxY();
				Line2D line = new Line2D.Double(x_begin, y_begin, x_end, y_end);
				g2D.draw(line);
				//horizontal line
				x_begin = scatterRect.getX();
				y_begin = i * dimGrid + scatterRect.getY();
				x_end = scatterRect.getMaxX();
				y_end = y_begin;
				line.setLine(x_begin, y_begin, x_end, y_end);
				g2D.draw(line);
			}
			//TODO daten zeichnen
			/*
	        for (String l : model.getLabels()) {
				Debug.print(l);
				Debug.print(",  ");
				Debug.println("");
			}
			for (Range range : model.getRanges()) {
				Debug.print(range.toString());
				Debug.print(",  ");
				Debug.println("");
			}
			for (Data d : model.getList()) {
				Debug.print(d.toString());
				Debug.println("");
			}
			*/
	        
			
		}
		
		private void paintScatterPlot(Graphics2D g2D, double dimGrid, int label_x, int label_y) {
			
		}
		
		public void setModel(Model model) {
			this.model = model;
		}
}
