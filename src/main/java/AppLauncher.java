public class AppLauncher {
    public static void main(String[] args) {
        PrivatBankAPIParser parser = new PrivatBankAPIParser();
        String response = parser.sendRequest("p24api/pubinfo?exchange&json");
        System.out.println(response);
    }

}
