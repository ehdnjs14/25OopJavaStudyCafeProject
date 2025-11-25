/*
화면 6: 정기권 상품 선택 및 결제 화면
 */
package gui; 

import payment.PriceManager;
import payment.TicketFactory;
import payment.TicketProduct;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import javax.swing.JTabbedPane;

public class DurationPassPanel extends JPanel {

    private KioskMainFrame mainFrame;
    private PriceManager priceManager;
    private TicketProduct selectedProduct = null;

    public DurationPassPanel(KioskMainFrame mainFrame, PriceManager priceManager) {
        this.mainFrame = mainFrame;
        this.priceManager = priceManager;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(new JLabel("구매하실 기간권을 선택하세요."), BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));

        // 기간권 탭
        JPanel durationPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        JButton btn1w = new JButton(TicketProduct.DURATION_1W.toString() + " (" + priceManager.getPrice(TicketProduct.DURATION_1W) + "원)");
        JButton btn2w = new JButton(TicketProduct.DURATION_2W.toString() + " (" + priceManager.getPrice(TicketProduct.DURATION_2W) + "원)");
        JButton btn1m = new JButton(TicketProduct.DURATION_1M.toString() + " (" + priceManager.getPrice(TicketProduct.DURATION_1M) + "원)");
        JButton btn3m = new JButton(TicketProduct.DURATION_3M.toString() + " (" + priceManager.getPrice(TicketProduct.DURATION_3M) + "원)");
        
        durationPanel.add(btn1w);
        durationPanel.add(btn2w);
        durationPanel.add(btn1m);
        durationPanel.add(btn3m);
        
        add(durationPanel, BorderLayout.CENTER);
        
        // 버튼 클릭 시 선택된 상품 저장
        btn1w.addActionListener(e -> selectedProduct = TicketProduct.DURATION_1W);
        btn2w.addActionListener(e -> selectedProduct = TicketProduct.DURATION_2W);
        btn1m.addActionListener(e -> selectedProduct = TicketProduct.DURATION_1M);
        btn3m.addActionListener(e -> selectedProduct = TicketProduct.DURATION_3M);
        
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JButton payBtn = new JButton("결제하기 (선택한 상품)");
        JButton backBtn = new JButton("뒤로가기");
        
        bottomPanel.add(payBtn);
        bottomPanel.add(backBtn);
        
        add(bottomPanel, BorderLayout.SOUTH);

     // 결제 버튼
        payBtn.addActionListener(e -> {
            if (selectedProduct == null) {
                JOptionPane.showMessageDialog(mainFrame, "상품을 선택해주세요!");
                return;
            }

            // 티켓 생성
            TicketFactory factory = new TicketFactory();
            if (mainFrame.getCurrentMember() != null) {
                mainFrame.getCurrentMember().setTicket(factory.createTicket(selectedProduct));
            }

            JOptionPane.showMessageDialog(mainFrame, "기간권 구매 완료!");
            mainFrame.showPanel(KioskMainFrame.SEAT_MAP_PANEL);
        });

        backBtn.addActionListener(e -> {
            mainFrame.showPanel(KioskMainFrame.TICKET_SELECTION_PANEL); 
        });
    }
}