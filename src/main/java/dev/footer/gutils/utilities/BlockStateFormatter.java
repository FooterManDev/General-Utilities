package dev.footer.gutils.utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

//Currently unused
public class BlockStateFormatter {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static String format(String blockStateString) {
        StringBuilder jsonStringBuilder = new StringBuilder();
        jsonStringBuilder.append("{\n");

        String[] pairs = blockStateString.split(",");
        for (int i = 0; i < pairs.length; i++) {
            String[] keyValue = pairs[i].split("=");
            if (keyValue.length == 2) {
                String key = keyValue[0].trim(); // Remove any leading/trailing whitespace
                String value = keyValue[1].trim(); // Remove any leading/trailing whitespace

                // Check if the value contains brackets and format it accordingly
                if (value.startsWith("[") && value.endsWith("]")) {
                    // Remove the brackets and split the content by commas
                    String[] arrayValues = value.substring(1, value.length() - 1).split(",");
                    // Reconstruct the value as a formatted array with line breaks for readability
                    value = "[\n" + String.join(",\n", arrayValues) + "\n]";
                }

                // Manually format the key-value pair as a JSON string with line breaks
                jsonStringBuilder.append(" \"").append(key).append("\": ").append(value);

                // Add a comma unless it's the last pair
                if (i < pairs.length - 1) {
                    jsonStringBuilder.append(",\n");
                }
            }
        }

        jsonStringBuilder.append("\n}");

        return jsonStringBuilder.toString();
    }
}
