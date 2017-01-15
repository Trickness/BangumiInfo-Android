package moe.exmagic.tricks.bangumiinfo.utils;

import android.content.Context;
import android.os.AsyncTask;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import moe.exmagic.tricks.bangumiinfo.SearchResultFragment;

/**
 * Created by SternW Zhang on 17-1-4.
 * WebSpider
 */

public class WebSpider {
    public static String SEARCH_ALL        = "all";
    public static String SEARCH_BANGUMI    = "2";
    public static String SEARCH_BOOK       = "1";
    public static String SEARCH_GAME       = "4";
    public static String SEARCH_MUSIC      = "3";
    public static String SEARCH_3DIM       = "6";
    public static String SEARCH_PERSON     = "person";
    public static String BASE_SITE         = "http://bangumi.tv/";
    public static String USER_AGENT        = "User-Agent: Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:50.0) Gecko/20100101 Firefox/50.0\\r\\n";
    public static String PROTOCOL      = "http:";

    public static int ITEM_TYPE_BANGUMI     = 1;
    public static int ITEM_TYPE_BOOK        = 2;
    public static int ITEM_TYPE_MUSIC       = 3;
    public static int ITEM_TYPE_GAME        = 4;
    public static int ITEM_TYPE_3DIM        = 6;

    private Map<String,String>  Cookies     = null;

