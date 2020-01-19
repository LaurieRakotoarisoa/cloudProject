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
import java.util.concurrent.atomic.LongAccumulator;
import java.util.function.LongBinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Jaccard {
	private static String DIRECTORY = "index/";
	
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
	
	private static void computeJaccardFile(String file,List<String> otherFiles) throws IOException {
		BufferedWriter bf =Files.newBufferedWriter(Paths.get("Indice-de-centralite/jaccard.txt"), StandardOpenOption.CREATE,StandardOpenOption.APPEND);
		otherFiles.stream()
		.filter(f2 -> f2.compareTo(file) > 0)
		.forEach(f2 -> distanceJaccard(bf,file, f2));
		bf.close();
	}
	
	private static void distanceJaccard(BufferedWriter bf,String f1, String f2) {
		try (Stream<String> f1_lines = Files.lines(Paths.get(DIRECTORY+f1)); 
			Stream<String> f2_lines = Files.lines(Paths.get(DIRECTORY+f2))) {
			Map<String,Long> map1 = f1_lines.map(line -> line.split(" "))
			.collect(Collectors.toMap(a -> new String(a[0]), a -> Long.parseLong(a[1]) ));
			Map<String,Long> map2 = f2_lines.map(line -> line.split(" "))
					.collect(Collectors.toMap(a -> new String(a[0]), a -> Long.parseLong(a[1]) ));
			String res = f1+" "+f2+" "+computeJaccardMap(map1, map2)+"\n";
			bf.write(res);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static double computeJaccardMap(Map<String,Long> m1, Map<String,Long> m2) {
		m1.keySet().retainAll(m2.keySet());
		m2.keySet().retainAll(m1.keySet());
		Long num = m1.entrySet().stream()
				.map(e -> Math.abs(e.getValue()- m2.get(e.getKey())))
				.reduce(Long.valueOf(0),Long::sum);
		Long den = m2.entrySet().stream()
				.map(e -> Math.max(e.getValue(), m1.get(e.getKey())))
				.reduce(Long.valueOf(0),Long::sum);
		
		if(den > 0) {
			return  num*1.0/den;
		}
		else {
			return 0.0;
		}
		
	}
	
	public static void main (String [] args) throws IOException {
		long start = System.currentTimeMillis(); 
		computeJaccard(getOrderedFileNames(DIRECTORY));
		long elapsedTimeMillis = System.currentTimeMillis() - start;
		System.out.println(elapsedTimeMillis);
	}
	

}
