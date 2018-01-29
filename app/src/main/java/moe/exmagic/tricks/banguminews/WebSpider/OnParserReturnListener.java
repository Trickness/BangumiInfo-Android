package moe.exmagic.tricks.banguminews.WebSpider;

import org.json.JSONObject;

/**
 * Created by Stern on 2018/1/23.
 */

public interface OnParserReturnListener<RetType> {
    void onSuccess(RetType data);
}
