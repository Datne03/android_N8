package haui.android.taskmanager.views;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import java.util.ArrayList;
import java.util.List;

import haui.android.taskmanager.R;
import haui.android.taskmanager.controller.DBHelper;
import haui.android.taskmanager.models.Task;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchTaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchTaskFragment extends Fragment {
    Toolbar mToolbarSearch;
    TextView emptySearchView;

    ListView listView;
    List<Task> taskArrayList = new ArrayList<Task>();
    TaskViewAdapter arrayAdapter = null;
    DBHelper dbHelper;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchTaskFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchWorkFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchTaskFragment newInstance(String param1, String param2) {
        SearchTaskFragment fragment = new SearchTaskFragment();
        Bundle args = new Bundle();
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

    private void setUpMenu(View view) {
        // Cài đặt Toolbar vào Fragment
        ((AppCompatActivity) requireActivity()).setSupportActionBar(mToolbarSearch);

        // Tùy chỉnh Toolbar
        mToolbarSearch.setCollapseIcon(R.drawable.baseline_arrow_back_24);
        mToolbarSearch.setNavigationIcon(R.drawable.baseline_close_32);

        mToolbarSearch.setNavigationOnClickListener(v -> {
//                backToTaskListFragment(new ListTaskFragment());
            // Vì ở ListFragment đã thên vào backstack rồi nên chỉ cần quay lại là được
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // Menu cũ bị deprecated, dùng cái này để quaản lý menu
        // Đăng ký MenuProvider
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                // Tạo menu
                menuInflater.inflate(R.menu.search_task_menu, menu);
                MenuItem searchItem = menu.findItem(R.id.action_search_task);

                SearchView searchView = (SearchView) searchItem.getActionView();

                if (searchView != null) {
                    ImageView clearButton = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
                    if (clearButton != null) {
                        clearButton.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN); // Thay đổi màu sắc ở đây
                    }
                    // Mở rộng SearchView ngay lập tức
                    searchItem.expandActionView();
                    searchView.requestFocus();

                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            List<Task> newList = dbHelper.searchTasks(query);
                            upDateTaskList(newList);
                            checkEmptyView(query);
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            return false;
                        }
                    });
                }
            }

            @Override
            public void onPrepareMenu(@NonNull Menu menu) {
                MenuProvider.super.onPrepareMenu(menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem item) {
                return false;
            }

        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

    }


    private void initView(View view) {
        mToolbarSearch = view.findViewById(R.id.m_toolbar_search);
        emptySearchView = view.findViewById(R.id.empty_search_view);
        listView = view.findViewById(R.id.search_task_listview);

        setUpMenu(view);
    }

    // Hàm kiểm tra dữ liệu để hiển thị Empty View
    private void checkEmptyView(String query) {
        if (taskArrayList.isEmpty()) {
            listView.setVisibility(View.GONE);
            emptySearchView.setText("Không tìm thấy mục nào cho '" + query + "'");
            emptySearchView.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            emptySearchView.setVisibility(View.GONE);
        }
    }

    private void upDateTaskList(List<Task> newList) {
        taskArrayList.clear();
        taskArrayList.addAll(newList);
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_task, container, false);
        initView(view);
        dbHelper = new DBHelper(getContext());

        arrayAdapter = new TaskViewAdapter(requireContext(), taskArrayList);
        listView.setAdapter(arrayAdapter);

        listView.setEmptyView(emptySearchView);
        emptySearchView.setVisibility(View.GONE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String data = String.valueOf(taskArrayList.get(position).getTaskID());

                Fragment editTaskFragment = EditTaskFragment.newInstance(data);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, editTaskFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        return view;
    }
}