package team.diamond.kaizer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class TrueStoryAdd extends AppCompatActivity {

    EditText TitleStoryTrue, DescriptionStoryTrue;
    Button UploadBtnStoryTrue;
    ImageView ImageStoryTrue;

    private SharedPreferences user_name_shared_preferences;
    public String inkognito, inkognito_id;

    String profile_pic;
    Integer zero = 0; // как прописать корректно ?

    DatabaseReference userDbRef; //  что за ссылка - что за хуйня ?
    //permissions constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    //image pick constants
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    //permissions array
    String[] cameraPermissions;
    String[] storagePermissions;
    //user info  ---   хз что за хуйня  и нахуй оно надо
    String name, uid;
    //image picked will be same in this  uri
    Uri image_rui = null;  //унифицированный идентификатор ресурса
    // progress bar
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_true_story_add);
        // зависимости
        hooks();
        // Shared preferences
        user_name_shared_preferences = getSharedPreferences("teen_pref", MODE_PRIVATE); //обяъвляем приватный режим для ОЧКОВ + прописываем ИМЯ в xml (чxml так и будет называться) + приватный режим
        inkognito = user_name_shared_preferences.getString("teen_name", inkognito);// пишем ВПЕРЕДИ  т.к. код исполняется по порядку + в этом xml опять пишем наши очки под именем которое задаем save_key_count
        //init permissions arrays
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //  диалог инициалицазия
        pd = new ProgressDialog(this);
        //get some info of current user to include in post
        userDbRef = FirebaseDatabase.getInstance().getReference("users");

        // хз зачем это условие по идее можно и без него    ХЗ НЕ СОВСЕМ ПОНЯТНО
        Query query = userDbRef.orderByChild("name").equalTo(inkognito);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for (DataSnapshot ds : datasnapshot.getChildren()) {
                    name = "" + ds.child("name").getValue();
                    uid = "" + ds.child("uid").getValue();
                    profile_pic = "" + ds.child("profile_pic").getValue();  // достаем фото
                    inkognito_id = "" + ds.child("inkognito_id").getValue();  // достаем id
                    // тут дописать команду где вынуть аву
                    //  с помощью этой команды можно вынуть инфу
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //get image from camera/gallery on click
        ImageStoryTrue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show image pick dialog
                showImagePickDialog();
            }
        });
        // нажатие кнопки + действия
        UploadBtnStoryTrue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get data (title, description from EditTexts)
                String title = TitleStoryTrue.getText().toString().trim();
                String description = DescriptionStoryTrue.getText().toString().trim();
                if (TextUtils.isEmpty(title)) {
                    Toast.makeText(TrueStoryAdd.this, "Enter title", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(description)) {
                    Toast.makeText(TrueStoryAdd.this, "Enter description", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (image_rui == null) {
                    //post without image
                    uploadData(title, description, "noImage");
                } else {
                    //post with image
                    uploadData(title, description, String.valueOf(image_rui));
                }
            }
        });


    }//____________________________________________

    private void hooks() {
        TitleStoryTrue = findViewById(R.id.TitleStoryTrue);
        DescriptionStoryTrue = findViewById(R.id.DescriptionStoryTrue);
        UploadBtnStoryTrue = findViewById(R.id.UploadBtnStoryTrue);
        ImageStoryTrue = findViewById(R.id.ImageStoryTrueLayout);

    }

    private void uploadData(String title, String description, String uri) {
        pd.setTitle("publish post");
        pd.show();

        //for post-image name, post-id, post-publish-time
        String timeStamp = String.valueOf(System.currentTimeMillis());
        // указываем имя которое кладем в хранилище
        String filePathAndName = "Posts/" + "post_" + timeStamp;
        // просто прописываем время 2 варианта на всякий случай
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA); // 1 вариант
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy_MM_dd", Locale.CANADA);   // 2 вариант
        Date now = new Date();
        String fileName = formatter.format(now);
        String fileName2 = formatter2.format(now);

        if (!uri.equals("noImage")) {
            //post with image
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
            ref.putFile(Uri.parse(uri))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //image is uploaded to firebase storage, now get it's url
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;

                            String downloadUri = uriTask.getResult().toString();

                            if (uriTask.isSuccessful()) {

                                //url is received upload post to firebase database
                                HashMap<Object, String> hashMap = new HashMap<>();
                                //put post info
                                hashMap.put("name", name);
                                hashMap.put("uid", uid);
                                hashMap.put("inkognito_id", inkognito_id);
                                hashMap.put("StoryTitle", title);
                                hashMap.put("StoryText", description);
                                hashMap.put("StoryImage", downloadUri);
                                hashMap.put("StoryTime", fileName2);
                                hashMap.put("profile_pic", profile_pic);
                                hashMap.put("keyFreeStory", timeStamp);
                                hashMap.put("customVer1", "");
                                hashMap.put("StoryLike", "0");
                                hashMap.put("StoryView", "zero");



                                //patch to store post data
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("StoryFree");
                                //put data in this ref
                                ref.child(timeStamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                //added in database
                                                pd.dismiss();
                                                Toast.makeText(TrueStoryAdd.this, "post published", Toast.LENGTH_SHORT).show();
                                                //reset views   clear
                                                TitleStoryTrue.setText("");
                                                DescriptionStoryTrue.setText("");
                                                ImageStoryTrue.setImageURI(null);
                                                image_rui = null;
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //failed adding post in database
                                                pd.dismiss();
                                                Toast.makeText(TrueStoryAdd.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed uploading image
                            pd.dismiss();
                            Toast.makeText(TrueStoryAdd.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            //post without image
            HashMap<Object, String> hashMap = new HashMap<>();
            //put post info
            hashMap.put("name", name);
            hashMap.put("uid", uid);
            hashMap.put("inkognito_id", inkognito_id);
            hashMap.put("StoryTitle", title);
            hashMap.put("StoryText", description);
            hashMap.put("StoryImage", "noImage");
            hashMap.put("StoryTime", fileName2);
            hashMap.put("profile_pic", profile_pic);
            hashMap.put("keyFreeStory", timeStamp);
            hashMap.put("customVer1", "");
            hashMap.put("StoryLike", "0");
            hashMap.put("StoryView", "zero");

            //patch to store post data
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("StoryFree");
            //put data in this ref
            ref.child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //added in database
                            pd.dismiss();
                            Toast.makeText(TrueStoryAdd.this, "post published", Toast.LENGTH_SHORT).show();
                            // clear all layout
                            TitleStoryTrue.setText("");
                            DescriptionStoryTrue.setText("");
                            ImageStoryTrue.setImageURI(null);
                            image_rui = null;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed adding post in database
                            pd.dismiss();
                            Toast.makeText(TrueStoryAdd.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void showImagePickDialog() {
        //options (camera/gallery) to show in dialog
        String[] options = {"Camera", "Gallery"};
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image from");
        //set options to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //item click handle
                if (which == 0) {
                    //camera clicked
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        pickFromCamera();
                    }
                }
                if (which == 1) {
                    //gallery clicked
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickFromGallery();
                    }
                }
            }
        });
        //create and show dialog
        builder.create().show();
    }


    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        //intent to pick image from camera
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "Temp Pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION, "Temp Descr");
        //получить преобразователь содержимого
        image_rui = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
        //  запуцск какого то действия
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_rui);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    // проверка разрешений
    private boolean checkStoragePermission() {
        //check if storage permission is enable or not
        //return true if enabled
        //return false if not enabled
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    // повторная проверка разрешений
    private void requestStoragePermission() {
        //request runtime storage permission
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    //выбиваем камеру
    private boolean checkCameraPermission() {
        //check if camera permission is enable or not
        //return true if enabled
        //return false if not enabled
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result & result1;
    }

    // запрос разрешений камеры
    private void requestCameraPermission() {
        //request runtime camera permission
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    // хз нахуй и для чего оно
    @Override
    protected void onStart() {
        super.onStart();
    }

    // хз нахуй и для чего оно
    @Override
    protected void onResume() {
        super.onResume();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//
//        menu.findItem(R.id.action_add_post).setVisible(false);
//        menu.findItem(R.id.action_search).setVisible(false);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        //get item id
//        int id = item.getItemId();
//        if (id == R.id.action_logout) {
//            firebaseAuth.signOut();
//            checkUserStatus();
//        }
//        return super.onOptionsItemSelected(item);
//    }

    //handle permission results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // this method is called when user press Allow or Deny from permission request dialog
        //here we will handle permission cases (allowed and denied)
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        // оба разрешения приняты
                        pickFromCamera();
                    } else {
                        Toast.makeText(this, "Camera & Storage both permission are necessary...", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //empty
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        // хранилище разрешения приняты
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, " Storage permission necessary...", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //empty
                }
            }
            break;
        }
    }

    // когда есть результат... запуск действий
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //this method will be called after picking image from camera or gallery
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                //image is picked from gallery, get uri of image
                image_rui = data.getData();
                //set to imageView
                ImageStoryTrue.setImageURI(image_rui);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //image is picked from camera, get uri of image
                ImageStoryTrue.setImageURI(image_rui);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    //нахуй оно надо и что за хуйня
    //  кнопка назад только вроде я уже написал отдельно
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // goto previous activity
        return super.onSupportNavigateUp();
    }

    //  хз зачем дублирую
    public void onBackPressed() {
        try {
            Intent intent = new Intent(TrueStoryAdd.this, kaizerActivity.class); // try = попытка
            startActivity(intent);
            finish();
        } catch (Exception e) {  // catch = ловить
        }
    }


}