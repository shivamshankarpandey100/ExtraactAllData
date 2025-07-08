package com.extract.ExtraactAllData.service;

import com.extract.ExtraactAllData.util.TextFormatter;
import org.springframework.stereotype.Service;

@Service
public class TextService {

    public String formatInput(String input) {
        return TextFormatter.format(input);
    }
}