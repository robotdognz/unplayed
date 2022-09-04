package sound;

import android.media.AudioAttributes;
import android.media.SoundPool;

import processing.core.PApplet;
import com.flypuppy.unplayed.R;

public class SoundManager {
    SoundPool soundPool;
    int test1, test2;

    public SoundManager(PApplet p) {
        PApplet.println("Starting sound manager");
//        AudioAttributes audioAttributes = new AudioAttributes.Builder()
//                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
//                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(3)
//                .setAudioAttributes(audioAttributes)
                .build();

        test1 = soundPool.load(p.getContext(), R.raw.test, 1);

        PApplet.println("Sound manager started");
    }

    public void test() {
        PApplet.println("Sound manager test started");
        soundPool.play(test1, 1, 1, 0, 0, 1);
        PApplet.println("Sound manager test ended");
    }

}
