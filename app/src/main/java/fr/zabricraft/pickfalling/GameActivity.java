package fr.zabricraft.pickfalling;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.games.Games;

import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private boolean init;
    private boolean playing;
    private boolean pause;
    private boolean lefttoright;
    private boolean control;
    private boolean nexting;
    private TextView score;
    private TextView clickto;
    private ImageView player;
    private ImageView object;
    private RelativeLayout activity_game;
    private Thread gameThread;
    private int points;
    private int currentImg;
    private Mode mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mode = Mode.CLASSIC;
        playing = true;
        pause = true;
        nexting = false;
        points = 0;

        Intent intent = getIntent();
        if(intent != null){
            mode = Mode.valueOf(intent.getStringExtra("mode"));
        }

        score = findViewById(R.id.textScore);
        clickto = findViewById(R.id.textClickTo);
        player = findViewById(R.id.imagePlayer);
        object = findViewById(R.id.imageObject);
        activity_game = findViewById(R.id.activity_game);

        score.setText(String.format(getString(R.string.score), points));

        currentImg = R.drawable.spinach;
        if(mode.equals(Mode.UNICORN)){
            player.setImageResource(R.drawable.unicorn);
            currentImg = R.drawable.heart;
            activity_game.setBackgroundColor(Color.parseColor("#FEBFD2"));
        }else if(mode.equals(Mode.FISH)){
            player.setImageResource(R.drawable.fish);
            currentImg = R.drawable.bubble;
            activity_game.setBackgroundColor(Color.parseColor("#26C4EC"));
        }else if(mode.equals(Mode.CAT)){
            player.setImageResource(R.drawable.cat);
            currentImg = R.drawable.fish;
            activity_game.setBackgroundColor(Color.parseColor("#FEBFD2"));
        }else if(mode.equals(Mode.BIRD)){
            player.setImageResource(R.drawable.bird);
            currentImg = R.drawable.maggot;
            activity_game.setBackgroundColor(Color.parseColor("#26C4EC"));
        }else if(mode.equals(Mode.SHELL)){
            player.setImageResource(R.drawable.crab);
            currentImg = R.drawable.shell;
        }

        object.setImageResource(currentImg);
        object.setVisibility(View.GONE);

        activity_game.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(pause){
                    pause = false;
                    clickto.setVisibility(View.GONE);
                    object.setVisibility(View.VISIBLE);
                }
                if(mode.equals(Mode.BIRD)){
                    player.setY(Math.max(score.getY()+score.getHeight(), Math.min(event.getY()-player.getHeight()/2, activity_game.getHeight()-player.getHeight())));
                }else{
                    float x = event.getX();
                    if(mode.equals(Mode.MIRROR) || mode.equals(Mode.EVERYTHING)){
                        float middle = activity_game.getWidth()/2;
                        x = middle - (x - middle);
                    }
                    if(mode.equals(Mode.CONTROL) && control) {
                        object.setX(Math.max(0, Math.min(x-object.getWidth()/2, activity_game.getWidth()-object.getWidth())));
                    } else {
                        player.setX(Math.max(0, Math.min(x-player.getWidth()/2, activity_game.getWidth()-player.getWidth())));
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void onPause(){
        super.onPause();
        playing = false;
        if(gameThread != null){
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        playing = true;
        gameThread = new Thread(new Runnable() {
            @Override
            public void run() {
                init = false;
                lefttoright = true;
                control = false;
                while(playing){
                    if(activity_game.getHeight() > 0 && activity_game.getWidth() > 0 && !nexting) {
                        if(!init){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mode.equals(Mode.BIRD)) {
                                        object.setX(activity_game.getWidth());
                                        object.setY(activity_game.getHeight() / 2 - object.getHeight() / 2);
                                        player.setX(0);
                                        player.setY(activity_game.getHeight() / 2 - player.getHeight() / 2);
                                    } else {
                                        object.setX(activity_game.getWidth() / 2 - object.getWidth() / 2);
                                        object.setY(-object.getHeight());
                                    }
                                }
                            });
                            init = true;
                        }
                        if(!pause) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mode.equals(Mode.BIRD)) {
                                        object.setX(object.getX() - activity_game.getWidth() / getSpeed());
                                    } else {
                                        object.setY(object.getY() + activity_game.getHeight() / getSpeed());
                                        if (mode.equals(Mode.ZIGZAG) || mode.equals(Mode.EVERYTHING) || (mode.equals(Mode.CONTROL) && !control)) {
                                            if (lefttoright) {
                                                object.setX(object.getX() + activity_game.getWidth() / getSpeed());
                                                if (object.getX() >= activity_game.getWidth() - object.getWidth()) {
                                                    lefttoright = false;
                                                }
                                            } else {
                                                object.setX(object.getX() - activity_game.getWidth() / getSpeed());
                                                if (object.getX() <= 0) {
                                                    lefttoright = true;
                                                }
                                            }
                                        }
                                    }
                                }
                            });
                            Rect o = new Rect((int) object.getX(), (int) object.getY(), (int) object.getX() + object.getWidth(), (int) object.getY() + object.getHeight());
                            Rect p = new Rect((int) player.getX(), (int) player.getY(), (int) player.getX() + player.getWidth(), (int) player.getY() + player.getHeight());
                            if (Rect.intersects(o, p)) {
                                if(currentImg == R.drawable.spinach2){
                                    endGame();
                                }else{
                                    next();
                                }
                            } else if ((mode.equals(Mode.BIRD) && object.getX() < -object.getWidth()) || (object.getY() >= activity_game.getHeight())) {
                                if(currentImg == R.drawable.spinach2){
                                    next();
                                }else{
                                    endGame();
                                }
                            }
                        }
                    }
                    try {
                        Thread.sleep(17);
                    } catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        });
        gameThread.start();
    }

    private int getSpeed(){
        if(mode.equals(Mode.QUICK)){
            return 25;
        }
        if(points < 5){
            return 120;
        }else if(points < 20){
            return 100;
        }else if(points < 30){
            return 80;
        }else if(points < 50){
            return 60;
        }else if(points < 70){
            return 50;
        }else if(points < 90){
            return 45;
        }else if(points < 110){
            return 40;
        }else if(points < 130){
            return 35;
        }else if(points < 150){
            return 30;
        }
        return 25;
    }

    public void endGame(){
        playing = false;

        SharedPreferences prefs = getSharedPreferences("PickFallingScore", Context.MODE_PRIVATE);

        int highest = prefs.getInt("highest", 0);
        int highest_mode = prefs.getInt("highest_"+mode.toString(), 0);
        int total = prefs.getInt("total", 0);

        total += points;

        if(points > highest){
            highest = points;
        }
        if(points > highest_mode){
            highest_mode = points;
        }

        prefs.edit().putInt("highest", highest).putInt("highest_"+mode.toString(), highest_mode).putInt("total", total).apply();

        if(GoogleSignIn.getLastSignedInAccount(this) != null){
            Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .submitScoreImmediate(getString(R.string.leaderboard_total_score), total);
            Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .submitScoreImmediate(getString(R.string.leaderboard_highest_score), highest);
            Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .submitScoreImmediate(getString(mode.getLeaderboard()), highest_mode);
        }

        Intent start = new Intent(GameActivity.this, LoseActivity.class);
        start.putExtra("points", points);
        start.putExtra("mode", mode.toString());
        startActivity(start);
        finish();
    }

    public void next(){
        nexting = true;
        if(currentImg == R.drawable.spinach1){
            points += 10;
        }else if(currentImg != R.drawable.spinach2){
            points++;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Random r = new Random();
                if (mode.equals(Mode.RANDOM) || mode.equals(Mode.BONUS) || mode.equals(Mode.EVERYTHING) || mode.equals(Mode.CONTROL)) {
                    ArrayList<Integer> ts = new ArrayList<>();
                    ts.add(R.drawable.spinach);
                    if(mode.equals(Mode.RANDOM) || mode.equals(Mode.EVERYTHING)){
                        ts.add(R.drawable.heart);
                        ts.add(R.drawable.bubble);
                        ts.add(R.drawable.fish);
                        ts.add(R.drawable.maggot);
                    }
                    if(mode.equals(Mode.BONUS) || mode.equals(Mode.EVERYTHING) || mode.equals(Mode.CONTROL)){
                        ts.add(R.drawable.spinach1);
                        ts.add(R.drawable.spinach2);
                    }
                    currentImg = ts.get(r.nextInt(ts.size()));
                    object.setImageResource(currentImg);
                }
                if (mode.equals(Mode.RANDOM2)) {
                    ArrayList<Integer> ts = new ArrayList<>();
                    ts.add(R.drawable.player);
                    ts.add(R.drawable.unicorn);
                    ts.add(R.drawable.fish);
                    ts.add(R.drawable.cat);
                    ts.add(R.drawable.bird);
                    ts.add(R.drawable.crab);
                    player.setImageResource(ts.get(r.nextInt(ts.size())));
                }
                if (mode.equals(Mode.BIRD)) {
                    object.setX(activity_game.getWidth());
                    object.setY(Math.max(Math.min(r.nextFloat() * ((float) activity_game.getHeight()), activity_game.getHeight() - object.getHeight()), score.getY() + score.getHeight()));
                } else {
                    object.setY(-object.getHeight());
                    object.setX(Math.min(r.nextFloat() * ((float) activity_game.getWidth()), activity_game.getWidth() - object.getWidth()));
                }
                if(mode.equals(Mode.CONTROL)){
                    control = !control;
                }
                score.setText(String.format(getString(R.string.score), points));
                nexting = false;
            }
        });
    }

}
