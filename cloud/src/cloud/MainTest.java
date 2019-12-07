package cloud;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainTest {
	
	public static void main(String [] args) {
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		System.out.println("Début "+dtf.format(LocalDateTime.now()));  
		FileIndex.createFileIndexOfDirectory("docs/");
		System.out.println("Fin "+dtf.format(LocalDateTime.now()));
		
		
	}

}
