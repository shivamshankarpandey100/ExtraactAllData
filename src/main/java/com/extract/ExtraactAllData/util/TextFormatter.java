package com.extract.ExtraactAllData.util;

public class TextFormatter {

    public static String format(String input) {
        String[] lines = input.split("\\r?\\n");
        StringBuilder result = new StringBuilder();
        StringBuilder currentBlock = new StringBuilder();

        for (String line : lines) {
            line = line.trim();

            // Skip empty or irrelevant lines
            if (line.isEmpty() || isJunkLine(line)) {
                continue;
            }

            // Join line into the current record
            currentBlock.append(line).append(" ");

            // Check for potential end of record
            if (isEndOfRecord(line)) {
                String record = currentBlock.toString().trim();

                // Add danda if missing
                if (!record.endsWith("।")) {
                    record += "।";
                }

                result.append(record).append("\n\n");
                currentBlock.setLength(0); // Reset block
            }
        }

        // Handle leftover text as a record
        if (currentBlock.length() > 0) {
            String record = currentBlock.toString().trim();
            if (!record.endsWith("।")) {
                record += "।";
            }
            result.append(record);
        }

        return result.toString().trim();
    }

    // Identifies irrelevant or noisy lines
    private static boolean isJunkLine(String line) {
        return line.matches(".*\\b(और लिखा पीछे|^\\d+$|^\\d+\\s+)$");
    }

    // Checks whether the line is likely the end of a record
    private static boolean isEndOfRecord(String line) {
        return line.matches(".*(सं\\.?\\s*\\d{3,4}|ता\\.?\\s*\\d{1,2}[-./]\\d{1,2}[-./]\\d{2,4}|\\d{4})$") ||
                line.endsWith("।") ||
                line.endsWith("\"") ||
                line.endsWith("//");
    }
}
