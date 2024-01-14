package personal.nfl.frameskipmonitor.frame.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import personal.nfl.frameskipmonitor.R;
import personal.nfl.frameskipmonitor.frame.model.RecyclerItem;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {
    List<RecyclerItem> mList;

    private boolean isNeedDelay;

    public RecyclerAdapter(List<RecyclerItem> list, boolean isDelay) {
        mList = list;
        isNeedDelay = isDelay;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        final RecyclerItem item = mList.get(position);
        long startTime = System.currentTimeMillis();
        if (isNeedDelay) {
            // 模拟耗时任务
            while (System.currentTimeMillis() - startTime < 50);
        }
        holder.tvTitle.setText(item.getTitle());
        holder.ivIcon.setImageResource(R.mipmap.ic_launcher);
        holder.ivIcon.setOnClickListener(v -> Toast.makeText(v.getContext(), item.getImg() + " " + position, Toast.LENGTH_LONG).show());
    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public final static class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView ivIcon;
        TextView tvTitle;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ivIcon = (ImageView) itemView.findViewById(R.id.iv_icon);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
        }
    }
}
