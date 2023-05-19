import parsers.PrivatBankAPIParser;
import services.TelegramBotService;

public class AppLauncher {
    public static void main(String[] args) {
      
        TelegramBotService bot = new TelegramBotService();
      
        PrivatBankAPIParser parser = new PrivatBankAPIParser();
        String response = parser.sendRequest("p24api/pubinfo?exchange&json");
        System.out.println(response);





    }

}
