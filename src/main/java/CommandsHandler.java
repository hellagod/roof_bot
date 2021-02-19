import commands.TestCommand;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class CommandsHandler extends TelegramLongPollingCommandBot {
    public CommandsHandler(String botUsername) {
        super(botUsername);
        register(new TestCommand("test", "test -> rar"));
        registerDefaultAction((absSender, message) -> {
            SendMessage commandUnknownMessage = new SendMessage();
            commandUnknownMessage.setChatId(message.getChatId());
            commandUnknownMessage.setText("The command '" + message.getText() + "' is not known by this bot. Here comes some help ");
            try {
                absSender.execute(commandUnknownMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        });
    }


    @Override
    public void processNonCommandUpdate(Update update) {

    }

    @Override
    public String getBotToken() {
        return "1697927258:AAGZeyNXZIe9AEtEzh-vGRDBjVzshiktQ98";
    }
}
