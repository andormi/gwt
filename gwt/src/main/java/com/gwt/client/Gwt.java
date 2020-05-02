package com.gwt.client;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Gwt implements EntryPoint, KeyPressHandler {
	
	private VerticalPanel introScreenPanel = new VerticalPanel();
	private VerticalPanel sortResetButtonPanel = new VerticalPanel();
	private HorizontalPanel sortScreenPanel = new HorizontalPanel();
	
	private FlexTable flexTable = new FlexTable();

	private TextBox textBox = new TextBox();
	private Button enterButton = new Button("Enter");
	private Button sortButton = new Button("Sort");
	private Button resetButton = new Button("Reset");
	
	private Label inputQuestionLabel = new Label();
	private Label validInputLabel = new Label();
	
	private int inputVal;
	private int[] arr;
	private boolean isSorted = false;
	boolean isRunning = false;
	private boolean incrOrder = false;
	private int delay = 500;
	
	private static class SmallerValuePopupPanel extends PopupPanel {
		private Label popupMessageLabel = new Label();
		public SmallerValuePopupPanel() {
			super(true);
			popupMessageLabel.setStyleName("validInputLabel");
			popupMessageLabel.setText("Please, select a value smaller or equal to 30");
			setWidget(popupMessageLabel);
		}
	}
	
	@Override
	public void onModuleLoad() {
		
		textBox.addKeyPressHandler(this);
		textBox.addKeyDownHandler(new KeyDownHandler() {
		    @Override
		    public void onKeyDown(KeyDownEvent event) {
		    	if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
		    		executeInputClick();
		    	}
		    }
		});
		textBox.setStyleName("textBox");
		
		enterButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				executeInputClick();
			}
		});
		enterButton.setStyleName("enterButton");

		validInputLabel.setStyleName("validInputLabel");
		validInputLabel.setText("Value must be in the range of 1 to 1000");
		
		inputQuestionLabel.setStyleName("inputQuestionLabel");
		inputQuestionLabel.setText("How many numbers to display?");
		
		introScreenPanel.add(inputQuestionLabel);
		introScreenPanel.add(textBox);
		introScreenPanel.add(enterButton);

		RootPanel.get("introScreenContainer").add(introScreenPanel);
	}

	@Override
	public void onKeyPress(KeyPressEvent event) {
		introScreenPanel.remove(validInputLabel);
		RegExp textBoxRegExpValid = RegExp.compile("\\D");
		
		if (textBoxRegExpValid.test(textBox.getText())) {
			introScreenPanel.add(validInputLabel);
		}
	}
	
	private void executeInputClick() {
		String inputValStr = textBox.getText();
		if (isValidValue(inputValStr)) {
			executeClearDataCommand();
			textBox.setText(inputValStr);
			inputVal = Integer.parseInt(inputValStr);
			executeInputDataCommand();
			sortScreenLoad();
		} else {
			introScreenPanel.add(validInputLabel);	
		}
	}
	
	private static boolean isValidValue(String value) {
		if (value == null) {
			return false;
		}
	
		if (value.length() > 4) {
			return false;
		}

		int inputVal = Integer.parseInt(value);
		boolean result = (inputVal > 0) && (inputVal <= 1000);
		return result;
	}
	
	private void sortScreenLoad() {
		
		String htmlString = "<table id=\"sortScreenTable\" align=\"left\">"
				+" <tr><td id=\"sortScreenContainer\" align=\"left\"></td></tr></table>";

		HTMLPanel htmlPanel = new HTMLPanel(htmlString);
		
		sortResetButtonPanel.add(htmlPanel);
		sortScreenPanel.add(htmlPanel);
		
		sortButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (isRunning) {
					isSorted = false;
					isRunning = false;
					incrOrder = !incrOrder;
					int[] copiedArr = Arrays.copyOf(arr, inputVal);
					arr = new int[0];
					arr = Arrays.copyOf(copiedArr, copiedArr.length);
				} 
				isSorted = false;
				isRunning = true;
				executeSortCommand();
			}
		});
		sortButton.setStyleName("sortResButton");
		
		resetButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				sortResetButtonPanel.remove(sortButton);
				sortResetButtonPanel.remove(resetButton);
				executeClearDataCommand();
				onModuleLoad();
			}
		});
		resetButton.setStyleName("sortResButton");
		
		sortResetButtonPanel.add(sortButton);
		sortResetButtonPanel.add(resetButton);
		
		sortScreenPanel.add(flexTable);
		sortScreenPanel.add(sortResetButtonPanel);
		
		RootPanel.get("introScreenContainer").clear();
		RootPanel.get("sortScreenContainer").add(sortScreenPanel);
	}
	
	private void executeClearDataCommand() {
		inputVal = 0;
		arr = new int[0];
		isSorted = false;
		isRunning = false;
		incrOrder = false;
		textBox.setValue(null);
		flexTable.removeAllRows();
		introScreenPanel.remove(validInputLabel);
	}
	
	private void executeInputDataCommand() {
		arr = getRandNumArr(inputVal);;
		numbButtonTableAdd();
	}
	
	private void numbButtonTableAdd() {
		for (int i = 0; i < arr.length; i++) {
			String numbButtonName = String.valueOf(arr[i]);
			Button numbButton = new Button();
			numbButton.setText(numbButtonName);
			numbButton.setStyleName("numbButton");
			
			numbButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					if (Integer.parseInt(numbButton.getText()) > 30) {
						new SmallerValuePopupPanel().show();
					} else {
						executeClearDataCommand();
						inputVal = Integer.parseInt(numbButton.getText());
						executeInputDataCommand();
					}
				}
			});

			addRow(numbButton, i);
		}
	}

	/**
	 * Add a row to the flexTable.
	 */
	private void addRow(Button button, int currentNumb) {
		int currRow = currentNumb%10;
		int currColumn = currentNumb/10;
		if (currColumn > 0) {
			flexTable.addCell(currRow);
		}
		flexTable.setWidget(currRow, currColumn, button);
	}

	/************************************
	 * 
	 * The Quicksort algorithm is defined in this block
	 * 
	 ************************************/	
	
	private void executeSortCommand() {
		Scheduler.get().scheduleFixedDelay(repCommand, delay);
	}
	
	RepeatingCommand repCommand = new RepeatingCommand() {
		
		private int l, r;
		private Stack<Integer> stack = new Stack<Integer>();
		
		private int[] iterativeQsort(int[] numbers, boolean incrDescOrder) {
			while (!stack.isEmpty()) {
				l = stack.pop();
				r = stack.pop();
				if (r <= l) {
					continue;
				}

				int i = l - 1;
				int j = r;
				int partVal = numbers[r];
				
				while (true) {
					while (less(numbers[++i], partVal, incrDescOrder))
						if (i == r) break;
					while (less(partVal, numbers[--j], incrDescOrder))
						if (j == l) break;
					if (i >= j) break;
					swap(numbers, i, j);
					return numbers;
				}
				if (numbers[i] != numbers[r]){
					swap(numbers, i, r);
					return numbers;
				}

				if (i - l > r - i) {
					stackPush(stack, l, i - 1);
					stackPush(stack, i + 1, r);
				} else {
					stackPush(stack, i + 1, r);
					stackPush(stack, l, i - 1);
				}
			}
			isSorted = true;
			return numbers;
		}

		private void stackPush(Stack<Integer> stack, int first, int last) {
			stack.push(last);
			stack.push(first);
		}
		
		private boolean less(int first, int second, boolean incrOrder) {
			boolean result;
			if (incrOrder) {
				result = first < second;
			} else {
				result = first > second;
			}
			return result;
		}

		private void swap(int[] arr, int i, int j) {
			if (arr[i] != arr[j]) {
				int temp = arr[i];
				arr[i] = arr[j];
				arr[j] = temp;
			}
		}
		
		@Override
		public boolean execute() {
			if (isSorted) {
				isSorted = false;
				incrOrder = !incrOrder;
				isRunning = false;
			} else {
				if (!isSorted && !isRunning) {
					stack.clear();
					return false;
				}
				stackPush(stack, 0, arr.length - 1);
				iterativeQsort(arr, incrOrder);

				flexTable.removeAllRows();
				numbButtonTableAdd();
			}
			return isRunning;
		}
	};

	/************************************
	 * 
	 * The pseudorandom sequence generation code
	 * is defined in this part 
	 * 
	 ************************************/
	
	private int[] getRandNumArr(int numbVal) {
		Integer[] randArr = new Integer[numbVal];
		int necessVal = randNumArr(1, 30)[0];
		randArr = Arrays.copyOf(randNumArr(numbVal-1, 1000), numbVal);
		randArr[randArr.length-1] = necessVal;
		
		List<Integer> intList = Arrays.asList(randArr);
		Collections.shuffle(intList);
		
		Integer[] intArr = new Integer[intList.size()];
		intArr = intList.toArray(intArr);
		return Arrays.stream(intArr).mapToInt(Integer::intValue).toArray();
	}
	
	private Integer[] randNumArr(int numbVal, int maxVal) {
		int min = 1;
		int max = maxVal + 1;
		int diff = max - min;
		Random random = new Random();
		Integer[] randArr = new Integer[numbVal];
		for (int i = 0; i < numbVal; i++) {
			int randInt = random.nextInt(diff) + min;
			randArr[i] = randInt;
		}
		return randArr;
	}
	
	/** 
	 ************************************/
}