package moe.exmagic.tricks.banguminews.WebSpider;

import com.alibaba.fastjson.*;

/**
 * Created by Stern on 2018/1/20.
 */

public interface OnLoginListener {
    void onSuccess(JSONObject data);
    void onBusy();
    void onFailed(JSONObject data);
}
