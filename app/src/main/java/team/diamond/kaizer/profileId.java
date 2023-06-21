package team.diamond.kaizer;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class profileId extends AppCompatActivity {


    private SharedPreferences user_name_shared_preferences;
    private String inkognito, inkognitoid;

    FirebaseDatabase firebaseDatabase; //  сама Firebase
    DatabaseReference refImgProfile;    //ссылки в Firebase on Img profile pic
    DatabaseReference databaseRefBalanceInf;
    DatabaseReference databaseRefPaidInf;

    TextView profile_id, balance, paindalbuminfo, lvlProfile, infadminuser;
    LinearLayout foto_public, stories, answerstest, profile_balance, foto_paid, showAva;
    ImageView profileImage, editProfile;
    Dialog dialogavaShow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_id);

        user_name_shared_preferences = getSharedPreferences("teen_pref", MODE_PRIVATE); //обяъвляем приватный режим для ОЧКОВ + прописываем ИМЯ в xml (чxml так и будет называться) + приватный режим
        inkognito = user_name_shared_preferences.getString("teen_name", inkognito);//  в этом xml опять пишем имя нашей белки  +  которое задаем значение teen_name
        inkognitoid = user_name_shared_preferences.getString("teen_id", inkognitoid);//  в этом xml опять пишем имя нашей белки  +  которое задаем значение teen_name

        hooks();
        //init database
        firebaseDatabase = FirebaseDatabase.getInstance();
        //loading Image to profile pic
        loadImgProfile();
        //load balance inf
        loadBalanceInf();
        //download the price of a paid album
        loadpaidalbuminf1();
        //download lvl profile
        loadlvlprofile();
        //download info user - admin
        loadinfouseradmin();
        //set name profile pic
        profile_id.setText(inkognito);
        //edit profile
        editProfile();


        // balance.setText(getString(R.string.pay)+" " + payment_for_basic_test);
        //balance.setText(" " + payments);

        foto_public.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(profileId.this, foto_free.class);
                startActivity(intent);
                finish();
            }
        });

        foto_paid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //       Intent intent = new Intent(profileId.this, team.diamond.kaizer.foto2.foto_paid.class);
                Intent intent = new Intent(profileId.this, foto_paid2.class);
                startActivity(intent);
                finish();
            }
        });




        profile_balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(profileId.this, profileBalance.class);
                startActivity(intent);
                finish();
            }
        });

        //показываем фото аватарки
        showAva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogavaShow.show();
            }
        });


        //___________________________________________________
        //Вызов диалогового окна при клике по аве
        dialogavaShow = new Dialog(this);// создаем диалоговое окно
        dialogavaShow.requestWindowFeature(Window.FEATURE_NO_TITLE);// скрываем заголовок
        dialogavaShow.setContentView(R.layout.dialogavashow); // путь к макету диалогово окна
        dialogavaShow.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //прозрачный фон жиалогового окна
        dialogavaShow.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, // развернуть на весь экран
                WindowManager.LayoutParams.MATCH_PARENT);
        dialogavaShow.setCancelable(false);// окно нельзя закрыть кнопкой назад

        //устанавливаем картинку в диалоговое окно  начало
        ImageView previewing = (ImageView) dialogavaShow.findViewById(R.id.avaimg);
        // чтение из базы ValueEventListener
        refImgProfile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //read  profile_pic
                String imgProfile = snapshot.getValue(String.class);
                //load Img to profile_pic
                try {
                    //if image is received then set
                    Picasso.get().load(imgProfile).into(previewing);

                } catch (Exception e) {
                    //if there is any exception while getting image then set default
                    Picasso.get().load(R.drawable.man).into(previewing);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        //текст для preview start
        TextView textView2 = (TextView) dialogavaShow.findViewById(R.id.description);
        textView2.setText(R.string.description);
        //текст для preview end


        //кнопка закрывает диалоговое окно начало
        TextView btnclose2 = (TextView) dialogavaShow.findViewById(R.id.btnclose);
        btnclose2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    dialogavaShow.dismiss();  // dialog close

                } catch (Exception e) {
                    //empty
                }
            }
        });
        //кнопка закрывает диалоговое окно конец


        //___________________________________________________


    }

    private void editProfile() {
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(profileId.this, AvatarkaEdit.class);
                startActivity(intent);
                finish();

            }
        });
    }

    private void loadinfouseradmin() {
        databaseRefPaidInf = firebaseDatabase.getReference("users").child(inkognitoid).child("inf_admin_user"); // вариант 3  типо прописали ссылку + родительский католог : что напротив него написано
        // чтение из базы ValueEventListener
        databaseRefPaidInf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot2) {
                try {
                    String inf_admin_user = snapshot2.getValue(String.class);
                    infadminuser.setText(" " + inf_admin_user);
                } catch (Exception e) {
                    infadminuser.setText(" ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                balance.setText("error");
            }
        });
    }


    //загрузка цены альбома   ОПРОС 1 раз ---  addListenerForSingleValueEvent
    private void loadpaidalbuminf1() {
        databaseRefPaidInf = firebaseDatabase.getReference("users").child(inkognitoid).child("paid_album"); // вариант 3  типо прописали ссылку + родительский католог : что напротив него написано
        // чтение из базы ValueEventListener
        databaseRefPaidInf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot2) {
                try {
                    String infpaindalbum = snapshot2.getValue(String.class);

                    paindalbuminfo.setText("цена доступа " + infpaindalbum);
                } catch (Exception e) {
                    paindalbuminfo.setText("error");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                balance.setText("error ");
            }
        });
    }

    //загрузка цены альбома   ОПРОС в реальном времени
    private void loadpaidalbuminf2() {
        databaseRefPaidInf = firebaseDatabase.getReference("users").child(inkognitoid).child("paid_album"); // вариант 3  типо прописали ссылку + родительский католог : что напротив него написано
        // чтение из базы ValueEventListener
        databaseRefPaidInf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot2) {
                try {
                    String infpaindalbum = snapshot2.getValue(String.class);

                    paindalbuminfo.setText("цена доступа " + infpaindalbum);
                } catch (Exception e) {
                    paindalbuminfo.setText("error");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                balance.setText("error ");
            }
        });
    }


    //загрузка lvl
    private void loadlvlprofile() {
        databaseRefBalanceInf = firebaseDatabase.getReference("usersInt").child(inkognitoid).child("lvl"); // вариант 3  типо прописали ссылку + родительский католог : что напротив него написано
        // чтение из базы ValueEventListener
        databaseRefBalanceInf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot2) {
                try {
                    Integer infLvl = snapshot2.getValue(Integer.class);
                    lvlProfile.setText("" + infLvl);
                } catch (Exception e) {
                    lvlProfile.setText("error");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                balance.setText("error");
            }
        });
    }

    //загрузка баланса
    private void loadBalanceInf() {
        databaseRefBalanceInf = firebaseDatabase.getReference("usersInt").child(inkognitoid).child("balance"); // вариант 3  типо прописали ссылку + родительский католог : что напротив него написано
        // чтение из базы ValueEventListener
        databaseRefBalanceInf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot2) {
                try {
                    Integer infBalance = snapshot2.getValue(Integer.class);
                    balance.setText("" + infBalance);
                } catch (Exception e) {
                    balance.setText("error");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                balance.setText("error");
            }
        });
    }

    //загрузка аватарки
    private void loadImgProfile() {
        refImgProfile = firebaseDatabase.getReference("users").child(inkognitoid).child("profile_pic"); // вариант 3  типо прописали ссылку + родительский католог : что напротив него написано
        // чтение из базы ValueEventListener
        refImgProfile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //read  profile_pic
                String imgProfile = snapshot.getValue(String.class);
                //load Img to profile_pic
                try {
                    //if image is received then set
                    Picasso.get().load(imgProfile).into(profileImage);

                } catch (Exception e) {
                    //if there is any exception while getting image then set default
                    Picasso.get().load(R.drawable.man).into(profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void hooks() {
        profile_id = findViewById(R.id.profile_id);
        foto_public = findViewById(R.id.foto_public);
        stories = findViewById(R.id.stories);
        answerstest = findViewById(R.id.answerstest);
        balance = findViewById(R.id.balance);
        profile_balance = findViewById(R.id.profile_balance);
        profileImage = findViewById(R.id.profileImage);
        foto_paid = findViewById(R.id.foto_paid);
        paindalbuminfo = findViewById(R.id.paindalbuminfo);
        lvlProfile = findViewById(R.id.lvlProfile);
        infadminuser = findViewById(R.id.infadminuser);
        editProfile = findViewById(R.id.editProfile);
        showAva = findViewById(R.id.showAva);

    }


    public void onBackPressed() {
        try {
            Intent intent = new Intent(profileId.this, kaizerActivity.class); // try = попытка
            startActivity(intent);
            finish();
        } catch (Exception e) {  // catch = ловить
        }
    }


}