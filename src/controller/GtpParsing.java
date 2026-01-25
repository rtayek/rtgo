package controller;

import java.util.Arrays;

final class GtpParsing {
    static final class ParsedTokens {
        final int id;
        final String[] tokens;

        ParsedTokens(int id, String[] tokens) {
            this.id = id;
            this.tokens = tokens;
        }
    }

    private GtpParsing() {}

    static ParsedTokens parseTokens(String string) {
        String[] tokens = splitTokens(string);
        int id = parseId(tokens);
        if (id != -1) {
            tokens = Arrays.copyOfRange(tokens, 1, tokens.length);
        }
        return new ParsedTokens(id, tokens);
    }

    static String[] splitTokens(String string) {
        return string.split(" ");
    }

    static int parseId(String[] tokens) {
        if (tokens.length > 0) {
            try {
                return Integer.parseInt(tokens[0]);
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        return -1;
    }

    static String joinTokensFrom(String[] tokens, int startIndex) {
        if (tokens.length <= startIndex) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = startIndex; i < tokens.length; i++) {
            if (i > startIndex) sb.append(' ');
            sb.append(tokens[i]);
        }
        return sb.toString();
    }

    static String strip(String string) {
        if (string == null) return null;
        String stripped = "";
        String trimmed = string.trim();
        for (int i = 0; i < trimmed.length(); i++) { // remove most control characters
            Character c = trimmed.charAt(i);
            if (Character.isISOControl(c)) { // check the spec on this
                if (c.equals('\n') || c.equals(GTPBackEnd.tab)) stripped += c;
            } else stripped += c;
        }
        trimmed = stripped;
        stripped = "";
        for (int i = 0; i < trimmed.length(); i++) { // remove comment
            Character c = trimmed.charAt(i);
            if (c.equals('#')) break;
            else stripped += c;
        }
        trimmed = stripped.trim();
        stripped = "";
        for (int i = 0; i < trimmed.length(); i++) { // change tab to space
            Character c = trimmed.charAt(i);
            if (c.equals('\t')) stripped += ' ';
            else stripped += c;
        }
        return stripped.trim();
    }
}
