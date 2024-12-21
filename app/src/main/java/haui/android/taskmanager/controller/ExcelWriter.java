package haui.android.taskmanager.controller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class ExcelWriter {

    public static void writeTasksToExcel(File file, List<String[]> data, String sheetName) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);

        // Tạo tiêu đề
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Tên nhiệm vụ", "Mô tả", "Ngày bắt đầu", "Thời gian bắt đầu", "Ngày kết thúc", "Thời gian kết thúc", "Sự ưu tiên", "Nhãn", "Trạng thái"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Thêm dữ liệu
        for (int i = 0; i < data.size(); i++) {
            Row row = sheet.createRow(i + 1);
            String[] rowData = data.get(i);
            for (int j = 0; j < rowData.length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(rowData[j]);
            }
        }

        // Ghi dữ liệu ra file
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            workbook.write(outputStream);
        }

        workbook.close();
    }
}
