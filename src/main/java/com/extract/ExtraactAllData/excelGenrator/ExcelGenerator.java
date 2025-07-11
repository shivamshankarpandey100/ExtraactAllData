package com.extract.ExtraactAllData.excelGenrator;

import com.extract.ExtraactAllData.model.ExtractedData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import java.util.List;

public class ExcelGenerator {

    public static byte[] convertToExcel(List<ExtractedData> dataList) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Extracted Data");

        // Create header row
        String[] headers = {
                "Image No", "Panda Name", "Bahi Name", "Folio No", "Data Position", "District",
                "Tehsil", "Station", "Post Office", "City/Village", "From Place", "Caste", "Subcaste",
                "Individual ID", "Given Name", "Surname", "Relation", "Gender", "Family ID",
                "Ritual Name", "Whose Ritual 1", "Whose Ritual 2", "Contact No1", "Contact No2",
                "Flags/Exceptions", "Additional Info"
        };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        // Fill data rows
        int rowNum = 1;
        for (ExtractedData data : dataList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(data.getImageNo());
            row.createCell(1).setCellValue(data.getPandaName());
            row.createCell(2).setCellValue(data.getBahiName());
            row.createCell(3).setCellValue(data.getFolioNo());
            row.createCell(4).setCellValue(data.getDataPosition());
            row.createCell(5).setCellValue(data.getDistrict());
            row.createCell(6).setCellValue(data.getTehsil());
            row.createCell(7).setCellValue(data.getStation());
            row.createCell(8).setCellValue(data.getPostOffice());
            row.createCell(9).setCellValue(data.getCityVillage());
            row.createCell(10).setCellValue(data.getFromWhichPlace());
            row.createCell(11).setCellValue(data.getCaste());
            row.createCell(12).setCellValue(data.getSubcaste());
            row.createCell(13).setCellValue(data.getIndividualID());
            row.createCell(14).setCellValue(data.getGivenName());
            row.createCell(15).setCellValue(data.getSurname());
            row.createCell(16).setCellValue(data.getRelation());
            row.createCell(17).setCellValue(data.getGender());
            row.createCell(18).setCellValue(data.getFamilyID());
            row.createCell(19).setCellValue(data.getRitualName());
            row.createCell(20).setCellValue(data.getWhoseRitual1());
            row.createCell(21).setCellValue(data.getWhoseRitual2());
            row.createCell(22).setCellValue(data.getContactNo1());
            row.createCell(23).setCellValue(data.getContactNo2());
            row.createCell(24).setCellValue(data.getFlagsAndException());
            row.createCell(25).setCellValue(data.getAdditionalInforma());
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();
        return bos.toByteArray();
    }
}
