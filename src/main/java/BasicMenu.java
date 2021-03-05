import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class BasicMenu {

    private List<List<InlineKeyboardButton>> menu;
    private Constants constants = new Constants();

    public BasicMenu(int role){
        menu = new ArrayList<>();
        if(constants.ADMIN_IND == role){
            menu.add(addSingleButton("Добавить пользователя", "1.0"));
            menu.add(addSingleButton("Удалить пользователя", "2.0"));
        }
    }

    public List<InlineKeyboardButton> addSingleButton(String name, String data){
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(name);
        button.setCallbackData(data);
        row.add(button);
        return row;
    }

    public List<List<InlineKeyboardButton>> getMenu(){
        return menu;
    }
}
