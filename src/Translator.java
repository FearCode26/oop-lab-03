import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;

class Translator {
    // Разделители слов в тексте
    private static final String WORDS_REGEX = "(?:(?<![a-zA-Z])'|'(?![a-zA-Z])|[^a-zA-Z'])+";

    // Сортировка по длине: первыми идут самые длинные выражения
    private final Map<String, String> dictionary = new TreeMap<>((first, second) -> {
        int byLength = Integer.compare(second.length(), first.length());
        return byLength != 0 ? byLength : first.compareTo(second);
    });

    // Загрузка словаря из файла
    public void loadDictionary(String filePath) throws FileReadException, InvalidFileFormatException {
        try {
            dictionary.clear();

            for (var line : Files.readAllLines(Paths.get(filePath))) {
                var parts = line.split("\\|");
                // Проверка формата: слово | перевод
                if (parts.length != 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
                    throw new InvalidFileFormatException("Invalid format file: " + line);
                }
                dictionary.put(parts[0].trim().toLowerCase(), parts[1].trim());
            }
        } catch (IOException e) {
            throw new FileReadException("Cant read the file: " + e.getMessage());
        }
    }

    // Перевод текста
    public String translate(String text) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }

        // Разбиваем текст на слова
        String[] words = text.split("\\s+");
        StringBuilder result = new StringBuilder();

        int i = 0;
        while (i < words.length) {
            String translated = null;
            int wordCount = 1;

            // Первое совпадение будет самым длинным
            for (String dictWord : dictionary.keySet()) {
                String[] phraseWords = dictWord.split(WORDS_REGEX);

                if (i + phraseWords.length > words.length) {
                    continue;
                }

                // Сравниваем выражение со словами текста
                boolean matches = true;
                for (int j = 0; j < phraseWords.length; j++) {
                    if (!words[i + j].equalsIgnoreCase(phraseWords[j])) {
                        matches = false;
                        break;
                    }
                }

                if (matches) {
                    translated = dictionary.get(dictWord);
                    wordCount = phraseWords.length;
                    break;
                }
            }

            if (i > 0) {
                result.append(" "); // Пробел между словами
            }
            result.append(translated != null ? translated : words[i]);
            i += wordCount; // Пропускаем уже переведённые слова
        }

        return result.toString();
    }
}
