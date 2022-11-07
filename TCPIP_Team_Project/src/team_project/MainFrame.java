package team_project;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class MainFrame extends JFrame implements ActionListener {

   String user;
   private JPanel panBase;
   private JPanel panNorth;
   private JPanel panCenter;
   private JPanel panSouth;
   private JButton btnServer;
   private JButton btnClient;
   private JButton btnShowRecord;
   private int portNum;
private JButton btnLogout;
Font font = new Font("나눔스퀘어라운드 Bold", Font.PLAIN, 12);

   public MainFrame(String title, int width, int height, String id) {
      
     team_project.DB.init();
    
     user = id;
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
      
      panBase.add(panNorth, BorderLayout.NORTH);
      panBase.add(panCenter, BorderLayout.CENTER);
      panBase.add(panSouth, BorderLayout.SOUTH);
   }

   private void createPanNorth() {
      panNorth = new JPanel();
      panNorth.setBorder(BorderFactory.createEmptyBorder(50, 10, 10, 10));
      
      ImageIcon logo = new ImageIcon("images/logo.png");
      JLabel lblImglogo = new JLabel(logo);
      
      panNorth.add(lblImglogo);
      panNorth.setBackground(new Color(220, 179, 92));
      
   }

   private void createPanCenter() {
      panCenter = new JPanel(new GridLayout(2, 1));
      panCenter.setBorder(BorderFactory.createEmptyBorder(0, 100, 95, 100));
      
      // 컴포넌트
      
      btnServer = new JButton("방 만들기");
      btnClient = new JButton("방 참여하기");
      
      btnServer.setFont(font);
      btnClient.setFont(font);
      
      btnServer.addActionListener(this);
      btnClient.addActionListener(this);
      
      JPanel panBtnServer = new JPanel();
      panBtnServer.setLayout(new BorderLayout());
      panBtnServer.setBorder(BorderFactory.createEmptyBorder(15,0,0,0));
      panBtnServer.add(btnServer);
      
      JPanel panBtnClient = new JPanel();
      panBtnClient.setLayout(new BorderLayout());
      panBtnClient.setBorder(BorderFactory.createEmptyBorder(15,0,0,0));
      panBtnClient.add(btnClient);
      
      panCenter.add(panBtnServer);
      panCenter.add(panBtnClient);
      
      panCenter.setBackground(new Color(220, 179, 92));
      panBtnServer.setBackground(new Color(220, 179, 92));
      panBtnClient.setBackground(new Color(220, 179, 92));
   }
   
   
   private void createPanSouth() {
      panSouth = new JPanel(new FlowLayout());
      panSouth.setBackground(new Color(220, 179, 92));
      
      btnShowRecord = new JButton("전적 보기");
      btnShowRecord.setFont(font);
      panSouth.add(btnShowRecord);
      btnLogout = new JButton("로그아웃");
      btnLogout.setFont(font);
      panSouth.add(btnLogout);
      
      btnShowRecord.addActionListener(this);
      btnLogout.addActionListener(this);
   }
   
   /*
   public static void main(String[] args) {
      new MainFrame("오목 게임", 400, 500); 
   }
   */

   @Override
   public void actionPerformed(ActionEvent e) {
      Object obj = e.getSource();
      
      if(obj == btnServer) {
         JOptionPane.showMessageDialog(null, "9190번으로 방을 만들었습니다.", "방 만들기", JOptionPane.INFORMATION_MESSAGE);
         this.setVisible(false);
         new ServerFrame("백돌", 420, 460, 9190, user);
      } else if(obj == btnClient) {
    	  JOptionPane.showMessageDialog(null, "9190번 방으로 입장합니다.", "방 참여하기", JOptionPane.INFORMATION_MESSAGE);
         this.setVisible(false);
         new ClientFrame("흑돌", 420, 460, 9190, user);
      }
      else if(obj == btnShowRecord) {
         
       String sql = "SELECT * FROM USERS WHERE ID = '" + user + "'" ;
        System.out.println(sql);
        ResultSet rs = team_project.DB.getResultSet(sql);
        
        try {
         if(rs.next()) {
            String name = rs.getString("NAME");
            int win = rs.getInt("win");
            int lose = rs.getInt("lose");
            int draw = rs.getInt("draw");
            JOptionPane.showMessageDialog(null, name + "님의 전적 기록은 \n 승리 " + win + "회, 패배 " + lose + "회, 무승부 "+draw+"회입니다.", "전적 기록", JOptionPane.INFORMATION_MESSAGE);
            
         }else {
            System.out.println("찾을 수 없습니다.");
         }
       } catch (SQLException e1) {
    	  e1.printStackTrace();
       }
        
      } else if(obj == btnLogout) {
    	setVisible(false);
  		new Login("Log In", 400, 500); 
      }
   }

}