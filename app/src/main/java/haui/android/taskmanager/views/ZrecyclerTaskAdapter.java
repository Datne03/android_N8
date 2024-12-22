package haui.android.taskmanager.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import haui.android.taskmanager.R;
import haui.android.taskmanager.models.Task;

public class ZrecyclerTaskAdapter extends RecyclerView.Adapter<ZrecyclerTaskAdapter.ZrecyclerTaskViewHolder> {
    public ArrayList<Task> listTask = null;
    public IClickItemListener iClickItemTask;

    public ZrecyclerTaskAdapter(ArrayList<Task> listTask) {
        this.listTask = listTask;
    }

    public void setOnItemClickListener(IClickItemListener listener){
        this.iClickItemTask = listener;
    }

    public void removeItem(int position) {
        listTask.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public ZrecyclerTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View convertView = inflater.inflate(R.layout.item_list_task, parent, false);
        return new ZrecyclerTaskViewHolder(convertView);

    }

    @Override
    public void onBindViewHolder(@NonNull ZrecyclerTaskViewHolder holder, int position) {
        if ((!listTask.isEmpty()) && (position >= 0)) {
            final Task task = listTask.get(position);
            final int pos = position;
            holder.tvTaskTitle.setText(task.getTaskName() + "");
            holder.tvTaskDescription.setText(task.getDescription() + "");
            holder.tvTaskTime.setText(task.getStartTime() + ", " + task.getStartDate() + " đến " + task.getEndTime() + ", " + task.getEndDate() + "");
            holder.layoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iClickItemTask.onItemClickTask(v, pos);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return listTask.size();
    }

    public static class ZrecyclerTaskViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTaskTitle;
        private final TextView tvTaskDescription;
        private final TextView tvTaskTime;
        private final CardView layoutItem;

        public ZrecyclerTaskViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutItem = itemView.findViewById(R.id.item_work);
            tvTaskTitle = itemView.findViewById(R.id.task_txt_title);
            tvTaskDescription = itemView.findViewById(R.id.task_txt_decription);
            tvTaskTime = itemView.findViewById(R.id.task_txt_time);
        }

    }


}
