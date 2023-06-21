package team.diamond.kaizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.security.acl.Group;
import java.util.UUID;

import team.diamond.kaizer.models.InterfaceJava;
import team.diamond.kaizer.models.User;

public class OnCallRandom extends AppCompatActivity {

    ImageView profile, connectingImage, videoBtn, micBtn, endCall;

    TextView name, city;

    WebView webView;


    androidx.constraintlayout.widget.Group loadingGroup, controls;


    String uniqueId = "";
    FirebaseAuth auth;
    String username = "";
    String friendsUsername = "";
    boolean isPeerConnected = false;
    DatabaseReference firebaseRef;
    boolean isAudio = true;
    boolean isVideo = true;
    String createdBy;
    boolean pageExit = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_call_random);


        hooks();

        auth = FirebaseAuth.getInstance();
        // uniqueId = auth.getUid();    // вариант не катит
        //  uniqueId = getUniqueId();

        firebaseRef = FirebaseDatabase.getInstance().getReference().child("randomCall");

        username = getIntent().getStringExtra("username"); //  аоткуда я их вытащю еслти я их не засовывал ???
        String incoming = getIntent().getStringExtra("incoming");
        createdBy = getIntent().getStringExtra("createdBy");

        // но с этим исправлением прогрузка будет у обоих
//        friendsUsername = "";
//
//        if (incoming.equalsIgnoreCase(friendsUsername))
//            friendsUsername = incoming;

        friendsUsername = incoming;

        setupWebView();

        micBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAudio = !isAudio;
                callJavaScriptFunction("javascript:toggleAudio(\"" + isAudio + "\")");
                if (isAudio) {
                    micBtn.setImageResource(R.drawable.btn_unmute_normal);
                } else {
                    micBtn.setImageResource(R.drawable.btn_mute_normal);
                }
            }
        });

        videoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVideo = !isVideo;
                callJavaScriptFunction("javascript:toggleVideo(\"" + isVideo + "\")");
                if (isVideo) {
                    videoBtn.setImageResource(R.drawable.btn_video_normal);
                } else {
                    videoBtn.setImageResource(R.drawable.btn_video_muted);
                }
            }
        });

        endCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  finish();
                firebaseRef.child(createdBy).setValue(null);
                Intent intent = new Intent(OnCallRandom.this, kaizerActivity.class); // try = попытка
                startActivity(intent);
                finish();
            }
        });
    }

    void setupWebView() {
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                //   super.onPermissionRequest(request);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.getResources());
                    // 1 59 32  хз возможно ошибка
                }
            }
        });


        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.addJavascriptInterface(new InterfaceJava(this), "Android");

        loadVideoCall();


    }


    //  on Page Finished - Notify the host application that a page has finished loading. This method is called only for main frame
    // on Page Finished — уведомляет хост-приложение о завершении загрузки страницы. Этот метод вызывается только для основного фрейма
    public void loadVideoCall() {
        String filePath = "file:android_asset/call.html";
        webView.loadUrl(filePath);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                initializePeer();
            }
        });
    }

    //direct communication    =   прямое общение
    //инициализировать одноранговый узел
    void initializePeer() {
        uniqueId = getUniqueId();

        callJavaScriptFunction("javascript:init(\"" + uniqueId + "\")");

        if (createdBy.equalsIgnoreCase(username)) {

            if (pageExit)
                return;
            firebaseRef.child(username).child("connId").setValue(uniqueId);
            firebaseRef.child(username).child("isAvailable").setValue(true); // isAvailable =  доступен

//            binding.loadingGroup.setVisibility(View.GONE);
//            binding.controls.setVisibility(View.VISIBLE);
            // т.е.  кто начал трансляцию у него будет пусто имя и город
            // прогрузиться только у того кто второй !!!!
            FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(friendsUsername)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);

//                            Glide.with(OnCallRandom.this).load(user.getProfile())
//                                    .into(profile);
                            name.setText(user.getName());
                            //      city.setText(user.getCity());
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });


        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    friendsUsername = createdBy;
                    // т.е.  кто начал трансляцию у него будет пусто имя и город
                    // прогрузиться только у того кто второй !!!!
                    FirebaseDatabase.getInstance().getReference()
                            .child("users")
                            .child(friendsUsername)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    User user = snapshot.getValue(User.class);

//                                    Glide.with(OnCallRandom.this).load(user.getProfile())
//                                            .into(profile);
                                    name.setText(user.getName());
//                                    city.setText(user.getCity());

                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });
                    FirebaseDatabase.getInstance().getReference()
                            .child("randomCall")
                            .child(friendsUsername)
                            .child("connId")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        sendCallRequest();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }, 2000);
        }
    }


    public void onPeerConnected() {
        isPeerConnected = true;
    }

    void sendCallRequest() {
        if (!isPeerConnected) {
            Toast.makeText(this, "You are not connected. Please check you internet.", Toast.LENGTH_SHORT).show();
            return;
        }
        //2 16 13
        listenConnId();
    }

    void listenConnId() {
        firebaseRef.child(friendsUsername).child("connId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null)
                    return;

                //я не знаю как корректо прописать инвиз
                loadingGroup.setVisibility(View.GONE);
                //  controls.setVisibility(View.VISIBLE);
                controls.setVisibility(View.GONE);
                String connId = snapshot.getValue(String.class);
                callJavaScriptFunction("javascript:startCall(\"" + connId + "\")");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void callJavaScriptFunction(String function) {
        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.evaluateJavascript(function, null);

            }
        });
    }

    //An 'uuid' is a Universlay Unique identifier (UUID) standardized 128-bit format for a string ID used to uniquely identify information its used to uniquely identify your applications Bluetooth service
    // «uuid» — это стандартизированный 128-битный формат универсального уникального идентификатора (UUID) для строкового идентификатора, используемого для уникальной идентификации информации, используемой для уникальной идентификации ваших приложений Служба Bluetooth
    String getUniqueId() {
        return UUID.randomUUID().toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pageExit = true;
        firebaseRef.child(createdBy).setValue(null);
        finish();
    }


    private void hooks() {
        profile = findViewById(R.id.profile);
        connectingImage = findViewById(R.id.connectingImage);
        videoBtn = findViewById(R.id.videoBtn);
        micBtn = findViewById(R.id.micBtn);
        endCall = findViewById(R.id.endCall);
        name = findViewById(R.id.name);
        city = findViewById(R.id.city);
        webView = findViewById(R.id.webView);
        loadingGroup = findViewById(R.id.loadingGroup);
        controls = findViewById(R.id.controls);
    }


    public void onBackPressed() {
        try {
            firebaseRef.child(createdBy).setValue(null);
            Intent intent = new Intent(OnCallRandom.this, kaizerActivity.class); // try = попытка
            startActivity(intent);
            finish();
        } catch (Exception e) {  // catch = ловить
        }
    }


}




