package team.diamond.kaizer.foto2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import team.diamond.kaizer.R;
import team.diamond.kaizer.profileId;

public class foto_paid2 extends AppCompatActivity implements ImageAdapter.OnItemClickListener {

    private SharedPreferences user_name_shared_preferences;
    public String inkognito;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference2;

    TextView paidflbumetxt;
    Button addpaidalbume;
    //progress dialog
    ProgressDialog pd;

    //____________________________ 1

    private static final int PICK_image_Request = 1;

    private EditText mEditTextFileName;
    private ImageView prewImage, addImage;
    private ProgressBar mProgressBar;

    private Uri mImageUri;

    private StorageReference mStorageRef;
    private StorageReference mStorageRefwtf; // указываем свою то что у тебя есть нычка
    private DatabaseReference mDatabaseRef;


    private StorageTask mUploadTask;

    //_____________________________   2
    private RecyclerView paidAlbumeRv;
    private ImageAdapter mAdapter;

    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef2;
    private ValueEventListener mDBListener;

    private List<Upload> mUploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.foto_paid2);

        user_name_shared_preferences = getSharedPreferences("teen_pref", MODE_PRIVATE); //обяъвляем приватный режим для ОЧКОВ + прописываем ИМЯ в xml (чxml так и будет называться) + приватный режим
        inkognito = user_name_shared_preferences.getString("teen_name", inkognito);// пишем ВПЕРЕДИ  т.к. код исполняется по порядку + в этом xml опять пишем наши очки под именем которое задаем save_key_count

        hooks();
        //init firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");

        //указываем путь в fire storage
        mStorageRef = FirebaseStorage.getInstance().getReference("paidAlbum").child(inkognito);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("paidAlbum").child(inkognito);


        //init progress dialog
        pd = new ProgressDialog(this);

        paidalbuminf();


        paidAlbumeRv.setHasFixedSize(true);
        paidAlbumeRv.setLayoutManager(new LinearLayoutManager(this));

        mUploads = new ArrayList<>();

        mAdapter = new ImageAdapter(foto_paid2.this, mUploads);

        paidAlbumeRv.setAdapter(mAdapter);

        mAdapter.setOnClickListener(foto_paid2.this);

        mStorage = FirebaseStorage.getInstance();
        // указываем путь к платному альбому
        mDatabaseRef2 = FirebaseDatabase.getInstance().getReference("paidAlbum").child(inkognito);

        mDBListener = mDatabaseRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUploads.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    upload.setKey(postSnapshot.getKey());
                    mUploads.add(upload);
                }

                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(foto_paid2.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();  // 3:10
            }
        });

        // кнопка выбрать изображение  начало
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
                uploadFile2();

            }
        });
        // кнопка выбрать изображение  конец

        addpaidalbume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPaid();
            }
        });


    }
    //______________________________

    //описание команды выбери фото
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_image_Request);
    }


    // хз как оно относиться к кому но оно работает  начала
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_image_Request && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(prewImage);
        }
    }
    // хз как оно относиться к кому но оно работает   конец


    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile2() {
        if (mImageUri != null) {

            StorageReference ref = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            ref.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            mProgressBar.setProgress(0);
                                        }
                                    }, 5000);

                                    Upload upload = new Upload(mEditTextFileName.getText().toString().trim(), uri.toString());
                                    mDatabaseRef.push().setValue(upload);
                                    nora();//нычка

                                    Toast.makeText(foto_paid2.this, "successful upload", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(foto_paid2.this, "fail uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);

                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }

    }

    //нычка
    private void nora() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
        Date now = new Date();
        String fileName = formatter.format(now);
        // mStorageRefwtf = FirebaseStorage.getInstance().getReference("image3/" + fileName);
        // mStorageRefwtf = FirebaseStorage.getInstance().getReference("wtf").child(inkognito + fileName);
        mStorageRefwtf = FirebaseStorage.getInstance().getReference("wtf").child(inkognito).child("paid/" + fileName);
        mStorageRefwtf.putFile(mImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //  binding.firebaseimage.setImageURI(null);
                        Toast.makeText(foto_paid2.this, " ", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(foto_paid2.this, " ", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void paidalbuminf() {
        databaseReference2 = firebaseDatabase.getReference("users").child(inkognito).child("paid_album"); // вариант 3  типо прописали ссылку + родительский католог : что напротив него написано
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String paidAlbumInf = snapshot.getValue(String.class);
                paidflbumetxt.setText("цена " + paidAlbumInf);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addPaid() {
        //  option show dialog
        String options[] = {"Указать цену альбома"};
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
                    pd.setMessage("Укажи цену альбома в кристалах");
                    addPaid2("paid_album");
                }
            }
        });
        //create and show dialog
        builder.create().show();
    }

    private void addPaid2(String key) {
        //custom dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Цена альбома");
        //set layout of dialog
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10, 10, 10, 10);
        //add edit test
        EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint("укажи цену в кристаллах");//hint e.g. Edit name OR Edit phone
        linearLayout.addView(editText);

        builder.setView(linearLayout);

        //add Buttons in dialog to Update
        builder.setPositiveButton("добавить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //input text from edit text
                String value = editText.getText().toString().trim();
                //validate if user has entered something or not
                if (!TextUtils.isEmpty(value)) {
                    pd.show();
                    HashMap<String, Object> result = new HashMap<>();
                    result.put(key, value);

                    databaseReference.child(inkognito).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    //updated, dismiss progress
                                    pd.dismiss();
                                    Toast.makeText(foto_paid2.this, "обновление", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //failed, dismiss progress, get and show error message
                                    pd.dismiss();
                                    Toast.makeText(foto_paid2.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(foto_paid2.this, "Update", Toast.LENGTH_SHORT).show();
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
        paidflbumetxt = findViewById(R.id.paidflbume);
        addpaidalbume = findViewById(R.id.addpaidalbume);
        addImage = findViewById(R.id.addImage);
        mEditTextFileName = findViewById(R.id.edit_txt_file_name);
        prewImage = findViewById(R.id.prewImage);
        mProgressBar = findViewById(R.id.progress_bar);
        paidAlbumeRv = findViewById(R.id.paidAlbumeRv);

    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "нажмите и держите чтобы удалить фото " + position, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onWhatEverClick(int position) {
        Toast.makeText(this, "Whatever click at position" + position, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDeleteClick(int position) {

        Upload selectedItem = mUploads.get(position);
        String selectedKey = selectedItem.getKey();

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabaseRef.child(selectedKey).removeValue();
                Toast.makeText(foto_paid2.this, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }

    public void onBackPressed() {
        try {
            Intent intent = new Intent(foto_paid2.this, profileId.class); // try = попытка
            startActivity(intent);
            finish();
        } catch (Exception e) {  // catch = ловить
        }
    }

}