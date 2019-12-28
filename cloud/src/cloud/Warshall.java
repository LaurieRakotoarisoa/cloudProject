package cloud;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Warshall {
	private static String File = "jaccard/jaccard.txt";
	private static double edgeThreshold = 0.6;
	private static List<Point> jaccard;
	
	public static List<Point> initiazePoint(String file) throws IOException {
		return Files.lines(Paths.get(file))
			.map(line -> line.split(" "))
			.map(a -> new Point(a[0], a[1], Double.parseDouble(a[2])))
			.collect(Collectors.toList());
	}
	
	public static double distance(String f1, String f2) {
		return jaccard.stream().filter(p -> (p.file1.equals(f1) && p.file2.equals(f2)) || (p.file1.equals(f2) && p.file2.equals(f1)))
						.map(p -> p.distance).findFirst().get();
	}
	
	public static double[][] calculShortestPaths() {
		List<String> files = jaccard.stream().map(p->p.file1).distinct().collect(Collectors.toList());
	    double[][] dist=new double[files.size()][files.size()];

	    for (int i=0;i<dist.length;i++) {
	      for (int j=0;j<dist.length;j++) {
	        if (i==j) {dist[i][i]=0; continue;}
	        if (distance(files.get(i),files.get(j))<=edgeThreshold) dist[i][j]=distance(files.get(i),files.get(j)); else dist[i][j]=Double.POSITIVE_INFINITY;
	      }
	    }

	    for (int k=0;k<dist.length;k++) {
	      for (int i=0;i<dist.length;i++) {
	        for (int j=0;j<dist.length;j++) {
	          if (dist[i][j]>dist[i][k] + dist[k][j]){
	            dist[i][j]=dist[i][k] + dist[k][j];
	          }
	        }
	      }
	    }

	    return dist;
	  }

	public static void main(String[] args) throws IOException {
		long start = System.currentTimeMillis(); 
		jaccard = initiazePoint(File);
		//System.out.println(points);
		double[][] result = calculShortestPaths();
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < result.length; j++) {
				System.out.print(result[i][j]+"\t");
			}
			System.out.println();
		}
		long elapsedTimeMillis = System.currentTimeMillis() - start;
		System.out.println(elapsedTimeMillis);
	}

}
