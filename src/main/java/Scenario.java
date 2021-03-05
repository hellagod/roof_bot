import com.mongodb.Block;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;

public class Scenario {


    private static Constants constants = new Constants();

    public static Object[] distributor(String scen, long chatId, String answer) {
        Object[] message = new Object[2];
        String[] array = scen.split("\\.");
        String prefix = array[0];
        String postfix = array[1];

        System.out.println(scen + " " + answer);

        switch (prefix) {
            case "0":
                SendMessage sendMessageM = new SendMessage().setChatId(chatId);
                sendMessageM.setText("-");

                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                inlineKeyboardMarkup.setKeyboard(new BasicMenu(0).getMenu());
                sendMessageM.setReplyMarkup(inlineKeyboardMarkup);

                message[0] = sendMessageM;
                if(postfix.equals("1"))
                    message[1] = deleteInlKeyboard(chatId);
                break;
            case "1":
                switch (postfix) {
                    case "0":
                        SendMessage sendMessage0 = new SendMessage().setChatId(chatId);
                        sendMessage0.setText(constants.CHOOSE_ROLE);
                        sendMessage0.setReplyMarkup((new InlineKeyboardMarkup()).setKeyboard(addRoleMenu()));
                        nextScenarioStep(chatId, prefix + "." + 1);

                        message[0] = sendMessage0;
                        message[1] = deleteInlKeyboard(chatId);
                        break;
                    case "1":
                        SendMessage sendMessage1 = new SendMessage().setChatId(chatId);
                        sendMessage1.setText(constants.GET_PHONE);

                        createNewUser(Integer.parseInt(array[2]), chatId);
                        nextScenarioStep(chatId, prefix + "." + 2);

                        message[0] = sendMessage1;
                        message[1] = deleteInlKeyboard(chatId);
                        break;
                    case "2":
                        SendMessage sendMessage2 = new SendMessage().setChatId(chatId);
                        answer = answer.replaceAll(" ", "").replaceAll("\\+", "");
                        System.out.println(answer);
                        if (answer.matches("[-+]?\\d+") && answer.length() == 11) {
                            sendMessage2.setText(constants.GET_WORK_NAME);
                            addPhoneForNewUser(chatId, answer);
                            nextScenarioStep(chatId, prefix + "." + 3);
                        } else {
                            sendMessage2.setText(constants.ERR_PHONE);
                            nextScenarioStep(chatId, prefix + "." + 2);
                        }
                        message[0] = sendMessage2;
                        break;
                    case "3":
                        SendMessage sendMessage3 = new SendMessage().setChatId(chatId);

                        if (isUnique(answer)) {
                            String[] arr = addWorkNameForNewUser(chatId, answer);
                            StringBuilder sb = new StringBuilder(constants.ADD_USER_SUCCESS);
                            sb.replace(sb.indexOf("1"), sb.indexOf("1") + 1, "<b>" + getRole(Integer.parseInt(arr[1])) + "</b>");
                            sb.replace(sb.indexOf("2"), sb.indexOf("2") + 1, "<b>" + answer + "</b>");
                            sb.replace(sb.indexOf("3"), sb.indexOf("3") + 1, "<b>" + arr[0] + "</b>");
                            sendMessage3.setText(sb.toString());
                            sendMessage3.setParseMode("html");
                            nextScenarioStep(chatId, 0 + "." + 0);
                        } else {
                            sendMessage3.setText(constants.ERR_WORK_NAME);
                            nextScenarioStep(chatId, prefix + "." + 3);
                        }
                        message[0] = sendMessage3;
                        break;
                    case "cancel":
                        SendMessage sendMessageC = new SendMessage().setChatId(chatId);
                        sendMessageC.setText(constants.ADD_USER_CANCEL);
                        deleteUserById(chatId);
                        nextScenarioStep(chatId, 0 + "." + 0);
                        message[0] = sendMessageC;
                        break;
                }
                break;
            case "2":
                switch (postfix) {
                    case "0":
                        SendMessage sendMessage0 = new SendMessage().setChatId(chatId);
                        sendMessage0.setText(constants.CHOOSE_USER);
                        sendMessage0.setReplyMarkup((new InlineKeyboardMarkup()).setKeyboard(getAllUsers()));
                        nextScenarioStep(chatId, prefix + "." + 1);
                        message[0] = sendMessage0;
                        message[1] = deleteInlKeyboard(chatId);
                        break;
                    case "1":
                        SendMessage sendMessage1 = new SendMessage().setChatId(chatId);
                        sendMessage1.setText(constants.GET_PHONE);

                        nextScenarioStep(chatId, prefix + "." + 2);
                        deleteUserByPhone(array[2]);

                        message[0] = sendMessage1;
                        message[1] = deleteInlKeyboard(chatId);
                        break;
                }

        }

        return message;
    }

