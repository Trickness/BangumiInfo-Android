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
        public String Signature;
    }
    public static class TagItem{
        public String   Tag;
        public String   Num;
    }
    public static class DetailItem{
        public SearchResultItem         BaseItem;   // base
        public String                   Summary;    // 简介（普通细节）
        public TreeMap<Integer,EpItem>   Eps;
        public ArrayList<TagItem>        Tags;
        public ArrayList<BlogItem>      Blogs;
        public ArrayList<CharacterItem> CharactersList;
        public ArrayList<CommentItem>   Comments;
        public ArrayList<SubjectTopicItem>     Topics;
        public Map<String,ArrayList<String>> KVInfo;     // key-value infomation
        public ArrayList<Integer>       ScoreDetail;
        public Map<String,ArrayList<PersonItem>>   StaffInfo;
        public String                   AirDate;
        public String                   AirWeekday;
    }
    public static class CharacterItem{
        public String CharacterName;
        public String CharacterTranslation;
        public int    CharacterID;
        public String CharacterRoleName;
        public String CharacterGender;
        public String CommentNumber;
        public String CollectsNumber;       //  收藏量
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
        public String BlogReplyNumber;
        public String   BlogText;
    }

    public static class PersonItem{
        public String   Name;
        public String   Translation;
        public String   PersonID;
        public String   HeaderUrl;
    }
    public static class EpItem{
        public String   Title;
        public String   Translation;
        public String   Episode;
        public int      Index;
        public int      CommentsNumber;     // cannot get this filed
        public String   EpID;
        public String   Status;
        public String   Summary;
        public String   AirDate;
        public String   Duration;
        public int      EpType; //>>
    }
    public static class SubjectTopicItem {
        public String       Title;
        public UserItem     Submitter;
        public int          RepliesNumber;
        public String       SubmitDate;
        public String       LastReplyDate;
        public String       TopicID;
        public String       SubjectID;
    }
    public static class SearchResult{
        public int maxPage = 0;
        public int currentPage = 0;
        public String searchType = "0";
        public String keyWord = "";
        public ArrayList<BgmDataType.SearchResultItem> result = new ArrayList<>();
    }
    public static class TopicCompactItem{
        public String       Title;
        public UserItem     Submitter;
        public String       RepliesNumber;
        public String       TopicID;
        public String       DepartmentType;         // subject or group
        public String       DepartmentName;     //      subject name or group name
        public String       DepartmentId;       //      subject id or group id
    }
    public static class TimeLineCommon {     // 通常，比如看过，抛弃等，以及收藏角色
        public UserItem     Submitter;
        public String       TimeLineId;
        public String       ParentDepartmentType;
        public String       ParentItemTitle;
        public String       ParentItemId;
        public String       Platform;
        public String       StrDate;

        public String       StrAction;
        public ArrayList<TimeLineTargetItem>    TargetItems;
        public String       StrObject;      // 宾语
        public String       Comment;
        public String       Stars;

        // -->  SubmitUser + StrAction + TimeLineCommonItem + StrObject
        // -->  Stars
        // -->  Comments
        // -->  TargetTime + Platform
        // 例如 永夜的魔法使    读过           大春物           第二话
        //      ⭐⭐⭐⭐                                        图图图图
        //      “真吉尔好看”                                 图图图图
        //      13分钟前  web                                  图图图图
    }
    public static class TimeLineTargetItem{
        public String       Title;
        public String       DepartmentType;     // user or subject
        public String       DepartmentId;
        public String       HeaderUrl;
    }

    public static class TopicItem{
        public UserItem     Submitter;
        public String       SubmitterSignature;
        public String       TopicTitle;
        public String       TopicContent;
        public String       SubmitDate;
        public ArrayList<TopicReplyItem> Replies;
    }

    public static class TopicReplyItem{
        public String       ReplyPostId;
        public String       ReplyIndex;
        public UserItem     Submitter;
        public String       SubmitterSignature;
        public String       ReplyContent;
        public String       ReplyDate;
        public ArrayList<TopicReplyItem> SubReplies;
    }
}