
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

public class Bot extends TelegramLongPollingBot {

    private static Constants constants = new Constants();


    @Override
    public void onUpdateReceived(Update update) {
        update.getUpdateId();
        Message message = update.getMessage();
        if (update.hasCallbackQuery()) {
            long chatId = update.getCallbackQuery().getFrom().getId();
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String scenario_ind = callbackQuery.getData();
            System.out.println(scenario_ind);
            long id = send(Scenario.distributor(scenario_ind, chatId, ""));
            Scenario.setTemporaryMessage(chatId, id);
        } else {
            try (MongoClient mongoClient = MongoClients.create()) {
                SendMessage sendMessage = new SendMessage().setChatId(message.getChatId());
                MongoDatabase db = mongoClient.getDatabase("test");
                MongoCollection users = db.getCollection("users");
                FindIterable documents = users.find(new Document("chat_id", message.getChatId()));
                if (message.hasContact()) {

                    ReplyKeyboardRemove removeKeyboard = new ReplyKeyboardRemove();
                    removeKeyboard.setSelective(true);
                    sendMessage.setReplyMarkup(removeKeyboard);

                    Contact contact = message.getContact();
                    System.out.println(contact.getPhoneNumber());
                    FindIterable documents_us = users.find(new Document("phone", contact.getPhoneNumber()));
                    Document user_reg = (Document) documents_us.first();


                    if (user_reg != null) {
                        //String name = contact.getFirstName() + (contact.getLastName() != null ? " " + contact.getLastName() : "");

                        users.updateOne(new Document("phone", user_reg.get("phone")),
                                new Document("$set", new Document("chat_id", contact.getUserID())));

                        users.updateOne(new Document("phone", user_reg.get("phone")),
                                new Document("$set", new Document("scenario", "0.0")));

                        StringBuilder sb = new StringBuilder(constants.REGISTRATION_PERFORMED);
                        sb.replace(sb.indexOf("1"), sb.indexOf("1") + 1, "<b>" + Scenario.getRole(user_reg.getInteger("role")) + "</b>");
                        sb.replace(sb.indexOf("2"), sb.indexOf("2") + 1, "<b>" + user_reg.getString("work_name") + "</b>");
                        sendMessage.setText(sb.toString());
                        sendMessage.setParseMode("html");
                        send(sendMessage);
                    } else {

                        sendMessage.setText(constants.REGISTRATION_ID_NOT_FOUND);
                        send(sendMessage);
                    }
                } else {
                    if (message.getText().charAt(0) == '/') {
                        sendMessage = new SendMessage().setChatId(message.getChatId());
                        String command = message.getText().substring(1);
                        if (command.contains("sign_up") || documents.first() == null) {
                            if(command.contains("user-add-admin")){
                                Map<String, Object> map = new HashMap<>();
                                map.put("_id", new ObjectId());
                                map.put("role", 0);
                                map.put("phone", "79602075687");
                                map.put("work_name", "Krest");
                                users.insertOne(new Document(map));
                                return;
                            }
                            sendMessage.setText(constants.REGISTRATION_INQUIRY);

                            sendMessage.setReplyMarkup(createRegistrationButton());

                            send(sendMessage);
                        } else {
                            Document user = (Document) documents.first();
                            String scenario = (String) user.get("scenario");
                            boolean isCancel = false;
                            if (command.contains("cancel") || !scenario.equals("0.0")) {
                                send(Scenario.distributor(scenario.substring(0, scenario.indexOf('.') + 1) + "cancel", message.getChatId(), ""));
                                isCancel = true;
                            }
                            switch (command) {
                                case "menu":
                                case "start":
                                    long id = send(Scenario.distributor("0." + (isCancel ? "1" : "0"), message.getChatId(), ""));
                                    Scenario.setTemporaryMessage(message.getChatId(), id);
                                    break;
                                default:
                                    sendMessage.setText(constants.ERROR_COMMAND);
                                    send(sendMessage);
                            }
                        }
                    } else {
                        if (documents.first() == null) {
                            sendMessage.setText(constants.REGISTRATION_INQUIRY);

                            sendMessage.setReplyMarkup(createRegistrationButton());

                            send(sendMessage);
                        } else {
                            Document user = (Document) documents.first();
                            String scenario = (String) user.get("scenario");
                            send(Scenario.distributor(scenario, message.getChatId(), message.getText()));
                        }
                    }
                }
            }
        }

    }

    static ReplyKeyboardMarkup createRegistrationButton() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardButton regButton = new KeyboardButton();
        regButton.setText(constants.REGISTRATION_BUTTON).setRequestContact(true);
        keyboardRow.add(regButton);
        keyboard.add(keyboardRow);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    long send(Object[] sendMessage) {
        System.out.println(sendMessage[1] != null);
        long id = 0;
        try {
            if (sendMessage[1] != null)
                execute((DeleteMessage) sendMessage[1]);
            if (sendMessage[0] != null)
                id = execute((SendMessage) sendMessage[0]).getMessageId();
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return id;
    }
    void send(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    @Override
    public String getBotUsername() {
        return "@amRoof_bot";
    }

    @Override
    public String getBotToken() {
        return "1697927258:AAGZeyNXZIe9AEtEzh-vGRDBjVzshiktQ98";
    }


}