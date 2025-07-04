
package com.extract.ExtraactAllData.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TextParsingUtil {

    private static final Pattern DATE_PATTERN = Pattern.compile("\\d{1,2}[-./]\\d{1,2}[-./]\\d{2,4}");
    private static final Pattern LOCATION_PATTERN = Pattern.compile(
            "(?:(?:[का|के|की]\\s+)?(?:वासी|निवासी|निवास स्थान|स्थानिक|स्थायी\\s+पता)\\s*)([^\\s,।]+)"
    );
    private static final Pattern PRA_NAME_PATTERN = Pattern.compile(
            "(?:प्रा[०|0|।.\\s]*|प्रार्थी\\s+|संबोधित\\s+)([^\\s,।]+(?:\\s+[^\\s,।]+){0,1})"
    );



//    private static final Pattern FATHER_NAME_PATTERN = Pattern.compile(
//            "(?:पिता(?:\\s*जी)?|पिताश्री|पापा|श्रीमान|श्रद्धेय|सुपुत्र|पुत्र)\\s+([^\\n,।]+)"
//    );
private static final Pattern FATHER_NAME_PATTERN = Pattern.compile(
        "(?:" +
                "पिता(?:\\s*जी)?|पिताश्री|पापा|श्रीमान|श्रद्धेय|सुपुत्र|पुत्र|" +
                "बेट[ाी]|बेटे|बेटी|बीटी|बेटा|बेट|बेटीजी|" +  // Beta/beti variations
                "पुत्री|पुत्र[ीि]|पुत्रीजी|" +  // Daughter variations
                "लड़का|लड़के|लड़की|लड़कीजी|" +  // Boy/girl variations
                "बेटवा|बेटवी|बटा|बटी|" +  // Colloquial/short forms
                "सुपुत्री|सुप्त्री|" +  // Noble daughter
                "बालक|बालिका|" +  // Child variations
                "सपूत|संतान|अवलंब" +  // Other references
                ")" +
                "[\\s.:]*([^\\s,।]+(?:\\s+[^\\s,।]+){0,2})"
);

    private static final Pattern MOTHER_NAME_PATTERN = Pattern.compile("माता\\s+([^\\n]+)");
    private static final Pattern CHACHA_NAME_PATTERN = Pattern.compile("चाचा\\s+([^\\n]+)");
//    private static final Pattern DADA_NAME_PATTERN = Pattern.compile("([^\\s]+)\\s+महतो");
//    private static final Pattern DADA_NAME_PATTERN = Pattern.compile(
//            "के\\s+पीता\\s+(स्व०[^\\s,।]+(?:\\s+[^\\s,।]+){1,3})(?:\\s+के)?"
//    );
//private static final Pattern DADA_NAME_PATTERN = Pattern.compile(
//        "के\\s+पीता\\s+(?:स्व[०0]\\s*)?([^\\s,।]+(?:\\s*[^\\s,।]+){0,3})(?:\\s+के)?"
//);
private static final Pattern DADA_NAME_PATTERN = Pattern.compile(
        "(?:" +
                // Main relationship terms with all possible misspellings
                "के\\s+" +
                "(?:" +
                // Grandfather terms (दादा) with misspellings
                "दादा|ददा|दाडा|दाद|दाड़ा|दाद्ह|दादह|दादाा|ददा़|" +

                // Grandson terms (पोता) with misspellings
                "पोता|पोत्ता|पोत|पोत्|पोटा|पोट|पोट्टा|पोतर|पोत्र|पोतार|पोतरा|पोतरि|पोतरी|" +

                // Granddaughter terms (पोती) with misspellings
                "पोती|पोत्ती|पोटी|पोटि|पोट्टी|पोतियाँ|पोतियां|" +

                // Father terms (पिता) with misspellings
                "पीता|पिता|पित|पित्|पित्ता|पीत|पिताह|पिताा|पिता़|" +

                // Grandmother terms (दादी) with misspellings
                "दादी|ददी|दाडी|दादि|दाड़ी|दाद्ही|दादही|दादीी|ददी़|" +

                // Other variations
                "पाता|पाटा|पात|पाट|पात्ता|पाटाह|पाती|पाटी|पाति" +
                ")" +
                ")" +
                "\\s+" +
                // Name pattern (with honorifics and common name misspellings)
                "(?:" +
                "(?:स्व[०0]\\s*)?" +  // Optional honorific
                "([\\p{L}०-९]+(?:\\s+[\\p{L}०-९]+){0,3})" +  // Name with Hindi chars and numbers
                "(?:\\s*[,\\-।.]*\\s*(?:के|की|का|को|कु)?)?" +  // Optional connectors with punctuation
                ")"
);





    private static final Pattern CASTE_PATTERN = Pattern.compile(
            "(?<!\\S)(" +
                    "कुरमी|कुर्मी|" +
                    "भूमिहार|" +
                    "नापित|नाई|" +
                    "कुम्हार|प्रजापति|" +
                    "महतो|महतो|" +
                    "सोनार|सुनार|स्वर्णकार|सोनी|" +
                    "ब्राह्मण|पंडित|" +
                    "ठाकुर|राजपूत|" +
                    "यादव|अहीर|" +
                    "कोयरी|कोरी|" +
                    "जाट|" +
                    "चमार|दलित|हरिजन|जScheduled Caste|अनुसूचित जाति|" +
                    "ओबीसी|अन्य पिछड़ा वर्ग|" +
                    "बनिया|वैश्य|" +
                    "मुस्लिम|इस्लाम|" +
                    "सिख|" +
                    "ईसाई|क्रिश्चियन|" +
                    "जैन|" +
                    "बौद्ध|" +
                    "SC|ST|OBC|" +
                    "मीणा|भील|गोंड|संथाल|मुंडा" +
                    ")(?!\\S)"
    );


private static final Pattern RITUAL_NAME_PATTERN = Pattern.compile(
        "(?:" +
                // Prefix patterns
                "(?:अनुष्ठान\\s*(?:का\\s*नाम)?|कर्मकांड|क्रिया|धार्मिक\\s+कर्म|कर्म)\\s*[:：.]?\\s*" +
                "|" +
                // Ritual names with common misspellings/alternate forms
                "(?:" +
                "पिंड\\s*(?:दान|दन|दाना|धान|दिया)" +  // Variations of पिंडदान
                "|" +
                "अ[स्ष]्?[तट]ि\\s*(?:" +  // Covers अस्थि, अस्ति, अष्टि, etc.
                "विसर्जन|विसरजन|विसर्जन्|बिसर्जन|" +  // Visarjan variants
                "प्रवाह|प्रभाव|परवाह|" +  // Pravah variants
                "लाय[ेँ]?|लाना|लाया|लये|ली|" +  // Laye variants
                "दाह|दह|डाह|दान" +  // Dah variants
                ")" +
                "|" +
                "दाह\\s*(?:संस्कार|संसकार|संस्कर|सस्कार|श्राद्ध)" +  // Daha sanskar variants
                "|" +
                "श्राद्ह|श्राद्ध|स्राद्ध|श्राद|श्रधा|सरद्ध|श्रद्धा" +  // Shraadh variants
                "|" +
                "अंत्येष्टि|अंत्येष्ठि|अन्त्येष्टि|अन्त्येष्ठि|अंतयेष्टी" +  // Antyeshti variants
                "|" +
                "सपिंडी|सपिण्डी|सपिन्दी|सपिंडि" +  // Sapindi variants
                "|" +
                "तर्पण|त्रपण|तर्पन|तलपन|तिरपण" +  // Tarpan variants
                "|" +
                "कापर|कार्पर|कापुर|कापा|कपर" +  // Kaapar variants
                "|" +
                "नवग्रह|नबग्रह|नवग्राह|नवग्र|नवगह" +  // Navgrah variants
                "|" +
                "तीर्थ|तिर्थ|तीर्थ्|तीत्थ|तीर्द" +  // Teerth variants
                "|" +
                "[पप]ूज[ाी]" +  // Pooja variations
                "|" +
                "हवन|होम|यज्ञ|हवन" +  // Havan variations
                "|" +
                "वेद\\s*पाठ|बेद\\s*पाठ|वेदपाठ" +  // Ved path variations
                "|" +
                "ब्राह्म(?:ण|मन)\\s*भोज|ब्राम्हण\\s*भोज" +  // Brahmin bhoj variations
                ")" +
                ")" +
                "(?:\\s+[^,.।\\n]*[,.।])?" +  // Optional text after ritual name until punctuation
                "|" +
                // Standalone ritual names without prefix
                "\\b(?:" +
                "(?:अ[स्ष]्?[तट]ि\\s*[वब]ि[सश]र्जन)|" +
                "(?:द?[हा]\\s*सं?स्?क[ार])|" +
                "श्र[ा]?द्ध[्ी]?" +
                ")\\b"
);


//    private static final Pattern RITUAL_PERSON_PATTERN = Pattern.compile("किसका अनुष्ठान[:：\s]*([^\\n]+)");
private static final Pattern RITUAL_PERSON_PATTERN = Pattern.compile(
        "(?:" +
                "(अस्थि\\s*(?:विसर्जन|प्रवाह|लाये)|पिंड\\s*दान|दाह\\s*संस्कार|श्राद्ध|अंत्येष्टि)" +
                "|(?:कर्म|कापर|नवग्रह|तीर्थ)" +
                ")" +
                "(?:\\s+(?:कराया|किया|करवाया|हुआ|कर|करके|कराई))?" +
                "\\s+([\\p{L}०-९]+(?:\\s+[\\p{L}०-९]+)*)" +  // Person's name
                "(?:\\s+(श्रीमती|श्री|स्व\\.?|स्वर्गीय|माता|पिता|चाचा))?"  // Honorific
);

    public String extractLocation(String text) {
        return extractFirstMatch(LOCATION_PATTERN, text);
    }

    public String extractPraMainPersonName(String text) {
        return extractFirstMatch(PRA_NAME_PATTERN, text);
    }

    public String extractFatherName(String text) {
        return extractFirstMatch(FATHER_NAME_PATTERN, text);
    }

    public String extractMotherName(String text) {
        return extractFirstMatch(MOTHER_NAME_PATTERN, text);
    }

    public String extractChachaName(String text) {
        return extractFirstMatch(CHACHA_NAME_PATTERN, text);
    }

    public String extractDadajiName(String text) {
        return extractFirstMatch(DADA_NAME_PATTERN, text);
    }

    public String extractDistrict(String text) {
        if (text.contains("बोकारो")) return "बोकारो";
        return null;
    }

    public String extractCaste(String text) {
        return extractFirstMatch(CASTE_PATTERN, text);
    }

    public String extractGender(String text) {
        if (text.contains("लड़का") || text.contains("बेटा")) return "पुरुष";
        if (text.contains("लड़की") || text.contains("बेटी")) return "महिला";
        return null;
    }

//    public String extractRitualName(String text) {
//        return extractFirstMatch(RITUAL_NAME_PATTERN, text);
//    }
public String extractanusthan_ka_naam(String text) {
    Matcher matcher = RITUAL_NAME_PATTERN.matcher(text);
    if (matcher.find()) {
        // Return the longest matched portion
        for (int i = 1; i <= matcher.groupCount(); i++) {
            if (matcher.group(i) != null && !matcher.group(i).trim().isEmpty()) {
                return matcher.group(i).trim().replaceAll("\\s+", " ");
            }
        }
    }
    return null;
}


//    public String extractKiskaAnusthan(String text) {
//        return extractFirstMatch(RITUAL_PERSON_PATTERN, text);
//    }
public String extractKiskaAnusthan(String text) {
    Matcher matcher = RITUAL_PERSON_PATTERN.matcher(text);
    if (matcher.find()) {
        // Combine name and honorific if present
        String name = matcher.group(matcher.groupCount() - 1); // Last group is name
        String honorific = matcher.group(matcher.groupCount()); // Optional honorific
        return (honorific != null) ? honorific + " " + name : name;
    }
    return null;
}

    public List<String> extractAllNames(String text) {
        List<String> names = new ArrayList<>();
        String[] patterns = {
                "बेटा\\s+([^\\n]+)",
                "लड़का\\s+([^\\n]+)",
                "माता\\s+([^\\n]+)",
                "चाचा\\s+([^\\n]+)",
                "([^\\s]+)\\s+महतो"
        };
        for (String regex : patterns) {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(text);
            while (m.find()) {
                names.add(m.group(1).trim());
            }
        }
        return names;
    }

    public String extractRelation(String text, String name) {
        if (text.contains("बेटा " + name)) return "बेटा";
        if (text.contains("लड़का " + name)) return "लड़का";
        if (text.contains("माता " + name)) return "माता";
        if (text.contains("चाचा " + name)) return "चाचा";
        return null;
    }

    public String extractHonorific(String name) {
        if (name.contains("महतो")) return "महतो";
        return null;
    }

    public List<String> extractDates(String text) {
        List<String> dates = new ArrayList<>();
        Matcher matcher = DATE_PATTERN.matcher(text);
        while (matcher.find()) {
            dates.add(matcher.group());
        }
        return dates;
    }

    public LocalDate parseDate(String dateStr) {
        if (StringUtils.isBlank(dateStr)) return null;
        try {
            String cleanDate = dateStr.replaceAll("[^0-9./-]", "");
            cleanDate = cleanDate.replace("/", "-").replace(".", "-");
            if (cleanDate.matches("\\d{1,2}-\\d{1,2}-\\d{4}")) {
                return LocalDate.parse(cleanDate, DateTimeFormatter.ofPattern("d-M-yyyy"));
            } else if (cleanDate.matches("\\d{1,2}-\\d{1,2}-\\d{2}")) {
                int year = Integer.parseInt(cleanDate.substring(cleanDate.lastIndexOf("-") + 1));
                year += (year < 50) ? 2000 : 1900;
                String fullDate = cleanDate.substring(0, cleanDate.lastIndexOf("-") + 1) + year;
                return LocalDate.parse(fullDate, DateTimeFormatter.ofPattern("d-M-yyyy"));
            }
        } catch (Exception e) {
            System.err.println("Error parsing date: " + dateStr);
        }
        return null;
    }

    public String cleanText(String text) {
        return text == null ? null : text.replaceAll("\\s+", " ").trim();
    }
//    public List<String> splitIntoParagraphsByTrigger(String fullText) {
//        List<String> paragraphs = new ArrayList<>();
//        String[] lines = fullText.split("\\n");
//        StringBuilder current = new StringBuilder();
//        for (String line : lines) {
//            current.append(line).append(" ");
//            if (DATE_PATTERN.matcher(line).find() || PRA_NAME_PATTERN.matcher(line).find()) {
//                paragraphs.add(current.toString().trim());
//                current = new StringBuilder();
//            }
//        }
//        if (current.length() > 0) {
//            paragraphs.add(current.toString().trim());
//        }
//        return paragraphs;
//    }
public List<String> splitIntoParagraphsByTrigger(String fullText) {
    List<String> paragraphs = new ArrayList<>();
    String[] lines = fullText.split("\\n");
    StringBuilder current = new StringBuilder();

    for (String line : lines) {
        current.append(line).append(" ");

        // Check if the line contains a date or a name
        boolean containsDate = DATE_PATTERN.matcher(line).find();
        boolean containsName = PRA_NAME_PATTERN.matcher(line).find();

        // If the line contains a date or a name, finalize the current paragraph
        if (containsDate || containsName) {
            paragraphs.add(current.toString().trim());
            current = new StringBuilder();
        } else {
            // If the line does not contain a date, we assume it's part of the current paragraph
            // If the next line is empty or the end of the text is reached, finalize the current paragraph
            if (line.trim().isEmpty() || line.equals(lines[lines.length - 1])) {
                paragraphs.add(current.toString().trim());
                current = new StringBuilder();
            }
        }
    }

    // Add any remaining text as a final paragraph
    if (current.length() > 0) {
        paragraphs.add(current.toString().trim());
    }

    return paragraphs;
}


    private String extractFirstMatch(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1).trim() : null;
    }
}


