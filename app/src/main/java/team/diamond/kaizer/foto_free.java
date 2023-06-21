package team.diamond.kaizer;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

import java.util.ArrayList;
import java.util.List;

import team.diamond.kaizer.adapters.ImageAdapterFree;
import team.diamond.kaizer.models.Upload;
import team.diamond.kaizer.models.UploadFree;

public class foto_free extends AppCompatActivity implements ImageAdapterFree.OnItemClickListener {

    private SharedPreferences user_name_shared_preferences;
    private String inkognito, inkognitoid;


    FirebaseDatabase firebaseDatabase;
    //указываем ссылки
    private DatabaseReference mDatabaseRef2;
    private DatabaseReference mDatabaseRef;

    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    //   база которая обновляется в реальном врмени   //  то что будет загружаться на сервер
    private ValueEventListener mDBListener;
    // высе что есть на листе
    private ImageView addFoto;
    private ProgressBar mProgressBar;
    ProgressDialog pd;
    //_____________________________   2    отображение
    private RecyclerView RvFreeAlbume;
    private ImageAdapterFree mAdapterFree;
    private List<UploadFree> mUploads;

    Uri image_uri;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;
    private final int CAMERA_PERMISSION_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.foto_public2);

        user_name_shared_preferences = getSharedPreferences("teen_pref", MODE_PRIVATE); //обяъвляем приватный режим для ОЧКОВ + прописываем ИМЯ в xml (чxml так и будет называться) + приватный режим
        inkognito = user_name_shared_preferences.getString("teen_name", inkognito);// пишем ВПЕРЕДИ  т.к. код исполняется по порядку + в этом xml опять пишем наши очки под именем которое задаем save_key_count
        inkognitoid = user_name_shared_preferences.getString("teen_id", inkognitoid);//  в этом xml опять пишем имя нашей белки  +  которое задаем значение teen_name

        hooks();
        //init firebase
        firebaseDatabase = FirebaseDatabase.getInstance();

        //указываем путь в fire storage
        mStorageRef = FirebaseStorage.getInstance().getReference("AlbumFree").child(inkognitoid);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("AlbumFree").child(inkognitoid);

        //init progress dialog
        pd = new ProgressDialog(this);

        RvFreeAlbume.setHasFixedSize(true);
        RvFreeAlbume.setLayoutManager(new LinearLayoutManager(this));

        mUploads = new ArrayList<>();

        mAdapterFree = new ImageAdapterFree(foto_free.this, mUploads);
        //  для RV устанавливаем адаптер
        RvFreeAlbume.setAdapter(mAdapterFree);

        mAdapterFree.setOnClickListener(foto_free.this);

        mStorage = FirebaseStorage.getInstance();
        // указываем путь к платному альбому
        mDatabaseRef2 = FirebaseDatabase.getInstance().getReference("AlbumFree").child(inkognitoid);

        mDBListener = mDatabaseRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUploads.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    UploadFree upload = postSnapshot.getValue(UploadFree.class);
                    upload.setKey(postSnapshot.getKey());
                    mUploads.add(upload);
                }

                mAdapterFree.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(foto_free.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();  // 3:10
            }
        });

        // кнопка выбрать изображение  начало
        addFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //выбираем галерея или фото
                newVerCustomCameraOreGallery();
            }
        });



    }
    //______________________________  НОВОЕ !!!!!!!!!  _____________________________

    // показать image  диалог
    private void newVerCustomCameraOreGallery() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image");
        builder.setMessage("Please select an option");
        builder.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                chekCameraPermission();                                        //  проверяем разрешения + открываем камеру
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pickFromGallery();                                        //  выбираем из Галереи
                dialog.dismiss();
            }
        });
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    // проверка камеры +
    private void chekCameraPermission() {
        if (ContextCompat.checkSelfPermission(foto_free.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(foto_free.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(foto_free.this, new String[]{
                    Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            pickFromCamera();
        }
    }


    //выбираем камеру
    private void pickFromCamera() {
        //Intent of picking image from device camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        //put image uri
        image_uri = foto_free.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //intent to start camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    // выбрать фото из галереи+
    private void pickFromGallery() {
        //pick from gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    //2 --- ???
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //this metod will be called after picking image from Camera or Gallery
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                //image is picked from gallery, get uri of image
                image_uri = data.getData();
                newCustomUpload(image_uri);
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //image is picked from camera, get uri of image
                newCustomUpload(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }




    //загружаем фото на сервер
    private void newCustomUpload( Uri image_uri) {
        if (image_uri != null) {

            StorageReference ref = mStorageRef.child(System.currentTimeMillis()
                    + "." + ("newVersion"));

            ref.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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


                                    Upload upload = new Upload(uri.toString(), uri.toString());
                                    mDatabaseRef.push().setValue(upload);
                                    Toast.makeText(foto_free.this, "successful upload", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(foto_free.this, "fail uploaded", Toast.LENGTH_SHORT).show();
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




    private void hooks() {
        addFoto = findViewById(R.id.addFoto);
        mProgressBar = findViewById(R.id.progress_bar);
        RvFreeAlbume = findViewById(R.id.RvFreeAlbume);

    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "нажмите и держите чтобы удалить фото ", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onWhatEverClick(int position) {
        Toast.makeText(this, "Whatever click at position" + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(int position) {

        UploadFree selectedItem = mUploads.get(position);
        String selectedKey = selectedItem.getKey();

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabaseRef.child(selectedKey).removeValue();
                Toast.makeText(foto_free.this, "Item deleted", Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(foto_free.this, profileId.class); // try = попытка
            startActivity(intent);
            finish();
        } catch (Exception e) {  // catch = ловить
        }
    }


}