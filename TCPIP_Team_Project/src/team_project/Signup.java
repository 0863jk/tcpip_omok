package team_project;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Signup extends JFrame implements ActionListener {

	private JTextField tfNewID;
	private JPasswordField tfNewPW;
	private JPanel panBase;
	private JPanel panNorth;
	private JPanel panCenter;
	private JPanel panSouth;
	private JButton btnSubmit;
	private JTextField tfName;
	Font font = new Font("나눔스퀘어라운드 Bold", Font.PLAIN, 12);

	public Signup(String title, int width, int height) {
		setTitle(title);
		setSize(width, height); 
		setLocationRelativeTo(this);
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
		panCenter.setBorder(BorderFactory.createEmptyBorder(0, 100, 50, 100));
		//panCenter.setBackground(Color.CYAN);
		
		// 컴포넌트
		JPanel panInfo = new JPanel();
		JPanel panName = new JPanel();
		
		JLabel lblID = new JLabel("  I   D   ");
		JLabel lblPW = new JLabel("P W   ");
		JLabel lblName = new JLabel("NAME ");
		
		lblName.setForeground(Color.WHITE);
		lblName.setFont(font);
		
		tfName = new JTextField(12);
		tfName.setFont(font);
		
		panName.add(lblName);
		panName.add(tfName);
		
		panInfo.add(panName);
		
		lblID.setForeground(Color.WHITE);
		lblPW.setForeground(Color.WHITE);
		lblID.setFont(font);
		lblPW.setFont(font);
		
		tfNewID = new JTextField(12);
		tfNewPW = new JPasswordField(12);

		tfNewID.setFont(font);
		
		JPanel panID = new JPanel();
		panID.add(lblID);
		panID.add(tfNewID);
		
		JPanel panPW = new JPanel();
		panPW.add(lblPW);
		panPW.add(tfNewPW);
		
		JPanel panLogin = new JPanel();
		panLogin.add(panID);
		panLogin.add(panPW);
		
		btnSubmit = new JButton("Submit");
		btnSubmit.setFont(font);
		
		btnSubmit.addActionListener(this);
		
		JPanel panBtnSubmit = new JPanel();
		panBtnSubmit.setLayout(new BorderLayout());
		panBtnSubmit.setBorder(BorderFactory.createEmptyBorder(0,0,15,0));
		panBtnSubmit.add(btnSubmit);
		
		panCenter.add(panLogin);
		panCenter.add(panInfo);
		panCenter.add(panBtnSubmit);
		
		panCenter.setBackground(new Color(220, 179, 92));
		panName.setBackground(new Color(220, 179, 92));
		panInfo.setBackground(new Color(220, 179, 92));
		panID.setBackground(new Color(220, 179, 92));
		panPW.setBackground(new Color(220, 179, 92));
		panLogin.setBackground(new Color(220, 179, 92));
		panBtnSubmit.setBackground(new Color(220, 179, 92));
	}
	
	
	private void createPanSouth() {
		panSouth = new JPanel();
		panSouth.setBackground(new Color(220, 179, 92));
		
	}
	
	/*
	public static void main(String[] args) {
		new signup("Sign Up", 400, 500); 
	}
	*/
	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		
		if(obj == btnSubmit) {
			String name = tfName.getText();
			String ID = tfNewID.getText();
			String PW = tfNewPW.getText();
			if(name.equals("") || ID.equals("") || PW.equals("")) {
				JOptionPane.showMessageDialog(null, "회원가입 실패\n모든 항목을 채워주세요.");
			} else {
				String checkID = "SELECT * FROM USERS WHERE ID = '" + ID + "'";
				ResultSet rs = DB.getResultSet(checkID);
				try {
					if(rs.next()) {
						JOptionPane.showMessageDialog(null, "이미 존재하는 ID입니다.");
					} else {
						makeAccount();
					}
				} catch (SQLException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				

			}
			
		}
	}

	private void makeAccount() {
		String name = tfName.getText();
		String ID = tfNewID.getText();
		String PW = tfNewPW.getText();
		
		String newUser = "INSERT INTO USERS\r\n"
				+ "VALUES('" + ID + "', '" + PW + "', '" + name + "', 0, 0, 0)";
		System.out.println(newUser);
		DB.getResultSet(newUser);
		
		String confirm = "SELECT ID, PW, NAME\r\n"
				+ "FROM USERS\r\n"
				+ "WHERE ID='" + ID + "'";
		ResultSet rs = DB.getResultSet(confirm);
		
		try {
			if(rs.next()) {
				JOptionPane.showMessageDialog(null, "회원가입 성공\n로그인 창으로 이동합니다.");
				this.setVisible(false);
				new Login("Log In", 400, 500); 
			} else {
				JOptionPane.showMessageDialog(null, "회원가입 실패\n오류 발생");
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
