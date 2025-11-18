package gui;

import ReadingRoomLogin.Member;
import ReadingRoomLogin.MemberManager;

import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import java.awt.GridLayout;

public class LoginPanel extends JPanel {

    private JTextField idField;
    private JPasswordField pwField;
    private KioskMainFrame mainFrame; 
    private MemberManager manager;

    public LoginPanel(KioskMainFrame mainFrame, MemberManager manager) {
        this.mainFrame = mainFrame;
        this.manager = manager;

        setLayout(new GridLayout(5, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        add(new JLabel("아이디:"));
        idField = new JTextField();
        add(idField);

        add(new JLabel("비밀번호:"));
        pwField = new JPasswordField();
        add(pwField);

        JButton loginBtn = new JButton("로그인");
        JButton registerBtn = new JButton("신규 회원가입");
        JButton nonMemberBtn = new JButton("비회원 당일 시간제 이용");

        add(loginBtn);
        add(registerBtn);
        add(new JLabel());
        add(nonMemberBtn);

        // 로그인 버튼 
        loginBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            String pw = new String(pwField.getPassword());

            Member member = manager.findMemberById(id); // MemberManager 로직 호출
           // 11/17 관리자 로그인 로직 변경
            if (id.equals("admin") && pw.equals("admin")) {
                JOptionPane.showMessageDialog(mainFrame, "관리자 로그인 성공!");
                new AdminFrame(manager).setVisible(true); 
            } else if (member != null && member.checkPassword(pw)) {
                mainFrame.setCurrentMember(member); 
                
                JOptionPane.showMessageDialog(mainFrame, member.getName() + "님, 환영합니다!");
                mainFrame.showPanel(KioskMainFrame.MAIN_MENU_PANEL);
                
            } else {
                JOptionPane.showMessageDialog(mainFrame, "아이디 또는 비밀번호가 틀렸습니다.");
            }
            
            pwField.setText("");
        });

        // 회원가입 버튼 
        registerBtn.addActionListener(e -> {
            new RegisterFrame(manager).setVisible(true);
        });
        
        // 비회원 이용 버튼 
        nonMemberBtn.addActionListener(e -> {
            mainFrame.setCurrentMember(null); 
            JOptionPane.showMessageDialog(mainFrame, "당일 시간제 이용 화면으로 이동합니다.");
            mainFrame.showPanel(KioskMainFrame.DAILY_TICKET_PANEL);
        });
    }
}
