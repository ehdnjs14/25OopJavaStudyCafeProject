package Ticket;

import ReadingRoomLogin.Member;
import Ticket.TimeTicket; // TimeTicket 정보를 가져오기 위함
import gui.KioskMainFrame;
import gui.Theme;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;

/**
 * [Ticket] 패키지에 속하는 GUI 클래스.
 * 화면 상단에 로그인된 회원의 잔여 이용 시간을 실시간으로 표시합니다.
 * TimeTicket을 확인하고 1분마다 갱신합니다.
 */
public class remainTime extends JPanel {

    private final KioskMainFrame mainFrame;
    private JLabel timeLabel;
    private Timer refreshTimer;
    
    // 표시할 정보를 명확하게 구분하기 위해 JLabel을 2개 사용
    private JLabel titleLabel; 

    public remainTime(KioskMainFrame mainFrame) {
        this.mainFrame = mainFrame;
        
        setLayout(new BorderLayout(5, 0));
        setPreferredSize(new Dimension(300, 40)); // 크기 지정
        setBackground(new Color(240, 240, 240)); // 배경색을 밝게 설정
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // 1. 제목 레이블: "잔여 시간"
        titleLabel = new JLabel("💰 잔여 시간: ");
        Theme.styleLabel(titleLabel, Theme.BASE_FONT.deriveFont(Font.BOLD, 16f));
        titleLabel.setForeground(Theme.PRIMARY_COLOR);
        add(titleLabel, BorderLayout.WEST);

        // 2. 시간 값 레이블: 실제 시간이 표시될 영역
        timeLabel = new JLabel("---", JLabel.RIGHT);
        Theme.styleLabel(timeLabel, Theme.BASE_FONT.deriveFont(Font.PLAIN, 16f));
        timeLabel.setForeground(Color.RED); // 시간이 중요하므로 빨간색 강조
        add(timeLabel, BorderLayout.CENTER);

        // 3. 타이머 설정: 1분(60000ms)마다 잔여 시간을 갱신합니다.
        refreshTimer = new Timer(60000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateRemainingTime();
            }
        });
    }

    /**
     * 현재 로그인된 회원의 잔여 시간을 가져와 화면을 갱신합니다.
     */
    public void updateRemainingTime() {
        Member member = mainFrame.getCurrentMember();
        
        if (member == null) {
            // 비로그인 상태
            timeLabel.setText("로그인 필요");
            timeLabel.setForeground(Color.GRAY);
            return;
        }

        // 회원이 현재 TimeTicket을 보유했는지 확인
        TimeTicket ticket = member.getCurrentTimeTicket();

        if (ticket != null) {
            // TimeTicket 클래스에 getRemainingTimeDisplay() 메서드가 있다고 가정
            String remaining = ticket.getRemainingTimeDisplay();
            timeLabel.setText(remaining);
            timeLabel.setForeground(Theme.PRIMARY_COLOR.darker()); // 시간이 있을 때 색상
        } else {
            // 시간권이 없는 상태
            timeLabel.setText("이용권 없음");
            timeLabel.setForeground(Color.RED);
        }
    }
    
    // 패널이 프레임에 추가될 때 타이머 시작
    @Override
    public void addNotify() {
        super.addNotify();
        updateRemainingTime(); // 즉시 한 번 갱신
        if (!refreshTimer.isRunning()) {
            refreshTimer.start();
        }
    }

    // 패널이 프레임에서 제거될 때 타이머 중지 (자원 해제)
    @Override
    public void removeNotify() {
        super.removeNotify();
        if (refreshTimer.isRunning()) {
            refreshTimer.stop();
        }
    }
}