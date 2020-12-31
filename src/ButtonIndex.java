package final_exam;

import java.awt.Button;

public class ButtonIndex extends Button{//Button 안에 index(i,j) 정보를 담기 위해 Button클래스를 상속받아 만든 ButtonIndex클래스.
	
	private static final long serialVersionUID = 1L;
	
	public int row;
	public int col;

	public ButtonIndex(int row, int col) {
		super();
		this.row = row;
		this.col = col;
	}

}
