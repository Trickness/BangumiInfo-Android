package moe.exmagic.tricks.banguminews.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by SternW Zhang on 17-1-4.
 * 公有数据类型都定义在这里
 */


public class BgmDataType {
    public static class SearchResultItem implements Serializable{
        public String   Title;           // Item　汉语言标题
        public String   OriginalTitle;   // Item　源语言标题
        public String   Info;            // Item　简介（超级简洁）
        public int      ItemType;        // Item　类型
        public int      ItemId;          // Item　的BangumiID
        public int      Rank;            // Item  排名
        public float    Score;           // Item　评分
        public String   RankN;           // Item　评分人数
        public String   DetailUrl;       // Item　的Bangumi细明页
        public String   CoverUrl;        // Item　封面链接
    }
    public static class UserItem implements Serializable{
        public String UserID;
        public String UserNickname;
        public String UserHeaderUrl;
        public boolean isHeaderLoading;
    }
    public static class DetailItem{
        public SearchResultItem         BaseItem;   // base
        public String                   Summary;    // 简介（普通细节）
        public TreeMap<String,EpItem>   Eps;
        public ArrayList<String>        Tags;
        public ArrayList<BlogItem>      Blogs;
        public ArrayList<CharacterItem> CharactersList;
        public ArrayList<CommentItem>   Comments;
        public ArrayList<TopicItem>     Topics;
        public Map<String,ArrayList<String>> KVInfo;     // key-value infomation
        public ArrayList<Integer>       ScoreDetail;
    }
    public static class CharacterItem{
        public String CharacterName;
        public String CharacterTranslation;
        public int    CharacterID;
        public String CharacterType;
        public String CharacterGender;
        public String CommentNumber;
        public String CharacterHeaderUrl;
        public ArrayList<PersonItem> CVInfo;
    }
    public static class CommentItem{
        public UserItem User;
        public String   SubmitDatetime;
        public int      Score;
        public String   Comment;
    }
    public static class BlogItem implements Serializable{
        public String   Title;
        public UserItem Submitter;
        public String   SubmitDatetime;
        public String   BlogPreview;
        public String   BlogID;
        public String   BlogCommentNumber;
        public String   BlogText;
    }

    public static class PersonItem{
        public String   Name;
        public String   Translation;
        public int      PersonID;
        public String   HeaderUrl;
    }
    public static class EpItem{
        public String   Title;
        public String   Translation;
        public String   Episode;
        public int      CommentsNumber;     // cannot get this filed
        public String   EpID;
        public boolean  isAvailable;
    }
    public static class TopicItem {
        public String       Title;
        public UserItem     Submitter;
        public int          RepliesNumber;
        public String       SubmitDate;
        public String       TopicID;
    }
    public static class SearchResult{
        public int maxPage = 0;
        public int currentPage = 0;
        public String searchType = "0";
        public String keyWord = "";
        public ArrayList<BgmDataType.SearchResultItem> result = new ArrayList<>();
    }
}