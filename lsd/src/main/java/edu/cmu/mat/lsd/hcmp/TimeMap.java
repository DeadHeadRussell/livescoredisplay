package edu.cmu.mat.lsd.hcmp;

public class TimeMap {
	private double _m;
	private double _b;

	public TimeMap(double m, double b) {
		_m = m;
		_b = b;
	}

	public static TimeMap Create(TimeMap tm1, TimeMap tm2) {
		double x1 = 0;
		double y1 = tm2.at(tm1.at(x1));

		double x2 = 10;
		double y2 = tm2.at(tm1.at(x2));

		double slope = (y2 - y1) / (x2 - x1);
		return Create(x1, y1, slope);
	}

	public static TimeMap Create(double x, double y, double slope) {
		double b = y - slope * x;
		return new TimeMap(slope, b);
	}

	public double at(double x) {
		return _m * x + _b;
	}

	public double from(double y) {
		return (y - _b) / _m;
	}
}
