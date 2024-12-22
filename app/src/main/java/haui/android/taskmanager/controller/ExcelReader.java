package haui.android.taskmanager.controller;

import android.util.Log;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelReader {
    private static final String TAG = "ExcelReader";

    public static List<String> readExcelFile(InputStream inputStream) {
        List<String> sheetContent = new ArrayList<>();
        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            // Lấy sheet đầu tiên
            Sheet sheet = workbook.getSheetAt(0);

            // Duyệt qua các hàng và cột
            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                StringBuilder rowContent = new StringBuilder();

                for (int colIndex = 0; colIndex < row.getLastCellNum(); colIndex++) {
                    if (row.getRowNum() <= 6 || colIndex == 0 || colIndex >= 10) continue;

                    Cell cell = row.getCell(colIndex);
                    if (cell != null) {
                        rowContent.append(getCellValue(cell)).append("|");
                    }
                }

                if (!rowContent.toString().contains("|||") && !rowContent.toString().isEmpty()) {
                    sheetContent.add(rowContent.toString());
                    Log.d(TAG, "readExcelFile: " + rowContent);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error reading Excel file: ", e);
        }

        return sheetContent;
    }

    private static String getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
}
