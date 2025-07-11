
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

        // Split the input text into paragraphs representing family blocks or distinct records
        List<String> paragraphs = textParsingUtil.splitIntoParagraphsByTrigger(inputText);

        int dataIndex = 1;
        for (String record : paragraphs) {
            if (record.trim().isEmpty()) continue;

            // --- Extract Common/Paragraph-level Data First ---
            String imageNo = textParsingUtil.extractImageNo(record);
            String pandaName = textParsingUtil.extractPandaName(record);
            String bahiName = textParsingUtil.extractBahiName(record);
            String folioNo = textParsingUtil.extractFolioNo(record);
            String district = textParsingUtil.extractJila(record);
            String tehsil = textParsingUtil.extractTahsil(record);
            String station = textParsingUtil.extractStation(record);
            String postOffice = textParsingUtil.extractPostOffice(record);
            String cityVillage = textParsingUtil.extractCityVillage(record);
            String fromWhichPlace = textParsingUtil.extractFromWhichPlace(record);
            String caste = textParsingUtil.extractCaste(record);
            String subcaste = textParsingUtil.extractSubCaste(record);
            String ritualName = textParsingUtil.extractanusthan_ka_naam(record);
            String whoseRitual1 = textParsingUtil.extractKiskaAnusthan(record);
            String whoseRitual2 = textParsingUtil.extractKiskaAnusthan2(record);
            String contactNo1 = textParsingUtil.extractContactNo1(record);
            String contactNo2 = textParsingUtil.extractContactNo2(record);
            String flags = textParsingUtil.extractUnknownTerms(record);
            String dateOfRitual= textParsingUtil.extractDateOfRitual(record);

            // Extract the date from additional information
            String rawDateStr = textParsingUtil.extractExtraNotesOrDate(record);
            LocalDate extractedDate = (rawDateStr != null) ? textParsingUtil.parseDate(rawDateStr) : null;
            String additionalInfo = (extractedDate != null) ? rawDateStr : null; // Store original date string or other notes


            // --- Extract Individual-specific Data ---
            List<String> allNamesInRecord = textParsingUtil.extractAllNames(record);

            // Try to find the "prarthi" or main person explicitly
            String prarthiName = textParsingUtil.extractPraMainPersonName(record);

            if (!allNamesInRecord.isEmpty()) {
                int individualIDCounter = 1;

                // Prioritize the Prarthi if found
                if (prarthiName != null && allNamesInRecord.contains(prarthiName)) {
                    ExtractedData data = createNewExtractedDataInstance(
                            dataIndex++, imageNo, pandaName, bahiName, folioNo, district, tehsil,
                            station, postOffice, cityVillage, fromWhichPlace, caste, subcaste,
                            ritualName, whoseRitual1, whoseRitual2, contactNo1, contactNo2, flags, additionalInfo
                    );
                    data.setIndividualID(String.valueOf(individualIDCounter++));
                    data.setGivenName(textParsingUtil.extractGivenName(prarthiName));
                    data.setSurname(textParsingUtil.extractSurname(prarthiName));
                    data.setRelation(textParsingUtil.extractRelation(record, prarthiName));
                    data.setGender(textParsingUtil.extractGender(record)); // Gender for the whole record for now
                    extractedDataList.add(data);
                    allNamesInRecord.remove(prarthiName); // Remove to avoid re-processing
                }


                // Now iterate through remaining names (family members)
                // Filter out names that are already identified as ritual persons if they're handled
                List<String> remainingNames = new ArrayList<>(allNamesInRecord);
                if (whoseRitual1 != null) remainingNames.remove(whoseRitual1);
                if (whoseRitual2 != null) remainingNames.remove(whoseRitual2);


                for (String name : remainingNames) {
                    ExtractedData data = createNewExtractedDataInstance(
                            dataIndex++, imageNo, pandaName, bahiName, folioNo, district, tehsil,
                            station, postOffice, cityVillage, fromWhichPlace, caste, subcaste,
                            ritualName, whoseRitual1, whoseRitual2, contactNo1, contactNo2, flags, additionalInfo
                    );

                    data.setIndividualID(String.valueOf(individualIDCounter++));
                    data.setGivenName(textParsingUtil.extractGivenName(name));
                    data.setSurname(textParsingUtil.extractSurname(name));
                    data.setRelation(textParsingUtil.extractRelation(record, name));
                    data.setGender(textParsingUtil.extractGender(record)); // Gender for the whole record for now

                    extractedDataList.add(data);
                }

                // If ritual persons were not among other names, add them specifically
                if (whoseRitual1 != null && !allNamesInRecord.contains(whoseRitual1)) {
                    ExtractedData data = createNewExtractedDataInstance(
                            dataIndex++, imageNo, pandaName, bahiName, folioNo, district, tehsil,
                            station, postOffice, cityVillage, fromWhichPlace, caste, subcaste,
                            ritualName, whoseRitual1, whoseRitual2, contactNo1, contactNo2, flags, additionalInfo
                    );
                    data.setIndividualID(String.valueOf(individualIDCounter++));
                    data.setGivenName(textParsingUtil.extractGivenName(whoseRitual1));
                    data.setSurname(textParsingUtil.extractSurname(whoseRitual1));
                    data.setRelation("मृतक (अनुष्ठान)"); // Explicitly mark as deceased for ritual
                    data.setGender(textParsingUtil.extractGender(record));
                    extractedDataList.add(data);
                }
                if (whoseRitual2 != null && !allNamesInRecord.contains(whoseRitual2)) {
                    ExtractedData data = createNewExtractedDataInstance(
                            dataIndex++, imageNo, pandaName, bahiName, folioNo, district, tehsil,
                            station, postOffice, cityVillage, fromWhichPlace, caste, subcaste,
                            ritualName, whoseRitual1, whoseRitual2, contactNo1, contactNo2, flags, additionalInfo
                    );
                    data.setIndividualID(String.valueOf(individualIDCounter++));
                    data.setGivenName(textParsingUtil.extractGivenName(whoseRitual2));
                    data.setSurname(textParsingUtil.extractSurname(whoseRitual2));
                    data.setRelation("मृतक (अनुष्ठान)"); // Explicitly mark as deceased for ritual
                    data.setGender(textParsingUtil.extractGender(record));
                    extractedDataList.add(data);
                }


            } else {
                // If no individual names found, add one entry for the family block with common data
                ExtractedData data = createNewExtractedDataInstance(
                        dataIndex++, imageNo, pandaName, bahiName, folioNo, district, tehsil,
                        station, postOffice, cityVillage, fromWhichPlace, caste, subcaste,
                        ritualName, whoseRitual1, whoseRitual2, contactNo1, contactNo2, flags, additionalInfo
                );
                extractedDataList.add(data);
            }
        }

        return extractedDataList;
    }

    // Helper method to create a new ExtractedData instance and set common fields
    private ExtractedData createNewExtractedDataInstance(
            int dataPosition, String imageNo, String pandaName, String bahiName, String folioNo,
            String district, String tehsil, String station, String postOffice, String cityVillage,
            String fromWhichPlace, String caste, String subcaste, String ritualName,
            String whoseRitual1, String whoseRitual2, String contactNo1, String contactNo2,
            String flagsAndException, String additionalInforma) {

        ExtractedData data = new ExtractedData();
        data.setDataPosition(String.valueOf(dataPosition));
        data.setImageNo(imageNo);
        data.setPandaName(pandaName);
        data.setBahiName(bahiName);
        data.setFolioNo(folioNo);
        data.setDistrict(district);
        data.setTehsil(tehsil);
        data.setStation(station);
        data.setPostOffice(postOffice);
        data.setCityVillage(cityVillage);
        data.setFromWhichPlace(fromWhichPlace);
        data.setCaste(caste);
        data.setSubcaste(subcaste);
        data.setRitualName(ritualName);
        data.setWhoseRitual1(whoseRitual1);
        data.setWhoseRitual2(whoseRitual2);
        data.setContactNo1(contactNo1);
        data.setContactNo2(contactNo2);
        data.setFlagsAndException(flagsAndException);
        data.setAdditionalInforma(additionalInforma);
        // familyID is left blank as per instructions
        return data;
    }
}
