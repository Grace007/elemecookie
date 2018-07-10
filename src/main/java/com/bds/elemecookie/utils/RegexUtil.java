package com.bds.elemecookie.utils;

import org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author eli
 * @date 2017/11/13 13:56
 */
public class RegexUtil {
    public static String match1(String regex,String str){
        Matcher matcher = Pattern.compile(regex).matcher(str);
        if (matcher.find()){
            return matcher.group();
        }
        return "";
    }
    public static String match2(String regex,String str){
        Matcher matcher = Pattern.compile(regex).matcher(str);
        if (matcher.find()){
            return matcher.group(1);
        }
        return "";
    }
    public static String[] match3(String regex,String str){
        Matcher matcher = Pattern.compile(regex).matcher(str);
        String[] results=new String[50];

        int i=0;
        while (matcher.find()){
            results[i]=matcher.group();
            i++;
        }
        return results;
    }
    public static String subString(String begin, String end, String content) {
        if (StringUtils.isEmpty(content)) {
            return content;
        }
        int index_s = 0;
        if (begin != null) {
            index_s = content.indexOf(begin) + begin.length();
        }
        String _end = content.substring(index_s, content.length());
        int index_e = 0;

        if (end == null) {
            index_e = content.length();
        } else {
            index_e = _end.indexOf(end);
        }
        return _end.substring(0, index_e);
    }


}
