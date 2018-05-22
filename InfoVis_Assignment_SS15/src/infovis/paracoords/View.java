package infovis.paracoords;

import infovis.scatterplot.Data;
import infovis.scatterplot.Model;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

public class View extends JPanel {
	private Model model = null;
	private Axis[] axis;
	private double width = 0;
	private double height = 0;
	private double padding = 100.0;
	private double distance = 0;
	private Rectangle2D viewRect = new Rectangle2D.Double(padding, padding, 0, 0);
	private boolean swap = false;

	@Override
	public void paint(Graphics g) {
		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.clearRect(0, 0, getWidth(), getHeight());

		if (width != getWidth()){
			width = getWidth();
			distance = (width - 2*padding) / (axis.length - 1);
			for(int i = 0; i < axis.length; i++) {
				axis[i].setPos(i * distance);
			}
			viewRect.setRect(padding, padding, getWidth() - 2*padding, getHeight() - 2*padding);
		}

		if(height != getHeight()){
			for(int i = 0; i < axis.length; i++) {
				axis[i].setLength(getHeight() - 2*padding);
				viewRect.setRect(padding, padding, getWidth() - 2*padding, getHeight() - 2*padding);
			}
		}

		g2D.translate(padding, padding);


		for(Axis a : axis){
			g2D.setColor(a.getColor());
			g2D.draw(a.getLine());
			g2D.drawString(a.getLabel(),(float) (a.getPos() - a.getLabel_length()/2), (float) (a.getLength() + padding/2));
		}

		//if(!swap) {
			for (Data d : model.getList()) {
				drawParaLine(d, g2D);
			}
		//}

	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}

	public Model getModel() {
		return model;
	}

	public Rectangle2D getViewRect() {
		return this.viewRect;
	}

	public void setModel(Model model) {
		this.model = model;
		this.axis = new Axis[model.getDim()];
		for (int i = 0; i < model.getDim(); i++){
			Axis a = new Axis();
			a.setId(i);
			a.setRange(model.getRanges().get(i));
			a.setScale();
			a.setLabel(model.getLabels().get(i));
			FontMetrics fm = getFontMetrics( getFont() );
			int width = fm.stringWidth(a.getLabel());
			a.setLabel_length(width);
			axis[i] = a;
		}
	}

	public void drawParaLine(Data d, Graphics2D g2D){
		double x1, x2 = 0, y1, y2 = 0, scale;
		g2D.setColor(d.getColor());
		for (int i = 0; i < axis.length - 1; i++) {
			if (i == 0) {
				x1 = axis[i].getPos();
				x2 = axis[i+1].getPos();
				y1 = axis[i].getLength() - (d.getValue(axis[i].getId()) - axis[i].getRange().getMin()) * axis[i].getScale();
				y2 = axis[i + 1].getLength() - (d.getValue(axis[i + 1].getId())- axis[i + 1].getRange().getMin()) * axis[i + 1].getScale();
			} else {
				x1 = x2;
				y1 = y2;
				x2 = axis[i+1].getPos();
				y2 = axis[i + 1].getLength() - (d.getValue(axis[i + 1].getId())- axis[i + 1].getRange().getMin()) * axis[i + 1].getScale();
			}
			g2D.draw(new Line2D.Double(x1, y1, x2, y2));
		}
	}

	public Axis[] getAxis() {
		return axis;
	}

	public void toggleSwap(){ this.swap = !swap;}

	public boolean swap() {return this.swap;}

	public void swapAxis(int left, int right){
		Axis tmp = axis[left];
		double pos_left = axis[left].getPos();
		axis[left] = axis[right];
		axis[right] = tmp;
		axis[right].setPos(axis[left].getPos());
		axis[left].setPos(pos_left);
	}

	public void setAxis(Axis[] axis) {
		this.axis = axis;
	}

	public double getPadding() {
		return padding;
	}

	public double getDistance() {
		return distance;
	}
	
}
