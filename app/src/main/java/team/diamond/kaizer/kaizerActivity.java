package team.diamond.kaizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class kaizerActivity extends AppCompatActivity {

    private SharedPreferences user_name_shared_preferences;
    private String inkognito, inkognitoid;

    ImageView profileImg;
    LottieAnimationView videoStream;
    TextView infAll, addPost, allpost;

    LinearLayout basictest, readStories, add, job, recentlyJoined;
    ProgressDialog progressDialog;

    FirebaseDatabase database; //  сама Firebase
    DatabaseReference refImgProfile, userDbRef;    //ссылки в Firebase on Img profile pic + ключ


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kaizer_main);

        user_name_shared_preferences = getSharedPreferences("teen_pref", MODE_PRIVATE); //обяъвляем приватный режим для ОЧКОВ + прописываем ИМЯ в xml (чxml так и будет называться) + приватный режим
        inkognito = user_name_shared_preferences.getString("teen_name", inkognito);// пишем ВПЕРЕДИ  т.к. код исполняется по порядку + в этом xml опять пишем наши очки под именем которое задаем save_key_count
        inkognitoid = user_name_shared_preferences.getString("teen_id", inkognitoid);//  в этом xml опять пишем имя нашей белки  +  которое задаем значение teen_name


        hooks();
        //init database
        database = FirebaseDatabase.getInstance();
        //loading Image to profile pic
        loadImgProfile();
        //загрузка инфы в случае чего
        loadInfAll();


        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        basictest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(kaizerActivity.this, AllTests.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    //empty
                }
            }
        });


        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(kaizerActivity.this, profileId.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    //empty
                }
            }
        });

        readStories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(kaizerActivity.this, TrueStory.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    //empty
                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(kaizerActivity.this, Help_standart.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    //empty
                }
            }
        });


        job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(kaizerActivity.this, job.class);
                startActivity(intent);
                finish();
            }
        });


        recentlyJoined.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(kaizerActivity.this, AllUsers.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    //empty
                }
            }
        });


        videoStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(kaizerActivity.this, AllVideoStarts.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    //empty
                }

            }
        });




    }

    //загружаем  информацию для всех в случае чего
    //ОБНОВЛЕНИЕ ИЛИ ЕЩЕ ЧТО НИТЬ
    private void loadInfAll() {
        DatabaseReference experients = database.getReference("Inf").child("all"); // вариант 3  типо прописали ссылку + родительский католог : что напротив него написано
        // чтение из базы ValueEventListener
        experients.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String m = snapshot.getValue(String.class);
                infAll.setText(m);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }

    //загрузка аватарки для перехода к профилю
    private void loadImgProfile() {
        refImgProfile = database.getReference("users").child(inkognitoid).child("profile_pic"); // вариант 3  типо прописали ссылку + родительский католог : что напротив него написано
        // чтение из базы ValueEventListener   В РЕАЛЬНОМ ВРЕМЕНИ !!!  --->  addValueEventListener
        refImgProfile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //read  profile_pic
                String imgProfile = snapshot.getValue(String.class);
                //load Img to profile_pic
                try {
                    //if image is received then set
                    Picasso.get().load(imgProfile).into(profileImg);
                } catch (Exception e) {
                    //if there is any exception while getting image then set default
                    Picasso.get().load(R.drawable.man).into(profileImg);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void hooks() {
        profileImg = findViewById(R.id.profile);
        basictest = findViewById(R.id.basictest);
        job = findViewById(R.id.job);
        readStories = findViewById(R.id.readStories);
        add = findViewById(R.id.add);
        recentlyJoined = findViewById(R.id.recentlyJoined);
        videoStream = findViewById(R.id.videoStream);
        infAll = findViewById(R.id.Infall);
        addPost = findViewById(R.id.addPost);
        allpost = findViewById(R.id.allpost);

    }


}