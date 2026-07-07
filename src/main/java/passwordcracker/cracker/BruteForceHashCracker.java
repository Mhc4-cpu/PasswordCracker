package passwordcracker.cracker;

import passwordcracker.util.Md5Util;

public class BruteForceHashCracker implements HashCracker {

    private static final char[] ALPHABET =
            "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final int MAX_LENGTH = 4;

    private int attempts;

    @Override
    public String crack(String hash) {
        attempts = 0;

        for (int length = 1; length <= MAX_LENGTH; length++) {
            String found = tryAllCombinations(new char[length], 0, hash);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    
    public int getAttempts() {
        return attempts;
    }

   
    private String tryAllCombinations(char[] buffer, int position, String hash) {
        if (position == buffer.length) {
            String candidate = new String(buffer);
            attempts++;
            return Md5Util.matches(candidate, hash) ? candidate : null;
        }

        for (char c : ALPHABET) {
            buffer[position] = c;
            String found = tryAllCombinations(buffer, position + 1, hash);
            if (found != null) {
                return found;
            }
        }
        return null;
    }
}
