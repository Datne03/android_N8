package haui.android.taskmanager;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import haui.android.taskmanager.controller.DBHelper;
    import haui.android.taskmanager.controller.ExcelReader;
import haui.android.taskmanager.controller.ExcelWriter;
import haui.android.taskmanager.models.Status;
import haui.android.taskmanager.models.Tag;
import haui.android.taskmanager.models.Task;
import haui.android.taskmanager.notification.NotificationScheduler;
import haui.android.taskmanager.views.CalendarFragment;
import haui.android.taskmanager.views.CreateTaskFragment;
import haui.android.taskmanager.views.HomeFragment;
import haui.android.taskmanager.views.ListNotificationFragment;
import haui.android.taskmanager.views.ListTaskFragment;

public class MainActivity extends AppCompatActivity {
    public static MeowBottomNavigation bottomNavigation;
    private LinearLayout subMenuContainer;

    private static final int REQUEST_CODE_PERMISSIONS = 100;
    private static final int REQUEST_CODE_NOTIFICATION_PERMISSION = 101;
    private static final String TAG = "MainActivity";
    private ActivityResultLauncher<Intent> filePickerLauncher;

    private static final int REQUEST_CODE_TEMPLATE_EDIT = 1001;
    private static final int REQUEST_CODE_FILE_PICKER = 1001;


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: 0");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNavigation = findViewById(R.id.bottom_navigation);
        subMenuContainer = findViewById(R.id.submenu_navbar_container);

       // findViewById(R.id.btnEditTemplate).setOnClickListener(v -> openTemplateForEditing());
        findViewById(R.id.btnImportFile).setOnClickListener(v -> openFilePicker());

