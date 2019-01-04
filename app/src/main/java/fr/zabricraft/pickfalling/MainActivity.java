package fr.zabricraft.pickfalling;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    private boolean isSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(this) != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton buttonPlay = findViewById(R.id.buttonPlay);
        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MenuActivity.class));
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();

        signInSilently();

        TextView high = findViewById(R.id.textHighScore);
        TextView total = findViewById(R.id.textTotalScore);

        SharedPreferences prefs = getSharedPreferences("PickFallingScore", Context.MODE_PRIVATE);

        int highest_score = prefs.getInt("highest", 0);
        int total_score = prefs.getInt("total", 0);

        if(isSignedIn()){
            ImageButton buttonScores = findViewById(R.id.buttonScores);
            buttonScores.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, LeaderboardsActivity.class));
                }
            });
            buttonScores.setEnabled(true);
            buttonScores.setVisibility(View.VISIBLE);
            ImageButton buttonAchievements = findViewById(R.id.buttonAchievements);
            buttonAchievements.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Games.getAchievementsClient(MainActivity.this, GoogleSignIn.getLastSignedInAccount(MainActivity.this))
                            .getAchievementsIntent()
                            .addOnSuccessListener(new OnSuccessListener<Intent>() {
                                @Override
                                public void onSuccess(Intent intent) {
                                    startActivityForResult(intent, 9003);
                                }
                            });
                }
            });
            buttonAchievements.setEnabled(true);
            buttonAchievements.setVisibility(View.VISIBLE);
        }

        high.setText(String.format(getString(R.string.highest), highest_score));
        total.setText(String.format(getString(R.string.total), total_score));
    }

    private void signInSilently() {
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        signInClient.silentSignIn().addOnCompleteListener(this,
                new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        if (task.isSuccessful()) {
                            // The signed in account is stored in the task's result.
                            GoogleSignInAccount signedInAccount = task.getResult();
                        } else {
                            // Player will need to sign-in explicitly using via UI
                            startSignInIntent();
                        }
                    }
                });
    }

    private void startSignInIntent() {
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(intent, 517);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 517) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // The signed in account is stored in the result.
                GoogleSignInAccount signedInAccount = result.getSignInAccount();
            } else {
                String message = result.getStatus().getStatusMessage();
                if (message != null && !message.isEmpty()) {
                    new AlertDialog.Builder(this).setMessage(message)
                            .setNeutralButton(android.R.string.ok, null).show();
                }
            }
        }
    }

}
