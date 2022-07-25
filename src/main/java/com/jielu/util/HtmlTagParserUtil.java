package com.jielu.util;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This Class created for extract html tag and then convert into linkedHashMap
 * The linked-hashMap wrapping every item
 * eg:<button id="it" click="event()"></button>
 */
public final class HtmlTagParserUtil {

    private final String text;

    private  String type;

    private static final Pattern TAG_PATTERN = Pattern.compile("<(\"[^\"]*\"|'[^']*'|[^'\">])*>");

    private Map<String, String> tagMap = Collections.synchronizedMap(new LinkedHashMap<>());

    private static  final Character START_FLAG ='<';

    private static  final Character END_FLAG ='>';

    private static  final Character EQUALS_FLAG ='=';

    private  HtmlTagParserUtil(){

        throw  new UnsupportedOperationException("Do not allowed init the class:"+HtmlTagParserUtil.class.getClass().getSimpleName());
    }

    public HtmlTagParserUtil(String text) {
        this.text = text.trim();
    }

    private static boolean matchText(String text) {
        Matcher matcher = TAG_PATTERN.matcher(text);
        return matcher.matches();
    }


    public Map<String, String> getTagMap() {
        return tagMap;
    }

    public String getText() {
        StringBuilder sbd = new StringBuilder();
        sbd.append(START_FLAG).append(type);
        tagMap.forEach((attribute, value) -> {
            sbd.append(" ");
            sbd.append(attribute);
            sbd.append(EQUALS_FLAG);
            sbd.append(value);
        });
        sbd.append(" ").append("/").append(END_FLAG);
        return sbd.toString();
    }

    public void convert() {
        String text = this.text;
        if(!matchText(text)){
            throw  new RuntimeException("The input text is not a valid html-tag:"+text);
        }
        char[] chs = text.substring(0, text.length() - 1).toCharArray();
        StringBuilder sbd = new StringBuilder();
        List<String> ls = new ArrayList<>();
        for (char c : chs) {
            if (c != EQUALS_FLAG && c != ' ') {
                sbd.append(Character.valueOf(c));
            }
            else if (c == ' ') {
                if (sbd.toString().trim().length() > 0) {
                    ls.add(sbd.toString());
                }
                sbd = new StringBuilder();
                continue;
            }
            if (c == EQUALS_FLAG) {
                if (sbd.toString().trim().length() > 0) {
                    ls.add(sbd.toString());
                }
                ls.add(EQUALS_FLAG +"");
                sbd = new StringBuilder();
            }
        }
        if (sbd.length() > 0) {
            ls.add(sbd.toString());
        }
        type = ls.get(0).substring(1);
        for (int i = 1; i < ls.size(); i++) {
            String dd = ls.get(i);
            if ((EQUALS_FLAG+"").equals(dd)) {
                if (i + 1 >= ls.size()) {
                    continue;
                }
                tagMap.put(ls.get(i - 1), ls.get(i + 1));
                ++i;
            }
        }

    }

    public void setAttribute(String attribute, String value) {
        if (tagMap.containsKey(attribute)) {
            tagMap.put(attribute, "\"" + value + "\"");
        }
    }


    public void addAttribute(String attribute, String value) {
        tagMap.put(attribute, "\"" + value + "\"");
    }

    public void removeAttribute(String attribute, String value) {
        tagMap.remove(attribute);
    }



    public static void main(String[] args) {
        String input = " <input type=\"text\" readonly=\"readonly\" autocomplete = \"off\" placeholder=\"请选择\" class=\"el-input__inner\">";
        HtmlTagParserUtil htmlTagParser = new HtmlTagParserUtil(input);
        htmlTagParser.convert();
        htmlTagParser.setAttribute("readonly", "false");
        htmlTagParser.addAttribute("style", "height=100;weight=200");
        System.out.println(htmlTagParser.getText());

    }
}
