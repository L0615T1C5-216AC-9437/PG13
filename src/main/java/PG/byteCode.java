package PG;

import arc.util.Log;
import org.json.JSONObject;

import java.util.Iterator;

public class byteCode {
    public static String censor(String string) {
        StringBuilder builder = new StringBuilder();
        String sentence[] = string.split(" ");
        JSONObject badList = Main.badList;
        if (badList == null) {
            Log.err("badList.cn does not exist!");
            return string;
        }
        for (String word: sentence) {
            for (Iterator<String> it = badList.keys(); it.hasNext(); ) {
                String key = it.next();
                if (word.contains(key)) {
                    String temp = "";
                    for (int i = 1; i < key.length(); i++) temp += "*";
                    word = word.replace(key, key.charAt(0)+temp);
                }
            }
            builder.append(word).append(" ");
        }
        return builder.toString();
    }
}
