package moe.exmagic.tricks.bangumiinfo.utils;

import android.graphics.Bitmap;

/**
 * Created by tricks on 17-1-4.
 */


public class DataType {
    public static class SearchResultItem {
        public String  Title;           // Item　汉语言标题
        public String  OriginalTitle;   // Item　源语言标题
        public String  Info;            // Item　简介
        public int     ItemType;        // Item　类型
        public int     ItemId;          // Item　的BangumiID
        public int     Rank;            // Item  排名
        public float   Score;           // Item　评分
        public String  RankN;           // Item　评分人数
        public String  DetailUrl;       // Item　的Bangumi细明页
        public String  CoverUrl;        // Item　封面链接
        public Bitmap  Cover;           // Item　封面图片
        public boolean isCoverLoading;  // Item　封面图片是否在加载中
    }
}