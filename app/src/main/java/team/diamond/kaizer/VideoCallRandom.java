package team.diamond.kaizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class VideoCallRandom extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase database;

    DatabaseReference firebaseRef;

    boolean isOkay = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call_random);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();



        String username = auth.getUid();

        firebaseRef = FirebaseDatabase.getInstance().getReference().child("randomCall").child(username);

        //создаем ссылку на видео чат
        database.getReference().child("randomCall")
                .orderByChild("status")
                .equalTo(0).limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getChildrenCount() > 0) {
                            isOkay = true;
                            // свободная комната
                            for (DataSnapshot childSnap : snapshot.getChildren()) {
                                database.getReference()
                                        .child("randomCall")
                                        .child(childSnap.getKey())
                                        .child("incoming")
                                        .setValue(username);
                                database.getReference()
                                        .child("randomCall")
                                        .child(childSnap.getKey())
                                        .child("status")
                                        .setValue(1);
                                Intent intent = new Intent(VideoCallRandom.this, OnCallRandom.class);
                                String incoming = childSnap.child("incoming").getValue(String.class);
                                String createdBy = childSnap.child("createdBy").getValue(String.class);
                                boolean isAvailable = childSnap.child("isAvailable").getValue(Boolean.class);  // isAvailable = доступен
                                intent.putExtra("username", username);
                                intent.putExtra("incoming", incoming);
                                intent.putExtra("createdBy", createdBy);
                                intent.putExtra("isAvailable", isAvailable);
                                startActivity(intent);
                                finish();

                            }
                        } else {
                            // не свободная комната    Not Available

                            HashMap<String, Object> room = new HashMap<>();
                            room.put("incoming", username);
                            room.put("createdBy", username);
                            room.put("isAvailable", true);
                            room.put("status", 0);

                            database.getReference()
                                    .child("randomCall")
                                    .child(username)
                                    .setValue(room).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            database.getReference()
                                                    .child("randomCall")
                                                    .child(username).addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (snapshot.child("status").exists()) {
                                                                if (snapshot.child("status").getValue(Integer.class) == 1) {

                                                                    if (isOkay)
                                                                        return;

                                                                    isOkay = true;
                                                                    Intent intent = new Intent(VideoCallRandom.this, OnCallRandom.class);
                                                                    String incoming = snapshot.child("incoming").getValue(String.class);
                                                                    String createdBy = snapshot.child("createdBy").getValue(String.class);
                                                                    boolean isAvailable = snapshot.child("isAvailable").getValue(Boolean.class);  // isAvailable = доступен
                                                                    intent.putExtra("username", username);
                                                                    intent.putExtra("incoming", incoming);
                                                                    intent.putExtra("createdBy", createdBy);
                                                                    intent.putExtra("isAvailable", isAvailable);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                        }
                                    });


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }


    public void onBackPressed() {
        try {
           // firebaseRef.child(createdBy).setValue(null);
            firebaseRef.setValue(null);


            Intent intent = new Intent(VideoCallRandom.this, kaizerActivity.class); // try = попытка
            startActivity(intent);
            finish();
        } catch (Exception e) {  // catch = ловить
        }
    }

}