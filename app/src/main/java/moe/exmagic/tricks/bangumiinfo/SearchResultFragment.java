package moe.exmagic.tricks.bangumiinfo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import moe.exmagic.tricks.bangumiinfo.utils.DataType;
import moe.exmagic.tricks.bangumiinfo.utils.WebSpider;

/**
 * Created by tricks on 17-1-5.
 */

public class SearchResultFragment extends Fragment{
    private RecyclerView mItemListView;
    private ItemsAdapter mAdapter;
    private String mSearchKeyWord = "";
    public void updateUI(WebSpider.SearchResult result){
        if(result == null)
            return;
        mAdapter = new ItemsAdapter(result);
        mItemListView.setAdapter(mAdapter);
    }
    public void search(String keyWord){
        this.mSearchKeyWord = keyWord;
        WebSpider spider = WebSpider.get(getActivity());
        WebSpider.SearchResult result = spider.searchItem(mSearchKeyWord,WebSpider.SEARCH_ALL,0);
        this.updateUI(result);
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    private class FetchBitmap extends AsyncTask<Void,Void,Bitmap>{
        private ImageView mV;  
        private String    mStrUrl;
        private DataType.SearchResultItem mItem;
        FetchBitmap(ImageView view, String strUrl, DataType.SearchResultItem item){
            mV = view;
            mStrUrl = "http:" + strUrl;
            mItem = item;
        }
        @Override
        protected  Bitmap doInBackground(Void... v){
            URL url;
            try {
                url = new URL(mStrUrl);
            }catch (MalformedURLException e){
                Log.e("Bitmap",mStrUrl + " AND " + e.toString());
                return null;
            }
            try {
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                if (conn.getResponseCode() == 200) {
                    InputStream inputStream = conn.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                }
            }catch(IOException e){
                return null;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap){
            mItem.isLoading = false;
            if(bitmap == null){
                return ;
            }
            mV.setImageBitmap(bitmap);
            mItem.Cover = bitmap;
        }
    }
    private class ItemsHolder extends RecyclerView.ViewHolder{
        public TextView pTitleView;
        public TextView pOriginalTitleView;
        public TextView pRankView;
        public ImageView pCoverView;
        public ItemsHolder(View itemView){
            super(itemView);
            pTitleView = (TextView) itemView.findViewById(R.id.textViewTitle);
            pOriginalTitleView = (TextView) itemView.findViewById(R.id.textViewOriginalTitle);
            pRankView = (TextView) itemView.findViewById(R.id.textRankInfoView);
            pCoverView = (ImageView) itemView.findViewById(R.id.coverView);
        }
    }
    private class ItemsAdapter extends RecyclerView.Adapter<ItemsHolder> {
        private WebSpider.SearchResult mResults;

        public ItemsAdapter(WebSpider.SearchResult results){
            mResults = results;
        }
        @Override
        public ItemsHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.search_result_item,parent,false);
            return new ItemsHolder(view);
        }
        @Override
        public int getItemCount(){
            return mResults.result.size();
        }
        @Override
        public void onBindViewHolder(ItemsHolder holder, int position){
            DataType.SearchResultItem item = mResults.result.get(position);
            holder.pTitleView.setText(item.Title);
            holder.pOriginalTitleView.setText(item.OriginalTitle);
            holder.pRankView.setText(item.RankN);
            if(item.Cover != null){
                holder.pCoverView.setImageBitmap(item.Cover);
            }else {
                if (item.isLoading)
                    return;
                item.isLoading = true;
                new FetchBitmap(holder.pCoverView, item.CoverUrl,item).execute();
            }
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_search_result_list,container,false);
        mItemListView = (RecyclerView) v.findViewById(R.id.placeholder_rv_recycler);
        mItemListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return v;
    }

}
