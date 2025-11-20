package gui; 

import KioskService.*;
import SeatManager.SeatManager;
import ReadingRoomLogin.Member;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.GridLayout;
import java.awt.BorderLayout;

public class MainMenuPanel extends JPanel {

    private KioskMainFrame mainFrame;
    private JLabel welcomeLabel;

    private SeatManager seatManager;
    private CheckInService checkInService;
    private CheckOutService checkOutService;
    private BreakService breakService;

    public MainMenuPanel(KioskMainFrame mainFrame, CheckInService checkIn, CheckOutService checkOut, BreakService breakSvc, SeatManager seatManager) {
        this.mainFrame = mainFrame;
        this.checkInService = checkIn;
        this.checkOutService = checkOut;
        this.breakService = breakSvc;
        this.seatManager = seatManager;

        setLayout(new BorderLayout(10, 10));

        welcomeLabel = new JLabel("", SwingConstants.CENTER);
        add(welcomeLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        
        JButton checkInBtn = new JButton("입실");
        JButton checkOutBtn = new JButton("퇴실");
        JButton breakBtn = new JButton("자리 이동하기");
        JButton extendBtn = new JButton("시간 연장(당일권)");
        JButton orderBtn = new JButton("상품 주문 (준비중)");
        JButton logoutBtn = new JButton("로그아웃");

        buttonPanel.add(checkInBtn);
        buttonPanel.add(checkOutBtn);
        buttonPanel.add(breakBtn);
        buttonPanel.add(extendBtn);
        buttonPanel.add(orderBtn);
        buttonPanel.add(logoutBtn);
        
        add(buttonPanel, BorderLayout.CENTER);
        
        addNotify(); 


        checkInBtn.addActionListener(e -> {
            Member member = mainFrame.getCurrentMember(); // Member 객체 사용
            if (member == null) {
                 JOptionPane.showMessageDialog(mainFrame, "로그인이 필요합니다.");
                 mainFrame.showPanel(KioskMainFrame.LOGIN_PANEL);
                 return;
            }

            if (seatManager.findSeatByMember(member.getId()) != null) {
                JOptionPane.showMessageDialog(mainFrame, "이미 입실 상태입니다.");
                return;
            }
            
            if (member.hasValidTicket()) {
                mainFrame.showPanel(KioskMainFrame.SEAT_MAP_PANEL); 
            } else {
                mainFrame.showPanel(KioskMainFrame.TICKET_SELECTION_PANEL);
            }
        });

        checkOutBtn.addActionListener(e -> {
            Member member = mainFrame.getCurrentMember();
            if (member == null) return;

            // 4. 실제 서비스 호출
            boolean success = checkOutService.checkOut(member.getId()); //
            if (success) {
                JOptionPane.showMessageDialog(mainFrame, "퇴실 처리되었습니다.");
            } else {
                JOptionPane.showMessageDialog(mainFrame, "입실하지 않은 회원입니다.");
            }
        });


        // 아래 버튼도 유사하게 수정 필요
        breakBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(mainFrame, "외출/복귀 처리되었습니다.");
        });

        extendBtn.addActionListener(e -> {
            mainFrame.showPanel(KioskMainFrame.DAILY_TICKET_PANEL);
        });

        orderBtn.setEnabled(false); 

        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(mainFrame, "로그아웃 하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                mainFrame.setCurrentMember(null);
                mainFrame.showPanel(KioskMainFrame.LOGIN_PANEL);
            }
        });
    }

    @Override
    public void addNotify() {
        super.addNotify();
        Member member = mainFrame.getCurrentMember();
        if (member != null) {
            welcomeLabel.setText(member.getName() + "님, 환영합니다!");
        } else {
            welcomeLabel.setText("로그인이 필요합니다.");
        }
    }
}