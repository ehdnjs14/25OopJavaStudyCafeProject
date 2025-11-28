package gui; 

import KioskService.*;
import SeatManager.SeatManager;
import ReadingRoomLogin.Member;
import Ticket.DurationTicket;
import Ticket.TimeTicket;
import Seat.UsageSession;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;      // 컵 몸통(사다리꼴)을 그리기 위해 필요
import java.awt.geom.RoundRectangle2D; // 둥근 뚜껑을 그리기 위해 필요
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MainMenuPanel extends JPanel {

    private KioskMainFrame mainFrame;
    private JLabel welcomeLabel;
    private JLabel sessionInfoLabel;
    private JLabel ticketInfoLabel;

    private SeatManager seatManager;
    private CheckInService checkInService;
    private CheckOutService checkOutService;
    private SessionManager sessionManager;
    private SeatMoveService seatMoveService;
    private Timer sessionTimer;

    // 스탬프 개수 상태
    private int currentStampCount = 0;
    private JPanel stampGrid;

    public MainMenuPanel(KioskMainFrame mainFrame, CheckInService checkIn, CheckOutService checkOut, SeatManager seatManager, SessionManager sessionManager, SeatMoveService seatMoveService) {
        this.mainFrame = mainFrame;
        this.checkInService = checkIn;
        this.checkOutService = checkOut;
        this.seatManager = seatManager;
        this.sessionManager = sessionManager;
        this.seatMoveService = seatMoveService;

        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND_COLOR);

        // 상단 헤더 영역 디자인 변경
        JPanel headerWrapper = new JPanel();
        headerWrapper.setLayout(new BorderLayout(0, 20)); // 상하 간격 20
        headerWrapper.setBackground(Theme.BACKGROUND_COLOR);
        headerWrapper.setBorder(BorderFactory.createEmptyBorder(30, 40, 10, 40)); // 전체 여백

        // 1. 상단: 중앙 환영 문구
        welcomeLabel = new JLabel("", SwingConstants.CENTER);
        Theme.styleLabel(welcomeLabel, Theme.TITLE_FONT);
        welcomeLabel.setFont(welcomeLabel.getFont().deriveFont(Font.BOLD, 24f)); // 폰트 조금 더 키움
        headerWrapper.add(welcomeLabel, BorderLayout.NORTH);

        // 2. 하단: 정보 패널(좌) + 스탬프 패널(우) 컨테이너
        JPanel statusContainer = new JPanel(new GridLayout(1, 2, 30, 0)); // 1행 2열, 좌우 간격 30
        statusContainer.setBackground(Theme.BACKGROUND_COLOR);
        statusContainer.setOpaque(false);

        // [좌측] 정보 패널 (카드 UI 스타일 적용)
        JPanel infoCard = new JPanel();
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        infoCard.setBackground(Color.WHITE); // 카드 배경색 흰색
        // 테두리와 내부 여백 설정 (그림자 효과 흉내)
        infoCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // 정보 라벨 스타일링
        sessionInfoLabel = new JLabel("학습 시간 정보를 불러오는 중...", SwingConstants.LEFT);
        Theme.styleLabel(sessionInfoLabel, Theme.MAIN_FONT);
        sessionInfoLabel.setForeground(new Color(80, 80, 80)); // 진한 회색 텍스트

        ticketInfoLabel = new JLabel("이용권 정보를 불러오는 중...", SwingConstants.LEFT);
        Theme.styleLabel(ticketInfoLabel, Theme.MAIN_FONT);
        ticketInfoLabel.setForeground(new Color(80, 80, 80));

        // 라벨 추가
        infoCard.add(createStyledInfoRow("⏱ 오늘의 학습 시간", sessionInfoLabel));
        infoCard.add(Box.createVerticalStrut(15)); // 간격
        infoCard.add(createStyledInfoRow("🎫 이용권 상태", ticketInfoLabel));

        // 쿠폰 스탬프 패널 (5x2)
        JPanel stampCard = new JPanel(new BorderLayout());
        stampCard.setBackground(new Color(235, 245, 255)); // 배경
        stampCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 2), // 테두리
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel stampTitle = new JLabel("STAMP CARD", SwingConstants.CENTER);
        stampTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        stampTitle.setForeground(new Color(139, 69, 19)); // 갈색
        stampCard.add(stampTitle, BorderLayout.NORTH);

        stampGrid = new JPanel(new GridLayout(2, 5, 10, 10)); // 2행 5열, 간격 10
        stampGrid.setBackground(new Color(235, 245, 255));
        stampGrid.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        buildStampGrid();
        stampCard.add(stampGrid, BorderLayout.CENTER);

        // 컨테이너에 추가
        statusContainer.add(infoCard);
        statusContainer.add(stampCard);

        headerWrapper.add(statusContainer, BorderLayout.CENTER);
        add(headerWrapper, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        buttonPanel.setBackground(Theme.BACKGROUND_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 50, 50));
        
        JButton checkInBtn = new JButton("입실 / 좌석배정");
        JButton checkOutBtn = new JButton("퇴실");
        JButton extendBtn = new JButton("시간 연장");
        JButton orderBtn = new JButton("상품 주문");
        JButton logoutBtn = new JButton("로그아웃");
        JButton placeholderBtn = new JButton("자리 이동하기");

        buttonPanel.add(checkInBtn);
        buttonPanel.add(checkOutBtn);
        buttonPanel.add(placeholderBtn);
        buttonPanel.add(extendBtn);
        buttonPanel.add(orderBtn);
        buttonPanel.add(logoutBtn);
        
        add(buttonPanel, BorderLayout.CENTER);

        Theme.styleButton(checkInBtn);
        Theme.styleButton(checkOutBtn);
        Theme.styleButton(extendBtn);
        Theme.styleButton(placeholderBtn);
        Theme.styleButton(orderBtn);
        Theme.styleSecondaryButton(logoutBtn);
        logoutBtn.setBackground(new Color(200, 100, 100));

        checkInBtn.addActionListener(e -> {
            System.out.println("--- 입실 버튼 클릭 ---");
            Member member = mainFrame.getCurrentMember();
            
            if (member == null) {
                System.out.println("멤버가 null입니다. 로그인 화면으로 전환합니다.");
                JOptionPane.showMessageDialog(mainFrame, "로그인이 필요합니다.");
                mainFrame.showPanel(KioskMainFrame.LOGIN_PANEL);
                return;
            }
            
            if (seatManager == null) {
                System.err.println("SeatManager가 null입니다!");
                JOptionPane.showMessageDialog(mainFrame, "시스템 오류: 좌석 관리 객체 초기화 실패.");
                return;
            }

            if (seatManager.findSeatByMember(member.getId()) != null) {
                System.out.println("이미 입실 상태입니다.");
                JOptionPane.showMessageDialog(mainFrame, "이미 입실 상태입니다.");
                return;
            }
            
            System.out.println("유효 티켓 확인: " + member.hasValidTicket());
            if (member.hasValidTicket()) {
                mainFrame.endSeatMoveMode();
                mainFrame.showPanel(KioskMainFrame.SEAT_MAP_PANEL); 
            } else {
                mainFrame.showPanel(KioskMainFrame.TICKET_SELECTION_PANEL);
            }
        });

        checkOutBtn.addActionListener(e -> {
            Member member = mainFrame.getCurrentMember();
            if (member == null) return;
            boolean success = checkOutService.checkOut(member.getId());
            if (success) {
                JOptionPane.showMessageDialog(mainFrame, "퇴실 처리되었습니다.");
                // 퇴실 시 스탬프/쿠폰 적립이 이루어지므로 즉시 갱신
                refreshSessionInfo();
            } else {
                JOptionPane.showMessageDialog(mainFrame, "입실하지 않은 회원입니다.");
            }
        });


        extendBtn.addActionListener(e -> {
            Member member = mainFrame.getCurrentMember();
            if (member == null) {
                JOptionPane.showMessageDialog(mainFrame, "로그인이 필요합니다.");
                mainFrame.showPanel(KioskMainFrame.LOGIN_PANEL);
                return;
            }

            if (member.getTicket() instanceof TimeTicket) {
                JOptionPane.showMessageDialog(mainFrame, "시간권 연장 화면으로 이동합니다.");
                mainFrame.showPassPurchaseForTime();
            } else if (member.getTicket() instanceof DurationTicket) {
                JOptionPane.showMessageDialog(mainFrame, "기간권/정기권 연장 화면으로 이동합니다.");
                mainFrame.showPassPurchaseForDuration();
            } else {
                JOptionPane.showMessageDialog(mainFrame, "이용권이 없습니다. 구매 화면으로 이동합니다.");
                mainFrame.showPanel(KioskMainFrame.TICKET_SELECTION_PANEL);
            }
        });

        orderBtn.addActionListener(e -> {
            mainFrame.showPanel(KioskMainFrame.SHOP_PANEL);
        }); 

        placeholderBtn.addActionListener(e -> {
            Member member = mainFrame.getCurrentMember();
            if (member == null) {
                JOptionPane.showMessageDialog(mainFrame, "로그인이 필요합니다.");
                return;
            }
            Seat.Seat currentSeat = seatManager.findSeatByMember(member.getId());
            if (currentSeat == null) {
                JOptionPane.showMessageDialog(mainFrame, "현재 사용 중인 좌석이 없습니다. 입실 후 자리 이동을 이용해 주세요.");
                return;
            }
            JOptionPane.showMessageDialog(mainFrame, "이동할 좌석을 선택하세요.");
            mainFrame.startSeatMoveMode();
        });

        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(mainFrame, "로그아웃 하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                mainFrame.setCurrentMember(null);
                mainFrame.showPanel(KioskMainFrame.LOGIN_PANEL);
            }
        });

        sessionTimer = new Timer(30_000, e -> refreshSessionInfo());
        sessionTimer.start();
    }

    // 정보 라벨 스타일링
    private JPanel createStyledInfoRow(String title, JLabel valueLabel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        titleLabel.setForeground(new Color(100, 100, 150)); // 약간 푸른빛
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        return panel;
    }

   // 커피 컵 형태의 스탬프 컴포넌트
