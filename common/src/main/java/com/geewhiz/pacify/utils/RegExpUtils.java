package com.geewhiz.pacify.utils;

import java.util.regex.Pattern;

public class RegExpUtils {

    public static Pattern getDefaultPattern(String beginToken, String endToken) {
        String regExp = "([^" + Pattern.quote(endToken) + "]*?)";

        return getPatternFor(beginToken, endToken, regExp);
    }
    
    
    public static Pattern getPatternFor(String beginToken, String endToken, String regExp) {
        String searchPattern = Pattern.quote(beginToken) + regExp + Pattern.quote(endToken);

        Pattern pattern = Pattern.compile(searchPattern);
        return pattern;
    }

}
