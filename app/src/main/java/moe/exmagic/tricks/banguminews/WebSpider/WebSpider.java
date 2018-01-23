package moe.exmagic.tricks.banguminews.WebSpider;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;



import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import moe.exmagic.tricks.banguminews.ActivityCharactersList;
import moe.exmagic.tricks.banguminews.ActivityCommentList;
import moe.exmagic.tricks.banguminews.ActivityItemDetail;
import moe.exmagic.tricks.banguminews.ActivityBlogView;
import moe.exmagic.tricks.banguminews.ActivityBlogList;
import moe.exmagic.tricks.banguminews.Fragments.FragmentHome;
import moe.exmagic.tricks.banguminews.Fragments.FragmentSearchResult;
import moe.exmagic.tricks.banguminews.Utils.BgmDataType.*;


// 第三方库
import com.alibaba.fastjson.*;
/**
 * Created by SternW Zhang on 17-1-4.
 * WebSpider
 */

public class WebSpider {
    public static String APP_NAME          = "BangumiNewS";
    public static String SEARCH_ALL        = "all";
    public static String SEARCH_BANGUMI    = "2";
    public static String SEARCH_BOOK       = "1";
    public static String SEARCH_GAME       = "4";
    public static String SEARCH_MUSIC      = "3";
    public static String SEARCH_3DIM       = "6";
    public static String SEARCH_PERSON     = "person";
    public static String BASE_SITE         = "https://bgm.tv/";
    public static String BAST_API_SITE     = "https://api.bgm.tv/";
    public static String LOGIN_URL         = "FollowTheRabbit";
    public static String USER_AGENT        = "User-Agent: Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:50.0) Gecko/20100101 Firefox/50.0\\r\\n";
    public static String PROTOCOL          = "https:";

    public static int ITEM_TYPE_BANGUMI     = 1;
    public static int ITEM_TYPE_BOOK        = 2;
    public static int ITEM_TYPE_MUSIC       = 3;
    public static int ITEM_TYPE_GAME        = 4;
    public static int ITEM_TYPE_3DIM        = 6;

    private SerializableMap  WebCookies = new SerializableMap();
    private SerializableMap  APICookies  = new SerializableMap();

