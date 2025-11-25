package Test;
import javax.swing.SwingUtilities;
import ReadingRoomLogin.ReadingRoomLogin;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new ReadingRoomLogin();   // 전체 GUI 시작
                System.out.println("✔ GUI 전체 프로그램 실행 성공!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
