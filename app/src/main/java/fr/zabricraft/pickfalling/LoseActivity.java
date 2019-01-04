package fr.zabricraft.pickfalling;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.games.Games;

public class LoseActivity extends AppCompatActivity {

    private Mode mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lose);

        int points = 0;
        mode = Mode.CLASSIC;

        Intent intent = getIntent();
        if(intent != null){
            points = intent.getIntExtra("points", 0);
            mode = Mode.valueOf(intent.getStringExtra("mode"));
        }

        TextView lastScore = findViewById(R.id.textLoseScore);
        lastScore.setText(String.format(getString(R.string.score), points));
        Button buttonReplay = findViewById(R.id.buttonReplay);
        buttonReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent start = new Intent(LoseActivity.this, GameActivity.class);
                start.putExtra("mode", mode.toString());
                startActivity(start);
                finish();
            }
        });

        Button buttonMenu = findViewById(R.id.buttonHome);
        buttonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoseActivity.this, MenuActivity.class));
                finish();
            }
        });

        TextView high = findViewById(R.id.textHighScoreLose);

        SharedPreferences prefs = getSharedPreferences("PickFallingScore", Context.MODE_PRIVATE);
        int highest_mode = prefs.getInt("highest_"+mode.toString(), 0);

        high.setText(String.format(getString(R.string.highest), highest_mode));

        if(GoogleSignIn.getLastSignedInAccount(this) != null) {
            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .unlockImmediate(getString(R.string.achievement_first_game));

            int highest = prefs.getInt("highest", 0);

            if (highest >= 250) {
                Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                        .unlockImmediate(getString(R.string.achievement_get_250_points_at_once));
            }
            if (highest >= 500) {
                Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                        .unlockImmediate(getString(R.string.achievement_get_500_points_at_once));
            }
            if (highest >= 750) {
                Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                        .unlockImmediate(getString(R.string.achievement_get_750_points_at_once));
            }
            if (highest >= 1000) {
                Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                        .unlockImmediate(getString(R.string.achievement_get_1000_points_at_once));
            }

            int total = prefs.getInt("total", 0);
            int unlocked = 0;
            for(Mode m : Mode.values()){
                if(total >= m.getScore() && m.getScore() != 0){
                    unlocked++;
                }
            }
            if (unlocked >= 1) {
                Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                        .unlockImmediate(getString(R.string.achievement_unlock_1_level));
            }
            if (unlocked >= 5) {
                Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                        .unlockImmediate(getString(R.string.achievement_unlock_5_levels));
            }
            if (unlocked >= 10) {
                Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                        .unlockImmediate(getString(R.string.achievement_unlock_10_levels));
            }
            if (unlocked == Mode.values().length) {
                Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                        .unlockImmediate(getString(R.string.achievement_unlock_all_levels));
            }
        }
    }

}
