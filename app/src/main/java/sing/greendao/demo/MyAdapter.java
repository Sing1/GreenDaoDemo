package sing.greendao.demo;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private OnItemClickListener listener;
    private List<User> dataset;

    public User get(int position) {
        return dataset.get(position);
    }

    public void setList(List<User> dataset) {
        this.dataset = dataset;
        notifyDataSetChanged();
    }

    public MyAdapter(OnItemClickListener listener) {
        this.listener = listener;
        this.dataset = new ArrayList<>();
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {
        User user = dataset.get(position);
        holder.tvName.setText(user.getName());
        holder.tvAge.setText(user.getAge()+"");
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName;
        public TextView tvAge;
        public Button btChange;
        public Button btDelete;

        public ViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvAge = itemView.findViewById(R.id.tv_age);
            btChange = itemView.findViewById(R.id.bt_change);
            btDelete = itemView.findViewById(R.id.bt_delete);

            btChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onClick(getAdapterPosition(), 0);
                    }
                }
            });
            btDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onClick(getAdapterPosition(), 1);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onClick(int position, int type);
    }
}