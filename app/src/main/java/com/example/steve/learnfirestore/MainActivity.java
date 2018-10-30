package com.example.steve.learnfirestore;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    // Log message tag
    private static final String TAG = "MainActivity";

    private static final String KEY_TITLE       = "title";
    private static final String KEY_DESCRIPTION ="description";


    private EditText title_input;
    private  EditText description_input;
    private TextView data_view;

    // fire store
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // used in loadNote
    private DocumentReference noteRef = db.collection("Notebook").document("First note");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialise text views
        title_input = findViewById(R.id.title_input);
        description_input = findViewById(R.id.description_input);
        data_view = findViewById(R.id.data_view);

    }


    @Override
    protected void onStart() {
        super.onStart();

        // adds real time checks to firebase for data_view
        noteRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                // 1: check if document exists
                if(documentSnapshot.exists()){

                    // get values from document directly using its field name
                    String title       = documentSnapshot.getString(KEY_TITLE);
                    String description = documentSnapshot.getString(KEY_DESCRIPTION);

                    // add data to data_view on screen
                    data_view.setText("Title: " + title + " \n " + "Description: " + description);
                }
            }
        });



    }

    // Called when save button is selected
    public void saveNote(View view) {

        // variable to get data from activity
        String title       = title_input.getText().toString();
        String desctiption = description_input.getText().toString();

        // map to hold data
        Map<String, Object> note = new HashMap<>();
        note.put(KEY_TITLE, title);
        note.put(KEY_DESCRIPTION,desctiption);

        // save to database
        noteRef.set(note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess");
                        Toast.makeText(MainActivity.this, "Note Saved", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {


                        Log.d(TAG, "onFailure");
                        Toast.makeText(MainActivity.this,"Note Failed to Save", Toast.LENGTH_SHORT).show();


                    }
                });

    }


    // get from database
    public void loadNote(View view){

        noteRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        // 1: check if document exists
                        if(documentSnapshot.exists()){

                            // get values from document directly using its field name
                            String title = documentSnapshot.getString(KEY_TITLE);
                            String description = documentSnapshot.getString(KEY_DESCRIPTION);

                            // add data to data_view on screen
                            data_view.setText("Title: " + title + " \n " + "Description: " + description);
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
    public void updateDescription(View view) {

        // get description from view
        String description = description_input.getText().toString();

        // create map to store description in note
        Map<String, Object> note = new HashMap<>();
        note.put(KEY_DESCRIPTION,description);

        // merges document rather than overriding as blank title field
        noteRef.set(note, SetOptions.merge());

        // alternative method using update
        // noteRef.update(note);

        // alternative method using update without hashmap
        // noteRef.update(KEY_DESCRIPTION, description);



    }
}
