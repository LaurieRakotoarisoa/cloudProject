package firebase;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import cloud.FileIndex;
import cloud.Jaccard;

public class Firebase {
	
	public static void addIndex2Firebase(String directory) throws IOException {
		Connection();
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
		data.put("name", filename);
		data.put("words", occurences);
		ApiFuture<DocumentReference> addedDocRef = db.collection("index").add(data);
		System.out.println("Added document with ID: " + addedDocRef.get().getId());
	}
	
	public static void main(String [] args) throws IOException {
		addIndex2Firebase("cloud/docs");
	}

}