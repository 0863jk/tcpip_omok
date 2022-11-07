package team_project;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.desktop.ScreenSleepEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import team_project.*;

public class ClientFrame extends JFrame implements ActionListener {
	
//https://kamang-it.tistory.com/entry/Swing-04Layout%EB%B0%B0%EC%B9%98%EA%B4%80%EB%A6%AC%EC%9E%90
	
	JPanel pGameBoard, pNorth, pSouth;
	JButton[] blkbtnBoard;
	boolean[] black, white;
	boolean blkTurn=true;
	
	String[] op = { "OK" };
	String user, mode;
	Socket socket = null;
	JButton btnSurrender;
	Font font = new Font("나눔스퀘어라운드 Bold", Font.PLAIN, 12);

	public ClientFrame(String title, int width, int height, int port, String id) {
		user = id;
		socket = Server.connAsCli(port);
		if(socket != null) {
			System.out.println(socket.toString());
		} else if(socket == null) {
			System.out.println("서버 생성 실패");
		}
		/*
		try {
			socket = new Socket("localhost", port);
			System.out.println(socket);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		setTitle(title);
		setSize(width, height); 
		setLocationRelativeTo(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBackground(new Color(220, 179, 92));
		
		// 게임판 패널, Center 위치
		pGameBoard = new JPanel();
		pGameBoard.setLayout(new GridLayout(13, 13));
		pGameBoard.setBorder(BorderFactory.createEmptyBorder(10,40,15,40));
		pGameBoard.setBackground(new Color(220, 179, 92));
		
		setpGameBoard();
		add(pGameBoard, BorderLayout.CENTER);
		
		
		// 방 호스트 정보 패널, North 위치
		pNorth = new JPanel();
		pNorth.setBackground(new Color(220, 179, 92));
		
		add(pNorth, BorderLayout.NORTH);
		
		
		// 클라이언트 정보 패널, South 위치
		pSouth = new JPanel();
		pSouth.setLayout(new FlowLayout());
		pSouth.setBackground(new Color(220, 179, 92));
		pSouth.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
		
		btnSurrender = new JButton("항복하기");
		btnSurrender.addActionListener(this);
		btnSurrender.setFont(font);
		pSouth.add(btnSurrender);
		
		add(pSouth, BorderLayout.SOUTH);
		
		setResizable(false);
		setVisible(true);
		
		mode = Server.recvMsg(socket);
		JOptionPane.showConfirmDialog(null, "상대가 고른 게임은 " + mode + "입니다.\n당신부터 시작합니다.", "흑돌", JOptionPane.INFORMATION_MESSAGE);
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
		
		black = new boolean[169];
		white = new boolean[169];
		
		for(int i = 0; i < black.length; i ++) {
			black[i] = false;
			white[i] = false;
		}
		
		blkbtnBoard = new JButton[169];
		
		for(int i = 0; i < blkbtnBoard.length; i++) {
			blkbtnBoard[i] = new JButton();
			ButtonSetting(blkbtnBoard[i]);
			
			if(i == 0) {
				blkbtnBoard[i].setIcon(CornerUpLeft);
			} else if(i > 0 && i < 12) {
				blkbtnBoard[i].setIcon(CornerUp);
			} else if(i == 12) {
				blkbtnBoard[i].setIcon(CornerUpRight);
			} else if(i % 13 == 0 && i != 156) {
				blkbtnBoard[i].setIcon(CornerLeft);
			} else if((i + 1) % 13 == 0 && i != 168) {
				blkbtnBoard[i].setIcon(CornerRight);
			} else if(i == 156) {
				blkbtnBoard[i].setIcon(CornerDownLeft);
			} else if(i > 156 && i < 168) {
				blkbtnBoard[i].setIcon(CornerDown);
			} else if(i == 168) {
				blkbtnBoard[i].setIcon(CornerDownRight);
			} else {
				blkbtnBoard[i].setIcon(Diagram); 
			}
			blkbtnBoard[i].addActionListener(this);
			pGameBoard.add(blkbtnBoard[i]);
		}
		
	}

	private void ButtonSetting(JButton btn) {
		btn.setBorderPainted(false);
		btn.setContentAreaFilled(false);
		btn.setFocusPainted(false);
		btn.setSize(24, 24);
		btn.setMaximumSize(getMinimumSize());
	}

	/*
	public static void main(String[] args) {
		new GameFrame3("백돌", 420, 460, 9999, socket);
	}
*/


	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			Object obj = e.getSource();
			
			if(obj == btnSurrender) {
				int confirm = JOptionPane.showConfirmDialog(null, "항복하고 해당 게임을 떠나시겠습니까?", "흑돌", JOptionPane.WARNING_MESSAGE);
				if(confirm == 0) {
					JOptionPane.showConfirmDialog(null, "패배했습니다.", "흑돌", JOptionPane.WARNING_MESSAGE);
					Server.sendMsg(socket, "lose");
					closeFrame();
				} else if(confirm == 1) {
					JOptionPane.showConfirmDialog(null, "다시 게임을 진행합니다.", "흑돌", JOptionPane.CLOSED_OPTION);
				}
			} else {
				for(int i = 0; i <blkbtnBoard.length; i ++) {
					if(obj == blkbtnBoard[i]) {
						if(white[i] == false && black[i] == false) {
							setBlkClick(i);
							if(checkDraw() == true) {
								Server.sendMsg(socket, "draw");
								Server.sendMsg(socket, i + "");
								updateWin("draw");
								closeFrame();
							} else if(checkDraw() == false) {
								if(checkWin(mode, i) == true) {
									JOptionPane.showConfirmDialog(null, "승리!", "흑돌", JOptionPane.INFORMATION_MESSAGE);
									Server.sendMsg(socket, "blackwin");
									Server.sendMsg(socket, i + "");
									updateWin("win"); // DB 업데이트
									closeFrame();
								} else if(checkWin(mode, i) == false) {
									Server.sendMsg(socket, i + "");
									JOptionPane.showMessageDialog(null, "상대방의 차례가 되었습니다.", "흑돌", JOptionPane.INFORMATION_MESSAGE);
									String inMsg = Server.recvMsg(socket);
									System.out.println("백돌: " + inMsg);
									if(inMsg.equals("lose")) {
										JOptionPane.showConfirmDialog(null, "상대방이 항복했습니다.\n승리!", "흑돌", JOptionPane.INFORMATION_MESSAGE);
										updateWin("win");
										closeFrame();
									} else if(inMsg.equals("whitewin")) {
										String num = Server.recvMsg(socket);
										setWhtClick(Integer.parseInt(num));
										
										updateWin("lose");
										JOptionPane.showMessageDialog(null, "상대방이 승리했습니다.", "흑돌", JOptionPane.INFORMATION_MESSAGE);
										closeFrame();
									} else if(inMsg.equals("draw")) {
										String num = Server.recvMsg(socket);
										setWhtClick(Integer.parseInt(num));
										
										updateWin("draw");
										JOptionPane.showMessageDialog(null, "무승부!", "흑돌", JOptionPane.INFORMATION_MESSAGE);
										closeFrame();
									} else {
										setWhtClick(Integer.parseInt(inMsg));
									}
								}
							}
						}
					}
				}
			}
		}catch(Exception e1) {
			e1.printStackTrace();
		}
	}

	private void closeFrame() {
		try {
			System.out.println("closeFrame 진입");
			socket.close();
			Thread.sleep(500);
			JOptionPane.showOptionDialog(null, "창을 종료합니다.", "흑돌", 0, 0, null, op, op[0]);
			new MainFrame("오목 게임", 400, 500, user);
			setVisible(false);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void updateWin(String string) {
		String select = "select * from users where id='" + user + "'";
		ResultSet rs = DB.getResultSet(select);
		int win = 0, lose = 0, draw = 0;
		
	
		try {
			System.out.println("데이터베이스 진입");
			if(rs.next()) {
				win = rs.getInt("win");
				lose = rs.getInt("lose");
				draw = rs.getInt("draw");
				System.out.println(user + ", " + win + ", " + lose);
				
				if(string.equals("win")) {
					int newWin = win + 1;
					String sql = "update users set win = " + newWin + " where id = '" + user + "'";
					DB.executeUpdate(sql);
					System.out.println("update 성공");
				
				} else if(string.equals("lose")) {
					int newLose = lose + 1;
					String sql = "update users set lose = " + newLose + " where id = '" + user + "'";
					DB.executeUpdate(sql);
					System.out.println("update 성공");
				} else if(string.equals("draw")) {
					int newDraw = draw + 1;
					String sql = "update users set draw = " + newDraw + " where id = '" + user + "'";
					DB.executeUpdate(sql);
					System.out.println("update 성공");
				}
			} else {
				System.out.println("에러!");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void setWhtClick(int whtClick) {
		// 서버에서 클릭한 버튼을 백돌로 바꿔주는 메소드
		ImageIcon whtCornerLeft = new ImageIcon("images/wht_Corner_Left.png");
		ImageIcon whtCornerRight = new ImageIcon("images/wht_Corner_Right.png");		
		ImageIcon whtCornerUp = new ImageIcon("images/wht_Corner_Up.png");
		ImageIcon whtCornerDown = new ImageIcon("images/wht_Corner_Down.png");
		ImageIcon whtCornerUpLeft = new ImageIcon("images/wht_Corner_Up_Left.png");
		ImageIcon whtCornerUpRight = new ImageIcon("images/wht_Corner_Up_Right.png");
		ImageIcon whtCornerDownLeft = new ImageIcon("images/wht_Corner_Down_Left.png");
		ImageIcon whtCornerDownRight = new ImageIcon("images/wht_Corner_Down_Right.png");
		ImageIcon whtDiagram = new ImageIcon("images/wht_Diagram.png");
		
		if(whtClick == 0) {
			blkbtnBoard[whtClick].setIcon(whtCornerUpLeft);
		} else if(whtClick > 0 && whtClick < 12) {
			blkbtnBoard[whtClick].setIcon(whtCornerUp);
		} else if(whtClick == 12) {
			blkbtnBoard[whtClick].setIcon(whtCornerUpRight);
		} else if(whtClick % 13 == 0 && whtClick != 156) {
			blkbtnBoard[whtClick].setIcon(whtCornerLeft);
		} else if((whtClick + 1) % 13 == 0 && whtClick != 168) {
			blkbtnBoard[whtClick].setIcon(whtCornerRight);
		} else if(whtClick == 156) {
			blkbtnBoard[whtClick].setIcon(whtCornerDownLeft);
		} else if(whtClick > 156 && whtClick < 168) {
			blkbtnBoard[whtClick].setIcon(whtCornerDown);
		} else if(whtClick == 168) {
			blkbtnBoard[whtClick].setIcon(whtCornerDownRight);
		} else {
			blkbtnBoard[whtClick].setIcon(whtDiagram); 
		}
		white[whtClick] = true;
		
	}
	
	private void setBlkClick(int blkClick) {
		// 클라이언트가 누른 버튼을 흑돌로 바꿔주는 메소드
		ImageIcon blkCornerLeft = new ImageIcon("images/blk_Corner_Left.png");
		ImageIcon blkCornerRight = new ImageIcon("images/blk_Corner_Right.png");		
		ImageIcon blkCornerUp = new ImageIcon("images/blk_Corner_Up.png");
		ImageIcon blkCornerDown = new ImageIcon("images/blk_Corner_Down.png");
		ImageIcon blkCornerUpLeft = new ImageIcon("images/blk_Corner_Up_Left.png");
		ImageIcon blkCornerUpRight = new ImageIcon("images/blk_Corner_Up_Right.png");
		ImageIcon blkCornerDownLeft = new ImageIcon("images/blk_Corner_Down_Left.png");
		ImageIcon blkCornerDownRight = new ImageIcon("images/blk_Corner_Down_Right.png");
		ImageIcon blkDiagram = new ImageIcon("images/blk_Diagram.png");
		
		if(blkClick == 0) {
			blkbtnBoard[blkClick].setIcon(blkCornerUpLeft);
		} else if(blkClick > 0 && blkClick < 12) {
			blkbtnBoard[blkClick].setIcon(blkCornerUp);
		} else if(blkClick == 12) {
			blkbtnBoard[blkClick].setIcon(blkCornerUpRight);
		} else if(blkClick % 13 == 0 && blkClick != 156) {
			blkbtnBoard[blkClick].setIcon(blkCornerLeft);
		} else if((blkClick + 1) % 13 == 0 && blkClick != 168) {
			blkbtnBoard[blkClick].setIcon(blkCornerRight);
		} else if(blkClick == 156) {
			blkbtnBoard[blkClick].setIcon(blkCornerDownLeft);
		} else if(blkClick > 156 && blkClick < 168) {
			blkbtnBoard[blkClick].setIcon(blkCornerDown);
		} else if(blkClick == 168) {
			blkbtnBoard[blkClick].setIcon(blkCornerDownRight);
		} else {
			blkbtnBoard[blkClick].setIcon(blkDiagram); 
		}
		black[blkClick] = true;
	}
	
	private boolean checkDraw() {
		int cnt = 0;
		for(int i = 0; i < black.length; i++) {
			if(black[i] == true || white[i] == true) {
				cnt++;
			}
		}
		if(cnt == black.length) {
			return true;
		} else {
			return false;
		}
	}

	private boolean checkWin(String mode, int i) {
		try {
			if(mode.equals("오목")) {
 				if((i < 165 && black[i] == true && black[i+1] == true && black[i+2] == true && black[i+3] == true && black[i+4] == true) ||
						(i < 166 && i > 0 && black[i-1] == true && black[i] == true && black[i+1] == true && black[i+2] == true && black[i+3] == true) ||
						(i < 167 && i > 1 && black[i-2] == true && black[i-1] == true && black[i] == true && black[i+1] == true && black[i+2] == true) ||
						(i < 168 && i > 2 && black[i-3] == true && black[i-2] == true && black[i-1] == true && black[i] == true && black[i+1] == true) ||
						(i < 169 && i > 3 && black[i-4] == true && black[i-3] == true && black[i-2] == true && black[i-1] == true && black[i] == true)) {
					// 가로로 다섯 개
 					if((i+4) % 13 == 0) {
 						if((i < 166 && black[i-1] == true && black[i] == true && black[i+1] == true && black[i+2] == true && black[i+3] == true) ||
 								(i < 167 && black[i-2] == true && black[i-1] == true && black[i] == true && black[i+1] == true && black[i+2] == true) ||
 								(i < 168 && black[i-3] == true && black[i-2] == true && black[i-1] == true && black[i] == true && black[i+1] == true) ||
 								(i < 169 && black[i-4] == true && black[i-3] == true && black[i-2] == true && black[i-1] == true && black[i] == true)) {
 							return true;
 						} else {
 							return false;
 						}
 					} else if((i+3) % 13 == 0) {
 						if((i < 169 && black[i-4] == true && black[i-3] == true && black[i-2] == true && black[i-1] == true && black[i] == true) ||
 							(i < 168 && black[i-3] == true && black[i-2] == true && black[i-1] == true && black[i] == true && black[i+1] == true) ||
 							(i < 167 && black[i-2] == true && black[i-1] == true && black[i] == true && black[i+1] == true && black[i+2] == true)) {
 							return true;
 						} else {
 							return false;
 						}
 					} else if((i+2) % 13 == 0) {
 						if((i < 169 && black[i-4] == true && black[i-3] == true && black[i-2] == true && black[i-1] == true && black[i] == true) ||
 								(i < 168 && black[i-3] == true && black[i-2] == true && black[i-1] == true && black[i] == true && black[i+1] == true)) {
 							return true;
 						} else {
 							return false;
 						}
 					} else if((i+1) % 13 == 0) {
 						if(black[i-4] == true && black[i-3] == true && black[i-2] == true && black[i-1] == true && black[i] == true) {
 							return true;
 						} else {
 							return false;
 						}
 					} else if(i % 13 == 0) {
 						if(black[i] == true && black[i+1] == true && black[i+2] == true && black[i+3] == true && black[i+4] == true) {
 							return true;
 						} else {
 							return false;
 						}
 					} else if((i-1) % 13 == 0) {
 						if((black[i] == true && black[i+1] == true && black[i+2] == true && black[i+3] == true && black[i+4] == true) ||
 							(black[i-1] == true && black[i] == true && black[i+1] == true && black[i+2] == true && black[i+3] == true)) {
 							return true;
 						} else {
 							return false;
 						}
 					} else if((i-2) % 13 == 0) {
 						if((black[i] == true && black[i+1] == true && black[i+2] == true && black[i+3] == true && black[i+4] == true) ||
 							(black[i-1] == true && black[i] == true && black[i+1] == true && black[i+2] == true && black[i+3] == true) ||
 							(black[i-2] == true && black[i-1] == true && black[i] == true && black[i+1] == true && black[i+2] == true)) {
 							return true;
 						} else {
 							return false;
 						}
 					} else if((i-3) % 13 == 0) {
 						if((black[i] == true && black[i+1] == true && black[i+2] == true && black[i+3] == true && black[i+4] == true) ||
 							(black[i-1] == true && black[i] == true && black[i+1] == true && black[i+2] == true && black[i+3] == true) ||
 							(black[i-2] == true && black[i-1] == true && black[i] == true && black[i+1] == true && black[i+2] == true) ||
 							(black[i-3] == true && black[i-2] == true && black[i-1] == true && black[i] == true && black[i+1] == true)) {
 							return true;
 						} else {
 							return false;
 						}
 					} else {
 						System.out.println("가로 5개 승리");
 						return true;
 					}
				} else if((i < 117 && black[i] == true && black[i+13] == true && black[i+26] == true && black[i+39] == true && black[i+52] == true) ||
						(i < 130 && i > 12 && black[i-13] == true && black[i] == true && black[i+13] == true && black[i+26] == true && black[i+39] == true) ||
						(i < 143 && i > 25 && black[i-26] == true && black[i-13] ==true && black[i] == true && black[i+13] == true && black[i+26] == true) ||
						(i < 156 && i > 38 && black[i-39] == true && black[i-26] == true && black[i-13] == true && black[i] == true && black[i+13] == true) ||
						(i < 169 && i > 51 && black[i-52] == true && black[i-39] == true && black[i-26] == true && black[i-13] == true && black[i] == true)) {
					System.out.println("세로 5개 승리");
					// 세로 다섯 개
					return true;
				} else if((i < 113 && black[i] == true && black[i+14] == true && black[i+28] == true && black[i+42] == true && black[i+56] == true) ||
						(i < 127 && i > 13 && black[i-14] == true && black[i] == true && black[i+14] == true && black[i+28] == true && black[i+42] == true) ||
						(i < 141 && i > 27 && black[i-28] == true && black[i-14] == true && black[i] == true && black[i+14] == true && black[i+28] == true) ||
						(i < 155 && i > 41 && black[i-42] == true && black[i-28] == true && black[i-14] == true && black[i] == true && black[i+14] == true) ||
						(i < 169 && i > 55 && black[i-56] == true && black[i-42] == true && black[i-28] == true && black[i-14] == true && black[i] == true)) {
					
					if(i < 113 && black[i] == true && black[i+14] == true && black[i+28] == true && black[i+42] == true && black[i+56] == true) {
						if((i+14) % 13 == 0 || (i+28) % 13 == 0 || (i+42) % 13 == 0 || (i+15) % 13 == 0 || (i+29) % 13 == 0 || (i+43) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else if(i < 127 && black[i-14] == true && black[i] == true && black[i+14] == true && black[i+28] == true && black[i+42] == true) {
						if(i % 13 == 0 || (i+14) % 13 == 0 || (i+28) % 13 == 0 || (i+1) % 13 == 0 || (i+15) % 13 == 0 || (i+29) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else if(i < 141 && black[i-28] == true && black[i-14] == true && black[i] == true && black[i+14] == true && black[i+28] == true) {
						if((i-14) % 13 == 0 || i % 13 == 0 || (i+14) % 13 == 0 || (i-13) % 13 == 0 || (i-1) % 13 == 0 || (i+15) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else if(i < 155 && black[i-42] == true && black[i-28] == true && black[i-14] == true && black[i] == true && black[i+14] == true) {
						if((i-28) % 13 == 0 || (i-14) % 13 == 0 || i % 13 == 0 || (i-27) % 13 == 0 || (i-13) % 13 == 0 || i % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else if(i < 169 && black[i-56] == true && black[i-42] == true && black[i-28] == true && black[i-14] == true && black[i] == true) {
						if((i-42) % 13 == 0 || (i-28) % 13 == 0 || (i-14) % 13 == 0 || (i-41) % 13 == 0 || (i-27) % 13 == 0 || (i-13) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else {
						System.out.println("오른쪽 대각선 승리");
						return true;
						// 오른쪽 대각선
					}
				} else if((i < 121 && black[i] == true && black[i+12] == true && black[i+24] == true && black[i+36] == true && black[i+48] == true) ||
						(i < 133 && i > 11 && black[i-12] == true && black[i] == true && black[i+12] == true && black[i+24] == true && black[i+36] == true) ||
						(i < 145 && i > 23 && black[i-24] == true && black[i-12] == true && black[i] == true && black[i+12] == true && black[i+24] == true) ||
						(i < 157 && i > 35 && black[i-36] == true && black[i-24] == true && black[i-12] == true && black[i] == true && black[i+12] == true) ||
						(i < 169 && i > 47 && black[i-48] == true && black[i-36] == true && black[i-24] == true && black[i-12] == true && black[i] == true)) {
					if(i < 121 && black[i] == true && black[i+12] == true && black[i+24] == true && black[i+36] == true && black[i+48] == true) {
						if((i+12) % 13 == 0 || (i+24) % 13 == 0 || (i+36) % 13 == 0 || (i+13) % 13 == 0 || (i+25) % 13 == 0 || (i+37) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else if(i < 133 && black[i-12] == true && black[i] == true && black[i+12] == true && black[i+24] == true && black[i+36] == true) {
						if(i % 13 == 0 || (i+12) % 13 == 0 || (i+24) % 13 == 0 || (i+1) % 13 == 0 || (i+13) % 13 == 0 || (i+25) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else if((i < 145 && black[i-24] == true && black[i-12] == true && black[i] == true && black[i+12] == true && black[i+24] == true)) {
						if((i-12) % 13 == 0 || i % 13 == 0 || (i+12) % 13 == 0 || (i-11) % 13 == 0 || (i+1) % 13 == 0 || (i+13) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else if(i < 157 && black[i-36] == true && black[i-24] == true && black[i-12] == true && black[i] == true && black[i+12] == true) {
						if((i-24) % 13 == 0 || (i-12) % 13 == 0 || i % 13 == 0 || (i-23) % 13 == 0 || (i-11) % 13 == 0 || (i+1) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else if(i < 169 && black[i-48] == true && black[i-36] == true && black[i-24] == true && black[i-12] == true && black[i] == true) {
						if((i-36) % 13 == 0 || (i-24) % 13 == 0 || (i-12) % 13 == 0 || (i-35) % 13 == 0 || (i-23) % 13 == 0 || (i-11) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					}
					System.out.println("왼쪽 대각선 승리");
					// 왼쪽 대각선
					return true;
				} else {
					return false;
				}
			} else if(mode.equals("삼목")) {
				if((i < 167 && black[i] == true && black[i+1] == true && black[i+2]) ||
						(i < 168 && i > 0 && black[i-1] == true && black[i] == true && black[i+1] == true) ||
						(i < 169 && i > 1 && black[i-2] == true && black[i-1] == true && black[i] == true)) {
					if((i+2)%13==0) {
						if((i < 168 && i > 0 && black[i-1] == true && black[i] == true && black[i+1] == true) ||
						(i < 169 && i > 1 &&black[i-2] == true && black[i-1] == true && black[i] == true)) {
							return true;
						} else {
							return false;
						}
					} else if((i+1)%13==0) {
						if(i > 1 && black[i-2] == true && black[i-1] == true && black[i] == true) {
							return true;
						} else {
							return false;
						}
					} else if(i % 13 == 0) {
						if(i < 167 && black[i] == true && black[i+1] == true && black[i+2]) {
							return true;
						} else {
							return false;
						}
					} else if((i-1) % 13 == 0) {
						if((i < 167 && black[i] == true && black[i+1] == true && black[i+2]) ||
							(i < 168 && i > 0 && black[i-1] == true && black[i] == true && black[i+1] == true)) {
							return true;
						} else {
							return false;
						}
					} else {
						System.out.println("가로 3개 승리");
						return true;
					}
					// 가로로 세 개
				} else if((i < 143 && black[i] == true && black[i+13] == true && black[i+26] == true) ||
						(i < 156 && i > 12 && black[i-13] == true && black[i] == true && black[i+13] == true) ||
						(i > 25 && black[i-26] == true && black[i-13] ==true && black[i] == true)) {
					System.out.println("세로 3개 승리");
					// 세로 다섯 개
					return true;
				} else if((i < 141 && black[i] == true && black[i+14] == true && black[i+28] == true) ||
						(i < 155 && i > 13 && black[i-14] == true && black[i] == true && black[i+14] == true) ||
						(i > 27 && black[i-28] == true && black[i-14] == true && black[i] == true)) {
					System.out.println("오른쪽 대각선 승리");
					// 오른쪽 대각선
					return true;
				} else if((i < 145 && black[i] == true && black[i+12] == true && black[i+24] == true) ||
						(i < 157 && i > 11 && black[i-12] == true && black[i] == true && black[i+12] == true) ||
						(i > 23 && black[i-24] == true && black[i-12] == true && black[i] == true)) {
					System.out.println("왼쪽 대각선 승리");
					// 왼쪽 대각선
					return true;
				} else {
					return false;
				}
			} else if(mode.equals("육목")){
				if((i < 164 && black[i] == true && black[i+1] == true && black[i+2] == true && black[i+3] == true && black[i+4] == true && black[i+5] == true) ||
						(i < 165 && i > 0 && black[i-1] == true && black[i] == true && black[i+1] == true && black[i+2] == true && black[i+3] == true && black[i+4] == true) ||
						(i < 166 && i > 1 && black[i-2] == true && black[i-1] == true && black[i] == true && black[i+1] == true && black[i+2] == true && black[i+3] == true) ||
						(i < 167 && i > 2 && black[i-3] == true && black[i-2] == true && black[i-1] == true && black[i] == true && black[i+1] == true && black[i+2] == true) ||
						(i < 168 && i > 3 && black[i-4] == true && black[i-3] == true && black[i-2] == true && black[i-1] == true && black[i] == true && black[i+1] == true) ||
						(i < 169 && i > 4 && black[i-5] == true && black[i-4] == true && black[i-3] == true && black[i-2] == true && black[i-1] == true && black[i] == true)) {
					// 가로로 여섯 개
					if((i+5) % 13 == 0) {
						if((i < 165 && i > 0 && black[i-1] == true && black[i] == true && black[i+1] == true && black[i+2] == true && black[i+3] == true && black[i+4] == true) ||
								(i < 166 && i > 1 && black[i-2] == true && black[i-1] == true && black[i] == true && black[i+1] == true && black[i+2] == true && black[i+3] == true) ||
								(i < 167 && i > 2 && black[i-3] == true && black[i-2] == true && black[i-1] == true && black[i] == true && black[i+1] == true && black[i+2] == true) ||
								(i < 168 && i > 3 && black[i-4] == true && black[i-3] == true && black[i-2] == true && black[i-1] == true && black[i] == true && black[i+1] == true) ||
								(i < 169 && i > 4 && black[i-5] == true && black[i-4] == true && black[i-3] == true && black[i-2] == true && black[i-1] == true && black[i] == true)) {
							return true;
						} else {
							return false;
						}
					} else if((i+4) % 13 == 0) {
						if((i < 166 && i > 1 && black[i-2] == true && black[i-1] == true && black[i] == true && black[i+1] == true && black[i+2] == true && black[i+3] == true) ||
								(i < 167 && i > 2 && black[i-3] == true && black[i-2] == true && black[i-1] == true && black[i] == true && black[i+1] == true && black[i+2] == true) ||
								(i < 168 && i > 3 && black[i-4] == true && black[i-3] == true && black[i-2] == true && black[i-1] == true && black[i] == true && black[i+1] == true) ||
								(i < 169 && i > 4 && black[i-5] == true && black[i-4] == true && black[i-3] == true && black[i-2] == true && black[i-1] == true && black[i] == true)) {
							return true;
						} else {
							return false;
						}
					} else if((i+3) % 13 == 0) {
						if((i < 167 && i > 2 && black[i-3] == true && black[i-2] == true && black[i-1] == true && black[i] == true && black[i+1] == true && black[i+2] == true) ||
								(i < 168 && i > 3 && black[i-4] == true && black[i-3] == true && black[i-2] == true && black[i-1] == true && black[i] == true && black[i+1] == true) ||
								(i < 169 && i > 4 && black[i-5] == true && black[i-4] == true && black[i-3] == true && black[i-2] == true && black[i-1] == true && black[i] == true)) {
							return true;
						} else {
							return false;
						}
					} else if((i + 2) % 13 == 0) {
						if((i < 168 && i > 3 && black[i-4] == true && black[i-3] == true && black[i-2] == true && black[i-1] == true && black[i] == true && black[i+1] == true) ||
						(i < 169 && i > 4 && black[i-5] == true && black[i-4] == true && black[i-3] == true && black[i-2] == true && black[i-1] == true && black[i] == true)) {
							return true;
						} else {
							return false;
						}
					} else if((i + 1) % 13 == 0) {
						if((i < 169 && i > 4 && black[i-5] == true && black[i-4] == true && black[i-3] == true && black[i-2] == true && black[i-1] == true && black[i] == true)) {
							return true;
						} else {
							return false;
						}
					} else if(i % 13 == 0) {
						if((i < 164 && black[i] == true && black[i+1] == true && black[i+2] == true && black[i+3] == true && black[i+4] == true && black[i+5] == true)) {
							return true;
						} else {
							return false;
						}
					} else if((i-1) % 13 == 0) {
						if((i < 164 && black[i] == true && black[i+1] == true && black[i+2] == true && black[i+3] == true && black[i+4] == true && black[i+5] == true) ||
						(i < 165 && i > 0 && black[i-1] == true && black[i] == true && black[i+1] == true && black[i+2] == true && black[i+3] == true && black[i+4] == true)) {
							return true;
						} else {
							return false;
						}
					} else if((i-2) % 13 == 0) {
						if((i < 164 && black[i] == true && black[i+1] == true && black[i+2] == true && black[i+3] == true && black[i+4] == true && black[i+5] == true) ||
						(i < 165 && i > 0 && black[i-1] == true && black[i] == true && black[i+1] == true && black[i+2] == true && black[i+3] == true && black[i+4] == true) ||
						(i < 166 && i > 1 && black[i-2] == true && black[i-1] == true && black[i] == true && black[i+1] == true && black[i+2] == true && black[i+3] == true)) {
							return true;
						} else {
							return false;
						}
					} else if((i-3) % 13 == 0) {
						if((i < 164 && black[i] == true && black[i+1] == true && black[i+2] == true && black[i+3] == true && black[i+4] == true && black[i+5] == true) ||
								(i < 165 && i > 0 && black[i-1] == true && black[i] == true && black[i+1] == true && black[i+2] == true && black[i+3] == true && black[i+4] == true) ||
								(i < 166 && i > 1 && black[i-2] == true && black[i-1] == true && black[i] == true && black[i+1] == true && black[i+2] == true && black[i+3] == true) ||
								(i < 167 && i > 2 && black[i-3] == true && black[i-2] == true && black[i-1] == true && black[i] == true && black[i+1] == true && black[i+2] == true)) {
							return true;
						} else {
							return false;
						}
					} else if((i-4) % 13 == 0) {
						if((i < 164 && black[i] == true && black[i+1] == true && black[i+2] == true && black[i+3] == true && black[i+4] == true && black[i+5] == true) ||
						(i < 165 && i > 0 && black[i-1] == true && black[i] == true && black[i+1] == true && black[i+2] == true && black[i+3] == true && black[i+4] == true) ||
						(i < 166 && i > 1 && black[i-2] == true && black[i-1] == true && black[i] == true && black[i+1] == true && black[i+2] == true && black[i+3] == true) ||
						(i < 167 && i > 2 && black[i-3] == true && black[i-2] == true && black[i-1] == true && black[i] == true && black[i+1] == true && black[i+2] == true) ||
						(i < 168 && i > 3 && black[i-4] == true && black[i-3] == true && black[i-2] == true && black[i-1] == true && black[i] == true && black[i+1] == true)) {
							return true;
						} else {
							return false;
						}
					} else {
						System.out.println("가로 6개 승리");
						return true;
					}
				} else if((i < 104 && black[i] == true && black[i+13] == true && black[i+26] == true && black[i+39] == true && black[i+52] == true && black[i+65] == true) ||
						(i < 117 && i > 12 && black[i-13] == true && black[i] == true && black[i+13] == true && black[i+26] == true && black[i+39] == true && black[i+52] == true) ||
						(i < 130 && i > 25 && black[i-26] == true && black[i-13] == true && black[i] == true && black[i+13] == true && black[i+26] == true && black[i+39] == true) ||
						(i < 143 && i > 38 && black[i-39] == true && black[i-26] == true && black[i-13] == true && black[i] == true && black[i+13] == true && black[i+26] == true) ||
						(i < 156 && i > 51 && black[i-52] == true && black[i-39] == true && black[i-26] == true && black[i-13] == true && black[i] == true && black[i+13] == true) ||
						(i < 169 && i > 64 && black[i-65] == true && black[i-52] == true && black[i-39] == true && black[i-26] == true && black[i-13] == true && black[i] == true)) {
					System.out.println("세로 6개 승리");
					// 세로 여섯 개
					return true;
				} else if((i < 99 && black[i] == true && black[i+14] == true && black[i+28] == true && black[i+42] == true && black[i+56] == true && black[i+70] == true) ||
						(i < 113 && i > 13 && black[i-14] == true && black[i] == true && black[i+14] == true && black[i+28] == true && black[i+42] == true && black[i+56] == true) ||
						(i < 127 && i > 27 && black[i-28] == true && black[i-14] == true && black[i] == true && black[i+14] == true && black[i+28] == true && black[i+42] == true) ||
						(i < 141 && i > 41 && black[i-42] == true && black[i-28] == true && black[i-14] == true && black[i] == true && black[i+14] == true && black[i+28] == true) ||
						(i < 155 && i > 55 && black[i-56] == true && black[i-42] == true && black[i-28] == true && black[i-14] == true && black[i] == true && black[i+14] == true) ||
						(i < 169 && i > 69 && black[i-70] == true && black[i-56] == true && black[i-42] == true && black[i-28] == true && black[i-14] == true && black[i] == true)) {
					if(i < 99 && black[i] == true && black[i+14] == true && black[i+28] == true && black[i+42] == true && black[i+56] == true && black[i+70] == true) {
						if((i+14) % 13 == 0 || (i+28) % 13 == 0 || (i+42) % 13 == 0 || (i+56) % 13 == 0 || (i+15) % 13 == 0 || (i+29) % 13 == 0 || (i+43) % 13 == 0 || (i+57) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else if(i < 113 && i > 13 && black[i-14] == true && black[i] == true && black[i+14] == true && black[i+28] == true && black[i+42] == true && black[i+56] == true) {
						if(i % 13 == 0 || (i+14) % 13 == 0 || (i+28) % 13 == 0 || (i+42) % 13 == 0 || (i+1) % 13 == 0 || (i+15) % 13 == 0 || (i+29) % 13 == 0 || (i+43) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else if(i < 127 && i > 27 && black[i-28] == true && black[i-14] == true && black[i] == true && black[i+14] == true && black[i+28] == true && black[i+42] == true) {
						if((i-14) % 13 == 0 || i % 13 == 0 || (i+14) % 13 == 0 || (i+28) % 13 == 0 || (i+1) % 13 == 0 || (i-13) % 13 == 0 || (i+15) % 13 == 0 || (i+29) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else if(i < 141 && i > 41 && black[i-42] == true && black[i-28] == true && black[i-14] == true && black[i] == true && black[i+14] == true && black[i+28] == true) {
						if((i-28) % 13 == 0 || (i-14) % 13 == 0 || i % 13 == 0 || (i+14) % 13 == 0 || (i-27) % 13 == 0 || (i-13) % 13 == 0 || (i+1) % 13 == 0 || (i+15) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else if(i < 155 && i > 55 && black[i-56] == true && black[i-42] == true && black[i-28] == true && black[i-14] == true && black[i] == true && black[i+14] == true) {
						if((i-42) % 13 == 0 || (i-28) % 13 == 0 || (i-14) % 13 == 0 || i % 13 == 0 || (i-41) % 13 == 0 || (i-27) % 13 == 0 || (i-13) % 13 == 0 || (i+1) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else if(i < 169 && i > 69 && black[i-70] == true && black[i-56] == true && black[i-42] == true && black[i-28] == true && black[i-14] == true && black[i] == true) {
						if((i-56) % 13 == 0 || (i-42) % 13 == 0 || (i-28) % 13 == 0 || (i-14) % 13 == 0 || (i-55) % 13 == 0 || (i-41) % 13 == 0 || (i-27) % 13 == 0 || (i-13) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else {
						System.out.println("오른쪽 대각선 승리");
						return true;
					}
				} else if((i < 109 && black[i] == true && black[i+12] == true && black[i+24] == true && black[i+36] == true && black[i+48] == true && black[i+60] == true) ||
						(i < 121 && i > 11 && black[i-12] == true && black[i] == true && black[i+12] == true && black[i+24] == true && black[i+36] == true && black[i+48] == true) ||
						(i < 133 && i > 23 && black[i-24] == true && black[i-12] == true && black[i] == true && black[i+12] == true && black[i+24] == true && black[i+36] == true) ||
						(i < 145 && i > 35 && black[i-36] == true && black[i-24] == true && black[i-12] == true && black[i] == true && black[i+12] == true && black[i+24] == true) ||
						(i < 157 && i > 47 && black[i-48] == true && black[i-36] == true && black[i-24] == true && black[i-12] == true && black[i] == true && black[i+12] == true) ||
						(i < 169 && i > 59 && black[i-60] == true && black[i-48] == true && black[i-36] == true && black[i-24] == true && black[i-12] == true && black[i] == true)) {
					if(i < 109 && black[i] == true && black[i+12] == true && black[i+24] == true && black[i+36] == true && black[i+48] == true && black[i+60] == true) {
						if((i+12) % 13 == 0 || (i+24) % 13 == 0 || (i+36) % 13 == 0 || (i+48) % 13 == 0 || (i+13) % 13 == 0 || (i+25) % 13 == 0 || (i+37) % 13 == 0 || (i+49) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else if(i < 121 && i > 11 && black[i-12] == true && black[i] == true && black[i+12] == true && black[i+24] == true && black[i+36] == true && black[i+48] == true) {
						if(i % 13 == 0 || (i+12) % 13 == 0 || (i+24) % 13 == 0 || (i+36) % 13 == 0 || (i+1) % 13 == 0 || (i+13) % 13 == 0 || (i+25) % 13 == 0 || (i+37) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else if(i < 133 && i > 23 && black[i-24] == true && black[i-12] == true && black[i] == true && black[i+12] == true && black[i+24] == true && black[i+36] == true) {
						if((i-12) % 13 == 0 || i % 13 == 0 || (i+12) % 13 == 0 || (i+24) % 13 == 0 || (i+1) % 13 == 0 || (i-11) % 13 == 0 || (i+13) % 13 == 0 || (i+25) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else if(i < 145 && i > 35 && black[i-36] == true && black[i-24] == true && black[i-12] == true && black[i] == true && black[i+12] == true && black[i+24] == true) {
						if((i-24) % 13 == 0 || (i-12) % 13 == 0 || i % 13 == 0 || (i+12) % 13 == 0 || (i-23) % 13 == 0 || (i-11) % 13 == 0 || (i+1) % 13 == 0 || (i+13) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else if(i < 157 && i > 47 && black[i-48] == true && black[i-36] == true && black[i-24] == true && black[i-12] == true && black[i] == true && black[i+12] == true) {
						if((i-36) % 13 == 0 || (i-24) % 13 == 0 || (i-12) % 13 == 0 || i % 13 == 0 || (i-35) % 13 == 0 || (i-23) % 13 == 0 || (i-11) % 13 == 0 || (i+1) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else if(i < 169 && i > 59 && black[i-60] == true && black[i-48] == true && black[i-36] == true && black[i-24] == true && black[i-12] == true && black[i] == true) {
						if((i-48) % 13 == 0 || (i-36) % 13 == 0 || (i-24) % 13 == 0 || (i-12) % 13 == 0 || (i-47) % 13 == 0 || (i-35) % 13 == 0 || (i-23) % 13 == 0 || (i-11) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else {
						// 왼쪽 대각선
						System.out.println("왼쪽 대각선 승리");
						return true;
					}
				} else {
					return false;
				}
			} else {
				return false;
			}
			
		} catch (ArrayIndexOutOfBoundsException e) {
			// TODO: handle exception
			//e.printStackTrace();
			//System.out.println(i +" black[] 범위에 벗어남!");
			return false;
		}
		
	}

}