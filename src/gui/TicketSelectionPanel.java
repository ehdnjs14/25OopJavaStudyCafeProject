package gui; 

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TicketSelectionPanel extends JPanel {

	private KioskMainFrame mainFrame;

	public TicketSelectionPanel(KioskMainFrame mainFrame) {
	    this.mainFrame = mainFrame;
	    setLayout(new BorderLayout(10, 20));
	    setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
	
	    add(new JLabel("구매하실 이용권을 선택하세요."), BorderLayout.NORTH);
	
	    JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
	
	    JButton dailyBtn = new JButton("A. 당일 시간제 이용");
	    JButton timeBtn = new JButton("B. 시간권 구매");
	    JButton durationBtn = new JButton("c. 정기권 구매");
	    JButton backBtn = new JButton("뒤로가기 (메인 메뉴)");
	
	    buttonPanel.add(dailyBtn);
	    buttonPanel.add(timeBtn);
	    buttonPanel.add(durationBtn);
	    buttonPanel.add(backBtn);
	
	    add(buttonPanel, BorderLayout.CENTER);
	    
	    //당일 시간제 구매
	    dailyBtn.addActionListener(e -> {
	        mainFrame.showPanel(KioskMainFrame.DAILY_TICKET_PANEL);
	    });
	
	    // 시간권 구매 화면으로 이동
	    timeBtn.addActionListener(e -> {
	        mainFrame.showPanel(KioskMainFrame.TIME_PASS_PANEL);
	    });
	
	    // 기간권 구매 화면으로 이동
	    durationBtn.addActionListener(e -> {
	        mainFrame.showPanel(KioskMainFrame.DURATION_PASS_PANEL);
	    });
	    //뒤로가기
	    backBtn.addActionListener(e -> {
	        mainFrame.showPanel(KioskMainFrame.MAIN_MENU_PANEL);
	    });
	}
}