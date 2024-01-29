package com.MobileFolk.DataProvider;

import com.MobileFolk.Model.StudentModel;
import com.MobileFolk.helpers.ExcelHelpers;
import org.testng.annotations.DataProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StudentDataProvider {
    final ExcelHelpers xlsReader = new ExcelHelpers();

    @DataProvider(name = "studentData")
    public Object[][] studentData() {
        String path = System.getProperty("user.dir");
        String file = "/src/test/resources/data-test/khanh/Guest.xlsx";

        String filePath = path + file;

        var data = xlsReader.readXLSData(filePath);
        List<StudentModel> studentModels = new ArrayList<>();
        data.entrySet().stream().filter(v -> !v.getKey().equals("0")).forEach(v -> {
            var rowData = data.get(v.getKey());
            List<String> sub = Arrays.asList(rowData.get(6).split(","));
            List<String> hob = Arrays.asList(rowData.get(7).split(","));
            var student = StudentModel.builder().firstName(rowData.get(1)).lastName(rowData.get(2)).email(rowData.get(3)).gender(rowData.get(4))
                    .mobile(rowData.get(5)).subject(sub).hobbies(hob).picture(rowData.get(8)).currentAddress(rowData.get(9)).build();
            studentModels.add(student);
        });
        System.out.println("Result");
        Object[][] result = new Object[studentModels.size()][1];
        for (int i = 0; i < studentModels.size(); i++) {
            result[i][0] = studentModels.get(i);
        }
        return result;
    }
}
