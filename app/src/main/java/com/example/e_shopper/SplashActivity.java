package com.example.e_shopper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();
        VideoView videoView = findViewById(R.id.videoView);

        // Set video URI
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.e_splash);
        videoView.setVideoURI(videoUri);

        // Adjust video scaling
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(false);
            float videoRatio = (float) mp.getVideoWidth() / mp.getVideoHeight();
            float screenRatio = (float) videoView.getWidth() / videoView.getHeight();
            android.view.ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();

            if (videoRatio > screenRatio) {
                layoutParams.width = videoView.getWidth();
                layoutParams.height = (int) (videoView.getWidth() / videoRatio);
            } else {
                layoutParams.width = (int) (videoView.getHeight() * videoRatio);
                layoutParams.height = videoView.getHeight();
            }

            videoView.setLayoutParams(layoutParams);
        });

        // Start video and move to the next activity when finished
        videoView.setOnCompletionListener(mp -> navigateToNextScreen());
        videoView.start();
    }

    private void navigateToNextScreen() {
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        } else {
            startActivity(new Intent(SplashActivity.this, AuthActivity.class));
        }
        finish();
    }
}


//package com.example.e_shopper;




//
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.widget.VideoView;
//import androidx.appcompat.app.AppCompatActivity;
//import com.google.firebase.auth.FirebaseAuth;
//
//public class SplashActivity extends AppCompatActivity {
//    private FirebaseAuth mAuth;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash);
//
//        mAuth = FirebaseAuth.getInstance();
//        VideoView videoView = findViewById(R.id.videoView);
//
//        // Set video URI
//        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.e_splash);
//        videoView.setVideoURI(videoUri);
//
//        // Start video and move to the next activity when finished
//        videoView.setOnCompletionListener(mp -> navigateToNextScreen());
//        videoView.start();
//    }
//
//    private void navigateToNextScreen() {
//        if (mAuth.getCurrentUser() != null) {
//            startActivity(new Intent(SplashActivity.this, MainActivity.class));
//        } else {
//            startActivity(new Intent(SplashActivity.this, AuthActivity.class));
//        }
//        finish();
//    }
//}
//
//
