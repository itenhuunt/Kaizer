package team.diamond.kaizer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoadingQuest extends AppCompatActivity {


    public static ArrayList<Modelclass> list;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_quest);


        list = new ArrayList<>();

        //         тут прописываем ТОЖЕ самое что на сайте
        databaseReference = FirebaseDatabase.getInstance().getReference("QuestBelka");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Modelclass modelclass = dataSnapshot.getValue(Modelclass.class);
                    list.add(modelclass);
                }

                Intent intent = new Intent(LoadingQuest.this,startBelka.class);
                startActivity(intent);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //используем databaseReference
//        list.add(new Modelclass("3wef","gfgdfgd","gfhfghfg","gdfghdfg"));
//        list.add(new Modelclass("3wef111","gfgdfgd111","gfhfghfg111","gdfghdfg111"));
//        list.add(new Modelclass("3wef222","gfgdfgd222","gfhfghfg222","gdfghdfg222"));

        //главная страница + код задержки через некоторое время
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
           //     startActivity(new Intent(MainActivity.this,universal_qestions.class));


            }
        }, 850);

    }
}