    /*
    *  私有方法
    */
    public abstract class DOMParser<RetType>{
        abstract RetType    ParseDOM(Document doc);
        abstract void       UpdateUI(RetType result);
    }
    private class SearchResultParser extends DOMParser<WebSpider.SearchResult>{
        private int currentPage;
        private String searchType;
        private String keyWords;
        private SearchResultFragment fm;
        SearchResultParser(int curpage, String type, String keyword,SearchResultFragment Fm){
            this.currentPage = curpage;
            this.searchType = type;
            this.keyWords = keyword;
            this.fm = Fm;
        }
        @Override
        WebSpider.SearchResult ParseDOM(Document doc) {
            if(doc == null){
                return null;
            }
            Elements Items = doc.getElementsByClass("item");
            WebSpider.SearchResult result = new WebSpider.SearchResult();
            DataType.SearchResultItem resultItem;

            result.currentPage = currentPage;
            result.maxPage = 0;     // !!!
            result.searchType = searchType;
            result.keyWord = keyWords;

            for (Element Item : Items){
                resultItem = new DataType.SearchResultItem();
                resultItem.ItemId = Integer.valueOf(Item.attr("id").substring(5));
                if(Item.getElementsByTag("img").first() != null)
                    resultItem.CoverUrl = Item.getElementsByTag("img").first().attr("src");
                else
                    resultItem.CoverUrl = "";
                resultItem.DetailUrl = WebSpider.BASE_SITE + "subject/" + resultItem.ItemId;

                Item = Item.getElementsByClass("inner").first();

                resultItem.ItemType = Integer.parseInt(Item.getElementsByClass("ll").first().attr("class").substring(30,31));
                resultItem.Title = Item.getElementsByTag("a").first().ownText();

                if (Item.getElementsByClass("grey").first() != null) {
                    resultItem.OriginalTitle = Item.getElementsByClass("grey").first().ownText();
                }else{
                    resultItem.OriginalTitle = "";
                }
                resultItem.Info = Item.getElementsByClass("info").first().ownText();

                if (Item.getElementsByClass("rank").first() != null){
                    resultItem.Rank = Integer.valueOf(Item.getElementsByClass("rank").first().ownText());
                }else{
                    resultItem.Rank = 0;
                }

                if (Item.getElementsByClass("fade").first() != null){
                    resultItem.Score = Float.parseFloat(Item.getElementsByClass("fade").first().ownText());
                }else{
                    resultItem.Score = 0;
                }

                if (Item.getElementsByClass("tip_j").first() != null)
                    resultItem.RankN = Item.getElementsByClass("tip_j").first().ownText();

                result.result.add(resultItem);
            }
            if(doc.getElementById("multipage") != null && doc.getElementById("multipage").getElementsByClass("p").last() != null){
                String tmpUrl = doc.getElementById("multipage").getElementsByClass("p").last().attr("href");
                String[] tmpStrs = tmpUrl.split("=");
                result.maxPage = Integer.parseInt(tmpStrs[tmpStrs.length-1]);
            }else{
                result.maxPage = 10;
            }
            return result;
        }
        @Override
        void UpdateUI(WebSpider.SearchResult result) {
            if(result.currentPage == 1)
                fm.updateUI(result);
            else
                fm.appendUI(result);
        }
    }
    private class ItemDetailParser extends DOMParser<DataType.DetailItem>{
        @Override
        DataType.DetailItem ParseDOM(Document doc) {
            if(doc == null)
                return null;
            DataType.DetailItem result = new DataType.DetailItem();
            // parse summary
            if (doc.getElementById("subject_summary") != null)
                result.Summary = doc.getElementById("subject_summary").text();
            // parse episode
            if (doc.getElementsByClass("subject_prg").size() == 1){
                Elements elements = doc.getElementsByClass("prg_list").first().children();
                DataType.EpItem episode;
                for(Element e : elements){
                    episode = new DataType.EpItem();
                    e = e.child(0);

                    episode.EpID = e.attr("href").substring(4);
                    episode.Episode = e.text();
                    episode.Title = e.attr("title");
                    episode.isAvailable = e.attr("class").endsWith("r");

                    result.Eps.put(episode.Episode,episode);
                }
            }
            // parse tags
            Element tmp = doc.getElementsByClass("subject_tag_section").first();
            if(tmp != null){
                tmp = tmp.getElementsByClass("inner").first();
                for(Element e : tmp.getElementsByTag("a")){
                    result.Tags.add(e.text());
                }
            }
            //parse characters
            if(doc.getElementById("browserItemList") != null){
                DataType.CharacterItem character;
                for (Element e : doc.getElementById("browserItemList").children()){
                    character = new DataType.CharacterItem();
                    character.CommentNumber = e.getElementsByClass("fade rr").first().text();
                    character.CharacterName = e.getElementsByClass("userImage").first().parent().text();
                    if(e.getElementsByClass("tip").size() == 1){
                        character.CharacterTranslation = e.getElementsByClass("tip").first().text();
                    }
                    character.CharacterDetailUrl = WebSpider.PROTOCOL + e.getElementsByClass("userImage").first().children().first().attr("src");

                    e = e.getElementsByClass("tip_j").first();

                    character.CharacterType = e.child(0).child(0).text();
                    if(e.getElementsByTag("a").size() == 1){
                        character.CVName = e.getElementsByTag("a").first().text();
                        character.CVDetailUrl = WebSpider.BASE_SITE + e.getElementsByTag("a").first().attr("href").substring(1);
                    }
                    result.CharactersList.add(character);
                }
            }
            // parse blog
            if(doc.getElementById("entry_list") != null){
                DataType.BlogItem blog;
                for (Element e : doc.getElementById("entry_list").children()){
                    blog = new DataType.BlogItem();
                    blog.Title = e.getElementsByClass("title").first().child(0).text();
                    blog.BlogID = e.getElementsByClass("title").first().child(0).attr("href").substring(6);
                    blog.Submitter.UserHeaderUrl = e.getElementsByTag("img").first().attr("src");
                    blog.Submitter.UserNickname = e.getElementsByClass("tip_j").first().child(0).text();
                    blog.Submitter.UserID = e.getElementsByClass("tip_j").first().child(0).attr("href").substring(6);
                    blog.Submitter.isHeaderLoading = false;
                    blog.SubmitDatetime = e.getElementsByClass("time").first().text();
                    blog.BlogCommentNumber = e.getElementsByClass("orange").first().text();
                    blog.BlogPreview = e.getElementsByClass("content").first().text();
                    result.Blogs.add(blog);
                }
            }
            // parse topics
            if(doc.getElementsByTag("tbody").size() == 1){
                DataType.TopicItem topic;
                for (Element e : doc.getElementsByTag("TopicItem")){
                    topic = new DataType.TopicItem();
                    topic.Title = e.child(0).child(0).text();
                    topic.TopicID = e.child(0).child(0).attr("href").substring(15);
                    topic.Submitter.UserID = e.child(1).child(0).attr("href").substring(6);
                    topic.Submitter.UserNickname = e.child(1).child(0).text();
                    topic.RepliesNumber = Integer.parseInt(e.child(2).child(0).text().split(" ")[0]);
                    topic.SubmitDate = e.child(3).child(0).text();
                    result.Topics.add(topic);
                }
            }
            // parse KV-info
            ArrayList<String> value;
            String key;
            for (Element e : doc.getElementById("infobox").children()){
                for (String s : e.text().split(";")){
                    value = new ArrayList<>();
                    String[] ss = s.split(":");
                    key = ss[0];
                    Collections.addAll(value, ss[1].split("、"));
                    result.KVInfo.put(key,value);
                }
            }
            return result;
        }
        @Override
        void UpdateUI(DataType.DetailItem result) {
        }
    }

