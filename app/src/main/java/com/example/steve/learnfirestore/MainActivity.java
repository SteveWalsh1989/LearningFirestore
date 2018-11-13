package com.example.steve.learnfirestore;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    // Log message tag
    private static final String TAG = "MainActivity";

    private static final String KEY_NAME  = "name";
    private static final String KEY_EMAIL ="email";


    private EditText client_name_input;
    private  EditText client_email_input;
    private  EditText filter_input;

    private TextView data_view;

    // fire store
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // used in loadNote
    private DocumentReference clientRef = db.collection("Notebook").document("SampleClient");
    private CollectionReference clietListRef = db.collection("Notebook");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialise text views
        client_name_input = findViewById(R.id.title_input);
        client_email_input = findViewById(R.id.description_input);
        filter_input = findViewById(R.id.filter_input);
        data_view = findViewById(R.id.data_view);

    }


    @Override
    protected void onStart() {
        super.onStart();

        // adds real time checks to firebase for data_view
        clietListRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {

                String data = "";

                // for each document in the collection
                for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {

                    // create client from document
                    client client = documentSnapshot.toObject(client.class);

                    // set client document based on the document ID
                    client.setDocumentID(documentSnapshot.getId());

                    String documentId = client.getDocumentID();
                    String name = client.getName();
                    String email = client.getEmail();

                    data += "\nID: " + documentId + "\nName: " + name + "\nEmail: " + email + "\n\n";
                }

                data_view.setText(data);
            }
       });

    }

    // Called when save button is selected
    public void saveClient(View view) {

        // variable to get data from activity
        String client_name  = client_name_input.getText().toString();
        String client_email = client_email_input.getText().toString();

        // create new client
        client client = new client(client_name,client_email );

        // save client to database
        clientRef.set(client)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d(TAG, "onFailure");

                    }
                });

    }


    public void saveClient_usingCollection(View view) {
        // variable to get data from activity
        String client_name  = client_name_input.getText().toString();
        String client_email = client_email_input.getText().toString();


        // checks if there was anything in field first
        if (filter_input.length() == 0 ) {
            filter_input.setText("0");

        }

        // gets value from filter input and parses to int
        int filter = Integer.parseInt(filter_input.getText().toString());


        // create new client
        client client = new client(client_name, client_email, filter);



        // add client to collection - could also add onCSuccessLister like saveClient
        clietListRef.add(client);
    }




    public void loadClients(View view){

        clietListRef.whereGreaterThanOrEqualTo("filter", 2)
                .orderBy("filter",Query.Direction.DESCENDING)
                .limit(3)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {

                        // string to store all client data
                        String data = "";

                        // loop through each document from returned query
                        for(QueryDocumentSnapshot documentSnapshots : querySnapshot) {


                            client client = documentSnapshots.toObject(client.class);

                            String name = client.getName();
                            String email = client.getEmail();
                            String documentID = client.getDocumentID();
                            int filter = client.getFilter();

                            data += "Name: " + name      + "\n" + "Email: " + email + "\n" +
                                    "ID: "   + documentID +"\n" +"Filter: " + filter + "\n\n";

                        }

                        // add data to textview
                        data_view.setText(data);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this,"Note Failed to load", Toast.LENGTH_SHORT).show();

                    }
                });


    }


    // get from database
    public void loadClient(View view){

        clientRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        // 1: check if document exists
                        if(documentSnapshot.exists()){

                           // saves document to client object
                           client client = documentSnapshot.toObject(client.class);

                            // add data to data_view on screen
                            data_view.setText("Name: " + client.getName() + " \n " + "Email: " + client.getEmail());
                        } else {

                            // if document does not exist
                            Toast.makeText(MainActivity.this,"Document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this,"Note Failed to load", Toast.LENGTH_SHORT).show();

                    }
                });




    }

    // updates single field of document without overriding other fields
    public void updateEmail(View view) {

        // get description from view
        String client_email = client_email_input.getText().toString();

        // alternative method using update without hashmap
        clientRef.update(KEY_EMAIL, client_email);



    }
}
