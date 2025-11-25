package gui; 

import ReadingRoomLogin.Member;
import Seat.Seat;

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

    public MainMenuPanel(KioskMainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(10, 10));

        welcomeLabel = new JLabel("", SwingConstants.CENTER);
        add(welcomeLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        
        JButton checkInBtn = new JButton("입실");
        JButton checkOutBtn = new JButton("퇴실");
        JButton breakBtn = new JButton("외출/복귀");
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


        //입실 버튼
        checkInBtn.addActionListener(e -> {
            Member member = mainFrame.getCurrentMember(); // Member 객체 사용
            if (member == null) {
                 JOptionPane.showMessageDialog(mainFrame, "로그인이 필요합니다.");
                 mainFrame.showPanel(KioskMainFrame.LOGIN_PANEL);
                 return;
            }
            
            // 회원이 이미 좌석 사용 중인지 체크
            Seat currentSeat = mainFrame.getSeatManager().findSeatByMember(member.getMemberId());
            if (currentSeat != null) {
                JOptionPane.showMessageDialog(mainFrame,
                    "이미 " + currentSeat.getSeatNumber() + "번 좌석을 이용 중입니다.\n퇴실 후 다시 입실할 수 있습니다.");
                return;
            }
            //이용권 여부 체크
            if (member.hasValidTicket()) {
                mainFrame.showPanel(KioskMainFrame.SEAT_MAP_PANEL); 
            } else {
                mainFrame.showPanel(KioskMainFrame.TICKET_SELECTION_PANEL);
            }
        });

        checkOutBtn.addActionListener(e -> {
        	Member member = mainFrame.getCurrentMember();

            if (member == null) {
                JOptionPane.showMessageDialog(mainFrame, "로그인이 필요합니다.");
                return;
            }

            Seat usedSeat = mainFrame.getSeatManager().findSeatByMember(member.getMemberId());

            if (usedSeat == null) {
                JOptionPane.showMessageDialog(mainFrame, "현재 이용 중인 좌석이 없습니다.");
                return;
            }

            // 실제 퇴실 처리
            mainFrame.getSeatManager().vacateSeat(member.getMemberId());

            JOptionPane.showMessageDialog(mainFrame,
                    usedSeat.getSeatNumber() + "번 좌석 퇴실 완료!");
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