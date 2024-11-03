package com.kkumteul.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class DateUtil {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");

    public Date parseBirthStringToDate(String birth) {
        try {
            return dateFormat.parse(birth);
        } catch (ParseException e) {
            throw new IllegalArgumentException("생년월일이 올바르지 않습니다. 'yyMMdd' 형태여야 합니다.", e);
        }
    }
}
