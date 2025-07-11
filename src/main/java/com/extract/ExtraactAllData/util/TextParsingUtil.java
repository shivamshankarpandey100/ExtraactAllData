
package com.extract.ExtraactAllData.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class TextParsingUtil {

    // --- Patterns ---
    // Date Pattern (flexible for various formats including Devanagari numerals)
    private static final Pattern DATE_PATTERN = Pattern.compile("(\\d{1,2}[-./]\\d{1,2}[-./]\\d{2,4}|[०-९]{1,2}[-./][०-९]{1,2}[-./][०-९]{2,4})");

    // Location Patterns (more specific to capture the value after the keyword)
    private static final Pattern JILA_PATTERN = Pattern.compile("(?:जिला|जिल्ला|Jila)\\s*([^,\\s।]+)");
    private static final Pattern TAHSIL_PATTERN = Pattern.compile("(?:तहसील|तालुका|Tahsil)\\s*([^,\\s।]+)");
    private static final Pattern STATION_PATTERN = Pattern.compile("(?:थाना|स्टेशन|पुलिस\\s*स्टेशन|Station)\\s*([^,\\s।]+)");
    private static final Pattern POST_OFFICE_PATTERN = Pattern.compile("(?:डाकघर|पोस्ट\\s*ऑफिस|Post\\s*Office)\\s*([^,\\s।]+)");
    private static final Pattern CITY_VILLAGE_PATTERN = Pattern.compile("(?:गांव|गाँव|ग्राम|शहर|नगर|कस्बा|महानगर|City|Village)\\s*([^,\\s।]+)");
    private static final Pattern FROM_WHICH_PLACE_PATTERN = Pattern.compile("(?:कहाँ\\s*से\\s*आये|From\\s*Where|से\\s*आये)[:：\\s]*([\\p{IsDevanagari}\\w\\s]{2,})");

    // Name and Relation Patterns
    // General person name pattern (captures Hindi/Devanagari words)
    private static final Pattern PERSON_NAME_RAW_PATTERN = Pattern.compile("((?:स्व[०o]?|श्री(?:मती)?|कुँवर|चौधरी|बाबू|पंडित|कुमार|श्रीमती|सुपुत्र|कैप्टन|डॉ)?\\s*[\\p{IsDevanagari}]{2,}(?:\\s+[\\p{IsDevanagari}]{2,}){0,3}(?:\\s*सिंह|कुमार|देवी|प्रसाद|लाल|शर्मा|वर्मा|गुप्ता|यादव|ठाकुर|राय|चौधरी|मिश्र|त्रिपाठी|अग्रवाल|कश्यप|पाठक|तिवारी|शुक्ल)?(?:\\s*चौ)?)");

    // Primary/Prarthi (Applicant/Main Person) pattern - looking for 'प्रा.', 'प्रार्थी', etc.
    private static final Pattern PRA_NAME_PATTERN = Pattern.compile("(?:प्रा[०0।]?|प्रार्थी|संबोधित|मुख्य\\s*व्यक्ति)\\s*([\\p{IsDevanagari}\\s]+(?:\\s*चौ)?)");

    // Family relation patterns, trying to capture the name directly after the relation keyword
    private static final Pattern FATHER_RELATION_PATTERN = Pattern.compile("(?:पिता(?:\\s*जी)?|पिताश्री|पापा|श्रीमान|श्रद्धेय|सुपुत्र|पुत्र)\\s+((?:स्व[०o]?|श्री)?\\s*[\\p{IsDevanagari}\\s]+(?:\\s*चौ)?)");
    private static final Pattern MOTHER_RELATION_PATTERN = Pattern.compile("(?:माता|माँ)\\s+((?:श्रीमती|स्व[०o]?|श्री)?\\s*[\\p{IsDevanagari}\\s]+(?:\\s*देवी)?)");
    private static final Pattern BROTHER_RELATION_PATTERN = Pattern.compile("(?:भाई|भ्राता)\\s+((?:स्व[०o]?|श्री)?\\s*[\\p{IsDevanagari}\\s]+(?:\\s*चौ)?)");
    private static final Pattern SON_RELATION_PATTERN = Pattern.compile("(?:लड़का|पुत्र|बेटा)\\s+((?:स्व[०o]?|श्री)?\\s*[\\p{IsDevanagari}\\s]+(?:\\s*चौ)?)");
    private static final Pattern GRANDFATHER_RELATION_PATTERN = Pattern.compile("(?:दादा|परदादा|बाबा|अज्या|पिता\\s*के\\s*पिता)\\s+((?:स्व[०o]?|श्री)?\\s*[\\p{IsDevanagari}\\s]+(?:\\s*चौ)?)");


    // Caste/Subcaste
    private static final Pattern CASTE_PATTERN = Pattern.compile("(कुर्मी|ब्राह्मण|यादव|राजपूत|भूमिहार|चमार|दलित|ठाकुर|सुनार|कुम्हार|नाई|महतो|जाट|बनिया|मुस्लिम|सिख|ईसाई|जैन|बौद्ध)");
    private static final Pattern SUB_CASTE_PATTERN = Pattern.compile("(पाठक|तिवारी|शुक्ल|सिंह|गुप्ता|राठौर|मिश्र|दिवेदी|त्रिवेदी|लोधी|अहीर|कोरी|प्रजापति|वैश्य|कुशवाहा|सिसोदिया|चौहान|शर्मा|वर्मा|अग्रवाल)");

    // Ritual and Whose Ritual
    private static final Pattern RITUAL_NAME_PATTERN = Pattern.compile("(अस्ती\\s*लाय[ेें]?|अस्तीलाये|पिंड\\s*(?:दान|दिया)|श्राद्ध|दाह\\s*(?:संस्कार|श्राद्ध)|हवन|होम|यज्ञ|पूजन|वेद\\s*पाठ|ब्राह्म(?:ण|मन)\\s*भोज|कर्मकाण्ड)");
    // This pattern for whose ritual needs to be very robust, capturing names potentially prefixed with 'स्व०'
    private static final Pattern WHOSE_RITUAL_PERSON_PATTERN = Pattern.compile("(?:अस्ती\\s*लाय[ेें]?|अस्थि\\s*विसर्जन|पिंड\\s*दान|श्राद्ध|दाह\\s*संस्कार|अंत्येष्टि|हवन|पूजन|यज्ञ|कर्म)\\s*का\\s*((?:स्व[०o]?|श्री(?:मती)?|बाबू)?\\s*[\\p{IsDevanagari}\\s]+(?:\\s*चौ)?)");
    // Fallback/Secondary pattern for ritual person if the above is too strict
    private static final Pattern WHOSE_RITUAL_PERSON_FALLBACK_PATTERN = Pattern.compile("(?:स्व[०o]?|श्री(?:मती)?|बाबू)?\\s*[\\p{IsDevanagari}]{2,}(?:\\s+[\\p{IsDevanagari}]{2,}){0,3}(?:\\s*सिंह|चौ)?");


    // Contact Numbers
    private static final Pattern CONTACT_NO_PATTERN = Pattern.compile("(?:मोबाइल|फोन|Contact|मो\\.|मोब)[:：\\s]*([6-9]\\d{9})");


    // --- Extraction Methods ---

    public String extractImageNo(String text) {
        Matcher matcher = Pattern.compile("(?:Image\\s*No\\.?|चित्र\\s*संख्या)[:：\\s]*(\\d+)").matcher(text);
        return matcher.find() ? matcher.group(1).trim() : null; // Changed group to 1 to match pattern
    }

    public String extractPandaName(String text) {
        Matcher matcher = Pattern.compile("(?:पंडा\\s*का\\s*नाम|Panda\\s*Name|पंडा)[:：\\s]*([\\p{IsDevanagari}\\w\\s.]{2,})").matcher(text);
        return matcher.find() ? matcher.group(1).trim() : null;
    }

    public String extractBahiName(String text) {
        // "ब्राह्मण वाही" from your example is not preceded by a keyword like "बही का नाम", so direct match might be needed
        Matcher matcher = Pattern.compile("(?:बही\\s*का\\s*नाम|Bahi\\s*Name)[:：\\s]*([\\p{IsDevanagari}\\w\\s.]{2,})").matcher(text);
        if (matcher.find()) return matcher.group(1).trim();

        // Specific case for "ब्राह्मण वाही" or similar standalone bahi names at the beginning
        matcher = Pattern.compile("^(ब्राह्मण\\s*वाही|भूमिहार\\s*वाली\\s*खनक)").matcher(text);
        if (matcher.find()) return matcher.group(1).trim();

        return null;
    }

    public String extractFolioNo(String text) {
        Matcher matcher = Pattern.compile("(?:फोलियो\\s*संख्या|Folio\\s*No)[:ः\\s]*(\\d+)").matcher(text);
        return matcher.find() ? matcher.group(1).trim() : null;
    }

    public String extractFromWhichPlace(String text) {
        return extractFirstMatch(FROM_WHICH_PLACE_PATTERN, text);
    }

    public String extractJila(String text) {
        return extractFirstMatch(JILA_PATTERN, text);
    }

    public String extractTahsil(String text) {
        return extractFirstMatch(TAHSIL_PATTERN, text);
    }

    public String extractStation(String text) {
        return extractFirstMatch(STATION_PATTERN, text);
    }

    public String extractPostOffice(String text) {
        return extractFirstMatch(POST_OFFICE_PATTERN, text);
    }

    public String extractCityVillage(String text) {
        return extractFirstMatch(CITY_VILLAGE_PATTERN, text);
    }

    // --- Specific Relation Name Extractors ---
    public String extractPraMainPersonName(String text) {
        return extractFirstMatch(PRA_NAME_PATTERN, text);
    }

    public String extractFatherName(String text) {
        return extractFirstMatch(FATHER_RELATION_PATTERN, text);
    }

    public String extractMotherName(String text) {
        return extractFirstMatch(MOTHER_RELATION_PATTERN, text);
    }

    public String extractBrotherName(String text) {
        return extractFirstMatch(BROTHER_RELATION_PATTERN, text);
    }

    public String extractSonName(String text) {
        return extractFirstMatch(SON_RELATION_PATTERN, text);
    }

    public String extractDadajiName(String text) {
        return extractFirstMatch(GRANDFATHER_RELATION_PATTERN, text);
    }
    // --- End Specific Relation Name Extractors ---


    public String extractCaste(String text) {
        return extractFirstMatch(CASTE_PATTERN, text);
    }

    public String extractSubCaste(String text) {
        return extractFirstMatch(SUB_CASTE_PATTERN, text);
    }

    public List<String> extractAllNames(String text) {
        List<String> names = new ArrayList<>();
        // First, try to get names associated with relations or as main prarthi
        String prarthiName = extractPraMainPersonName(text);
        if (prarthiName != null) names.add(prarthiName);

        String fatherName = extractFatherName(text);
        if (fatherName != null) names.add(fatherName);

        String motherName = extractMotherName(text);
        if (motherName != null) names.add(motherName);

        String brotherName = extractBrotherName(text);
        if (brotherName != null) names.add(brotherName);

        String sonName = extractSonName(text);
        if (sonName != null) names.add(sonName);

        String grandfatherName = extractDadajiName(text);
        if (grandfatherName != null) names.add(grandfatherName);

        // Additionally, find general names that might not have a direct relation keyword next to them
        // This is a more liberal pattern, so filter carefully
        Matcher matcher = PERSON_NAME_RAW_PATTERN.matcher(text);
        while (matcher.find()) {
            String name = cleanName(matcher.group(1).trim());
            // Avoid names that are already captured by relation-specific patterns
            // and avoid single common words that are not names
            if (!name.isEmpty() && !isCommonWord(name) && !names.contains(name) && name.split("\\s+").length > 1) {
                names.add(name);
            }
        }

        // Remove duplicates and sort by length (longer, potentially more complete names first)
        return names.stream()
                .distinct()
                .sorted((n1, n2) -> Integer.compare(n2.length(), n1.length()))
                .collect(Collectors.toList());
    }

    // Helper to clean extracted names (remove 'स्व०', 'श्री', etc.)
    private String cleanName(String name) {
        if (name == null) return null;
        return name.replaceAll("स्व[०o]?\\s*|श्री(?:मती)?\\s*|बाबू\\s*|पंडित\\s*|कुमार\\s*|कुँवर\\s*", "").trim();
    }


    private boolean isCommonWord(String word) {
        // Expanded list of common words, prepositions, conjunctions, and terms that are unlikely to be names
        String[] commonWords = {"पिता", "माता", "बेटा", "बेटी", "पुत्र", "पुत्री", "श्राद्ध", "दाह", "गांव", "जिला", "अनुष्ठान",
                "कर्म", "पंडा", "बही", "फोलियो", "अस्ती", "लाये", "पिंड", "दिया", "हवन", "होम", "यज्ञ",
                "पूजन", "वेद", "पाठ", "ब्राह्मण", "मन", "भोज", "थाना", "तहसील", "स्टेशन", "डाकघर",
                "पोस्ट", "ऑफिस", "कहाँ", "से", "आये", "स्थान", "नाम", "संख्या", "संबोधित", "मुख्य", "व्यक्ति",
                "के", "व", "अरुण", "वर", "बिन्दु", "अशोक", "दिनेश", "अस्ती", "लाये", "माँ", "श्रीमती", "दिया", "ता", "आभूमिहार", "वाली", "खनक", "दि", "०", "का", "में", "और", "या", "की", "से", "पर", "द्वारा", "तक", "लिए", "ने", "को", "भी", "ही", "है", "था", "थे", "थी", "हो", "हुआ", "हुए", "हुई", "जा", "कर", "करके", "करते", "वाला", "वाली", "वाले", "आदि", "खाली", "पायखाजिस्य", "बा०", "प्रा", "चौ", "स्व०", "अविनाश", "सिंह", "चौ", "केपीता", "जितेन्द्र", "नाथ", "के", "भाई", "नरेश", "के", "लड़का", "रजिशसिंह", "अस्ती", "लाये", "माँ", "श्रीमती", "सुन्दर", "दिया", "ता", "आभूमिहार", "वाली", "खनक", "दिनांक", "दि०"}; // Added words from your example and common Hindi words
        String cleanedWord = word.toLowerCase().replaceAll("[०-९]", "").trim(); // Remove numbers and trim
        for (String common : commonWords) {
            if (cleanedWord.equals(common.toLowerCase())) return true;
        }
        return false;
    }


    // --- Relation Extraction based on keywords near the name ---
    public String extractRelation(String text, String name) {
        if (StringUtils.isBlank(name)) return null;
        String cleanedName = cleanName(name);

        // Specific relations with keywords preceding the name
        if (Pattern.compile("(?:पिता(?:\\s*जी)?|पिताश्री|पापा|सुपुत्र|पुत्र)\\s+" + Pattern.quote(name)).matcher(text).find()) return "पिता";
        if (Pattern.compile("(?:माता|माँ)\\s+" + Pattern.quote(name)).matcher(text).find()) return "माता";
        if (Pattern.compile("(?:भाई|भ्राता)\\s+" + Pattern.quote(name)).matcher(text).find()) return "भाई";
        if (Pattern.compile("(?:लड़का|पुत्र|बेटा)\\s+" + Pattern.quote(name)).matcher(text).find()) return "पुत्र";
        if (Pattern.compile("(?:पुत्री|बेटी|लड़की)\\s+" + Pattern.quote(name)).matcher(text).find()) return "पुत्री";
        if (Pattern.compile("(?:पति|स्वामी)\\s+" + Pattern.quote(name)).matcher(text).find()) return "पति";
        if (Pattern.compile("(?:पत्नी|धर्मपत्नी|बहू)\\s+" + Pattern.quote(name)).matcher(text).find()) return "पत्नी";
        if (Pattern.compile("(?:दादा|परदादा|बाबा)\\s+" + Pattern.quote(name)).matcher(text).find()) return "दादा";
        if (Pattern.compile("(?:चाचा|काका)\\s+" + Pattern.quote(name)).matcher(text).find()) return "चाचा";

        // More complex relations like "X के पिता Y", "X के भाई Y", "X के लड़का Y"
        // This requires careful parsing and might be better handled by iterating through identified names
        // For now, let's look for "के पिता NAME", "के भाई NAME", "के लड़का NAME"
        if (Pattern.compile("के\\s*(?:पिता|पीता|पिटा|पा)\\s+" + Pattern.quote(name)).matcher(text).find()) return "पिता (के)"; // 'के' indicates relation to someone else
        if (Pattern.compile("के\\s*भाई\\s+" + Pattern.quote(name)).matcher(text).find()) return "भाई (के)";
        if (Pattern.compile("के\\s*लड़का\\s+" + Pattern.quote(name)).matcher(text).find()) return "पुत्र (के)";

        // Special case for "प्रा." (Prarthi/Primary)
        if (Pattern.compile("(?:प्रा[०0।]?|प्रार्थी|संबोधित)\\s*" + Pattern.quote(name)).matcher(text).find()) return "प्रार्थी";
        if (Pattern.compile("श्रीमती\\s*" + Pattern.quote(name)).matcher(text).find()) return "पत्नी/महिला"; // General female indicator

        // Check if the name itself implies a relation (e.g., if 'पिता' or 'माँ' is part of the extracted 'name' if regex was too broad)
        if (name.contains("पिता")) return "पिता";
        if (name.contains("माता") || name.contains("माँ")) return "माता";

        return "अन्य"; // Default if no specific relation is found
    }

    public String extractGivenName(String fullName) {
        if (StringUtils.isBlank(fullName)) return null;
        String cleaned = cleanName(fullName);
        String[] parts = cleaned.trim().split("\\s+");
        return parts.length > 0 ? parts[0] : null;
    }

//    public String extractSurname(String fullName) {
//        if (StringUtils.isBlank(fullName)) return null;
//        String cleaned = cleanName(fullName);
//        String[] parts = cleaned.trim().split("\\s+");
//        return parts.length > 1 ? parts[parts.length - 1] : null;
//    }
//public String extractSurname(String fullName) {
//    if (StringUtils.isBlank(fullName)) return null;
//    String cleaned = cleanName(fullName); // Make sure cleanName is robust
//    String[] parts = cleaned.trim().split("\\s+");
//    // This returns the *last* word. For "रजिशसिंह चौ०", it will return "चौ".
//    // For "जितेन्द्र नाथ सिंह", it will return "सिंह".
//    // For "नरेश", it will return null because parts.length is 1.
//    return parts.length > 1 ? parts[parts.length - 1] : null;
//}
private static final Set<String> COMMON_INDIAN_SURNAMES = new HashSet<>(Arrays.asList(
        "सिंह", "चौधरी", "कुमार", "देवी", "प्रसाद", "लाल", "वर्मा", "शर्मा", "गुप्ता", "यादव",
        "अग्रवाल", "मिश्र", "त्रिपाठी", "कश्यप", "पाठक", "तिवारी", "शुक्ल", "चौ", "राय", "ठाकुर",
        "दुबे", "पांडेय", "व्यास", "जैन", "खन्ना", "मेहता", "झा", "पटेल", "खान", "अहमद", "अली",
        "कौर", "कुमारी"
));
public String extractSurname(String fullName) {
    if (StringUtils.isBlank(fullName)) {
        return null;
    }

    String cleaned = cleanName(fullName);
    String[] parts = cleaned.trim().split("\\s+");

    if (parts.length == 0) {
        return null;
    }

    if (parts.length > 1) {
        return parts[parts.length - 1];
    }

    if (COMMON_INDIAN_SURNAMES.contains(parts[0])) {
        return parts[0];
    }

    return null;
}

    public String extractGender(String text) {
        if (text.contains("लड़का") || text.contains("बेटा") || text.contains("पुत्र")) return "पुरुष";
        if (text.contains("लड़की") || text.contains("बेटी") || text.contains("पुत्री")) return "महिला";

        // Also infer gender from common titles in names found
        Matcher maleMatcher = Pattern.compile("श्री|कुमार|सिंह|ठाकुर|बाबू").matcher(text);
        if (maleMatcher.find()) return "पुरुष";

        Matcher femaleMatcher = Pattern.compile("श्रीमती|देवी|कुमारी|बहिन|पत्नी|माँ").matcher(text);
        if (femaleMatcher.find()) return "महिला";

        return "अज्ञात";
    }


    public String extractanusthan_ka_naam(String text) {
        Matcher matcher = RITUAL_NAME_PATTERN.matcher(text);
        if (matcher.find()) return matcher.group(1).trim();
        return null;
    }

    public String extractKiskaAnusthan(String text) {
        Matcher matcher = WHOSE_RITUAL_PERSON_PATTERN.matcher(text);
        if (matcher.find()) {
            return cleanName(matcher.group(1).trim());
        }
        // Fallback for names not directly preceded by "का" after ritual
        matcher = Pattern.compile("(?:श्राद्ध|दाह\\s*संस्कार|अस्ती\\s*लाय[ेें]?)\\s*((?:स्व[०o]?|श्री(?:मती)?|बाबू)?\\s*[\\p{IsDevanagari}\\s]+(?:\\s*चौ)?)").matcher(text);
        if (matcher.find()) {
            return cleanName(matcher.group(1).trim());
        }
        return null;
    }

    public String extractKiskaAnusthan2(String text) {
        List<String> ritualPersons = new ArrayList<>();
        Matcher matcher = WHOSE_RITUAL_PERSON_PATTERN.matcher(text);
        while (matcher.find()) {
            ritualPersons.add(cleanName(matcher.group(1).trim()));
        }
        // Also consider the fallback pattern
        Matcher fallbackMatcher = Pattern.compile("(?:श्राद्ध|दाह\\s*संस्कार|अस्ती\\s*लाय[ेें]?)\\s*((?:स्व[०o]?|श्री(?:मती)?|बाबू)?\\s*[\\p{IsDevanagari}\\s]+(?:\\s*चौ)?)").matcher(text);
        while(fallbackMatcher.find()){
            String person = cleanName(fallbackMatcher.group(1).trim());
            if(!ritualPersons.contains(person)) { // Avoid duplicates
                ritualPersons.add(person);
            }
        }
        return ritualPersons.size() > 1 ? ritualPersons.get(1) : null;
    }

    public String extractContactNo1(String text) {
        Matcher matcher = CONTACT_NO_PATTERN.matcher(text);
        return matcher.find() ? matcher.group(1).trim() : null;
    }

    public String extractContactNo2(String text) {
        List<String> contacts = new ArrayList<>();
        Matcher matcher = CONTACT_NO_PATTERN.matcher(text);
        while (matcher.find()) {
            contacts.add(matcher.group(1).trim());
        }
        return contacts.size() > 1 ? contacts.get(1) : null;
    }

    public String extractUnknownTerms(String text) {
        // Remove known entities to find "flags and exceptions"
        String cleanedText = text;

        // Remove names (cleaned versions)
        List<String> allExtractedNames = extractAllNames(text);
        for(String name : allExtractedNames){
            cleanedText = cleanedText.replace(name, "");
            cleanedText = cleanedText.replaceAll("स्व[०o]?\\s*" + Pattern.quote(name), ""); // Remove with prefix
        }

        // Remove other extracted entities
        cleanedText = removePattern(cleanedText, JILA_PATTERN);
        cleanedText = removePattern(cleanedText, TAHSIL_PATTERN);
        cleanedText = removePattern(cleanedText, STATION_PATTERN);
        cleanedText = removePattern(cleanedText, POST_OFFICE_PATTERN);
        cleanedText = removePattern(cleanedText, CITY_VILLAGE_PATTERN);
        cleanedText = removePattern(cleanedText, FROM_WHICH_PLACE_PATTERN);
        cleanedText = removePattern(cleanedText, CASTE_PATTERN);
        cleanedText = removePattern(cleanedText, SUB_CASTE_PATTERN);
        cleanedText = removePattern(cleanedText, RITUAL_NAME_PATTERN);
        cleanedText = removePattern(cleanedText, WHOSE_RITUAL_PERSON_PATTERN);
        cleanedText = removePattern(cleanedText, WHOSE_RITUAL_PERSON_FALLBACK_PATTERN);
        cleanedText = removePattern(cleanedText, CONTACT_NO_PATTERN);
        cleanedText = removePattern(cleanedText, DATE_PATTERN);
        cleanedText = removePattern(cleanedText, Pattern.compile("(?:Image\\s*No\\.?|चित्र\\s*संख्या)[:：\\s]*(\\d+)"));
        cleanedText = removePattern(cleanedText, Pattern.compile("(?:पंडा\\s*का\\s*नाम|Panda\\s*Name)[:：\\s]*([\\p{IsDevanagari}\\w\\s.]{2,})"));
        cleanedText = removePattern(cleanedText, Pattern.compile("(?:बही\\s*का\\s*नाम|Bahi\\s*Name)[:：\\s]*([\\p{IsDevanagari}\\w\\s.]{2,})"));
        cleanedText = removePattern(cleanedText, Pattern.compile("^(ब्राह्मण\\s*वाही|भूमिहार\\s*वाली\\s*खनक)")); // For specific bahi names
        cleanedText = removePattern(cleanedText, Pattern.compile("(?:फोलियो\\s*संख्या|Folio\\s*No)[:：\\s]*(\\d+)"));
        cleanedText = removePattern(cleanedText, Pattern.compile("(?:प्रा[०0।]?|प्रार्थी|संबोधित|मुख्य\\s*व्यक्ति)\\s*([\\p{IsDevanagari}\\s]+(?:\\s*चौ)?)"));
        cleanedText = removePattern(cleanedText, Pattern.compile("(?:पिता(?:\\s*जी)?|पिताश्री|पापा|श्रीमान|श्रद्धेय|सुपुत्र|पुत्र)\\s+((?:स्व[०o]?|श्री)?\\s*[\\p{IsDevanagari}\\s]+(?:\\s*चौ)?)"));
        cleanedText = removePattern(cleanedText, Pattern.compile("(?:माता|माँ)\\s+((?:श्रीमती|स्व[०o]?|श्री)?\\s*[\\p{IsDevanagari}\\s]+(?:\\s*देवी)?)"));
        cleanedText = removePattern(cleanedText, Pattern.compile("(?:चाचा|काका)\\s+((?:स्व[०o]?|श्री)?\\s*[\\p{IsDevanagari}\\s]+(?:\\s*चौ)?)"));
        cleanedText = removePattern(cleanedText, Pattern.compile("(?:दादा|परदादा|बाबा|अज्या|पिता\\s*के\\s*पिता)\\s+((?:स्व[०o]?|श्री)?\\s*[\\p{IsDevanagari}\\s]+(?:\\s*चौ)?)"));
        cleanedText = removePattern(cleanedText, Pattern.compile("के\\s*(?:पिता|पीता|पिटा|पा)")); // 'के पिता' keywords themselves
        cleanedText = removePattern(cleanedText, Pattern.compile("के\\s*भाई"));
        cleanedText = removePattern(cleanedText, Pattern.compile("के\\s*लड़का"));
        cleanedText = removePattern(cleanedText, Pattern.compile("प्रा\\.")); // Just "प्रा."
        cleanedText = removePattern(cleanedText, Pattern.compile("बा०")); // "बा०"

        // Remove common prepositions, conjunctions, and general filler words
        String[] fillers = {"के", "व", "अरुण", "वर", "बिन्दु", "अशोक", "दिनेश", "अस्ती", "लाये", "माँ", "श्रीमती", "दिया", "ता", "आभूमिहार", "वाली", "खनक", "दि", "०", "का", "में", "और", "या", "की", "से", "पर", "द्वारा", "तक", "लिए", "ने", "को", "भी", "ही", "है", "था", "थे", "थी", "हो", "हुआ", "हुए", "हुई", "जा", "कर", "करके", "करते", "वाला", "वाली", "वाले", "आदि"};
        for(String filler : fillers){
            cleanedText = cleanedText.replaceAll("\\b" + Pattern.quote(filler) + "\\b", ""); // Use word boundary
        }

        return cleanText(cleanedText);
    }

    private String removePattern(String text, Pattern pattern) {
        return pattern.matcher(text).replaceAll(" ").trim();
    }


    public String extractExtraNotesOrDate(String text) {
        List<String> dates = extractDates(text);
        if (!dates.isEmpty()) {
            String lastDate = dates.get(dates.size() - 1);
            // Remove the date from the text before returning, to isolate other notes
            return lastDate; // For now, just return the date as per previous output example.
            // If other notes are needed, then you'd return the modified text.
        }
        return null;
    }

    public List<String> extractDates(String text) {
        List<String> dates = new ArrayList<>();
        Matcher matcher = DATE_PATTERN.matcher(text);
        while (matcher.find()) {
            dates.add(convertDevanagariNumbers(matcher.group()));
        }
        return dates;
    }

    private String convertDevanagariNumbers(String dateStr) {
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

    public LocalDate parseDate(String dateStr) {
        if (StringUtils.isBlank(dateStr)) return null;
        try {
            // Normalize separators and handle potential 'ta.' prefix
            String clean = dateStr.replaceAll("[^0-9./-]", "").replace("/", "-").replace(".", "-");

            // Try "d-M-yyyy" first
            if (clean.matches("\\d{1,2}-\\d{1,2}-\\d{4}")) {
                return LocalDate.parse(clean, DateTimeFormatter.ofPattern("d-M-yyyy"));
            }
            // Try "d-M-yy" (and assume 20xx for yy < current year)
            else if (clean.matches("\\d{1,2}-\\d{1,2}-\\d{2}")) {
                int year = Integer.parseInt(clean.substring(clean.lastIndexOf("-") + 1));
                int currentYearLastTwoDigits = LocalDate.now().getYear() % 100;
                year += (year <= currentYearLastTwoDigits) ? 2000 : 1900; // Heuristic for century
                return LocalDate.parse(clean.substring(0, clean.lastIndexOf("-") + 1) + year, DateTimeFormatter.ofPattern("d-M-yyyy"));
            }
        } catch (Exception e) {
            System.err.println("Date parse error for '" + dateStr + "': " + e.getMessage());
        }
        return null;
    }

    public List<String> splitIntoParagraphsByTrigger(String fullText) {
        List<String> paragraphs = new ArrayList<>();
        String[] lines = fullText.split("(?<=\\n)"); // Split but keep newline to process per line

        StringBuilder currentParagraph = new StringBuilder();
        boolean firstParagraphFound = false;

        for (String line : lines) {
            String trimmedLine = line.trim();
            if (trimmedLine.isEmpty()) {
                if (currentParagraph.length() > 0 && firstParagraphFound) {
                    paragraphs.add(currentParagraph.toString().trim());
                    currentParagraph = new StringBuilder();
                }
                continue;
            }

            // A new record usually starts with a name or date related pattern
            // Using a specific trigger like "बा०" (Babu/Baal/Vahi - often signifies a new entry in registers)
            // or a date, or "प्रा." (Prarthi)
            boolean isNewRecordTrigger = Pattern.compile("^(?:बा०|प्रा[०0।]?|ता\\.|दि\\.|दिनांक)", Pattern.CASE_INSENSITIVE).matcher(trimmedLine).find() ||
                    DATE_PATTERN.matcher(trimmedLine).find() ||
                    PRA_NAME_PATTERN.matcher(trimmedLine).find();

            if (isNewRecordTrigger && currentParagraph.length() > 0) {
                paragraphs.add(currentParagraph.toString().trim());
                currentParagraph = new StringBuilder();
            }

            currentParagraph.append(trimmedLine).append(" "); // Append line and a space for later cleaning
            firstParagraphFound = true;
        }

        // Add the last paragraph if it exists
        if (currentParagraph.length() > 0) {
            paragraphs.add(currentParagraph.toString().trim());
        }
        return paragraphs;
    }

    private String extractFirstMatch(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1).trim() : null;
    }

    public String cleanText(String text) {
        if (text == null) return null;
        // Replace multiple spaces with single, remove leading/trailing spaces
        String cleaned = text.replaceAll("\\s+", " ").trim();
        // Remove common punctuation marks at the end if they are not part of names (e.g., '।')
        return cleaned.replaceAll("[\\.,;।]$", "");
    }

    public String extractDateOfRitual(String text) {
        Pattern datePattern = Pattern.compile(
                "(?:ता|त|ता०|त०|ता\\.|ता\\:|त\\.|त\\:)?\\s*" +
                        "([०१२३४५६७८९\\d]{1,2})[./\\-\\s]" +
                        "([०१२३४५६७८९\\d]{1,2})[./\\-\\s]" +
                        "([०१२३४५६७८९\\d]{2,4})"
        );

        Matcher matcher = datePattern.matcher(text);
        while (matcher.find()) {
            String rawDay = matcher.group(1);
            String rawMonth = matcher.group(2);
            String rawYear = matcher.group(3);

            String dayStr = convertHindiToEnglishDigits(rawDay);
            String monthStr = convertHindiToEnglishDigits(rawMonth);
            String yearStr = convertHindiToEnglishDigits(rawYear);

            try {
                int day = Integer.parseInt(dayStr);
                int month = Integer.parseInt(monthStr);
                int year = Integer.parseInt(yearStr.length() == 2
                        ? (Integer.parseInt(yearStr) < 50 ? "20" + yearStr : "19" + yearStr)
                        : yearStr);

                // Validate range before creating LocalDate
                if (day >= 1 && day <= 31 && month >= 1 && month <= 12) {
                    LocalDate date = LocalDate.of(year, month, day);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("hi", "IN"));
                    return date.format(formatter);
                }
            } catch (Exception e) {
                // Ignore this match and continue checking next
                System.err.println("Skipping invalid date: " + rawDay + "." + rawMonth + "." + rawYear);
            }
        }
        return null;
    }


    private String convertHindiToEnglishDigits(String input) {
        return input.replace('०', '0')
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
