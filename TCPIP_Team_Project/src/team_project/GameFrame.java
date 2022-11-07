package team_project;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GameFrame extends JFrame {
	
//https://kamang-it.tistory.com/entry/Swing-04Layout%EB%B0%B0%EC%B9%98%EA%B4%80%EB%A6%AC%EC%9E%90
	
	JPanel pBG, pYellow, pGreen;
	JLabel lblID, lblPW, lblChk;
	JTextField tfID, tfPW;
	JCheckBox chk1, chk2;
	JButton btnOk;
	
	JPanel pGameBoard, pHostInfo, pClientInfo;
	JPanel pGoU, pGoUr, pGoBD, pGoI, pGoUl;
	
	JButton btnCornerUpLeft, btnCornerDownLeft, btnCornerUpRight, btnCornerDownRight;
	JButton[] btnCornerUp, btnCornerLeft, btnCornerRight, btnCornerDown, btnDiagram;
	
	public GameFrame(String title, int width, int height) {
		setTitle(title);
		setSize(width, height); 
		setLocationRelativeTo(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// 게임판 패널, Center 위치
		pGameBoard = new JPanel();
//		pGameBoard.setBackground(Color.blue);
		pGameBoard.setLayout(new GridLayout(6, 6));
		System.out.println(pGameBoard.getLayout());
		
		setpGameBoard();
		
		add(pGameBoard, BorderLayout.CENTER);
		
		
		// 방 호스트 정보 패널, North 위치
		pHostInfo = new JPanel();
		pHostInfo.setBackground(Color.cyan);
		
//		add(pHostInfo, BorderLayout.NORTH);
		
		
		
		// 클라이언트 정보 패널, South 위치
		pClientInfo = new JPanel();
		pClientInfo.setBackground(Color.darkGray);
		
//		add(pClientInfo, BorderLayout.SOUTH);
		
		
/*
		// ���̾ƿ�		
		Container c = getContentPane();
		c.setBackground(Color.GRAY);
		c.setLayout(null);
		
		pBG = new JPanel();
		pBG.setLayout(new GridLayout(1, 2, 20, 20));
		pBG.setBackground(Color.GRAY);
		
		pYellow = new JPanel();
		pYellow.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
		pYellow.setBackground(Color.YELLOW);
		
		lblID = new JLabel("Type ID");
		lblPW = new JLabel("Type Password");
		
		tfID = new JTextField("", 10);
		tfPW = new JTextField("", 10);
		
		pYellow.add(lblID);
		pYellow.add(tfID);
		pYellow.add(lblPW);
		pYellow.add(tfPW);
		
		pGreen = new JPanel();
		pGreen.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
		pGreen.setBackground(Color.GREEN);
		
		lblChk = new JLabel("Please Check!!");
		chk1 = new JCheckBox("C# JCheckBox");
		chk2 = new JCheckBox("C++ JCheckBox");
		
		pGreen.add(lblChk);
		pGreen.add(chk1);
		pGreen.add(chk2);
		
		pBG.add(pYellow);
		pBG.add(pGreen);
		pBG.setBounds(15, 15, 250, 190);
		
		btnOk = new JButton("OK");
		btnOk.setBounds(105, 220, 70, 30);
		
		c.add(pBG);
		c.add(btnOk);
		*/
		
		setResizable(false);
		setVisible(true);
	}
	
	private void setpGameBoard() {
		ImageIcon CornerLeft = new ImageIcon("images/Corner_Left.png");
		ImageIcon CornerRight = new ImageIcon("images/Corner_Right.png");		
		ImageIcon CornerUp = new ImageIcon("images/Corner_Up.png");
		ImageIcon CornerDown = new ImageIcon("images/Corner_Down.png");
		ImageIcon CornerUpLeft = new ImageIcon("images/Corner_Up_Left.png");
		ImageIcon CornerUpRight = new ImageIcon("images/Corner_Up_Right.png");
		ImageIcon CornerDownLeft = new ImageIcon("images/Corner_Down_Left.png");
		ImageIcon CornerDownRight = new ImageIcon("images/Corner_Down_Right.png");
		ImageIcon Diagram = new ImageIcon("images/Diagram.png");
		
		btnCornerUpLeft = new JButton(); // 「
		ButtonSetting(btnCornerUpLeft);
		btnCornerUpLeft.setIcon(CornerUpLeft);
		pGameBoard.add(btnCornerUpLeft);
		
		btnCornerUp = new JButton[4]; // -
		for(int i = 0; i < btnCornerUp.length; i++) {
			btnCornerUp[i] = new JButton();
			ButtonSetting(btnCornerUp[i]);
			btnCornerUp[i].setIcon(CornerUp);
			pGameBoard.add(btnCornerUp[i]);
		}
		
		btnCornerUpRight = new JButton(); // ㄱ
		ButtonSetting(btnCornerUpRight);
		btnCornerUpRight.setIcon(CornerUpRight);
		pGameBoard.add(btnCornerUpRight);
		
		btnCornerLeft = new JButton[4]; // ㅏ
		btnCornerRight = new JButton[4]; // ㅓ
		for(int i = 0; i < btnCornerLeft.length; i++) {
			btnCornerLeft[i] = new JButton();
			btnCornerRight[i] = new JButton();
			ButtonSetting(btnCornerLeft[i]);
			ButtonSetting(btnCornerRight[i]);
			btnCornerLeft[i].setIcon(CornerLeft);
			btnCornerRight[i].setIcon(CornerRight);
		}
		
		btnDiagram = new JButton[16]; // +
		for(int i = 0; i < btnDiagram.length; i++) {
			btnDiagram[i] = new JButton();
			ButtonSetting(btnDiagram[i]);
			btnDiagram[i].setIcon(Diagram);
		}

		for(int i = 0; i < btnCornerLeft.length; i++) {
			pGameBoard.add(btnCornerLeft[i]);
			
			for(int j = 0; j < btnCornerLeft.length; j++) {
				pGameBoard.add(btnDiagram[j]);
			}
			
			pGameBoard.add(btnCornerRight[i]);
		}
		
		btnCornerDownLeft = new JButton(); // ㄴ
		ButtonSetting(btnCornerDownLeft);
		btnCornerDownLeft.setIcon(CornerDownLeft);
		pGameBoard.add(btnCornerDownLeft);
		
		btnCornerDown = new JButton[4]; // -
		for(int i = 0; i < btnCornerDown.length; i++) {
			btnCornerDown[i] = new JButton();
			ButtonSetting(btnCornerDown[i]);
			btnCornerDown[i].setIcon(CornerDown);
			pGameBoard.add(btnCornerDown[i]);
		}
		
		btnCornerDownRight = new JButton(); // 」
		ButtonSetting(btnCornerDownRight);
		btnCornerDownRight.setIcon(CornerDownRight);
		pGameBoard.add(btnCornerDownRight);
		
	}

	private void ButtonSetting(JButton btn) {
		
//		btn.setBorderPainted(false);
//		btn.setContentAreaFilled(false);
//		btn.setFocusPainted(false);
		btn.setMinimumSize(getMinimumSize());
		
	}

	public static void main(String[] args) {
		new GameFrame("Container & Component", 700, 800);
	}

}
