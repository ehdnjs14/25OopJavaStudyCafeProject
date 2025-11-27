package gui; 

import ReadingRoomLogin.Member;
import ReadingRoomLogin.MemberManager;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

class AdminFrame extends JFrame {
    private MemberManager manager;
    private DefaultListModel<Member> listModel;
    private JList<Member> memberList;
    
    // 사용 로그 (Usage Log) 필드
    private DefaultListModel<UsageEntry> usageListModel;
    private JList<UsageEntry> usageList;
    private List<UsageEntry> allUsageEntries = new ArrayList<>(); 
    private JTextField usageSearchField; 

    // 결제 로그 (Payment Log) 필드
    private DefaultListModel<PaymentEntry> paymentListModel; 
    private JList<PaymentEntry> paymentList; 
    private List<PaymentEntry> allPaymentEntries = new ArrayList<>(); 
    private JTextField paymentSearchField; 

    // 주문 로그 (Order Log) 필드
    private DefaultListModel<OrderEntry> orderListModel; 
    private JList<OrderEntry> orderList; 
    private List<OrderEntry> allOrderEntries = new ArrayList<>(); 
    private JTextField orderSearchField; 
    
    private JTextField searchField; // 회원 검색 필드
    private Timer logRefreshTimer;
    
    // 로그 파일 경로 상수
    private static final String USAGE_LOG_PATH = "logs/usage.jsonl";
    private static final String PAYMENTS_LOG_PATH = "logs/payments.jsonl"; 
    private static final String ORDER_LOG_PATH = "logs/orders.jsonl"; 
    
    private static final DateTimeFormatter LOG_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public AdminFrame(MemberManager manager) {
        this.manager = manager;
        setTitle("회원 관리 (관리자 모드)");
        setSize(1200, 700); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        Container cp = getContentPane();
        cp.setBackground(Theme.BACKGROUND_COLOR);
        cp.setLayout(new BorderLayout());

        // 상단 타이틀
        JLabel titleLabel = new JLabel("회원 관리 시스템", SwingConstants.CENTER);
        Theme.styleLabel(titleLabel, Theme.TITLE_FONT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        cp.add(titleLabel, BorderLayout.NORTH);

        // --- 좌측: 회원 목록 영역 ---

        listModel = new DefaultListModel<>();
        memberList = new JList<>(listModel);
        memberList.setFixedCellHeight(50);
        memberList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        memberList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JPanel panel = new JPanel(new GridLayout(1, 3));
            panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            String ticketInfo = (value.getTicket() != null)
                    ? value.getTicket().getClass().getSimpleName().replace("Ticket", "") + " 이용권"
                    : "미보유";
            JLabel l1 = new JLabel(value.getId(), SwingConstants.CENTER);
            JLabel l2 = new JLabel(value.getName(), SwingConstants.CENTER);
            JLabel l3 = new JLabel(ticketInfo, SwingConstants.CENTER);

            l1.setFont(Theme.MAIN_FONT);
            l2.setFont(Theme.MAIN_FONT);
            l3.setFont(Theme.MAIN_FONT);

            if (isSelected) {
                panel.setBackground(Theme.PRIMARY_COLOR);
                l1.setForeground(Color.WHITE);
                l2.setForeground(Color.WHITE);
                l3.setForeground(Color.WHITE);
            } else {
                panel.setBackground(Color.WHITE);
                l1.setForeground(Theme.TEXT_COLOR);
                l2.setForeground(Theme.TEXT_COLOR);
                l3.setForeground(Theme.TEXT_COLOR);
            }

            panel.add(l1);
            panel.add(l2);
            panel.add(l3);
            return panel;
        });

