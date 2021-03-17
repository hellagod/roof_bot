import com.mongodb.Block;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class Scenario {

    private static Constants constants = new Constants();

    static Object[] distributor(String scen, long chatId, String answer, String mess_for_rec) {
        Object[] message = new Object[3];
        String[] array = scen.split("\\.");
        String prefix = array[0];
        String postfix = array[1];


        LOG.log(scen + ": " + answer);
        switch (prefix) {
            case "sign_up":
                switch (postfix) {
                    case "0":
                        SendMessage sendMessageS0 = new SendMessage().setChatId(chatId);
                        sendMessageS0.setText(constants.REGISTRATION_INQUIRY);

                        sendMessageS0.setReplyMarkup(createRegistrationButton());

                        message[0] = sendMessageS0;
                        break;
                }
                break;
            case "0":
                SendMessage sendMessageM = new SendMessage().setChatId(chatId);
                sendMessageM.setText(mess_for_rec);
                sendMessageM.setParseMode("html");

                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                inlineKeyboardMarkup.setKeyboard(new BasicMenu(0).getMenu());
                sendMessageM.setReplyMarkup(inlineKeyboardMarkup);

                message[0] = sendMessageM;
                if (postfix.equals("1") || postfix.equals("cancel"))
                    message[1] = deleteInlKeyboard(chatId);
                break;
            case "1":
                switch (postfix) {
                    case "0":
                        SendMessage sendMessage0 = new SendMessage().setChatId(chatId);
                        sendMessage0.setText(constants.CHOOSE_ROLE);
                        sendMessage0.setReplyMarkup((new InlineKeyboardMarkup()).setKeyboard(addRoleMenu(prefix)));
                        nextScenarioStep(chatId, prefix + "." + 1);
                        message[0] = sendMessage0;
                        message[1] = deleteInlKeyboard(chatId);
                        break;
                    case "1":
                        SendMessage sendMessage1 = new SendMessage().setChatId(chatId);
                        sendMessage1.setText(constants.GET_PHONE);
                        if (array.length < 3)
                            message = distributor(prefix + ".cancel", chatId, "", "");
                        else {
                            createNewUser(Integer.parseInt(array[2]), chatId);
                            nextScenarioStep(chatId, prefix + "." + 2);

                            message[0] = sendMessage1;
                            message[1] = deleteInlKeyboard(chatId);
                        }
                        break;
                    case "2":
                        SendMessage sendMessage2 = new SendMessage().setChatId(chatId);
                        answer = answer.replaceAll(" ", "").replaceAll("\\+", "");
                        if (answer.matches("[-+]?\\d+") && answer.length() == 11) {
                            if (!addPhoneForNewUser(chatId, answer)) {
                                sendMessage2.setText(constants.GET_WORK_NAME);
                                nextScenarioStep(chatId, prefix + "." + 3);
                            } else {
                                sendMessage2.setText(constants.ERR_PHONE_EXIST);
                                Object[] message0 = distributor(prefix + ".cancel", chatId, answer, "");
                                message[1] = message0[1];
                                message[2] = message0[0];
                            }
                        } else {
                            sendMessage2.setText(constants.ERR_PHONE);
                        }
                        message[0] = sendMessage2;
                        break;
                    case "3":
                        SendMessage sendMessage3 = new SendMessage().setChatId(chatId);

                        if (isUnique(answer)) {
                            String[] arr = addWorkNameForNewUser(chatId, answer);

                            StringBuilder sb = new StringBuilder(constants.ADD_USER_SUCCESS);
                            sb.replace(sb.indexOf("1"), sb.indexOf("1") + 1, "<b>"
                                    + getRole(Integer.parseInt(arr[1])) + "</b>");
                            sb.replace(sb.indexOf("2"), sb.indexOf("2") + 1, "<b>" + answer + "</b>");
                            sb.replace(sb.indexOf("3"), sb.indexOf("3") + 1, "<b>" + arr[0] + "</b>");

                            nextScenarioStep(chatId, 0 + "." + 0);

                            Object[] message0 = distributor(0 + "." + 0, chatId, answer, "меню");

                            sendMessage3.setText(sb.toString());
                            sendMessage3.setParseMode("html");

                            message[0] = sendMessage3;
                            message[2] = message0[0];
                        } else {
                            sendMessage3.setText(constants.ERR_WORK_NAME);
                            nextScenarioStep(chatId, prefix + "." + 3);
                            message[0] = sendMessage3;
                        }
                        break;
                    case "cancel":
                        deleteUserById(chatId);
                        nextScenarioStep(chatId, 0 + "." + 0);
                        message = distributor(0 + "." + 1, chatId, answer, constants.ADD_USER_CANCEL);
                        break;
                }
                break;
            case "2":
                switch (postfix) {
                    case "0":
                        SendMessage sendMessage0 = new SendMessage().setChatId(chatId);
                        sendMessage0.setText(constants.CHOOSE_USER);
                        sendMessage0.setReplyMarkup((new InlineKeyboardMarkup()).setKeyboard(getAllUsers(prefix)));
                        nextScenarioStep(chatId, prefix + "." + 1);
                        message[0] = sendMessage0;
                        message[1] = deleteInlKeyboard(chatId);
                        break;
                    case "1":
                        SendMessage sendMessage1 = new SendMessage().setChatId(chatId);

                        if (array.length < 3)
                            message = distributor(prefix + ".cancel", chatId, "", "");
                        else {
                            String[] arr = getUserInf(array[2], chatId);

                            nextScenarioStep(chatId, prefix + "." + 2);

                            StringBuilder sb = new StringBuilder(constants.USER_INF);
                            sb.replace(sb.indexOf("1"), sb.indexOf("1") + 1, "<b>"
                                    + getRole(Integer.parseInt(arr[1])) + "</b>");
                            sb.replace(sb.indexOf("2"), sb.indexOf("2") + 1, "<b>" + arr[2] + "</b>");
                            sb.replace(sb.indexOf("3"), sb.indexOf("3") + 1, "<b>" + arr[0] + "</b>");
                            sendMessage1.setText(sb.toString());
                            sendMessage1.setParseMode("html");

                            sendMessage1.setReplyMarkup(createConfirmationButton());

                            message[0] = sendMessage1;
                            message[1] = deleteInlKeyboard(chatId);
                        }
                        break;
                    case "2":
                        SendMessage sendMessage2 = new SendMessage().setChatId(chatId);
                        nextScenarioStep(chatId, 0 + "." + 0);
                        deleteUserByPhone(chatId);
                        undoDelete(chatId);

                        ReplyKeyboardRemove removeKeyboard = new ReplyKeyboardRemove();
                        removeKeyboard.setSelective(true);
                        sendMessage2.setReplyMarkup(removeKeyboard);

                        sendMessage2.setText(constants.DEL_USER_SUCCESS);

                        Object[] message0 = distributor(0 + "." + 1, chatId, answer, "меню");

                        message[0] = sendMessage2;
                        message[1] = message0[1];
                        message[2] = message0[0];
                        break;
                    case "cancel":
                        undoDelete(chatId);
                        nextScenarioStep(chatId, 0 + "." + 1);
                        message = distributor(0 + "." + 1, chatId, answer, constants.DEL_USER_CANCEL);
                        break;
                }
                break;
            case "3":
                switch (postfix) {
                    case "0":
                        break;
                }
                break;
            case "4":
                switch (postfix) {
                    case "0":
                        SendMessage sendMessage0 = new SendMessage().setChatId(chatId);
                        sendMessage0.setText(constants.CHOOSE_ROLE);
                        sendMessage0.setReplyMarkup((new InlineKeyboardMarkup()).setKeyboard(getAllReservations()));
                        nextScenarioStep(chatId, prefix + "." + 1);
                        message[0] = sendMessage0;
                        message[1] = deleteInlKeyboard(chatId);
                        break;
                    case "cancel":
                        nextScenarioStep(chatId, 0 + "." + 1);
                        message = distributor(0 + "." + 1, chatId, answer, "меню");
                        break;
                }
                break;
            case "5":
                switch (postfix) {
                    case "0":
                        break;
                }
                break;
            case "6":
                switch (postfix) {
                    case "0":
                        SendMessage sendMessage0 = new SendMessage().setChatId(chatId);
                        sendMessage0.setText("Выберете способ ввода:");
                        sendMessage0.setReplyMarkup((new InlineKeyboardMarkup()).setKeyboard(addInputType()));
                        nextScenarioStep(chatId, prefix + "." + 2);
                        message[0] = sendMessage0;
                        message[1] = deleteInlKeyboard(chatId);
                        break;
                    case "1":
                        if (array.length < 3)
                            message = distributor(prefix + ".cancel", chatId, "", "");
                        else
                            switch (array[2]) {
                                case "0":
                                    SendMessage sendMessage10 = new SendMessage().setChatId(chatId);
                                    sendMessage10.setText("Введите номер телефона:");
                                    nextScenarioStep(chatId, prefix + "." + postfix + "." + 1);
                                    message[0] = sendMessage10;
                                    message[1] = deleteInlKeyboard(chatId);
                                    break;
                                case "1":
                                    SendMessage sendMessage11 = new SendMessage().setChatId(chatId);
                                    answer = answer.replaceAll(" ", "").replaceAll("\\+", "");
                                    if (answer.matches("[-+]?\\d+") && answer.length() == 11) {
                                        sendMessage11.setText("Укажите количество человек и стоимость <количество>*<стоимость>");
                                        createNewReservation(answer, chatId);
                                        nextScenarioStep(chatId, prefix + "." + postfix + "." + 2);
                                    } else {
                                        sendMessage11.setText("Некоректный номер, попробуйте еще раз:");
                                    }
                                    message[0] = sendMessage11;
                                    break;
                                case "2":
                                    SendMessage sendMessage12 = new SendMessage().setChatId(chatId);
                                    answer = answer.replaceAll(" ", "");
                                    if (isCost(answer)) {
                                        addCostToReservation(answer, chatId);
                                        sendMessage12.setText("Выберите место:");
                                        sendMessage12.setReplyMarkup((new InlineKeyboardMarkup()).
                                                setKeyboard(getAllVenues(prefix)));
                                        nextScenarioStep(chatId, prefix + "." + postfix + "." + 3);
                                    } else {
                                        sendMessage12.setText("Некоректный формат ввода, попробуйте еще раз:");
                                    }
                                    message[0] = sendMessage12;
                                    break;
                                case "3":
                                    SendMessage sendMessage13 = new SendMessage().setChatId(chatId);
                                    if (array.length < 4)
                                        message = distributor(prefix + ".cancel", chatId, "", "");
                                    else {
                                        addVenuesToReservation(array[3], chatId);
                                        sendMessage13.setText("Выберите промежуток времени:");
                                        sendMessage13.setReplyMarkup((new InlineKeyboardMarkup()).
                                                setKeyboard(getAllPeriods(prefix)));
                                        nextScenarioStep(chatId, prefix + "." + postfix + "." + 4);
                                        message[0] = sendMessage13;
                                        message[1] = deleteInlKeyboard(chatId);
                                    }
                                    break;
                                case "4":
                                    SendMessage sendMessage14 = new SendMessage().setChatId(chatId);
                                    if (array.length < 4)
                                        message = distributor(prefix + ".cancel", chatId, "", "");
                                    else {
                                        addTimeToReservation(array[3], chatId);
                                        sendMessage14.setText("Напишите дату в формате dd.mm.yy:");
                                        nextScenarioStep(chatId, prefix + "." + postfix + "." + 5);
                                        message[0] = sendMessage14;
                                        message[1] = deleteInlKeyboard(chatId);
                                    }
                                    break;
                                case "5":
                                    SendMessage sendMessage15 = new SendMessage().setChatId(chatId);
                                    if (isDate(answer)) {
                                        String[] arr = addDateToReservation(answer.replaceAll(" ", ""), chatId);

                                        Object[] message0 = distributor(0 + "." + 0, chatId, answer, "меню");

                                        sendMessage15.setText(Arrays.toString(arr));
                                        sendMessage15.setParseMode("html");

                                        message[0] = sendMessage15;
                                        message[2] = message0[0];
                                        nextScenarioStep(chatId, 0 + "." + 0);
                                    } else {
                                        sendMessage15.setText("Некоректная дата, попробуйте еще раз:");
                                    }

                                    message[0] = sendMessage15;
                                    break;
                            }

                        break;
                    case "2":
                        SendMessage sendMessage2 = new SendMessage().setChatId(chatId);
                        sendMessage2.setText(constants.GET_PHONE);
                        if (array.length < 3)
                            message = distributor(prefix + ".cancel", chatId, "", "");
                        else
                            switch (array[2]) {
                                case "0":
                                    SendMessage sendMessage20 = new SendMessage().setChatId(chatId);
                                    sendMessage20.setText("Введите заказы одним сообщением в формате:\n" +
                                            "<b>&lt;дата&gt; &lt;время&gt;\n&lt;количество людей&gt;*&lt;стоимость&gt;" +
                                            "\n&lt;номер телефона&gt;</b>");
                                    sendMessage20.setParseMode("html");
                                    nextScenarioStep(chatId, prefix + "." + postfix + "." + 1);
                                    message[0] = sendMessage20;
                                    message[1] = deleteInlKeyboard(chatId);
                                    break;
                                case "1":
                                    SendMessage sendMessage21 = new SendMessage().setChatId(chatId);
                                    String[] pars = parseArrayOfReservations(answer, chatId);
                                    if (Integer.parseInt(pars[0]) > 0) {

                                        StringBuilder sb = new StringBuilder("Все заявки добавленны (в количестве: 1)");
                                        sb.replace(sb.indexOf("1"), sb.indexOf("1") + 1, pars[0]);
                                        Object[] message0 = distributor(0 + "." + 0, chatId, answer, "меню");

                                        sendMessage21.setText(String.valueOf(sb));

                                        message[0] = sendMessage21;
                                        message[2] = message0[0];

                                        nextScenarioStep(chatId, 0 + "." + 0);
                                    } else {
                                        sendMessage21.setText(pars[1] + " Исправьте ошибку и попробуйте еще раз:");
                                        message[0] = sendMessage21;
                                    }
                                    break;
                            }
                        break;
                    case "cancel":
                        nextScenarioStep(chatId, 0 + "." + 1);
                        deleteReservation(chatId);
                        message = distributor(0 + "." + 1, chatId, answer, "Запрос на добавление отменен");
                        break;
                }
                break;
        }

        return message;
    }

    private static ArrayList<String> getArrayOfPeriods() {
        ArrayList<String> ans = new ArrayList<>();
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection periods = db.getCollection("periods");
            FindIterable iterable = periods.find();
            iterable.forEach((Block<Document>) document -> ans.add(document.getString("name")));
        }
        return ans;
    }

    private static String[] parseArrayOfReservations(String str, long chatId) {
        int size = 0;
        String[] lines = str.split("\n");
        String[] ans = new String[2];
        ans[0] = "0";
        int i = 0;
        int j = 0;
        ArrayList<String> periods = getArrayOfPeriods();
        ArrayList<Document> reservations = new ArrayList<>();
        HashMap<String, Object> map = new HashMap<>();
        while (i != lines.length) {
            if (!lines[i].replaceAll(" ", "").equals("")) {
                String[] words = lines[i].split(" ");
                if (j == 0) {
                    for (String word : words)
                        if (!word.equals(""))
                            if (map.size() == 0)
                                if (word.contains(".") && word.length() == 5)
                                    map.put("date", word);
                                else {
                                    ans[1] = "Ошибка входных данных (" + (size + 1) + " заказ, " + (i + 1) + " строка, " +
                                            "неверный формат ввода даты)";
                                    return ans;
                                }
                            else if (map.size() == 1)
                                if (periods.contains(word))
                                    map.put("period", word);
                                else {
                                    ans[1] = "Ошибка входных данных (" + (size + 1) + " заказ, " + (i + 1) +
                                            " строка, неверно указано время)";
                                    return ans;
                                }
                    if (map.size() != 2) {
                        ans[1] = "Ошибка входных данных (" + (i + 1) + " строка)";
                        return ans;
                    }
                    j++;
                } else if (j == 1) {
                    for (String word : words)
                        if (!word.equals(""))
                            if (isCost(word)) {
                                String[] s = word.split("\\*");
                                map.put("cost", Integer.parseInt(s[1]));
                                map.put("number_of_visitors", Integer.parseInt(s[0]));
                            } else {
                                ans[1] = "Ошибка входных данных (" + (size + 1) + " заказ, " + (i + 1) + " строка, " +
                                        "неверный формат ввода количества человек и цены)";
                                return ans;
                            }
                    if (map.size() != 4) {
                        ans[1] = "Ошибка входных данных (" + (i + 1) + " строка)";
                        return ans;
                    }
                    j++;
                } else if (j == 2) {
                    for (String word : words)
                        if (!word.equals("")) {
                            word = word.replaceAll("\\+", "");
                            if (word.matches("[-+]?\\d+") && word.length() == 11)
                                map.put("phone", word);
                            else {
                                ans[1] = "Ошибка входных данных (" + (size + 1) + " заказ, " + (i + 1) +
                                        " строка, неверно указан телефон)";
                                return ans;
                            }
                        }
                    if (map.size() != 5) {
                        ans[1] = "Ошибка входных данных (" + (i + 1) + " строка)";
                        return ans;
                    } else {
                        reservations.add(new Document(map));
                        map = new HashMap<>();
                        size++;
                    }
                    j = 0;
                }
            }
            i++;
        }
        ans[0] = size + "";

        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection collection = db.getCollection("reservations");
            collection.insertMany(reservations);
        }
        return ans;
    }

    private static boolean isDate(String date) {
        SimpleDateFormat format = new java.text.SimpleDateFormat("dd.MM.YY");
        try {
            LOG.log(date);
            format.parse(date.replaceAll(" ", ""));
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private static String[] addDateToReservation(String date, long chatId) {
        String[] arr = new String[5];
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection reservations = db.getCollection("reservations");
            Map<String, Object> map = new HashMap<>();
            map.put("$set", new Document("date", date));
            map.put("$unset", new Document("benchmark", chatId));
            Document reservation = (Document) reservations.find(new Document("benchmark", chatId)).first();
            assert reservation != null;
            arr[0] = reservation.getInteger("number_of_visitors") + "*" + reservation.getInteger("cost");
            arr[1] = reservation.getString("phone");
            arr[2] = date;
            arr[3] = db.getCollection("venues").find(new Document("_id", reservation.
                    getObjectId("venue"))).first().getString("name");
            arr[4] = db.getCollection("periods").find(new Document("_id", reservation.
                    getObjectId("period"))).first().getString("name");
            reservations.updateOne(new Document("benchmark", chatId), new Document(map));
        }
        return arr;
    }

    private static void deleteReservation(long chatId) {
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection reservations = db.getCollection("reservations");
            reservations.deleteMany(new Document("benchmark", chatId));
        }
    }

    private static void addTimeToReservation(String name, long chatId) {
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection periods = db.getCollection("periods");
            MongoCollection reservations = db.getCollection("reservations");
            Document period = (Document) periods.find(new Document("name", name)).first();
            Map<String, Object> map = new HashMap<>();
            assert period != null;
            map.put("period", period.getObjectId("_id"));
            reservations.updateOne(new Document("benchmark", chatId), new Document("$set", map));
        }
    }

    private static boolean isCost(String cost) {
        int first = cost.indexOf("*");
        int last = cost.lastIndexOf("*");
        cost = cost.replaceAll("\\*", "");
        return first != -1 && first == last && cost.matches("[-+]?\\d+");
    }

    private static void addCostToReservation(String cost, long chatId) {
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection reservations = db.getCollection("reservations");
            Map<String, Object> map = new HashMap<>();
            String[] s = cost.split("\\*");
            map.put("cost", Integer.parseInt(s[1]));
            map.put("number_of_visitors", Integer.parseInt(s[0]));
            reservations.updateOne(new Document("benchmark", chatId), new Document("$set", map));
        }
    }


    private static void addVenuesToReservation(String name, long chatId) {
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection venues = db.getCollection("venues");
            MongoCollection reservations = db.getCollection("reservations");
            Document venue = (Document) venues.find(new Document("name", name)).first();
            Map<String, Object> map = new HashMap<>();
            assert venue != null;
            map.put("venue", venue.getObjectId("_id"));
            map.put("added_by", chatId);
            reservations.updateOne(new Document("benchmark", chatId), new Document("$set", map));
        }
    }

    private static List<List<InlineKeyboardButton>> getAllVenues(String prefix) {
        List<List<InlineKeyboardButton>> menu = new ArrayList<>();
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection venues = db.getCollection("venues");
            FindIterable iterable = venues.find();
            iterable.forEach((Block<Document>) document -> menu.add(addSingleButton(document.getString("name"),
                    prefix + ".1.3." + document.getString("name"))));
        }
        return menu;
    }

    private static List<List<InlineKeyboardButton>> getAllPeriods(String prefix) {
        List<List<InlineKeyboardButton>> menu = new ArrayList<>();
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection periods = db.getCollection("periods");
            FindIterable iterable = periods.find();
            iterable.forEach((Block<Document>) document -> menu.add(addSingleButton(document.getString("name"),
                    prefix + ".1.4." + document.getString("index"))));
        }
        return menu;
    }

    private static void createNewReservation(String phone, long father_user_chatId) {
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection reservations = db.getCollection("reservations");
            Map<String, Object> map = new HashMap<>();
            map.put("_id", new ObjectId());
            map.put("phone", phone);
            map.put("short_phone", getShortPhone(phone));
            map.put("benchmark", father_user_chatId);
            reservations.insertOne(new Document(map));
        }
    }

    private static List<List<InlineKeyboardButton>> addInputType() {
        List<List<InlineKeyboardButton>> menu = new ArrayList<>();
        menu.add(addSingleButton("ввод каждого параметра по отдельности", "6.1.0"));
        menu.add(addSingleButton("ввод по шаблону (можно сразу несколько)", "6.2.0"));
        return menu;
    }

    private static ReplyKeyboardMarkup createConfirmationButton() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardButton buttonYes = new KeyboardButton();
        KeyboardButton buttonCancel = new KeyboardButton();
        buttonYes.setText(constants.BUTTON_OK);
        buttonCancel.setText(constants.BUTTON_CANCEL);
        keyboardRow.add(buttonYes);
        keyboardRow.add(buttonCancel);
        keyboard.add(keyboardRow);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    private static DeleteMessage deleteInlKeyboard(long chatId) {
        long id;
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection users = db.getCollection("users");
            id = ((Document) users.find(new Document("chat_id", chatId)).first()).getLong("temp_message");
            users.updateOne(new Document("chat_id", chatId), new Document("$set",
                    new Document("temp_message", -1L)));
        }
        if (id == -1)
            return null;
        DeleteMessage delmess = new DeleteMessage();
        delmess.setMessageId((int) id);
        delmess.setChatId(chatId);
        return delmess;
    }

    private static boolean isUnique(String name) {
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection users = db.getCollection("users");
            if (users.find(new Document("work_name", name)).first() != null)
                return false;
        }
        return true;
    }

    private static List<List<InlineKeyboardButton>> addRoleMenu(String prefix) {
        List<List<InlineKeyboardButton>> menu = new ArrayList<>();
        menu.add(addSingleButton(constants.ADMIN, prefix + ".1." + constants.ADMIN_IND));
        menu.add(addSingleButton(constants.OPERATOR, prefix + ".1." + constants.OPERATOR_IND));
        menu.add(addSingleButton(constants.GUIDE, prefix + ".1." + constants.GUIDE_IND));
        menu.add(addSingleButton(constants.PROMOTER, prefix + ".1." + constants.PROMOTER_IND));
        return menu;
    }

    private static List<List<InlineKeyboardButton>> getAllUsers(String prefix) {
        List<List<InlineKeyboardButton>> menu = new ArrayList<>();
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection users = db.getCollection("users");
            FindIterable iterable = users.find();
            iterable.forEach((Block<Document>) document -> menu.add(addSingleButton(document.get("work_name") + " - " +
                            getRole(document.getInteger("role")) + "\n+" + document.get("phone"),
                    prefix + ".1." + document.getString("phone"))));

        }
        return menu;
    }

    private static List<List<InlineKeyboardButton>> getAllReservations() {
        List<List<InlineKeyboardButton>> menu = new ArrayList<>();
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection reservations = db.getCollection("reservations");
            FindIterable iterable = reservations.find();
            int i = 0;
            iterable.forEach((Block<Document>) document -> {
                menu.add(addReservationB(document.get("name") + " - " +
                        getShortPhone(document.getString("phone")), false, i));
            });

        }
        return menu;
    }

    private static String getShortPhone(String s) {
        return s.substring(s.length() - 4);
    }

    private static List<InlineKeyboardButton> addReservationB(String name, boolean performed, int i) {
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText(name);
        button1.setCallbackData("0");

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText((performed ? "✅" : "☑"));
        button2.setCallbackData("1");

        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText("❌");
        button3.setCallbackData("2");

        row.add(button1);
        row.add(button2);
        row.add(button3);

        return row;
    }

    private static List<InlineKeyboardButton> addSingleButton(String name, String data) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(name);
        button.setCallbackData(data);
        row.add(button);
        return row;
    }

    private static void nextScenarioStep(long chatId, String sc) {
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection users = db.getCollection("users");
            users.updateOne(new Document("chat_id", chatId),
                    new Document("$set", new Document("scenario", sc)));
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

    private static void deleteUserById(long chatId) {
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection users = db.getCollection("users");
            users.deleteMany(new Document("benchmark", chatId));
        }
    }

    private static ReplyKeyboardMarkup createRegistrationButton() {
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

    private static void deleteUserByPhone(long chatId) {
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection users = db.getCollection("users");
            Document user = (Document) users.find(new Document("chat_id", chatId)).first();
            assert user != null;
            String phone = user.getString("del_user");
            users.deleteMany(new Document("phone", phone));
        }
    }

    private static void undoDelete(long chatId) {
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection users = db.getCollection("users");
            users.updateOne(new Document("chat_id", chatId), new Document("$unset", new Document("del_user", "")));
        }
    }

    private static void createNewUser(int role, long father_user_chatId) {
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection users = db.getCollection("users");
            Map<String, Object> map = new HashMap<>();
            map.put("_id", new ObjectId());
            map.put("role", role);
            map.put("benchmark", father_user_chatId);
            map.put("temp_message", -1L);
            users.insertOne(new Document(map));
        }
    }


    private static boolean addPhoneForNewUser(long chatId, String phone) {
        boolean already_exist = false;
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection users = db.getCollection("users");
            if (users.find(new Document("phone", phone)).first() == null) {
                users.updateOne(new Document("benchmark", chatId),
                        new Document("$set", new Document("phone", phone)));
            } else
                already_exist = true;
        }
        return already_exist;
    }

    static void FFFFF() {
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection periods = db.getCollection("periods");
            for (int i = 1; i < 7; i++) {

                Map<String, Object> map = new HashMap<>();
                map.put("_id", new ObjectId());
                map.put("name", "" + i);
                periods.insertOne(new Document(map));
            }
        }
    }

    private static String[] addWorkNameForNewUser(long chatId, String name) {
        String[] arr = new String[2];
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
            assert user != null;
            arr[0] = (String) user.get("phone");
            arr[1] = "" + user.get("role");

            users.updateOne(new Document("benchmark", chatId),
                    new Document(map));

        }
        return arr;
    }

    private static String[] getUserInf(String phone, long chatId) {
        String[] arr = new String[3];
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection users = db.getCollection("users");

            Document user = (Document) users.find(new Document("phone", phone)).first();
            assert user != null;
            arr[0] = (String) user.get("phone");
            arr[1] = "" + user.get("role");
            String name = user.getString("name");
            arr[2] = user.getString("work_name") + (name == null ||
                    name.equals("") ? "" : " (" + name + ")");

            users.updateOne(new Document("chat_id", chatId),
                    new Document("$set", new Document("del_user", phone)));
        }
        return arr;
    }

    static String getRole(int id) {
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

