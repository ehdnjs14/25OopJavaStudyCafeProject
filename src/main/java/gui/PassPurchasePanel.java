/*
화면 6: 정기권 상품 선택 및 결제 화면
 */
package gui; 

import payment.PriceManager;
import payment.TicketProduct;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import javax.swing.JTabbedPane;

public class PassPurchasePanel extends JPanel {

    private KioskMainFrame mainFrame;
    private PriceManager priceManager;

    public PassPurchasePanel(KioskMainFrame mainFrame, PriceManager priceManager) {
        this.mainFrame = mainFrame;
        this.priceManager = priceManager;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(new JLabel("구매하실 정기권을 선택하세요."), BorderLayout.NORTH);

        JTabbedPane tabPane = new JTabbedPane();

        // 기간권 탭
        JPanel durationPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        durationPanel.add(new JButton(TicketProduct.DURATION_1W.toString() + " (" + priceManager.getPrice(TicketProduct.DURATION_1W) + "원)"));
        durationPanel.add(new JButton(TicketProduct.DURATION_2W.toString() + " (" + priceManager.getPrice(TicketProduct.DURATION_2W) + "원)"));
        durationPanel.add(new JButton(TicketProduct.DURATION_1M.toString() + " (" + priceManager.getPrice(TicketProduct.DURATION_1M) + "원)"));
        durationPanel.add(new JButton(TicketProduct.DURATION_3M.toString() + " (" + priceManager.getPrice(TicketProduct.DURATION_3M) + "원)"));
        tabPane.addTab("기간권", durationPanel);

        // 시간권 탭
        JPanel timePanel = new JPanel(new GridLayout(3, 1, 10, 10));
        timePanel.add(new JButton(TicketProduct.TIME_50H.toString() + " (" + priceManager.getPrice(TicketProduct.TIME_50H) + "원)"));
        timePanel.add(new JButton(TicketProduct.TIME_100H.toString() + " (" + priceManager.getPrice(TicketProduct.TIME_100H) + "원)"));
        timePanel.add(new JButton(TicketProduct.TIME_200H.toString() + " (" + priceManager.getPrice(TicketProduct.TIME_200H) + "원)"));
        tabPane.addTab("시간권", timePanel);
        
        add(tabPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JButton payBtn = new JButton("결제하기 (선택한 상품)");
        JButton backBtn = new JButton("뒤로가기");
        bottomPanel.add(payBtn);
        bottomPanel.add(backBtn);
        
        add(bottomPanel, BorderLayout.SOUTH);

        payBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(mainFrame, "정기권 구매 완료! 좌석 배치도 화면으로 이동합니다.");
            mainFrame.showPanel(KioskMainFrame.SEAT_MAP_PANEL);
        });

        backBtn.addActionListener(e -> {
            mainFrame.showPanel(KioskMainFrame.TICKET_SELECTION_PANEL); 
        });
    }
}