package controller;

import java.util.Arrays;

public final class GtpParsing {
    public static final class ParsedTokens {
        final int id;
        final String[] tokens;

        ParsedTokens(int id, String[] tokens) {
            this.id = id;
            this.tokens = tokens;
        }
    }

    private GtpParsing() {}

    public static ParsedTokens parseTokens(String string) {
        String[] tokens = splitTokens(string);
        int id = parseId(tokens);
        if (id != -1) {
            tokens = Arrays.copyOfRange(tokens, 1, tokens.length);
        }
        return new ParsedTokens(id, tokens);
    }

    public static String[] normalizeArguments(String stripped, String[] tokens) {
        if (tokens.length > 0 && tokens[0].equals(Command.tgo_receive_sgf.name())) {
            int index = stripped.indexOf(Command.tgo_receive_sgf.name());
            String[] arguments = new String[2];
            arguments[0] = tokens[0];
            arguments[1] = stripped.substring(index + Command.tgo_receive_sgf.name().length());
            arguments[1] = strip(arguments[1]);
            return arguments;
        }
        return tokens;
    }

    public static String[] splitTokens(String string) {
        return string.split(" ");
    }

    public static int parseId(String[] tokens) {
        if (tokens.length > 0) {
            try {
                return Integer.parseInt(tokens[0]);
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        return -1;
    }

    public static String joinTokensFrom(String[] tokens, int startIndex) {
        if (tokens.length <= startIndex) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = startIndex; i < tokens.length; i++) {
            if (i > startIndex) sb.append(' ');
            sb.append(tokens[i]);
        }
        return sb.toString();
    }

    public static String strip(String string) {
        if (string == null) return null;
        String trimmed = string.trim();
        StringBuilder sb = new StringBuilder(trimmed.length());
        for (int i = 0; i < trimmed.length(); i++) { // remove most control characters
            char c = trimmed.charAt(i);
            if (Character.isISOControl(c)) { // check the spec on this
                if (c == '\n' || c == GTPBackEnd.tab) sb.append(c);
            } else sb.append(c);
        }
        trimmed = sb.toString();
        sb.setLength(0);
        for (int i = 0; i < trimmed.length(); i++) { // remove comment
            char c = trimmed.charAt(i);
            if (c == '#') break;
            sb.append(c);
        }
        trimmed = sb.toString().trim();
        sb.setLength(0);
        for (int i = 0; i < trimmed.length(); i++) { // change tab to space
            char c = trimmed.charAt(i);
            sb.append(c == '\t' ? ' ' : c);
        }
        return sb.toString().trim();
    }
}
