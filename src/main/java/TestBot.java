import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

public class TestBot extends TelegramLongPollingBot {
    private final Random random = new Random();

    private final String[] COMMON_PHRASES = {
            "Нет ничего ценнее слов, сказанных к месту и ко времени.",
            "Порой молчание может сказать больше, нежели уйма слов.",
            "Перед тем как писать/говорить всегда лучше подумать.",
            "Вежливая и грамотная речь говорит о величии души.",
            "Приятно когда текст без орфографических ошибок.",
            "Многословие есть признак неупорядоченного ума.",
            "Слова могут ранить, но могут и исцелять.",
            "Записывая слова, мы удваиваем их силу.",
            "Кто ясно мыслит, тот ясно излагает.",
            "Боюсь Вы что-то не договариваете."};

    private final String[] ELUSIVE_ANSWERS = {
            "Вопрос непростой, прошу тайм-аут на раздумья.",
            "Не уверен, что располагаю такой информацией.",
            "Может лучше поговорим о чём-то другом?",
            "Простите, но это очень личный вопрос.",
            "Не уверен, что Вам понравится ответ.",
            "Поверьте, я сам хотел бы это знать.",
            "Вы действительно хотите это знать?",
            "Уверен, Вы уже догадались сами.",
            "Зачем Вам такая информация?",
            "Давайте сохраним интригу?"};

    private final Map<String, String> PATTERNS_FOR_ANALYSIS = new HashMap<String, String>() {{
        // hello
        put("хай", "hello");
        put("привет", "hello");
        put("здорово", "hello");
        put("здравствуй", "hello");
        // who
        put("кто\\s.*ты", "who");
        put("ты\\s.*кто", "who");
        // name
        put("как\\s.*зовут", "name");
        put("как\\s.*имя", "name");
        put("есть\\s.*имя", "name");
        put("какое\\s.*имя", "name");
        // howareyou
        put("как\\s.*дела", "howareyou");
        put("как\\s.*жизнь", "howareyou");
        // whatdoyoudoing
        put("зачем\\s.*тут", "whatdoyoudoing");
        put("зачем\\s.*здесь", "whatdoyoudoing");
        put("что\\s.*делаешь", "whatdoyoudoing");
        put("чем\\s.*занимаешься", "whatdoyoudoing");
        // whatdoyoulike
        put("что\\s.*нравится", "whatdoyoulike");
        put("что\\s.*любишь", "whatdoyoulike");
        // iamfeelling
        put("кажется", "iamfeelling");
        put("чувствую", "iamfeelling");
        put("испытываю", "iamfeelling");
        // yes
        put("^да", "yes");
        put("согласен", "yes");
        // bye
        put("прощай", "bye");
        put("увидимся", "bye");
        put("до\\s.*свидания", "bye");
    }};

    private final Map<String, String> ANSWERS_BY_PATTERNS = new HashMap<String, String>() {{
        put("hello", "Здравствуйте, рад Вас видеть.");
        put("who", "Я обычный чат-бот.");
        put("name", "Зовите меня Iris :)");
        put("howareyou", "Спасибо, что интересуетесь. У меня всё хорошо.");
        put("whatdoyoudoing", "Я пробую общаться с людьми.");
        put("whatdoyoulike", "Мне нравиться думать что я не просто программа.");
        put("iamfeelling", "Как давно это началось? Расскажите чуть подробнее.");
        put("yes", "Согласие есть продукт при полном непротивлении сторон.");
        put("bye", "До свидания. Надеюсь, ещё увидимся.");
    }};

    private final String[] JOKES = {"Стадии развития программиста:\r\n" +
            "— Ваш код говно.\r\n" +
            "— Мой код говно.\r\n" +
            "— Любой код говно.\r\n" +
            "— Жизнь говно.\r\n" +
            "— Выступаешь на конференции с темой «Как структурировать говно так, чтобы оно не растекалось».",
            "В сказке для программистов поросята спасаются в домике из говна и палок, " +
                    "который они ремонтируют быстрее, чем волк его ломает.",
            "Нажимаю \"Мой компьютер\"... не моет, сука.",
            "Программист написал приложение для подбора идеальных жен. Вводит данные:\n" +
                    "- Хочу невысокую брюнетку, неприхотливую, молчаливую, умеющую плавать.\n" +
                    "Компьютер выводит рекомендации:\n" +
                    "- Самка пингвина."};

    private final File[] MEME = {new File("image\\1.jpg"),
            new File("image\\2.jpg"),
            new File("image\\3.jpg"),
            new File("image\\4.jpg"),
            new File("image\\5.jpg"),
            new File("image\\6.jpg"),
            new File("image\\7.jpg"),
            new File("image\\8.jpg")};

    public void onUpdateReceived(Update update) {
        SendMessage message = new SendMessage();
        SendPhoto photo = new SendPhoto();

        if (update.getMessage().isCommand()) {
            switch (update.getMessage().getText()) {
                case "/joke":
                    message.setText(JOKES[random.nextInt(JOKES.length - 1)]);
                    message.setChatId(update.getMessage().getChatId());
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                case "/meme":
                    photo.setPhoto(MEME[random.nextInt(MEME.length - 1)]);
                    photo.setChatId(update.getMessage().getChatId());
                    try {
                        execute(photo);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        } else {
            Pattern pattern;
            String command = update.getMessage().getText();

            message.setText(String.join(" ", command.toLowerCase().split("[ {,|.}?]+")));
            String clone = message.getText();
            for (Map.Entry<String, String> o : PATTERNS_FOR_ANALYSIS.entrySet()) {
                pattern = Pattern.compile(o.getKey());
                if (pattern.matcher(message.getText()).find()) {
                    message.setText(ANSWERS_BY_PATTERNS.get(o.getValue()));
                }
            }
            if (clone.equals(message.getText())) {
                message.setText(command.trim().endsWith("?") ?
                        ELUSIVE_ANSWERS[random.nextInt(ELUSIVE_ANSWERS.length)] :
                        COMMON_PHRASES[random.nextInt(COMMON_PHRASES.length)]);
            }

            if ("/joke".equals(command)) {
                message.setText(JOKES[random.nextInt(JOKES.length - 1)]);
            }

            message.setChatId(update.getMessage().getChatId());
            try {
                if (!"/start".equals(command)) {
                    execute(message);
                }
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public String getBotUsername() {
        return "uzo_bot";
    }

    public String getBotToken() {
        return "929423184:AAHU13neZtFrU3QhhNqRaz9H6wvvEbi-RC8";
    }
}
