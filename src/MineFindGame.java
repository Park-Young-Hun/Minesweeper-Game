package final_exam;

import java.util.*;
import java.util.function.Supplier;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MineFindGame {

	public static void main(String[] args) {
		new StartFrame();
	}
}

class StartFrame extends JFrame {//첫 난이도 선택화면.

	private static final long serialVersionUID = 5669199298483054352L;//JFrame을 상속받아 쓰기위한 시리얼넘버.
	public static Frame f1;

	public StartFrame() {
		f1 = new JFrame("난이도 선택");

		f1.setSize(500, 300);
		f1.setVisible(true);
		f1.setLayout(null);
		f1.setLocationRelativeTo(null);

		CheckboxGroup group = new CheckboxGroup();
		Checkbox easy = new Checkbox("Easy 9X9", group, false);
		Checkbox medium = new Checkbox("Medium 16X16", group, false);
		Checkbox hard = new Checkbox("Hard 22X22", group, false);
		Button start = new Button("Start");

		easy.setBounds(200, 30, 100, 50);//라디오 버튼들과 버튼을 절대위치로 배치.
		medium.setBounds(200, 80, 100, 50);
		hard.setBounds(200, 130, 100, 50);
		start.setBounds(200, 200, 80, 30);
		f1.add(easy);
		f1.add(medium);
		f1.add(hard);
		f1.add(start);

		easy.addItemListener(new FrameEvent());//각각의 ui에 이벤트 등록.
		medium.addItemListener(new FrameEvent());
		hard.addItemListener(new FrameEvent());
		start.addActionListener(new FrameEvent());
		f1.addWindowListener(new FrameEvent());
	}
}

class MainFrame extends JFrame {//지뢰 찾기 게임이 이루어지는 메인화면.

	private static final long serialVersionUID = -5081323641646180364L;
	
	public static Frame f2;
	public static int width;
	public static int height;
	public static int mine;
	public static ArrayList<ArrayList<ButtonIndex>> btns;//게임 판을 만들기 위한 2차원 ArrayList.

	public MainFrame() {

		f2 = new JFrame("지뢰찾기");
		f2.setSize(30 * width, 30 * height);
		f2.setLocationRelativeTo(null);
		f2.setLayout(new GridLayout(width, height));
		f2.setVisible(true);

		btns = new ArrayList<ArrayList<ButtonIndex>>();

		for (int j = 0; j < height; j++) {

			ArrayList<ButtonIndex> list = new ArrayList<>();

			for (int i = 0; i < width; i++) {
				ButtonIndex btn = new ButtonIndex(i, j);//Button 안에 index(i,j) 정보를 담기 위해 Button클래스를 상속받아 만든 ButtonIndex클래스.
				btn.addActionListener(new FrameEvent());
				f2.add(btn);
				list.add(btn);
			}
			btns.add(list);
		}

		Supplier<Integer> s = () -> {
			Random ran = new Random();
			return ran.nextInt(width);
		};

		for (int i = 0; i < mine; i++) {//람다식을 활용하여 난수생성한 위치에 지뢰들을 뿌려줍니다.
			btns.get(getRanNum(s)).get(getRanNum(s)).setName("Mine");
		}
		f2.addWindowListener(new FrameEvent());
	}

	public int getRanNum(Supplier<Integer> s) {
		return s.get();
	}
}

class FrameEvent extends WindowAdapter implements ItemListener, ActionListener {//Frame에서 발생하는 모든 이벤트들을 정의합니다.
	
	public static Frame win; //승리 시 나오는 창.
	public static Frame lose; //패배 시 나오는창.
	public static int win_count;//0이 되면 플레이어가 승리합니다.

	public void windowClosing(WindowEvent arg0) {

		System.exit(0); // 프로그램 종료
	}

