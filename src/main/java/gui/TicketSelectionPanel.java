package gui; 

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import java.awt.GridLayout;
import java.awt.BorderLayout;

public class TicketSelectionPanel extends JPanel {

    private KioskMainFrame mainFrame;

    public TicketSelectionPanel(KioskMainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(10, 20));
        setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        add(new JLabel("이용권이 없습니다. 구매하실 이용권을 선택하세요."), BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));

        JButton dailyBtn = new JButton("B. 당일 시간제 이용");
        JButton passBtn = new JButton("B. 정기권 구매");
        JButton backBtn = new JButton("뒤로가기 (메인 메뉴)");

        buttonPanel.add(dailyBtn);
        buttonPanel.add(passBtn);
        buttonPanel.add(backBtn);

        add(buttonPanel, BorderLayout.CENTER);

        dailyBtn.addActionListener(e -> {
            mainFrame.showPanel(KioskMainFrame.SEAT_MAP_PANEL);
        });

        passBtn.addActionListener(e -> {
            mainFrame.showPanel(KioskMainFrame.PASS_PURCHASE_PANEL);
        });

        backBtn.addActionListener(e -> {
            mainFrame.showPanel(KioskMainFrame.MAIN_MENU_PANEL);
        });
    }
}