//
//
//package com.extract.ExtraactAllData.util;
//
//public class TextFormatter {
//
//    public static String format(String input) {
//        // Normalize input
//        String cleanedInput = input
//                .replaceAll("[\\r\\n]+", " ")
//                .replaceAll(" +", " ")
//                .trim();
//
//        // Enhanced pattern to split paragraphs
//        String paragraphPattern = "(?<=।)|(?<=[\"”])|(?<=सं\\.[०-९]{4})";
//
//        String[] paragraphs = cleanedInput.split(paragraphPattern);
//        StringBuilder result = new StringBuilder();
//
//        for (String paragraph : paragraphs) {
//            paragraph = paragraph.trim();
//            if (paragraph.isEmpty()) continue;
//
//            // Preserve existing ब्रा. prefix if present
//            if (!paragraph.startsWith("ब्रा.") &&
//                    !paragraph.startsWith("ब्रा०") &&
//                    !paragraph.startsWith("ब्रा")) {
//
//                // Don't add ब्रा. if not already present
//                // Just ensure proper ending
//                if (!paragraph.endsWith("।") && !paragraph.matches(".*[\"”]$")) {
//                    paragraph = paragraph + "।";
//                }
//            } else {
//                // Paragraph already starts with ब्रा. variant - ensure proper ending
//                if (!paragraph.endsWith("।") && !paragraph.matches(".*[\"”]$")) {
//                    paragraph = paragraph + "।";
//                }
//            }
//
//            result.append(paragraph).append("\n\n");
//        }
//
//        return result.toString().trim();
//    }
//
//    // Helper to check valid prefixes
//    private static boolean startsWithBraPrefix(String text) {
//        return text.startsWith("ब्रा.") ||
//                text.startsWith("ब्रा०") ||
//                text.startsWith("ब्रा");
//    }
//}
package com.extract.ExtraactAllData.util;

import java.util.*;
import java.util.regex.*;

public class TextFormatter {

    public static String format(String input) {
        // Step 1: Normalize input - remove line breaks and extra spaces
        String cleanedInput = input
                .replaceAll("[\\r\\n]+", " ")      // remove line breaks
                .replaceAll(" +", " ")             // collapse multiple spaces
                .trim();

        List<String> paragraphs = new ArrayList<>();
        StringBuilder currentParagraph = new StringBuilder();

        // Step 2: Tokenize by space
        String[] tokens = cleanedInput.split(" ");
        boolean paragraphEnded = true;

        for (int i = 0; i < tokens.length; i++) {
            String word = tokens[i].trim();
            if (word.isEmpty()) continue;

            // If current token is a paragraph starter and previous paragraph ended
            if (paragraphEnded && isStartPrefix(word)) {
                if (currentParagraph.length() > 0) {
                    String para = finalizeParagraph(currentParagraph.toString());
                    paragraphs.add(para);
                    currentParagraph.setLength(0);
                }
                paragraphEnded = false;
            }

            currentParagraph.append(word).append(" ");

            if (endsParagraph(currentParagraph.toString().trim())) {
                paragraphEnded = true;
            }
        }

        // Add any remaining content
        if (currentParagraph.length() > 0) {
            String lastPara = finalizeParagraph(currentParagraph.toString());
            paragraphs.add(lastPara);
        }

        // Step 3: Build final formatted output
        StringBuilder result = new StringBuilder();
        for (String para : paragraphs) {
            result.append(para).append("\n\n");
        }

        return result.toString().trim();
    }

    private static String finalizeParagraph(String paragraph) {
        paragraph = paragraph.trim();
        if (!paragraph.endsWith("।")) {
            paragraph += "।";
        }
        return paragraph;
    }

    /**
     * Detect whether current text is the end of a paragraph.
     */
    private static boolean endsParagraph(String text) {
        String pattern = ".*("
                + "ता[\\.०-९ ]*[०-९]{1,2}[./\\-][०-९]{1,2}[./\\-][०-९]{2,4}"  // ता. ४.१.०८
                + "|[०-९]{1,2}/[०-९]{1,2}/[०-९]{2,4}"                         // १०/6/02
                + "|[०-९]{1,2}-[०-९]{1,2}-[०-९]{2,4}"                         // 11-10-2012
                + "|[०-९]{10}"                                                // mobile numbers
                + "|[०-९]{4,5}[-–][०-९]{4,5}"                                 // ID/Mobile-like
                + "ता[\\.०-९0-9 ]*[०-९0-9]{1,2}[./\\-][०-९0-9]{1,2}[./\\-][०-९0-9]{2,4}"
                + "|अस्ती लाये"
                + "|अस्तीलाये"
                + "|पूर्णपिता"
                + "|आई बु\\."
                + "|वासी[^\\s]*"
                + "|के आये"
                + ")$";
        return text.matches(pattern);
    }

    /**
     * Detect whether a token marks the start of a new paragraph.
     */
    private static boolean isStartPrefix(String word) {
        return word.startsWith("ब्रा") ||
                word.startsWith("प्रा") ||
                word.startsWith("बा०") ||
                word.startsWith("●") ||
                word.startsWith("ना.") ||
                word.startsWith("परिकियरनाथ");
    }
}