	public void itemStateChanged(ItemEvent e) {//첫 화면의 라디오 버튼에 대한 이벤트 함수.
		Object obj = e.getItem();
		if (obj == "Easy 9X9") {
			MainFrame.width = 9;
			MainFrame.height = 9;
			MainFrame.mine = 10;
		} else if (obj == "Medium 16X16") {
			MainFrame.width = 16;
			MainFrame.height = 16;
			MainFrame.mine = 40;
		} else if (obj == "Hard 22X22") {
			MainFrame.width = 22;
			MainFrame.height = 22;
			MainFrame.mine = 99;
		}
	}
	
	public void actionPerformed(ActionEvent e) {//모든 Frame의 버튼들에 대한 이벤트함수.

		if (e.getActionCommand() == "Start") {
			StartFrame.f1.setVisible(false);
			new MainFrame();
		} else if (e.getActionCommand() == "Yes") {//이겼을 때와 졌을 때를 구분해주고 게임을 재시작합니다.
			MainFrame.f2.setVisible(false);
			if (win_count == 0)
				win.setVisible(false);
			else
				lose.setVisible(false);
			
			new StartFrame();

		} else if (e.getActionCommand() == "No") {//이겼을 때와 졌을 때를 구분해주고 게임을 종료합니다.
			MainFrame.f2.setVisible(false);
			if (win_count == 0)
				win.setVisible(false);
			else
				lose.setVisible(false);
			
			System.exit(0);

		} else {//게임판의 버튼이 눌렸을 땐 searchMine 메소드를 호출합니다.
			ButtonIndex btn = (ButtonIndex) e.getSource();
			searchMine(btn);
		}
	}

	public boolean isMine(ButtonIndex btn) {//인자로 받은 버튼은 지뢰인지 확인해주는 메소드.
		if (btn.getName() == "Mine")
			return true;
		else
			return false;
	}

	public void showInfo(ButtonIndex btn, int mineNum) {//버튼이 눌렸을 때 정보를 보여주고 지뢰가 남았는지 확인하여 승리 창을 띄워줍니다.

		btn.setLabel(Integer.toString(mineNum));
		btn.setBackground(Color.darkGray);
		btn.setForeground(Color.white);
		btn.setName("Checked");
		
		win_count = MainFrame.width * MainFrame.height - MainFrame.mine;

		if (win_count != 0)//지뢰가 아닌 버튼 수에서 하나 씩 차감하여 지뢰를 제외한 모든 버튼을 눌렀는지 확인.
			win_count--;
		if (win_count == 0) {//승리 창 띄워줌.
			win = new JFrame();
			win.setSize(300, 200);
			win.setVisible(true);
			win.setLayout(null);
			win.setLocationRelativeTo(null);
			
			Label winLabel = new Label("You Win! Restart the game?");
			Button yesBtn = new Button("Yes");
			Button noBtn = new Button("No");
			
			winLabel.setBounds(70, 35, 200, 20);
			yesBtn.setBounds(60, 100, 80, 30);
			noBtn.setBounds(160, 100, 80, 30);
			win.add(winLabel);
			win.add(yesBtn);
			win.add(noBtn);
			
			yesBtn.addActionListener(this);
			noBtn.addActionListener(this);
		}

	}

