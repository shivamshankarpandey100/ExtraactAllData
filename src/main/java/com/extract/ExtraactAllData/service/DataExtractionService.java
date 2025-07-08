

package com.extract.ExtraactAllData.service;

import com.extract.ExtraactAllData.model.ExtractedData;
import com.extract.ExtraactAllData.util.TextParsingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DataExtractionService {

    @Autowired
    private TextParsingUtil textParsingUtil;

    public List<ExtractedData> extractDataFromText(String inputText) {
        List<ExtractedData> extractedDataList = new ArrayList<>();

        // Split the input text into paragraphs based on triggers
        List<String> paragraphs = textParsingUtil.splitIntoParagraphsByTrigger(inputText);

        int fileNo = 1;
        for (String record : paragraphs) {
            if (record.trim().isEmpty()) continue;

            ExtractedData
                    data = new ExtractedData();
            data.setFileNo(String.valueOf(fileNo++));
            data.setLocation(textParsingUtil.extractLocation(record));
            data.setMainPersonName(textParsingUtil.extractPraMainPersonName(record));
            data.setRecordDate(extractLastDate(record));

            ExtractedData.FamilyDetails familyDetails = new ExtractedData.FamilyDetails();
            familyDetails.setPitajiKaNaam(textParsingUtil.extractFatherName(record));
            familyDetails.setDadajiKaNaam(textParsingUtil.extractDadajiName(record));
            familyDetails.setFamilyMembers(new ArrayList<>());

            for (String name : textParsingUtil.extractAllNames(record)) {
                String relation = textParsingUtil.extractRelation(record, name);
//                String honorific = textParsingUtil.extractHonorific(name);
                if (relation != null) {
                    familyDetails.getFamilyMembers().add(new ExtractedData.FamilyMember(name, relation));
                }
            }

            data.setFamilyDetails(familyDetails);

            ExtractedData.LocationDetails locationDetails = new ExtractedData.LocationDetails();
            locationDetails.setJila(textParsingUtil.extractJila(record));
            locationDetails.setTahsil(textParsingUtil.extractTahsil(record));
            data.setLocationDetails(locationDetails);

            ExtractedData.PersonalDetails personalDetails = new ExtractedData.PersonalDetails();
            personalDetails.setJati(textParsingUtil.extractCaste(record));
            personalDetails.setUpjati(textParsingUtil.extractSubCaste(record));
            personalDetails.setLing(textParsingUtil.extractGender(record));
            data.setPersonalDetails(personalDetails);

//            data.setRitualDetails(new ExtractedData.RitualDetails()); // Placeholder for ritual details
            ExtractedData.RitualDetails ritualDetails = new ExtractedData.RitualDetails();
            ritualDetails.setAnusthan_ka_naam(textParsingUtil.extractanusthan_ka_naam(record));
            ritualDetails.setKiska_anusthan(textParsingUtil.extractKiskaAnusthan(record)); // If applicable
            data.setRitualDetails(ritualDetails); // Set the ritual details


            extractedDataList.add(data);
        }

        return extractedDataList;
    }

    private LocalDate extractLastDate(String text) {
        // Regex pattern to match Hindi, English, and Sanskrit date formats
        String datePattern = "(\\d{1,2}[.-/\\s]*\\d{1,2}[.-/\\s]*\\d{2,4}]|" + // English dates
                "[०-९]{1,2}[.-/\\s]*[०-९]{1,2}[.-/\\s]*[०-९]{2,4}]|" + // Hindi dates
                "(?:[एक|दो|तीन|चार|पांच|छह|सात|आठ|नौ|दस|ग्यारह|बारह]\\s*[-/\\s]*)?(?:[जनवरी|फरवरी|मार्च|अप्रैल|मई|जून|जुलाई|अगस्त|सितंबर|अक्टूबर|नवंबर|दिसंबर]\\s*[-/\\s]*)?[०-९]{1,2}\\s*[-/\\s]*[०-९]{2,4})"; // Sanskrit dates

        // Compile the regex pattern
        Pattern pattern = Pattern.compile(datePattern);
        Matcher matcher = pattern.matcher(text);

        LocalDate lastDate = null;

        // Find all matches
        while (matcher.find()) {
            String dateStr = matcher.group();
            // Convert Devanagari numbers to English if necessary
            String convertedDate = convertDevanagariNumbers(dateStr);
            // Parse the date
            lastDate = textParsingUtil.parseDate(convertedDate);
        }

        return lastDate; // Return the last found date
    }



    // Helper method to convert Devanagari numerals to English
//    private String convertDevanagariNumbers(String devanagariDate) {
//        return devanagariDate.chars()
//                .map(c -> {
//                    if (c >= 0x0966 && c <= 0x096F) { // Devanagari digits range
//                        return c - 0x0966 + '0'; // Convert to ASCII digit
//                    }
//                    return c; // Keep other characters (like separators)
//                })
//                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
//                .toString();
//    }
    private String convertDevanagariNumbers(String dateStr) {
        // Replace Devanagari digits with English digits
        return dateStr.replace('०', '0')
                .replace('१', '1')
                .replace('२', '2')
                .replace('३', '3')
                .replace('४', '4')
                .replace('५', '5')
                .replace('६', '6')
                .replace('७', '7')
                .replace('८', '8')
                .replace('९', '9');
    }


}

