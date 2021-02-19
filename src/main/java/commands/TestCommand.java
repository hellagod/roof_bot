package commands;

import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TestCommand extends BotCommand {
    public TestCommand(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        SendMessage answer = new SendMessage();
        answer.setChatId(chat.getId().toString());
        answer.setText("rar+5");
        try{
            absSender.execute(answer);
        }catch (TelegramApiException e){
            e.printStackTrace();
        }
    }
}
