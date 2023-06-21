package team.diamond.kaizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class profileBalance extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference2;
    DatabaseReference databaseRefBalanceInf;

    private SharedPreferences user_name_shared_preferences;
    private String inkognito,inkognitoid;

    TextView profile_id, balance, you_payment_card_txt;
    LinearLayout requestpayment;
    //progress dialog
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_balance);

        user_name_shared_preferences = getSharedPreferences("teen_pref", MODE_PRIVATE); //обяъвляем приватный режим для ОЧКОВ + прописываем ИМЯ в xml (чxml так и будет называться) + приватный режим
        inkognito = user_name_shared_preferences.getString("teen_name", inkognito);//  в этом xml опять пишем имя нашей белки  +  которое задаем значение teen_name
        inkognitoid = user_name_shared_preferences.getString("teen_id", inkognitoid);//  в этом xml опять пишем имя нашей белки  +  которое задаем значение teen_name


        hooks();
        //init firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");
        //load balance inf
        loadBalanceInf();
        //init progress dialog
        pd = new ProgressDialog(this);
        //inf payout information
        payoutinformationtxt();

        requestpayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestpaymentClick();
            }
        });


    }

    private void loadBalanceInf() {
        databaseRefBalanceInf = firebaseDatabase.getReference("usersInt").child(inkognitoid).child("balance"); // вариант 3  типо прописали ссылку + родительский католог : что напротив него написано
        // чтение из базы ValueEventListener
        databaseRefBalanceInf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot2) {
                //read  profile_pic
                Integer infBalance = snapshot2.getValue(Integer.class);
                balance.setText("" + infBalance);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    //___________________________________________________________________________

    private void payoutinformationtxt() {
        databaseReference2 = firebaseDatabase.getReference("users").child(inkognitoid).child("payment_card"); // вариант 3  типо прописали ссылку + родительский католог : что напротив него написано
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String payoutinftxt = snapshot.getValue(String.class);
                you_payment_card_txt.setText(payoutinftxt);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void requestpaymentClick() {
        //  option show dialog
        String options[] = {"Вывести баланс", "Custom"};
        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //set title
        builder.setTitle("Выберите действие");
        // set item to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //handle dialog item clicks
                if (which == 0) {
                    // edit profile clicked
                    pd.setMessage("Update");
                    outPay("payment_card");
                } else if (which == 1) {
                    // edit custom
                    pd.setMessage("Updating cover");

                }
            }
        });
        //create and show dialog
        builder.create().show();
    }


    private void outPay(String key) {
        //custom dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Вывести");
        //set layout of dialog
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10, 10, 10, 10);
        //add edit test
        EditText editText = new EditText(this);
        editText.setHint("укажите номер карты или другой способ ");//hint e.g. Edit name OR Edit phone
        linearLayout.addView(editText);

        builder.setView(linearLayout);

        //add Buttons in dialog to Update
        builder.setPositiveButton("Вывести", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //input text from edit text
                String value = editText.getText().toString().trim();
                //validate if user has entered something or not
                if (!TextUtils.isEmpty(value)) {
                    pd.show();
                    HashMap<String, Object> result = new HashMap<>();
                    result.put(key, value);

                    databaseReference.child(inkognitoid).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    //updated, dismiss progress
                                    pd.dismiss();
                                    Toast.makeText(profileBalance.this, "запрос принят", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //failed, dismiss progress, get and show error message
                                    pd.dismiss();
                                    Toast.makeText(profileBalance.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(profileBalance.this, "Update", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //add Buttons in dialog to Cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //create and show dialog
        builder.create().show();
    }


    private void hooks() {
        profile_id = findViewById(R.id.profile_id);
        balance = findViewById(R.id.balance);
        requestpayment = findViewById(R.id.requestpayment);
        you_payment_card_txt = findViewById(R.id.you_payment_card_txt);

    }


    public void onBackPressed() {
        try {
            Intent intent = new Intent(profileBalance.this, profileId.class); // try = попытка
            startActivity(intent);
            finish();
        } catch (Exception e) {  // catch = ловить
        }
    }


}

