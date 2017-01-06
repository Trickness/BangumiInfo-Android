package moe.exmagic.tricks.bangumiinfo.utils;

import android.graphics.Bitmap;

/**
 * Created by tricks on 17-1-4.
 */


public class DataType {
    public static class SearchResultItem {
        public String  Title;
        public String  OriginalTitle;
        public String  Info;
        public int     ItemType;
        public int     ItemId;
        public int     Rank;
        public float   Score;
        public String  RankN;      //  评分人数
        public String  DetailUrl;
        public String  CoverUrl;
        public Bitmap  Cover;
        public boolean isLoading = false;
    }
}