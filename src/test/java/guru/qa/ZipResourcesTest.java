package guru.qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.opencsv.CSVReader;
import guru.qa.datautils.Pet;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class ZipResourcesTest {
    private final ClassLoader CLASS_LOADER = Thread.currentThread().getContextClassLoader();
    private final String FILE_NAME = "examples.zip";

    @Tags({
            @Tag("Smoke"),
            @Tag("Major")
    })

    @DisplayName("В файле " + FILE_NAME + " должны содержаться файлы с именами: \"List of teachers.pdf\", \"List of teachers.xlsx\",\"students - Sheet1.csv")
    @Test
    public void checkNamesFilesFromZip() throws IOException, NullPointerException {
        List<String> entriesNamesExpected = new ArrayList<>(Arrays.asList("List of teachers.pdf", "List of teachers.xlsx", "students - Sheet1.csv"));
        try (ZipInputStream zipInputStream = new ZipInputStream(CLASS_LOADER.getResourceAsStream(FILE_NAME))) {
            ZipEntry zipEntry;
            int i = 0;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String actualResult = zipEntry.getName().substring(zipEntry.getName().lastIndexOf("/") + 1);
                Assertions.assertEquals(entriesNamesExpected.get(i), actualResult, "Актуальное значение не соответствует ожидаемому");
                i++;
            }
        }
    }

    @Tags({
            @Tag("Smoke"),
            @Tag("Major")
    })

    @DisplayName("Валидация файла \"List of teachers.pdf\" из zip архива")
    @Test
    public void checkPdfFileFromZip() throws IOException, NullPointerException {
        ZipFile zipFile = new ZipFile(new File("src/test/resources/examples.zip"));
        try (ZipInputStream zipInputStream = new ZipInputStream(CLASS_LOADER.getResourceAsStream(FILE_NAME))) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (zipEntry.getName().endsWith(".pdf"))
                    try (InputStream inputStreamPdf = zipFile.getInputStream(zipEntry)) {
                        PDF newPdf = new PDF(inputStreamPdf);
                        Assertions.assertEquals("CloudConvert", newPdf.creator, "Creator не валиден");
                        Assertions.assertEquals("CloudConvert", newPdf.producer, "Producer не валиден");
                        Assertions.assertEquals("CloudConvert", newPdf.creator, "Creator не валиден");
                        Assertions.assertEquals(14, newPdf.numberOfPages);
                    }
            }
        }
    }

    @Tags({
            @Tag("Smoke"),
            @Tag("Major")
    })

    @DisplayName("Валидация файла \"List of teachers.xlsx\" из zip архива")
    @Test
    public void checkXlsxFileFromZip() throws IOException, NullPointerException {
        ZipFile zipFile = new ZipFile(new File("src/test/resources/examples.zip"));
        try (ZipInputStream zipInputStream = new ZipInputStream(CLASS_LOADER.getResourceAsStream(FILE_NAME))) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (zipEntry.getName().endsWith(".xlsx"))
                    try (InputStream inputStreamXls = zipFile.getInputStream(zipEntry)) {
                        XLS xlsFile = new XLS(inputStreamXls);
                        Assertions.assertEquals("Ladies Teachers List for \"UTKARSHA TRAINING\"", xlsFile.excel.getSheet("secondary school").getRow(1).getCell(3).toString());
                        Assertions.assertEquals("Junior College Ladies Teachers List for \"UTKARSHA TRAINING\"", xlsFile.excel.getSheet("junior clg").getRow(2).getCell(3).toString());
                        Assertions.assertEquals("Secondary Schools having no Ladies Teachers", xlsFile.excel.getSheet("26").getRow(2).getCell(2).toString());
                    }
            }
        }
    }

    @Tags({
            @Tag("Smoke"),
            @Tag("Major")
    })

    @DisplayName("Валидация файла \"students - Sheet1.csv\" из zip архива")
    @Test
    public void checkCsvFileFromZip() throws IOException, NullPointerException {
        ZipFile zipFile = new ZipFile(new File("src/test/resources/examples.zip"));
        try (ZipInputStream zipInputStream = new ZipInputStream(CLASS_LOADER.getResourceAsStream(FILE_NAME))) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (zipEntry.getName().endsWith(".csv"))
                    try (InputStream inputStreamCsv = zipFile.getInputStream(zipEntry)) {
                        CSVReader reader = new CSVReader(new InputStreamReader(inputStreamCsv, StandardCharsets.UTF_8));
                        List<String[]> actual = reader.readAll().subList(0, 3);
                        System.out.println();
                        org.assertj.core.api.Assertions.assertThat(actual).contains(
                                new String[]{"id", "first_name", "last_name", "date_of_birth", "ethnicity", "gender", "status", "entry_academic_period", "exclusion_type", "act_composite", "act_math", "act_english", "act_reading", "sat_combined", "sat_math", "sat_verbal", "sat_reading", "hs_gpa", "hs_city", "hs_state", "hs_zip", "email", "entry_age", "ged", "english_2nd_language", "first_generation"},
                                new String[]{"111111", "John", "Doe", "01/2000", "Hispanic", "M", "FT", "Fall 2008", "", "", "", "", "", "", "", "", "", "2.71", "Albuquerque", "New Mexico", "87112", "jdoe@example.com", "17.9", "FALSE", "FALSE", "TRUE"},
                                new String[]{"111112", "Jane", "Smith", "05/2001", "Hispanic", "F", "TRANSFER", "Fall 2006", "", "", "", "", "", "", "", "", "", "3.73", "New York", "New York", "10009", "jsmith@example.com", "18.1", "FALSE", "FALSE", "TRUE"}
                        );
                    }
            }
        }
    }

    @Tags({
            @Tag("Smoke"),
            @Tag("Major")
    })

    @DisplayName("Валидация JSON Pet GSON")
    @Test
    public void checkPetJsonGson() throws IOException, NullPointerException {
        Gson gson = new Gson();
        try (InputStream inputStream = CLASS_LOADER.getResourceAsStream("pet.json");
             InputStreamReader reader = new InputStreamReader(inputStream)) {
            Pet pet = gson.fromJson(reader, Pet.class);
            Assertions.assertEquals(12345, pet.id, "id питомца не корректный");
            Assertions.assertEquals(12345, pet.category.id, "id категории не корректный");
            Assertions.assertEquals("cat", pet.category.name, "name категории не корректный");
            Assertions.assertEquals("Jack", pet.name, "name питомца не корректный");
            Assertions.assertEquals(2, pet.photoUrls.size(), "В массиве url отсутствуют некоторые эл-ты");
            Assertions.assertEquals("pet", pet.tags.get(0).name, "В массиве tags отсутствует эл-т с name \"pet\"");
            Assertions.assertEquals("HomePet", pet.tags.get(1).name, "В массиве tags отсутствует эл-т с name \"HomePet\"");
        }
    }

    @DisplayName("Валидация JSON Pet Jackson")
    @Test
    public void checkPetJsonJackson() throws IOException, NullPointerException {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream inputStream = CLASS_LOADER.getResourceAsStream("pet.json");
             InputStreamReader reader = new InputStreamReader(inputStream)) {
            Pet pet = mapper.readValue(reader, Pet.class);
            Assertions.assertEquals(12345, pet.id, "id питомца не корректный");
            Assertions.assertEquals(12345, pet.category.id, "id категории не корректный");
            Assertions.assertEquals("cat", pet.category.name, "name категории не корректный");
            Assertions.assertEquals("Jack", pet.name, "name питомца не корректный");
            Assertions.assertEquals(2, pet.photoUrls.size(), "В массиве url отсутствуют некоторые эл-ты");
            Assertions.assertEquals("pet", pet.tags.get(0).name, "В массиве tags отсутствует эл-т с name \"pet\"");
            Assertions.assertEquals("HomePet", pet.tags.get(1).name, "В массиве tags отсутствует эл-т с name \"HomePet\"");
        }
    }
}
