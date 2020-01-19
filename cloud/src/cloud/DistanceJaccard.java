package cloud;

public class DistanceJaccard {
	private String book1;
	private String book2;
	private double distance;
	
	public DistanceJaccard(String book1, String book2, double distance) {
		this.book1 = book1;
		this.book2 = book2;
		this.distance = distance;
	}
	
	public String getBook1() {
		return book1;
	}
	
	public String getBook2() {
		return book2;
	}
	
	public double getDistance() {
		return distance;
	}

}
