package yaswindapps.com.skullkiller;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ScoreActivity extends AppCompatActivity {

    TextView puntos;
    Button menu;
    MediaPlayer musica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        if(SplashActivity.sonido) {
            musica = MediaPlayer.create(this, R.raw.score);
            musica.start();
        }

        puntos = (TextView) findViewById(R.id.tvPuntos);

        int muertes = getIntent().getIntExtra("kills",0);

        puntos.setText("Tu puntuación: "+muertes);

        menu = (Button) findViewById(R.id.btMenu);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SplashActivity.sonido){
                    musica.release();
                }
                Intent i=new Intent(ScoreActivity.this, MenuActivity.class);
                startActivity(i);
                finish();
            }
        });

    }


    @Override
    public void onBackPressed(){
        if(SplashActivity.sonido) {
            musica.release();
        }
        Intent i=new Intent(ScoreActivity.this, MenuActivity.class);
        startActivity(i);
        finish();
    }
}
