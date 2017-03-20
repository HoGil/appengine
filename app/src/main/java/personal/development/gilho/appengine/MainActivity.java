package personal.development.gilho.appengine;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get ListView object from xml
        final ListView listView = (ListView)findViewById(R.id.listView);

        // create new adapter
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1);

        // assign adapter to ListViwe
        listView.setAdapter(adapter);

        // connect to Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // get a reference to the todoItems child items in the database
        final DatabaseReference myRef = database.getReference("todoItems");

        // assign a listener to detect changes to the child items
        // of the database reference
        myRef.addChildEventListener(new ChildEventListener() {

            // this function is called once for each child that exists
            // when the listener is added. then it is called each time
            // a new child is added
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                String value = dataSnapshot.getValue(String.class);
                adapter.add(value);
            }

            // this function is called each time a child item is removed
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                adapter.remove(value);
            }

            // the fullowing functions are also required in ChildEventListener implementation
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName){};
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName){};

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("TAG: ", "Failed to read value. ", error.toException());
            }

        });


        // add items via the Button and EditText at the bottom of the window
        final EditText text = (EditText)findViewById(R.id.todoText);
        final Button button = (Button)findViewById(R.id.addButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create a new child with an auto-generated ID
                DatabaseReference childRef = myRef.push();

                // set the childs data to the value passed in from the textbox
                childRef.setValue(text.getText().toString());
            }
        });

        // delete items when clicked
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Query myQuery = myRef.orderByValue().equalTo((String)listView.getItemAtPosition(position));
                myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                            firstChild.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                })

            ;}

        })

    ;}


}
