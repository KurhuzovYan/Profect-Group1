import services.TelegramBotService;

public class AppLauncher {
    public static void main(String[] args) {

        Timer timer = new Timer();
        Thread messageInTime = new Thread(timer);
        messageInTime.start();


        TelegramBotService bot = new TelegramBotService();
//        System.out.println(new UsersSettings());

    }

}
