package Ticket;

import ReadingRoomLogin.Member;
import Seat.UsageSession; // Present 기능 확인용
import gui.KioskMainFrame;
import gui.Theme; 

import javax.swing.*;
import java.awt.*;

/**
 * [Ticket] 패키지에 속하는 GUI 패널.
 * - 이용권 상태 표시 및 잔여 시간 추가(PlusTime) 기능을 제공합니다.
 * - 패널 표시 시 Present 기능을 확인합니다.
 */
public class TicketManagementPanel extends JPanel {

    public static final String PANEL_NAME = "TicketManage"; 

    private final KioskMainFrame mainFrame;
    private final PlusTime plusTimeService;
    private final Present presentService; // Present 서비스 추가

    private JLabel statusLabel;
    private JLabel remainingTimeLabel;
    private JComboBox<String> timeOptions;

    // 생성자에서 필요한 서비스 객체들을 주입받습니다.
    public TicketManagementPanel(KioskMainFrame mainFrame, PlusTime plusTime, Present present) {
        this.mainFrame = mainFrame;
        this.plusTimeService = plusTime;
        this.presentService = present; 

        setLayout(new BorderLayout(20, 20));
        setBackground(Theme.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // ... (GUI 컴포넌트 설정 및 레이아웃 코드는 이전과 동일) ...

        // 1. 제목 영역
        JLabel titleLabel = new JLabel("이용권 관리 및 잔여 시간 추가", SwingConstants.CENTER);
        Theme.styleLabel(titleLabel, Theme.TITLE_FONT);
        add(titleLabel, BorderLayout.NORTH);

        // 2. 중앙 컨텐츠 (상태 표시 및 기능 버튼)
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 30));
        centerPanel.setBackground(Theme.BACKGROUND_COLOR);

        // 2-1. 이용권 상태 영역
        JPanel statusPanel = createStatusPanel();
        centerPanel.add(statusPanel);

        // 2-2. 시간 추가 기능 영역
        JPanel extensionPanel = createExtensionPanel();
        centerPanel.add(extensionPanel);

        add(centerPanel, BorderLayout.CENTER);

        // 3. 하단 버튼
        JButton backButton = new JButton("메인 메뉴로 돌아가기");
        Theme.styleSecondaryButton(backButton);
        backButton.addActionListener(e -> mainFrame.showPanel(KioskMainFrame.MAIN_MENU_PANEL));
        
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        southPanel.setBackground(Theme.BACKGROUND_COLOR);
        southPanel.add(backButton);
        add(southPanel, BorderLayout.SOUTH);
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.setBackground(Theme.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Theme.PRIMARY_COLOR), 
                                                        "나의 이용권 상태", 0, 0, Theme.SUB_TITLE_FONT.deriveFont(Font.BOLD)));

        statusLabel = new JLabel("로그인이 필요합니다.", SwingConstants.CENTER);
        Theme.styleLabel(statusLabel, Theme.SUB_TITLE_FONT);
        panel.add(statusLabel);

        remainingTimeLabel = new JLabel("", SwingConstants.CENTER);
        Theme.styleLabel(remainingTimeLabel, Theme.SUB_TITLE_FONT.deriveFont(Font.PLAIN, 24f));
        panel.add(remainingTimeLabel);
        
        JLabel presentLabel = new JLabel("🎁 이용권 상태 확인 시 커피 증정 여부가 확인됩니다.", SwingConstants.CENTER);
        Theme.styleLabel(presentLabel, Theme.BASE_FONT);
        panel.add(presentLabel);

        return panel;
    }

    private JPanel createExtensionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panel.setBackground(Theme.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Theme.ACCENT_COLOR), 
                                                        "잔여 시간 추가 (PlusTime)", 0, 0, Theme.SUB_TITLE_FONT.deriveFont(Font.BOLD)));

        JLabel instructionLabel = new JLabel("추가할 시간 선택 (분):");
        Theme.styleLabel(instructionLabel, Theme.BASE_FONT);
        
        String[] options = {"60분 (1시간)", "120분 (2시간)", "300분 (5시간)", "600분 (10시간)"};
        timeOptions = new JComboBox<>(options);
        timeOptions.setPreferredSize(new Dimension(200, 40));
        timeOptions.setFont(Theme.BASE_FONT);

        JButton addButton = new JButton("시간 추가하기");
        Theme.styleButton(addButton);
        addButton.setPreferredSize(new Dimension(180, 40));
        addButton.addActionListener(this::handleAddTime);

        panel.add(instructionLabel);
        panel.add(timeOptions);
        panel.add(addButton);

        return panel;
    }

    private void handleAddTime(java.awt.event.ActionEvent e) {
        Member member = mainFrame.getCurrentMember();
        if (member == null) {
            JOptionPane.showMessageDialog(this, "로그인이 필요합니다.", "오류", JOptionPane.ERROR_MESSAGE);
            mainFrame.showPanel(KioskMainFrame.LOGIN_PANEL);
            return;
        }
        
        String selected = (String) timeOptions.getSelectedItem();
        long minutesToAdd = Long.parseLong(selected.split("분")[0].trim());

        // PlusTime 서비스 호출
        boolean success = plusTimeService.addRemainingTime(member, minutesToAdd);

        if (success) {
            JOptionPane.showMessageDialog(this, minutesToAdd + "분이 성공적으로 추가되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
            refreshPanelData(); 
        } else {
            JOptionPane.showMessageDialog(this, "시간 추가에 실패했습니다. 유효한 이용권을 확인해주세요.", "실패", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 패널이 표시될 때마다 이용권 상태와 Present 증정 여부를 확인/갱신합니다.
     */
    public void refreshPanelData() {
        Member member = mainFrame.getCurrentMember();
        if (member == null) {
            statusLabel.setText("로그인 후 이용 가능합니다.");
            remainingTimeLabel.setText("잔여 시간 정보 없음");
            return;
        }

        // 1. 이용권 상태 표시
        TimeTicket ticket = member.getCurrentTimeTicket();
        if (ticket != null) {
            statusLabel.setText(member.getName() + "님, 유효한 이용권을 보유 중입니다.");
            remainingTimeLabel.setText("남은 시간: " + ticket.getRemainingTimeDisplay());
        } else {
            statusLabel.setText(member.getName() + "님, 유효한 이용권이 없습니다.");
            remainingTimeLabel.setText("이용권 구매/충전이 필요합니다.");
        }
        
        // 2. Present 서비스 체크 (이용권 상태 확인과 함께 선물 증정 로직 실행)
        UsageSession session = mainFrame.getSessionManager().getActiveSession(member.getId());
        if (session != null) {
            int newPresents = presentService.checkAndGivePresent(session);
            if (newPresents > 0) {
                JOptionPane.showMessageDialog(this, 
                    "축하합니다! 누적 이용 시간 충족으로 아메리카노 " + newPresents + "잔을 받으셨습니다!", 
                    "선물 증정", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        refreshPanelData();
    }
}