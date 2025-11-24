/*
화면 5: 당일 시간제 선택 및 결제 화면
 */
package gui; 

import ReadingRoomLogin.Member;
import payment.PriceManager;
import payment.TicketProduct;
import payment.PurchaseService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class DailyTicketPanel extends JPanel {

    private KioskMainFrame mainFrame;
    private PriceManager priceManager;

    private PurchaseService purchaseService;
    private TicketProduct selectedProduct = null;
    private int selectedPrice = 0;
    private JLabel selectedInfoLabel;

    public DailyTicketPanel(KioskMainFrame mainFrame, PriceManager priceManager, PurchaseService purchaseService) {
        this.mainFrame = mainFrame;
        this.priceManager = priceManager;
        this.purchaseService = purchaseService;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        add(new JLabel("이용하실 시간을 선택하세요."), BorderLayout.NORTH);

        JPanel ticketPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        
        addButton(ticketPanel, TicketProduct.DAILY_3H);
        addButton(ticketPanel, TicketProduct.DAILY_6H);
        addButton(ticketPanel, TicketProduct.DAILY_12H);
        addButton(ticketPanel, TicketProduct.DAILY_24H);
        
        add(ticketPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));

        selectedInfoLabel = new JLabel("상품을 선택하세요.", SwingConstants.CENTER);
        selectedInfoLabel.setBorder(BorderFactory.createEtchedBorder());
        selectedInfoLabel.setPreferredSize(new Dimension(300, 40));
        bottomPanel.add(selectedInfoLabel, BorderLayout.NORTH);

        JPanel buttonGrid = new JPanel(new GridLayout(1, 2, 10, 10));
        JButton payBtn = new JButton("결제하기");
        JButton backBtn = new JButton("뒤로가기");
        buttonGrid.add(payBtn);
        buttonGrid.add(backBtn);

        bottomPanel.add(buttonGrid, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        payBtn.addActionListener(e -> {
            if (selectedProduct == null) {
                JOptionPane.showMessageDialog(mainFrame, "상품을 먼저 선택해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Member member = mainFrame.getCurrentMember();
            if (member == null) {
                JOptionPane.showMessageDialog(mainFrame, "오류: 로그인 정보가 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                mainFrame.showPanel(KioskMainFrame.LOGIN_PANEL);
                return;
            }

            if (purchaseService.hasValidTicket(member.getId())) {
                JOptionPane.showMessageDialog(mainFrame, "이미 유효한 이용권을 보유 중입니다.\n추가 구매가 불가합니다.", "구매 불가", JOptionPane.ERROR_MESSAGE);
                return;
            }

            PaymentDialog dialog = new PaymentDialog(mainFrame, "결제하기", selectedPrice);
            dialog.setVisible(true);

            if (dialog.isPaymentSuccess()) {
                String method = dialog.getSelectedMethod();

                boolean success = purchaseService.purchaseTicket(member.getId(), selectedProduct, method);

                if (success) {
                    JOptionPane.showMessageDialog(mainFrame, "이용권이 발급되었습니다. 좌석 배치도 화면으로 이동합니다.");
                    selectedProduct = null;
                    selectedInfoLabel.setText("상품을 선택하세요.");
                    mainFrame.showPanel(KioskMainFrame.SEAT_MAP_PANEL);
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "발급 처리 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                }
            } else {

            }
        });
        
        backBtn.addActionListener(e -> {
            selectedProduct = null;
            selectedInfoLabel.setText("상품을 선택하세요.");
            mainFrame.showPanel(KioskMainFrame.SEAT_MAP_PANEL);
        });
    }

    private void addButton(JPanel panel, TicketProduct product) {
        int price = priceManager.getPrice(product);
        JButton btn = new JButton(product.toString() + " (" + price + "원)");

        btn.addActionListener(e -> {
            this.selectedProduct = product;
            this.selectedPrice = price;
            selectedInfoLabel.setText("선택: " + product.toString() + " / " + price + "원");
            System.out.println("당일권 선택됨: " + product);
        });
        panel.add(btn);
    }
}