package sound;

import android.media.AudioAttributes;
import android.media.SoundPool;

import processing.core.PApplet;
import com.flypuppy.unplayed.R;

public class SoundManager {
    SoundPool soundPool;
    int test1, test2;

    public SoundManager(PApplet p) {
        soundPool = new SoundPool.Builder().setMaxStreams(3).build();

        test1 = soundPool.load(p.getContext(), R.raw.test, 1);
    }

    public void test() {
        PApplet.println("Sound manager test");
        soundPool.play(test1, 1, 1, 0, 0, 1);
    }

}
