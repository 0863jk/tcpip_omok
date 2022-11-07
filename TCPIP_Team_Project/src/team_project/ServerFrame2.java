package team_project;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
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
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public class ServerFrame2 extends JFrame implements MouseListener{
	
//https://kamang-it.tistory.com/entry/Swing-04Layout%EB%B0%B0%EC%B9%98%EA%B4%80%EB%A6%AC%EC%9E%90
	
	
	JPanel pGameBoard, pNorth, pSouth;
	JButton[] whtbtnBoard;
	boolean[] black, white;
	
	ServerSocket server = null;
	Socket socket = null;
	BufferedReader in = null;
	BufferedWriter out = null;
	private JButton btnSurrender;
	
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
		btn.setMaximumSize(getMinimumSize());
	}
	
	public ServerFrame2(String title, int width, int height, int port) {
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
		btnSurrender.addMouseListener(this);
		pSouth.add(btnSurrender);
		
		add(pSouth, BorderLayout.SOUTH);
		setResizable(false);
		setVisible(true);
			
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


	public static void main(String[] args) {
		new ServerFrame2("백돌", 420, 460, 9190);
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		try {
			Object obj = e.getSource();
			
			ImageIcon whtCornerLeft = new ImageIcon("images/wht_Corner_Left.png");
			ImageIcon whtCornerRight = new ImageIcon("images/wht_Corner_Right.png");		
			ImageIcon whtCornerUp = new ImageIcon("images/wht_Corner_Up.png");
			ImageIcon whtCornerDown = new ImageIcon("images/wht_Corner_Down.png");
			ImageIcon whtCornerUpLeft = new ImageIcon("images/wht_Corner_Up_Left.png");
			ImageIcon whtCornerUpRight = new ImageIcon("images/wht_Corner_Up_Right.png");
			ImageIcon whtCornerDownLeft = new ImageIcon("images/wht_Corner_Down_Left.png");
			ImageIcon whtCornerDownRight = new ImageIcon("images/wht_Corner_Down_Right.png");
			ImageIcon whtDiagram = new ImageIcon("images/wht_Diagram.png");
			boolean succ = false;
			
			
			for(int i = 0; i < whtbtnBoard.length; i ++) {
				if(obj == btnSurrender) {
					int confirm = JOptionPane.showConfirmDialog(null, "항복하고 해당 게임을 떠나시겠습니까?", "백돌", JOptionPane.WARNING_MESSAGE);
					if(confirm == 0) {
						JOptionPane.showConfirmDialog(null, "패배했습니다.", "백돌", JOptionPane.WARNING_MESSAGE);
						setVisible(false);
						break;
					} else if(confirm == 1) {
						JOptionPane.showConfirmDialog(null, "다시 게임을 진행합니다.", "백돌", JOptionPane.CLOSED_OPTION);
					}
					
				} else if(obj == whtbtnBoard[i]) {
					if(black[i] == false) { // 흑돌이 놓여져있는지 확인
						// 안 놓여져 있다면
						if(i == 0) {
							whtbtnBoard[i].setIcon(whtCornerUpLeft);
						} else if(i > 0 && i < 12) {
							whtbtnBoard[i].setIcon(whtCornerUp);
						} else if(i == 12) {
							whtbtnBoard[i].setIcon(whtCornerUpRight);
						} else if(i % 13 == 0 && i != 156) {
							whtbtnBoard[i].setIcon(whtCornerLeft);
						} else if((i + 1) % 13 == 0 && i != 168) {
							whtbtnBoard[i].setIcon(whtCornerRight);
						} else if(i == 156) {
							whtbtnBoard[i].setIcon(whtCornerDownLeft);
						} else if(i > 156 && i < 168) {
							whtbtnBoard[i].setIcon(whtCornerDown);
						} else if(i == 168) {
							whtbtnBoard[i].setIcon(whtCornerDownRight);
							System.out.println("백돌 바껴라");
						} else {
							whtbtnBoard[i].setIcon(whtDiagram);
						}
						
						System.out.println("백돌: " + i);
						white[i] = true; // 백돌을 둠
						
						if(checkWin("삼목", i) == true) {
							JOptionPane.showConfirmDialog(null, "승리!", "백돌", JOptionPane.INFORMATION_MESSAGE);
						} else if(checkWin("삼목", i) == false) {
							// 승리 아님
							System.out.println("승리 아닙니다.");
						}
						
						succ = true;
//						checkWin(i); // 누른 버튼을 기준으로 승리를 확인
						
					} else if(black[i] == true) {
						JOptionPane.showConfirmDialog(null, "상대방의 바둑돌이 이미 존재합니다.", "백돌", JOptionPane.WARNING_MESSAGE);
						succ = false;
						break;
					} else if(white[i] == true) {
						JOptionPane.showMessageDialog(null, "당신의 바둑돌이 이미 존재합니다.", "흑돌", JOptionPane.WARNING_MESSAGE);
						succ = false;
						break;
					}
					
					
				}
			} 
		} catch (Exception e1) {
			e1.printStackTrace();
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
	
	private boolean checkWin(String mode, int i) {
		System.out.println("CheckWin 진입");
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
