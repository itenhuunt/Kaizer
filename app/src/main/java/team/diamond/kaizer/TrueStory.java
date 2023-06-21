package team.diamond.kaizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import team.diamond.kaizer.adapters.AdapterStoryFree;
import team.diamond.kaizer.models.ModelStoryFree;

public class TrueStory extends AppCompatActivity {

    ImageView addTrueStory;
    RecyclerView trueStoryRv;

    List<ModelStoryFree> listModelStoryFree;
    AdapterStoryFree adapterStoryFree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_true_story);

        hooks();

        //recycler view and its properties
        LinearLayoutManager layoutManager = new LinearLayoutManager(TrueStory.this);
        //show newest post first, for this load from last
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //set layout to recyclerview
        trueStoryRv.setLayoutManager(layoutManager);
        //init post list
        listModelStoryFree = new ArrayList<>();
        // загрузка историй в recycleView
        loadStoryFree();

        addTrueStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(TrueStory.this, TrueStoryAdd.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    //empty
                }
            }
        });

    }//____________________________________________

    private void loadStoryFree() {
        //patch of all posts
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("StoryFree");
        //get all data from this ref
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listModelStoryFree.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelStoryFree modelStoryFree = ds.getValue(ModelStoryFree.class);
                    listModelStoryFree.add(modelStoryFree);
                    //adapter
                    adapterStoryFree = new AdapterStoryFree(TrueStory.this, listModelStoryFree);
                    //set adapter to recyclerview
                    trueStoryRv.setAdapter(adapterStoryFree);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //in case error
                Toast.makeText(TrueStory.this, " " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


// так то по идее можно оставить проверку пользователя на всякий случай
//    private void checkUserStatus() {
//        // get current user
//        FirebaseUser user = firebaseAuth.getCurrentUser();
//        if (user != null) {
//            //user is single in stay here
//
//            //set email of logged in user
//            // mProfileTv.setText(user.getEmail());
//
//        } else {
//            //user not single in, go to main activity
//            startActivity(new Intent(getActivity(), team.diamond.firebase2.MainActivity.class));
//            getActivity().finish();
//        }
//    }

    private void hooks() {
        addTrueStory = findViewById(R.id.addTrueStory);
        trueStoryRv = findViewById(R.id.trueStoryRv);
    }

    //кнопка назад
    public void onBackPressed() {
        try {
            Intent intent = new Intent(TrueStory.this, kaizerActivity.class); // try = попытка
            startActivity(intent);
            finish();
        } catch (Exception e) {  // catch = ловить
        }
    }
}