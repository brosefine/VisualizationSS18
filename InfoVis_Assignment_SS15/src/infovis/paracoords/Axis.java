package infovis.paracoords;

import infovis.scatterplot.Range;

import java.awt.*;
import java.awt.geom.Line2D;

public class Axis {

    private Line2D line_ = new Line2D.Double(0.0, 0.0, 0.0, 0.0);
    private int id_ = 0;
    private Range range;
    private Color color = Color.BLACK;


    private double scale;

    public Line2D getLine() {
        return line_;
    }

    public void setLine(Line2D line_) {
        this.line_ = line_;
    }

    public int getId() {
        return id_;
    }

    public void setId(int id) {
        this.id_ = id;
    }

    public void setColor(Color col) {this.color = col;}

    public Color getColor() {return this.color;}

    public double getDistance (double x, double y){
        return line_.ptLineDist(x, y);
    }

    public void setLength(double y){
        this.line_ = new Line2D.Double(line_.getX1(), 0.0, line_.getX2(), y);
    }

    public double getLength() {return this.line_.getY2(); }

    public void setPos(double x){
        this.line_ = new Line2D.Double(x, line_.getY1(), x, line_.getY2());
    }

    public double getPos() { return this.line_.getX1();}

    public Range getRange() {
        return range;
    }

    public void setRange(Range range) {
        this.range = range;
    }

    public double getScale() {
        return scale * getLength();
    }

    public void setScale() {
        this.scale = 1.0 / (range.getMax() - range.getMin());
    }


}


