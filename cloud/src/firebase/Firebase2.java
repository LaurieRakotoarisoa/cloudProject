package firebase;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;

import cloud.DistanceJaccard;
import cloud.FileIndex;
import cloud.Jaccard;

public class Firebase2 {
	
	public static void addFiles2Firebase(String directory) throws IOException, InterruptedException, ExecutionException {
		Firestore db = FirestoreClient.getFirestore();
		CollectionReference books = db.collection("books");
		CollectionReference jaccard = db.collection("jaccard");
		CollectionReference index = db.collection("index");
		Stream<String> files = Jaccard.getOrderedFileNames(directory).stream();
		files.forEach(f -> {
				addFile2Firebase(f,books,jaccard,index);
		});
	}
	
	public static void addFile2Firebase(String filename, CollectionReference books, CollectionReference jaccard, CollectionReference index) {
		Map<String, Long> map1 = FileIndex.createMapIndex(filename);
		Firestore db = FirestoreClient.getFirestore();
		StreamSupport.stream(books.listDocuments().spliterator(),false)
		.forEach(b2 -> { try {
			if(b2.get().get().exists()) {
				addJaccard(filename, b2, jaccard, index, map1);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		});
		DocumentReference document = db.collection("index").document(filename);
		DocumentReference bookname = db.collection("books").document(filename);
		Map<String,Object> fields = new HashMap<String, Object>();
		fields.put("words", map1);
		document.set(fields);
		fields.clear();
		fields.put("name", filename);
		
		bookname.set(fields);
		
	}
	/**
	 * calcul de la distance de jaccard d'un nouveau livre avec un livre déjà présent dans la base
	 * 
	 * @param book livre à insérer dans la base
	 * @param otherBook référence d'un livre déjà présent
	 * @param jaccard collection des distances de jaccard
	 * @param index collection des index
	 * @param map1 index de book
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@SuppressWarnings("unchecked")
	public static void addJaccard(String book, DocumentReference otherBook, CollectionReference jaccard, CollectionReference index, Map<String, Long> map1) throws InterruptedException, ExecutionException {
		String name;
		String b1;
		String b2;
		String nameb2 = otherBook.getId();
		if(nameb2.compareTo(book) < 0) {
			b1 = nameb2;
			b2 = book;
		}
		else {
			if(nameb2.compareTo(book) > 0) {
			b1 = book;
			b2 = nameb2;
			
			}
			else {
				return;
			}
		}

		name = b1+"&"+b2;
		jaccard.document(name).set(new DistanceJaccard(b1, b2, Jaccard.computeJaccardMap(map1, (Map<String,Long>)index.document(nameb2).get().get().get("words"))));
	}

}