	public void searchMine(ButtonIndex btn) {//지뢰일 경우 패배창을 띄워주고 아니라면 주위 8칸의 지뢰의 갯수를 표시해줌.

		int mineNum = 0;

		if (btn.getName() == "Mine") {//지뢰이면 패배 창을 띄움.
			btn.setLabel("!");
			btn.setBackground(Color.red);
			btn.setForeground(Color.white);

			lose = new JFrame();
			lose.setSize(300, 200);
			lose.setVisible(true);
			lose.setLayout(null);
			lose.setLocationRelativeTo(null);
			
			Label loseLabel = new Label("You lose! Restart the game?");
			Button yesBtn = new Button("Yes");
			Button noBtn = new Button("No");
			
			loseLabel.setBounds(70, 35, 200, 20);
			yesBtn.setBounds(60, 100, 80, 30);
			noBtn.setBounds(160, 100, 80, 30);
			lose.add(loseLabel);
			lose.add(yesBtn);
			lose.add(noBtn);
			
			yesBtn.addActionListener(this);
			noBtn.addActionListener(this);
			
		}

		else if (btn.col == 0) {
			if (btn.row == 0)// 왼쪽위 모서리 좌표일 경우 주위에 지뢰가 몇개 있는지 계산.
			{
				if (isMine(MainFrame.btns.get(1).get(0)))
					mineNum++;

				if (isMine(MainFrame.btns.get(0).get(1)))
					mineNum++;

				if (isMine(MainFrame.btns.get(1).get(1)))
					mineNum++;

				showInfo(btn, mineNum);
				if (mineNum == 0) { // 주위의 지뢰의 갯수가 0이라면 재귀적으로 주변으로 0이 안나올 때까지 확장한다.
					if (MainFrame.btns.get(0).get(1).getName() != "Checked")//"Checked"를 통해 이미 갔던 곳으론 재귀적으로 확장하지 않는다.
						searchMine(MainFrame.btns.get(0).get(1));
					if (MainFrame.btns.get(1).get(0).getName() != "Checked")
						searchMine(MainFrame.btns.get(1).get(0));
				}
				return;
			}

			else {
				if (btn.row == MainFrame.width - 1)// 오른쪽위 모서리 좌표일 경우 주위에 지뢰가 몇개 있는지 계산.
				{
					if (isMine(MainFrame.btns.get(0).get(btn.row - 1)))
						mineNum++;

					if (isMine(MainFrame.btns.get(1).get(btn.row - 1)))
						mineNum++;

					if (isMine(MainFrame.btns.get(1).get(btn.row)))
						mineNum++;

					showInfo(btn, mineNum);
					if (mineNum == 0) {// 주위의 지뢰의 갯수가 0이라면 재귀적으로 주변으로 0이 안나올 때까지 확장한다.
						if (MainFrame.btns.get(0).get(btn.row - 1).getName() != "Checked")
							searchMine(MainFrame.btns.get(0).get(btn.row - 1));
						if (MainFrame.btns.get(1).get(btn.row).getName() != "Checked")
							searchMine(MainFrame.btns.get(1).get(btn.row));
					}
					return;
				}

				else// 모서리를 제외한 위쪽 1행에 대하여 주위에 지뢰가 몇개 있는지 계산.
				{
					if (isMine(MainFrame.btns.get(btn.col).get(btn.row - 1)))
						mineNum++;

					if (isMine(MainFrame.btns.get(btn.col + 1).get(btn.row - 1)))
						mineNum++;

					if (isMine(MainFrame.btns.get(btn.col + 1).get(btn.row)))
						mineNum++;

					if (isMine(MainFrame.btns.get(btn.col + 1).get(btn.row + 1)))
						mineNum++;

					if (isMine(MainFrame.btns.get(btn.col).get(btn.row + 1)))
						mineNum++;

					showInfo(btn, mineNum);
					if (mineNum == 0) {// 주위의 지뢰의 갯수가 0이라면 재귀적으로 주변으로 0이 안나올 때까지 확장한다.
						if (MainFrame.btns.get(btn.col).get(btn.row - 1).getName() != "Checked")
							searchMine(MainFrame.btns.get(btn.col).get(btn.row - 1));
						if (MainFrame.btns.get(btn.col + 1).get(btn.row).getName() != "Checked")
							searchMine(MainFrame.btns.get(btn.col + 1).get(btn.row));
						if (MainFrame.btns.get(btn.col).get(btn.row + 1).getName() != "Checked")
							searchMine(MainFrame.btns.get(btn.col).get(btn.row + 1));
					}
					return;
				}
			}
		} else if (btn.col == MainFrame.height - 1) {
			if (btn.row == 0)// 왼쪽 아래 모서리 좌표일 경우 주위에 지뢰가 몇개 있는지 계산.
			{
				if (isMine(MainFrame.btns.get(btn.col - 1).get(0)))
					mineNum++;
				if (isMine(MainFrame.btns.get(btn.col - 1).get(1)))
					mineNum++;
				if (isMine(MainFrame.btns.get(btn.col).get(1)))
					mineNum++;

				showInfo(btn, mineNum);
				if (mineNum == 0) {// 주위의 지뢰의 갯수가 0이라면 재귀적으로 주변으로 0이 안나올 때까지 확장한다.
					if (MainFrame.btns.get(btn.col - 1).get(0).getName() != "Checked")
						searchMine(MainFrame.btns.get(btn.col - 1).get(0));
					if (MainFrame.btns.get(btn.col).get(1).getName() != "Checked")
						searchMine(MainFrame.btns.get(btn.col).get(1));
				}
				return;
			}

			else {
				if (btn.row == MainFrame.width - 1)// 오른쪽아래 모서리 좌표일 경우 주위에 지뢰가 몇개 있는지 계산.
				{
					if (isMine(MainFrame.btns.get(btn.col).get(btn.row - 1)))
						mineNum++;
					if (isMine(MainFrame.btns.get(btn.col - 1).get(btn.row - 1)))
						mineNum++;
					if (isMine(MainFrame.btns.get(btn.col - 1).get(btn.row)))
						mineNum++;

					showInfo(btn, mineNum);
					if (mineNum == 0) {// 주위의 지뢰의 갯수가 0이라면 재귀적으로 주변으로 0이 안나올 때까지 확장한다.
						if (MainFrame.btns.get(btn.col).get(btn.row - 1).getName() != "Checked")
							searchMine(MainFrame.btns.get(btn.col).get(btn.row - 1));

						if (MainFrame.btns.get(btn.col - 1).get(btn.row).getName() != "Checked")
							searchMine(MainFrame.btns.get(btn.col - 1).get(btn.row));
					}
					return;
				}

				else// 모서리를 제외한 맨 아래 행인 경우 주위에 지뢰가 몇개 있는지 계산.
				{
					if (isMine(MainFrame.btns.get(btn.col).get(btn.row - 1)))
						mineNum++;

					if (isMine(MainFrame.btns.get(btn.col - 1).get(btn.row - 1)))
						mineNum++;

					if (isMine(MainFrame.btns.get(btn.col - 1).get(btn.row)))
						mineNum++;

					if (isMine(MainFrame.btns.get(btn.col - 1).get(btn.row + 1)))
						mineNum++;

					if (isMine(MainFrame.btns.get(btn.col).get(btn.row + 1)))
						mineNum++;

					showInfo(btn, mineNum);
					if (mineNum == 0) {// 주위의 지뢰의 갯수가 0이라면 재귀적으로 주변으로 0이 안나올 때까지 확장한다.
						if (MainFrame.btns.get(btn.col).get(btn.row - 1).getName() != "Checked")
							searchMine(MainFrame.btns.get(btn.col).get(btn.row - 1));

						if (MainFrame.btns.get(btn.col - 1).get(btn.row).getName() != "Checked")
							searchMine(MainFrame.btns.get(btn.col - 1).get(btn.row));

						if (MainFrame.btns.get(btn.col).get(btn.row + 1).getName() != "Checked")
							searchMine(MainFrame.btns.get(btn.col).get(btn.row + 1));
					}
					return;
				}
			}
		}

		else if (btn.row == 0)// 모서리를 제외한 왼쪽 첫 1열인 경우 주위에 지뢰가 몇개 있는지 계산.
		{
			if (isMine(MainFrame.btns.get(btn.col - 1).get(btn.row)))
				mineNum++;

			if (isMine(MainFrame.btns.get(btn.col - 1).get(btn.row + 1)))
				mineNum++;

			if (isMine(MainFrame.btns.get(btn.col).get(btn.row + 1)))
				mineNum++;

			if (isMine(MainFrame.btns.get(btn.col + 1).get(btn.row + 1)))
				mineNum++;

			if (isMine(MainFrame.btns.get(btn.col + 1).get(btn.row)))
				mineNum++;

			showInfo(btn, mineNum);
			if (mineNum == 0) {// 주위의 지뢰의 갯수가 0이라면 재귀적으로 주변으로 0이 안나올 때까지 확장한다.
				if (MainFrame.btns.get(btn.col - 1).get(btn.row).getName() != "Checked")
					searchMine(MainFrame.btns.get(btn.col - 1).get(btn.row));

				if (MainFrame.btns.get(btn.col).get(btn.row + 1).getName() != "Checked")
					searchMine(MainFrame.btns.get(btn.col).get(btn.row + 1));

				if (MainFrame.btns.get(btn.col + 1).get(btn.row).getName() != "Checked")
					searchMine(MainFrame.btns.get(btn.col + 1).get(btn.row));
			}
			return;
		}

		else if (btn.row == MainFrame.width - 1)// 모서리를 제외한 맨 오른쪽 열인 경우 주위에 지뢰가 몇개 있는지 계산.
		{
			if (isMine(MainFrame.btns.get(btn.col - 1).get(btn.row)))
				mineNum++;
			
			if (isMine(MainFrame.btns.get(btn.col - 1).get(btn.row - 1)))
				mineNum++;

			if (isMine(MainFrame.btns.get(btn.col).get(btn.row - 1)))
				mineNum++;

			if (isMine(MainFrame.btns.get(btn.col + 1).get(btn.row - 1)))
				mineNum++;

			if (isMine(MainFrame.btns.get(btn.col + 1).get(btn.row)))
				mineNum++;

			showInfo(btn, mineNum);
			if (mineNum == 0) {// 주위의 지뢰의 갯수가 0이라면 재귀적으로 주변으로 0이 안나올 때까지 확장한다.
				if (MainFrame.btns.get(btn.col - 1).get(btn.row).getName() != "Checked")
					searchMine(MainFrame.btns.get(btn.col - 1).get(btn.row));

				if (MainFrame.btns.get(btn.col).get(btn.row - 1).getName() != "Checked")
					searchMine(MainFrame.btns.get(btn.col).get(btn.row - 1));

				if (MainFrame.btns.get(btn.col + 1).get(btn.row).getName() != "Checked")
					searchMine(MainFrame.btns.get(btn.col + 1).get(btn.row));
			}
			return;
		}

		else// 그 외의 나머지 일반적인 경우에 대한 주위에 있을 지뢰 갯수 계산.
		{
			if (isMine(MainFrame.btns.get(btn.col - 1).get(btn.row - 1)))
				mineNum++;

			if (isMine(MainFrame.btns.get(btn.col - 1).get(btn.row)))
				mineNum++;

			if (isMine(MainFrame.btns.get(btn.col - 1).get(btn.row + 1)))
				mineNum++;

			if (isMine(MainFrame.btns.get(btn.col).get(btn.row - 1)))
				mineNum++;

			if (isMine(MainFrame.btns.get(btn.col).get(btn.row + 1)))
				mineNum++;

			if (isMine(MainFrame.btns.get(btn.col + 1).get(btn.row - 1)))
				mineNum++;

			if (isMine(MainFrame.btns.get(btn.col + 1).get(btn.row)))
				mineNum++;

			if (isMine(MainFrame.btns.get(btn.col + 1).get(btn.row + 1)))
				mineNum++;

			showInfo(btn, mineNum);
			if (mineNum == 0) {// 주위의 지뢰의 갯수가 0이라면 재귀적으로 주변으로 0이 안나올 때까지 확장한다.

				if (MainFrame.btns.get(btn.col - 1).get(btn.row).getName() != "Checked")
					searchMine(MainFrame.btns.get(btn.col - 1).get(btn.row));

				if (MainFrame.btns.get(btn.col).get(btn.row - 1).getName() != "Checked")
					searchMine(MainFrame.btns.get(btn.col).get(btn.row - 1));
				if (MainFrame.btns.get(btn.col).get(btn.row + 1).getName() != "Checked")
					searchMine(MainFrame.btns.get(btn.col).get(btn.row + 1));

				if (MainFrame.btns.get(btn.col + 1).get(btn.row).getName() != "Checked")
					searchMine(MainFrame.btns.get(btn.col + 1).get(btn.row));
			}
			return;
		}
		return;
	}
}

