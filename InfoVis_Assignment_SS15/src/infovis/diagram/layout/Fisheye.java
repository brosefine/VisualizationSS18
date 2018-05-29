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

	public void setMouseCoords(int x, int y, View view) {
		// TODO Auto-generated method stub
	}

	public Model transform(Model model, View view) {
		Point2D fisheyeCenter = new Point2D.Double(view.getWidth() / 2, view.getHeight() / 2);
		Model fisheyeModel = view.getModel();
		
		for (Vertex vert: model.getVertices()){
			double x = vert.getCenterX(); 
			double y = vert.getCenterY();
			double q_norm_x, q_norm_y, s_x, s_y;
			
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
			q_norm_x = calculatePosition(fisheyeCenter.getX(), q_norm_x, view.getWidth());
			q_norm_y = calculatePosition(fisheyeCenter.getY(), q_norm_y, view.getHeight());
			
			s_x = Math.abs(q_norm_x - x);
			s_y = Math.abs(q_norm_y - y);
			
			vert.setFrame(x - s_x, y - s_y, s_x * 2, s_y * 2);
			//new position
			//new size
		}
		// TODO Auto-generated method stub
		return fisheyeModel;
	}
	
	private double g(double x) {
		double d = 4;
		return (((d + 1) * x) / d * x + 1); 
	}
	
	private double calculatePosition(double fish, double p, double border) {
		double d_max;
		double d_norm = Math.abs(p - fish);
		
		
		if(p < fish) {
			d_max = fish;
			return (fish - g(d_norm / d_max) * d_max);
		} else {
			d_max = Math.abs(border - fish);
			return (fish + g(d_norm / d_max) * d_max);
		}
		
		
		
	}
	
}