    static DeleteMessage deleteInlKeyboard(long chatId) {
        long id;
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection users = db.getCollection("users");
            id = ((Document) users.find(new Document("chat_id", chatId)).first()).getLong("temp_message");
        }
        DeleteMessage delmess = new DeleteMessage();
        delmess.setMessageId((int) id);
        delmess.setChatId(chatId);
        return delmess;
    }

    static boolean isUnique(String name) {
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection users = db.getCollection("users");
            if (users.find(new Document("work_name", name)).first() != null)
                return false;
        }
        return true;
    }

    private static List<List<InlineKeyboardButton>> addRoleMenu() {
        List<List<InlineKeyboardButton>> menu = new ArrayList<>();
        menu.add(addSingleButton(constants.ADMIN, "1.1." + constants.ADMIN_IND));
        menu.add(addSingleButton(constants.OPERATOR, "1.1." + constants.OPERATOR_IND));
        menu.add(addSingleButton(constants.GUIDE, "1.1." + constants.GUIDE_IND));
        menu.add(addSingleButton(constants.PROMOTER, "1.1." + constants.OPERATOR_IND));
        return menu;
    }

    private static List<List<InlineKeyboardButton>> getAllUsers() {
        List<List<InlineKeyboardButton>> menu = new ArrayList<>();
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection users = db.getCollection("users");
            FindIterable iterable = users.find();
            iterable.forEach(new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    menu.add(addSingleButton(document.get("work_name") + " - " +
                                    getRole(document.getInteger("role")) + "\n+" + document.get("phone"),
                            "2.1." + document.getString("phone")));
                }
            });

        }
        return menu;
    }

    static List<InlineKeyboardButton> addSingleButton(String name, String data) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(name);
        button.setCallbackData(data);
        row.add(button);
        return row;
    }

    public static void nextScenarioStep(long chatId, String sc) {
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection users = db.getCollection("users");
            users.updateOne(new Document("chat_id", chatId),
                    new Document("$set", new Document("scenario", sc)));
//            for (int i = 0; i < size; i++) {
//                users.updateOne(new Document("chat_id", chatId),
//                        new Document("$set", new Document(revisions[i][0], revisions[i][1])));
//            }
        }
    }

    static void setTemporaryMessage(long chatId, long id) {
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection users = db.getCollection("users");
            users.updateOne(new Document("chat_id", chatId),
                    new Document("$set", new Document("temp_message", id)));
        }
    }

    static void deleteUserById(long chatId) {
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection users = db.getCollection("users");
            users.deleteMany(new Document("benchmark", chatId));
        }
    }

    static void deleteUserByPhone(String phone) {
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection users = db.getCollection("users");
            users.deleteMany(new Document("phone", phone));
        }
    }

    static void createNewUser(int role, long father_user_chatId) {
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection users = db.getCollection("users");
            Map<String, Object> map = new HashMap<>();
            map.put("_id", new ObjectId());
            map.put("role", role);
            map.put("benchmark", father_user_chatId);
            users.insertOne(new Document(map));
        }
    }


    static void addPhoneForNewUser(long chatId, String phone) {
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection users = db.getCollection("users");
            phone = phone.substring(phone.indexOf('7'), phone.indexOf('7') + 11);
            users.updateOne(new Document("benchmark", chatId),
                    new Document("$set", new Document("phone", phone)));
        }
    }

    static String[] addWorkNameForNewUser(long chatId, String name) {
        String arr[] = new String[2];
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection users = db.getCollection("users");
            Map<String, Object> map0 = new HashMap<>();
            map0.put("work_name", name);
            map0.put("added_by", chatId);
            Map<String, Object> map = new HashMap<>();
            map.put("$set", map0);
            map.put("$unset", new Document("benchmark", chatId));

            Document user = (Document) users.find(new Document("benchmark", chatId)).first();
            arr[0] = (String) user.get("phone");
            arr[1] = "" + user.get("role");

            users.updateOne(new Document("benchmark", chatId),
                    new Document(map));

        }
        return arr;
    }

    public static String getRole(int id) {
        if (id == constants.ADMIN_IND)
            return constants.ADMIN;
        else if (id == constants.OPERATOR_IND)
            return constants.OPERATOR;
        else if (id == constants.GUIDE_IND)
            return constants.GUIDE;
        else if (id == constants.PROMOTER_IND)
            return constants.PROMOTER;
        return " - ";
    }

}

