package moe.exmagic.tricks.bangumiinfo.utils;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Created by SternW Zhang on 17-1-4.
 * 公有数据类型都定义在这里
 */


public class DataType {
    public static class SearchResultItem {
        public String  Title;           // Item　汉语言标题
        public String  OriginalTitle;   // Item　源语言标题
        public String  Info;            // Item　简介（超级简洁）
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
    public static class UserItem{
        public String UserID;
        public String UserNickname;
        public Bitmap UserHeader;
        public String UserHeaderUrl;
        public boolean isHeaderLoading;
    }
    public static class DetailItem{
        public SearchResultItem         BaseItem;   // base
        public String                   Summary;    // 简介（普通细节）
        public Map<String,EpItem>       Eps;
        public ArrayList<String>        Tags;
        public ArrayList<BlogItem>      Blogs;
        public ArrayList<CharacterItem> CharactersList;
        public ArrayList<CommentItem>   Comments;
        public ArrayList<TopicItem>     Topics;
        public Map<String,ArrayList<PersonItem>> KVInfo;     // key-value infomation
    }
    public static class CharacterItem{
        public String CharacterName;
        public String CharacterTranslation;
        public String CharacterDetailUrl;
        public String CharacterType;
        public String CVName;
        public String CVDetailUrl;
        public String CommentNumber;
    }
    public static class CommentItem{
        public UserItem User;
        public Date     SubmitDatetime;
        public int      Score;
        public String   Comment;
    }
    public static class BlogItem{
        public String   Title;
        public UserItem Submitter;
        public String   SubmitDatetime;
        public String   BlogPreview;
        public String   BlogID;
        public String   BlogCommentNumber;
    }

    public static class PersonItem{
        public String   Name;
        public String   Translation;
        public String   PersonID;
    }
    public static class EpItem{
        public String   Title;
        public String   Translation;
        public String   Episode;
        public int      CommentsNumber;     // cannot get this filed
        public String   EpID;
        public boolean  isAvailable;
    }
    public static class TopicItem{
        public String       Title;
        public UserItem     Submitter;
        public int          RepliesNumber;
        public String       SubmitDate;
        public String       TopicID;
    }
}