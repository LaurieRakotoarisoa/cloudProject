package cloud;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Jaccard {
	private static String DIRECTORY = "index/";
	
	public static void computeJaccard(List<String> files) {
		files.stream()
		.forEach(f -> {
			try {
				computeJaccardFile(f, files);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
	
	public static List<String> getOrderedFileNames(String directory){
		try(Stream<Path> files = Files.walk(Paths.get(directory))){
			return files.filter(Files::isRegularFile)
			.map(f -> f.getFileName().toString())
			.sorted()
			.collect(Collectors.toList());
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private static void computeJaccardFile(String file,List<String> otherFiles) throws IOException {
		BufferedWriter bf =Files.newBufferedWriter(Paths.get("docs/jaccard"), StandardOpenOption.CREATE,StandardOpenOption.APPEND);
		otherFiles.stream()
		.filter(f2 -> f2.compareTo(file) > 0)
		.forEach(f2 -> distanceJaccard(bf,file, f2));
		bf.close();
	}
	
	private static void distanceJaccard(BufferedWriter bf,String f1, String f2) {
		try (Stream<String> f1_lines = Files.lines(Paths.get(DIRECTORY+f1)); 
			Stream<String> f2_lines = Files.lines(Paths.get(DIRECTORY+f2))) {
			Map<String,Integer> map1 = f1_lines.map(line -> line.split(" "))
			.collect(Collectors.toMap(a -> new String(a[0]), a -> Integer.parseInt(a[1]) ));
			Map<String,Integer> map2 = f2_lines.map(line -> line.split(" "))
					.collect(Collectors.toMap(a -> new String(a[0]), a -> Integer.parseInt(a[1]) ));
			map1.keySet().retainAll(map2.keySet());
			map2.keySet().retainAll(map1.keySet());
			String res = f1+" "+f2+" "+computeJaccardMap(map1, map2)+"\n";
			bf.write(res);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static double computeJaccardMap(Map<String,Integer> m1, Map<String,Integer> m2) {
		Integer num = m1.entrySet().stream()
				.map(e -> Math.abs(e.getValue()-m2.get(e.getKey())))
				.reduce(0, Integer::sum);
		Integer den = m2.entrySet().stream()
				.map(e -> Math.max(e.getValue(), m1.get(e.getKey())))
				.reduce(0, Integer::sum);
		
		if(den > 0) {
			return  num*1.0/den;
		}
		else {
			return 0.0;
		}
		
	}
	
	public static void main (String [] args) throws IOException {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		System.out.println("Début "+dtf.format(LocalDateTime.now()));  
		computeJaccard(getOrderedFileNames(DIRECTORY));
		System.out.println("Fin "+dtf.format(LocalDateTime.now()));  
	}
	

}
