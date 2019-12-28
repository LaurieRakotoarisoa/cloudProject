package cloud;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Closeness {
	private static String Warshall = "Indice-de-centralite/warshall.txt";
	private static String Closeness = "Indice-de-centralite/closeness.txt";
	private static List<Point> warshall;
	
	public static List<Point> initiazePoint() throws IOException {
		return Files.lines(Paths.get(Warshall))
			.map(line -> line.split(" "))
			.filter(a -> !a[2].equals("Infinity"))
			.map(a -> new Point(a[0], a[1], Double.parseDouble(a[2])))
			.collect(Collectors.toList());
	}
	
	public static double computeCloseness(String file) {
		double n= warshall.stream().map(p -> p.file1).distinct().count()-1;
		double sum = warshall.stream().filter(p -> p.file1.equals(file) || p.file2.equals(file))
				.mapToDouble(p-> p.distance).sum();
		System.out.println(n+" / "+sum+" = "+n/sum);
		return n/sum;
	}
	
	private static void computeClosenessFile() throws IOException{
		BufferedWriter bf =Files.newBufferedWriter(Paths.get(Closeness));
		warshall.stream().map(p->p.file1).distinct().forEach(f -> {
			try {
				bf.write(f+" "+computeCloseness(f)+"\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		bf.close();
	}
	
	public static void main(String[] args) throws IOException {
		long start = System.currentTimeMillis(); 
		warshall = initiazePoint();
		computeClosenessFile();
		long elapsedTimeMillis = System.currentTimeMillis() - start;
		System.out.println(elapsedTimeMillis);
	}

}
