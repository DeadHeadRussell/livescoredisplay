package edu.cmu.mat.lsd.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class JArrow extends JPanel {
	private static final long serialVersionUID = 5905333838784070812L;

	private JComponent _parent;
	private static final BasicStroke _normal_stroke = new BasicStroke(1);
	private static final BasicStroke _wide_stroke = new BasicStroke(3);
	private static final double _side = 15.0;
	private static final double _head_angle = Math.toRadians(60);
	private static final Color _stemColor = new Color(0,255,0,64);
	private static final Color _headColor = new Color(0,255,0,255);
	private int _from_x;
	private int _from_y;
	private int _to_x;
	private int _to_y;
	
	public JArrow(JComponent parent) {
		_parent = parent;
	}
	
	public void setPosition(int from_x, int from_y, int to_x, int to_y) {
		_from_x = from_x;
		_from_y = from_y;
		_to_x = to_x;
		_to_y = to_y;
	}
	
	@Override
	public Dimension getPreferredSize() {
		return _parent.getSize();
	}
	
	@Override
	public void paint(Graphics graphics) {
		double x1 = (double) _from_x;
		double y1 = (double) _from_y;
		double dx = _to_x - _from_x;
		double dy = _to_y - _from_y;
		double theta = Math.atan2(dy, dx);
		double length = Math.sqrt(dx * dx + dy * dy) - _side/2;
		double x2 = x1 + length * Math.cos(theta);
		double y2 = y1 + length * Math.sin(theta);
		
		Graphics2D g2 = (Graphics2D) graphics;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setStroke(_wide_stroke);
		g2.setColor(_stemColor);
		g2.draw(new Line2D.Double(x1, y1, x2, y2));
		
		double theta1 = theta - Math.toRadians(180) + _head_angle/2;
		double p1_x = _to_x + _side * Math.cos(theta1);
		double p1_y = _to_y + _side * Math.sin(theta1);
		double theta2 = theta1 - _head_angle; 
		double p2_x = _to_x + _side * Math.cos(theta2);
		double p2_y = _to_y + _side * Math.sin(theta2);
		int[] xs = {_to_x, (int)p1_x, (int)p2_x};
		int[] ys = {_to_y, (int)p1_y, (int)p2_y};
		Polygon p = new Polygon(xs, ys, 3);
		g2.setColor(_headColor);
		g2.setStroke(_normal_stroke);
		g2.fillPolygon(p);
	}
	
}
