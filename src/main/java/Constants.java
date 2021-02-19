import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.Random;

public class Constants {
    String REGISTRATION_INQUIRY, REGISTRATION_BUTTON, REGISTRATION_ID_NOT_FOUND,
            REGISTRATION_PERFORMED, ADMIN, OPERATOR, PROMOTER, GUIDE;
    Constants(){
        FileInputStream fileInputStream;
        Properties property = new Properties();

        try {
            fileInputStream = new FileInputStream("src/main/resources/string.properties");
            property.load(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8));

            REGISTRATION_INQUIRY = property.getProperty("registration.inquiry");
            REGISTRATION_BUTTON = property.getProperty("registration.button");
            REGISTRATION_ID_NOT_FOUND = property.getProperty("registration.id_not_found");
            REGISTRATION_PERFORMED = property.getProperty("registration.performed");
            ADMIN = property.getProperty("users.role.admin");
            OPERATOR = property.getProperty("users.role.operator");
            PROMOTER = property.getProperty("users.role.promoter");
            GUIDE = property.getProperty("users.role.guide");

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