        // Add navigation items
        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.navbar_ic_home));
        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.navbar_ic_calendar));
        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.navbar_ic_add_more));
        bottomNavigation.add(new MeowBottomNavigation.Model(4, R.drawable.navbar_ic_task));
        bottomNavigation.add(new MeowBottomNavigation.Model(5, R.drawable.navbar_ic_noti_normal));

        // Set up listeners
        bottomNavigation.setOnShowListener(model -> {
            Log.d(TAG, "Item selected: " + model.getId());
        });

        // Handle item clicks
        bottomNavigation.setOnClickMenuListener(model -> {
            int enterAnimation = R.anim.enter_from_right;
            int exitAnimation = R.anim.exit_to_left;
            switch (model.getId()) {
                case 1:
                    // Open Home
                    enterAnimation = R.anim.enter_from_right;
                    exitAnimation = R.anim.exit_to_left;
                    openFragment(new HomeFragment(), enterAnimation, exitAnimation);
                    subMenuContainer.setVisibility(View.GONE);
                    break;
                case 2:
                    // Open Calendar
                    enterAnimation = R.anim.enter_from_right;
                    exitAnimation = R.anim.exit_to_left;
                    openFragment(new CalendarFragment(), enterAnimation, exitAnimation);
                    subMenuContainer.setVisibility(View.GONE);
                    break;
                case 3:
                    // Open submenu
                    showSubMenuDialog();
                    break;
                case 4:
                    // Open Task
                    enterAnimation = R.anim.enter_from_left;
                    exitAnimation = R.anim.exit_to_right;
                    openFragment(new ListTaskFragment(), enterAnimation, exitAnimation);
                    subMenuContainer.setVisibility(View.GONE);
                    break;
                case 5:
                    // Open Notification
                    openFragment(new ListNotificationFragment(), enterAnimation, exitAnimation);
                    subMenuContainer.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        });

        bottomNavigation.setOnReselectListener(model -> {
            // Handle reselection if needed
            int enterAnimation = R.anim.enter_from_right;
            int exitAnimation = R.anim.exit_to_left;
            switch (model.getId()) {
                case 1:
                    // Open Home
                    enterAnimation = R.anim.enter_from_right;
                    exitAnimation = R.anim.exit_to_left;
                    openFragment(new HomeFragment(), enterAnimation, exitAnimation);
                    subMenuContainer.setVisibility(View.GONE);
                    break;
                case 2:
                    // Open Calendar
                    enterAnimation = R.anim.enter_from_right;
                    exitAnimation = R.anim.exit_to_left;
                    openFragment(new CalendarFragment(), enterAnimation, exitAnimation);
                    subMenuContainer.setVisibility(View.GONE);
                    break;
                case 3:
                    // Open submenu
                    showSubMenuDialog();
                    break;
                case 4:
                    // Open Task
                    enterAnimation = R.anim.enter_from_left;
                    exitAnimation = R.anim.exit_to_right;
                    openFragment(new ListTaskFragment(), enterAnimation, exitAnimation);
                    subMenuContainer.setVisibility(View.GONE);
                    break;
                case 5:
                    // Open Notification
                    openFragment(new ListNotificationFragment(), enterAnimation, exitAnimation);
                    subMenuContainer.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        });

        // Set default selection
        // bottomNavigation.setCount(1, "5");
        bottomNavigation.show(1, true);
        openFragment(new HomeFragment(), -1, -1);
        subMenuContainer.setVisibility(View.GONE);

        // Initialize the file picker launcher
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri uri = data.getData();
//                            Log.d(TAG, "Selected file URI: " + uri);
                            readExcelFileFromUri(uri);
                        }
                    }
                });

        requestPermissions();
    }


    private void openFragment(Fragment fragment, int enterAnimation, int exitAnimation) {
        if (enterAnimation == -1 || exitAnimation == -1) {
            enterAnimation = R.anim.exit_to_left;
            exitAnimation = R.anim.enter_from_right;
        }
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(enterAnimation, exitAnimation)
                .replace(R.id.fragment_container, fragment)
                .commit();
    }


    private void showSubMenuDialog() {
//        LinearLayout subMenuContainer = findViewById(R.id.submenu_navbar_container);

        if (subMenuContainer.getVisibility() == View.VISIBLE) {
            subMenuContainer.setVisibility(View.GONE);
        } else {
            subMenuContainer.setVisibility(View.VISIBLE);
        }


//        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
//        View sheetView = getLayoutInflater().inflate(R.layout.submenu_navbar_container, null);
//        bottomSheetDialog.setContentView(sheetView);

        TextView btnCreateTask = subMenuContainer.findViewById(R.id.btnCreateTask);
       // TextView btnEditTemplate = subMenuContainer.findViewById(R.id.btnEditTemplate);
        TextView btnImportFile = subMenuContainer.findViewById(R.id.btnImportFile);
        TextView btnExportFile = subMenuContainer.findViewById(R.id.btnExportFile);

        btnCreateTask.setOnClickListener(v -> {
            // Handle Notepad button click
//            bottomSheetDialog.dismiss();
            openFragment(new CreateTaskFragment(), -1, -1);
            subMenuContainer.setVisibility(View.GONE);
        });

//        btnEditTemplate.setOnClickListener(v -> {
//            // Handle Reminder button click
////            bottomSheetDialog.dismiss();
//            openTemplateForEditing();
//            subMenuContainer.setVisibility(View.GONE);
//        });

        btnImportFile.setOnClickListener(v -> {
            // Handle Create Task button click
//            bottomSheetDialog.dismiss();
            openFilePickerWithTemplateEdit();
            subMenuContainer.setVisibility(View.GONE);
        });

        btnExportFile.setOnClickListener(v -> {
            // Handle Create Task button click
//            bottomSheetDialog.dismiss();
            exportDataToExcel();
            subMenuContainer.setVisibility(View.GONE);
        });

//        bottomSheetDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void requestPermissions() {
        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.USE_EXACT_ALARM
        };

        boolean allPermissionsGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }

        if (!allPermissionsGranted) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSIONS);
        } else {
            openFilePicker(); // Chỉ mở file picker khi quyền đã được cấp
        }
    }



    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        intent.setType("application/vnd.ms-excel");

        String[] mimeTypes = {
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        };

        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        filePickerLauncher.launch(intent);
    }
    private void openFilePickerWithTemplateEdit() {
        String templateFileName = "task_template.xlsx";
        File externalFile = new File(getExternalFilesDir(null), templateFileName);

        if (!externalFile.exists()) {
            try {
                InputStream is = getAssets().open(templateFileName);
                FileOutputStream os = new FileOutputStream(externalFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
                is.close();
                os.close();
            } catch (Exception e) {
                showAlert("Lỗi khi sao chép file mẫu.");
                return;
            }
        }

        // Mở file mẫu để chỉnh sửa
        Uri uri = FileProvider.getUriForFile(this, "haui.android.taskmanager.fileprovider", externalFile);
        Intent editIntent = new Intent(Intent.ACTION_EDIT);
        editIntent.setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        editIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent chooser = Intent.createChooser(editIntent, "Chỉnh sửa mẫu Excel");
        startActivityForResult(chooser, REQUEST_CODE_TEMPLATE_EDIT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_TEMPLATE_EDIT) {
            if (resultCode == RESULT_OK || resultCode == RESULT_CANCELED) {
                // Mở file picker sau khi chỉnh sửa file mẫu
                openFilePicker();
            } else {
                showAlert("Chỉnh sửa tệp mẫu thất bại hoặc bị hủy.");
            }
        } else if (requestCode == REQUEST_CODE_FILE_PICKER) {
            if (resultCode == RESULT_OK && data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    readExcelFileFromUri(uri);
                } else {
                    showAlert("Không tìm thấy file được chọn.");
                }
            } else {
                showAlert("Chọn file thất bại hoặc bị hủy.");
            }
        }
    }


    private void readExcelFileFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                List<String> sheetContent = ExcelReader.readExcelFile(inputStream);

                DBHelper dbHelper = new DBHelper(this);
                for (String row : sheetContent) {
                    try {
                        String[] cell = row.split("\\|");

                        String name = cell[0];
                        String description = cell[1];
                        String startDate = cell[2];
                        String startTime = cell[3];
                        String endDate = cell[4];
                        String endTime = cell[5];
                        int priority = 1; // Mặc định 1, có thể chỉnh sửa sau
                        int selectedTagId;
                        String tagName = cell[7];
                        int statusId;
                        String statusName = cell[8];

                        // Xử lý tag
                        Tag tag = dbHelper.getTagByTagName(tagName);
                        if (tag != null) {
                            selectedTagId = tag.getTagID();
                        } else {
                            Tag newTag = new Tag(0, tagName, "");
                            selectedTagId = (int) dbHelper.addTag(newTag);
                        }

                        // Xử lý status
                        Status status = dbHelper.getStatusByName(statusName);
                        if (status != null) {
                            statusId = status.getStatusID();
                        } else {
                            statusId = (int) dbHelper.addStatus(tagName);
                        }

                        // Xử lý ngày tháng
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                        Date startDateTime = dateFormat.parse(startDate + " " + startTime);
                        Date endDateTime = dateFormat.parse(endDate + " " + endTime);

                        if (endDateTime.before(startDateTime)) {
                            showAlert("Ngày kết thúc phải sau ngày bắt đầu!");
                            return;
                        } else if (endDateTime.equals(startDateTime)) {
                            showAlert("Thời gian kết thúc phải sau thời gian bắt đầu!");
                            return;
                        }

                        long taskId = dbHelper.insertTask(name, description, startDate, startTime, endDate, endTime, priority, statusId, selectedTagId);

                        if (taskId != -1) {
                            showAlert("Thêm task thành công.");
                            scheduleTaskNotifications((int) taskId, name, description, startDateTime, endDateTime);
                        } else {
                            showAlert("Có lỗi xảy ra khi thêm task.");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi xử lý hàng: " + row, e);
                    }
                }
                inputStream.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi đọc file Excel: ", e);
        }
    }


    private void openExcelFile(File file) {
        Uri uri = FileProvider.getUriForFile(this, "haui.android.taskmanager.fileprovider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(Intent.createChooser(intent, "Chọn ứng dụng để mở tệp"));
        } catch (Exception e) {
            Toast.makeText(this, "Không tìm thấy ứng dụng nào để mở tệp Excel.", Toast.LENGTH_SHORT).show();
        }
    }
    private void exportDataToExcel() {
        String fileName = "tasks_export.xlsx";
        File externalFile = new File(getExternalFilesDir(null), fileName);

        try {
            // Lấy dữ liệu từ database
            DBHelper dbHelper = new DBHelper(this);
            List<Task> tasks = dbHelper.getAllTasks();

            // Chuyển dữ liệu thành dạng danh sách String[]
            List<String[]> data = new ArrayList<>();
            for (Task task : tasks) {
                Tag tag = dbHelper.getTagById(task.getTagID());
                Status status = dbHelper.getStatus(task.getStatusID());

                data.add(new String[]{
                        task.getTaskName(),
                        task.getDescription(),
                        task.getStartDate(),
                        task.getStartTime(),
                        task.getEndDate(),
                        task.getEndTime(),
                        String.valueOf(task.getPriority()),
                        tag != null ? tag.getTagName() : "N/A",
                        status != null ? status.getStatusName() : "N/A"
                });
            }

            // Sử dụng ExcelWriter để ghi dữ liệu ra file
            ExcelWriter.writeTasksToExcel(externalFile, data, "Tasks");

            // Mở file Excel sau khi xuất
            openExcelFile(externalFile);

        } catch (Exception e) {
            Log.e(TAG, "Error exporting data to Excel: ", e);
            showAlert("Lỗi khi xuất dữ liệu: " + e.getMessage());
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                openFilePicker();
            } else {
                System.out.println("Required permissions not granted.");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void showAlert(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Thông báo")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void scheduleTaskNotifications(int taskID, String taskName, String taskDescription, Date startDateTime, Date endDateTime) {
        String channelId = "taskNotifications";
        String channelName = "Thông báo nhiệm vụ";
        String channelDescription = "Thông báo cho các sự kiện nhiệm vụ\n";

        NotificationScheduler notificationScheduler = new NotificationScheduler(this);

        notificationScheduler.createNotificationChannel(channelId, channelName, channelDescription, NotificationManager.IMPORTANCE_HIGH);

        // Tính thời gian bằng mili giây
        int startNotificationId = taskID + 1000;
        int endNotificationId = startNotificationId + 999;

        long startTimeMillis = startDateTime.getTime() - System.currentTimeMillis();
        long endTimeMillis = endDateTime.getTime() - System.currentTimeMillis();

        notificationScheduler.scheduleNotificationWithTwoActions(channelId,
                startNotificationId,
                "Bắt đầu: " + taskName, taskDescription,
                startTimeMillis);
        notificationScheduler.scheduleNotificationWithTwoActions(channelId,
                endNotificationId,
                "Hết hạn: " + taskName, taskDescription,
                endTimeMillis);
    }

}