package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import payment.PriceManager;
import payment.TicketFactory;
import payment.TicketProduct;

public class TimePassPanel extends JPanel{
	 private KioskMainFrame mainFrame;
	 private PriceManager priceManager;
	 private TicketProduct selectedProduct = null;

	    public TimePassPanel(KioskMainFrame mainFrame, PriceManager priceManager) {
	        this.mainFrame = mainFrame;
	        this.priceManager = priceManager;

	        setLayout(new BorderLayout(10, 10));
	        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

	        add(new JLabel("구매하실 시간권을 선택하세요."), BorderLayout.NORTH);

	        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));

	        // 시간권 탭 
	        JPanel timePanel = new JPanel(new GridLayout(4, 1, 10, 10)); 
	        JButton btn50h = new JButton(TicketProduct.TIME_50H.toString() + " (" + priceManager.getPrice(TicketProduct.TIME_50H) + "원)"); 
	        JButton btn100h = new JButton(TicketProduct.TIME_100H.toString() + " (" + priceManager.getPrice(TicketProduct.TIME_100H) + "원)"); 
	        JButton btn200h = new JButton(TicketProduct.TIME_200H.toString() + " (" + priceManager.getPrice(TicketProduct.TIME_200H) + "원)");
	      
	        timePanel.add(btn50h);
	        timePanel.add(btn100h);
	        timePanel.add(btn200h);
	        add(timePanel, BorderLayout.CENTER);
	        
	        // 상품 선택 저장
	        btn50h.addActionListener(e -> selectedProduct = TicketProduct.TIME_50H);
	        btn100h.addActionListener(e -> selectedProduct = TicketProduct.TIME_100H);
	        btn200h.addActionListener(e -> selectedProduct = TicketProduct.TIME_200H);
	        
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

	            JOptionPane.showMessageDialog(mainFrame, "시간권 구매 완료!");
	            mainFrame.showPanel(KioskMainFrame.SEAT_MAP_PANEL);
	        });

	        backBtn.addActionListener(e -> {
	            mainFrame.showPanel(KioskMainFrame.TICKET_SELECTION_PANEL); 
	        });
	    }

}