    /*
    *  私有方法
    */
    private static abstract class DOMParser<RetType>{
        abstract RetType    ParseDOM(Document doc);
        abstract void       UpdateUI(RetType result);
    }
    //  页面分析器
    static abstract class ElementParser{
        static ArrayList<String>        ParseTags(Element E) {
            ArrayList<String> result = new ArrayList<>();
            if(E != null){
                E = E.getElementsByClass("inner").first();
                for(Element e : E.getElementsByTag("a")){
                    result.add(e.text());
                }
            }
            return result;
        }
        static TreeMap<String,EpItem>   ParseEps(Element E){
            TreeMap<String,EpItem> result = new TreeMap();
            if(E == null){
                return result;
            }

            String epType = "";
            Elements elements = E.getElementsByClass("prg_list").first().children();
            EpItem episode;
            for(Element e : elements){
                episode = new EpItem();
                e = e.child(0);

                if(e.tagName() == "span"){
                    epType = e.text() + " ";
                    continue;
                }
                episode.EpID = e.attr("href").substring(4);
                episode.Episode = epType + e.text();
                episode.Title = e.attr("title");
                episode.isAvailable = e.attr("class").endsWith("r");

                result.put(episode.Episode,episode);
            }

            return result;
        }
        static String                   ParseSummary(Element E){
            if(E == null){
                return "";
            }
            return E.getElementById("subject_summary").html().replace("<br />","").replace("&nbsp;","");
        }
        static ArrayList<CharacterItem> ParseCharactersCompact(Element E){
            ArrayList<CharacterItem> result = new ArrayList<>();
            if(E == null){
                return result;
            }
            CharacterItem character;
            for (Element e : E.getElementById("browserItemList").children()){
                character = new CharacterItem();
                character.CommentNumber = e.getElementsByClass("fade rr").first().text();
                character.CharacterName = e.getElementsByClass("userImage").first().parent().text();
                character.CharacterID = Integer.parseInt(e.getElementsByClass("userImage").first().parent().attr("href").substring(11));
                if(e.getElementsByClass("tip").size() == 1){
                    character.CharacterTranslation = e.getElementsByClass("tip").first().text();
                }
                character.CharacterHeaderUrl = WebSpider.PROTOCOL + e.getElementsByClass("userImage").first().children().first().attr("src");

                e = e.getElementsByClass("tip_j").first();

                character.CharacterType = e.child(0).child(0).text();
                if(e.getElementsByTag("a").size() == 1){
                    character.CVInfo = new ArrayList<>();
                    PersonItem person = new PersonItem();
                    person.Name = e.getElementsByTag("a").first().text();
                    person.PersonID = Integer.parseInt(e.getElementsByTag("a").first().attr("href").substring(8));
                    character.CVInfo.add(person);
                }
                result.add(character);
            }
            return result;
        }
        static ArrayList<Integer>           ParseVotes(Element E){
            ArrayList<Integer> result = new ArrayList<>();
            for(int i = 0; i < 11 ; i++){
                result.add(0);
            }
            int tempI = 10;
            for(Element e : E.children()){
                result.set(tempI,Integer.parseInt(e.getElementsByClass("count").text().replace("(","").replace(")","")));
                tempI --;
            }
            return result;
        }
        static HashMap<String,ArrayList<String>> ParseKVInfo(Element E){
            HashMap<String,ArrayList<String>> result = new HashMap<>();
            ArrayList<String> value;
            String key;
            for (Element e : E.children()){
                for (String s : e.text().split(";")){
                    value = new ArrayList<>();
                    String[] ss = s.split(":");
                    key = ss[0];
                    Collections.addAll(value, ss[1].split("、"));
                    result.put(key,value);
                }
            }
            return result;
        }
        static ArrayList<CommentItem>   ParseCommentWithVote(Element E){
            ArrayList<CommentItem> result = new ArrayList<>();
            if(E == null)
                return result;
            for(Element e : E.children()){
                CommentItem comment = new CommentItem();
                UserItem user = new UserItem();
                user.UserID = e.child(0).attr("href").substring(6);
                user.UserHeaderUrl = e.child(0).child(0).attr("style").substring(22);
                user.UserHeaderUrl = "http:" + user.UserHeaderUrl.substring(0,user.UserHeaderUrl.length()-1).replace("/s/","/l/");
                user.UserNickname = e.getElementsByClass("l").first().text();
                comment.User = user;
                Element starE = e.getElementsByClass("starsinfo").first();
                if(starE != null){
                    comment.Score = Integer.parseInt(starE.attr("class").substring(6,8).replace(" ",""));
                }else{
                    comment.Score = -1;
                }
                comment.SubmitDatetime = e.getElementsByClass("grey").first().text().substring(2);
                comment.Comment = e.getElementsByTag("p").first().text();
                result.add(comment);
            }
            return result;
        }
    }
    private class SearchResultParser extends DOMParser<SearchResult>{
        private int currentPage;
        private String searchType;
        private String keyWords;
        private FragmentSearchResult fm;
        SearchResultParser(int curpage, String type, String keyword,FragmentSearchResult Fm){
            this.currentPage = curpage;
            this.searchType = type;
            this.keyWords = keyword;
            this.fm = Fm;
        }
        @Override
        SearchResult ParseDOM(Document doc) {
            if(doc == null){
                return null;
            }
            Elements Items = doc.getElementsByClass("item");
            SearchResult result = new SearchResult();
            SearchResultItem resultItem;

            result.currentPage = currentPage;
            result.maxPage = 0;     // !!!
            result.searchType = searchType;
            result.keyWord = keyWords;

            for (Element Item : Items){
                resultItem = new SearchResultItem();
                resultItem.ItemId = Integer.valueOf(Item.attr("id").substring(5));
                if(Item.getElementsByTag("img").first() != null) {
                    resultItem.CoverUrl = Item.getElementsByTag("img").first().attr("src");
                    resultItem.CoverUrl = "http://lain.bgm.tv/pic/cover/l/" + resultItem.CoverUrl.substring(26);
                }else {
                    resultItem.CoverUrl = "";
                }
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
        void UpdateUI(SearchResult result) {
            if(fm == null)
                return;
            if(result == null){
                fm.updateUI(null);
                return;
            }
            if(result.currentPage == 1)
                fm.updateUI(result);
            else
                fm.appendUI(result);
        }
    }
    private class ItemDetailParser extends DOMParser<DetailItem>{
        private ActivityItemDetail mFragment;
        ItemDetailParser(ActivityItemDetail fragment){
            mFragment = fragment;
        }
        @Override
        DetailItem ParseDOM(Document doc) {
            if(doc == null)
                return null;
            DetailItem result = new DetailItem();
            // parse summar
            result.Summary = ElementParser.ParseSummary(doc.getElementById("subject_summary"));
            // parse episode
            result.Eps = ElementParser.ParseEps(doc.getElementsByClass("subject_prg").first());
            // parse tags
            result.Tags = ElementParser.ParseTags(doc.getElementsByClass("subject_tag_section").first());
            //parse characters
            result.CharactersList = ElementParser.ParseCharactersCompact(doc.getElementById("browserItemList"));
            // parse votes
            result.ScoreDetail = ElementParser.ParseVotes(doc.getElementById("ChartWarpper").getElementsByClass("horizontalChart").first());
            // parse blog
            if(doc.getElementById("entry_list") != null){
                result.Blogs = new ArrayList<>();
                BlogItem blog;
                for (Element e : doc.getElementById("entry_list").children()){
                    blog = new BlogItem();
                    blog.Title = e.getElementsByClass("title").first().child(0).text();
                    blog.BlogID = e.getElementsByClass("title").first().child(0).attr("href").substring(6);
                    blog.Submitter = new UserItem();
                    blog.Submitter.UserHeaderUrl = WebSpider.PROTOCOL + e.getElementsByTag("img").first().attr("src");
                    blog.Submitter.UserNickname = e.getElementsByClass("tip_j").first().child(0).text();
                    blog.Submitter.UserID = e.getElementsByClass("tip_j").first().child(0).attr("href").substring(6);
                    blog.Submitter.isHeaderLoading = false;
                    blog.SubmitDatetime = e.getElementsByClass("time").get(1).ownText();
                    blog.BlogCommentNumber = e.getElementsByClass("orange").first().text();
                    blog.BlogPreview = e.getElementsByClass("content").first().ownText();
                    result.Blogs.add(blog);
                }
            }
            // parse topics
            if(doc.getElementsByTag("tbody").size() == 1){
                result.Topics = new ArrayList<>();
                SubjectTopicItem topic;
                for (int i = 0; i < doc.getElementsByTag("tbody").first().children().size() - 1; i++){
                    Element e = doc.getElementsByTag("tbody").first().child(i);
                    topic = new SubjectTopicItem();
                    topic.Title = e.child(0).child(0).text();
                    topic.TopicID = e.child(0).child(0).attr("href").substring(15);
                    topic.Submitter = new UserItem();
                    topic.Submitter.UserID = e.child(1).child(0).attr("href").substring(6);
                    topic.Submitter.UserNickname = e.child(1).child(0).text();
                    topic.RepliesNumber = Integer.parseInt(e.child(2).child(0).text().split(" ")[0]);
                    topic.SubmitDate = e.child(3).child(0).text();
                    result.Topics.add(topic);
                }
            }
            // parse KV-info
            result.KVInfo = ElementParser.ParseKVInfo(doc.getElementById("infobox"));
            // parse Comments
            result.Comments = ElementParser.ParseCommentWithVote(doc.getElementById("comment_box"));
            // TODO: Parse Music Item
            return result;
        }
        @Override
        void UpdateUI(DetailItem result) {
            if(mFragment != null)
                mFragment.updateUI(result);
        }
    }
    private class BlogParser extends DOMParser<BlogItem>{
        private ActivityBlogView mActivity;
        public BlogParser (ActivityBlogView activity){
            mActivity = activity;
        }
        @Override
        BlogItem ParseDOM(Document doc) {
            BlogItem result = new BlogItem();
            result.BlogText = doc.getElementById("entry_content").html().replace("<br />","").replace("&nbsp;","");
            Element relatedSubjectDoc = doc.getElementById("related_subject_list");
            if(relatedSubjectDoc != null){
                for(Element e : relatedSubjectDoc.children()){
                    // TODO: related subjects
                }
            }
            return result;
        }

        @Override
        void UpdateUI(BlogItem result) {
            mActivity.updateUI(result);
        }
    }
    private class BlogListParser extends DOMParser<ArrayList<BlogItem>>{
        private ActivityBlogList mActivity;
        public BlogListParser (ActivityBlogList activity){
            mActivity = activity;
        }
        @Override
        ArrayList<BlogItem> ParseDOM(Document doc) {
            if(doc == null){
                return null;
            }
            ArrayList<BlogItem> result = new ArrayList<>();
            if(doc.getElementById("entry_list") == null){
                return result;
            }
            for (Element e : doc.getElementById("entry_list").children()){
                BlogItem item = new BlogItem();
                item.Submitter = new UserItem();

                item.Submitter.UserHeaderUrl = "http:" + e.getElementsByTag("img").first().attr("src");
                e = e.getElementsByClass("entry").first();
                Element title = e.getElementsByTag("h2").first().children().get(0);
                item.Title = title.text();
                item.BlogID = title.attr("href").substring(6);
                item.Submitter.UserNickname = e.child(1).getElementsByTag("a").first().text();
                item.SubmitDatetime = e.child(1).getElementsByClass("time").first().text();
                item.BlogCommentNumber = e.child(1).getElementsByClass("orange").first().text();
                item.BlogPreview = e.child(2).ownText().replace("<br />","").replace("&nbsp;","");
                result.add(item);
            }
            return result;
        }

        @Override
        void UpdateUI(ArrayList<BlogItem> result) {
            mActivity.updateUI(result);
        }
    }
    private class CommentListParser extends DOMParser<ArrayList<CommentItem>>{
        private ActivityCommentList mActivity;
        public CommentListParser(ActivityCommentList activity){
            mActivity = activity;
        }
        @Override
        ArrayList<CommentItem> ParseDOM(Document doc) {
            if(doc == null){
                return null;
            }
            return ElementParser.ParseCommentWithVote(doc.getElementById("comment_box"));
        }
        @Override
        void UpdateUI(ArrayList<CommentItem> result) {
            mActivity.updateUI(result);
        }
    }
    private class CharacterListParser extends DOMParser<ArrayList<CharacterItem>>{
        private ActivityCharactersList mActivity;
        public CharacterListParser(ActivityCharactersList activity){
            mActivity = activity;
        }
        @Override
        ArrayList<CharacterItem> ParseDOM(Document doc) {
            if(doc == null){
                return null;
            }
            ArrayList<CharacterItem> result = new ArrayList<>();
            Element box = doc.getElementById("columnInSubjectA");
            if(box == null){
                return result;
            }
            for (Element e : box.getElementsByClass("light_odd")){
                CharacterItem character = new CharacterItem();
                character.CVInfo = new ArrayList<>();
                character.CharacterID = Integer.parseInt(e.getElementsByTag("h2").first().child(0).attr("href").substring(11)); //
                character.CharacterHeaderUrl = WebSpider.PROTOCOL + e.getElementsByTag("img").first().attr("src");  //
                if(e.getElementsByClass("na").size() != 0)  //
                    character.CommentNumber = e.getElementsByClass("na").first().text();        //
                else
                    character.CommentNumber = "";   //
                character.CharacterName = e.getElementsByTag("h2").first().child(0).text(); //
                if(e.getElementsByTag("h2").first().children().size() == 2) //
                    character.CharacterTranslation = e.getElementsByTag("h2").first().child(1).text();  //
                else
                    character.CharacterTranslation = "";
                character.CharacterType = e.getElementsByClass("badge_job").first().ownText();
                character.CharacterGender = e.getElementsByClass("clearit").first().child(1).ownText().replace(" ","");
                character.CVInfo = new ArrayList<>();
                for (Element p : doc.getElementsByClass("actorBadge")){
                    PersonItem cv = new PersonItem();
                    cv.PersonID = Integer.parseInt(p.child(0).attr("href").substring(8));
                    cv.HeaderUrl = WebSpider.PROTOCOL + p.child(0).child(0).attr("src");
                    cv.Name = p.child(1).child(0).ownText();
                    cv.Translation = p.child(1).child(1).ownText();
                    character.CVInfo.add(cv);
                }
                result.add(character);
            }
            return result;
        }
        @Override
        void UpdateUI(ArrayList<CharacterItem> result) {
            mActivity.updateUI(result);
        }
    }
    private class HomepageParser extends DOMParser<JSONArray>{
        FragmentHome mFragment;
        public HomepageParser(FragmentHome fragment){
            mFragment = fragment;
        }
        @Override
        JSONArray ParseDOM(Document doc) {
            JSONArray data = new JSONArray();
            JSONObject tdata;
            if(doc == null)
                return new JSONArray();
            Log.d("DEBUG",doc.toString());
            Log.d("DEBUG",sWebSpider.WebCookies.map.toString());
            Log.d("DEBUG",sWebSpider.APICookies.map.toString());
            // Parse 小组话题 and 热门讨论条目
            for(Element c : doc.getElementsByClass("sideTpcList")){
                for(Element e : c.getElementsByTag("li")){
                    if(e.classNames().contains("tools"))
                        continue;
                    tdata = new JSONObject();
                    String submitUserId     = "";
                    Matcher matcher = Pattern.compile("[0-9]+\\.").matcher(e.child(0).child(0).attr("src"));
                    if(matcher.find())
                        submitUserId = matcher.group();
                    String[] list = e.child(0).attr("href").split("/");
                    Element t = e.child(1).getElementsByTag("p").first().getElementsByTag("a").first();
                    tdata.put("id",list[3]);
                    tdata.put("type",list[1]);
                    tdata.put("title",e.child(1).child(0).ownText());
                    tdata.put("reply_count",e.child(1).child(1).ownText());
                    tdata.put(list[1].concat("_name"),t.ownText());
                    tdata.put((list[1].concat("_id")),t.attr("href"));
                    tdata.put("submit_user_id",submitUserId.substring(0,submitUserId.length()-1));
                    tdata.put("submit_user_name",e.child(0).child(0).attr("title"));
                    data.add(tdata);
                }
            }
            return null;
        }

        @Override
        void UpdateUI(JSONArray result) {
            mFragment.updateUI(result);
        }
    }

    // API控制

    // 网络部分
    private static class AsyncTaskNetwork extends  AsyncTask<Void,Void,Document>{
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
                if(mParent.WebCookies.map == null){
                    sWebSpider.WebCookies.map.putAll(Jsoup.connect(WebSpider.BASE_SITE).timeout(3000).execute().cookies());
                    if(mParent.WebCookies == null)
                        return null;
                }
                Connection con = Jsoup.connect(TargetUrl)
                        .cookies(mParent.WebCookies.map)
                        .timeout(3000)
                        .parser(Parser.xmlParser());
                doc = con.get();
                mParent.WebCookies.map.putAll(con.response().cookies());
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
    private static class AsyncApiLogin extends AsyncTask<Void, Void, JSONObject>{
        OnLoginListener mListener;
        String mUsername;
        String mPassword;
        String mB64Key;
        private AsyncApiLogin(String username, String password, String b64Key, OnLoginListener listener){
            mListener = listener;
            mUsername = username;
            mPassword = password;
            mB64Key = b64Key;
        }
        @Override
        protected JSONObject doInBackground(Void... params){
            while(sWebSpider.api_login_lock){         // delay
                try {
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    Log.d("DEBUG-KERNEL","INTERRUPTED");
                }
            }
            sWebSpider.api_login_lock = true; // lock
            JSONObject data;
            Connection con = Jsoup.connect("http://api.bgm.tv/auth?source=onAir").timeout(5000);
            con.header("Accept","application/json");
            con.cookies(sWebSpider.APICookies.map);
            con.ignoreContentType(true);
            if(mB64Key == null) {
                con.data("username", mUsername);
                con.data("password", mPassword);
            }else{
                con.header("Authorization",mB64Key);
            }
            try{
                String text;
                if(mB64Key == null) {
                     text = con.post().body().text();
                }else{
                    text = con.get().body().text();
                }
                Log.d("DEBUG-KERNEL",text);
                data = JSONObject.parseObject(text);
                 //data = new JSONObject(con.post().body().text());
            }catch (Exception e){
                Log.d("DEBUG-KERNEL",e.toString());
                return null;
            }
            sWebSpider.APICookies.map.putAll(con.response().cookies());
            return data;
        }
        @Override
        protected void onPostExecute(JSONObject data){
            sWebSpider.api_login_lock = false;
            if(data == null){
                mListener.onBusy();
                return;
            }
            boolean status = false;
            if(data.containsKey("error")){
                status = false;
            }else{
                status = true;
                if(mB64Key == null || mB64Key.equals(""))
                    sWebSpider.mAuthB64Key = sWebSpider.generateB64Key(mUsername,mPassword);
                sWebSpider.mAuthInfo = data;
            }
            sWebSpider.setApiAuthStatus(status);
            if(status)  mListener.onSuccess(data);
            else        mListener.onFailed(data);
        }
    }
    private static class AsyncWebLogin extends AsyncTask<Void, Void, Boolean>{
        OnLoginListener mListener;
        String mEmail;
        String mPassword;
        String mCaptcha;
        public AsyncWebLogin(String email, String password, String captcha, OnLoginListener listener){
            mListener = listener;
            mEmail = email;
            mPassword = password;
            mCaptcha = captcha;
        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            while(sWebSpider.web_login_lock){         // delay
                try {
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    Log.d("DEBUG-KERNEL","INTERRUPTED");
                }
            }
            sWebSpider.web_login_lock = true;
            if(sWebSpider.mChiiAuth.equals("")) {
                try {
                    Connection con = Jsoup.connect("https://bgm.tv/FollowTheRabbit")
                            .ignoreContentType(true)
                            .cookies(sWebSpider.WebCookies.map)
                            .data("formhash", "6e441a0b")
                            .data("email", mEmail)
                            .data("password", mPassword)
                            .data("captcha_challenge_field", mCaptcha)
                            .data("loginsubmit", "登陆")
                            .method(Connection.Method.POST);
                    Connection.Response res = con.execute();
                    sWebSpider.WebCookies.map.putAll(res.cookies());
                    if (!res.cookies().containsKey("chii_auth")) {
                        return false;
                    }
                    Document doc = res.parse();
                    return true;
                } catch (IOException e) {
                    Log.d("DEBUG-KERNEL", e.toString());
                }
            }else{
                try {
                    if(sWebSpider.WebCookies.map.containsKey("chii_sid"))
                        sWebSpider.WebCookies.map.remove("chii_sid");
                    sWebSpider.WebCookies.map.put("chii_auth",sWebSpider.mChiiAuth);
                    Connection con = Jsoup.connect("https://bgm.tv/FollowTheRabbit")
                            .ignoreContentType(true)
                            .cookies(sWebSpider.WebCookies.map)
                            .method(Connection.Method.GET);
                    Connection.Response res = con.execute();
                    if(res.url().getFile().equals("/"))
                        return true;
                    else
                        return false;
                }catch (IOException e){
                    Log.d("DEBUG-KERNEL",e.toString());
                }
            }
            return false;
        }
        @Override
        protected void onPostExecute(Boolean status){
            sWebSpider.web_login_lock  = false;
            sWebSpider.setWebAuthStatus(status);
            if(status){
                mListener.onSuccess(null);
            }else{
                mListener.onFailed(null);
            }
        }
    }
    public static class AsyncCaptchaLoader extends AsyncTask<Void, Void, Bitmap> {
        OnCaptchaReadyListener mListener;
        public AsyncCaptchaLoader(OnCaptchaReadyListener listener){
            mListener = listener;
        }
        @Override
        protected Bitmap doInBackground(Void... voids) {
            String timestamp = String.valueOf((new Date().getTime())/1000);
            String rand      = String.valueOf((new Random().nextInt(6))+1);
            String url       = "https://bgm.tv/signup/captcha?".concat(timestamp).concat(rand);

            // Refresh cookies
            if((!sWebSpider.WebCookies.map.containsKey("__cfduid")) || (!sWebSpider.WebCookies.map.containsKey("chii_sid"))) {  // 没有cookies
                try {
                    Connection.Response res = Jsoup.connect("https://bgm.tv/FollowTheRabbit").execute();
                    sWebSpider.WebCookies.map.putAll(res.cookies());
                    Thread.sleep(5000);
                } catch (Exception e) {
                    Log.d("DEBUG-KERNEL", e.toString());
                }
            }

            // Fetch Captcha
            Bitmap captcha;
            try {
                Log.d("DEBUG-KERNEL",url);
                Connection con = Jsoup.connect(url);
                con.cookies(sWebSpider.WebCookies.map);
                con.header("Accept","image/webp,image/apng,image/*,*/*;q=0.8");
                con.header("refer","https://bgm.tv/login");
                con.cookies(sWebSpider.WebCookies.map);
                con.ignoreContentType(true);
                Connection.Response res = con.execute();
                sWebSpider.WebCookies.map.putAll(res.cookies());
                byte[] data = res.bodyAsBytes();
                captcha = BitmapFactory.decodeByteArray(data,0,data.length);

            }catch (IOException e){
                Log.d("DEBUG-KERNEL",e.toString());
                return null;
            }
            return captcha;
        }
        @Override
        protected void onPostExecute(Bitmap img){
            sWebSpider.captcha_lock = false;// unlock
            if(img == null){
                mListener.onFailed();
            }else{
                mListener.onSuccess(img);
            }
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
    private boolean captcha_lock = false;
    private boolean api_login_lock = false;
    private boolean web_login_lock = false;
    /*
    * 定义全局变量
    */
    private static WebSpider sWebSpider;
    private Context mContext;
    private WebSpider(Context context) {
        this.mContext = context;
    }
    protected void finalize(){
        this.SaveStatus(mContext);
    }

    /*
    * 公共类
    */

    public static class CommentItems{
        public int maxPage = 0;
        public int currentPage = 0;
        public ArrayList<CommentItem> items = new ArrayList<>();
    }
    public static class SerializableMap implements Serializable{
        public Map<String,String> map = new HashMap<>();
    }
    /*
    * 公共方法
    */
    private static String StringFilter(String   str){
        // 只允许字母和数字
        // String   regEx  =  "[^a-zA-Z0-9]";
        // 清除掉所有特殊字符
        String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p   =   Pattern.compile(regEx);
        Matcher m   =   p.matcher(str);
        return   m.replaceAll("+").trim();
    }
    public void Search(String keyWords, String type, int page, FragmentSearchResult fm){
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
    public void GetBlogItem(String Url, ActivityBlogView activity){
        new AsyncTaskNetwork(this, new BlogParser(activity),Url).execute();
    }
    public void GetItemDetail(String Url,ActivityItemDetail fragment){
        new AsyncTaskNetwork(this,new ItemDetailParser(fragment),Url).execute();
    }
    public void GetBlogList(String url, ActivityBlogList activity){
        new AsyncTaskNetwork(this, new BlogListParser(activity),url).execute();
    }
    public void GetCommentList(String url, ActivityCommentList activity){
        new AsyncTaskNetwork(this, new CommentListParser(activity),url).execute();
    }
    public void GetCharacterList(String Url, ActivityCharactersList activity){
        new AsyncTaskNetwork(this, new CharacterListParser(activity),Url).execute();
    }
    public void GetHomepage(FragmentHome home){
        new AsyncTaskNetwork(this, new HomepageParser(home),"http://bgm.tv/").execute();
    }


    public static WebSpider get(Context context) {
        if(sWebSpider == null){
            sWebSpider = new WebSpider(context);
            // INIT
            sWebSpider.LoadStatus(context);
        }
        return sWebSpider;
    }
    public static String getStrType(int type){
        String strType = "" + type;
        if(strType.equals(WebSpider.SEARCH_BANGUMI)){
            return "Anime";
        } else if(strType.equals(WebSpider.SEARCH_3DIM)){
            return "Real";
        } else if(strType.equals(WebSpider.SEARCH_BOOK)){
            return "Book";
        } else if(strType.equals(WebSpider.SEARCH_GAME)){
            return "Game";
        } else if(strType.equals(WebSpider.SEARCH_MUSIC)){
            return "Music";
        }
        return "";
    }


    // PROTOCOL LAYER

    private boolean mAuthStatus = false;

    private JSONObject mAuthBasicInfo   = null;
    private String mAuthB64Key          = "";
    private String mChiiAuth            = "";
    private boolean mWebAuthStatus       = false;
    private boolean mApiAuthStatus       = false;


    private static String NAME_WS_CONFIG = "name_ws_config";
    private static String CONFIG_KEY_BASIC_INFO  = "config_key_basic_info";      // from api get user basic info
    private static String CONFIG_KEY_AUTH_BASIC  = "config_key_auth_basic";      // B64Key for api to login
    private static String CONFIG_KEY_CHII_AUTH   = "config_key_chii_auth";       // Web cookies chii_auth when login
    private static String CONFIG_KEY_WEB_COOKIES = "config_key_web_cookies";
    private static String CONFIG_KEY_API_COOKIES = "config_key_api_cookies";

    private JSONObject mAuthInfo = new JSONObject();

    private void setWebAuthStatus(boolean status){
        mWebAuthStatus = status;
        if(mWebAuthStatus && mApiAuthStatus)
            mAuthStatus = true;
    }
    private void setApiAuthStatus(boolean status){
        mApiAuthStatus = status;
        if(mWebAuthStatus && mApiAuthStatus)
            mAuthStatus = true;
    }
    private String generateB64Key(String email, String password){
        String b64key = "";
        try{
            b64key = "Basic ".concat(new String(Base64.encode((email.concat(":").concat(password)).getBytes("utf-8"), Base64.NO_WRAP), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            Log.d("DEBUG", e.toString());
        }
        return b64key;
    }

    public void APIAuth(String email, String password, String b64Key, OnLoginListener listener){
        if(api_login_lock)
            listener.onBusy();
        else
            new AsyncApiLogin(email,password,b64Key,listener).execute();
    }
    public void WebAuth(String email, String password, String captcha, OnLoginListener listener){
        if(web_login_lock)
            listener.onBusy();
        else
            new AsyncWebLogin(email,password,captcha,listener).execute();
    }
    public void WebAuthFetchCaptcha(OnCaptchaReadyListener listener){
        if(captcha_lock)
            listener.onBusy();
        captcha_lock = true;
        new AsyncCaptchaLoader(listener).execute();
    }
    public void CheckAPIAuthStatus(OnLoginListener listener){
        if(api_login_lock)
            listener.onFailed(JSONObject.parseObject("{\"error\":\"繁忙，请稍后再试\"}"));
        else if(mAuthB64Key.equals(""))
            listener.onFailed(JSONObject.parseObject("{\"error\":\"没有有效的B64Key\"}"));
        else
            new AsyncApiLogin(null, null, mAuthB64Key, listener).execute();
    }
    public void CheckWebAuthStatus(OnLoginListener listener){
        if(web_login_lock)
            listener.onFailed(JSONObject.parseObject("{\"error\":\"繁忙，请稍后再试\"}"));
        else if(mChiiAuth.equals(""))
            listener.onFailed(JSONObject.parseObject("{\"error\":\"没有有效的chii_auth\"}"));
        else
            new AsyncWebLogin(null,null,null,listener).execute();
    }
    public boolean getAuthStatus(){
        return mAuthStatus;
    }
    public JSONObject getAuthInfo(){
        return mAuthInfo;
    }
    public void SaveStatus(Context context){
        SharedPreferences sp = context.getSharedPreferences(WebSpider.NAME_WS_CONFIG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if(mAuthBasicInfo != null)
            editor.putString(WebSpider.CONFIG_KEY_BASIC_INFO, mAuthBasicInfo.toString());
        if(!mAuthB64Key.equals(""))
            editor.putString(WebSpider.CONFIG_KEY_AUTH_BASIC, mAuthB64Key);
        if(WebCookies.map.containsKey("chii_auth")) {
            editor.putString(WebSpider.CONFIG_KEY_CHII_AUTH, WebCookies.map.get("chii_auth"));
            WebCookies.map.remove("chii_auth");
        }
        editor.putString(WebSpider.CONFIG_KEY_WEB_COOKIES,JSON.toJSONString(WebCookies.map));
        editor.putString(WebSpider.CONFIG_KEY_API_COOKIES,JSON.toJSONString(APICookies.map));
        editor.apply();
    }
    public void LoadStatus(Context context){
        SharedPreferences sp = context.getSharedPreferences(WebSpider.NAME_WS_CONFIG,0);

        String basicInfo    = sp.getString(WebSpider.CONFIG_KEY_BASIC_INFO,"");
        String b64Key       = sp.getString(WebSpider.CONFIG_KEY_AUTH_BASIC,"");
        String web_cookies  = sp.getString(WebSpider.CONFIG_KEY_WEB_COOKIES,"");
        String api_cookies  = sp.getString(WebSpider.CONFIG_KEY_API_COOKIES,"");
        mChiiAuth           = sp.getString(WebSpider.CONFIG_KEY_CHII_AUTH,"");

        if(!web_cookies.equals(""))
            sWebSpider.WebCookies.map = JSON.parseObject(web_cookies,new TypeReference<Map<String, String>>(){});
        if(!api_cookies.equals(""))
            sWebSpider.APICookies.map = JSON.parseObject(api_cookies,new TypeReference<Map<String, String>>(){});
        if(!b64Key.equals(""))
            sWebSpider.mAuthB64Key = b64Key;
        if(!basicInfo.equals(""))
            sWebSpider.mAuthBasicInfo = JSON.parseObject(basicInfo);
    }
    public void ClearSavedStatus(Context context){
        SharedPreferences sp = context.getSharedPreferences(WebSpider.NAME_WS_CONFIG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }
}