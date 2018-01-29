package moe.exmagic.tricks.banguminews.WebSpider;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by Stern on 2018/1/26.
 */

public interface OnApiResponseListener {
    void onSuccess(JSONObject object);
    void onFailed(JSONObject object);
}
