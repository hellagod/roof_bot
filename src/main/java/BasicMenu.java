import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class BasicMenu {

    private List<List<InlineKeyboardButton>> menu;

    BasicMenu(int role){
        menu = new ArrayList<>();
        Constants constants = new Constants();
        if(constants.ADMIN_IND == role){
            menu.add(addSingleButton("Добавить пользователя", "1.0"));
            menu.add(addSingleButton("Удалить пользователя", "2.0"));
            menu.add(addSingleButton("-----", "4.0"));
            menu.add(addSingleButton("Добавить бронь", "6.0"));
        }
    }

    private List<InlineKeyboardButton> addSingleButton(String name, String data){
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(name);
        button.setCallbackData(data);
        row.add(button);
        return row;
    }

    List<List<InlineKeyboardButton>> getMenu(){
        return menu;
    }
}
