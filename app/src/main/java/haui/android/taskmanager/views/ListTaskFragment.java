package haui.android.taskmanager.views;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import haui.android.taskmanager.R;
import haui.android.taskmanager.controller.DBHelper;
import haui.android.taskmanager.models.Task;
import haui.android.taskmanager.notification.NotificationScheduler;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListTaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListTaskFragment extends Fragment implements View.OnClickListener {
    // add
    public static final String TAG = "ListTaskFragment";

    AppCompatButton btnAll, btnWorking, btnLate, btnComplete;
    AppCompatImageView btnSearch;
//    LinearLayout linearLayoutList;

    RecyclerView recyclerViewTask;
    ZrecyclerTaskAdapter arrayAdapter = null;

    // add
    TextView emptyView;

    List<Task> taskArrayList = new ArrayList<>();
    DBHelper dbHelper;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ListTaskFragment() {
        // Required empty public constructor
    }

    private void initView(View view) {
        btnAll = view.findViewById(R.id.list_btn_all);
        btnWorking = view.findViewById(R.id.list_btn_working);
        btnLate = view.findViewById(R.id.list_btn_late);
        btnComplete = view.findViewById(R.id.list_btn_complete);
        btnSearch = view.findViewById(R.id.list_btn_search);

//        linearLayoutList = view.findViewById(R.id.linearLayoutList);

        recyclerViewTask = view.findViewById(R.id.recycler_view_task);

        emptyView = view.findViewById(R.id.emptyView);
    }

    // TODO: Rename and change types and number of parameters
    // Thằng này chưa dùng ở đâu cả
    public static ListTaskFragment newInstance(String param1, String param2) {
        ListTaskFragment fragment = new ListTaskFragment();
        Bundle args = new Bundle();
        // Có vẻ 2 thằng này không có tác dụng gì
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    // Phương thức để đặt trạng thái chọn cho nút và thay đổi màu
    private void setButtonSelected(AppCompatButton button) {
        button.setTextColor(Color.WHITE);
        button.setBackgroundColor(Color.parseColor("#5F33E1"));
        button.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.border_search_button));
    }

    // Phương thức để đặt trạng thái không chọn cho nút và thay đổi màu
    private void setButtonUnselected(AppCompatButton button) {
        button.setTextColor(Color.parseColor("#5F33E1"));
        button.setBackgroundColor(Color.parseColor("#EDE8FF"));
        button.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.border_phanloai_listtask));
    }

    public void onResume() {
        super.onResume();
    }

    // Hàm kiểm tra dữ liệu để hiển thị Empty View
    private void checkEmptyView() {
        if (taskArrayList.isEmpty()) {
            recyclerViewTask.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerViewTask.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_task, container, false);
        initView(view);
        dbHelper = new DBHelper(getContext());

        try {

            List<Task> newList = dbHelper.getAllTasksByStatus(1);
            taskArrayList.clear();
            taskArrayList.addAll(newList);

            // Không đặt this ở đây được, vì thg này là fragment
            recyclerViewTask.setLayoutManager(new LinearLayoutManager(getActivity()));

            arrayAdapter = new ZrecyclerTaskAdapter((ArrayList<Task>) taskArrayList);

            arrayAdapter.setOnItemClickListener((v, position) -> showEditTaskFrag(position));

            recyclerViewTask.setAdapter(arrayAdapter);

            // Kiểm tra ngay khi khởi chạy
            checkEmptyView();

            ZswipeToDeleteCallback swipeToDeleteCallback = new ZswipeToDeleteCallback(
                    arrayAdapter, getContext(),
                    dbHelper, this::checkEmptyView); // imple interface
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
            itemTouchHelper.attachToRecyclerView(recyclerViewTask);

            // Cập nhật Empty View khi dữ liệu thay đổi
            arrayAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    checkEmptyView();
                }
            });
            // clean
            addButtonEvent();

        } catch (Exception ex) {
            Log.d(TAG, "onCreateView: " + ex);
        }
        return view;

    }


    private void showDeleteConfirmationDialog(int position) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn xóa công việc này không?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    // Remove the item from the list
                    int taskID = taskArrayList.get(position).getTaskID();
                    dbHelper.deleteTask(taskID);
                    taskArrayList.remove(position);
                    arrayAdapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(), "Xóa thành công.", Toast.LENGTH_SHORT).show();
                    // Xóa thông báo cũ đã hẹn giờ
                    NotificationScheduler notificationScheduler = new NotificationScheduler(requireContext());
                    notificationScheduler.cancelNotification(taskID + 1000);
                    notificationScheduler.cancelNotification(taskID + 1999);
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_delete)
                .show();
    }

    @Override
    public void onClick(View v) {
        if (v == btnAll) {
            showAll();
        } else if (v == btnWorking) {
            showWorking();
        } else if (v == btnComplete) {
            showComplete();
        } else if (v == btnLate) {
            showLate();
        } else if (v == btnSearch) {
            showSearchFrag();
        }
    }

    private void upDateTaskList(List<Task> newList) {
        taskArrayList.clear();
        taskArrayList.addAll(newList);
        arrayAdapter.notifyDataSetChanged();
    }

    private void showAll() {
        setButtonSelected(btnAll);
        setButtonUnselected(btnWorking);
        setButtonUnselected(btnComplete);
        setButtonUnselected(btnLate);

        taskArrayList.clear();
        List<Task> newList = dbHelper.getAllTasksByStatus(1);
        upDateTaskList(newList);

//        Toast.makeText(getActivity(), "Show all. Check adapterListener: " + arrayAdapter.iClickItemTask, Toast.LENGTH_SHORT).show();
    }

    private void showWorking() {
        setButtonSelected(btnWorking);
        setButtonUnselected(btnAll);
        setButtonUnselected(btnComplete);
        setButtonUnselected(btnLate);

        taskArrayList.clear();
        List<Task> newList = dbHelper.getAllTasksByStatus(2);
        upDateTaskList(newList);

//        Toast.makeText(getActivity(), "Show all. Check adapterListener: " + arrayAdapter.iClickItemTask, Toast.LENGTH_SHORT).show();

    }

    private void showComplete() {
        setButtonSelected(btnComplete);
        setButtonUnselected(btnWorking);
        setButtonUnselected(btnAll);
        setButtonUnselected(btnLate);

        taskArrayList.clear();
        List<Task> newList = dbHelper.getAllTasksByStatus(3);
        upDateTaskList(newList);

//        Toast.makeText(getActivity(), "Show all. Check adapterListener: " + arrayAdapter.iClickItemTask, Toast.LENGTH_SHORT).show();

    }

    private void showLate() {
        setButtonSelected(btnLate);
        setButtonUnselected(btnWorking);
        setButtonUnselected(btnComplete);
        setButtonUnselected(btnAll);

        taskArrayList.clear();
        List<Task> newList = dbHelper.getAllTasksByStatus(4);
        upDateTaskList(newList);

//        Toast.makeText(getActivity(), "Show all. Check adapterListener: " + arrayAdapter.iClickItemTask, Toast.LENGTH_SHORT).show();

    }

    private void showSearchFrag() {
        if (getActivity() == null) {
            Log.e(TAG, "ShowSearchFrag: getActivity() is null.");
            return;
        }
        Fragment searchTaskFragment = new SearchTaskFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, searchTaskFragment)
                .addToBackStack(null)
                .commit();

    }

    private void addButtonEvent() {
        btnAll.setOnClickListener(this);
        btnWorking.setOnClickListener(this);
        btnComplete.setOnClickListener(this);
        btnLate.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
    }

    private void showEditTaskFrag(int position) {
        if (getActivity() == null) {
            Log.e(TAG, "ShowEditTaskFrag: getActivity() is null.");
            return;
        }
        String data = String.valueOf(taskArrayList.get(position).getTaskID());
        Log.d("ID task", "onItemClick: " + data);
        Fragment editTaskFragment = EditTaskFragment.newInstance(data);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, editTaskFragment)
                .addToBackStack(null)
                .commit();

    }
}