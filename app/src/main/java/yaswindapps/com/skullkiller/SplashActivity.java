package yaswindapps.com.skullkiller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class SplashActivity extends AppCompatActivity {

    public static MediaPlayer mp;
    /** Duración **/
    private final int SPLASH_DISPLAY_SEG = 10000;

    public static boolean sonido;
    public static boolean vibracion;
    public static int dificultad;


    /** Llamada cuando la activity es creada */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash);

        SharedPreferences prefs = getSharedPreferences("yaswindapps.com.skullkiller_preferences",MODE_PRIVATE);

        sonido = prefs.getBoolean("pSonido",true);

        vibracion = prefs.getBoolean("pVibrar", true);

        dificultad = Integer.parseInt(prefs.getString("pDificultad", "1"));


        /**musica de inicio para splash**/
        if(sonido){
            mp = MediaPlayer.create(getApplicationContext(), R.raw.splash);
            mp.start();
        }


        /** New Handler para iniciar el MainActivity y cerrar el splash despues de 10 segundos**/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /** Creacion de un intent que iniciará el main activity **/
                Intent mainIntent = new Intent(SplashActivity.this, MenuActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                if(sonido) mp.release();
                SplashActivity.this.finish();

            }
        }, SPLASH_DISPLAY_SEG);
    }

    @Override
    public void onBackPressed(){
        if(sonido) {
            mp.release();
        }
        finish();
    }
}
