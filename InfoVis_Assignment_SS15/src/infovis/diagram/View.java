package infovis.diagram;

import infovis.diagram.elements.Element;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import javax.swing.JPanel;



public class View extends JPanel{
	private Model model = null;
	private Color color = Color.BLUE;
	private double scale = 1;
	private double overviewScale = 1;
	private double translateX= 0;
	private double translateY=0;
	private Rectangle2D marker = new Rectangle2D.Double(0, 0, 300, 250);
	private Rectangle2D overviewRect = new Rectangle2D.Double(0, 0, 300, 250);   

	public Model getModel() {
		return model;
	}
	public void setModel(Model model) {
		this.model = model;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}

	
	public void paint(Graphics g) {
		
		Graphics2D g2D = (Graphics2D) g;
		
	
		overviewScale = overviewRect.getHeight() / model.getMaxY();
		if(overviewRect.getWidth()/model.getMaxX() < overviewScale) overviewScale = overviewRect.getWidth()/model.getMaxX();
		double marker_scale = overviewScale / scale;
		
		//diagram world
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.clearRect(0, 0, getWidth(), getHeight());
		g2D.translate(translateX, translateY);
		g2D.scale(scale, scale);
		paintDiagram(g2D);
		//back to normal size , position
		g2D.scale(1/scale, 1/scale);
		g2D.translate(-translateX, -translateY);
		//draw overview
		g2D.setColor(Color.WHITE);
		g2D.fill(overviewRect);
		g2D.setColor(Color.BLACK);
		g2D.draw(overviewRect);
		g2D.translate(overviewRect.getX(), overviewRect.getY());
				
		g2D.setClip(overviewRect);
		g2D.scale(overviewScale, overviewScale);
		
		paintDiagram(g2D);
		//diagram in overviewworld
		g2D.scale(1/overviewScale, 1/overviewScale);
		
		//g2D.scale(1/scale, 1/scale);
		//set marker
		setMarker(marker.getX(), marker.getY(), getWidth()*marker_scale, getHeight()*marker_scale);
		//updateMarker(marker.getX(), marker.getY());
		//draw marker
		g2D.setColor(Color.RED);
		g2D.draw(marker);
		
		

	}
	private void paintDiagram(Graphics2D g2D){
		for (Element element: model.getElements()){
			element.paint(g2D);
		}
	}
	
	public void setScale(double scale) {
		this.scale = scale;
	}
	public double getScale(){
		return scale;
	}
	public double getOverviewScale(){
		return overviewScale;
	}
	public double getTranslateX() {
		return translateX;
	}
	public void setTranslateX(double translateX) {
		this.translateX = translateX;
	}
	public double getTranslateY() {
		return translateY;
	}
	public void setTranslateY(double tansslateY) {
		this.translateY = tansslateY;
	}
	public void updateTranslation(double x, double y){
		setTranslateX(x);
		setTranslateY(y);
	}	
	public void updateMarker(double d, double e){
		marker.setRect(d, e, getWidth(), getHeight());
	}
	public Rectangle2D getMarker(){
		return marker;
	}
	public void setMarker(Rectangle2D marker){
		this.marker = marker;
	}
	public void setMarker(double x, double y, double w, double h){
		this.marker = new Rectangle2D.Double(x, y, w, h);
	}
	public void setMarker(double d, double e) {
		this.marker.setRect(d, e,  getWidth(), getHeight());
	}
	public boolean markerContains(int x, int y){
		return marker.contains(x, y);
	}
	public Rectangle2D getOverview() {
		return overviewRect;
	}
}
 