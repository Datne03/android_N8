package haui.android.taskmanager.views;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import haui.android.taskmanager.R;
import haui.android.taskmanager.controller.DBHelper;
import haui.android.taskmanager.notification.NotificationScheduler;

public class ZswipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
    private final ZrecyclerTaskAdapter mAdapter;
    private final Context mContext;
    private final DBHelper mDbHelper;
    private final IOnListEmptyListener listEmptyListener;

    public ZswipeToDeleteCallback(ZrecyclerTaskAdapter adapter, Context context, DBHelper dbHelper, IOnListEmptyListener listener) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT); // Chỉ cho phép trượt trái và phải
        mAdapter = adapter;
        mContext = context;
        mDbHelper = dbHelper;
        listEmptyListener = listener;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        return false; // Không cần xử lý di chuyển
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        // Lấy vị trí của item bị trượt
        int position = viewHolder.getAdapterPosition();

        // Hiển thị dialog xác nhận xóa
        new AlertDialog.Builder(mContext)
                .setTitle("Xác nhận")
                .setMessage("Bạn có muốn xóa mục này không?")
                .setPositiveButton("Có", (dialog, which) -> {
                    // Xử lý xóa item
                    int taskID = mAdapter.listTask.get(position).getTaskID();
                    Log.d("Check taskID: ", "onSwiped: " + taskID);
                    mDbHelper.deleteTask(taskID);
                    mAdapter.removeItem(position);
                    // Kiểm tra nếu danh sách trống, gọi lại Fragment
                    if (mAdapter.getItemCount() == 0 && listEmptyListener != null) {
                        listEmptyListener.onListEmpty();
                    }
                    NotificationScheduler notificationScheduler = new NotificationScheduler(mContext);
                    notificationScheduler.cancelNotification(taskID + 1000);
                    notificationScheduler.cancelNotification(taskID + 1999);
                    Snackbar snackbar = Snackbar.make(viewHolder.itemView, "Đã xóa", Snackbar.LENGTH_LONG);
                    snackbar.show();
                })
                .setNegativeButton("Không", (dialog, which) -> {
                    // Thực hiện hủy thao tác trượt
                    mAdapter.notifyItemChanged(position);
                })
                .show();
        Log.d("Check dialog logic: ", "code block after dialog");
    }

    @Override
    public void onChildDraw(@NonNull Canvas c,
                            @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        // Hiển thị icon khi trượt (ví dụ: icon xóa)
        if (isCurrentlyActive) {
            // Vẽ icon khi trượt
            Drawable icon = ContextCompat.getDrawable(mContext, R.drawable.baseline_delete_36);

            int right = viewHolder.itemView.getRight();
            int left = viewHolder.itemView.getLeft();
            int top = viewHolder.itemView.getTop();
            int bottom = viewHolder.itemView.getBottom();
            int width = viewHolder.itemView.getWidth();
            int height = viewHolder.itemView.getHeight();
//            Log.d("Check position: ", "right: " + right + ", left: " + left + ", top: " + top + ", bottom: " + bottom);
//            Log.d("Check direction: ", "dX: " + dX + ", dY: " + dY);
            if (icon != null) {
                int iconWidth = icon.getIntrinsicWidth();
                int iconHeight = icon.getIntrinsicHeight();
//                Log.d("Check icon size: ", "iconWidth: " + iconWidth + ", iconHeight: " + iconHeight);
                // Trượt từ phải qua trái
                if (dX < 0) {
                    icon.setBounds(
                            right - 200, // Vị trí icon
                            top + (height - iconHeight) / 2,
                            right - 200 + iconWidth, // Vị trí icon
                            bottom - (height - iconHeight) / 2
                    );
                } else {
                    icon.setBounds(left + 200 - iconWidth,
                            top + (height - iconHeight) / 2,
                            left + 200,
                            bottom - (height - iconHeight) / 2);
                }
                icon.draw(c);
            }
        }

    }

    public interface IOnListEmptyListener {
        void onListEmpty();

    }
}
