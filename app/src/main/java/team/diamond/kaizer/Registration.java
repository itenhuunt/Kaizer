package team.diamond.kaizer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    EditText name, pass;
    String timeInkognitoId, emailTimeInkognitoId;
    AppCompatButton registerBtn;

    private SharedPreferences user_name_shared_preferences;   // обяъвляем  SharedPreferences
    private String inkognito = "no_name"; // присваиваем no name по умолчанию
    private String inkognitoid = "random";// присваиваем id = random  по умолчанию

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registerver2);

        hooks();
        //init
        firebaseAuth = FirebaseAuth.getInstance();

        user_name_shared_preferences = getSharedPreferences("teen_pref", MODE_PRIVATE); //обяъвляем приватный режим + прописываем ИМЯ в xml (.xml так и будет называться) + приватный режим
        inkognito = user_name_shared_preferences.getString("teen_name", inkognito);//  в этом xml опять пишем имя нашей белки  +  которое задаем значение teen_name
        inkognitoid = user_name_shared_preferences.getString("teen_id", inkognitoid);//  в этом xml опять пишем имя нашей белки  +  которое задаем значение teen_name

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering User...");

        //chek if user already logged in   хз как то не безопасно ... надо способ проверки использовать другой
        if (!MemoryData.getData(this).isEmpty()) {
            Intent intent = new Intent(Registration.this, kaizerActivity.class);
            intent.putExtra("mobile", MemoryData.getData(this));    //зачем вытаскиваем ??? телефон / имя / почту
            intent.putExtra("name", MemoryData.getName(this));    //  указываем ТОЧНОЕ ЗНАЧЕНИЕ
            startActivity(intent);
            finish();
        }

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // установить почту как inkognito + time б а имя отдельно передавать
                timeInkognitoId = String.valueOf(System.currentTimeMillis());  // берем милисикунды
                emailTimeInkognitoId = "in" + timeInkognitoId + "@gmail.com";
                String nameUnique = name.getText().toString().trim();
                String password = pass.getText().toString().trim();
                //validate
                if (!Patterns.EMAIL_ADDRESS.matcher(emailTimeInkognitoId).matches()) {
                    // set error and focus to email edittext
                    name.setError("неправильная почта");
                    name.setFocusable(true);
                } else if (password.length() < 6) {
                    // set error and focus to password edittext
                    pass.setError("пароль должен быть больше 6 символов");
                    pass.setFocusable(true);
                } else {
                    registerUser(nameUnique, emailTimeInkognitoId, password);
                }
            }
        });


    }

    private void registerUser(String nameUnique, String emailTimeInkognitoId, String password) {
        // email and password pattern is valid show progress dialog and start registering user
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(emailTimeInkognitoId, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //single in success, dismiss and start register activity
                            progressDialog.dismiss();

                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            //get user email and uid auth
                            String email = user.getEmail();
                            String uid = user.getUid();
                            //when user is registered store user info in firebase realtime data base too
                            //using HashMap
                            HashMap<Object, String> hashMap = new HashMap<>();
                            //put info in hasmap
                            hashMap.put("email", email);
                            hashMap.put("uid", uid);
                            hashMap.put("name", nameUnique); // will add alter e/g/ edit profile
                            hashMap.put("onlineStatus", "online");
                            hashMap.put("typingTo", "noOne");
                            hashMap.put("lvl", "");
                            hashMap.put("pass", password);
                            hashMap.put("profile_pic", "");
                            hashMap.put("inf_admin_user", "");

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            //patch to store user data named "Users"
                            DatabaseReference reference = database.getReference("users");
                            //put data whit in hashmap in database
                            reference.child(uid).setValue(hashMap);

                            //   закидываем целые числа
                            Map<String, Integer> myMap = new HashMap<String, Integer>();
                            myMap.put("lvl", 0);
                            myMap.put("balance", 0);
                            //patch to store user data named "Users"
                            DatabaseReference reference2 = database.getReference("usersInt");
                            //put data whit in hashmap in database
                            reference2.child(uid).setValue(myMap);


                            Toast.makeText(Registration.this, "Registred...\n", Toast.LENGTH_SHORT).show();
                            //save name to memory      СОХРАНЯЕМ ВСЕ ЧТО НУЖНО КОГДА РЕГИСТРАЦИЯ ПРОШЛА УСПЕШНО
                            MemoryData.saveName(nameUnique, Registration.this);
                            //save pass to memory
                            MemoryData.saveData(password, Registration.this);

                            SharedPreferences.Editor editor = user_name_shared_preferences.edit(); //SharedPreferences edit редактируем ОЧКИ (счет/count)
                            editor.putString("teen_name", nameUnique); // положить значение в ...
                            editor.putString("teen_id", uid); //
                            editor.commit();//каммит фиксируем ) кам кам  сак   кам

                            startActivity(new Intent(Registration.this, kaizerActivity.class));
                            finish();
                        } else {
                            // if sing in fails, display a message  to the user
                            progressDialog.dismiss();
                            Toast.makeText(Registration.this, "Aut failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Registration.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void hooks() {
        name = findViewById(R.id.r_nametxt);
        pass = findViewById(R.id.r_passwtxt);
        registerBtn = findViewById(R.id.r_registredBtn);
    }


}


//удалил  т.к. оно не используется на втором слое
// нет стоп моэно вытащить их насколько я понял  возможно
//                                intent.putExtra("mobile", mobileTxt);    //зачем вытаскиваем ??? телефон / имя / почту
//                                intent.putExtra("name", nameTxt);    //  указываем ТОЧНОЕ ЗНАЧЕНИЕ
//                                intent.putExtra("email", emailTxt);      //  т.к. оно будет вытаскиваться в MainActivity.class