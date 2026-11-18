import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Путь можно передать аргументом или ввести с клавиатуры
        String filePath;
        if (args.length == 0) {
            System.out.print("Enter the path to the dictionary file: ");
            filePath = scanner.nextLine();
        } else {
            filePath = args[0];
        }

        Translator translator = new Translator();

        try {
            // Читаем словарь перед переводом
            translator.loadDictionary(filePath);
        } catch (FileReadException | InvalidFileFormatException e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.println("Enter text to translate: ");
        String text = scanner.nextLine(); // Ввод текста пользователем
        System.out.println(translator.translate(text));
    }
}