        // 회원 검색 패널
        searchField = new JTextField(20);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Theme.BACKGROUND_COLOR);
        searchPanel.add(new JLabel("ID 또는 이름 검색:"));
        searchPanel.add(searchField);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { filterMemberList(); }
            public void removeUpdate(DocumentEvent e) { filterMemberList(); }
            public void insertUpdate(DocumentEvent e) { filterMemberList(); }
        });

        // 리스트 헤더
        JPanel headerPanel = new JPanel(new GridLayout(1, 3));
        headerPanel.setBackground(new Color(220, 230, 240));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        Font headerFont = new Font("Malgun Gothic", Font.BOLD, 16);
        JLabel h1 = new JLabel("아이디", SwingConstants.CENTER); h1.setFont(headerFont);
        JLabel h2 = new JLabel("이름", SwingConstants.CENTER); h2.setFont(headerFont);
        JLabel h3 = new JLabel("이용권 정보", SwingConstants.CENTER); h3.setFont(headerFont);
        headerPanel.add(h1);
        headerPanel.add(h2);
        headerPanel.add(h3);

        JScrollPane memberScroll = new JScrollPane(memberList);
        memberScroll.getViewport().setBackground(Color.WHITE);
        memberScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 226, 235)));

        JPanel memberPanel = new JPanel(new BorderLayout());
        memberPanel.setBackground(Theme.BACKGROUND_COLOR);
        memberPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        memberPanel.add(searchPanel, BorderLayout.NORTH);

        JPanel listWrapper = new JPanel(new BorderLayout());
        listWrapper.setBackground(Theme.BACKGROUND_COLOR);
        listWrapper.add(headerPanel, BorderLayout.NORTH);
        listWrapper.add(memberScroll, BorderLayout.CENTER);

        memberPanel.add(listWrapper, BorderLayout.CENTER);

        // --- 중앙 분할 및 우측 로그 영역 (JTabbedPane) ---

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, memberPanel, createLogTabbedPane());
        // 🌟 비율을 1:1로 수정
        splitPane.setResizeWeight(0.5); 

        cp.add(splitPane, BorderLayout.CENTER);

        // --- 하단 버튼 영역 ---

        JButton editBtn = new JButton("이름 수정");
        JButton deleteBtn = new JButton("회원 삭제");
        JButton refreshBtn = new JButton("새로고침");

        Theme.styleButton(editBtn);
        Theme.styleButton(deleteBtn);
        Theme.styleButton(refreshBtn);
        Font btnFont = Theme.MAIN_FONT.deriveFont(13f);
        editBtn.setFont(btnFont);
        deleteBtn.setFont(btnFont);
        refreshBtn.setFont(btnFont);
        editBtn.setPreferredSize(new Dimension(140, 36));
        deleteBtn.setPreferredSize(new Dimension(140, 36));
        refreshBtn.setPreferredSize(new Dimension(140, 36));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        btnPanel.setBackground(Theme.BACKGROUND_COLOR);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);

        cp.add(btnPanel, BorderLayout.SOUTH);

        // --- 이벤트 리스너 ---
        
        refreshBtn.addActionListener(e -> {
            refreshList();
            refreshUsageLog(); 
            refreshPaymentLog(); 
            refreshOrderLog();   
        });
        
        deleteBtn.addActionListener(e -> {
            Member m = memberList.getSelectedValue();
            if (m == null) return;
            int confirm = JOptionPane.showConfirmDialog(this,
                    "회원 [" + m.getName() + "]을(를) 삭제하시겠습니까?",
                    "회원 삭제 확인",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                manager.removeMember(m);
                filterMemberList();
            }
        });
        
        editBtn.addActionListener(e -> {
            Member m = memberList.getSelectedValue();
            if (m == null) return;
            String newName = JOptionPane.showInputDialog("새 이름 입력:", m.getName());
            if (newName != null && !newName.trim().isEmpty()) {
                m.setName(newName.trim());
                manager.saveMembers();
                filterMemberList();
            }
        });

        // --- 초기 로딩 및 타이머 ---

        refreshList();
        refreshUsageLog(); 
        refreshPaymentLog(); 
        refreshOrderLog();   

        // 1분마다 로그 새로고침 (진행 중 세션의 경과 시간 반영)
        logRefreshTimer = new Timer(60_000, e -> refreshUsageLogTimerAction());
        logRefreshTimer.start();
    }

    // =========================================================================
    // UI 생성 메서드
    // =========================================================================

    private JTabbedPane createLogTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Malgun Gothic", Font.BOLD, 14));

        tabbedPane.addTab("✅ 이용 기록", createUsagePanel()); 
        tabbedPane.addTab("💳 결제 기록", createPaymentPanel()); 
        tabbedPane.addTab("🍔 상품 주문 기록", createOrderPanel()); 

        return tabbedPane;
    }

    private JPanel createUsagePanel() {
        // 기존 usageListModel, usageList는 필드로 선언됨
        usageListModel = new DefaultListModel<>();
        usageList = new JList<>(usageListModel);
        usageList.setFixedCellHeight(40);
        
        // --- Cell Renderer (이전 코드에서 복사) ---
        usageList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JPanel row = new JPanel(new GridLayout(1, 5, 10, 0));
            row.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            String end = nvl(value.endTime);
            long duration = (value.type != null && value.type.equals("MOVE"))
                    ? -1
                    : (value.endTime != null ? value.durationMinutes : computeDuration(value));

            Font timeFont = new Font("Monospaced", Font.PLAIN, 12);

            JLabel c1 = new JLabel(nvl(value.memberId), SwingConstants.CENTER);
            JLabel c2 = new JLabel(nvl(value.seatNumber), SwingConstants.CENTER);
            JLabel c3 = new JLabel(nvl(value.startTime), SwingConstants.CENTER);
            JLabel c4 = new JLabel(end, SwingConstants.CENTER);
            JLabel c5 = new JLabel(duration < 0 ? "이동" : String.valueOf(duration), SwingConstants.CENTER); 

            c1.setFont(Theme.MAIN_FONT);
            c2.setFont(Theme.MAIN_FONT);
            c3.setFont(timeFont);
            c4.setFont(timeFont);
            c5.setFont(Theme.MAIN_FONT);

            if (isSelected) {
                row.setBackground(Theme.PRIMARY_COLOR);
                c1.setForeground(Color.WHITE); c2.setForeground(Color.WHITE); c3.setForeground(Color.WHITE);
                c4.setForeground(Color.WHITE); c5.setForeground(Color.WHITE);
            } else {
                if ("MOVE".equals(value.type)) {
                    row.setBackground(new Color(255, 255, 220)); 
                } else {
                    row.setBackground(Color.WHITE);
                }
                c1.setForeground(Theme.TEXT_COLOR); c2.setForeground(Theme.TEXT_COLOR); c3.setForeground(Theme.TEXT_COLOR);
                c4.setForeground(Theme.TEXT_COLOR); c5.setForeground(Theme.TEXT_COLOR);
            }
            row.add(c1); row.add(c2); row.add(c3); row.add(c4); row.add(c5);
            return row;
        });
        
        // --- 검색 패널 ---
        usageSearchField = new JTextField(20);
        JPanel usageSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        usageSearchPanel.setBackground(Theme.BACKGROUND_COLOR);
        usageSearchPanel.add(new JLabel("로그 검색 (ID, 좌석, 시간):"));
        usageSearchPanel.add(usageSearchField);
        usageSearchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { filterUsageLog(); }
            public void removeUpdate(DocumentEvent e) { filterUsageLog(); }
            public void insertUpdate(DocumentEvent e) { filterUsageLog(); }
        });
        
        // --- 헤더 패널 ---
        JPanel usageHeader = new JPanel(new GridLayout(1, 5, 10, 0));
        usageHeader.setBackground(new Color(220, 230, 240));
        usageHeader.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        Font uFont = new Font("Malgun Gothic", Font.BOLD, 14);
        usageHeader.add(createHeaderLabel("ID", uFont));
        usageHeader.add(createHeaderLabel("좌석", uFont));
        usageHeader.add(createHeaderLabel("시작/이동시각", uFont));
        usageHeader.add(createHeaderLabel("종료시각", uFont));
        usageHeader.add(createHeaderLabel("경과(분)", uFont));
        
        JScrollPane usageScroll = new JScrollPane(usageList);
        usageScroll.getViewport().setBackground(Color.WHITE);
        usageScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 226, 235)));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        
        JPanel usageTopPanel = new JPanel(new BorderLayout());
        usageTopPanel.add(usageSearchPanel, BorderLayout.NORTH);
        usageTopPanel.add(usageHeader, BorderLayout.SOUTH);
        
        panel.add(usageTopPanel, BorderLayout.NORTH);
        panel.add(usageScroll, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JLabel createHeaderLabel(String text, Font font) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(font);
        return label;
    }

    private JPanel createPaymentPanel() {
        // 필드에서 선언된 paymentListModel 및 paymentList 사용
        paymentListModel = new DefaultListModel<>();
        paymentList = new JList<>(paymentListModel);
        paymentList.setFont(Theme.MAIN_FONT);
        paymentList.setFixedCellHeight(40);
        
        // 결제 목록 렌더러 설정
        paymentList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            
        	String productName = nvl(value.productPurchased);
        	String displayProduct = convertProductNameToDisplay(productName);
            
            JLabel label = new JLabel(
                    String.format("ID: %s | 상품: %s | 금액: %,d원 | 방법: %s (%s)", 
                        nvl(value.memberID),         
                        displayProduct,              // 🌟 수정된 문자열 사용
                        value.price,                 
                        nvl(value.paymentMethod),    
                        nvl(value.paymentTime)       
                    )
                );
            label.setOpaque(true);
            label.setFont(Theme.MAIN_FONT);
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            if (isSelected) {
                label.setBackground(Theme.PRIMARY_COLOR);
                label.setForeground(Color.WHITE);
            } else {
                label.setBackground(Color.WHITE);
                label.setForeground(Theme.TEXT_COLOR);
            }
            return label;
        });

        // 검색 패널
        paymentSearchField = new JTextField(20);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Theme.BACKGROUND_COLOR);
        searchPanel.add(new JLabel("ID/시간/상품 검색:"));
        searchPanel.add(paymentSearchField);
        paymentSearchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { filterPaymentLog(); }
            public void removeUpdate(DocumentEvent e) { filterPaymentLog(); }
            public void insertUpdate(DocumentEvent e) { filterPaymentLog(); }
        });
        
        JScrollPane scroll = new JScrollPane(paymentList);
        scroll.getViewport().setBackground(Color.WHITE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

        return panel;
    }

    private JPanel createOrderPanel() {
        orderListModel = new DefaultListModel<>();
        orderList = new JList<>(orderListModel);

        // 주문 목록 렌더러 설정
        orderList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(String.format("ID: %s | 좌석: %s | 주문: %s | 총액: %d원 (%s)", 
                                                nvl(value.memberId), nvl(value.seatNumber), nvl(value.detailedOrder), value.totalAmount, nvl(value.timestamp)));
            label.setOpaque(true);
            label.setFont(Theme.MAIN_FONT);
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            if (isSelected) {
                label.setBackground(Theme.PRIMARY_COLOR);
                label.setForeground(Color.WHITE);
            } else {
                label.setBackground(Color.WHITE);
                label.setForeground(Theme.TEXT_COLOR);
            }
            return label;
        });
        
        // 검색 패널
        orderSearchField = new JTextField(20);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Theme.BACKGROUND_COLOR);
        searchPanel.add(new JLabel("ID/좌석/상품 검색:"));
        searchPanel.add(orderSearchField);
        orderSearchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { filterOrderLog(); }
            public void removeUpdate(DocumentEvent e) { filterOrderLog(); }
            public void insertUpdate(DocumentEvent e) { filterOrderLog(); }
        });

        JScrollPane scroll = new JScrollPane(orderList);
        scroll.getViewport().setBackground(Color.WHITE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        
        return panel;
    }


    // =========================================================================
    // 데이터 로딩 및 필터링 메서드
    // =========================================================================

    private void refreshList() {
        filterMemberList();
    }

    private void filterMemberList() {
        String searchText = searchField.getText().trim().toLowerCase();
        listModel.clear();
        if (searchText.isEmpty()) {
            for (Member m : manager.getAllMembers()) {
                listModel.addElement(m);
            }
            return;
        }

        for (Member m : manager.getAllMembers()) {
            boolean idMatch = m.getId().toLowerCase().contains(searchText);
            boolean nameMatch = m.getName().toLowerCase().contains(searchText);
            if (idMatch || nameMatch) {
                listModel.addElement(m);
            }
        }
    }
    
    private void refreshUsageLogTimerAction() {
        filterUsageLog(); 
    }


    private void refreshUsageLog() {
        try {
            if (!Files.exists(Paths.get(USAGE_LOG_PATH))) {
                allUsageEntries.clear();
                usageListModel.clear();
                return;
            }
            List<String> lines = Files.readAllLines(Paths.get(USAGE_LOG_PATH), StandardCharsets.UTF_8);
            
            allUsageEntries = parseUsageEntries(lines);
            
            filterUsageLog(); 
            
        } catch (Exception ex) {
            allUsageEntries.clear();
            usageListModel.clear();
            usageListModel.addElement(new UsageEntry("오류", "-", "-", ex.getMessage(), 0));
        }
    }

    private void filterUsageLog() {
        String searchText = usageSearchField.getText().trim().toLowerCase();
        usageListModel.clear();
        
        for (UsageEntry e : allUsageEntries) {
            boolean idMatch = nvl(e.memberId).toLowerCase().contains(searchText);
            boolean seatMatch = nvl(e.seatNumber).toLowerCase().contains(searchText);
            boolean timeMatch = nvl(e.startTime).toLowerCase().contains(searchText); 

            if (searchText.isEmpty() || idMatch || seatMatch || timeMatch) {
                usageListModel.addElement(e);
            }
        }
    }
    
    private void refreshPaymentLog() {
        try {
            if (!Files.exists(Paths.get(PAYMENTS_LOG_PATH))) {
                allPaymentEntries.clear();
                paymentListModel.clear();
                return;
            }
            List<String> lines = Files.readAllLines(Paths.get(PAYMENTS_LOG_PATH), StandardCharsets.UTF_8);
            
            Gson gson = new Gson();
            allPaymentEntries.clear();
            for (String line : lines) {
                if (line == null || line.isBlank()) continue;
                try {
                    // PaymentEntry를 로딩 시도 (PaymentProduct 내부 클래스가 포함됨)
                    PaymentEntry entry = gson.fromJson(line, PaymentEntry.class);
                    allPaymentEntries.add(entry);
                } catch (JsonSyntaxException ignore) {
                    // 파싱 실패 로그는 무시
                    // System.err.println("결제 로그 파싱 실패: " + ignore.getMessage() + " Line: " + line);
                }
            }
            java.util.Collections.reverse(allPaymentEntries); 

            filterPaymentLog();
        } catch (Exception ex) {
            System.err.println("결제 로그 로딩 실패: " + ex.getMessage());
        }
    }
    
    private void filterPaymentLog() {
        String searchText = paymentSearchField.getText().trim().toLowerCase();
        paymentListModel.clear();
        
        String productDescription = "";
        
        for (PaymentEntry e : allPaymentEntries) {
            if (e.productPurchased != null) {
                // 상품 정보 검색
                productDescription = nvl(e.productPurchased).toLowerCase();
            } else {
                productDescription = "";
            }
            
            // memberID, paymentTime, 상품 정보로 검색
            boolean idMatch = nvl(e.memberID).toLowerCase().contains(searchText); 
            boolean timeMatch = nvl(e.paymentTime).toLowerCase().contains(searchText);
            boolean descMatch = productDescription.contains(searchText); 

            if (searchText.isEmpty() || idMatch || timeMatch || descMatch) {
                paymentListModel.addElement(e);
            }
        }
    }

    private void refreshOrderLog() {
        try {
            if (!Files.exists(Paths.get(ORDER_LOG_PATH))) {
                allOrderEntries.clear();
                orderListModel.clear();
                return;
            }
            List<String> lines = Files.readAllLines(Paths.get(ORDER_LOG_PATH), StandardCharsets.UTF_8);
            
            Gson gson = new Gson();
            allOrderEntries.clear();
            for (String line : lines) {
                if (line == null || line.isBlank()) continue;
                try {
                    OrderEntry entry = gson.fromJson(line, OrderEntry.class);
                    allOrderEntries.add(entry);
                } catch (JsonSyntaxException ignore) {
                }
            }
            java.util.Collections.reverse(allOrderEntries); 

            filterOrderLog();
        } catch (Exception ex) {
            System.err.println("주문 로그 로딩 실패: " + ex.getMessage());
        }
    }
    
    private void filterOrderLog() {
        String searchText = orderSearchField.getText().trim().toLowerCase();
        orderListModel.clear();
        
        for (OrderEntry e : allOrderEntries) {
            boolean idMatch = nvl(e.memberId).toLowerCase().contains(searchText);
            boolean seatMatch = nvl(e.seatNumber).toLowerCase().contains(searchText);
            boolean orderMatch = nvl(e.detailedOrder).toLowerCase().contains(searchText);

            if (searchText.isEmpty() || idMatch || seatMatch || orderMatch) {
                orderListModel.addElement(e);
            }
        }
    }

    private List<UsageEntry> parseUsageEntries(List<String> lines) {
        List<UsageEntry> entries = new ArrayList<>();
        Gson gson = new Gson();
        for (String line : lines) {
            if (line == null || line.isBlank()) continue;
            try {
                com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(line).getAsJsonObject();
                String type = obj.has("type") ? obj.get("type").getAsString() : "SESSION";
                if ("MOVE".equals(type)) {
                    UsageEntry move = new UsageEntry();
                    move.type = "MOVE";
                    move.memberId = obj.has("memberId") ? obj.get("memberId").getAsString() : "-";
                    move.fromSeat = obj.has("fromSeat") ? obj.get("fromSeat").getAsString() : "-";
                    move.toSeat = obj.has("toSeat") ? obj.get("toSeat").getAsString() : "-";
                    move.seatNumber = move.fromSeat + "→" + move.toSeat;
                    move.startTime = obj.has("movedAt") ? obj.get("movedAt").getAsString() : "-";
                    move.endTime = null; 
                    move.durationMinutes = 0;
                    entries.add(move);
                } else {
                    UsageEntry entry = gson.fromJson(line, UsageEntry.class);
                    entry.type = "SESSION";
                    entries.add(entry);
                }
            } catch (JsonSyntaxException ignore) {
            }
        }
        return entries;
    }

    private long computeDuration(UsageEntry e) {
        if (e.endTime != null) return e.durationMinutes;
        if (e.startTime == null) return e.durationMinutes;
        try {
            LocalDateTime start = LocalDateTime.parse(e.startTime, LOG_TIME_FORMAT);
            return Math.max(0, java.time.Duration.between(start, LocalDateTime.now()).toMinutes());
        } catch (Exception ex) {
            return e.durationMinutes;
        }
    }

    private String nvl(String s) {
        return (s == null || s.trim().isEmpty()) ? "-" : s;
    }

    // =========================================================================
    // 내부 로그 엔트리 클래스
    // =========================================================================

    private static class UsageEntry {
        String type;        
        String memberId;
        String seatNumber; 
        String fromSeat;
        String toSeat;
        String startTime;   
        String endTime;     
        long durationMinutes;

        UsageEntry() {}
        UsageEntry(String memberId, String seatNumber, String startTime, String endTime, long durationMinutes) {
            this.type = "SESSION";
            this.memberId = memberId;
            this.seatNumber = seatNumber;
            this.startTime = startTime;
            this.endTime = endTime;
            this.durationMinutes = durationMinutes;
        }
    }
    
    // Payment.java 구조를 반영하여 수정됨
    private static class PaymentEntry {
        String paymentID;
        String paymentTime;     
        String memberID;        
        String productPurchased;
        int price;              
        String paymentMethod;
        
    }

    private static class OrderEntry {
        String timestamp;
        String type = "ORDER";
        String memberId;
        String seatNumber;
        String detailedOrder;
        int totalAmount;
    }

    private String convertProductNameToDisplay(String enumName) {
        if (enumName == null || enumName.isEmpty() || enumName.equals("-")) {
            return "정보 없음";
        }
        // 예시 변환 로직 (실제 TicketProduct의 Enum 값에 맞춰 상세 수정 필요)
        return switch (enumName) {
            case "DAILY_6H" -> "일일권 (6시간)";
            case "DURATION_2W" -> "기간권 (2주)";
            case "DURATION_1W" -> "기간권 (1주)";
            case "TIME_50H" -> "시간권 (50시간)";
            // 필요한 다른 Enum 값들도 추가
            default -> enumName;
        };
    }
}