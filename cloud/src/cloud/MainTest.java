package cloud;

public class MainTest {
	
	public static void main(String [] args) {
		
		long start = System.currentTimeMillis();
		FileIndex.createFileIndexOfDirectory("docs/");
		long elapsedTimeMillis = System.currentTimeMillis() - start;
		System.out.println(elapsedTimeMillis);
		
		
	}

}
