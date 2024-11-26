package com.nttdata.application.service;

import com.nttdata.domain.entity.Transaction;
import com.nttdata.domain.entity.User;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xddf.usermodel.chart.ChartTypes;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExcelService {

    public List<User> parseExcelFile(MultipartFile file) throws IOException {
        List<User> users = new ArrayList<>();

        try (InputStream is = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                User user = new User();
                user.setName(row.getCell(0).getStringCellValue());
                user.setEmail(row.getCell(1).getStringCellValue());
                user.setLogin(row.getCell(2).getStringCellValue());
                user.setPassword(row.getCell(3).getStringCellValue());
                user.setDate(new Date());
                users.add(user);
            }
        }
        return users;
    }

    public ByteArrayOutputStream generateExpenseAnalysisExcel(List<Transaction> transactions) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.createSheet("Análise de Despesas");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Categoria");
            header.createCell(1).setCellValue("Tipo");
            header.createCell(2).setCellValue("Valor");
            header.createCell(3).setCellValue("Data");

            int rowNum = 1;
            for (Transaction transaction : transactions) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(transaction.getCategory().name());
                row.createCell(1).setCellValue(transaction.getType().name());
                row.createCell(2).setCellValue(transaction.getAmount());
                row.createCell(3).setCellValue(transaction.getDate().toString());
            }

            createBarChart(workbook, sheet, "Despesas por Categoria (Business)", 5, 0, transactions, "business");
            createBarChart(workbook, sheet, "Despesas por Categoria (Current)", 20, 0, transactions, "current");
            createBarChart(workbook, sheet, "Despesas por Categoria (Saving)", 35, 0, transactions, "saving");

            workbook.write(out);
            return out;
        }
    }

    private void createBarChart(XSSFWorkbook workbook, XSSFSheet sheet, String chartTitle, int rowStart, int colStart, List<Transaction> transactions, String accountType) {
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, colStart, rowStart, colStart + 10, rowStart + 15);
        XSSFChart chart = drawing.createChart(anchor);

        List<Transaction> filteredTransactions = transactions.stream()
            .filter(t -> t.getAccount().getType().toString().equalsIgnoreCase(accountType))
            .collect(Collectors.toList());

        Map<String, Double> categoryTotals = filteredTransactions.stream()
            .collect(Collectors.groupingBy(
                t -> t.getCategory().name(),
                Collectors.summingDouble(Transaction::getAmount)
            ));

        chart.setTitleText(chartTitle);
        chart.setTitleOverlay(false);
        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.TOP_RIGHT);

        XDDFCategoryAxis categoryAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        categoryAxis.setTitle("Categorias");
        XDDFValueAxis valueAxis = chart.createValueAxis(AxisPosition.LEFT);
        valueAxis.setTitle("Total Gasto");

        XDDFDataSource<String> categories = XDDFDataSourcesFactory.fromArray(
            categoryTotals.keySet().toArray(new String[0])
        );
        XDDFNumericalDataSource<Double> values = XDDFDataSourcesFactory.fromArray(
            categoryTotals.values().toArray(new Double[0])
        );

        XDDFBarChartData barData = (XDDFBarChartData) chart.createData(ChartTypes.BAR, categoryAxis, valueAxis);
        XDDFBarChartData.Series series = (XDDFBarChartData.Series) barData.addSeries(categories, values);
        series.setTitle("Despesas", null);

        chart.plot(barData);
    }
}
