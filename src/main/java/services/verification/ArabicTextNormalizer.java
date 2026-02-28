package services.verification;

public final class ArabicTextNormalizer {
    private ArabicTextNormalizer() {
    }

    public static String normalize(String input) {
        if (input == null) return "";
        String s = input;

        // remove tatweel
        s = s.replace("ـ", "");

        // remove arabic diacritics
        s = s.replaceAll("[\\u064B-\\u0652\\u0670]", "");

        // normalize common letters
        s = s.replace('أ', 'ا').replace('إ', 'ا').replace('آ', 'ا');
        s = s.replace('ى', 'ي');
        s = s.replace('ؤ', 'و');
        s = s.replace('ئ', 'ي');

        // normalize spaces
        s = s.replaceAll("\\s+", " ").trim();

        return s;
    }
}
