package team_project;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
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


public class ServerFrame extends JFrame implements MouseListener{
	
//https://kamang-it.tistory.com/entry/Swing-04Layout%EB%B0%B0%EC%B9%98%EA%B4%80%EB%A6%AC%EC%9E%90
	
	JPanel pGameBoard, pNorth, pSouth;
	JButton[] whtbtnBoard;
	boolean[] black, white;
	String[] op = { "OK" };
	String user, mode;
	Socket socket = null;
	JButton btnSurrender;
	Font font = new Font("나눔스퀘어라운드 Bold", Font.PLAIN, 12);
	
	public ServerFrame(String title, int width, int height, int port, String id) {
		socket = Server.openServer(port);
		if(socket != null) {
			System.out.println(socket.toString());
		} else if(socket == null) {
			System.out.println("서버 생성 실패");
		}
		user = id;
		setTitle(title);
		setSize(width, height); 
		setLocationRelativeTo(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
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
		btnSurrender.setFont(font);
		btnSurrender.addMouseListener(this);
		pSouth.add(btnSurrender);
		
		add(pSouth, BorderLayout.SOUTH);
		setResizable(false);
		setVisible(true);
		
		String[] arrMode = {"삼목", "오목", "육목"};
		mode = (String)JOptionPane.showInputDialog(null, "모드를 선택해주세요!", "백돌", JOptionPane.INFORMATION_MESSAGE, null, arrMode, arrMode[1]);
		Server.sendMsg(socket, mode);
		JOptionPane.showMessageDialog(null, "상대방의 수를 기다립니다.", "백돌", JOptionPane.INFORMATION_MESSAGE);
		/*
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		String inMsg = Server.recvMsg(socket);
		setBoard(inMsg);
	}
	
	private void setBoard(String inMsg) {
		System.out.println("흑돌: " + inMsg);
		int blkClick = Integer.parseInt(inMsg);
		
		if(white[blkClick] == false) { // 백돌이 놓여있지 않다면
			setBlkClick(blkClick); // 흑돌을 놓는다
		}
		
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
		
		whtbtnBoard = new JButton[169];
		
		for(int i = 0; i < whtbtnBoard.length; i++) {
			whtbtnBoard[i] = new JButton();
			ButtonSetting(whtbtnBoard[i]);
			
			if(i == 0) {
				whtbtnBoard[i].setIcon(CornerUpLeft);
			} else if(i > 0 && i < 12) {
				whtbtnBoard[i].setIcon(CornerUp);
			} else if(i == 12) {
				whtbtnBoard[i].setIcon(CornerUpRight);
			} else if(i % 13 == 0 && i != 156) {
				whtbtnBoard[i].setIcon(CornerLeft);
			} else if((i + 1) % 13 == 0 && i != 168) {
				whtbtnBoard[i].setIcon(CornerRight);
			} else if(i == 156) {
				whtbtnBoard[i].setIcon(CornerDownLeft);
			} else if(i > 156 && i < 168) {
				whtbtnBoard[i].setIcon(CornerDown);
			} else if(i == 168) {
				whtbtnBoard[i].setIcon(CornerDownRight);
			} else {
				whtbtnBoard[i].setIcon(Diagram); 
			}
			whtbtnBoard[i].addMouseListener(this);
			pGameBoard.add(whtbtnBoard[i]);
		}
		
		black = new boolean[169];
		white = new boolean[169];
		
		for(int i = 0; i < black.length; i ++) {
			black[i] = false;
			white[i] = false;
		}
	}

	private void ButtonSetting(JButton btn) {
		btn.setBorderPainted(false);
		btn.setContentAreaFilled(false);
		btn.setFocusPainted(false);
		btn.setSize(24, 24);
	}
	
	private void updateWin(String string) {
		String select = "select * from users where id='" + user + "'";
		System.out.println(select);
		ResultSet rs = DB.getResultSet(select);
		int win = 0, lose = 0, draw = 0;
		
		try {
			if(rs.next()) {
				System.out.println("데이터베이스 진입");
				win = rs.getInt("win");
				lose = rs.getInt("lose");
				draw = rs.getInt("draw");
				System.out.println(user + ", " + win + ", " + lose + ", " + draw);
				
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
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	private void setWhtClick(int whtClick) {
		ImageIcon whtCornerLeft = new ImageIcon("images/wht_Corner_Left.png");
		ImageIcon whtCornerRight = new ImageIcon("images/wht_Corner_Right.png");		
		ImageIcon whtCornerUp = new ImageIcon("images/wht_Corner_Up.png");
		ImageIcon whtCornerDown = new ImageIcon("images/wht_Corner_Down.png");
		ImageIcon whtCornerUpLeft = new ImageIcon("images/wht_Corner_Up_Left.png");
		ImageIcon whtCornerUpRight = new ImageIcon("images/wht_Corner_Up_Right.png");
		ImageIcon whtCornerDownLeft = new ImageIcon("images/wht_Corner_Down_Left.png");
		ImageIcon whtCornerDownRight = new ImageIcon("images/wht_Corner_Down_Right.png");
		ImageIcon whtDiagram = new ImageIcon("images/wht_Diagram.png");
		
		if(black[whtClick] == false) {
			if(whtClick == 0) {
				whtbtnBoard[whtClick].setIcon(whtCornerUpLeft);
			} else if(whtClick > 0 && whtClick < 12) {
				whtbtnBoard[whtClick].setIcon(whtCornerUp);
			} else if(whtClick == 12) {
				whtbtnBoard[whtClick].setIcon(whtCornerUpRight);
			} else if(whtClick % 13 == 0 && whtClick != 156) {
				whtbtnBoard[whtClick].setIcon(whtCornerLeft);
			} else if((whtClick + 1) % 13 == 0 && whtClick != 168) {
				whtbtnBoard[whtClick].setIcon(whtCornerRight);
			} else if(whtClick == 156) {
				whtbtnBoard[whtClick].setIcon(whtCornerDownLeft);
			} else if(whtClick > 156 && whtClick < 168) {
				whtbtnBoard[whtClick].setIcon(whtCornerDown);
			} else if(whtClick == 168) {
				whtbtnBoard[whtClick].setIcon(whtCornerDownRight);
			} else {
				whtbtnBoard[whtClick].setIcon(whtDiagram); 
			}
			white[whtClick] = true;
		}
		
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
			whtbtnBoard[blkClick].setIcon(blkCornerUpLeft);
		} else if(blkClick > 0 && blkClick < 12) {
			whtbtnBoard[blkClick].setIcon(blkCornerUp);
		} else if(blkClick == 12) {
			whtbtnBoard[blkClick].setIcon(blkCornerUpRight);
		} else if(blkClick % 13 == 0 && blkClick != 156) {
			whtbtnBoard[blkClick].setIcon(blkCornerLeft);
		} else if((blkClick + 1) % 13 == 0 && blkClick != 168) {
			whtbtnBoard[blkClick].setIcon(blkCornerRight);
		} else if(blkClick == 156) {
			whtbtnBoard[blkClick].setIcon(blkCornerDownLeft);
		} else if(blkClick > 156 && blkClick < 168) {
			whtbtnBoard[blkClick].setIcon(blkCornerDown);
		} else if(blkClick == 168) {
			whtbtnBoard[blkClick].setIcon(blkCornerDownRight);
		} else {
			whtbtnBoard[blkClick].setIcon(blkDiagram); 
		}
		black[blkClick] = true;
	}

/*
	public static void main(String[] args) {
		new ServerFrame("백돌", 420, 460, 9190);
	}
*/

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		try {
			Object obj = e.getSource();
			
			if(obj == btnSurrender) {
				int confirm = JOptionPane.showConfirmDialog(null, "항복하고 해당 게임을 떠나시겠습니까?", "백돌", JOptionPane.WARNING_MESSAGE);
				if(confirm == 0) {
					JOptionPane.showConfirmDialog(null, "패배했습니다.", "백돌", JOptionPane.WARNING_MESSAGE);
					Server.sendMsg(socket, "lose");
					closeFrame();
				} else if(confirm == 1) {
					JOptionPane.showConfirmDialog(null, "다시 게임을 진행합니다.", "백돌", JOptionPane.CLOSED_OPTION);
				}
			} else {
				for(int i = 0; i < whtbtnBoard.length; i ++) {
					if(obj == whtbtnBoard[i]) {
						if(black[i] == false && white[i] == false) {
							setWhtClick(i);
							if(checkDraw() == true) {
								Server.sendMsg(socket, "draw");
								Server.sendMsg(socket, i + "");
								updateWin("draw");
								closeFrame();
							} else if(checkDraw() == false) {
								if(checkWin(mode, i) == true) {
									JOptionPane.showConfirmDialog(null, "승리!", "백돌", JOptionPane.INFORMATION_MESSAGE);
									Server.sendMsg(socket, "whitewin");
									Server.sendMsg(socket, i + "");
									updateWin("win");
									Thread.sleep(500);
									closeFrame();
									
								} else if(checkWin(mode, i) == false) {
									Server.sendMsg(socket, i + "");
									JOptionPane.showMessageDialog(null, "상대방의 차례가 되었습니다.", "백돌", JOptionPane.INFORMATION_MESSAGE);
									String inMsg = Server.recvMsg(socket);
									System.out.println("흑돌: " + inMsg);
									if(inMsg.equals("lose")) {
										JOptionPane.showConfirmDialog(null, "상대방이 항복했습니다.\n승리!", "백돌", JOptionPane.INFORMATION_MESSAGE);
										updateWin("win");
										closeFrame();
									} else if(inMsg.equals("blackwin")) {
										Thread.sleep(100);
										String num = Server.recvMsg(socket);
										setBlkClick(Integer.parseInt(num));
										
										JOptionPane.showMessageDialog(null, "상대방이 승리했습니다.", "백돌", JOptionPane.INFORMATION_MESSAGE);
										updateWin("lose");
										closeFrame();
									} else if(inMsg.equals("draw")) {
										String num = Server.recvMsg(socket);
										setWhtClick(Integer.parseInt(num));
										
										updateWin("draw");
										JOptionPane.showMessageDialog(null, "무승부!", "백돌", JOptionPane.INFORMATION_MESSAGE);
										closeFrame();
									} else {
										setBlkClick(Integer.parseInt(inMsg));
									}
								}
							}
						} else if(black[i] == true) {
							JOptionPane.showConfirmDialog(null, "상대방의 바둑돌이 이미 존재합니다.", "백돌", JOptionPane.WARNING_MESSAGE);
						} else if(white[i] == true) {
							JOptionPane.showMessageDialog(null, "당신의 바둑돌이 이미 존재합니다.", "백돌", JOptionPane.WARNING_MESSAGE);
						}
					} 
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	
	}

	private void closeFrame() {
		try {
			JOptionPane.showOptionDialog(null, "창을 종료합니다.", "백돌", 0, 0, null, op, op[0]);
			setVisible(false);
			new MainFrame("오목 게임", 400, 500, user);
			socket.close();
			Server.closeSock();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	private boolean checkDraw() {
		int cnt = 0;
		for(int i = 0; i < white.length; i++) {
			if(black[i] == true || white[i] == true) {
				cnt++;
			}
		}
		if(cnt == white.length) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean checkWin(String mode, int i) {
//		System.out.println("CheckWin 진입");
		try {
			if(mode.equals("오목")) {
 				if((i < 165 && white[i] == true && white[i+1] == true && white[i+2] == true && white[i+3] == true && white[i+4] == true) ||
						(i < 166 && i > 0 && white[i-1] == true && white[i] == true && white[i+1] == true && white[i+2] == true && white[i+3] == true) ||
						(i < 167 && i > 1 && white[i-2] == true && white[i-1] == true && white[i] == true && white[i+1] == true && white[i+2] == true) ||
						(i < 168 && i > 2 && white[i-3] == true && white[i-2] == true && white[i-1] == true && white[i] == true && white[i+1] == true) ||
						(i < 169 && i > 3 && white[i-4] == true && white[i-3] == true && white[i-2] == true && white[i-1] == true && white[i] == true)) {
					// 가로로 다섯 개
 					if((i+4) % 13 == 0) {
 						if((i < 166 && white[i-1] == true && white[i] == true && white[i+1] == true && white[i+2] == true && white[i+3] == true) ||
 								(i < 167 && white[i-2] == true && white[i-1] == true && white[i] == true && white[i+1] == true && white[i+2] == true) ||
 								(i < 168 && white[i-3] == true && white[i-2] == true && white[i-1] == true && white[i] == true && white[i+1] == true) ||
 								(i < 169 && white[i-4] == true && white[i-3] == true && white[i-2] == true && white[i-1] == true && white[i] == true)) {
 							return true;
 						} else {
 							return false;
 						}
 					} else if((i+3) % 13 == 0) {
 						if((i < 169 && white[i-4] == true && white[i-3] == true && white[i-2] == true && white[i-1] == true && white[i] == true) ||
 							(i < 168 && white[i-3] == true && white[i-2] == true && white[i-1] == true && white[i] == true && white[i+1] == true) ||
 							(i < 167 && white[i-2] == true && white[i-1] == true && white[i] == true && white[i+1] == true && white[i+2] == true)) {
 							return true;
 						} else {
 							return false;
 						}
 					} else if((i+2) % 13 == 0) {
 						if((i < 169 && white[i-4] == true && white[i-3] == true && white[i-2] == true && white[i-1] == true && white[i] == true) ||
 								(i < 168 && white[i-3] == true && white[i-2] == true && white[i-1] == true && white[i] == true && white[i+1] == true)) {
 							return true;
 						} else {
 							return false;
 						}
 					} else if((i+1) % 13 == 0) {
 						if(white[i-4] == true && white[i-3] == true && white[i-2] == true && white[i-1] == true && white[i] == true) {
 							return true;
 						} else {
 							return false;
 						}
 					} else if(i % 13 == 0) {
 						if(white[i] == true && white[i+1] == true && white[i+2] == true && white[i+3] == true && white[i+4] == true) {
 							return true;
 						} else {
 							return false;
 						}
 					} else if((i-1) % 13 == 0) {
 						if((white[i] == true && white[i+1] == true && white[i+2] == true && white[i+3] == true && white[i+4] == true) ||
 							(white[i-1] == true && white[i] == true && white[i+1] == true && white[i+2] == true && white[i+3] == true)) {
 							return true;
 						} else {
 							return false;
 						}
 					} else if((i-2) % 13 == 0) {
 						if((white[i] == true && white[i+1] == true && white[i+2] == true && white[i+3] == true && white[i+4] == true) ||
 							(white[i-1] == true && white[i] == true && white[i+1] == true && white[i+2] == true && white[i+3] == true) ||
 							(white[i-2] == true && white[i-1] == true && white[i] == true && white[i+1] == true && white[i+2] == true)) {
 							return true;
 						} else {
 							return false;
 						}
 					} else if((i-3) % 13 == 0) {
 						if((white[i] == true && white[i+1] == true && white[i+2] == true && white[i+3] == true && white[i+4] == true) ||
 							(white[i-1] == true && white[i] == true && white[i+1] == true && white[i+2] == true && white[i+3] == true) ||
 							(white[i-2] == true && white[i-1] == true && white[i] == true && white[i+1] == true && white[i+2] == true) ||
 							(white[i-3] == true && white[i-2] == true && white[i-1] == true && white[i] == true && white[i+1] == true)) {
 							return true;
 						} else {
 							return false;
 						}
 					} else {
 						System.out.println("가로 5개 승리");
 						return true;
 					}
				} else if((i < 117 && white[i] == true && white[i+13] == true && white[i+26] == true && white[i+39] == true && white[i+52] == true) ||
						(i < 130 && i > 12 && white[i-13] == true && white[i] == true && white[i+13] == true && white[i+26] == true && white[i+39] == true) ||
						(i < 143 && i > 25 && white[i-26] == true && white[i-13] ==true && white[i] == true && white[i+13] == true && white[i+26] == true) ||
						(i < 156 && i > 38 && white[i-39] == true && white[i-26] == true && white[i-13] == true && white[i] == true && white[i+13] == true) ||
						(i < 169 && i > 51 && white[i-52] == true && white[i-39] == true && white[i-26] == true && white[i-13] == true && white[i] == true)) {
					System.out.println("세로 5개 승리");
					// 세로 다섯 개
					return true;
				} else if((i < 113 && white[i] == true && white[i+14] == true && white[i+28] == true && white[i+42] == true && white[i+56] == true) ||
						(i < 127 && i > 13 && white[i-14] == true && white[i] == true && white[i+14] == true && white[i+28] == true && white[i+42] == true) ||
						(i < 141 && i > 27 && white[i-28] == true && white[i-14] == true && white[i] == true && white[i+14] == true && white[i+28] == true) ||
						(i < 155 && i > 41 && white[i-42] == true && white[i-28] == true && white[i-14] == true && white[i] == true && white[i+14] == true) ||
						(i < 169 && i > 55 && white[i-56] == true && white[i-42] == true && white[i-28] == true && white[i-14] == true && white[i] == true)) {
					
					if(i < 113 && white[i] == true && white[i+14] == true && white[i+28] == true && white[i+42] == true && white[i+56] == true) {
						if((i+14) % 13 == 0 || (i+28) % 13 == 0 || (i+42) % 13 == 0 || (i+15) % 13 == 0 || (i+29) % 13 == 0 || (i+43) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else if(i < 127 && white[i-14] == true && white[i] == true && white[i+14] == true && white[i+28] == true && white[i+42] == true) {
						if(i % 13 == 0 || (i+14) % 13 == 0 || (i+28) % 13 == 0 || (i+1) % 13 == 0 || (i+15) % 13 == 0 || (i+29) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else if(i < 141 && white[i-28] == true && white[i-14] == true && white[i] == true && white[i+14] == true && white[i+28] == true) {
						if((i-14) % 13 == 0 || i % 13 == 0 || (i+14) % 13 == 0 || (i-13) % 13 == 0 || (i-1) % 13 == 0 || (i+15) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else if(i < 155 && white[i-42] == true && white[i-28] == true && white[i-14] == true && white[i] == true && white[i+14] == true) {
						if((i-28) % 13 == 0 || (i-14) % 13 == 0 || i % 13 == 0 || (i-27) % 13 == 0 || (i-13) % 13 == 0 || i % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else if(i < 169 && white[i-56] == true && white[i-42] == true && white[i-28] == true && white[i-14] == true && white[i] == true) {
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
				} else if((i < 121 && white[i] == true && white[i+12] == true && white[i+24] == true && white[i+36] == true && white[i+48] == true) ||
						(i < 133 && i > 11 && white[i-12] == true && white[i] == true && white[i+12] == true && white[i+24] == true && white[i+36] == true) ||
						(i < 145 && i > 23 && white[i-24] == true && white[i-12] == true && white[i] == true && white[i+12] == true && white[i+24] == true) ||
						(i < 157 && i > 35 && white[i-36] == true && white[i-24] == true && white[i-12] == true && white[i] == true && white[i+12] == true) ||
						(i < 169 && i > 47 && white[i-48] == true && white[i-36] == true && white[i-24] == true && white[i-12] == true && white[i] == true)) {
					if(i < 121 && white[i] == true && white[i+12] == true && white[i+24] == true && white[i+36] == true && white[i+48] == true) {
						if((i+12) % 13 == 0 || (i+24) % 13 == 0 || (i+36) % 13 == 0 || (i+13) % 13 == 0 || (i+25) % 13 == 0 || (i+37) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else if(i < 133 && white[i-12] == true && white[i] == true && white[i+12] == true && white[i+24] == true && white[i+36] == true) {
						if(i % 13 == 0 || (i+12) % 13 == 0 || (i+24) % 13 == 0 || (i+1) % 13 == 0 || (i+13) % 13 == 0 || (i+25) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else if((i < 145 && white[i-24] == true && white[i-12] == true && white[i] == true && white[i+12] == true && white[i+24] == true)) {
						if((i-12) % 13 == 0 || i % 13 == 0 || (i+12) % 13 == 0 || (i-11) % 13 == 0 || (i+1) % 13 == 0 || (i+13) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else if(i < 157 && white[i-36] == true && white[i-24] == true && white[i-12] == true && white[i] == true && white[i+12] == true) {
						if((i-24) % 13 == 0 || (i-12) % 13 == 0 || i % 13 == 0 || (i-23) % 13 == 0 || (i-11) % 13 == 0 || (i+1) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else if(i < 169 && white[i-48] == true && white[i-36] == true && white[i-24] == true && white[i-12] == true && white[i] == true) {
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
				if((i < 167 && white[i] == true && white[i+1] == true && white[i+2]) ||
						(i < 168 && i > 0 && white[i-1] == true && white[i] == true && white[i+1] == true) ||
						(i < 169 && i > 1 && white[i-2] == true && white[i-1] == true && white[i] == true)) {
					if((i+2)%13==0) {
						if((i < 168 && i > 0 && white[i-1] == true && white[i] == true && white[i+1] == true) ||
						(i < 169 && i > 1 &&white[i-2] == true && white[i-1] == true && white[i] == true)) {
							return true;
						} else {
							return false;
						}
					} else if((i+1)%13==0) {
						if(i > 1 && white[i-2] == true && white[i-1] == true && white[i] == true) {
							return true;
						} else {
							return false;
						}
					} else if(i % 13 == 0) {
						if(i < 167 && white[i] == true && white[i+1] == true && white[i+2]) {
							return true;
						} else {
							return false;
						}
					} else if((i-1) % 13 == 0) {
						if((i < 167 && white[i] == true && white[i+1] == true && white[i+2]) ||
							(i < 168 && i > 0 && white[i-1] == true && white[i] == true && white[i+1] == true)) {
							return true;
						} else {
							return false;
						}
					} else {
						System.out.println("가로 3개 승리");
						return true;
					}
					// 가로로 세 개
				} else if((i < 143 && white[i] == true && white[i+13] == true && white[i+26] == true) ||
						(i < 156 && i > 12 && white[i-13] == true && white[i] == true && white[i+13] == true) ||
						(i > 25 && white[i-26] == true && white[i-13] ==true && white[i] == true)) {
					System.out.println("세로 3개 승리");
					// 세로 다섯 개
					return true;
				} else if((i < 141 && white[i] == true && white[i+14] == true && white[i+28] == true) ||
						(i < 155 && i > 13 && white[i-14] == true && white[i] == true && white[i+14] == true) ||
						(i > 27 && white[i-28] == true && white[i-14] == true && white[i] == true)) {
					System.out.println("오른쪽 대각선 승리");
					// 오른쪽 대각선
					return true;
				} else if((i < 145 && white[i] == true && white[i+12] == true && white[i+24] == true) ||
						(i < 157 && i > 11 && white[i-12] == true && white[i] == true && white[i+12] == true) ||
						(i > 23 && white[i-24] == true && white[i-12] == true && white[i] == true)) {
					System.out.println("왼쪽 대각선 승리");
					// 왼쪽 대각선
					return true;
				} else {
					return false;
				}
			} else if(mode.equals("육목")){
				if((i < 164 && white[i] == true && white[i+1] == true && white[i+2] == true && white[i+3] == true && white[i+4] == true && white[i+5] == true) ||
						(i < 165 && i > 0 && white[i-1] == true && white[i] == true && white[i+1] == true && white[i+2] == true && white[i+3] == true && white[i+4] == true) ||
						(i < 166 && i > 1 && white[i-2] == true && white[i-1] == true && white[i] == true && white[i+1] == true && white[i+2] == true && white[i+3] == true) ||
						(i < 167 && i > 2 && white[i-3] == true && white[i-2] == true && white[i-1] == true && white[i] == true && white[i+1] == true && white[i+2] == true) ||
						(i < 168 && i > 3 && white[i-4] == true && white[i-3] == true && white[i-2] == true && white[i-1] == true && white[i] == true && white[i+1] == true) ||
						(i < 169 && i > 4 && white[i-5] == true && white[i-4] == true && white[i-3] == true && white[i-2] == true && white[i-1] == true && white[i] == true)) {
					// 가로로 여섯 개
					if((i+5) % 13 == 0) {
						if((i < 165 && i > 0 && white[i-1] == true && white[i] == true && white[i+1] == true && white[i+2] == true && white[i+3] == true && white[i+4] == true) ||
								(i < 166 && i > 1 && white[i-2] == true && white[i-1] == true && white[i] == true && white[i+1] == true && white[i+2] == true && white[i+3] == true) ||
								(i < 167 && i > 2 && white[i-3] == true && white[i-2] == true && white[i-1] == true && white[i] == true && white[i+1] == true && white[i+2] == true) ||
								(i < 168 && i > 3 && white[i-4] == true && white[i-3] == true && white[i-2] == true && white[i-1] == true && white[i] == true && white[i+1] == true) ||
								(i < 169 && i > 4 && white[i-5] == true && white[i-4] == true && white[i-3] == true && white[i-2] == true && white[i-1] == true && white[i] == true)) {
							return true;
						} else {
							return false;
						}
					} else if((i+4) % 13 == 0) {
						if((i < 166 && i > 1 && white[i-2] == true && white[i-1] == true && white[i] == true && white[i+1] == true && white[i+2] == true && white[i+3] == true) ||
								(i < 167 && i > 2 && white[i-3] == true && white[i-2] == true && white[i-1] == true && white[i] == true && white[i+1] == true && white[i+2] == true) ||
								(i < 168 && i > 3 && white[i-4] == true && white[i-3] == true && white[i-2] == true && white[i-1] == true && white[i] == true && white[i+1] == true) ||
								(i < 169 && i > 4 && white[i-5] == true && white[i-4] == true && white[i-3] == true && white[i-2] == true && white[i-1] == true && white[i] == true)) {
							return true;
						} else {
							return false;
						}
					} else if((i+3) % 13 == 0) {
						if((i < 167 && i > 2 && white[i-3] == true && white[i-2] == true && white[i-1] == true && white[i] == true && white[i+1] == true && white[i+2] == true) ||
								(i < 168 && i > 3 && white[i-4] == true && white[i-3] == true && white[i-2] == true && white[i-1] == true && white[i] == true && white[i+1] == true) ||
								(i < 169 && i > 4 && white[i-5] == true && white[i-4] == true && white[i-3] == true && white[i-2] == true && white[i-1] == true && white[i] == true)) {
							return true;
						} else {
							return false;
						}
					} else if((i + 2) % 13 == 0) {
						if((i < 168 && i > 3 && white[i-4] == true && white[i-3] == true && white[i-2] == true && white[i-1] == true && white[i] == true && white[i+1] == true) ||
						(i < 169 && i > 4 && white[i-5] == true && white[i-4] == true && white[i-3] == true && white[i-2] == true && white[i-1] == true && white[i] == true)) {
							return true;
						} else {
							return false;
						}
					} else if((i + 1) % 13 == 0) {
						if((i < 169 && i > 4 && white[i-5] == true && white[i-4] == true && white[i-3] == true && white[i-2] == true && white[i-1] == true && white[i] == true)) {
							return true;
						} else {
							return false;
						}
					} else if(i % 13 == 0) {
						if((i < 164 && white[i] == true && white[i+1] == true && white[i+2] == true && white[i+3] == true && white[i+4] == true && white[i+5] == true)) {
							return true;
						} else {
							return false;
						}
					} else if((i-1) % 13 == 0) {
						if((i < 164 && white[i] == true && white[i+1] == true && white[i+2] == true && white[i+3] == true && white[i+4] == true && white[i+5] == true) ||
						(i < 165 && i > 0 && white[i-1] == true && white[i] == true && white[i+1] == true && white[i+2] == true && white[i+3] == true && white[i+4] == true)) {
							return true;
						} else {
							return false;
						}
					} else if((i-2) % 13 == 0) {
						if((i < 164 && white[i] == true && white[i+1] == true && white[i+2] == true && white[i+3] == true && white[i+4] == true && white[i+5] == true) ||
						(i < 165 && i > 0 && white[i-1] == true && white[i] == true && white[i+1] == true && white[i+2] == true && white[i+3] == true && white[i+4] == true) ||
						(i < 166 && i > 1 && white[i-2] == true && white[i-1] == true && white[i] == true && white[i+1] == true && white[i+2] == true && white[i+3] == true)) {
							return true;
						} else {
							return false;
						}
					} else if((i-3) % 13 == 0) {
						if((i < 164 && white[i] == true && white[i+1] == true && white[i+2] == true && white[i+3] == true && white[i+4] == true && white[i+5] == true) ||
								(i < 165 && i > 0 && white[i-1] == true && white[i] == true && white[i+1] == true && white[i+2] == true && white[i+3] == true && white[i+4] == true) ||
								(i < 166 && i > 1 && white[i-2] == true && white[i-1] == true && white[i] == true && white[i+1] == true && white[i+2] == true && white[i+3] == true) ||
								(i < 167 && i > 2 && white[i-3] == true && white[i-2] == true && white[i-1] == true && white[i] == true && white[i+1] == true && white[i+2] == true)) {
							return true;
						} else {
							return false;
						}
					} else if((i-4) % 13 == 0) {
						if((i < 164 && white[i] == true && white[i+1] == true && white[i+2] == true && white[i+3] == true && white[i+4] == true && white[i+5] == true) ||
						(i < 165 && i > 0 && white[i-1] == true && white[i] == true && white[i+1] == true && white[i+2] == true && white[i+3] == true && white[i+4] == true) ||
						(i < 166 && i > 1 && white[i-2] == true && white[i-1] == true && white[i] == true && white[i+1] == true && white[i+2] == true && white[i+3] == true) ||
						(i < 167 && i > 2 && white[i-3] == true && white[i-2] == true && white[i-1] == true && white[i] == true && white[i+1] == true && white[i+2] == true) ||
						(i < 168 && i > 3 && white[i-4] == true && white[i-3] == true && white[i-2] == true && white[i-1] == true && white[i] == true && white[i+1] == true)) {
							return true;
						} else {
							return false;
						}
					} else {
						System.out.println("가로 6개 승리");
						return true;
					}
				} else if((i < 104 && white[i] == true && white[i+13] == true && white[i+26] == true && white[i+39] == true && white[i+52] == true && white[i+65] == true) ||
						(i < 117 && i > 12 && white[i-13] == true && white[i] == true && white[i+13] == true && white[i+26] == true && white[i+39] == true && white[i+52] == true) ||
						(i < 130 && i > 25 && white[i-26] == true && white[i-13] == true && white[i] == true && white[i+13] == true && white[i+26] == true && white[i+39] == true) ||
						(i < 143 && i > 38 && white[i-39] == true && white[i-26] == true && white[i-13] == true && white[i] == true && white[i+13] == true && white[i+26] == true) ||
						(i < 156 && i > 51 && white[i-52] == true && white[i-39] == true && white[i-26] == true && white[i-13] == true && white[i] == true && white[i+13] == true) ||
						(i < 169 && i > 64 && white[i-65] == true && white[i-52] == true && white[i-39] == true && white[i-26] == true && white[i-13] == true && white[i] == true)) {
					System.out.println("세로 6개 승리");
					// 세로 여섯 개
					return true;
				} else if((i < 99 && white[i] == true && white[i+14] == true && white[i+28] == true && white[i+42] == true && white[i+56] == true && white[i+70] == true) ||
						(i < 113 && i > 13 && white[i-14] == true && white[i] == true && white[i+14] == true && white[i+28] == true && white[i+42] == true && white[i+56] == true) ||
						(i < 127 && i > 27 && white[i-28] == true && white[i-14] == true && white[i] == true && white[i+14] == true && white[i+28] == true && white[i+42] == true) ||
						(i < 141 && i > 41 && white[i-42] == true && white[i-28] == true && white[i-14] == true && white[i] == true && white[i+14] == true && white[i+28] == true) ||
						(i < 155 && i > 55 && white[i-56] == true && white[i-42] == true && white[i-28] == true && white[i-14] == true && white[i] == true && white[i+14] == true) ||
						(i < 169 && i > 69 && white[i-70] == true && white[i-56] == true && white[i-42] == true && white[i-28] == true && white[i-14] == true && white[i] == true)) {
					if(i < 99 && white[i] == true && white[i+14] == true && white[i+28] == true && white[i+42] == true && white[i+56] == true && white[i+70] == true) {
						if((i+14) % 13 == 0 || (i+28) % 13 == 0 || (i+42) % 13 == 0 || (i+56) % 13 == 0 || (i+15) % 13 == 0 || (i+29) % 13 == 0 || (i+43) % 13 == 0 || (i+57) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else if(i < 113 && i > 13 && white[i-14] == true && white[i] == true && white[i+14] == true && white[i+28] == true && white[i+42] == true && white[i+56] == true) {
						if(i % 13 == 0 || (i+14) % 13 == 0 || (i+28) % 13 == 0 || (i+42) % 13 == 0 || (i+1) % 13 == 0 || (i+15) % 13 == 0 || (i+29) % 13 == 0 || (i+43) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else if(i < 127 && i > 27 && white[i-28] == true && white[i-14] == true && white[i] == true && white[i+14] == true && white[i+28] == true && white[i+42] == true) {
						if((i-14) % 13 == 0 || i % 13 == 0 || (i+14) % 13 == 0 || (i+28) % 13 == 0 || (i+1) % 13 == 0 || (i-13) % 13 == 0 || (i+15) % 13 == 0 || (i+29) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else if(i < 141 && i > 41 && white[i-42] == true && white[i-28] == true && white[i-14] == true && white[i] == true && white[i+14] == true && white[i+28] == true) {
						if((i-28) % 13 == 0 || (i-14) % 13 == 0 || i % 13 == 0 || (i+14) % 13 == 0 || (i-27) % 13 == 0 || (i-13) % 13 == 0 || (i+1) % 13 == 0 || (i+15) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else if(i < 155 && i > 55 && white[i-56] == true && white[i-42] == true && white[i-28] == true && white[i-14] == true && white[i] == true && white[i+14] == true) {
						if((i-42) % 13 == 0 || (i-28) % 13 == 0 || (i-14) % 13 == 0 || i % 13 == 0 || (i-41) % 13 == 0 || (i-27) % 13 == 0 || (i-13) % 13 == 0 || (i+1) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else if(i < 169 && i > 69 && white[i-70] == true && white[i-56] == true && white[i-42] == true && white[i-28] == true && white[i-14] == true && white[i] == true) {
						if((i-56) % 13 == 0 || (i-42) % 13 == 0 || (i-28) % 13 == 0 || (i-14) % 13 == 0 || (i-55) % 13 == 0 || (i-41) % 13 == 0 || (i-27) % 13 == 0 || (i-13) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else {
						System.out.println("오른쪽 대각선 승리");
						return true;
					}
				} else if((i < 109 && white[i] == true && white[i+12] == true && white[i+24] == true && white[i+36] == true && white[i+48] == true && white[i+60] == true) ||
						(i < 121 && i > 11 && white[i-12] == true && white[i] == true && white[i+12] == true && white[i+24] == true && white[i+36] == true && white[i+48] == true) ||
						(i < 133 && i > 23 && white[i-24] == true && white[i-12] == true && white[i] == true && white[i+12] == true && white[i+24] == true && white[i+36] == true) ||
						(i < 145 && i > 35 && white[i-36] == true && white[i-24] == true && white[i-12] == true && white[i] == true && white[i+12] == true && white[i+24] == true) ||
						(i < 157 && i > 47 && white[i-48] == true && white[i-36] == true && white[i-24] == true && white[i-12] == true && white[i] == true && white[i+12] == true) ||
						(i < 169 && i > 59 && white[i-60] == true && white[i-48] == true && white[i-36] == true && white[i-24] == true && white[i-12] == true && white[i] == true)) {
					if(i < 109 && white[i] == true && white[i+12] == true && white[i+24] == true && white[i+36] == true && white[i+48] == true && white[i+60] == true) {
						if((i+12) % 13 == 0 || (i+24) % 13 == 0 || (i+36) % 13 == 0 || (i+48) % 13 == 0 || (i+13) % 13 == 0 || (i+25) % 13 == 0 || (i+37) % 13 == 0 || (i+49) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else if(i < 121 && i > 11 && white[i-12] == true && white[i] == true && white[i+12] == true && white[i+24] == true && white[i+36] == true && white[i+48] == true) {
						if(i % 13 == 0 || (i+12) % 13 == 0 || (i+24) % 13 == 0 || (i+36) % 13 == 0 || (i+1) % 13 == 0 || (i+13) % 13 == 0 || (i+25) % 13 == 0 || (i+37) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else if(i < 133 && i > 23 && white[i-24] == true && white[i-12] == true && white[i] == true && white[i+12] == true && white[i+24] == true && white[i+36] == true) {
						if((i-12) % 13 == 0 || i % 13 == 0 || (i+12) % 13 == 0 || (i+24) % 13 == 0 || (i+1) % 13 == 0 || (i-11) % 13 == 0 || (i+13) % 13 == 0 || (i+25) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else if(i < 145 && i > 35 && white[i-36] == true && white[i-24] == true && white[i-12] == true && white[i] == true && white[i+12] == true && white[i+24] == true) {
						if((i-24) % 13 == 0 || (i-12) % 13 == 0 || i % 13 == 0 || (i+12) % 13 == 0 || (i-23) % 13 == 0 || (i-11) % 13 == 0 || (i+1) % 13 == 0 || (i+13) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else if(i < 157 && i > 47 && white[i-48] == true && white[i-36] == true && white[i-24] == true && white[i-12] == true && white[i] == true && white[i+12] == true) {
						if((i-36) % 13 == 0 || (i-24) % 13 == 0 || (i-12) % 13 == 0 || i % 13 == 0 || (i-35) % 13 == 0 || (i-23) % 13 == 0 || (i-11) % 13 == 0 || (i+1) % 13 == 0) {
							return false;
						} else {
							return true;
						}
					} else if(i < 169 && i > 59 && white[i-60] == true && white[i-48] == true && white[i-36] == true && white[i-24] == true && white[i-12] == true && white[i] == true) {
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
			//System.out.println(i +" white[] 범위에 벗어남!");
			return false;
		}
	}
}
