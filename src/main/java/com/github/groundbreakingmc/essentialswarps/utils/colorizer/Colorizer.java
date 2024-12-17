package com.github.groundbreakingmc.essentialswarps.utils.colorizer;

public final class Colorizer {

    public final char colorChar = '§';

    public String colorize(final String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }

        final StringBuilder builder = new StringBuilder();
        final char[] messageChars = message.toCharArray();

        boolean isColor = false, isHashtag = false, isDoubleTag = false;

        for (int index = 0; index < messageChars.length; ) {
            final char currentChar = messageChars[index];
            if (isDoubleTag) {
                isDoubleTag = false;
                if (this.processDoubleTag(builder, messageChars, index)) {
                    index += 3;
                    continue;
                }

                builder.append("&##");

            } else if (isHashtag) {
                isHashtag = false;
                if (currentChar == '#') {
                    isDoubleTag = true;
                    index++;
                    continue;
                }

                if (this.processSingleTag(builder, messageChars, index)) {
                    index += 6;
                    continue;
                }

                builder.append("&#");

            } else if (isColor) {
                isColor = false;
                if (currentChar == '#') {
                    isHashtag = true;
                    index++;
                    continue;
                }

                if (this.isColorCharacter(currentChar)) {
                    builder.append(this.colorChar).append(currentChar);
                    index++;
                    continue;
                }

                builder.append('&');

            } else if (currentChar == '&') {
                isColor = true;
                index++;
            } else {
                builder.append(currentChar);
                index++;
            }
        }

        this.appendRemainingColorTags(builder, isColor, isHashtag, isDoubleTag);

        return builder.toString();
    }

    private boolean processDoubleTag(final StringBuilder builder, final char[] messageChars, final int index) {
        if (index + 3 <= messageChars.length && this.isValidHexCode(messageChars, index, 3)) {
            builder.append(this.colorChar).append('x');
            for (int i = index; i < index + 3; i++) {
                builder
                        .append(this.colorChar)
                        .append(messageChars[i])
                        .append(this.colorChar)
                        .append(messageChars[i]);
            }

            return true;
        }

        return false;
    }

    private boolean processSingleTag(final StringBuilder builder, final char[] messageChars, final int index) {
        if (index + 6 <= messageChars.length && this.isValidHexCode(messageChars, index, 6)) {
            builder.append(this.colorChar).append('x');
            for (int i = index; i < index + 6; i++) {
                builder.append(this.colorChar).append(messageChars[i]);
            }

            return true;
        }

        return false;
    }

    private boolean isValidHexCode(final char[] chars, final int start, final int length) {
        for (int i = start; i < start + length; i++) {
            char tmp = chars[i];
            if (!((tmp >= '0' && tmp <= '9') || (tmp >= 'a' && tmp <= 'f') || (tmp >= 'A' && tmp <= 'F'))) {
                return false;
            }
        }

        return true;
    }

    private void appendRemainingColorTags(final StringBuilder builder, final boolean isColor, final boolean isHashtag, final boolean isDoubleTag) {
        if (isColor) {
            builder.append('&');
        } else if (isHashtag) {
            builder.append("&#");
        } else if (isDoubleTag) {
            builder.append("&##");
        }
    }

    public boolean isColorCharacter(final char c) {
        return (c >= '0' && c <= '9') ||
                (c >= 'a' && c <= 'f') ||
                c == 'r' ||
                (c >= 'k' && c <= 'o') ||
                c == 'x' ||
                (c >= 'A' && c <= 'F') ||
                c == 'R' ||
                (c >= 'K' && c <= 'O') ||
                c == 'X';
    }
}
