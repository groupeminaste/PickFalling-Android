package fr.zabricraft.pickfalling;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        LinearLayout menu = findViewById(R.id.menuLayout);

        int textColor = Color.parseColor("#EEEEEE");
        int backgroundColor = Color.parseColor("#ED7F10");
        int lockedColor = Color.parseColor("#CECECE");

        SharedPreferences prefs = getSharedPreferences("PickFallingScore", Context.MODE_PRIVATE);

        int total = prefs.getInt("total", 0);

        for(final Mode m : Mode.values()){
            Button button = new Button(this);
            button.setTextSize(30);
            button.setTextColor(textColor);
            button.setTransformationMethod(null);
            if(total >= m.getScore()){
                button.setText(String.format(getString(R.string.mode_button), m.toString(), String.format(getString(R.string.highest), prefs.getInt("highest_"+m.toString(), 0))));
                button.setBackgroundColor(backgroundColor);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent start = new Intent(MenuActivity.this, GameActivity.class);
                        start.putExtra("mode", m.toString());
                        startActivity(start);
                        finish();
                    }
                });
            }else{
                button.setText(String.format(getString(R.string.mode_button), m.toString(), String.format(getString(R.string.unlock), m.getScore())));
                button.setBackgroundColor(lockedColor);
            }
            menu.addView(button);
            menu.addView(new TextView(this));
        }
    }

}
