import dto.UsersSettings;
import services.TelegramBotService;
import controller.TelegramBot;

public class AppLauncher {
    public static void main(String[] args) {
      
        TelegramBotService bot = new TelegramBotService();
        UsersSettings settings = new TelegramBot().getSettings();
        System.out.println(settings);

    }

}
