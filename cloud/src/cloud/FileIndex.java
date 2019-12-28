package cloud;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileIndex {
	
	private static Map<String,Long> createMapIndex(String path){
		try (Stream<String> lines = Files.lines(Paths.get("docs/"+path))) {
			return lines.flatMap(l -> Arrays.stream(l.split("[^A-Za-z]+")))
		     .map(w->w.toLowerCase())
		     .filter(w-> w.length()>3)
		     .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private static void createFileIndex(String path) {
		String s = createMapIndex(path).entrySet().stream()
			     .map(entry -> entry.getKey()+" "+entry.getValue())
			     .collect(Collectors.joining("\n"));
		try {
			Files.write(Paths.get("index/"+path),s.getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public static void createFileIndexOfDirectory(String directory) {
		try(Stream<Path> files = Files.walk(Paths.get(directory))){
			files.filter(Files::isRegularFile)
			.map(f -> f.getFileName().toString())
			.forEach(FileIndex::createFileIndex);
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
