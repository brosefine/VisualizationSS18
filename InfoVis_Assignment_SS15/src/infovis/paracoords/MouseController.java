package infovis.paracoords;

import infovis.scatterplot.Data;
import infovis.scatterplot.Model;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.concurrent.TimeUnit;

import static java.awt.geom.Line2D.ptLineDist;
import static java.lang.Math.abs;

public class MouseController implements MouseListener, MouseMotionListener {
	private View view = null;
	private Model model = null;
	Shape currentShape = null;
	Axis left, right;
	int left_id, right_id;
	double rad = 5.0, x1, x2, y1, y2;
	
	public void mouseClicked(MouseEvent e) {
		
	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

	public void mousePressed(MouseEvent e) {
		if(view.getViewRect().contains(e.getX(), e.getY())) {
			double x = e.getX() - view.getPadding();
			double y = e.getY() - view.getPadding();
			if(view.swap()){
				for (int i = 0; i < view.getAxis().length; i++) {
					Axis a = view.getAxis()[i];
					if (abs(a.getPos() - x) < rad) {
						right_id = i;
						view.repaint();
						view.swapAxis(left_id, right_id);
						view.repaint();
						view.toggleSwap();
						view.getAxis()[right_id].setColor(Color.BLACK);
						view.getAxis()[left_id].setColor(Color.BLACK);
						view.repaint();
						return;
					}
				}
			}

			for (int i = 0; i < view.getAxis().length; i++) {
				Axis a = view.getAxis()[i];
				if (abs(a.getPos() - x) < rad){
					view.toggleSwap();
					left_id = i;
					a.setColor(Color.BLUE);
					view.repaint();
					return;
				}
				if (a.getPos() <= x) {
					left = a;
					x1 = a.getPos();
				} else {
					right = a;
					x2 = a.getPos();
					break;
				}
			}

			for(Data d : model.getList()){
				y1 = left.getLength() - (d.getValue(left.getId()) - left.getRange().getMin()) * left.getScale();
				y2 = right.getLength() - (d.getValue(right.getId())- right.getRange().getMin()) * right.getScale();

				if(ptLineDist(x1, y1, x2, y2, x, y) < rad){
					if(d.getColor() == Color.GREEN){
						d.setColor(Color.RED);
					} else {
						d.setColor(Color.GREEN);
					}
					view.repaint();
				}

			}

		}


	}

	public void mouseReleased(MouseEvent e) {

	}

	public void mouseDragged(MouseEvent e) {

	}

	public void mouseMoved(MouseEvent e) {

	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

}
