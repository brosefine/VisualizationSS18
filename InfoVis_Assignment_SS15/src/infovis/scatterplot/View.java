package infovis.scatterplot;

import infovis.debug.Debug;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

public class View extends JPanel {
	     private Model model = null;
	     private Rectangle2D markerRectangle = new Rectangle2D.Double(0,0,0,0);
	     private Rectangle2D matrixRectangle = new Rectangle2D.Double(0,0,0,0);
	     private double dimGrid = 0.0;


		 public double getDimGrid() {
			return dimGrid;
		 }

		 public void setDimGrid(double dimGrid) {
			this.dimGrid = dimGrid;
		 }

		 public Rectangle2D getMarkerRectangle() {
			return markerRectangle;
		 }

		 public void setMarkerRectangle(double min_x, double min_y, double max_x, double max_y) {
			this.markerRectangle = new Rectangle2D.Double(min_x, min_y, (max_x - min_x), (max_y - min_y));
		 }

		 public Rectangle2D getMatrixRectangle() { return matrixRectangle;}

		 public void setMatrixRectangle(Rectangle2D matrixRectangle) {
			this.matrixRectangle = matrixRectangle;
		 }
		 
		@Override
		public void paint(Graphics g) {

			Graphics2D g2D = (Graphics2D) g;
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			g2D.clearRect(0, 0, getWidth(), getHeight());
			
			double numberOfLabels = model.getLabels().size();
			double padding = 50.0;
			if(getWidth() < getHeight()) dimGrid = (getWidth() - padding) / numberOfLabels;
			else dimGrid = (getHeight() - padding) / numberOfLabels;

			matrixRectangle = new Rectangle2D.Double(10.0, 10.0, numberOfLabels*dimGrid, numberOfLabels*dimGrid);
			
			g2D.setColor(Color.BLACK);
			g2D.draw(matrixRectangle);
			
			//TODO Label beschriften

			double x_begin, x_end, y_begin, y_end;
			for(int i = 1; i <= numberOfLabels; i++) {
				//vertical line
				x_begin = i * dimGrid + matrixRectangle.getX();
				y_begin = matrixRectangle.getY();
				x_end = x_begin;
				y_end = matrixRectangle.getMaxY();
				Line2D line = new Line2D.Double(x_begin, y_begin, x_end, y_end);
				g2D.draw(line);
				//horizontal line
				x_begin = matrixRectangle.getX();
				y_begin = i * dimGrid + matrixRectangle.getY();
				x_end = matrixRectangle.getMaxX();
				y_end = y_begin;
				line.setLine(x_begin, y_begin, x_end, y_end);
				g2D.draw(line);
			}

			g2D.translate(matrixRectangle.getX(), matrixRectangle.getY());
			for(int i = 0; i < numberOfLabels; i++){
				for(int j = 0; j < numberOfLabels; j++){
					paintScatterPlot(g2D, dimGrid, i, j);
				}
			}


			g2D.setColor(Color.GREEN);
			g2D.draw(markerRectangle);
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
		 	double d_x, d_y;
		 	double rad = 3.0;

			double x_min = model.getRanges().get(label_x).getMin();
			double x_max = model.getRanges().get(label_x).getMax();
			double y_min = model.getRanges().get(label_y).getMin();
			double y_max = model.getRanges().get(label_y).getMax();


			double scaling_x = dimGrid / (x_max - x_min);
			double scaling_y = dimGrid / (y_max - y_min);

			g2D.translate((label_x) * dimGrid, (label_y) * dimGrid);

			for(Data d : model.getList()){
				d_x = (d.getValue(label_x) - x_min) * scaling_x;
				d_y = (d.getValue(label_y) - y_min) * scaling_y;

				g2D.setColor(d.getColor());
				g2D.fill(new Ellipse2D.Double(d_x - rad, d_y - rad, 2*rad, 2*rad));
			}

			g2D.translate(-(label_x) * dimGrid, -(label_y) * dimGrid);

		}


		
		public void setModel(Model model) {
			this.model = model;
		}
}
