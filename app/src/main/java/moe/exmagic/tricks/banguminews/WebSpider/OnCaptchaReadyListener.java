package moe.exmagic.tricks.banguminews.WebSpider;

import android.graphics.Bitmap;

/**
 * Created by Stern on 2018/1/22.
 */

public interface OnCaptchaReadyListener {
    void onSuccess(Bitmap image);
    void onFailed();
    void onBusy();
}
