package yaswindapps.com.skullkiller;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener{

    Button btJugar;
    Button btInstrucciones;
    Button btSalir;
    Button btPreferencias;
    MediaPlayer musica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        SharedPreferences prefs = getSharedPreferences("yaswindapps.com.skullkiller_preferences", MODE_PRIVATE);

        SplashActivity.sonido = prefs.getBoolean("pSonido", true);


        SplashActivity.vibracion = prefs.getBoolean("pVibrar", true);

        SplashActivity.dificultad = Integer.parseInt(prefs.getString("pDificultad", "1"));

        /*if(SplashActivity.sonido) {
            musica = MediaPlayer.create(getApplicationContext(), R.raw.menu);
            musica.setLooping(true);
            musica.start();
        }*/



        btJugar = (Button) findViewById(R.id.btJugar);
        btInstrucciones = (Button) findViewById(R.id.btInstrucciones);
        btPreferencias = (Button) findViewById(R.id.btPreferencias);
        btSalir = (Button) findViewById(R.id.btSalir);

        btJugar.setOnClickListener(this);
        btInstrucciones.setOnClickListener(this);
        btPreferencias.setOnClickListener(this);
        btSalir.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btJugar:
                if(SplashActivity.sonido && musica!=null)
                    musica.release();
                Intent i = new Intent(this,MainActivity.class);
                startActivity(i);
                finish();
                break;
            case R.id.btInstrucciones:
                Intent i2 = new Intent(this,InstruccionesActivity.class);
                startActivity(i2);
                break;
            case R.id.btPreferencias:
                Intent i3 = new Intent(this,PreferenciasActivity.class);
                startActivity(i3);
                break;
            case R.id.btSalir:
                if(SplashActivity.sonido)
                musica.release();
                finish();
                break;
        }

    }

    @Override
    public void onPause(){
        super.onPause();
    }


    @Override
    public void onResume(){
        super.onResume();

        SharedPreferences prefs = getSharedPreferences("yaswindapps.com.skullkiller_preferences", MODE_PRIVATE);

        SplashActivity.sonido = prefs.getBoolean("pSonido", true);

        SplashActivity.vibracion = prefs.getBoolean("pVibrar", true);

        SplashActivity.dificultad = Integer.parseInt(prefs.getString("pDificultad", "1"));

        if(musica==null){
            musica = MediaPlayer.create(getApplicationContext(), R.raw.menu);
        }

        if(SplashActivity.sonido && musica!=null) {
            musica.setLooping(true);
            musica.start();
        }

        if(!SplashActivity.sonido && musica.isPlaying()) {
            musica.stop();
            musica=null;
        }


    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
       if(SplashActivity.sonido)
        musica.release();
        finish();
    }
}
