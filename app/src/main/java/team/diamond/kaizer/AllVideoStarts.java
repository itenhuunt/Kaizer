package team.diamond.kaizer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AllVideoStarts extends AppCompatActivity {


    TextView randomStrem;


    String[] permissions = new String[]{android.Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
    private int requestCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_video_starts);

        hooks();

        randomStrem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPermissionsGranted()) {
                    Intent intent = new Intent(AllVideoStarts.this, VideoCallRandom.class);
//                        intent.putExtra("profile", user.getProfile());
                    startActivity(intent);
                    finish();

                } else {
                    askPermissions();
                }


            }
        });


    }

    private void hooks() {
        randomStrem = findViewById(R.id.randomStrem);
    }


    void askPermissions() {  //спрашиваем разрешение  которые прописали в манифесте
        ActivityCompat.requestPermissions(this, permissions, requestCode);
    }


    private boolean isPermissionsGranted() { // РАЗРЕШЕНИЕ ПОЛУЧЕНО
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)  // compat = Совместимость
                return false;
        }
        return true;
    }


    public void onBackPressed() {
        try {
            Intent intent = new Intent(AllVideoStarts.this, kaizerActivity.class); // try = попытка
            startActivity(intent);
            finish();
        } catch (Exception e) {  // catch = ловить
        }
    }


}