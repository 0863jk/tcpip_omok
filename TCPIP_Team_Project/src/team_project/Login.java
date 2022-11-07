package team_project;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import team_project.DB;

public class Login extends JFrame implements ActionListener {

	private JTextField tfID;
	private JPasswordField tfPW;
	private JPanel panBase;
	private JPanel panNorth;
	private JPanel panCenter;
	private JPanel panSouth;
	private JButton btnLogin;
	private JButton btnSignup;
	private String inputID;
	private String inputPW;
	private JButton btnExit;
	Font font = new Font("나눔스퀘어라운드 Bold", Font.PLAIN, 12);

	public Login(String title, int width, int height) {
		setTitle(title);
		setSize(width, height); 
		setLocationRelativeTo(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// 화면설정
		setLayout(new BorderLayout());
		setResizable(false);
		
		createPanBase();
		add(panBase);
		
		
		setVisible(true);
	}
	
	private void createPanBase() {
		panBase = new JPanel(new BorderLayout(30, 30));
		panBase.setBackground(new Color(220, 179, 92));
		
		createPanNorth();
		createPanCenter();
		createPanSouth();
		
		//JPanel panWest = new JPanel();
		//JPanel panEast = new JPanel();
		
		panBase.add(panNorth, BorderLayout.NORTH);
		panBase.add(panCenter, BorderLayout.CENTER);
		panBase.add(panSouth, BorderLayout.SOUTH);
		
		//panBase.add(panEast, BorderLayout.EAST);
		//panBase.add(panWest, BorderLayout.WEST);
	}

	private void createPanNorth() {
		panNorth = new JPanel();
		panNorth.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
		
		ImageIcon logo = new ImageIcon("images/logo.png");
		JLabel lblImglogo = new JLabel(logo);
		
		panNorth.add(lblImglogo);
		panNorth.setBackground(new Color(220, 179, 92));
		
	}

	private void createPanCenter() {
		panCenter = new JPanel(new GridLayout(3, 1));
		panCenter.setBorder(BorderFactory.createEmptyBorder(0, 100, 0, 100));
		
		// 컴포넌트
		
		JLabel lblID = new JLabel("  I   D   ");
		JLabel lblPW = new JLabel("P W   ");
		
		lblID.setForeground(Color.WHITE);
		lblPW.setForeground(Color.WHITE);
		lblID.setFont(font);
		lblPW.setFont(font);
		
		tfID = new JTextField(12);
		tfPW = new JPasswordField(13);
		tfID.setFont(font);
		
		JPanel panID = new JPanel();
		panID.add(lblID);
		panID.add(tfID);
		
		JPanel panPW = new JPanel();
		panPW.add(lblPW);
		panPW.add(tfPW);
		
		JPanel panLogin = new JPanel();
		panLogin.add(panID);
		panLogin.add(panPW);
		
		btnLogin = new JButton("Log In");
		btnSignup = new JButton("Sign Up");
		
		btnLogin.setFont(font);
		btnSignup.setFont(font);
		
		btnLogin.addActionListener(this);
		btnSignup.addActionListener(this);
		
		JPanel panBtnLogin = new JPanel();
		panBtnLogin.setLayout(new BorderLayout());
		panBtnLogin.setBorder(BorderFactory.createEmptyBorder(15,0,0,0));
		panBtnLogin.add(btnLogin);
		
		JPanel panBtnSignup = new JPanel();
		panBtnSignup.setLayout(new BorderLayout());
		panBtnSignup.setBorder(BorderFactory.createEmptyBorder(15,0,0,0));
		panBtnSignup.add(btnSignup);
		
		panCenter.add(panLogin);
		panCenter.add(panBtnLogin);
		panCenter.add(panBtnSignup);
		
		panCenter.setBackground(new Color(220, 179, 92));
		panID.setBackground(new Color(220, 179, 92));
		panPW.setBackground(new Color(220, 179, 92));
		panLogin.setBackground(new Color(220, 179, 92));
		panBtnLogin.setBackground(new Color(220, 179, 92));
		panBtnSignup.setBackground(new Color(220, 179, 92));
	}
	
	
	private void createPanSouth() {
		panSouth = new JPanel();
		panSouth.setBackground(new Color(220, 179, 92));
		panSouth.setBorder(BorderFactory.createEmptyBorder(30,0,30,0));
	}
	
	
	public static void main(String[] args) {
		team_project.DB.init();
		new Login("Log In", 400, 500); 
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		
		if(obj == btnLogin) {
			boolean checkID = checkLoginInfo();
			if(checkID == true) {
				JOptionPane.showMessageDialog(null, "로그인 성공");
				this.setVisible(false);
				new MainFrame("오목 게임", 400, 500, inputID); 
			} else {
				JOptionPane.showMessageDialog(null, "로그인 실패");
				tfID.setText("");
				tfPW.setText("");
			}
			
		} else if(obj == btnSignup) {
			this.setVisible(false);
			new Signup("Sign Up", 400, 500); 
		}
	}

	private boolean checkLoginInfo() {
		
		inputID = tfID.getText();
		inputPW = tfPW.getText();

		boolean check = false;
		
		String sql = "SELECT * FROM USERS WHERE ID = '"+ inputID + "' AND PW = '" + inputPW + "'";
		System.out.println(sql);
		ResultSet rs = team_project.DB.getResultSet(sql);
		
		try {
			if(rs.next()) {
				System.out.println(rs.getString(1));
				check = true;
			} else {
				System.out.println("해당 사용자가 없습니다.");
				check = false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return check;
	}

}
