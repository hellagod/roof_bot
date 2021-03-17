import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

class Constants {
    String REGISTRATION_INQUIRY, REGISTRATION_BUTTON, REGISTRATION_ID_NOT_FOUND,
            REGISTRATION_PERFORMED, ADMIN, OPERATOR, PROMOTER, GUIDE, ERROR_COMMAND,
            GET_PHONE, ERR_PHONE, ERR_PHONE_EXIST, GET_WORK_NAME, ADD_USER_SUCCESS, CHOOSE_ROLE,
            ADD_USER_CANCEL, ERR_WORK_NAME, CHOOSE_USER, USER_INF, BUTTON_OK, BUTTON_CANCEL,
            DEL_USER_SUCCESS, DEL_USER_CANCEL;
    int ADMIN_IND, OPERATOR_IND, PROMOTER_IND, GUIDE_IND;
    Constants(){
        FileInputStream fileInputStream;
        Properties property = new Properties();

        try {
            fileInputStream = new FileInputStream("src/main/resources/string.properties");
            property.load(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8));

            REGISTRATION_INQUIRY = property.getProperty("registration.inquiry");
            REGISTRATION_BUTTON = property.getProperty("registration.button");
            REGISTRATION_ID_NOT_FOUND = property.getProperty("registration.error.id_not_found");
            REGISTRATION_PERFORMED = property.getProperty("registration.performed");
            ADMIN = property.getProperty("users.role.admin");
            OPERATOR = property.getProperty("users.role.operator");
            PROMOTER = property.getProperty("users.role.promoter");
            GUIDE = property.getProperty("users.role.guide");
            ERROR_COMMAND = property.getProperty("errors.command_not_found");

            GET_PHONE = property.getProperty("scenarios.add_new_user.phone.get");
            GET_WORK_NAME = property.getProperty("scenarios.add_new_user.work_name.get");
            ERR_PHONE = property.getProperty("scenarios.add_new_user.phone.error");
            ERR_PHONE_EXIST = property.getProperty("scenarios.add_new_user.phone.error.exist");
            ERR_WORK_NAME = property.getProperty("scenarios.add_new_user.work_name.error");
            ADD_USER_SUCCESS = property.getProperty("scenarios.add_new_user.success");
            CHOOSE_ROLE = property.getProperty("scenarios.add_new_user.choose_role");
            ADD_USER_CANCEL = property.getProperty("scenarios.add_new_user.cancel");

            BUTTON_OK = property.getProperty("scenarios.delete_user.button.ok");
            BUTTON_CANCEL = property.getProperty("scenarios.delete_user.button.cancel");
            DEL_USER_CANCEL = property.getProperty("scenarios.delete_user.cancel");
            DEL_USER_SUCCESS = property.getProperty("scenarios.delete_user.success");

            CHOOSE_USER = property.getProperty("scenarios.delete_user.choose_user");
            USER_INF = property.getProperty("scenarios.delete_user.user_inf");

            ADMIN_IND = Integer.parseInt(property.getProperty("users.role.admin.ind"));
            OPERATOR_IND = Integer.parseInt(property.getProperty("users.role.operator.ind"));
            PROMOTER_IND = Integer.parseInt(property.getProperty("users.role.promoter.ind"));
            GUIDE_IND = Integer.parseInt(property.getProperty("users.role.guide.ind"));

        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
