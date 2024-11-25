package com.nttdata.application.service;

import com.nttdata.domain.entity.User;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ExcelService {

    public List<User> parseExcelFile(MultipartFile file) throws IOException {
        List<User> users = new ArrayList<>();

        try (InputStream is = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0); // Primeira aba do Excel

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Ignorar cabeçalho

                User user = new User();
                user.setName(row.getCell(0).getStringCellValue());
                user.setEmail(row.getCell(1).getStringCellValue());
                user.setLogin(row.getCell(2).getStringCellValue());
                user.setPassword(row.getCell(3).getStringCellValue());
                user.setDate(new Date()); // Use uma data padrão para novos registros
                users.add(user);
            }
        }
        return users;
    }
}
