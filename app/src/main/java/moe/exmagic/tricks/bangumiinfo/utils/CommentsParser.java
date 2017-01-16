package moe.exmagic.tricks.bangumiinfo.utils;

import moe.exmagic.tricks.bangumiinfo.utils.WebSpider;
import moe.exmagic.tricks.bangumiinfo.utils.DataType;

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
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhuyuhui on 17-1-16.
 */

public class CommentsParser extends WebSpider.DOMParser<WebSpider.CommentItems> {
    //private int currentPage;
//    private SearchResultFragment fm;
//    CommentsParser(int curpage,SearchResultFragment Fm){
//        this.currentPage = curpage;
//        this.fm = Fm;
//    }
    @Override
    WebSpider.CommentItems ParseDOM(Document doc) {
        if(doc == null){
            return null;
        }
        Elements Items = doc.getElementsByClass("item");
        WebSpider.CommentItems commentItems = new WebSpider.CommentItems();
        DataType.CommentItem commentItem;

        //commentItems.currentPage = currentPage;
        commentItems.maxPage = 0;     // !!!

        for (Element Item : Items){
            commentItem = new DataType.CommentItem();
            DataType.UserItem user=new DataType.UserItem();
            user.UserID=Item.getElementsByClass("l").first().attr("href").substring(6);
            user.UserNickname=Item.getElementsByClass("l").first().ownText();
            user.UserHeaderUrl=Item.getElementsByClass("avatarNueue").first().attr("style").substring(24);
            commentItem.User=user;

            if(Item.getElementsByClass("starsinfo").first()!=null){
                commentItem.Score=Integer.parseInt(Item.getElementsByClass("starsinfo").first().attr("class").substring(6,7));
            }else{
                commentItem.Score=-1;//不存在评分时设置为-1
            }

            commentItem.SubmitDatetime=Item.getElementsByTag("small").first().ownText();

            commentItem.Comment=Item.getElementsByClass("text").first().getElementsByTag("p").first().ownText();


            commentItems.items.add(commentItem);
        }
        if(doc.getElementsByClass("page_inner").first() != null && doc.getElementsByClass("page_inner").first().getElementsByClass("p").last() != null){
            String tmpUrl = doc.getElementsByClass("page_inner").first().getElementsByClass("p").last().attr("href");
            String[] tmpStrs = tmpUrl.split("=");
            commentItems.maxPage = Integer.parseInt(tmpStrs[tmpStrs.length-1]);
        }else{
            commentItems.maxPage = 10;
        }
        return commentItems;
    }
    @Override
    void UpdateUI(WebSpider.CommentItems items) {
    }
}