    private class AsyncTaskNetwork extends  AsyncTask<Void,Void,Document>{
        private WebSpider   mParent;
        private DOMParser<SearchResult> mParser;
        private String TargetUrl;
        private AsyncTaskNetwork(WebSpider parent, DOMParser parser, String url){
            mParent = parent;
            mParser = parser;
            TargetUrl = url;
        }
        @Override
        protected Document doInBackground(Void... params) {
            Document doc;
            try {
                if(mParent.Cookies == null){
                    mParent.Cookies = Jsoup.connect(WebSpider.BASE_SITE).timeout(3000).execute().cookies();
                    if(mParent.Cookies == null)
                        return null;
                }
                doc = Jsoup.connect(TargetUrl)
                        .cookies(mParent.Cookies)
                        .timeout(3000)
                        .get();
            }catch (IOException e){
                mParent.unlock();
                return null;
            }
            this.mParent.unlock();
            return doc;
        }
        @Override
        protected void onPostExecute(Document doc){
            mParser.UpdateUI(mParser.ParseDOM(doc));
        }
    }
    private class FetchResponse extends AsyncTask<WebSpider,Void,Void> {
        @Override
        protected Void doInBackground(WebSpider... params){
            WebSpider parent = params[0];
            Connection.Response res;
            try {
                res = Jsoup.connect(WebSpider.BASE_SITE).timeout(3000).execute();
            }catch (IOException e){
                return null;
            }
            parent.Cookies = res.cookies();
            return null;
        }
    }

    private int syn_lock = 0;
    private boolean lock(){
        if(this.syn_lock == 1){
            return false;
        }
        this.syn_lock = 1;
        return true;
    }
    private void    unlock(){
        this.syn_lock = 0;
    }

    private static String StringFilter(String   str){
        // 只允许字母和数字
        // String   regEx  =  "[^a-zA-Z0-9]";
        // 清除掉所有特殊字符
        String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p   =   Pattern.compile(regEx);
        Matcher m   =   p.matcher(str);
        return   m.replaceAll("+").trim();
    }


    /*
    * 定义全局变量
    */
    private static WebSpider sWebSpider;
    private WebSpider(Context context) {
        new FetchResponse().execute(this);      // set cookies
    }

    /*
    * 公共类
    */
    public static class SearchResult{
        public int maxPage = 0;
        public int currentPage = 0;
        public String searchType = "0";
        public String keyWord = "";
        public ArrayList<DataType.SearchResultItem> result = new ArrayList<>();
    }

    /*
    * 公共方法
    */
    public void Search(String keyWords, String type, int page, SearchResultFragment fm){
        String targetUrl;
        targetUrl = BASE_SITE + "subject_search/" + WebSpider.StringFilter(keyWords) + "?cat=" + type;
        if (page > 0){
            targetUrl += "&page=" + page;
        } else {
            page = 1;
        }
        if (!this.lock())
            return;
        new AsyncTaskNetwork(this,new SearchResultParser(page,type,keyWords,fm),targetUrl).execute();
    }
    public void GetItemDetail(String Url){
        new AsyncTaskNetwork(this,new ItemDetailParser(),Url).execute();
    }
    public static WebSpider get(Context context){
        if(sWebSpider == null){
            sWebSpider = new WebSpider(context);
        }
        return sWebSpider;
    }
    public static String getStrType(int type){
        String strType = "" + type;
        if(strType.equals(WebSpider.SEARCH_BANGUMI)){
            return "动画";
        } else if(strType.equals(WebSpider.SEARCH_3DIM)){
            return "三次元";
        } else if(strType.equals(WebSpider.SEARCH_BOOK)){
            return "书籍";
        } else if(strType.equals(WebSpider.SEARCH_GAME)){
            return "游戏";
        } else if(strType.equals(WebSpider.SEARCH_MUSIC)){
            return "音乐";
        } else if(strType.equals(WebSpider.SEARCH_PERSON)){     // BUG
            return "人物";
        }
        return "";
    }
}

