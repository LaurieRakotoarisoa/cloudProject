package cloud;

public class Point {
	
	public String file1;
	public String file2;
	public double distance;
	public Point(String file1, String file2, double distance) {
		this.file1 = file1;
		this.file2 = file2;
		this.distance = distance;
	}
	@Override
	public String toString() {
		return file1+" "+file2+" "+distance;
	}

}
