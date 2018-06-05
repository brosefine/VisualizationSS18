package infovis.diagram.layout;

import infovis.debug.Debug;
import infovis.diagram.Model;
import infovis.diagram.View;
import infovis.diagram.elements.Edge;
import infovis.diagram.elements.Element;
import infovis.diagram.elements.Vertex;

import java.awt.geom.Point2D;
import java.util.Iterator;

/*
 * 
 */

public class Fisheye implements Layout{

	double sizes_x[];
	double sizes_y[];
	double coord_x[];
	double coord_y[];

	int mouse_x = 0;
	int mouse_y = 0;


	public void setMouseCoords(int x, int y, View view) {
		reset(view.getModel());
		mouse_x = x;
		mouse_y = y;
	}

	public Model transform(Model model, View view) {
		Point2D fisheyeCenter = new Point2D.Double(mouse_x, mouse_y);
		//Point2D fisheyeCenter = new Point2D.Double(0, 0);
		Model fisheyeModel = new Model(view.getModel());
		int numOfVert = fisheyeModel.getVertices().size();
		sizes_x = new double [numOfVert];
		sizes_y = new double [numOfVert];
		coord_x = new double [numOfVert];
		coord_y = new double [numOfVert];
		
		for (int i = 0; i < numOfVert; ++i){
			Vertex vert = fisheyeModel.getVertices().get(i);
			coord_x[i] = vert.getX();
			coord_y[i] = vert.getY();
			sizes_x[i] = vert.getWidth();
			sizes_y[i] = vert.getHeight();

			double x = vert.getCenterX();
			double y = vert.getCenterY();
			double edge_x = vert.getX();
			double edge_y = vert.getY();
			double q_norm_x, q_norm_y, s_x, s_y, s;
			double ratio = vert.getHeight() / vert.getWidth();
			
			if(x < fisheyeCenter.getX()) {
				q_norm_x = vert.getX();
			} else {
				q_norm_x = vert.getMAxX();
			}
			
			if(y < fisheyeCenter.getY()) {
				q_norm_y = vert.getY();
			} else {
				q_norm_y = vert.getMaxY();
			}
			
			x = calculatePosition(fisheyeCenter.getX(), x, view.getWidth());
			y = calculatePosition(fisheyeCenter.getY(), y, view.getHeight());
			edge_x = calculatePosition(fisheyeCenter.getX(), edge_x, view.getWidth());
			edge_y = calculatePosition(fisheyeCenter.getY(), edge_y, view.getHeight());
			q_norm_x = calculatePosition(fisheyeCenter.getX(), q_norm_x, view.getWidth());
			q_norm_y = calculatePosition(fisheyeCenter.getY(), q_norm_y, view.getHeight());
			
			s_x = Math.abs(q_norm_x - x);
			s_y = Math.abs(q_norm_y - y);
			s = 2 * Math.min(s_x, s_y);
			
			vert.setFrame(edge_x, edge_y, s, s * ratio);

		}
		// TODO Auto-generated method stub
		view.setModel(fisheyeModel);
		repaint(view);

		System.out.println("after paint in transform");




		return model;
	}

	@Override
	synchronized public Model reset(Model model) {
		double numOfVert = coord_x.length;
		for (int i = 0; i < numOfVert; ++i){
			Vertex vert = model.getVertices().get(i);
			vert.setFrame(coord_x[i], coord_y[i], sizes_x[i], sizes_y[i]);
		}

		return model;
	}

	private double g(double x) {
		double d = 2;
		return (((d + 1) * x) / (d * x + 1));
	}
	
	private double calculatePosition(double fish, double p, double border) {
		double d_max;
		double d_norm = p - fish;
		
		
		if(p < fish) {
			d_max = -fish;
		} else {
			d_max = border - fish;
		}

		return (fish + g(d_norm / d_max) * d_max);
		
	}

	synchronized private void repaint(View view){
		view.repaint();
	}
	
}
