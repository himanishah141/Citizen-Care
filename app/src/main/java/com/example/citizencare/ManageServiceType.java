package com.example.citizencare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ManageServiceType extends AppCompatActivity {

    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_service_type);

        //Get values in service type dropdown
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Service Type");
        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.dropdown_servicetype);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                List<String> suggestions = new ArrayList<>();
                for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                    String value = snapshot.getValue(String.class);
                    suggestions.add(value);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ManageServiceType.this, R.layout.list_item, suggestions);
                autoCompleteTextView.setAdapter(adapter);
                autoCompleteTextView.setOnItemClickListener((adapterView, view, i, l) -> Toast.makeText(getApplicationContext(), "Item: " + suggestions.get(i), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageServiceType.this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        });

        //Add Button
        Button add = findViewById(R.id.button_servicetype_add);
        EditText txt1 = findViewById(R.id.editText_servicetype_add);
        FirebaseDatabase mData = FirebaseDatabase.getInstance();
        DatabaseReference mRef = mData.getReference("Service Type");
        add.setOnClickListener(view -> {
            String data1 = txt1.getText().toString();
            if (!data1.isEmpty()) {
                String key = mRef.push().getKey();//Generate unique key
                //noinspection MismatchedQueryAndUpdateOfCollection
                Map<String, Object> map = new HashMap<>();
                map.put(key, data1); //set the new value with unique key
                mRef.child(Objects.requireNonNull(key)).setValue(data1);
                txt1.setText("");
                Toast.makeText(ManageServiceType.this, "Service Type Added Successfully", Toast.LENGTH_SHORT).show();
            } else {
                txt1.setError("No Text");
            }
        });

        //Get Key
        Button delete = findViewById(R.id.button_servicetype_delete);
        FirebaseDatabase database1 = FirebaseDatabase.getInstance();
        DatabaseReference myRef1 = database1.getReference("Service Type");
        myRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //noinspection MismatchedQueryAndUpdateOfCollection
                List<String> suggestions = new ArrayList<>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String value = snapshot1.getValue(String.class);
                    suggestions.add(value);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ManageServiceType.this, R.layout.list_item, suggestions);
                autoCompleteTextView.setAdapter(adapter);
                autoCompleteTextView.setOnItemClickListener((adapterView, view, i, l) -> {
                    String selectedItem=adapterView.getItemAtPosition(i).toString();
                    for (DataSnapshot snapshot1 :snapshot.getChildren()){
                        String item= snapshot1.getValue(String.class);
                        if(Objects.requireNonNull(item).equals(selectedItem)){
                            key= snapshot1.getKey();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageServiceType.this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        });

        //Delete Button
        delete.setOnClickListener(view -> {
            if (key!=null){
                myRef1.child((key)).removeValue();
                autoCompleteTextView.setText("");
                Toast.makeText(ManageServiceType.this, "Service Type Deleted Successfully", Toast.LENGTH_SHORT).show();
                key=null;
            }else {
                Toast.makeText(ManageServiceType.this, "Please Select An Item To Delete", Toast.LENGTH_SHORT).show();
            }
        });
    }
}