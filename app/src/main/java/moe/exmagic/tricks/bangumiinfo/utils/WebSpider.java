package moe.exmagic.tricks.bangumiinfo.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import moe.exmagic.tricks.bangumiinfo.SearchResultFragment;

/**
 * Created by tricks on 17-1-4.
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

    public static int ITEM_TYPE_BANGUMI = 1;
    public static int ITEM_TYPE_BOOK = 2;
    public static int ITEM_TYPE_MUSIC = 3;
    public static int ITEM_TYPE_GAME = 4;
    public static int ITEM_TYPE_3DIM = 6;

    private Map<String,String> Cookies = null;
    public String   error           = "";
    public String   keyWord         = "";
    private String  TargetUrl       = "";
    private Document             HttpDocument = null;
    public Fragment parent;

    public static class SearchResult{
        public int maxpage = 0;
        public int currentpage = 0;
        public String searchtype = "0";
        public String keyword = "";
        public WebSpider parent = null;
        public Fragment fm;
        public SearchResult getNext(){
            if (this.currentpage == this.maxpage) {
                return null;
            }
            return this.parent.searchItem(this.keyword, this.searchtype, this.currentpage+1,fm);
        }
        public ArrayList<DataType.SearchResultItem> result = new ArrayList<>();
    }
    private class FetchResponse extends AsyncTask<WebSpider,Void,Void> {
        @Override
        protected Void doInBackground(WebSpider... params){
            WebSpider parent = params[0];
            Connection.Response res;
            try {
                res = Jsoup.connect(WebSpider.BASE_SITE).timeout(3000).execute();
            }catch (IOException e){
                parent.setError(e.toString());
                return null;
            }
            parent.setCookies(res.cookies());
            return null;
        }
    }
    private class FetchHttp extends AsyncTask<Void,Void,Void> {
        private WebSpider   mParent;
        private int         mCurrentPage;
        private String      mSearchType;
        private Fragment    mFm;
        public FetchHttp(WebSpider parent, int currentPage, String searchType,Fragment fm){
            this.mParent = parent;
            this.mCurrentPage = currentPage;
            this.mSearchType = searchType;
            this.mFm = fm;
        }
        @Override
        protected Void doInBackground(Void... v){
            try {
                if(mParent.getCookies() == null){
                    mParent.setCookies(Jsoup.connect(WebSpider.BASE_SITE).timeout(3000).execute().cookies());
                    if(mParent.getCookies() == null)
                        return null;
                }
                mParent.HttpDocument = Jsoup.connect(mParent.TargetUrl)
                        .cookies(mParent.getCookies())
                        .timeout(3000)
                        .get();
            }catch (IOException e){
                mParent.setError(e.toString());
                mParent.unlock();
                mParent.HttpDocument = null;
                return null;
            }
            this.mParent.unlock();
            return null;
        }
        @Override
        protected void onPostExecute(Void v){
            WebSpider.SearchResult result = WebSpider.parseDOMResult(this.mParent.HttpDocument,mCurrentPage,mSearchType,this.mParent.keyWord);
            if(result != null)
                result.parent = this.mParent;
            result.fm = mFm;
            if(mCurrentPage == 1)
                ((SearchResultFragment)mFm).updateUI(result);
            else
                ((SearchResultFragment)mFm).appendUI(result);

        }
    }

    private int syn_lock = 0;
    public boolean lock(){
        while(this.syn_lock == 1){
            return false;
            /*try{      // 阻塞
                Thread.sleep(100);
            }catch(InterruptedException e){
                return false;
            }*/
        }
        this.syn_lock = 1;
        return true;
    }
    public void unlock(){
        this.syn_lock = 0;
    }

    private WebSpider(Context context) {     // initlized
        new FetchResponse().execute(this);
        // test CODE START
        /*while(this.Cookies == null){
            try {
                Thread.sleep(1000);
            }catch (InterruptedException e){
                return ;
            }
        }
        this.SearchItem("Heart",WebSpider.SEARCH_ALL,0);*/
        // test CODE END
    }
    public Document FetchHTTPResult(String keyWords,String type, int page,Fragment fm){
        String targetUrl;
        targetUrl = BASE_SITE + "subject_search/" + WebSpider.StringFilter(keyWords) + "?cat=" + type;
        this.keyWord = keyWords;
        if (page > 0){
            targetUrl += "&page=" + page;
        }
        this.TargetUrl = targetUrl;
        Log.d("TargetUrl",targetUrl);
        if (!this.lock())
            return null;
        new FetchHttp(this,page,type,fm).execute();
        return null;
    }
    public void setCookies(Map<String,String> cookies){
        this.Cookies = cookies;
    }
    public void setError(String error){
        this.error = error;
    }
    public Map<String,String> getCookies(){
        return  this.Cookies;
    }
    public static WebSpider.SearchResult parseDOMResult(Document HttpDocument,int currentPage, String searchType, String keyWords){
        if(HttpDocument == null){
            return null;
        }
        Elements Items = HttpDocument.getElementsByClass("item");
        WebSpider.SearchResult result = new WebSpider.SearchResult();
        DataType.SearchResultItem resultItem = null;

        result.currentpage = currentPage;
        result.maxpage = 0;     // !!!
        result.searchtype = searchType;
        result.keyword = keyWords;

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
        if(HttpDocument.getElementById("multipage") != null && HttpDocument.getElementById("multipage").getElementsByClass("p").last() != null){
            String tmpUrl = HttpDocument.getElementById("multipage").getElementsByClass("p").last().attr("href");
            String[] tmpStrs = tmpUrl.split("=");
            result.maxpage = Integer.parseInt(tmpStrs[tmpStrs.length-1]);
        }else{
            result.maxpage = 10;
        }
        return result;
    }
    public SearchResult searchItem(String keyWords, String type, int page,Fragment fm){
        return WebSpider.parseDOMResult(this.FetchHTTPResult(keyWords,type,page,fm),page,type,this.keyWord);
    }
    private static WebSpider sWebSpider;
    public static WebSpider get(Context context){
        if(sWebSpider == null){
            sWebSpider = new WebSpider(context);
        }
        return sWebSpider;
    }
    public   static   String StringFilter(String   str){
        // 只允许字母和数字
        // String   regEx  =  "[^a-zA-Z0-9]";
        // 清除掉所有特殊字符
        String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p   =   Pattern.compile(regEx);
        Matcher m   =   p.matcher(str);
        return   m.replaceAll("+").trim();
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

