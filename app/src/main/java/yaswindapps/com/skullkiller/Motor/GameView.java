package yaswindapps.com.skullkiller.Motor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

import yaswindapps.com.skullkiller.Juego.Enemy;
import yaswindapps.com.skullkiller.Juego.Heroe;
import yaswindapps.com.skullkiller.MainActivity;
import yaswindapps.com.skullkiller.R;
import yaswindapps.com.skullkiller.ScoreActivity;
import yaswindapps.com.skullkiller.SplashActivity;

public class GameView extends SurfaceView {
    private SurfaceHolder holder;
    public static GameThread gameThread;

    public static int anchoPantalla;
    public static int altoPantalla;

    private int spawnCD;
    public  int kills = 0;
    public int baseHP = 500;
    public int maxBaseHP = 500;
    private boolean hit = false;

    Bitmap sueloBmp = null;
    Bitmap heroeBmp = null;
    Bitmap enemybmp = null;

    public static Heroe heroe;
    ArrayList<Enemy> enemys;


    //Dificultad
    public int tiempoSpawn;
    public int damageToBase;
    public static int heroeHP;
    public static int enemyHP;
    public static int probEnemyHit;
    public static int maxEnemySpeed;

    public GameView(Context context) {
        super(context);

        switch (SplashActivity.dificultad) {
            case 1:
                tiempoSpawn = 80;
                damageToBase = 25;
                heroeHP=5;
                probEnemyHit=50;
                enemyHP=1;
                maxEnemySpeed=7;
                break;
            case 2:
                tiempoSpawn = 55;
                damageToBase = 50;
                heroeHP=3;
                probEnemyHit=75;
                enemyHP=2;
                maxEnemySpeed=10;
                break;
            case 3:
                tiempoSpawn = 30;
                damageToBase = 100;
                heroeHP = 2;
                probEnemyHit=90;
                enemyHP=4;
                maxEnemySpeed=15;
                break;
        }

        spawnCD = 100;

        enemys = new ArrayList<>();

        gameThread = new GameThread(this);
        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                gameThread.setRunning(false);
                sueloBmp = null;
                heroeBmp = null;
                enemybmp = null;
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                loadBitmaps();
                anchoPantalla = getWidth();
                altoPantalla = getHeight();
                createHeroe();
                gameThread.setRunning(true);
                gameThread.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
            }
        });
    }

    private void loadBitmaps() {
        sueloBmp = BitmapFactory.decodeResource(getResources(), R.drawable.suelo);
        heroeBmp = BitmapFactory.decodeResource(getResources(), R.drawable.killer);
        enemybmp = BitmapFactory.decodeResource(getResources(), R.drawable.skull);
    }


    private void createHeroe() {

        heroe = new Heroe(this, heroeBmp, 3);
    }

    private void createEnemy() {
        if (enemys.size() == 0) {
            enemys.add(new Enemy(this, enemybmp, 0));
        } else {
            int c = altoPantalla / Enemy.frameHeight;
            enemys.add(new Enemy(this, enemybmp, (int) (Math.random() * c)));
            MainActivity.ma.humo();
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        synchronized (getHolder()) {
            heroe.attack(enemys);
        }
        return super.onTouchEvent(event);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        pintarFondo(canvas);
        spawn();
        for (int i = 0; i < enemys.size(); i++) {
            enemys.get(i).onDraw(canvas);
        }
        heroe.onDraw(canvas);
        pintarStats(canvas);
        if (hit) {
            hit = false;
            flash(canvas);
        }
    }

    private void flash(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, anchoPantalla, altoPantalla, paint);
    }

    private void spawn() {
        if (spawnCD-- <= 0) {
            createEnemy();
            spawnCD = (int) (Math.random() * tiempoSpawn); //Cargar from preferencias;
        }

        checkEnemys();
    }


    private void pintarFondo(Canvas canvas) {
        Rect src = new Rect(0, 0, sueloBmp.getWidth(), sueloBmp.getHeight());
        Rect dst = new Rect(0, 0, anchoPantalla, altoPantalla);
        canvas.drawBitmap(sueloBmp, src, dst, null);
    }

    private void pintarStats(Canvas canvas) {
        Paint p = new Paint();
        p.setColor(Color.YELLOW);
        p.setTextSize(32);
        canvas.drawText("KILLS: " + kills, getWidth() - 175, 45, p);
        p.setColor(Color.RED);

        int sx = anchoPantalla / 2 - maxBaseHP;

        int sy = altoPantalla / 25;

        canvas.drawRect(sx, sy, sx + maxBaseHP, sy + 25, p);
        p.setColor(Color.GREEN);
        canvas.drawRect(sx, sy, sx + baseHP, sy + 25, p);
        p.setColor(Color.BLACK);
        p.setTextSize(16);
        canvas.drawText(baseHP + "/" + maxBaseHP, sx + maxBaseHP / 2, sy + 23, p);
    }

    public void endGame() {
        gameThread.setRunning(false);
        MainActivity.ma.finPartida(kills);
    }

    public void reachBase(Enemy enemy) {
        MainActivity.ma.punch();
        MainActivity.ma.vibrar(200);
        baseHP -= (int) (Math.random() * 5) + damageToBase;
        enemys.remove(enemy);
        hit = true;
        if (baseHP <= 0) endGame();
    }

    public void checkEnemys() {
        for (int i = 0; i < enemys.size(); i++) {
            if (enemys.get(i).isMuerto()) {
                enemys.remove(i);
                kills++;
            }
        }
    }

}