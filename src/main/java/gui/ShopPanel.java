package gui;
import javax.swing.*;

import Seat.Seat;
import ReadingRoomLogin.Member;
import payment.ILogManager;
import payment.OrderLogEntry;
import java.net.URL;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class ShopPanel extends JPanel {

    private KioskMainFrame parentFrame; // KioskMainFrame 인스턴스 저장
    private ILogManager logManager;     // 주문 로그용

    // 주문 정보를 저장할 맵 (상품명 -> 수량)
    private Map<String, Integer> orderMap = new HashMap<>();
    private JPanel cartPanel; // 장바구니 항목들을 담을 패널
    private JLabel totalLabel;
    private JPanel itemPanel; // 상품 버튼을 담을 중앙 패널

    // 상품 데이터 정의: [상품명, 가격]
    private final Map<String, String[][]> productData = new HashMap<>() {{
        put("식사류", new String[][]{
                {"진라면", "4000"},
                {"신라면", "4500"},
                {"불닭볶음면", "5000"},
                {"짜파게티", "4500"},
                {"김치볶음밥", "5000"},
                {"참치마요주먹밥", "3500"}
        });
        put("음료", new String[][]{
                {"콜라", "2000"},
                {"사이다", "2000"},
                {"에너지드링크", "1500"},
                {"아이스아메리카노", "3000"},
                {"포카리스웨트", "2500"},
                {"오렌지주스", "2500"}
        });
        put("간식류", new String[][]{
                {"새우깡", "1500"},
                {"감자칩", "2500"},
                {"홈런볼", "2000"},
                {"핫바", "2500"},
                {"소시지", "2000"},
                {"구운계란(2개)", "1500"}
        });
    }};

    public ShopPanel(KioskMainFrame parentFrame, ILogManager logManager) {
        this.parentFrame = parentFrame;
        this.logManager = logManager;

        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND_COLOR);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Theme.BACKGROUND_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel titleLabel = new JLabel(" 상품 주문", SwingConstants.CENTER);
        Theme.styleLabel(titleLabel, Theme.TITLE_FONT);
        JButton backButton = new JButton("돌아가기");
        Theme.styleSecondaryButton(backButton);

        backButton.addActionListener(e -> {
            // 장바구니 비우기
            if (!orderMap.isEmpty()) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "장바구니에 상품이 남아있습니다. 정말 돌아가시겠습니까?",
                        "경고", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;
            }
            // 메인 메뉴 패널로 전환
            parentFrame.showPanel(KioskMainFrame.MAIN_MENU_PANEL);
        });

        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);
        // 1. 카테고리
        JList<String> categoryList = new JList<>(new Vector<>(productData.keySet()));
        categoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categoryList.setFont(Theme.MAIN_FONT.deriveFont(Font.BOLD, 16f));
        categoryList.setSelectionForeground(Color.WHITE);
        categoryList.setFixedCellHeight(50);
        categoryList.setPreferredSize(new Dimension(160, 0));

        categoryList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedCategory = categoryList.getSelectedValue();
                if (selectedCategory != null) {
                    displayItems(selectedCategory);
                }
            }
        });

        JScrollPane categoryScroll = new JScrollPane(categoryList);
        categoryScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 226, 235)));
        add(categoryScroll, BorderLayout.WEST);

        // 2. 상품 목록
        itemPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        itemPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        itemPanel.setBackground(Color.WHITE);
        JScrollPane itemScroll = new JScrollPane(itemPanel);
        itemScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        itemScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(itemScroll, BorderLayout.CENTER);

        // 3. 장바구니/주문
        JPanel orderPanel = new JPanel(new BorderLayout());
        orderPanel.setBackground(Theme.BACKGROUND_COLOR);
        orderPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        cartPanel = new JPanel();
        cartPanel.setLayout(new BoxLayout(cartPanel, BoxLayout.Y_AXIS));
        cartPanel.setBackground(Color.WHITE);

        JScrollPane cartScrollPane = new JScrollPane(cartPanel);
        cartScrollPane.setBorder(BorderFactory.createTitledBorder(" 장바구니 내역"));

        totalLabel = new JLabel("총 결제 금액: 0원", SwingConstants.RIGHT);
        totalLabel.setFont(Theme.TITLE_FONT.deriveFont(18f));

        JButton confirmButton = new JButton("주문 완료 및 결제");
        Theme.styleButton(confirmButton);
        confirmButton.setBackground(new Color(60, 179, 113));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.addActionListener(e -> completeOrder());

        JPanel bottomControlPanel = new JPanel(new GridLayout(2, 1, 8, 8));
        bottomControlPanel.setBackground(Theme.BACKGROUND_COLOR);
        bottomControlPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        bottomControlPanel.add(totalLabel);
        bottomControlPanel.add(confirmButton);

        orderPanel.setPreferredSize(new Dimension(320, 0));
        orderPanel.add(cartScrollPane, BorderLayout.CENTER);
        orderPanel.add(bottomControlPanel, BorderLayout.SOUTH);

        add(orderPanel, BorderLayout.EAST);

        if (!productData.isEmpty()) {
            categoryList.setSelectedIndex(0);
        }

    }

    private void displayItems(String category) {
        itemPanel.removeAll();
        String[][] items = productData.get(category);

        if (items != null) {
            for (String[] item : items) {
                String name = item[0];
                int price = Integer.parseInt(item[1]);

                JButton itemButton = createItemButton(name, price);
                itemButton.addActionListener(e -> addItemToCart(name));
                itemPanel.add(itemButton);
            }
        }
        itemPanel.revalidate();
        itemPanel.repaint();
    }

    private JButton createItemButton(String name, int price) {
        String text = String.format("<html><center>%s<br><font size=5>%,d원</font></center></html>", name, price);
        JButton btn = new JButton(text);
        Theme.styleButton(btn);
        btn.setPreferredSize(new Dimension(150, 180));
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setIconTextGap(10);
        ImageIcon icon = loadResizedIcon(name, 130, 120);
        if (icon != null) {
            btn.setIcon(icon);
        }

        return btn;
    }

    private ImageIcon loadResizedIcon(String imageName, int width, int height) {
        try {
            String path = "images/" + imageName + ".jpg";
            URL url = getClass().getClassLoader().getResource(path);

            if (url != null) {
                ImageIcon originalIcon = new ImageIcon(url);
                Image img = originalIcon.getImage();
                Image resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(resizedImg);
            } else
                // 이미지를 찾을 수 없는 경우 로그 출력 후 플레이스홀더 반환
                System.out.println("이미지를 찾을 수 없음: " + path);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 장바구니에 상품 추가
    private void addItemToCart(String name) {
        orderMap.put(name, orderMap.getOrDefault(name, 0) + 1);
        updateCartDisplay();
    }

    // 장바구니에서 상품 삭제
    private void removeItemFromCart(String name) {
        orderMap.remove(name);
        updateCartDisplay();
    }

    // 장바구니를 완전히 비움
    private void clearCart() {
        orderMap.clear();
        updateCartDisplay();
    }

    // 장바구니 UI를 갱신하고 총 금액을 계산
    private void updateCartDisplay() {
        cartPanel.removeAll(); // 기존 항목 전체 제거
        long totalAmount = 0;

        for (Map.Entry<String, Integer> entry : orderMap.entrySet()) {
            String name = entry.getKey();
            int quantity = entry.getValue();

            int price = getProductPrice(name);
            long itemTotal = (long) price * quantity;
            totalAmount += itemTotal;

            // 장바구니 항목 UI 생성 (상품명, 수량, 가격, 삭제 버튼)
            JPanel itemRow = new JPanel(new BorderLayout());
            itemRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            itemRow.setBackground(Color.WHITE);
            String itemText = String.format("%s x %d (%,d원)", name, quantity, itemTotal);
            JLabel itemLabel = new JLabel(itemText);
            itemLabel.setFont(Theme.MAIN_FONT.deriveFont(14f));

            JButton deleteButton = new JButton("X");
            deleteButton.setMargin(new Insets(0,0,0,0));
            deleteButton.setPreferredSize(new Dimension(30, 30));
            deleteButton.setBackground(new Color(255, 100, 100));
            deleteButton.setForeground(Color.WHITE);
            deleteButton.setFocusPainted(false);
            deleteButton.addActionListener((ActionEvent e) -> removeItemFromCart(name));

            itemRow.add(itemLabel, BorderLayout.CENTER);
            itemRow.add(deleteButton, BorderLayout.EAST);

            cartPanel.add(itemRow);
            cartPanel.add(Box.createVerticalStrut(5));
        }

        // 장바구니가 비어 있을 때 메시지 표시
        if (orderMap.isEmpty()) {
            cartPanel.add(new JLabel("   장바구니가 비어 있습니다.", SwingConstants.CENTER));
        }

        // UI 갱신
        cartPanel.revalidate();
        cartPanel.repaint();

        // 총 금액 업데이트
        totalLabel.setText(String.format("총 결제 금액: %,d원", totalAmount));
    }

    // 상품 가격을 찾는 헬퍼 메서드
    private int getProductPrice(String name) {
        for (String[][] items : productData.values()) {
            for (String[] item : items) {
                if (item[0].equals(name)) {
                    return Integer.parseInt(item[1]);
                }
            }
        }
        return 0;
    }

    // 주문 완료 로직 (기존 동작 유지)
    private void completeOrder() {
        if (orderMap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "장바구니가 비어 있습니다.");
            return;
        }
        long total = calculateTotalAmount();
        StringBuilder summaryBuilder = new StringBuilder();
        for (Map.Entry<String, Integer> entry : orderMap.entrySet()) {
            String name = entry.getKey();
            int quantity = entry.getValue();
            long itemTotal = (long) getProductPrice(name) * quantity;
            summaryBuilder.append(String.format("%s x %d개 (%,d원)\n", name, quantity, itemTotal));
        }

        // 주문 로그 기록
        logOrderDetails(summaryBuilder.toString(), total);

        JOptionPane.showMessageDialog(this, "주문이 완료되었습니다!");
        clearCart();
    }

    private long calculateTotalAmount() {
        long totalAmount = 0;
        for (Map.Entry<String, Integer> entry : orderMap.entrySet()) {
            int quantity = entry.getValue();
            int price = getProductPrice(entry.getKey());
            totalAmount += (long) price * quantity;
        }
        return totalAmount;
    }

    /**
     * 11/23
     * 로그 파일에 주문 내역을 기록하는 메서드
     * 형식: 시간, 이용자ID, 좌석번호, 주문내역, 총액
     */
    private void logOrderDetails(String orderSummary, long totalAmount) {
        if (logManager == null) {
            System.err.println("LogManager가 초기화되지 않았습니다. 로그 기록 실패.");
            return;
        }

        // 현재 사용자 정보 가져오기
        Member currentMember = parentFrame.getCurrentMember();
        String memberId = (currentMember != null) ? currentMember.getId() : "NON_MEMBER";
        String seatNumber = "N/A"; // 좌석 번호 초기값

        Seat seat = parentFrame.getSeatManager().findSeatByMember(memberId);
        if (seat != null) {
            seatNumber = String.valueOf(seat.getSeatNumber());
        }

        String detailedOrder = orderSummary.trim()
                .replace("\n", ", ")
                .replaceAll(" +", " ")
                .replaceAll("[,;] $", ""); // 끝 콤마/세미콜론 제거

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        OrderLogEntry entry = new OrderLogEntry(timestamp, memberId, seatNumber, detailedOrder, totalAmount);
        try {
            logManager.saveOrderLog(entry);
            System.out.println("[LOG] 주문 기록: " + detailedOrder);
        } catch (Exception ex) {
            System.err.println("주문 로그 기록 실패: " + ex.getMessage());
        }
    }
}
