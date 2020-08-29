package optinvent.com.raftol;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.Display;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends Activity {

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    Thread splashTread;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        startAnimations();
    }

    private void startAnimations() {
        final int animationTime = 5500;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.name_text);
        anim.reset();
        ImageView toAnim = findViewById(R.id.name_text);
        toAnim.clearAnimation();
        toAnim.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.desc_text);
        anim.reset();
        toAnim = findViewById(R.id.desc_text);
        toAnim.clearAnimation();
        toAnim.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.glow);
        anim.reset();
        toAnim = findViewById(R.id.glow);
        toAnim.clearAnimation();
        toAnim.startAnimation(anim);

        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < animationTime) {
                        sleep(100);
                        waited += 100;
                    }
                    Intent intent = new Intent(SplashActivity.this,
                            MainActivity.class);
                    Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(),
                            R.anim.fadein, R.anim.fadeout).toBundle();
                    startActivity(intent, bundle);
                    SplashActivity.this.finish();
                }
                catch (InterruptedException ignored) {}
                finally {
                    SplashActivity.this.finish();
                }

            }
        };
        splashTread.start();
    }

}