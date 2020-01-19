package firebase;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.log4j.BasicConfigurator;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import cloud.FileIndex;
import cloud.Jaccard;

public class Firebase {
	
	public static void addIndex2Firebase(String directory) throws IOException, InterruptedException, ExecutionException {
		Stream<String> files = Jaccard.getOrderedFileNames(directory).stream();
		files.forEach(f -> {
			try {
				addIndex(f,FileIndex.createMapIndex(f));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
	
	public static void Connection() throws IOException {
		FileInputStream serviceAccount = new FileInputStream("./serviceAccount.json");
		
		FirebaseOptions options = new FirebaseOptions.Builder()
		  .setCredentials(GoogleCredentials.fromStream(serviceAccount))
		  .setDatabaseUrl("https://cloud-76b51.firebaseio.com")
		  .build();
		
		FirebaseApp.initializeApp(options);
	}
	
	public static void addIndex(String filename, Map<String,Long> occurences) throws InterruptedException, ExecutionException {
		Firestore db = FirestoreClient.getFirestore();
		// Add document data with auto-generated id.
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("words", occurences);
		db.collection("index").document(filename).set(data);
		//System.out.println("Added document with ID: " + addedDocRef.get().getId());
	}
	
	public static void deleteDocumentsFromCollection(String collection) throws IOException {
		Firestore db = FirestoreClient.getFirestore();
		CollectionReference documents = db.collection(collection);
		deleteCollection(documents,100);

	}
	
	static void deleteCollection(CollectionReference collection, int batchSize) throws IOException {
		
		  try {
		    // retrieve a small batch of documents to avoid out-of-memory errors
		    ApiFuture<QuerySnapshot> future = collection.limit(batchSize).get();
		    int deleted = 0;
		    // future.get() blocks on document retrieval
		    List<QueryDocumentSnapshot> documents = future.get().getDocuments();
		    for (QueryDocumentSnapshot document : documents) {
		      document.getReference().delete();
		      ++deleted;
		    }
		    if (deleted >= batchSize) {
		      // retrieve and delete another batch
		      deleteCollection(collection, batchSize);
		    }
		  } catch (Exception e) {
		    System.err.println("Error deleting collection : " + e.getMessage());
		  }
	}
	
	public List<String> getFilesStored() throws IOException, InterruptedException, ExecutionException {
		Connection();
		Firestore db = FirestoreClient.getFirestore();
		Iterable<DocumentReference> documents = db.collection("index").listDocuments();
		return StreamSupport.stream(documents.spliterator(), true)
		.map(docRef -> docRef.getId())
		.collect(Collectors.toList());
	}
	
	public static void searchWord(String word) throws IOException, InterruptedException, ExecutionException {
		Connection();
		Firestore db = FirestoreClient.getFirestore();
		Iterable<DocumentReference> documents = db.collection("index").listDocuments();
		
		StreamSupport.stream(documents.spliterator(), true)
		.filter(doc -> {
			try {
				return containsWord(doc, word);
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		})
		.forEach(doc -> {
			try {
				System.out.println(doc.get().get().get("name"));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
	
	public static void getIndex(String file) throws InterruptedException, ExecutionException {
		Firestore db = FirestoreClient.getFirestore();
		DocumentReference documents = db.collection("index").document(file);
		System.out.println(documents.get().get().get("words"));
	}
	
	@SuppressWarnings("unchecked")
	public static boolean containsWord(DocumentReference doc, String word) throws InterruptedException, ExecutionException {
		DocumentSnapshot snapshot = doc.get().get();
		return((Map<String,Long>)snapshot.get("words")).keySet().parallelStream()
				.anyMatch(str -> str.equals(word));
	}
	
	public static void count() throws IOException, InterruptedException, ExecutionException {
		Firestore db = FirestoreClient.getFirestore();
		ApiFuture<QuerySnapshot> documents = db.collection("jaccard").get();
		System.out.println(documents.get().getDocuments().size());

	}
	
	
	public static void main(String [] args) throws IOException, InterruptedException, ExecutionException {
		//BasicConfigurator.configure();
		Connection();
		long start = System.currentTimeMillis();
		//addIndex2Firebase("cloud/docs");
		//deleteDocumentsFromCollection("index");
		//count();
		//getIndex("46419.txt.utf-8");
//		Firebase2.addFiles2Firebase("cloud/docs");
		long elapsedTimeMillis = System.currentTimeMillis() - start;
		System.out.println("duree "+elapsedTimeMillis);
//		getFilesFromCollection("index");
//		searchWord("abercrombie");
	}

}