private static class CoffeeStamp extends JPanel {
    private final int number;       // 스탬프 번호
    private final boolean isFilled; // 채워짐 여부

    public CoffeeStamp(int number, boolean isFilled) {
        this.number = number;
        this.isFilled = isFilled;
        setOpaque(false);
        // 컨테이너가 너무 좁아도 최소 크기는 확보 (50x60)
        setMinimumSize(new Dimension(50, 60));
        setPreferredSize(new Dimension(50, 60));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // --- [수정됨] 중앙 정렬 좌표 계산 ---
        // 컵의 기본 크기
        int cupW = 30;
        int cupH = 35;

        // 현재 패널의 실제 너비/높이
        int w = getWidth();
        int h = getHeight();

        // 중앙에 위치하기 위한 시작 좌표 계산 (뚜껑 높이 약 4px 고려)
        int startX = (w - cupW) / 2;
        int startY = (h - (cupH + 4)) / 2 + 2;
        // --------------------------------

        // 컵 몸통 (사다리꼴)
        GeneralPath cupBody = new GeneralPath();
        cupBody.moveTo(startX, startY);
        cupBody.lineTo(startX + cupW, startY);
        cupBody.lineTo(startX + cupW - 5, startY + cupH);
        cupBody.lineTo(startX + 5, startY + cupH);
        cupBody.closePath();

        // 뚜껑 (둥근 사각형)
        RoundRectangle2D lid = new RoundRectangle2D.Float(startX - 2, startY - 4, cupW + 4, 6, 3, 3);

        if (isFilled) {
            // 채워진 컵
            g2.setColor(new Color(111, 78, 55)); // 커피색
            g2.fill(cupBody);

            g2.setColor(Color.WHITE);
            g2.fill(lid);

            // 홀더(밴드)
            g2.setColor(new Color(200, 170, 120));
            g2.fillRect(startX + 4, startY + 14, cupW - 8, 10);

            // 체크 표시
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2f));
            g2.drawLine(startX + 10, startY + 24, startX + 15, startY + 29);
            g2.drawLine(startX + 15, startY + 29, startX + 24, startY + 16);
        } else {
            // 빈 컵: 점선 테두리 + 숫자
            g2.setColor(new Color(180, 180, 180));
            Stroke dashed = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0);
            g2.setStroke(dashed);
            g2.draw(cupBody);
            g2.draw(lid);

            // 번호 표시
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            FontMetrics fm = g2.getFontMetrics();
            String text = String.valueOf(number);
            int textX = startX + (cupW - fm.stringWidth(text)) / 2;
            int textY = startY + cupH / 2 + fm.getAscent() / 2 - 2;
            g2.drawString(text, textX, textY);
        }
    }
}

    // -------------------------------------

    public void updateWelcomeMessage() {
        Member member = mainFrame.getCurrentMember();
        
        if(member != null) {
            String message = member.getName() + "님, 오늘도 열공하세요!";
            welcomeLabel.setText(message);
            System.out.println("[MainMenuPanel Debug] 메시지 설정 성공: " + message);
            refreshSessionInfo();
        } else {
            welcomeLabel.setText("로그인 하지 않음!");
            sessionInfoLabel.setText("");
            System.out.println("[MainMenuPanel Debug] 멤버 없음: 로그인 하지 않음!");
        }
        
        this.revalidate(); 
        this.repaint();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (sessionTimer != null && !sessionTimer.isRunning()) {
            sessionTimer.start();
        }
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (sessionTimer != null && sessionTimer.isRunning()) {
            sessionTimer.stop();
        }
    }

    private void refreshSessionInfo() {
        Member member = mainFrame.getCurrentMember();
        if (member == null) {
            sessionInfoLabel.setText("");
            currentStampCount = 0;
            buildStampGrid();
            return;
        }
        UsageSession session = sessionManager.getActiveSession(member.getId());
        if (session != null) {
            long minutes = session.getDurationInMinutes();
            sessionInfoLabel.setText(formatMinutesAsHoursAndMinutes(minutes)); // 제목은 createStyledInfoRow에 있음
        } else {
            sessionInfoLabel.setText("-");
        }

        updateTicketInfo(member, session);
        currentStampCount = member.getStampCount();
        buildStampGrid();
    }

    private String formatMinutesAsHoursAndMinutes(long minutes) {
        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;
        return hours + "시간 " + remainingMinutes + "분";
    }

    private void updateTicketInfo(Member member, UsageSession session) {
        if (member == null) {
            ticketInfoLabel.setText("");
            return;
        }

        var ticket = member.getTicket();
        if (ticket == null) {
            ticketInfoLabel.setText("이용권 없음");
            return;
        }

        if (ticket instanceof DurationTicket durationTicket) {
            LocalDate expiryDate = durationTicket.getExpiryDate().toLocalDate();
            String typeLabel = expiryDate.isEqual(LocalDate.now()) ? "[당일권] " : "[기간권] ";
            String formatted = durationTicket.getExpiryDate().format(DateTimeFormatter.ofPattern("yy/MM/dd HH:mm 만료"));
            ticketInfoLabel.setText(typeLabel + formatted);
            return;
        }

        if (ticket instanceof TimeTicket timeTicket) {
            long baseMinutes = timeTicket.getRemainingMinutes();
            long usedMinutes = (session != null) ? session.getDurationInMinutes() : 0;
            long remainMinutes = Math.max(0, baseMinutes - usedMinutes);
            long hours = remainMinutes / 60;
            long minutes = remainMinutes % 60;
            ticketInfoLabel.setText("[시간권] 잔여 " + hours + "시간 " + minutes + "분");
            return;
        }

        ticketInfoLabel.setText("정보 없음");
    }

    // 스탬프 UI를 현재 개수에 맞춰 다시 그림
    private void buildStampGrid() {
        if (stampGrid == null) return;
        stampGrid.removeAll();
        for (int i = 1; i <= 10; i++) {
            boolean isStamped = (i <= currentStampCount);
            stampGrid.add(new CoffeeStamp(i, isStamped));
        }
        stampGrid.revalidate();
        stampGrid.repaint();
    }
}
