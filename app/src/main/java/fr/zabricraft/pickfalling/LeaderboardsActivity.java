package fr.zabricraft.pickfalling;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class LeaderboardsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboards);

        LinearLayout leaderboards = findViewById(R.id.leaderboardsLayout);

        int textColor = Color.parseColor("#EEEEEE");
        int backgroundColor = Color.parseColor("#ED7F10");

        ArrayList<Pair<String, Integer>> list = new ArrayList<>();

        list.add(new Pair<>(getString(R.string.leaderboards_total), R.string.leaderboard_total_score));
        list.add(new Pair<>(getString(R.string.leaderboards_highest), R.string.leaderboard_highest_score));
        for(final Mode m : Mode.values()){
            list.add(new Pair<>(String.format(getString(R.string.leaderboards_highest_in), m.toString()), m.getLeaderboard()));
        }

        for(final Pair<String, Integer> i : list){
            Button button = new Button(this);
            button.setTextSize(30);
            button.setTextColor(textColor);
            button.setTransformationMethod(null);
            button.setText(i.first);
            button.setBackgroundColor(backgroundColor);
            button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Games.getLeaderboardsClient(LeaderboardsActivity.this, GoogleSignIn.getLastSignedInAccount(LeaderboardsActivity.this))
                                .getLeaderboardIntent(getString(i.second))
                                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                                    @Override
                                    public void onSuccess(Intent intent) {
                                        startActivityForResult(intent, 9004);
                                    }
                                });
                    }
            });
            leaderboards.addView(button);
            leaderboards.addView(new TextView(this));
        }
    }
}
