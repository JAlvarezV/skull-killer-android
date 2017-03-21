package yaswindapps.com.skullkiller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import yaswindapps.com.skullkiller.Juego.Heroe;
import yaswindapps.com.skullkiller.Motor.GameView;

public class MainActivity extends AppCompatActivity  implements SensorEventListener{

    SensorManager sensorManager;
    Sensor sensor;

    public static MainActivity ma;

    MediaPlayer musica;
    MediaPlayer humo;
    MediaPlayer latigo;
    MediaPlayer latigoHit;
    MediaPlayer ouch;
    MediaPlayer punch;
    MediaPlayer sword;

    private boolean sonido;
    private boolean vibracion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sonido = SplashActivity.sonido;
        vibracion = SplashActivity.vibracion;
        ma=this;




        if(sonido){
            musica = MediaPlayer.create(getApplicationContext(), R.raw.game);
            musica.setLooping(true);
            if(!musica.isPlaying()){
                musica.start();
            }else{
                musica.stop();
                musica.start();
            }

            humo = MediaPlayer.create(getApplicationContext(), R.raw.smoke);
            latigo = MediaPlayer.create(getApplicationContext(), R.raw.latigo);
            latigoHit=  MediaPlayer.create(getApplicationContext(), R.raw.hitlatigo);
            ouch =  MediaPlayer.create(getApplicationContext(), R.raw.ouch);
            punch = MediaPlayer.create(getApplicationContext(), R.raw.punch);
            sword = MediaPlayer.create(getApplicationContext(), R.raw.smoke);
        }


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else{
            Toast.makeText(MainActivity.this, "El teléfono no tiene acelerómetro.",Toast.LENGTH_SHORT).show();
            finish();
        }



        setContentView(new GameView(this));


    }

    @Override
    public void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this);
       if(sonido) musica.release();
        ma=null;
    }


    @Override
    public void onResume(){
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        ma=this;
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        GameView.gameThread.setRunning(false);
        Intent i = new Intent(getApplicationContext(),MenuActivity.class);
        startActivity(i);
        finish();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        int a = (int)event.values[1];
        int b = (int)event.values[0];

        if (GameView.heroe!=null){
            if(a>1 || a<1) {
                GameView.heroe.setxSpeed(a*4);
            }else{
                GameView.heroe.setxSpeed(0);
            }
            if(b>4 || b<4) {
                GameView.heroe.setySpeed(b*4);
            }else{
                GameView.heroe.setySpeed(0);
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public void finPartida(int score){
        GameView.gameThread.setRunning(false);
        Intent i = new Intent(getApplicationContext(),ScoreActivity.class);
        i.putExtra("kills",score);
        startActivity(i);
        finish();
    }

    public void humo() {
        if(sonido){
            humo.release();
            humo = MediaPlayer.create(getApplicationContext(), R.raw.smoke);
            humo.start();
        }
    }

    public void latigo() {
        if (sonido) {
            if(!latigo.isPlaying()){
                latigo.release();
                latigo = MediaPlayer.create(getApplicationContext(), R.raw.latigo);
                latigo.start();
            }
        }
    }

    public void latigoHit(){
        if(sonido){
            latigoHit.release();
            latigoHit = MediaPlayer.create(getApplicationContext(), R.raw.hitlatigo);
            latigoHit.start();
        }

    }

    public void ouch(){
        if(sonido){
            ouch.release();
            ouch = MediaPlayer.create(getApplicationContext(), R.raw.ouch);
            ouch.start();
        }
    }

    public void sword(){
        if(sonido){
            sword.release();
            sword = MediaPlayer.create(getApplicationContext(), R.raw.sword);
            sword.start();
        }

    }

    public void punch(){
        if(sonido){
            punch.release();
            punch = MediaPlayer.create(getApplicationContext(), R.raw.punch);
            punch.start();
        }
    }

    public void vibrar(int duracion){
        if(vibracion){
            Vibrator vibra = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
            vibra.vibrate(duracion);
        }
    }


}
