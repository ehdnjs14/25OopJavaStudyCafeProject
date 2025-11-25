/*
화면 5: 당일 시간제 선택 및 결제 화면
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

public class DailyTicketPanel extends JPanel {

    private KioskMainFrame mainFrame;
    private PriceManager priceManager;
    private TicketProduct selectedProduct = null;

    public DailyTicketPanel(KioskMainFrame mainFrame, PriceManager priceManager) {
        this.mainFrame = mainFrame;
        this.priceManager = priceManager;
        

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        add(new JLabel("이용하실 시간을 선택하세요."), BorderLayout.NORTH);

        JPanel ticketPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        
        JButton btn3h = new JButton(TicketProduct.DAILY_3H.toString() + " (" + priceManager.getPrice(TicketProduct.DAILY_3H) + "원)");
        JButton btn6h = new JButton(TicketProduct.DAILY_6H.toString() + " (" + priceManager.getPrice(TicketProduct.DAILY_6H) + "원)");
        JButton btn12h = new JButton(TicketProduct.DAILY_12H.toString() + " (" + priceManager.getPrice(TicketProduct.DAILY_12H) + "원)"); // <--- 오타 수정
        JButton btn24h = new JButton(TicketProduct.DAILY_24H.toString() + " (" + priceManager.getPrice(TicketProduct.DAILY_24H) + "원)");

        ticketPanel.add(btn3h);
        ticketPanel.add(btn6h);
        ticketPanel.add(btn12h);
        ticketPanel.add(btn24h);
        
        add(ticketPanel, BorderLayout.CENTER);
        
        //버튼 클릭 시 상품 저장
        btn3h.addActionListener(e -> selectedProduct = TicketProduct.DAILY_3H);
        btn6h.addActionListener(e -> selectedProduct = TicketProduct.DAILY_6H);
        btn12h.addActionListener(e -> selectedProduct = TicketProduct.DAILY_12H);
        btn24h.addActionListener(e -> selectedProduct = TicketProduct.DAILY_24H);

        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JButton payBtn = new JButton("결제하기 (선택한 상품)");
        JButton backBtn = new JButton("뒤로가기");
        bottomPanel.add(payBtn);
        bottomPanel.add(backBtn);
        
        add(bottomPanel, BorderLayout.SOUTH);
        
        
        payBtn.addActionListener(e -> {
        	//상품 선택 안 했을 때
            if (selectedProduct == null) {
                JOptionPane.showMessageDialog(mainFrame, "상품을 선택해주세요!");
                return;
            }
                
             	//회원 -> 티켓 생성
                payment.TicketFactory factory = new payment.TicketFactory();
                if (mainFrame.getCurrentMember() != null) {
                    mainFrame.getCurrentMember().setTicket(factory.createTicket(selectedProduct));
                }

                JOptionPane.showMessageDialog(mainFrame, "당일권 구매 완료!");
                mainFrame.showPanel(KioskMainFrame.SEAT_MAP_PANEL);
            });
        
        backBtn.addActionListener(e -> {
            mainFrame.showPanel(KioskMainFrame.LOGIN_PANEL); 
        });
    }

        
}