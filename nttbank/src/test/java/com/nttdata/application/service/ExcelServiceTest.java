package com.nttdata.application.service;

import com.nttdata.domain.entity.Account;
import com.nttdata.domain.entity.Transaction;
import com.nttdata.domain.entity.User;
import com.nttdata.domain.enums.AccountType;
import com.nttdata.domain.enums.TransactionCategory;
import com.nttdata.domain.enums.TransactionType;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExcelServiceTest {

    @InjectMocks
    private ExcelService excelService;

    @Mock
    private List<User> users;

    @Mock
    private List<Transaction> transactions;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testParseExcelFile() throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Users");

        XSSFRow headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Nome");
        headerRow.createCell(1).setCellValue("Email");
        headerRow.createCell(2).setCellValue("Login");
        headerRow.createCell(3).setCellValue("Senha");

        XSSFRow row1 = sheet.createRow(1);
        row1.createCell(0).setCellValue("User1");
        row1.createCell(1).setCellValue("user1@example.com");
        row1.createCell(2).setCellValue("login1");
        row1.createCell(3).setCellValue("senha1");

        XSSFRow row2 = sheet.createRow(2);
        row2.createCell(0).setCellValue("User2");
        row2.createCell(1).setCellValue("user2@example.com");
        row2.createCell(2).setCellValue("login2");
        row2.createCell(3).setCellValue("senha2");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        byte[] excelData = out.toByteArray();

        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", excelData);

        List<User> users = excelService.parseExcelFile(file);

        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("User1", users.get(0).getName());
        assertEquals("user1@example.com", users.get(0).getEmail());
        assertEquals("login1", users.get(0).getLogin());
    }


    @Test
    void testGenerateFullUserReport() throws IOException {
        List<User> mockUsers = new ArrayList<>();
        User user = new User();
        user.setName("User1");
        user.setEmail("user1@example.com");
        mockUsers.add(user);

        ByteArrayOutputStream out = excelService.generateFullUserReport(mockUsers);

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(out.toByteArray());
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            assertNotNull(workbook);
            assertEquals(1, workbook.getNumberOfSheets());
            assertEquals("Relatório de Usuários", workbook.getSheetAt(0).getSheetName());
        }
    }

    @Test
    void testGenerateExpenseAnalysisExcel() throws IOException {
        List<Transaction> mockTransactions = new ArrayList<>();

        Account account1 = new Account();
        account1.setType(AccountType.EMPRESARIAL);

        Transaction transaction1 = new Transaction();
        transaction1.setCategory(TransactionCategory.OUTROS);
        transaction1.setAmount(100.0);
        transaction1.setType(TransactionType.PIX);
        transaction1.setDate(new Date());
        transaction1.setAccount(account1);
        mockTransactions.add(transaction1);

        Account account2 = new Account();
        account2.setType(AccountType.POUPANÇA);

        Transaction transaction2 = new Transaction();
        transaction2.setCategory(TransactionCategory.ALIMENTAÇAO);
        transaction2.setAmount(50.0);
        transaction2.setType(TransactionType.DEPOSITO);
        transaction2.setDate(new Date());
        transaction2.setAccount(account2);
        mockTransactions.add(transaction2);

        ByteArrayOutputStream out = excelService.generateExpenseAnalysisExcel(mockTransactions);

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(out.toByteArray());
             XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {

            assertNotNull(workbook);
            assertEquals(1, workbook.getNumberOfSheets());
            assertEquals("Análise de Despesas", workbook.getSheetAt(0).getSheetName());

            XSSFSheet sheet = workbook.getSheetAt(0);

            assertTrue(sheet.getPhysicalNumberOfRows() > 1);

            XSSFRow row1 = sheet.getRow(1);
            assertNotNull(row1);
            assertEquals("OUTROS", row1.getCell(0).getStringCellValue());

            XSSFCell cell = row1.getCell(1);
            if (cell.getCellType() == CellType.NUMERIC) {
                assertEquals(100.0, cell.getNumericCellValue(), 0.01);
            } else if (cell.getCellType() == CellType.STRING) {
                assertEquals("PIX", cell.getStringCellValue());
            }

            XSSFRow row2 = sheet.getRow(2);
            assertNotNull(row2);
            assertEquals("ALIMENTAÇAO", row2.getCell(0).getStringCellValue());
            XSSFCell cell2 = row2.getCell(1);
            if (cell2.getCellType() == CellType.NUMERIC) {
                assertEquals(50.0, cell2.getNumericCellValue(), 0.01);
            } else if (cell2.getCellType() == CellType.STRING) {
                assertEquals("DEPOSITO", cell2.getStringCellValue());
            }
        }
    }
}
