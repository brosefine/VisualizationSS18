package infovis.scatterplot;

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
	private double start_x, start_y;
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

	    if(matrix.contains(start_x, start_y)) {

            start_x = start_x - matrix.getX();
            start_y = start_y - matrix.getY();
            id_x = (int)(start_x / dim);
            id_y = (int)(start_y / dim);


            //Iterator<Data> iter = model.iterator();
            //view.getMarkerRectangle().setRect(x,y,w,h);
            //view.repaint();
        }
	}

	public void mouseReleased(MouseEvent arg0) {
	}

	public void mouseDragged(MouseEvent e) {
        double padding_x = view.getMatrixRectangle().getX();
        double padding_y = view.getMatrixRectangle().getY();
        double pos_x = e.getX() - padding_x;
        double pos_y = e.getY() - padding_y;
        double dim = view.getDimGrid();


        double min_x = max(min(start_x, pos_x), id_x * dim);
        double min_y = max(min(start_y, pos_y), id_y * dim);
        double max_x = min(max(start_x, pos_x), (id_x + 1) * dim);
        double max_y = min(max(start_y, pos_y), (id_y + 1) * dim);
        view.setMarkerRectangle(min_x, min_y, max_x, max_y);

        double x_min = model.getRanges().get(id_x).getMin();
        double x_max = model.getRanges().get(id_x).getMax();
        double y_min = model.getRanges().get(id_y).getMin();
        double y_max = model.getRanges().get(id_y).getMax();

        double local_min_x = id_x * dim + padding_x;
        double local_min_y = id_y * dim + padding_y;
        //double local_max_x = (id_x + 1) * dim + padding_x;
        //double local_max_y = (id_y + 1) * dim + padding_y;

        //System.out.println(local_max_x + " , " + local_max_y + " , " + local_min_x + " , " + local_min_y);

        min_x = min_x - local_min_x + padding_x;
        max_x = max_x - local_min_x + padding_y;
        min_y = min_y - local_min_y + padding_x;
        max_y = max_y - local_min_y + padding_y;

        //System.out.println("x: " + min_x + " - " + max_x + " y: " + min_y + " - " + max_y);

        double scaling_x = dim / (x_max - x_min);
        double scaling_y = dim / (y_max - y_min);

        for(Data d : model.getList()){
            double d_x = (d.getValue(id_x) - x_min) * scaling_x;
            double d_y = (d.getValue(id_y) - y_min) * scaling_y;

            if(min_x <= d_x && d_x <= max_x && min_y <= d_y && d_y <= max_y){
                d.setColor(Color.GREEN);
                System.out.println(d.getLabel());
            } else {
                d.setColor(Color.RED);
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
