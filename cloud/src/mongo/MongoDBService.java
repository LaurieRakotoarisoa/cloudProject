package mongo;

import java.net.UnknownHostException;
import java.util.Map;
import java.util.stream.Stream;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import cloud.FileIndex;
import cloud.Jaccard;

public class MongoDBService {
	
	
	public static MongoClient connection() throws UnknownHostException {
		return new MongoClient();
	}
	
	public static void insertAllIndexes(String directory) throws UnknownHostException {
		MongoClient client = new MongoClient();
		DB database = client.getDB("cloudProject");
		DBCollection collection = database.getCollection("index");
		Stream<String> files = Jaccard.getOrderedFileNames(directory).stream();
		files.forEach(file -> insertIndex(file,collection));
	}
	
	public static void insertIndex(String filename, DBCollection collection) {
		Map<String, Long> index = FileIndex.createMapIndex(filename);
		DBObject indexObject = new BasicDBObject("name",filename)
				.append("words", index);
		collection.insert(indexObject);
	}
	
	public static void searchWord(String word) throws UnknownHostException {
		MongoClient client = new MongoClient();
		DB database = client.getDB("cloudProject");
		DBCollection collection = database.getCollection("index");
		DBObject query = new BasicDBObject(word, new BasicDBObject("$exists",true));
		DBCursor cursor = collection.find(query);
		
		try {
			while (cursor.hasNext()) {
				System.out.println(cursor.next());
			}
		} finally {
			// close the cursor
			cursor.close();
		}
	}
	
	public static void main(String [] args) {
		
		
		
		
		try {
			//insertAllIndexes("cloud/docs");
			searchWord("dottore");
		} catch (UnknownHostException e) {
			System.out.println("Unknown HOST");
		} 
		
	}

}
