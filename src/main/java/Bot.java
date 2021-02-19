import com.mongodb.client.*;
import org.bson.Document;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

public class Bot extends TelegramLongPollingBot{

private static Constants constants=new Constants();

@Override
public void onUpdateReceived(Update update){
        update.getUpdateId();
        Message message=update.getMessage();

        if(message.hasContact()){
        Contact contact=message.getContact();
        try(MongoClient mongoClient=MongoClients.create()){
        MongoDatabase db=mongoClient.getDatabase("test");
        MongoCollection users=db.getCollection("users");
        FindIterable documents=users.find(new Document("phone",Long.parseLong(contact.getPhoneNumber())));
        Document user=(Document)documents.first();
        if(user!=null){
        //String name = contact.getFirstName() + (contact.getLastName() != null ? " " + contact.getLastName() : "");
        users.updateOne(new Document("phone",user.get("phone")),
        new Document("$set",new Document("chat_id",contact.getUserID())));

        SendMessage sendMessage=new SendMessage().setChatId(message.getChatId());
        StringBuilder sb=new StringBuilder(constants.REGISTRATION_PERFORMED);
        sb.replace(sb.indexOf("1"),sb.indexOf("1")+1,"<b>"+getRole(user.getInteger("role"))+"</b>");
        sb.replace(sb.indexOf("2"),sb.indexOf("2")+1,"<b>"+user.getString("work_name")+"</b>");
        sendMessage.setText(sb.toString());
        sendMessage.setParseMode("html");
        try{
        execute(sendMessage);
        }catch(TelegramApiException e){
        e.printStackTrace();
        }
        }else{
        SendMessage sendMessage=new SendMessage().setChatId(message.getChatId());
        sendMessage.setText(constants.REGISTRATION_ID_NOT_FOUND);
        try{
        execute(sendMessage);
        }catch(TelegramApiException e){
        e.printStackTrace();
        }
        }
        }
        }else{
        SendMessage sendMessage=new SendMessage().setChatId(message.getChatId());

        switch(message.getText().toLowerCase()){
        case"reg":
        sendMessage.setText(constants.REGISTRATION_INQUIRY);
        createRegistrationButton(sendMessage);
        try{
        execute(sendMessage);
        }catch(TelegramApiException e){
        e.printStackTrace();
        }
        break;
        case"getid":
        sendMessage.setText(String.valueOf(message.getContact()));
        try{
        execute(sendMessage);
        }catch(TelegramApiException e){
        e.printStackTrace();
        }
        break;
        }
        }

        }

static void createRegistrationButton(SendMessage sendMessage){
        ReplyKeyboardMarkup replyKeyboardMarkup=new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> keyboard=new ArrayList<>();
        KeyboardRow keyboardRow=new KeyboardRow();
        KeyboardButton regButton=new KeyboardButton();
        regButton.setText(constants.REGISTRATION_BUTTON).setRequestContact(true);
        keyboardRow.add(regButton);
        keyboard.add(keyboardRow);
        replyKeyboardMarkup.setKeyboard(keyboard);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        }

static String getRole(int id){
        switch(id){
        case 0:
        return constants.ADMIN;
        case 1:
        return constants.OPERATOR;
        case 2:
        return constants.GUIDE;
default:
        return"";
        }
        }

public static SendMessage sendInlineKeyBoardMessage(long chatId){
        InlineKeyboardMarkup inlineKeyboardMarkup=new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1=new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2=new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Тык");
        inlineKeyboardButton1.setCallbackData("Button \"Тык\" has been pressed");
        inlineKeyboardButton2.setText("Тык2");
        inlineKeyboardButton2.setCallbackData("Button \"Тык2\" has been pressed");
        List<InlineKeyboardButton> keyboardButtonsRow1=new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2=new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText("Fi4a").setCallbackData("CallFi4a"));
        keyboardButtonsRow2.add(inlineKeyboardButton2);
        List<List<InlineKeyboardButton>>rowList=new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return new SendMessage().setChatId(chatId).setText("Примерaafddddddddddddddddddddddddddddddddddd").setReplyMarkup(inlineKeyboardMarkup);
        }

@Override
public String getBotUsername(){
        return"@amRoof_bot";
        }

@Override
public String getBotToken(){
        return"1697927258:AAGZeyNXZIe9AEtEzh-vGRDBjVzshiktQ98";
        }


        }