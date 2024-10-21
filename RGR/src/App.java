import controller.Controller;

public class App {
    void main() {
        try {
            Controller controller = new Controller();
            controller.start();
        } catch (IllegalStateException e) {
            System.err.println("Fatal Error: " + e.getMessage());
        }
    }
}