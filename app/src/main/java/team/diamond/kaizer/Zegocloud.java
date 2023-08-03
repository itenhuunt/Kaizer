package team.diamond.kaizer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Random;

public class Zegocloud extends AppCompatActivity {


    LottieAnimationView watch_live, start_live;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zegocloud);

        hooks();

        long appID = 858673536;
        String appSign = "06811fd3a90f8cee0d3b9d0bde6746c5708de3f86542a87ca65f681b24514e06";

        String userID = Build.MANUFACTURER + "_" + generateUserID();
        String userName = userID + "_Name";
        String liveID = "test_live_id";




        //host
        start_live.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Zegocloud.this, LiveActivity.class);
                intent.putExtra("host", true);
                intent.putExtra("appID", appID);
                intent.putExtra("appSign", appSign);
                intent.putExtra("userID", userID);
                intent.putExtra("userName", userName);
                intent.putExtra("liveID", liveID);
                startActivity(intent);

            }
        });

        //смотреть  hosta
        watch_live.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Zegocloud.this, LiveActivity.class);
                intent.putExtra("appID", appID);
                intent.putExtra("appSign", appSign);
                intent.putExtra("userID", userID);
                intent.putExtra("userName", userName);
                intent.putExtra("liveID", liveID);
                startActivity(intent);
            }
        });


    }

    private String generateUserID() {
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        while (builder.length() < 5) {
            int nextInt = random.nextInt(10);
            if (builder.length() == 0 && nextInt == 0) {
                continue;
            }
            builder.append(nextInt);
        }
        return builder.toString();
    }

    private void hooks() {
        watch_live = findViewById(R.id.watch_live);
        start_live = findViewById(R.id.start_live);
    }
}