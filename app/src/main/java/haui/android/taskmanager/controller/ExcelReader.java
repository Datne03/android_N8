package haui.android.taskmanager.controller;

import static android.content.ContentValues.TAG;

import android.util.Log;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelReader {

    public static List<String> readExcelFile(InputStream inputStream) {
        List<String> sheetContent = new ArrayList<>();
        Workbook workbook = null;

        try {
            // Sử dụng Apache POI để mở Workbook
            workbook = WorkbookFactory.create(inputStream);
            // Lấy sheet đầu tiên
            Sheet sheet = workbook.getSheetAt(0);

            // Lặp qua các hàng và cột
            for (Row row : sheet) {
                StringBuilder rowContent = new StringBuilder();
                for (Cell cell : row) {
                    int colIndex = cell.getColumnIndex();
                    int rowIndex = cell.getRowIndex();
                    // Bỏ qua các hàng/cột không cần thiết
                    if (rowIndex <= 6 || colIndex == 0 || colIndex >= 10) {
                        continue;
                    }
                    rowContent.append(cell.toString()).append("|");
                }
                if (!rowContent.toString().contains("|||") && !rowContent.toString().isEmpty()) {
                    sheetContent.add(rowContent.toString());
                    Log.d(TAG, "readExcelFile: " + rowContent);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error reading Excel file: ", e);
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error closing workbook: ", e);
            }
        }

        return sheetContent;
    }
}
