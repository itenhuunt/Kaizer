package team.diamond.kaizer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;

public class addStories extends AppCompatActivity {

    LinearLayout save_stories, save_stories2;
    EditText editTextTextMultiLine;

    public String inkognito = "no_name"; // присваиваем no name по умолчанию
    private SharedPreferences user_name_shared_preferences;   // обяъвляем  SharedPreferences

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kaizerver3-default-rtdb.firebaseio.com/");

    //все что связано с временем
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    String Date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_stories);


        user_name_shared_preferences = getSharedPreferences("teen_pref", MODE_PRIVATE); //обяъвляем приватный режим + прописываем ИМЯ в xml (.xml так и будет называться) + приватный режим
        inkognito = user_name_shared_preferences.getString("teen_name", inkognito);//  в этом xml опять пишем имя нашей белки  +  которое задаем значение teen_name

        find();


        // кнопка опубликовать рассказ
        save_stories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveStoriesClick();
            }
        });

        // ver 2
        save_stories2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveStoriesClick2();
            }
        });


    }


    //_________________________________________________________________________________________________________________________

    //команда сохранить рассказ
    private void saveStoriesClick() {

        final String myStoriesEdTxt = editTextTextMultiLine.getText().toString();
        if (myStoriesEdTxt.isEmpty()) {
            Toast.makeText(addStories.this, "пиши рассказ сучка )", Toast.LENGTH_SHORT).show();
        } else {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    // отображение времени
                    // final String currentTimestamp = String.valueOf(System.currentTimeMillis()).substring(0, 10);
                    //   databaseReference.child("рассказы маленьких сучек").child(inkognito).child(currentTimestamp).setValue(myStoriesEdTxt);

                    //  дата  пропечатывается
                    calendar = Calendar.getInstance();
                    simpleDateFormat = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
                    Date = simpleDateFormat.format(calendar.getTime());

                    databaseReference.child("рассказы маленьких сучек").child(inkognito).child(Date).setValue(myStoriesEdTxt);


                    Toast.makeText(addStories.this, R.string.download, Toast.LENGTH_SHORT).show();

                    editTextTextMultiLine.setText("");  // очищаем поле editText

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }


    //команда VER2
    private void saveStoriesClick2() {

        final String myStoriesEdTxt = editTextTextMultiLine.getText().toString();
        if (myStoriesEdTxt.isEmpty()) {
            Toast.makeText(addStories.this, "пиши рассказ сучка )", Toast.LENGTH_SHORT).show();
        } else {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    // отображение времени
                    // final String currentTimestamp = String.valueOf(System.currentTimeMillis()).substring(0, 10);
                    //   databaseReference.child("рассказы маленьких сучек").child(inkognito).child(currentTimestamp).setValue(myStoriesEdTxt);

                    //  дата  пропечатывается
                    calendar = Calendar.getInstance();
                    simpleDateFormat = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
                    Date = simpleDateFormat.format(calendar.getTime());

                    String uploadId = databaseReference.push().getKey(); // получаем рандомный ключ
                    databaseReference.child("рассказы маленьких сучек2").child(inkognito).child(uploadId).child("time").setValue(Date);
                    databaseReference.child("рассказы маленьких сучек2").child(inkognito).child(uploadId).child("stories").setValue(myStoriesEdTxt);
                    //  databaseReference.child("рассказы маленьких сучек").child(inkognito).child(uploadId).child("stories").setValue(myStoriesEdTxt);

                    //   databaseReference.child("рассказы маленьких сучек").child(inkognito).child(Date).setValue(myStoriesEdTxt);


                    Toast.makeText(addStories.this, R.string.download, Toast.LENGTH_SHORT).show();

                    editTextTextMultiLine.setText("");  // очищаем поле editText

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }


    // установки find
    private void find() {

        save_stories = findViewById(R.id.save_stories);
        save_stories2 = findViewById(R.id.save_stories2);
        editTextTextMultiLine = findViewById(R.id.editTextTextMultiLine);

    }


    public void onBackPressed() {
        try {
            Intent intent = new Intent(addStories.this, kaizerActivity.class); // try = попытка
            startActivity(intent);
            finish();
        } catch (Exception e) {  // catch = ловить
        }
    }


}