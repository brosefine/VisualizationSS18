package infovis.scatterplot;

import infovis.diagram.elements.None;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class MouseController implements MouseListener, MouseMotionListener {

	private Model model = null;
	private View view = null;
    private boolean draw_rect = false;
	private double start_x, start_y, padding_x, padding_y;
	private double range_min_x, range_min_y, range_max_x, range_max_y;
	private double scaling_x, scaling_y, local_min_x, local_min_y;
	private int id_x, id_y;

	public void mouseClicked(MouseEvent arg0) {
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent e) {

	    Rectangle2D matrix = view.getMatrixRectangle();
	    double dim = view.getDimGrid();
        start_x = e.getX();
        start_y = e.getY();
        padding_x = matrix.getX();
        padding_y = matrix.getY();

	    if(matrix.contains(start_x, start_y)) {

            start_x = start_x - padding_x;
            start_y = start_y - padding_y;
            id_x = (int)(start_x / dim);
            id_y = (int)(start_y / dim);
            range_min_x = model.getRanges().get(id_x).getMin();
            range_max_x = model.getRanges().get(id_x).getMax();
            range_min_y = model.getRanges().get(id_y).getMin();
            range_max_y = model.getRanges().get(id_y).getMax();
            scaling_x = dim / (range_max_x - range_min_x);
            scaling_y = dim / (range_max_y - range_min_y);

            local_min_x = id_x * dim + padding_x;
            local_min_y = id_y * dim + padding_y;
            draw_rect = true;


            //Iterator<Data> iter = model.iterator();
            //view.getMarkerRectangle().setRect(x,y,w,h);
            //view.repaint();
        }
	}

	public void mouseReleased(MouseEvent arg0) {
	    draw_rect = false;
	}

	public void mouseDragged(MouseEvent e) {
        if(draw_rect) {
            double pos_x = e.getX() - padding_x;
            double pos_y = e.getY() - padding_y;
            double dim = view.getDimGrid();

            double min_x = max(min(start_x, pos_x), id_x * dim);
            double min_y = max(min(start_y, pos_y), id_y * dim);
            double max_x = min(max(start_x, pos_x), (id_x + 1) * dim);
            double max_y = min(max(start_y, pos_y), (id_y + 1) * dim);
            view.setMarkerRectangle(min_x, min_y, max_x, max_y);

            min_x = min_x - local_min_x + padding_x;
            max_x = max_x - local_min_x + padding_y;
            min_y = min_y - local_min_y + padding_x;
            max_y = max_y - local_min_y + padding_y;

            for (Data d : model.getList()) {
                double d_x = (d.getValue(id_x) - range_min_x) * scaling_x;
                double d_y = (d.getValue(id_y) - range_min_y) * scaling_y;

                if (min_x <= d_x && d_x <= max_x && min_y <= d_y && d_y <= max_y) {
                    d.setColor(Color.GREEN);
                } else {
                    d.setColor(Color.RED);
                }


            }
        }


        view.repaint();
	}

	public void mouseMoved(MouseEvent arg0) {
	}

	public void setModel(Model model) {
		this.model  = model;	
	}

	public void setView(View view) {
		this.view  = view;
	}

}
