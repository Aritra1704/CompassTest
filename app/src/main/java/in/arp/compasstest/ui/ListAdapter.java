package in.arp.compasstest.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import in.arp.compasstest.R;
import in.arp.compasstest.models.PicDO;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.WordViewHolder> {

    class WordViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvLat;
        private final TextView tvLng;
        private final TextView tvAngle;

        private WordViewHolder(View itemView) {
            super(itemView);
            tvLat = itemView.findViewById(R.id.tvLat);
            tvLng = itemView.findViewById(R.id.tvLng);
            tvAngle = itemView.findViewById(R.id.tvAngle);
        }
    }

    private final LayoutInflater mInflater;
    private List<PicDO> mWords; // Cached copy of words

    public ListAdapter(Context context) { mInflater = LayoutInflater.from(context); }

    @Override
    public WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new WordViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WordViewHolder holder, int position) {
        if (mWords != null) {
            PicDO current = mWords.get(position);
//            holder.wordItemView.setText(current.getWord());
        } else {
            // Covers the case of data not being ready yet.
//            holder.wordItemView.setText("No Word");
        }
    }

    void setWords(List<PicDO> words){
        mWords = words;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mWords != null)
            return mWords.size();
        else return 0;
    }
}
