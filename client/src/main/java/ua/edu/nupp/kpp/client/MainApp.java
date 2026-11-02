package ua.edu.nupp.kpp.client;

public class MainApp {

    public static void main(String[] args) {
        System.out.println("Air Quality Coursework Client");
        System.out.println("=============================");

        HttpClient client = new HttpClient("http://localhost:8080");

        try {
            String response = client.get("/equipment");
            System.out.println(response);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
