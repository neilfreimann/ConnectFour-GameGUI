package com.home.neil.connectfour.boardstate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.connectfour.knowledgebase.KnowledgeBaseFileAccessReadEvaluatedTask;
import com.home.neil.connectfour.knowledgebase.KnowledgeBaseFileAccessReadTask;
import com.home.neil.connectfour.knowledgebase.KnowledgeBaseFileAccessWriteEvaluatedTask;
import com.home.neil.connectfour.knowledgebase.KnowledgeBaseFileAccessWriteTask;
import com.home.neil.connectfour.knowledgebase.KnowledgeBaseFilePool;
import com.home.neil.connectfour.knowledgebase.exception.KnowledgeBaseException;
import com.home.neil.connectfour.managers.appmanager.ApplicationPrecompilerSettings;


public class BoardState {
	public static final String CLASS_NAME = BoardState.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	private static boolean slogBoardStateActive = false;

	public static final int MAX_ROWS = 6;
	public static final int MAX_COLUMNS = 7;
	public static final int CONNECT_LENGTH = 4;

	public static final int CELL_STATE_SIZE_IN_BITS = 2;
	public static final int MOVE_STATE_SIZE_IN_BITS = 4;
	public static final int GAME_STATE_SIZE_IN_BITS = 2;
	public static final int BIT_DELIMITER_SIZE = 1;

	public static final int BOARD_STATE_SIZE_IN_BITS = MAX_ROWS * MAX_COLUMNS * CELL_STATE_SIZE_IN_BITS + BIT_DELIMITER_SIZE + MOVE_STATE_SIZE_IN_BITS
			+ BIT_DELIMITER_SIZE + GAME_STATE_SIZE_IN_BITS + BIT_DELIMITER_SIZE;

	public static final int BOARD_STARTLOCATION_BIT = 0;
	public static final int BOARD_DELIMITERLOCATION_BIT = MAX_ROWS * MAX_COLUMNS * CELL_STATE_SIZE_IN_BITS;
	public static final int MOVE_STARTLOCATION_BIT = MAX_ROWS * MAX_COLUMNS * CELL_STATE_SIZE_IN_BITS + BIT_DELIMITER_SIZE;
	public static final int MOVE_DELIMITERLOCATION_BIT = MAX_ROWS * MAX_COLUMNS * CELL_STATE_SIZE_IN_BITS + BIT_DELIMITER_SIZE + MOVE_STATE_SIZE_IN_BITS;
	public static final int GAMESTATE_STARTLOCATION_BIT = MAX_ROWS * MAX_COLUMNS * CELL_STATE_SIZE_IN_BITS + BIT_DELIMITER_SIZE + MOVE_STATE_SIZE_IN_BITS
			+ BIT_DELIMITER_SIZE;
	public static final int GAMESTATE_DELIMITERLOCATION_BIT = MAX_ROWS * MAX_COLUMNS * CELL_STATE_SIZE_IN_BITS + BIT_DELIMITER_SIZE + MOVE_STATE_SIZE_IN_BITS
			+ BIT_DELIMITER_SIZE + GAME_STATE_SIZE_IN_BITS;

	public static final byte SCORE_WINNING_MOVE = 100;
	public static final byte SCORE_LOSING_MOVE = -100;
	public static final byte SCORE_UNKNOWN_MOVE = -120;
	public static final byte SCORE_DRAW_MOVE = 0;

	private KnowledgeBaseFilePool mKnowledgeBaseFilePool = null;
	
	private BitSet mBoardStateInBits = null;

	private MoveScore mMoveScore = null;
	
	public static final int WINNING_COMBO_SIZE_IN_BITS = 9;
	
	public static enum WinningCombo {
		
		//						Who			Direction			COLUMN POS				ROW POS
		OPPONENT_VERTICAL_0_0(	false, 		false, false,   	false, false, false,   	false, false, false, 	"OV00", 0), 
		OPPONENT_VERTICAL_0_1(	false, 		false, false,   	false, false, false,   	false, false, true, 	"OV01", 1),  
		OPPONENT_VERTICAL_0_2(	false, 		false, false,   	false, false, false,   	false, true, false, 	"OV02", 2),   

		OPPONENT_VERTICAL_1_0(	false, 		false, false,   	false, false, true,   	false, false, false, 	"OV10", 3),  
		OPPONENT_VERTICAL_1_1(	false, 		false, false,   	false, false, true,   	false, false, true, 	"OV11", 4),  
		OPPONENT_VERTICAL_1_2(	false, 		false, false,   	false, false, true,   	false, true, false, 	"OV12", 5),  

		OPPONENT_VERTICAL_2_0(	false, 		false, false,   	false, true, false,   	false, false, false, 	"OV20", 6),   
		OPPONENT_VERTICAL_2_1(	false, 		false, false,   	false, true, false,   	false, false, true, 	"OV21", 7),    
		OPPONENT_VERTICAL_2_2(	false, 		false, false,   	false, true, false,   	false, true, false, 	"OV22", 8),     

		OPPONENT_VERTICAL_3_0(	false, 		false, false,   	false, true, true,   	false, false, false, 	"OV30", 9),      
		OPPONENT_VERTICAL_3_1(	false, 		false, false,   	false, true, true,   	false, false, true, 	"OV31", 10),      
		OPPONENT_VERTICAL_3_2(	false, 		false, false,   	false, true, true,   	false, true, false, 	"OV32", 11),       

		OPPONENT_VERTICAL_4_0(	false, 		false, false,   	true, false, false,   	false, false, false, 	"OV40", 12),       
		OPPONENT_VERTICAL_4_1(	false, 		false, false,   	true, false, false,   	false, false, true, 	"OV41", 13),        
		OPPONENT_VERTICAL_4_2(	false, 		false, false,   	true, false, false,   	false, true, false, 	"OV42", 14),         

		OPPONENT_VERTICAL_5_0(	false, 		false, false,   	true, false, true,   	false, false, false, 	"OV50", 15),          
		OPPONENT_VERTICAL_5_1(	false, 		false, false,   	true, false, true,   	false, false, true, 	"OV51", 16),           
		OPPONENT_VERTICAL_5_2(	false, 		false, false,   	true, false, true,   	false, true, false, 	"OV52", 17),            

		OPPONENT_VERTICAL_6_0(	false, 		false, false,   	true, true, false,   	false, false, false, 	"OV60", 18),           
		OPPONENT_VERTICAL_6_1(	false, 		false, false,   	true, true, false,   	false, false, true, 	"OV61", 19),            
		OPPONENT_VERTICAL_6_2(	false, 		false, false,   	true, true, false,   	false, true, false, 	"OV62", 20),            


		//							Who			Direction			COLUMN POS				ROW POS
		OPPONENT_HORIZONTAL_0_0(	false, 		false, true,  	 	false, false, false,   	false, false, false, 	"OH00", 21),            
		OPPONENT_HORIZONTAL_0_1(	false, 		false, true,   		false, false, false,   	false, false, true, 	"OH01", 22),             
		OPPONENT_HORIZONTAL_0_2(	false, 		false, true,   		false, false, false,   	false, true, false, 	"OH02", 23),             
		OPPONENT_HORIZONTAL_0_3(	false, 		false, true,   		false, false, false,   	false, true, true, 		"OH03", 24),             
		OPPONENT_HORIZONTAL_0_4(	false, 		false, true,  	 	false, false, false,   	true, false, false, 	"OH04", 25),             
		OPPONENT_HORIZONTAL_0_5(	false, 		false, true,   		false, false, false,   	true, false, true, 		"OH05", 26),             

		OPPONENT_HORIZONTAL_1_0(	false, 		false, true,  	 	false, false, true,   	false, false, false, 	"OH10", 27),             
		OPPONENT_HORIZONTAL_1_1(	false, 		false, true,   		false, false, true,   	false, false, true, 	"OH11", 28),              
		OPPONENT_HORIZONTAL_1_2(	false, 		false, true,   		false, false, true,   	false, true, false, 	"OH12", 29),               
		OPPONENT_HORIZONTAL_1_3(	false, 		false, true,   		false, false, true,   	false, true, true, 		"OH13", 30),               
		OPPONENT_HORIZONTAL_1_4(	false, 		false, true,  	 	false, false, true,   	true, false, false, 	"OH14", 31),               
		OPPONENT_HORIZONTAL_1_5(	false, 		false, true,   		false, false, true,   	true, false, true, 		"OH15", 32),               
		
		OPPONENT_HORIZONTAL_2_0(	false, 		false, true,  	 	false, true, false,   	false, false, false, 	"OH20", 33),              
		OPPONENT_HORIZONTAL_2_1(	false, 		false, true,   		false, true, false,   	false, false, true, 	"OH21", 34),               
		OPPONENT_HORIZONTAL_2_2(	false, 		false, true,   		false, true, false,   	false, true, false, 	"OH22", 35),                
		OPPONENT_HORIZONTAL_2_3(	false, 		false, true,   		false, true, false,   	false, true, true, 		"OH23", 36),               
		OPPONENT_HORIZONTAL_2_4(	false, 		false, true,  	 	false, true, false,   	true, false, false, 	"OH24", 37),               
		OPPONENT_HORIZONTAL_2_5(	false, 		false, true,   		false, true, false,   	true, false, true, 		"OH25", 38),               
		
		OPPONENT_HORIZONTAL_3_0(	false, 		false, true,  	 	false, true, true,   	false, false, false, 	"OH30", 39),               
		OPPONENT_HORIZONTAL_3_1(	false, 		false, true,   		false, true, true,   	false, false, true, 	"OH31", 40),                
		OPPONENT_HORIZONTAL_3_2(	false, 		false, true,   		false, true, true,   	false, true, false, 	"OH32", 41),                
		OPPONENT_HORIZONTAL_3_3(	false, 		false, true,   		false, true, true,   	false, true, true, 		"OH33", 42),                
		OPPONENT_HORIZONTAL_3_4(	false, 		false, true,  	 	false, true, true,   	true, false, false, 	"OH34", 43),                
		OPPONENT_HORIZONTAL_3_5(	false, 		false, true,   		false, true, true,   	true, false, true, 		"OH35", 44),                
		
		//						Who			Direction			COLUMN POS				ROW POS
		OPPONENT_DIAGONAL_0_0(	false, 		true, false,  	 	false, false, false,   	false, false, false, 	"OD00", 45),                
		OPPONENT_DIAGONAL_0_1(	false, 		true, false,   		false, false, false,   	false, false, true, 	"OD01", 46),                 
		OPPONENT_DIAGONAL_0_2(	false, 		true, false,   		false, false, false,   	false, true, false, 	"OD02", 47),                  

		OPPONENT_DIAGONAL_1_0(	false, 		true, false,  	 	false, false, true,   	false, false, false, 	"OD10", 48),                 
		OPPONENT_DIAGONAL_1_1(	false, 		true, false,   		false, false, true,   	false, false, true, 	"OD11", 49),                  
		OPPONENT_DIAGONAL_1_2(	false, 		true, false,   		false, false, true,   	false, true, false, 	"OD12", 50),                   
		
		OPPONENT_DIAGONAL_2_0(	false, 		true, false,  	 	false, true, false,   	false, false, false, 	"OD20", 51),                  
		OPPONENT_DIAGONAL_2_1(	false, 		true, false,   		false, true, false,   	false, false, true, 	"OD21", 52),                   
		OPPONENT_DIAGONAL_2_2(	false, 		true, false,   		false, true, false,   	false, true, false, 	"OD22", 53),                    
		
		OPPONENT_DIAGONAL_3_0(	false, 		true, false,  	 	false, true, true,   	false, false, false, 	"OD30", 54),                   
		OPPONENT_DIAGONAL_3_1(	false, 		true, false,   		false, true, true,   	false, false, true, 	"OD31", 55),                    
		OPPONENT_DIAGONAL_3_2(	false, 		true, false,   		false, true, true,   	false, true, false, 	"OD32", 56),                    
		
		//							Who			Direction			COLUMN POS				ROW POS
		OPPONENT_OPPOSITE_0_3(	false, 		true, true,   		false, false, false,   	false, true, true, 		"OO03", 57),                    
		OPPONENT_OPPOSITE_0_4(	false, 		true, true,  	 	false, false, false,   	true, false, false, 	"OO04", 58),                     
		OPPONENT_OPPOSITE_0_5(	false, 		true, true,   		false, false, false,   	true, false, true, 		"OO05", 59),                      

		OPPONENT_OPPOSITE_1_3(	false, 		true, true,   		false, false, true,   	false, true, true, 		"OO13", 60),                     
		OPPONENT_OPPOSITE_1_4(	false, 		true, true,  	 	false, false, true,   	true, false, false, 	"OO14", 61),                      
		OPPONENT_OPPOSITE_1_5(	false, 		true, true,   		false, false, true,   	true, false, true, 		"OO15", 62),                       
		
		OPPONENT_OPPOSITE_2_3(	false, 		true, true,   		false, true, false,   	false, true, true, 		"OO23", 63),                      
		OPPONENT_OPPOSITE_2_4(	false, 		true, true,  	 	false, true, false,   	true, false, false, 	"OO24", 64),                       
		OPPONENT_OPPOSITE_2_5(	false, 		true, true,   		false, true, false,   	true, false, true, 		"OO25", 65),                       
		
		OPPONENT_OPPOSITE_3_3(	false, 		true, true,   		false, true, true,   	false, true, true, 		"OO33", 66),                       
		OPPONENT_OPPOSITE_3_4(	false, 		true, true,  	 	false, true, true,   	true, false, false, 	"OO34", 67),                        
		OPPONENT_OPPOSITE_3_5(	false, 		true, true,   		false, true, true,   	true, false, true, 		"OO35", 68),                        
		
		
		//						Who			Direction			COLUMN POS				ROW POS
		SELF_VERTICAL_0_0(	true, 		false, false,   	false, false, false,   	false, false, false, 	"SV00", 0), 
		SELF_VERTICAL_0_1(	true, 		false, false,   	false, false, false,   	false, false, true, 	"SV01", 1),  
		SELF_VERTICAL_0_2(	true, 		false, false,   	false, false, false,   	false, true, false, 	"SV02", 2),   

		SELF_VERTICAL_1_0(	true, 		false, false,   	false, false, true,   	false, false, false, 	"SV10", 3),  
		SELF_VERTICAL_1_1(	true, 		false, false,   	false, false, true,   	false, false, true, 	"SV11", 4),  
		SELF_VERTICAL_1_2(	true, 		false, false,   	false, false, true,   	false, true, false, 	"SV12", 5),  

		SELF_VERTICAL_2_0(	true, 		false, false,   	false, true, false,   	false, false, false, 	"SV20", 6),   
		SELF_VERTICAL_2_1(	true, 		false, false,   	false, true, false,   	false, false, true, 	"SV21", 7),    
		SELF_VERTICAL_2_2(	true, 		false, false,   	false, true, false,   	false, true, false, 	"SV22", 8),     

		SELF_VERTICAL_3_0(	true, 		false, false,   	false, true, true,   	false, false, false, 	"SV30", 9),      
		SELF_VERTICAL_3_1(	true, 		false, false,   	false, true, true,   	false, false, true, 	"SV31", 10),      
		SELF_VERTICAL_3_2(	true, 		false, false,   	false, true, true,   	false, true, false, 	"SV32", 11),       

		SELF_VERTICAL_4_0(	true, 		false, false,   	true, false, false,   	false, false, false, 	"SV40", 12),       
		SELF_VERTICAL_4_1(	true, 		false, false,   	true, false, false,   	false, false, true, 	"SV41", 13),        
		SELF_VERTICAL_4_2(	true, 		false, false,   	true, false, false,   	false, true, false, 	"SV42", 14),         

		SELF_VERTICAL_5_0(	true, 		false, false,   	true, false, true,   	false, false, false, 	"SV50", 15),          
		SELF_VERTICAL_5_1(	true, 		false, false,   	true, false, true,   	false, false, true, 	"SV51", 16),           
		SELF_VERTICAL_5_2(	true, 		false, false,   	true, false, true,   	false, true, false, 	"SV52", 17),            

		SELF_VERTICAL_6_0(	true, 		false, false,   	true, true, false,   	false, false, false, 	"SV60", 18),           
		SELF_VERTICAL_6_1(	true, 		false, false,   	true, true, false,   	false, false, true, 	"SV61", 19),            
		SELF_VERTICAL_6_2(	true, 		false, false,   	true, true, false,   	false, true, false, 	"SV62", 20),            


		//							Who			Direction			COLUMN POS				ROW POS
		SELF_HORIZONTAL_0_0(	true, 		false, true,  	 	false, false, false,   	false, false, false, 	"SH00", 21),            
		SELF_HORIZONTAL_0_1(	true, 		false, true,   		false, false, false,   	false, false, true, 	"SH01", 22),             
		SELF_HORIZONTAL_0_2(	true, 		false, true,   		false, false, false,   	false, true, false, 	"SH02", 23),             
		SELF_HORIZONTAL_0_3(	true, 		false, true,   		false, false, false,   	false, true, true, 		"SH03", 24),             
		SELF_HORIZONTAL_0_4(	true, 		false, true,  	 	false, false, false,   	true, false, false, 	"SH04", 25),             
		SELF_HORIZONTAL_0_5(	true, 		false, true,   		false, false, false,   	true, false, true, 		"SH05", 26),             

		SELF_HORIZONTAL_1_0(	true, 		false, true,  	 	false, false, true,   	false, false, false, 	"SH10", 27),             
		SELF_HORIZONTAL_1_1(	true, 		false, true,   		false, false, true,   	false, false, true, 	"SH11", 28),              
		SELF_HORIZONTAL_1_2(	true, 		false, true,   		false, false, true,   	false, true, false, 	"SH12", 29),               
		SELF_HORIZONTAL_1_3(	true, 		false, true,   		false, false, true,   	false, true, true, 		"SH13", 30),               
		SELF_HORIZONTAL_1_4(	true, 		false, true,  	 	false, false, true,   	true, false, false, 	"SH14", 31),               
		SELF_HORIZONTAL_1_5(	true, 		false, true,   		false, false, true,   	true, false, true, 		"SH15", 32),               
		
		SELF_HORIZONTAL_2_0(	true, 		false, true,  	 	false, true, false,   	false, false, false, 	"SH20", 33),              
		SELF_HORIZONTAL_2_1(	true, 		false, true,   		false, true, false,   	false, false, true, 	"SH21", 34),               
		SELF_HORIZONTAL_2_2(	true, 		false, true,   		false, true, false,   	false, true, false, 	"SH22", 35),                
		SELF_HORIZONTAL_2_3(	true, 		false, true,   		false, true, false,   	false, true, true, 		"SH23", 36),               
		SELF_HORIZONTAL_2_4(	true, 		false, true,  	 	false, true, false,   	true, false, false, 	"SH24", 37),               
		SELF_HORIZONTAL_2_5(	true, 		false, true,   		false, true, false,   	true, false, true, 		"SH25", 38),               
		
		SELF_HORIZONTAL_3_0(	true, 		false, true,  	 	false, true, true,   	false, false, false, 	"SH30", 39),               
		SELF_HORIZONTAL_3_1(	true, 		false, true,   		false, true, true,   	false, false, true, 	"SH31", 40),                
		SELF_HORIZONTAL_3_2(	true, 		false, true,   		false, true, true,   	false, true, false, 	"SH32", 41),                
		SELF_HORIZONTAL_3_3(	true, 		false, true,   		false, true, true,   	false, true, true, 		"SH33", 42),                
		SELF_HORIZONTAL_3_4(	true, 		false, true,  	 	false, true, true,   	true, false, false, 	"SH34", 43),                
		SELF_HORIZONTAL_3_5(	true, 		false, true,   		false, true, true,   	true, false, true, 		"SH35", 44),                
		
		//						Who			Direction			COLUMN POS				ROW POS
		SELF_DIAGONAL_0_0(	true, 		true, false,  	 	false, false, false,   	false, false, false, 	"SD00", 45),                
		SELF_DIAGONAL_0_1(	true, 		true, false,   		false, false, false,   	false, false, true, 	"SD01", 46),                 
		SELF_DIAGONAL_0_2(	true, 		true, false,   		false, false, false,   	false, true, false, 	"SD02", 47),                  

		SELF_DIAGONAL_1_0(	true, 		true, false,  	 	false, false, true,   	false, false, false, 	"SD10", 48),                 
		SELF_DIAGONAL_1_1(	true, 		true, false,   		false, false, true,   	false, false, true, 	"SD11", 49),                  
		SELF_DIAGONAL_1_2(	true, 		true, false,   		false, false, true,   	false, true, false, 	"SD12", 50),                   
		
		SELF_DIAGONAL_2_0(	true, 		true, false,  	 	false, true, false,   	false, false, false, 	"SD20", 51),                  
		SELF_DIAGONAL_2_1(	true, 		true, false,   		false, true, false,   	false, false, true, 	"SD21", 52),                   
		SELF_DIAGONAL_2_2(	true, 		true, false,   		false, true, false,   	false, true, false, 	"SD22", 53),                    
		
		SELF_DIAGONAL_3_0(	true, 		true, false,  	 	false, true, true,   	false, false, false, 	"SD30", 54),                   
		SELF_DIAGONAL_3_1(	true, 		true, false,   		false, true, true,   	false, false, true, 	"SD31", 55),                    
		SELF_DIAGONAL_3_2(	true, 		true, false,   		false, true, true,   	false, true, false, 	"SD32", 56),                    
		
		//							Who			Direction			COLUMN POS				ROW POS
		SELF_OPPOSITE_0_3(	true, 		true, true,   		false, false, false,   	false, true, true, 		"SO03", 57),                    
		SELF_OPPOSITE_0_4(	true, 		true, true,  	 	false, false, false,   	true, false, false, 	"SO04", 58),                     
		SELF_OPPOSITE_0_5(	true, 		true, true,   		false, false, false,   	true, false, true, 		"SO05", 59),                      

		SELF_OPPOSITE_1_3(	true, 		true, true,   		false, false, true,   	false, true, true, 		"SO13", 60),                     
		SELF_OPPOSITE_1_4(	true, 		true, true,  	 	false, false, true,   	true, false, false, 	"SO14", 61),                      
		SELF_OPPOSITE_1_5(	true, 		true, true,   		false, false, true,   	true, false, true, 		"SO15", 62),                       
		
		SELF_OPPOSITE_2_3(	true, 		true, true,   		false, true, false,   	false, true, true, 		"SO23", 63),                      
		SELF_OPPOSITE_2_4(	true, 		true, true,  	 	false, true, false,   	true, false, false, 	"SO24", 64),                       
		SELF_OPPOSITE_2_5(	true, 		true, true,   		false, true, false,   	true, false, true, 		"SO25", 65),                       
		
		SELF_OPPOSITE_3_3(	true, 		true, true,   		false, true, true,   	false, true, true, 		"SO33", 66),                       
		SELF_OPPOSITE_3_4(	true, 		true, true,  	 	false, true, true,   	true, false, false, 	"SO34", 67),                        
		SELF_OPPOSITE_3_5(	true, 		true, true,   		false, true, true,   	true, false, true, 		"SO35", 68);                        
				
		
		private BitSet mWinningCombo = new BitSet(BoardState.WINNING_COMBO_SIZE_IN_BITS);
		private String mWinningComboString = new String();
		private int mWinningComboBitPosition = 0;
		ArrayList <MovePosition> mRequiredMovePositions = null;
		
		WinningCombo (boolean pFirst, 
					boolean pSecond, boolean pThird, 
					boolean pFourth, boolean pFifth, boolean pSixth,
					boolean pSeventh, boolean pEighth, boolean pNinth,
					String pWinningComboString, int pWinningComboBitPosition) {
			mWinningCombo.set(0, pFirst);
			mWinningCombo.set(1, pSecond);
			mWinningCombo.set(2, pThird);
			mWinningCombo.set(3, pFourth);
			mWinningCombo.set(4, pFifth);
			mWinningCombo.set(5, pSixth);
			mWinningCombo.set(6, pSeventh);
			mWinningCombo.set(7, pEighth);
			mWinningCombo.set(8, pNinth);
			mWinningComboString = pWinningComboString;
			mWinningComboBitPosition = pWinningComboBitPosition;
		}

		public boolean isSelfWinningCombo () {
			return mWinningCombo.get(0);
		}

		public boolean isOpponentWinningCombo () {
			return !mWinningCombo.get(0);
		}
		
		public ArrayList <MovePosition> getRequiredMovePositions () {
			return mRequiredMovePositions;
		}
		
		public void setRequiredMovePositions (ArrayList <MovePosition> pRequiredMovePositions) {
			mRequiredMovePositions = pRequiredMovePositions;
		}

		
		public BitSet getWinningComboBitSet() {
			return mWinningCombo;
		}

		public String getWinningComboString() {
			return mWinningComboString;
		}

		public int getWinningComboBitPosition() {
			return mWinningComboBitPosition;
		}
	}


	
	
	public static final int MOVE_POSITION_SIZE_IN_BITS = 7;
	
	public static enum MovePosition {
		//				Who			COLUMN POS				ROW POS
		OPPONENT_0_0(	false, 		false, false, false,   	false, false, false, 	0,0, "O00", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_0_0,
				WinningCombo.SELF_VERTICAL_0_0,
				WinningCombo.SELF_DIAGONAL_0_0
				)),	new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_0_0,
						WinningCombo.OPPONENT_VERTICAL_0_0,
						WinningCombo.OPPONENT_DIAGONAL_0_0
				))), 
		OPPONENT_0_1(	false, 		false, false, false,   	false, false, true, 	0,1, "O01", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_0_1,
				WinningCombo.SELF_VERTICAL_0_0,
				WinningCombo.SELF_VERTICAL_0_1,
				WinningCombo.SELF_DIAGONAL_0_1
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_0_1,
						WinningCombo.OPPONENT_VERTICAL_0_0,
						WinningCombo.OPPONENT_VERTICAL_0_1,
						WinningCombo.OPPONENT_DIAGONAL_0_1
				))),  
		OPPONENT_0_2(	false, 		false, false, false,   	false, true, false, 	0,2, "O02", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_0_2,
				WinningCombo.SELF_VERTICAL_0_0,
				WinningCombo.SELF_VERTICAL_0_1,
				WinningCombo.SELF_VERTICAL_0_2,
				WinningCombo.SELF_DIAGONAL_0_2
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_0_2,
						WinningCombo.OPPONENT_VERTICAL_0_0,
						WinningCombo.OPPONENT_VERTICAL_0_1,
						WinningCombo.OPPONENT_VERTICAL_0_2,
						WinningCombo.OPPONENT_DIAGONAL_0_2
				))),    
		OPPONENT_0_3(	false, 		false, false, false,   	false, true, true, 		0,3, "O03", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_0_3,
				WinningCombo.SELF_VERTICAL_0_0,
				WinningCombo.SELF_VERTICAL_0_1,
				WinningCombo.SELF_VERTICAL_0_2,
				WinningCombo.SELF_OPPOSITE_0_3
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_0_3,
						WinningCombo.OPPONENT_VERTICAL_0_0,
						WinningCombo.OPPONENT_VERTICAL_0_1,
						WinningCombo.OPPONENT_VERTICAL_0_2,
						WinningCombo.OPPONENT_OPPOSITE_0_3
				))),  
		OPPONENT_0_4(	false, 		false, false, false,   	true, false, false, 	0,4, "O04", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_0_4,
				WinningCombo.SELF_VERTICAL_0_1,
				WinningCombo.SELF_VERTICAL_0_2,
				WinningCombo.SELF_OPPOSITE_0_4
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_0_4,
						WinningCombo.OPPONENT_VERTICAL_0_1,
						WinningCombo.OPPONENT_VERTICAL_0_2,
						WinningCombo.OPPONENT_OPPOSITE_0_4
				))),  
		OPPONENT_0_5(	false, 		false, false, false,   	true, false, true, 		0,5, "O05", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_0_5,
				WinningCombo.SELF_VERTICAL_0_2,
				WinningCombo.SELF_OPPOSITE_0_5
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_0_5,
						WinningCombo.OPPONENT_VERTICAL_0_2,
						WinningCombo.OPPONENT_OPPOSITE_0_5
				))),  
 

		OPPONENT_1_0(	false, 		false, false, true,   	false, false, false, 	1,0, "O10", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_0_0,
				WinningCombo.SELF_HORIZONTAL_1_0,
				WinningCombo.SELF_VERTICAL_1_0,
				WinningCombo.SELF_DIAGONAL_1_0
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_0_0,
						WinningCombo.OPPONENT_HORIZONTAL_1_0,
						WinningCombo.OPPONENT_VERTICAL_1_0,
						WinningCombo.OPPONENT_DIAGONAL_1_0
				))),   
		OPPONENT_1_1(	false, 		false, false, true,   	false, false, true, 	1,1, "O11", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_0_1,
				WinningCombo.SELF_HORIZONTAL_1_1,
				WinningCombo.SELF_VERTICAL_1_0,
				WinningCombo.SELF_VERTICAL_1_1,
				WinningCombo.SELF_DIAGONAL_0_0,
				WinningCombo.SELF_DIAGONAL_1_1
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_0_1,
						WinningCombo.OPPONENT_HORIZONTAL_1_1,
						WinningCombo.OPPONENT_VERTICAL_1_0,
						WinningCombo.OPPONENT_VERTICAL_1_1,
						WinningCombo.OPPONENT_DIAGONAL_0_0,
						WinningCombo.OPPONENT_DIAGONAL_1_1
				))),  
		OPPONENT_1_2(	false, 		false, false, true,   	false, true, false, 	1,2, "O12", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_0_2,
				WinningCombo.SELF_HORIZONTAL_1_2,
				WinningCombo.SELF_VERTICAL_1_0,
				WinningCombo.SELF_VERTICAL_1_1,
				WinningCombo.SELF_VERTICAL_1_2,
				WinningCombo.SELF_DIAGONAL_0_1,
				WinningCombo.SELF_DIAGONAL_1_2,
				WinningCombo.SELF_OPPOSITE_0_3
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_0_2,
						WinningCombo.OPPONENT_HORIZONTAL_1_2,
						WinningCombo.OPPONENT_VERTICAL_1_0,
						WinningCombo.OPPONENT_VERTICAL_1_1,
						WinningCombo.OPPONENT_VERTICAL_1_2,
						WinningCombo.OPPONENT_DIAGONAL_0_1,
						WinningCombo.OPPONENT_DIAGONAL_1_2,
						WinningCombo.OPPONENT_OPPOSITE_0_3
				))),  
		OPPONENT_1_3(	false, 		false, false, true,   	false, true, true, 		1,3, "O13", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_0_3,
				WinningCombo.SELF_HORIZONTAL_1_3,
				WinningCombo.SELF_VERTICAL_1_0,
				WinningCombo.SELF_VERTICAL_1_1,
				WinningCombo.SELF_VERTICAL_1_2,
				WinningCombo.SELF_DIAGONAL_0_2,
				WinningCombo.SELF_OPPOSITE_0_4,
				WinningCombo.SELF_OPPOSITE_1_3
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_0_3,
						WinningCombo.OPPONENT_HORIZONTAL_1_3,
						WinningCombo.OPPONENT_VERTICAL_1_0,
						WinningCombo.OPPONENT_VERTICAL_1_1,
						WinningCombo.OPPONENT_VERTICAL_1_2,
						WinningCombo.OPPONENT_DIAGONAL_0_2,
						WinningCombo.OPPONENT_OPPOSITE_0_4,
						WinningCombo.OPPONENT_OPPOSITE_1_3
				))),  
		OPPONENT_1_4(	false, 		false, false, true,   	true, false, false, 	1,4, "O14", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_0_4,
				WinningCombo.SELF_HORIZONTAL_1_4,
				WinningCombo.SELF_VERTICAL_1_1,
				WinningCombo.SELF_VERTICAL_1_2,
				WinningCombo.SELF_OPPOSITE_0_5,
				WinningCombo.SELF_OPPOSITE_1_4
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_0_4,
						WinningCombo.OPPONENT_HORIZONTAL_1_4,
						WinningCombo.OPPONENT_VERTICAL_1_1,
						WinningCombo.OPPONENT_VERTICAL_1_2,
						WinningCombo.OPPONENT_OPPOSITE_0_5,
						WinningCombo.OPPONENT_OPPOSITE_1_4
						))),  
		OPPONENT_1_5(	false, 		false, false, true,   	true, false, true,	 	1,5, "O15", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_0_5,
				WinningCombo.SELF_HORIZONTAL_1_5,
				WinningCombo.SELF_VERTICAL_1_2,
				WinningCombo.SELF_OPPOSITE_1_5
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_0_5,
						WinningCombo.OPPONENT_HORIZONTAL_1_5,
						WinningCombo.OPPONENT_VERTICAL_1_2,
						WinningCombo.OPPONENT_OPPOSITE_1_5
				))),  
				
		OPPONENT_2_0(	false, 		false, true, false,   	false, false, false, 	2,0, "O20", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_0_0,
				WinningCombo.SELF_HORIZONTAL_1_0,
				WinningCombo.SELF_HORIZONTAL_2_0,
				WinningCombo.SELF_VERTICAL_2_0,
				WinningCombo.SELF_DIAGONAL_2_0
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_0_0,
						WinningCombo.OPPONENT_HORIZONTAL_1_0,
						WinningCombo.OPPONENT_HORIZONTAL_2_0,
						WinningCombo.OPPONENT_VERTICAL_2_0,
						WinningCombo.OPPONENT_DIAGONAL_2_0
				))),   
		OPPONENT_2_1(	false, 		false, true, false,   	false, false, true, 	2,1, "O21", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_0_1,
				WinningCombo.SELF_HORIZONTAL_1_1,
				WinningCombo.SELF_HORIZONTAL_2_1,
				WinningCombo.SELF_VERTICAL_2_0,
				WinningCombo.SELF_VERTICAL_2_1,
				WinningCombo.SELF_DIAGONAL_1_0,
				WinningCombo.SELF_DIAGONAL_2_1,
				WinningCombo.SELF_OPPOSITE_0_3
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_0_1,
						WinningCombo.OPPONENT_HORIZONTAL_1_1,
						WinningCombo.OPPONENT_HORIZONTAL_2_1,
						WinningCombo.OPPONENT_VERTICAL_2_0,
						WinningCombo.OPPONENT_VERTICAL_2_1,
						WinningCombo.OPPONENT_DIAGONAL_1_0,
						WinningCombo.OPPONENT_DIAGONAL_2_1,
						WinningCombo.OPPONENT_OPPOSITE_0_3
				))),  
		OPPONENT_2_2(	false, 		false, true, false,   	false, true, false, 	2,2, "O22", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_0_2,
				WinningCombo.SELF_HORIZONTAL_1_2,
				WinningCombo.SELF_HORIZONTAL_2_2,
				WinningCombo.SELF_VERTICAL_2_0,
				WinningCombo.SELF_VERTICAL_2_1,
				WinningCombo.SELF_VERTICAL_2_2,
				WinningCombo.SELF_DIAGONAL_0_0,
				WinningCombo.SELF_DIAGONAL_1_1,
				WinningCombo.SELF_DIAGONAL_2_2,
				WinningCombo.SELF_OPPOSITE_0_4,
				WinningCombo.SELF_OPPOSITE_1_3
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_0_2,
						WinningCombo.OPPONENT_HORIZONTAL_1_2,
						WinningCombo.OPPONENT_HORIZONTAL_2_2,
						WinningCombo.OPPONENT_VERTICAL_2_0,
						WinningCombo.OPPONENT_VERTICAL_2_1,
						WinningCombo.OPPONENT_VERTICAL_2_2,
						WinningCombo.OPPONENT_DIAGONAL_0_0,
						WinningCombo.OPPONENT_DIAGONAL_1_1,
						WinningCombo.OPPONENT_DIAGONAL_2_2,
						WinningCombo.OPPONENT_OPPOSITE_0_4,
						WinningCombo.OPPONENT_OPPOSITE_1_3
				))),  
		OPPONENT_2_3(	false, 		false, true, false,   	false, true, true, 		2,3, "O23", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_0_3,
				WinningCombo.SELF_HORIZONTAL_1_3,
				WinningCombo.SELF_HORIZONTAL_2_3,
				WinningCombo.SELF_VERTICAL_2_0,
				WinningCombo.SELF_VERTICAL_2_1,
				WinningCombo.SELF_VERTICAL_2_2,
				WinningCombo.SELF_DIAGONAL_0_1,
				WinningCombo.SELF_DIAGONAL_1_2,
				WinningCombo.SELF_OPPOSITE_0_5,
				WinningCombo.SELF_OPPOSITE_1_4,
				WinningCombo.SELF_OPPOSITE_2_3
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_0_3,
						WinningCombo.OPPONENT_HORIZONTAL_1_3,
						WinningCombo.OPPONENT_HORIZONTAL_2_3,
						WinningCombo.OPPONENT_VERTICAL_2_0,
						WinningCombo.OPPONENT_VERTICAL_2_1,
						WinningCombo.OPPONENT_VERTICAL_2_2,
						WinningCombo.OPPONENT_DIAGONAL_0_1,
						WinningCombo.OPPONENT_DIAGONAL_1_2,
						WinningCombo.OPPONENT_OPPOSITE_0_5,
						WinningCombo.OPPONENT_OPPOSITE_1_4,
						WinningCombo.OPPONENT_OPPOSITE_2_3
				))),  
		OPPONENT_2_4(	false, 		false, true, false,   	true, false, false, 	2,4, "O24", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_0_4,
				WinningCombo.SELF_HORIZONTAL_1_4,
				WinningCombo.SELF_HORIZONTAL_2_4,
				WinningCombo.SELF_VERTICAL_2_1,
				WinningCombo.SELF_VERTICAL_2_2,
				WinningCombo.SELF_DIAGONAL_0_2,
				WinningCombo.SELF_OPPOSITE_1_5,
				WinningCombo.SELF_OPPOSITE_2_4
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_0_4,
						WinningCombo.OPPONENT_HORIZONTAL_1_4,
						WinningCombo.OPPONENT_HORIZONTAL_2_4,
						WinningCombo.OPPONENT_VERTICAL_2_1,
						WinningCombo.OPPONENT_VERTICAL_2_2,
						WinningCombo.OPPONENT_DIAGONAL_0_2,
						WinningCombo.OPPONENT_OPPOSITE_1_5,
						WinningCombo.OPPONENT_OPPOSITE_2_4
				))),  
		OPPONENT_2_5(	false, 		false, true, false,   	true, false, true,	 	2,5, "O25", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_0_5,
				WinningCombo.SELF_HORIZONTAL_1_5,
				WinningCombo.SELF_HORIZONTAL_2_5,
				WinningCombo.SELF_VERTICAL_2_2,
				WinningCombo.SELF_OPPOSITE_2_5
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_0_5,
						WinningCombo.OPPONENT_HORIZONTAL_1_5,
						WinningCombo.OPPONENT_HORIZONTAL_2_5,
						WinningCombo.OPPONENT_VERTICAL_2_2,
						WinningCombo.OPPONENT_OPPOSITE_2_5
				))),  


		OPPONENT_3_0(	false, 		false, true, true,   	false, false, false, 	3,0, "O30", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_0_0,
				WinningCombo.SELF_HORIZONTAL_1_0,
				WinningCombo.SELF_HORIZONTAL_2_0,
				WinningCombo.SELF_HORIZONTAL_3_0,
				WinningCombo.SELF_VERTICAL_3_0,
				WinningCombo.SELF_DIAGONAL_3_0,
				WinningCombo.SELF_OPPOSITE_0_3
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_0_0,
						WinningCombo.OPPONENT_HORIZONTAL_1_0,
						WinningCombo.OPPONENT_HORIZONTAL_2_0,
						WinningCombo.OPPONENT_HORIZONTAL_3_0,
						WinningCombo.OPPONENT_VERTICAL_3_0,
						WinningCombo.OPPONENT_DIAGONAL_3_0,
						WinningCombo.OPPONENT_OPPOSITE_0_3
				))),   
		OPPONENT_3_1(	false, 		false, true, true,   	false, false, true, 	3,1, "O31", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_0_1,
				WinningCombo.SELF_HORIZONTAL_1_1,
				WinningCombo.SELF_HORIZONTAL_2_1,
				WinningCombo.SELF_HORIZONTAL_3_1,
				WinningCombo.SELF_VERTICAL_3_0,
				WinningCombo.SELF_VERTICAL_3_1,
				WinningCombo.SELF_DIAGONAL_2_0,
				WinningCombo.SELF_DIAGONAL_3_1,
				WinningCombo.SELF_OPPOSITE_0_4,
				WinningCombo.SELF_OPPOSITE_1_3
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_0_1,
						WinningCombo.OPPONENT_HORIZONTAL_1_1,
						WinningCombo.OPPONENT_HORIZONTAL_2_1,
						WinningCombo.OPPONENT_HORIZONTAL_3_1,
						WinningCombo.OPPONENT_VERTICAL_3_0,
						WinningCombo.OPPONENT_VERTICAL_3_1,
						WinningCombo.OPPONENT_DIAGONAL_2_0,
						WinningCombo.OPPONENT_DIAGONAL_3_1,
						WinningCombo.OPPONENT_OPPOSITE_0_4,
						WinningCombo.OPPONENT_OPPOSITE_1_3
				))),  
		OPPONENT_3_2(	false, 		false, true, true,   	false, true, false, 	3,2, "O32", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_0_2,
				WinningCombo.SELF_HORIZONTAL_1_2,
				WinningCombo.SELF_HORIZONTAL_2_2,
				WinningCombo.SELF_HORIZONTAL_3_2,
				WinningCombo.SELF_VERTICAL_3_0,
				WinningCombo.SELF_VERTICAL_3_1,
				WinningCombo.SELF_VERTICAL_3_2,
				WinningCombo.SELF_DIAGONAL_1_0,
				WinningCombo.SELF_DIAGONAL_2_1,
				WinningCombo.SELF_DIAGONAL_3_2,
				WinningCombo.SELF_OPPOSITE_0_5,
				WinningCombo.SELF_OPPOSITE_1_4,
				WinningCombo.SELF_OPPOSITE_2_3
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_0_2,
						WinningCombo.OPPONENT_HORIZONTAL_1_2,
						WinningCombo.OPPONENT_HORIZONTAL_2_2,
						WinningCombo.OPPONENT_HORIZONTAL_3_2,
						WinningCombo.OPPONENT_VERTICAL_3_0,
						WinningCombo.OPPONENT_VERTICAL_3_1,
						WinningCombo.OPPONENT_VERTICAL_3_2,
						WinningCombo.OPPONENT_DIAGONAL_1_0,
						WinningCombo.OPPONENT_DIAGONAL_2_1,
						WinningCombo.OPPONENT_DIAGONAL_3_2,
						WinningCombo.OPPONENT_OPPOSITE_0_5,
						WinningCombo.OPPONENT_OPPOSITE_1_4,
						WinningCombo.OPPONENT_OPPOSITE_2_3
				))),  
		OPPONENT_3_3(	false, 		false, true, true,   	false, true, true, 		3,3, "O33", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_0_3,
				WinningCombo.SELF_HORIZONTAL_1_3,
				WinningCombo.SELF_HORIZONTAL_2_3,
				WinningCombo.SELF_HORIZONTAL_3_3,
				WinningCombo.SELF_VERTICAL_3_0,
				WinningCombo.SELF_VERTICAL_3_1,
				WinningCombo.SELF_VERTICAL_3_2,
				WinningCombo.SELF_DIAGONAL_0_0,
				WinningCombo.SELF_DIAGONAL_1_1,
				WinningCombo.SELF_DIAGONAL_2_2,
				WinningCombo.SELF_OPPOSITE_1_5,
				WinningCombo.SELF_OPPOSITE_2_4,
				WinningCombo.SELF_OPPOSITE_3_3
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_0_3,
						WinningCombo.OPPONENT_HORIZONTAL_1_3,
						WinningCombo.OPPONENT_HORIZONTAL_2_3,
						WinningCombo.OPPONENT_HORIZONTAL_3_3,
						WinningCombo.OPPONENT_VERTICAL_3_0,
						WinningCombo.OPPONENT_VERTICAL_3_1,
						WinningCombo.OPPONENT_VERTICAL_3_2,
						WinningCombo.OPPONENT_DIAGONAL_0_0,
						WinningCombo.OPPONENT_DIAGONAL_1_1,
						WinningCombo.OPPONENT_DIAGONAL_2_2,
						WinningCombo.OPPONENT_OPPOSITE_1_5,
						WinningCombo.OPPONENT_OPPOSITE_2_4,
						WinningCombo.OPPONENT_OPPOSITE_3_3
					))),  
		OPPONENT_3_4(	false, 		false, true, true,   	true, false, false, 	3,4, "O34", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_0_4,
				WinningCombo.SELF_HORIZONTAL_1_4,
				WinningCombo.SELF_HORIZONTAL_2_4,
				WinningCombo.SELF_HORIZONTAL_3_4,
				WinningCombo.SELF_VERTICAL_3_1,
				WinningCombo.SELF_VERTICAL_3_2,
				WinningCombo.SELF_DIAGONAL_0_1,
				WinningCombo.SELF_DIAGONAL_1_2,
				WinningCombo.SELF_OPPOSITE_2_5,
				WinningCombo.SELF_OPPOSITE_3_4
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_0_4,
						WinningCombo.OPPONENT_HORIZONTAL_1_4,
						WinningCombo.OPPONENT_HORIZONTAL_2_4,
						WinningCombo.OPPONENT_HORIZONTAL_3_4,
						WinningCombo.OPPONENT_VERTICAL_3_1,
						WinningCombo.OPPONENT_VERTICAL_3_2,
						WinningCombo.OPPONENT_DIAGONAL_0_1,
						WinningCombo.OPPONENT_DIAGONAL_1_2,
						WinningCombo.OPPONENT_OPPOSITE_2_5,
						WinningCombo.OPPONENT_OPPOSITE_3_4
				))),  
		OPPONENT_3_5(	false, 		false, true, true,   	true, false, true,	 	3,5, "O35", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_0_5,
				WinningCombo.SELF_HORIZONTAL_1_5,
				WinningCombo.SELF_HORIZONTAL_2_5,
				WinningCombo.SELF_HORIZONTAL_3_5,
				WinningCombo.SELF_VERTICAL_3_2,
				WinningCombo.SELF_DIAGONAL_0_2,
				WinningCombo.SELF_OPPOSITE_3_5
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_0_5,
						WinningCombo.OPPONENT_HORIZONTAL_1_5,
						WinningCombo.OPPONENT_HORIZONTAL_2_5,
						WinningCombo.OPPONENT_HORIZONTAL_3_5,
						WinningCombo.OPPONENT_VERTICAL_3_2,
						WinningCombo.OPPONENT_DIAGONAL_0_2,
						WinningCombo.OPPONENT_OPPOSITE_3_5
				))),  

		OPPONENT_4_0(	false, 		true, false, false,   	false, false, false, 	4,0, "O40", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_1_0,
				WinningCombo.SELF_HORIZONTAL_2_0,
				WinningCombo.SELF_HORIZONTAL_3_0,
				WinningCombo.SELF_VERTICAL_4_0,
				WinningCombo.SELF_OPPOSITE_1_3
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_1_0,
						WinningCombo.OPPONENT_HORIZONTAL_2_0,
						WinningCombo.OPPONENT_HORIZONTAL_3_0,
						WinningCombo.OPPONENT_VERTICAL_4_0,
						WinningCombo.OPPONENT_OPPOSITE_1_3
				))),   
		OPPONENT_4_1(	false, 		true, false, false,   	false, false, true, 	4,1, "O41", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_1_1,
				WinningCombo.SELF_HORIZONTAL_2_1,
				WinningCombo.SELF_HORIZONTAL_3_1,
				WinningCombo.SELF_VERTICAL_4_0,
				WinningCombo.SELF_VERTICAL_4_1,
				WinningCombo.SELF_DIAGONAL_3_0,
				WinningCombo.SELF_OPPOSITE_1_4,
				WinningCombo.SELF_OPPOSITE_2_3
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_1_1,
						WinningCombo.OPPONENT_HORIZONTAL_2_1,
						WinningCombo.OPPONENT_HORIZONTAL_3_1,
						WinningCombo.OPPONENT_VERTICAL_4_0,
						WinningCombo.OPPONENT_VERTICAL_4_1,
						WinningCombo.OPPONENT_DIAGONAL_3_0,
						WinningCombo.OPPONENT_OPPOSITE_1_4,
						WinningCombo.OPPONENT_OPPOSITE_2_3
				))),  
		OPPONENT_4_2(	false, 		true, false, false,   	false, true, false, 	4,2, "O42", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_1_2,
				WinningCombo.SELF_HORIZONTAL_2_2,
				WinningCombo.SELF_HORIZONTAL_3_2,
				WinningCombo.SELF_VERTICAL_4_0,
				WinningCombo.SELF_VERTICAL_4_1,
				WinningCombo.SELF_VERTICAL_4_2,
				WinningCombo.SELF_DIAGONAL_2_0,
				WinningCombo.SELF_DIAGONAL_3_1,
				WinningCombo.SELF_OPPOSITE_1_5,
				WinningCombo.SELF_OPPOSITE_2_4,
				WinningCombo.SELF_OPPOSITE_3_3
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_1_2,
						WinningCombo.OPPONENT_HORIZONTAL_2_2,
						WinningCombo.OPPONENT_HORIZONTAL_3_2,
						WinningCombo.OPPONENT_VERTICAL_4_0,
						WinningCombo.OPPONENT_VERTICAL_4_1,
						WinningCombo.OPPONENT_VERTICAL_4_2,
						WinningCombo.OPPONENT_DIAGONAL_2_0,
						WinningCombo.OPPONENT_DIAGONAL_3_1,
						WinningCombo.OPPONENT_OPPOSITE_1_5,
						WinningCombo.OPPONENT_OPPOSITE_2_4,
						WinningCombo.OPPONENT_OPPOSITE_3_3
				))),  
		OPPONENT_4_3(	false, 		true, false, false,   	false, true, true, 		4,3, "O43", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_1_3,
				WinningCombo.SELF_HORIZONTAL_2_3,
				WinningCombo.SELF_HORIZONTAL_3_3,
				WinningCombo.SELF_VERTICAL_4_0,
				WinningCombo.SELF_VERTICAL_4_1,
				WinningCombo.SELF_VERTICAL_4_2,
				WinningCombo.SELF_DIAGONAL_1_0,
				WinningCombo.SELF_DIAGONAL_2_1,
				WinningCombo.SELF_DIAGONAL_3_2,
				WinningCombo.SELF_OPPOSITE_2_5,
				WinningCombo.SELF_OPPOSITE_3_4
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_1_3,
						WinningCombo.OPPONENT_HORIZONTAL_2_3,
						WinningCombo.OPPONENT_HORIZONTAL_3_3,
						WinningCombo.OPPONENT_VERTICAL_4_0,
						WinningCombo.OPPONENT_VERTICAL_4_1,
						WinningCombo.OPPONENT_VERTICAL_4_2,
						WinningCombo.OPPONENT_DIAGONAL_1_0,
						WinningCombo.OPPONENT_DIAGONAL_2_1,
						WinningCombo.OPPONENT_DIAGONAL_3_2,
						WinningCombo.OPPONENT_OPPOSITE_2_5,
						WinningCombo.OPPONENT_OPPOSITE_3_4
				))),  
		OPPONENT_4_4(	false, 		true, false, false,   	true, false, false, 	4,4, "O44", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_1_4,
				WinningCombo.SELF_HORIZONTAL_2_4,
				WinningCombo.SELF_HORIZONTAL_3_4,
				WinningCombo.SELF_VERTICAL_4_1,
				WinningCombo.SELF_VERTICAL_4_2,
				WinningCombo.SELF_DIAGONAL_1_1,
				WinningCombo.SELF_DIAGONAL_2_2,
				WinningCombo.SELF_OPPOSITE_3_5
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_1_4,
						WinningCombo.OPPONENT_HORIZONTAL_2_4,
						WinningCombo.OPPONENT_HORIZONTAL_3_4,
						WinningCombo.OPPONENT_VERTICAL_4_1,
						WinningCombo.OPPONENT_VERTICAL_4_2,
						WinningCombo.OPPONENT_DIAGONAL_1_1,
						WinningCombo.OPPONENT_DIAGONAL_2_2,
						WinningCombo.OPPONENT_OPPOSITE_3_5
				))),  
		OPPONENT_4_5(	false, 		true, false, false,   	true, false, true,	 	4,5, "O45", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_1_5,
				WinningCombo.SELF_HORIZONTAL_2_5,
				WinningCombo.SELF_HORIZONTAL_3_5,
				WinningCombo.SELF_VERTICAL_4_2,
				WinningCombo.SELF_DIAGONAL_1_2
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_1_5,
						WinningCombo.OPPONENT_HORIZONTAL_2_5,
						WinningCombo.OPPONENT_HORIZONTAL_3_5,
						WinningCombo.OPPONENT_VERTICAL_4_2,
						WinningCombo.OPPONENT_DIAGONAL_1_2
				))),  

		OPPONENT_5_0(	false, 		true, false, true,   	false, false, false, 	5,0, "O50", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_2_0,
				WinningCombo.SELF_HORIZONTAL_3_0,
				WinningCombo.SELF_VERTICAL_5_0,
				WinningCombo.SELF_OPPOSITE_2_3
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_2_0,
						WinningCombo.OPPONENT_HORIZONTAL_3_0,
						WinningCombo.OPPONENT_VERTICAL_5_0,
						WinningCombo.OPPONENT_OPPOSITE_2_3
				))),   
		OPPONENT_5_1(	false, 		true, false, true,   	false, false, true, 	5,1, "O51", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_2_1,
				WinningCombo.SELF_HORIZONTAL_3_1,
				WinningCombo.SELF_VERTICAL_5_0,
				WinningCombo.SELF_VERTICAL_5_1,
				WinningCombo.SELF_OPPOSITE_2_4,
				WinningCombo.SELF_OPPOSITE_3_3
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_2_1,
						WinningCombo.OPPONENT_HORIZONTAL_3_1,
						WinningCombo.OPPONENT_VERTICAL_5_0,
						WinningCombo.OPPONENT_VERTICAL_5_1,
						WinningCombo.OPPONENT_OPPOSITE_2_4,
						WinningCombo.OPPONENT_OPPOSITE_3_3
				))),  
		OPPONENT_5_2(	false, 		true, false, true,   	false, true, false, 	5,2, "O52", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_2_2,
				WinningCombo.SELF_HORIZONTAL_3_2,
				WinningCombo.SELF_VERTICAL_5_0,
				WinningCombo.SELF_VERTICAL_5_1,
				WinningCombo.SELF_VERTICAL_5_2,
				WinningCombo.SELF_DIAGONAL_3_0,
				WinningCombo.SELF_OPPOSITE_2_5,
				WinningCombo.SELF_OPPOSITE_3_4
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_2_2,
						WinningCombo.OPPONENT_HORIZONTAL_3_2,
						WinningCombo.OPPONENT_VERTICAL_5_0,
						WinningCombo.OPPONENT_VERTICAL_5_1,
						WinningCombo.OPPONENT_VERTICAL_5_2,
						WinningCombo.OPPONENT_DIAGONAL_3_0,
						WinningCombo.OPPONENT_OPPOSITE_2_5,
						WinningCombo.OPPONENT_OPPOSITE_3_4
				))),  
		OPPONENT_5_3(	false, 		true, false, true,   	false, true, true, 		5,3, "O53", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_2_3,
				WinningCombo.SELF_HORIZONTAL_3_3,
				WinningCombo.SELF_VERTICAL_5_0,
				WinningCombo.SELF_VERTICAL_5_1,
				WinningCombo.SELF_VERTICAL_5_2,
				WinningCombo.SELF_DIAGONAL_2_0,
				WinningCombo.SELF_DIAGONAL_3_1,
				WinningCombo.SELF_OPPOSITE_3_5
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_2_3,
						WinningCombo.OPPONENT_HORIZONTAL_3_3,
						WinningCombo.OPPONENT_VERTICAL_5_0,
						WinningCombo.OPPONENT_VERTICAL_5_1,
						WinningCombo.OPPONENT_VERTICAL_5_2,
						WinningCombo.OPPONENT_DIAGONAL_2_0,
						WinningCombo.OPPONENT_DIAGONAL_3_1,
						WinningCombo.OPPONENT_OPPOSITE_3_5
				))),  
		OPPONENT_5_4(	false, 		true, false, true,   	true, false, false, 	5,4, "O54", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_2_4,
				WinningCombo.SELF_HORIZONTAL_3_4,
				WinningCombo.SELF_VERTICAL_5_1,
				WinningCombo.SELF_VERTICAL_5_2,
				WinningCombo.SELF_DIAGONAL_2_1,
				WinningCombo.SELF_DIAGONAL_3_2
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_2_4,
						WinningCombo.OPPONENT_HORIZONTAL_3_4,
						WinningCombo.OPPONENT_VERTICAL_5_1,
						WinningCombo.OPPONENT_VERTICAL_5_2,
						WinningCombo.OPPONENT_DIAGONAL_2_1,
						WinningCombo.OPPONENT_DIAGONAL_3_2
				))),  
		OPPONENT_5_5(	false, 		true, false, true,   	true, false, true,	 	5,5, "O55", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_2_5,
				WinningCombo.SELF_HORIZONTAL_3_5,
				WinningCombo.SELF_VERTICAL_5_2,
				WinningCombo.SELF_DIAGONAL_2_2
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_2_5,
						WinningCombo.OPPONENT_HORIZONTAL_3_5,
						WinningCombo.OPPONENT_VERTICAL_5_2,
						WinningCombo.OPPONENT_DIAGONAL_2_2
				))),  

		OPPONENT_6_0(	false, 		true, true, false,   	false, false, false, 	6,0, "O60", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_3_0,
				WinningCombo.SELF_VERTICAL_6_0,
				WinningCombo.SELF_OPPOSITE_3_3
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_3_0,
						WinningCombo.OPPONENT_VERTICAL_6_0,
						WinningCombo.OPPONENT_OPPOSITE_3_3
				))),   
		OPPONENT_6_1(	false, 		true, true, false,   	false, false, true, 	6,1, "O61", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_3_1,
				WinningCombo.SELF_VERTICAL_6_0,
				WinningCombo.SELF_VERTICAL_6_1,
				WinningCombo.SELF_OPPOSITE_3_4
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_3_1,
						WinningCombo.OPPONENT_VERTICAL_6_0,
						WinningCombo.OPPONENT_VERTICAL_6_1,
						WinningCombo.OPPONENT_OPPOSITE_3_4
				))),  
		OPPONENT_6_2(	false, 		true, true, false,   	false, true, false, 	6,2, "O62", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_3_2,
				WinningCombo.SELF_VERTICAL_6_0,
				WinningCombo.SELF_VERTICAL_6_1,
				WinningCombo.SELF_VERTICAL_6_2,
				WinningCombo.SELF_OPPOSITE_3_5
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_3_2,
						WinningCombo.OPPONENT_VERTICAL_6_0,
						WinningCombo.OPPONENT_VERTICAL_6_1,
						WinningCombo.OPPONENT_VERTICAL_6_2,
						WinningCombo.OPPONENT_OPPOSITE_3_5
				))),  
		OPPONENT_6_3(	false, 		true, true, false,   	false, true, true, 		6,3, "O63", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_3_3, 
				WinningCombo.SELF_VERTICAL_6_0,
				WinningCombo.SELF_VERTICAL_6_1,
				WinningCombo.SELF_VERTICAL_6_2,
				WinningCombo.SELF_DIAGONAL_3_0
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_3_3, 
						WinningCombo.OPPONENT_VERTICAL_6_0,
						WinningCombo.OPPONENT_VERTICAL_6_1,
						WinningCombo.OPPONENT_VERTICAL_6_2,
						WinningCombo.OPPONENT_DIAGONAL_3_0
				))),  
		OPPONENT_6_4(	false, 		true, true, false,   	true, false, false, 	6,4, "O64", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_3_4,
				WinningCombo.SELF_VERTICAL_6_1,
				WinningCombo.SELF_VERTICAL_6_2,
				WinningCombo.SELF_DIAGONAL_3_1
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_3_4,
						WinningCombo.OPPONENT_VERTICAL_6_1,
						WinningCombo.OPPONENT_VERTICAL_6_2,
						WinningCombo.OPPONENT_DIAGONAL_3_1
				))),  
		OPPONENT_6_5(	false, 		true, true, false,   	true, false, true,	 	6,5, "O65", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.SELF_HORIZONTAL_3_5,
				WinningCombo.SELF_VERTICAL_6_2,
				WinningCombo.SELF_DIAGONAL_3_2
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.OPPONENT_HORIZONTAL_3_5,
						WinningCombo.OPPONENT_VERTICAL_6_2,
						WinningCombo.OPPONENT_DIAGONAL_3_2
				))),  				
				
				

				
				
				
				
				
				
				
				
				
				
		//				Who			COLUMN POS				ROW POS
		SELF_0_0(	true, 		false, false, false,   	false, false, false, 	0,0, "S00", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_0_0,
				WinningCombo.OPPONENT_VERTICAL_0_0,
				WinningCombo.OPPONENT_DIAGONAL_0_0
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_0_0,
						WinningCombo.SELF_VERTICAL_0_0,
						WinningCombo.SELF_DIAGONAL_0_0
				))), 
		SELF_0_1(	true, 		false, false, false,   	false, false, true, 	0,1, "S01", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_0_1,
				WinningCombo.OPPONENT_VERTICAL_0_0,
				WinningCombo.OPPONENT_VERTICAL_0_1,
				WinningCombo.OPPONENT_DIAGONAL_0_1
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_0_1,
						WinningCombo.SELF_VERTICAL_0_0,
						WinningCombo.SELF_VERTICAL_0_1,
						WinningCombo.SELF_DIAGONAL_0_1
				))),  
		SELF_0_2(	true, 		false, false, false,   	false, true, false, 	0,2, "S02", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_0_2,
				WinningCombo.OPPONENT_VERTICAL_0_0,
				WinningCombo.OPPONENT_VERTICAL_0_1,
				WinningCombo.OPPONENT_VERTICAL_0_2,
				WinningCombo.OPPONENT_DIAGONAL_0_2
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_0_2,
						WinningCombo.SELF_VERTICAL_0_0,
						WinningCombo.SELF_VERTICAL_0_1,
						WinningCombo.SELF_VERTICAL_0_2,
						WinningCombo.SELF_DIAGONAL_0_2
				))),  
		SELF_0_3(	true, 		false, false, false,   	false, true, true, 		0,3, "S03", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_0_3,
				WinningCombo.OPPONENT_VERTICAL_0_0,
				WinningCombo.OPPONENT_VERTICAL_0_1,
				WinningCombo.OPPONENT_VERTICAL_0_2,
				WinningCombo.OPPONENT_OPPOSITE_0_3
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_0_3,
						WinningCombo.SELF_VERTICAL_0_0,
						WinningCombo.SELF_VERTICAL_0_1,
						WinningCombo.SELF_VERTICAL_0_2,
						WinningCombo.SELF_OPPOSITE_0_3
				))),  
		SELF_0_4(	true, 		false, false, false,   	true, false, false, 	0,4, "S04", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_0_4,
				WinningCombo.OPPONENT_VERTICAL_0_1,
				WinningCombo.OPPONENT_VERTICAL_0_2,
				WinningCombo.OPPONENT_OPPOSITE_0_4
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_0_4,
						WinningCombo.SELF_VERTICAL_0_1,
						WinningCombo.SELF_VERTICAL_0_2,
						WinningCombo.SELF_OPPOSITE_0_4
				))),  
		SELF_0_5(	true, 		false, false, false,   	true, false, true, 		0,5, "S05", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_0_5,
				WinningCombo.OPPONENT_VERTICAL_0_2,
				WinningCombo.OPPONENT_OPPOSITE_0_5
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_0_5,
						WinningCombo.SELF_VERTICAL_0_2,
						WinningCombo.SELF_OPPOSITE_0_5
				))),  
 

		SELF_1_0(	true, 		false, false, true,   	false, false, false, 	1,0, "S10", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_0_0,
				WinningCombo.OPPONENT_HORIZONTAL_1_0,
				WinningCombo.OPPONENT_VERTICAL_1_0,
				WinningCombo.OPPONENT_DIAGONAL_1_0
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_0_0,
						WinningCombo.SELF_HORIZONTAL_1_0,
						WinningCombo.SELF_VERTICAL_1_0,
						WinningCombo.SELF_DIAGONAL_1_0
						))),   
		SELF_1_1(	true, 		false, false, true,   	false, false, true, 	1,1, "S11", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_0_1,
				WinningCombo.OPPONENT_HORIZONTAL_1_1,
				WinningCombo.OPPONENT_VERTICAL_1_0,
				WinningCombo.OPPONENT_VERTICAL_1_1,
				WinningCombo.OPPONENT_DIAGONAL_0_0,
				WinningCombo.OPPONENT_DIAGONAL_1_1
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_0_1,
						WinningCombo.SELF_HORIZONTAL_1_1,
						WinningCombo.SELF_VERTICAL_1_0,
						WinningCombo.SELF_VERTICAL_1_1,
						WinningCombo.SELF_DIAGONAL_0_0,
						WinningCombo.SELF_DIAGONAL_1_1
				))),  
		SELF_1_2(	true, 		false, false, true,   	false, true, false, 	1,2, "S12", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_0_2,
				WinningCombo.OPPONENT_HORIZONTAL_1_2,
				WinningCombo.OPPONENT_VERTICAL_1_0,
				WinningCombo.OPPONENT_VERTICAL_1_1,
				WinningCombo.OPPONENT_VERTICAL_1_2,
				WinningCombo.OPPONENT_DIAGONAL_0_1,
				WinningCombo.OPPONENT_DIAGONAL_1_2,
				WinningCombo.OPPONENT_OPPOSITE_0_3
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_0_2,
						WinningCombo.SELF_HORIZONTAL_1_2,
						WinningCombo.SELF_VERTICAL_1_0,
						WinningCombo.SELF_VERTICAL_1_1,
						WinningCombo.SELF_VERTICAL_1_2,
						WinningCombo.SELF_DIAGONAL_0_1,
						WinningCombo.SELF_DIAGONAL_1_2,
						WinningCombo.SELF_OPPOSITE_0_3
				))),  
		SELF_1_3(	true, 		false, false, true,   	false, true, true, 		1,3, "S13", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_0_3,
				WinningCombo.OPPONENT_HORIZONTAL_1_3,
				WinningCombo.OPPONENT_VERTICAL_1_0,
				WinningCombo.OPPONENT_VERTICAL_1_1,
				WinningCombo.OPPONENT_VERTICAL_1_2,
				WinningCombo.OPPONENT_DIAGONAL_0_2,
				WinningCombo.OPPONENT_OPPOSITE_0_4,
				WinningCombo.OPPONENT_OPPOSITE_1_3
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_0_3,
						WinningCombo.SELF_HORIZONTAL_1_3,
						WinningCombo.SELF_VERTICAL_1_0,
						WinningCombo.SELF_VERTICAL_1_1,
						WinningCombo.SELF_VERTICAL_1_2,
						WinningCombo.SELF_DIAGONAL_0_2,
						WinningCombo.SELF_OPPOSITE_0_4,
						WinningCombo.SELF_OPPOSITE_1_3
				))),  
		SELF_1_4(	true, 		false, false, true,   	true, false, false, 	1,4, "S14", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_0_4,
				WinningCombo.OPPONENT_HORIZONTAL_1_4,
				WinningCombo.OPPONENT_VERTICAL_1_1,
				WinningCombo.OPPONENT_VERTICAL_1_2,
				WinningCombo.OPPONENT_OPPOSITE_0_5,
				WinningCombo.OPPONENT_OPPOSITE_1_4
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_0_4,
						WinningCombo.SELF_HORIZONTAL_1_4,
						WinningCombo.SELF_VERTICAL_1_1,
						WinningCombo.SELF_VERTICAL_1_2,
						WinningCombo.SELF_OPPOSITE_0_5,
						WinningCombo.SELF_OPPOSITE_1_4
				))),  
		SELF_1_5(	true, 		false, false, true,   	true, false, true,	 	1,5, "S15", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_0_5,
				WinningCombo.OPPONENT_HORIZONTAL_1_5,
				WinningCombo.OPPONENT_VERTICAL_1_2,
				WinningCombo.OPPONENT_OPPOSITE_1_5
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_0_5,
						WinningCombo.SELF_HORIZONTAL_1_5,
						WinningCombo.SELF_VERTICAL_1_2,
						WinningCombo.SELF_OPPOSITE_1_5
				))),  
		SELF_2_0(	true, 		false, true, false,   	false, false, false, 	2,0, "S20", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_0_0,
				WinningCombo.OPPONENT_HORIZONTAL_1_0,
				WinningCombo.OPPONENT_HORIZONTAL_2_0,
				WinningCombo.OPPONENT_VERTICAL_2_0,
				WinningCombo.OPPONENT_DIAGONAL_2_0
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_0_0,
						WinningCombo.SELF_HORIZONTAL_1_0,
						WinningCombo.SELF_HORIZONTAL_2_0,
						WinningCombo.SELF_VERTICAL_2_0,
						WinningCombo.SELF_DIAGONAL_2_0
				))),   
		SELF_2_1(	true, 		false, true, false,   	false, false, true, 	2,1, "S21", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_0_1,
				WinningCombo.OPPONENT_HORIZONTAL_1_1,
				WinningCombo.OPPONENT_HORIZONTAL_2_1,
				WinningCombo.OPPONENT_VERTICAL_2_0,
				WinningCombo.OPPONENT_VERTICAL_2_1,
				WinningCombo.OPPONENT_DIAGONAL_1_0,
				WinningCombo.OPPONENT_DIAGONAL_2_1,
				WinningCombo.OPPONENT_OPPOSITE_0_3
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_0_1,
						WinningCombo.SELF_HORIZONTAL_1_1,
						WinningCombo.SELF_HORIZONTAL_2_1,
						WinningCombo.SELF_VERTICAL_2_0,
						WinningCombo.SELF_VERTICAL_2_1,
						WinningCombo.SELF_DIAGONAL_1_0,
						WinningCombo.SELF_DIAGONAL_2_1,
						WinningCombo.SELF_OPPOSITE_0_3
				))),  
		SELF_2_2(	true, 		false, true, false,   	false, true, false, 	2,2, "S22", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_0_2,
				WinningCombo.OPPONENT_HORIZONTAL_1_2,
				WinningCombo.OPPONENT_HORIZONTAL_2_2,
				WinningCombo.OPPONENT_VERTICAL_2_0,
				WinningCombo.OPPONENT_VERTICAL_2_1,
				WinningCombo.OPPONENT_VERTICAL_2_2,
				WinningCombo.OPPONENT_DIAGONAL_0_0,
				WinningCombo.OPPONENT_DIAGONAL_1_1,
				WinningCombo.OPPONENT_DIAGONAL_2_2,
				WinningCombo.OPPONENT_OPPOSITE_0_4,
				WinningCombo.OPPONENT_OPPOSITE_1_3
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_0_2,
						WinningCombo.SELF_HORIZONTAL_1_2,
						WinningCombo.SELF_HORIZONTAL_2_2,
						WinningCombo.SELF_VERTICAL_2_0,
						WinningCombo.SELF_VERTICAL_2_1,
						WinningCombo.SELF_VERTICAL_2_2,
						WinningCombo.SELF_DIAGONAL_0_0,
						WinningCombo.SELF_DIAGONAL_1_1,
						WinningCombo.SELF_DIAGONAL_2_2,
						WinningCombo.SELF_OPPOSITE_0_4,
						WinningCombo.SELF_OPPOSITE_1_3
				))),  
		SELF_2_3(	true, 		false, true, false,   	false, true, true, 		2,3, "S23", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_0_3,
				WinningCombo.OPPONENT_HORIZONTAL_1_3,
				WinningCombo.OPPONENT_HORIZONTAL_2_3,
				WinningCombo.OPPONENT_VERTICAL_2_0,
				WinningCombo.OPPONENT_VERTICAL_2_1,
				WinningCombo.OPPONENT_VERTICAL_2_2,
				WinningCombo.OPPONENT_DIAGONAL_0_1,
				WinningCombo.OPPONENT_DIAGONAL_1_2,
				WinningCombo.OPPONENT_OPPOSITE_0_5,
				WinningCombo.OPPONENT_OPPOSITE_1_4,
				WinningCombo.OPPONENT_OPPOSITE_2_3
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_0_3,
						WinningCombo.SELF_HORIZONTAL_1_3,
						WinningCombo.SELF_HORIZONTAL_2_3,
						WinningCombo.SELF_VERTICAL_2_0,
						WinningCombo.SELF_VERTICAL_2_1,
						WinningCombo.SELF_VERTICAL_2_2,
						WinningCombo.SELF_DIAGONAL_0_1,
						WinningCombo.SELF_DIAGONAL_1_2,
						WinningCombo.SELF_OPPOSITE_0_5,
						WinningCombo.SELF_OPPOSITE_1_4,
						WinningCombo.SELF_OPPOSITE_2_3
				))),  
		SELF_2_4(	true, 		false, true, false,   	true, false, false, 	2,4, "S24", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_0_4,
				WinningCombo.OPPONENT_HORIZONTAL_1_4,
				WinningCombo.OPPONENT_HORIZONTAL_2_4,
				WinningCombo.OPPONENT_VERTICAL_2_1,
				WinningCombo.OPPONENT_VERTICAL_2_2,
				WinningCombo.OPPONENT_DIAGONAL_0_2,
				WinningCombo.OPPONENT_OPPOSITE_1_5,
				WinningCombo.OPPONENT_OPPOSITE_2_4
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_0_4,
						WinningCombo.SELF_HORIZONTAL_1_4,
						WinningCombo.SELF_HORIZONTAL_2_4,
						WinningCombo.SELF_VERTICAL_2_1,
						WinningCombo.SELF_VERTICAL_2_2,
						WinningCombo.SELF_DIAGONAL_0_2,
						WinningCombo.SELF_OPPOSITE_1_5,
						WinningCombo.SELF_OPPOSITE_2_4
				))),  
		SELF_2_5(	true, 		false, true, false,   	true, false, true,	 	2,5, "S25", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_0_5,
				WinningCombo.OPPONENT_HORIZONTAL_1_5,
				WinningCombo.OPPONENT_HORIZONTAL_2_5,
				WinningCombo.OPPONENT_VERTICAL_2_2,
				WinningCombo.OPPONENT_OPPOSITE_2_5
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_0_5,
						WinningCombo.SELF_HORIZONTAL_1_5,
						WinningCombo.SELF_HORIZONTAL_2_5,
						WinningCombo.SELF_VERTICAL_2_2,
						WinningCombo.SELF_OPPOSITE_2_5
				))),  


		SELF_3_0(	true, 		false, true, true,   	false, false, false, 	3,0, "S30", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_0_0,
				WinningCombo.OPPONENT_HORIZONTAL_1_0,
				WinningCombo.OPPONENT_HORIZONTAL_2_0,
				WinningCombo.OPPONENT_HORIZONTAL_3_0,
				WinningCombo.OPPONENT_VERTICAL_3_0,
				WinningCombo.OPPONENT_DIAGONAL_3_0,
				WinningCombo.OPPONENT_OPPOSITE_0_3
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_0_0,
						WinningCombo.SELF_HORIZONTAL_1_0,
						WinningCombo.SELF_HORIZONTAL_2_0,
						WinningCombo.SELF_HORIZONTAL_3_0,
						WinningCombo.SELF_VERTICAL_3_0,
						WinningCombo.SELF_DIAGONAL_3_0,
						WinningCombo.SELF_OPPOSITE_0_3
				))),   
		SELF_3_1(	true, 		false, true, true,   	false, false, true, 	3,1, "S31", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_0_1,
				WinningCombo.OPPONENT_HORIZONTAL_1_1,
				WinningCombo.OPPONENT_HORIZONTAL_2_1,
				WinningCombo.OPPONENT_HORIZONTAL_3_1,
				WinningCombo.OPPONENT_VERTICAL_3_0,
				WinningCombo.OPPONENT_VERTICAL_3_1,
				WinningCombo.OPPONENT_DIAGONAL_2_0,
				WinningCombo.OPPONENT_DIAGONAL_3_1,
				WinningCombo.OPPONENT_OPPOSITE_0_4,
				WinningCombo.OPPONENT_OPPOSITE_1_3
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_0_1,
						WinningCombo.SELF_HORIZONTAL_1_1,
						WinningCombo.SELF_HORIZONTAL_2_1,
						WinningCombo.SELF_HORIZONTAL_3_1,
						WinningCombo.SELF_VERTICAL_3_0,
						WinningCombo.SELF_VERTICAL_3_1,
						WinningCombo.SELF_DIAGONAL_2_0,
						WinningCombo.SELF_DIAGONAL_3_1,
						WinningCombo.SELF_OPPOSITE_0_4,
						WinningCombo.SELF_OPPOSITE_1_3
				))),  
		SELF_3_2(	true, 		false, true, true,   	false, true, false, 	3,2, "S32", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_0_2,
				WinningCombo.OPPONENT_HORIZONTAL_1_2,
				WinningCombo.OPPONENT_HORIZONTAL_2_2,
				WinningCombo.OPPONENT_HORIZONTAL_3_2,
				WinningCombo.OPPONENT_VERTICAL_3_0,
				WinningCombo.OPPONENT_VERTICAL_3_1,
				WinningCombo.OPPONENT_VERTICAL_3_2,
				WinningCombo.OPPONENT_DIAGONAL_1_0,
				WinningCombo.OPPONENT_DIAGONAL_2_1,
				WinningCombo.OPPONENT_DIAGONAL_3_2,
				WinningCombo.OPPONENT_OPPOSITE_0_5,
				WinningCombo.OPPONENT_OPPOSITE_1_4,
				WinningCombo.OPPONENT_OPPOSITE_2_3
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_0_2,
						WinningCombo.SELF_HORIZONTAL_1_2,
						WinningCombo.SELF_HORIZONTAL_2_2,
						WinningCombo.SELF_HORIZONTAL_3_2,
						WinningCombo.SELF_VERTICAL_3_0,
						WinningCombo.SELF_VERTICAL_3_1,
						WinningCombo.SELF_VERTICAL_3_2,
						WinningCombo.SELF_DIAGONAL_1_0,
						WinningCombo.SELF_DIAGONAL_2_1,
						WinningCombo.SELF_DIAGONAL_3_2,
						WinningCombo.SELF_OPPOSITE_0_5,
						WinningCombo.SELF_OPPOSITE_1_4,
						WinningCombo.SELF_OPPOSITE_2_3
				))),  
		SELF_3_3(	true, 		false, true, true,   	false, true, true, 		3,3, "S33", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_0_3,
				WinningCombo.OPPONENT_HORIZONTAL_1_3,
				WinningCombo.OPPONENT_HORIZONTAL_2_3,
				WinningCombo.OPPONENT_HORIZONTAL_3_3,
				WinningCombo.OPPONENT_VERTICAL_3_0,
				WinningCombo.OPPONENT_VERTICAL_3_1,
				WinningCombo.OPPONENT_VERTICAL_3_2,
				WinningCombo.OPPONENT_DIAGONAL_0_0,
				WinningCombo.OPPONENT_DIAGONAL_1_1,
				WinningCombo.OPPONENT_DIAGONAL_2_2,
				WinningCombo.OPPONENT_OPPOSITE_1_5,
				WinningCombo.OPPONENT_OPPOSITE_2_4,
				WinningCombo.OPPONENT_OPPOSITE_3_3
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_0_3,
						WinningCombo.SELF_HORIZONTAL_1_3,
						WinningCombo.SELF_HORIZONTAL_2_3,
						WinningCombo.SELF_HORIZONTAL_3_3,
						WinningCombo.SELF_VERTICAL_3_0,
						WinningCombo.SELF_VERTICAL_3_1,
						WinningCombo.SELF_VERTICAL_3_2,
						WinningCombo.SELF_DIAGONAL_0_0,
						WinningCombo.SELF_DIAGONAL_1_1,
						WinningCombo.SELF_DIAGONAL_2_2,
						WinningCombo.SELF_OPPOSITE_1_5,
						WinningCombo.SELF_OPPOSITE_2_4,
						WinningCombo.SELF_OPPOSITE_3_3
				))),  
		SELF_3_4(	true, 		false, true, true,   	true, false, false, 	3,4, "S34", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_0_4,
				WinningCombo.OPPONENT_HORIZONTAL_1_4,
				WinningCombo.OPPONENT_HORIZONTAL_2_4,
				WinningCombo.OPPONENT_HORIZONTAL_3_4,
				WinningCombo.OPPONENT_VERTICAL_3_1,
				WinningCombo.OPPONENT_VERTICAL_3_2,
				WinningCombo.OPPONENT_DIAGONAL_0_1,
				WinningCombo.OPPONENT_DIAGONAL_1_2,
				WinningCombo.OPPONENT_OPPOSITE_2_5,
				WinningCombo.OPPONENT_OPPOSITE_3_4
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_0_4,
						WinningCombo.SELF_HORIZONTAL_1_4,
						WinningCombo.SELF_HORIZONTAL_2_4,
						WinningCombo.SELF_HORIZONTAL_3_4,
						WinningCombo.SELF_VERTICAL_3_1,
						WinningCombo.SELF_VERTICAL_3_2,
						WinningCombo.SELF_DIAGONAL_0_1,
						WinningCombo.SELF_DIAGONAL_1_2,
						WinningCombo.SELF_OPPOSITE_2_5,
						WinningCombo.SELF_OPPOSITE_3_4
				))),  
		SELF_3_5(	true, 		false, true, true,   	true, false, true,	 	3,5, "S35", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_0_5,
				WinningCombo.OPPONENT_HORIZONTAL_1_5,
				WinningCombo.OPPONENT_HORIZONTAL_2_5,
				WinningCombo.OPPONENT_HORIZONTAL_3_5,
				WinningCombo.OPPONENT_VERTICAL_3_2,
				WinningCombo.OPPONENT_DIAGONAL_0_2,
				WinningCombo.OPPONENT_OPPOSITE_3_5
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_0_5,
						WinningCombo.SELF_HORIZONTAL_1_5,
						WinningCombo.SELF_HORIZONTAL_2_5,
						WinningCombo.SELF_HORIZONTAL_3_5,
						WinningCombo.SELF_VERTICAL_3_2,
						WinningCombo.SELF_DIAGONAL_0_2,
						WinningCombo.SELF_OPPOSITE_3_5
				))),  

		SELF_4_0(	true, 		true, false, false,   	false, false, false, 	4,0, "S40", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_1_0,
				WinningCombo.OPPONENT_HORIZONTAL_2_0,
				WinningCombo.OPPONENT_HORIZONTAL_3_0,
				WinningCombo.OPPONENT_VERTICAL_4_0,
				WinningCombo.OPPONENT_OPPOSITE_1_3
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_1_0,
						WinningCombo.SELF_HORIZONTAL_2_0,
						WinningCombo.SELF_HORIZONTAL_3_0,
						WinningCombo.SELF_VERTICAL_4_0,
						WinningCombo.SELF_OPPOSITE_1_3
				))),   
		SELF_4_1(	true, 		true, false, false,   	false, false, true, 	4,1, "S41", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_1_1,
				WinningCombo.OPPONENT_HORIZONTAL_2_1,
				WinningCombo.OPPONENT_HORIZONTAL_3_1,
				WinningCombo.OPPONENT_VERTICAL_4_0,
				WinningCombo.OPPONENT_VERTICAL_4_1,
				WinningCombo.OPPONENT_DIAGONAL_3_0,
				WinningCombo.OPPONENT_OPPOSITE_1_4,
				WinningCombo.OPPONENT_OPPOSITE_2_3
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_1_1,
						WinningCombo.SELF_HORIZONTAL_2_1,
						WinningCombo.SELF_HORIZONTAL_3_1,
						WinningCombo.SELF_VERTICAL_4_0,
						WinningCombo.SELF_VERTICAL_4_1,
						WinningCombo.SELF_DIAGONAL_3_0,
						WinningCombo.SELF_OPPOSITE_1_4,
						WinningCombo.SELF_OPPOSITE_2_3
				))),  
		SELF_4_2(	true, 		true, false, false,   	false, true, false, 	4,2, "S42", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_1_2,
				WinningCombo.OPPONENT_HORIZONTAL_2_2,
				WinningCombo.OPPONENT_HORIZONTAL_3_2,
				WinningCombo.OPPONENT_VERTICAL_4_0,
				WinningCombo.OPPONENT_VERTICAL_4_1,
				WinningCombo.OPPONENT_VERTICAL_4_2,
				WinningCombo.OPPONENT_DIAGONAL_2_0,
				WinningCombo.OPPONENT_DIAGONAL_3_1,
				WinningCombo.OPPONENT_OPPOSITE_1_5,
				WinningCombo.OPPONENT_OPPOSITE_2_4,
				WinningCombo.OPPONENT_OPPOSITE_3_3
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_1_2,
						WinningCombo.SELF_HORIZONTAL_2_2,
						WinningCombo.SELF_HORIZONTAL_3_2,
						WinningCombo.SELF_VERTICAL_4_0,
						WinningCombo.SELF_VERTICAL_4_1,
						WinningCombo.SELF_VERTICAL_4_2,
						WinningCombo.SELF_DIAGONAL_2_0,
						WinningCombo.SELF_DIAGONAL_3_1,
						WinningCombo.SELF_OPPOSITE_1_5,
						WinningCombo.SELF_OPPOSITE_2_4,
						WinningCombo.SELF_OPPOSITE_3_3
				))),  
		SELF_4_3(	true, 		true, false, false,   	false, true, true, 		4,3, "S43", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_1_3,
				WinningCombo.OPPONENT_HORIZONTAL_2_3,
				WinningCombo.OPPONENT_HORIZONTAL_3_3,
				WinningCombo.OPPONENT_VERTICAL_4_0,
				WinningCombo.OPPONENT_VERTICAL_4_1,
				WinningCombo.OPPONENT_VERTICAL_4_2,
				WinningCombo.OPPONENT_DIAGONAL_1_0,
				WinningCombo.OPPONENT_DIAGONAL_2_1,
				WinningCombo.OPPONENT_DIAGONAL_3_2,
				WinningCombo.OPPONENT_OPPOSITE_2_5,
				WinningCombo.OPPONENT_OPPOSITE_3_4
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_1_3,
						WinningCombo.SELF_HORIZONTAL_2_3,
						WinningCombo.SELF_HORIZONTAL_3_3,
						WinningCombo.SELF_VERTICAL_4_0,
						WinningCombo.SELF_VERTICAL_4_1,
						WinningCombo.SELF_VERTICAL_4_2,
						WinningCombo.SELF_DIAGONAL_1_0,
						WinningCombo.SELF_DIAGONAL_2_1,
						WinningCombo.SELF_DIAGONAL_3_2,
						WinningCombo.SELF_OPPOSITE_2_5,
						WinningCombo.SELF_OPPOSITE_3_4
				))),  
		SELF_4_4(	true, 		true, false, false,   	true, false, false, 	4,4, "S44", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_1_4,
				WinningCombo.OPPONENT_HORIZONTAL_2_4,
				WinningCombo.OPPONENT_HORIZONTAL_3_4,
				WinningCombo.OPPONENT_VERTICAL_4_1,
				WinningCombo.OPPONENT_VERTICAL_4_2,
				WinningCombo.OPPONENT_DIAGONAL_1_1,
				WinningCombo.OPPONENT_DIAGONAL_2_2,
				WinningCombo.OPPONENT_OPPOSITE_3_5
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_1_4,
						WinningCombo.SELF_HORIZONTAL_2_4,
						WinningCombo.SELF_HORIZONTAL_3_4,
						WinningCombo.SELF_VERTICAL_4_1,
						WinningCombo.SELF_VERTICAL_4_2,
						WinningCombo.SELF_DIAGONAL_1_1,
						WinningCombo.SELF_DIAGONAL_2_2,
						WinningCombo.SELF_OPPOSITE_3_5
				))),  
		SELF_4_5(	true, 		true, false, false,   	true, false, true,	 	4,5, "S45", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_1_5,
				WinningCombo.OPPONENT_HORIZONTAL_2_5,
				WinningCombo.OPPONENT_HORIZONTAL_3_5,
				WinningCombo.OPPONENT_VERTICAL_4_2,
				WinningCombo.OPPONENT_DIAGONAL_1_2
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_1_5,
						WinningCombo.SELF_HORIZONTAL_2_5,
						WinningCombo.SELF_HORIZONTAL_3_5,
						WinningCombo.SELF_VERTICAL_4_2,
						WinningCombo.SELF_DIAGONAL_1_2
				))),  

		SELF_5_0(	true, 		true, false, true,   	false, false, false, 	5,0, "S50", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_2_0,
				WinningCombo.OPPONENT_HORIZONTAL_3_0,
				WinningCombo.OPPONENT_VERTICAL_5_0,
				WinningCombo.OPPONENT_OPPOSITE_2_3
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_2_0,
						WinningCombo.SELF_HORIZONTAL_3_0,
						WinningCombo.SELF_VERTICAL_5_0,
						WinningCombo.SELF_OPPOSITE_2_3
				))),   
		SELF_5_1(	true, 		true, false, true,   	false, false, true, 	5,1, "S51", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_2_1,
				WinningCombo.OPPONENT_HORIZONTAL_3_1,
				WinningCombo.OPPONENT_VERTICAL_5_0,
				WinningCombo.OPPONENT_VERTICAL_5_1,
				WinningCombo.OPPONENT_OPPOSITE_2_4,
				WinningCombo.OPPONENT_OPPOSITE_3_3
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_2_1,
						WinningCombo.SELF_HORIZONTAL_3_1,
						WinningCombo.SELF_VERTICAL_5_0,
						WinningCombo.SELF_VERTICAL_5_1,
						WinningCombo.SELF_OPPOSITE_2_4,
						WinningCombo.SELF_OPPOSITE_3_3
				))),  
		SELF_5_2(	true, 		true, false, true,   	false, true, false, 	5,2, "S52", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_2_2,
				WinningCombo.OPPONENT_HORIZONTAL_3_2,
				WinningCombo.OPPONENT_VERTICAL_5_0,
				WinningCombo.OPPONENT_VERTICAL_5_1,
				WinningCombo.OPPONENT_VERTICAL_5_2,
				WinningCombo.OPPONENT_DIAGONAL_3_0,
				WinningCombo.OPPONENT_OPPOSITE_2_5,
				WinningCombo.OPPONENT_OPPOSITE_3_4
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_2_2,
						WinningCombo.SELF_HORIZONTAL_3_2,
						WinningCombo.SELF_VERTICAL_5_0,
						WinningCombo.SELF_VERTICAL_5_1,
						WinningCombo.SELF_VERTICAL_5_2,
						WinningCombo.SELF_DIAGONAL_3_0,
						WinningCombo.SELF_OPPOSITE_2_5,
						WinningCombo.SELF_OPPOSITE_3_4
				))),  
		SELF_5_3(	true, 		true, false, true,   	false, true, true, 		5,3, "S53", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_2_3,
				WinningCombo.OPPONENT_HORIZONTAL_3_3,
				WinningCombo.OPPONENT_VERTICAL_5_0,
				WinningCombo.OPPONENT_VERTICAL_5_1,
				WinningCombo.OPPONENT_VERTICAL_5_2,
				WinningCombo.OPPONENT_DIAGONAL_2_0,
				WinningCombo.OPPONENT_DIAGONAL_3_1,
				WinningCombo.OPPONENT_OPPOSITE_3_5
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_2_3,
						WinningCombo.SELF_HORIZONTAL_3_3,
						WinningCombo.SELF_VERTICAL_5_0,
						WinningCombo.SELF_VERTICAL_5_1,
						WinningCombo.SELF_VERTICAL_5_2,
						WinningCombo.SELF_DIAGONAL_2_0,
						WinningCombo.SELF_DIAGONAL_3_1,
						WinningCombo.SELF_OPPOSITE_3_5
				))),  
		SELF_5_4(	true, 		true, false, true,   	true, false, false, 	5,4, "S54", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_2_4,
				WinningCombo.OPPONENT_HORIZONTAL_3_4,
				WinningCombo.OPPONENT_VERTICAL_5_1,
				WinningCombo.OPPONENT_VERTICAL_5_2,
				WinningCombo.OPPONENT_DIAGONAL_2_1,
				WinningCombo.OPPONENT_DIAGONAL_3_2
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_2_4,
						WinningCombo.SELF_HORIZONTAL_3_4,
						WinningCombo.SELF_VERTICAL_5_1,
						WinningCombo.SELF_VERTICAL_5_2,
						WinningCombo.SELF_DIAGONAL_2_1,
						WinningCombo.SELF_DIAGONAL_3_2
				))),  
		SELF_5_5(	true, 		true, false, true,   	true, false, true,	 	5,5, "S55", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_2_5,
				WinningCombo.OPPONENT_HORIZONTAL_3_5,
				WinningCombo.OPPONENT_VERTICAL_5_2,
				WinningCombo.OPPONENT_DIAGONAL_2_2
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_2_5,
						WinningCombo.SELF_HORIZONTAL_3_5,
						WinningCombo.SELF_VERTICAL_5_2,
						WinningCombo.SELF_DIAGONAL_2_2
				))),  

		SELF_6_0(	true, 		true, true, false,   	false, false, false, 	6,0, "S60", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_3_0,
				WinningCombo.OPPONENT_VERTICAL_6_0,
				WinningCombo.OPPONENT_OPPOSITE_3_3
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_3_0,
						WinningCombo.SELF_VERTICAL_6_0,
						WinningCombo.SELF_OPPOSITE_3_3
				))),   
		SELF_6_1(	true, 		true, true, false,   	false, false, true, 	6,1, "S61", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_3_1,
				WinningCombo.OPPONENT_VERTICAL_6_0,
				WinningCombo.OPPONENT_VERTICAL_6_1,
				WinningCombo.OPPONENT_OPPOSITE_3_4
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_3_1,
						WinningCombo.SELF_VERTICAL_6_0,
						WinningCombo.SELF_VERTICAL_6_1,
						WinningCombo.SELF_OPPOSITE_3_4
				))),  
		SELF_6_2(	true, 		true, true, false,   	false, true, false, 	6,2, "S62", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_3_2,
				WinningCombo.OPPONENT_VERTICAL_6_0,
				WinningCombo.OPPONENT_VERTICAL_6_1,
				WinningCombo.OPPONENT_VERTICAL_6_2,
				WinningCombo.OPPONENT_OPPOSITE_3_5
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_3_2,
						WinningCombo.SELF_VERTICAL_6_0,
						WinningCombo.SELF_VERTICAL_6_1,
						WinningCombo.SELF_VERTICAL_6_2,
						WinningCombo.SELF_OPPOSITE_3_5
				))),  
		SELF_6_3(	true, 		true, true, false,   	false, true, true, 		6,3, "S63", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_3_3,
				WinningCombo.OPPONENT_VERTICAL_6_0,
				WinningCombo.OPPONENT_VERTICAL_6_1,
				WinningCombo.OPPONENT_VERTICAL_6_2,
				WinningCombo.OPPONENT_DIAGONAL_3_0
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_3_3,
						WinningCombo.SELF_VERTICAL_6_0,
						WinningCombo.SELF_VERTICAL_6_1,
						WinningCombo.SELF_VERTICAL_6_2,
						WinningCombo.SELF_DIAGONAL_3_0
				))),  
		SELF_6_4(	true, 		true, true, false,   	true, false, false, 	6,4, "S64", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_3_4,
				WinningCombo.OPPONENT_VERTICAL_6_1,
				WinningCombo.OPPONENT_VERTICAL_6_2,
				WinningCombo.OPPONENT_DIAGONAL_3_1
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_3_4,
						WinningCombo.SELF_VERTICAL_6_1,
						WinningCombo.SELF_VERTICAL_6_2,
						WinningCombo.SELF_DIAGONAL_3_1
				))),  
		SELF_6_5(	true, 		true, true, false,   	true, false, true,	 	6,5, "S65", new ArrayList <WinningCombo>(Arrays.asList(
				WinningCombo.OPPONENT_HORIZONTAL_3_5,
				WinningCombo.OPPONENT_VERTICAL_6_2,
				WinningCombo.OPPONENT_DIAGONAL_3_2
				)), new ArrayList <WinningCombo>(Arrays.asList(
						WinningCombo.SELF_HORIZONTAL_3_5,
						WinningCombo.SELF_VERTICAL_6_2,
						WinningCombo.SELF_DIAGONAL_3_2
				)));  				
					
			
		
		private BitSet mMovePosition = new BitSet(BoardState.WINNING_COMBO_SIZE_IN_BITS);
		private String mMovePositionString = new String();
		private int mColumn = 0;
		private int mRow = 0;
		private ArrayList <WinningCombo> mAffectedWinningCombos = null;
		private ArrayList <WinningCombo> mWinningCombosToCheckForWin = null;

		MovePosition (boolean pFirst, 
					boolean pSecond, boolean pThird, boolean pFourth, 
					boolean pFifth, boolean pSixth, boolean pSeventh, 
					int pColumn, int pRow,
					String pMovePositionString, 
					ArrayList<WinningCombo> pAffectedWinningCombos,
					ArrayList<WinningCombo> pWinningCombosToCheckForWin) {
			mMovePosition.set(0, pFirst);
			mMovePosition.set(1, pSecond);
			mMovePosition.set(2, pThird);
			mMovePosition.set(3, pFourth);
			mMovePosition.set(4, pFifth);
			mMovePosition.set(5, pSixth);
			mMovePosition.set(6, pSeventh);
			mColumn = pColumn;
			mRow = pRow;
			mMovePositionString = pMovePositionString;
			mAffectedWinningCombos = pAffectedWinningCombos;
			mWinningCombosToCheckForWin = pWinningCombosToCheckForWin;
		}
		
		public boolean isSelfMove () {
			return mMovePosition.get(0);
		}
		
		public CellState getCellState () {
			if (mMovePosition.get(0)) 
				return CellState.SELF_OCCUPIED;
			else 
				return CellState.OPPONENT_OCCUPIED;
		}

		public boolean isOpponentMove () {
			return !mMovePosition.get(0);
		}
		
		public int getColumn () {
			return mColumn;
		}

		public int getRow () {
			return mRow;
		}
		
		public static MovePosition getMovePosition (CellState lCellState, int column, int row) {
			switch (lCellState) {
			case OPPONENT_OCCUPIED:
				switch (column) {
				case 0:
					switch (row) {
					case 0:
						return MovePosition.OPPONENT_0_0;
					case 1:
						return MovePosition.OPPONENT_0_1;
					case 2:
						return MovePosition.OPPONENT_0_2;
					case 3:
						return MovePosition.OPPONENT_0_3;
					case 4:
						return MovePosition.OPPONENT_0_4;
					case 5:
						return MovePosition.OPPONENT_0_5;
					}
				break;
				case 1:
					switch (row) {
					case 0:
						return MovePosition.OPPONENT_1_0;
					case 1:
						return MovePosition.OPPONENT_1_1;
					case 2:
						return MovePosition.OPPONENT_1_2;
					case 3:
						return MovePosition.OPPONENT_1_3;
					case 4:
						return MovePosition.OPPONENT_1_4;
					case 5:
						return MovePosition.OPPONENT_1_5;
					}
				break;
				case 2:
					switch (row) {
					case 0:
						return MovePosition.OPPONENT_2_0;
					case 1:
						return MovePosition.OPPONENT_2_1;
					case 2:
						return MovePosition.OPPONENT_2_2;
					case 3:
						return MovePosition.OPPONENT_2_3;
					case 4:
						return MovePosition.OPPONENT_2_4;
					case 5:
						return MovePosition.OPPONENT_2_5;
					}
				break;
				case 3:
					switch (row) {
					case 0:
						return MovePosition.OPPONENT_3_0;
					case 1:
						return MovePosition.OPPONENT_3_1;
					case 2:
						return MovePosition.OPPONENT_3_2;
					case 3:
						return MovePosition.OPPONENT_3_3;
					case 4:
						return MovePosition.OPPONENT_3_4;
					case 5:
						return MovePosition.OPPONENT_3_5;
					}
				break;
				case 4:
					switch (row) {
					case 0:
						return MovePosition.OPPONENT_4_0;
					case 1:
						return MovePosition.OPPONENT_4_1;
					case 2:
						return MovePosition.OPPONENT_4_2;
					case 3:
						return MovePosition.OPPONENT_4_3;
					case 4:
						return MovePosition.OPPONENT_4_4;
					case 5:
						return MovePosition.OPPONENT_4_5;
					}
				break;
				case 5:
					switch (row) {
					case 0:
						return MovePosition.OPPONENT_5_0;
					case 1:
						return MovePosition.OPPONENT_5_1;
					case 2:
						return MovePosition.OPPONENT_5_2;
					case 3:
						return MovePosition.OPPONENT_5_3;
					case 4:
						return MovePosition.OPPONENT_5_4;
					case 5:
						return MovePosition.OPPONENT_5_5;
					}
				break;
				case 6:
					switch (row) {
					case 0:
						return MovePosition.OPPONENT_6_0;
					case 1:
						return MovePosition.OPPONENT_6_1;
					case 2:
						return MovePosition.OPPONENT_6_2;
					case 3:
						return MovePosition.OPPONENT_6_3;
					case 4:
						return MovePosition.OPPONENT_6_4;
					case 5:
						return MovePosition.OPPONENT_6_5;
					}
				break;
				}
			
				break;
			case SELF_OCCUPIED:
				switch (column) {
				case 0:
					switch (row) {
					case 0:
						return MovePosition.SELF_0_0;
					case 1:
						return MovePosition.SELF_0_1;
					case 2:
						return MovePosition.SELF_0_2;
					case 3:
						return MovePosition.SELF_0_3;
					case 4:
						return MovePosition.SELF_0_4;
					case 5:
						return MovePosition.SELF_0_5;
					}
				break;
				case 1:
					switch (row) {
					case 0:
						return MovePosition.SELF_1_0;
					case 1:
						return MovePosition.SELF_1_1;
					case 2:
						return MovePosition.SELF_1_2;
					case 3:
						return MovePosition.SELF_1_3;
					case 4:
						return MovePosition.SELF_1_4;
					case 5:
						return MovePosition.SELF_1_5;
					}
				break;
				case 2:
					switch (row) {
					case 0:
						return MovePosition.SELF_2_0;
					case 1:
						return MovePosition.SELF_2_1;
					case 2:
						return MovePosition.SELF_2_2;
					case 3:
						return MovePosition.SELF_2_3;
					case 4:
						return MovePosition.SELF_2_4;
					case 5:
						return MovePosition.SELF_2_5;
					}
				break;
				case 3:
					switch (row) {
					case 0:
						return MovePosition.SELF_3_0;
					case 1:
						return MovePosition.SELF_3_1;
					case 2:
						return MovePosition.SELF_3_2;
					case 3:
						return MovePosition.SELF_3_3;
					case 4:
						return MovePosition.SELF_3_4;
					case 5:
						return MovePosition.SELF_3_5;
					}
				break;
				case 4:
					switch (row) {
					case 0:
						return MovePosition.SELF_4_0;
					case 1:
						return MovePosition.SELF_4_1;
					case 2:
						return MovePosition.SELF_4_2;
					case 3:
						return MovePosition.SELF_4_3;
					case 4:
						return MovePosition.SELF_4_4;
					case 5:
						return MovePosition.SELF_4_5;
					}
				break;
				case 5:
					switch (row) {
					case 0:
						return MovePosition.SELF_5_0;
					case 1:
						return MovePosition.SELF_5_1;
					case 2:
						return MovePosition.SELF_5_2;
					case 3:
						return MovePosition.SELF_5_3;
					case 4:
						return MovePosition.SELF_5_4;
					case 5:
						return MovePosition.SELF_5_5;
					}
				break;
				case 6:
					switch (row) {
					case 0:
						return MovePosition.SELF_6_0;
					case 1:
						return MovePosition.SELF_6_1;
					case 2:
						return MovePosition.SELF_6_2;
					case 3:
						return MovePosition.SELF_6_3;
					case 4:
						return MovePosition.SELF_6_4;
					case 5:
						return MovePosition.SELF_6_5;
					}
				break;
				}
				break;
			}
			return null;
		}

		public BitSet getMovePositionBitSet() {
			return mMovePosition;
		}

		public String getMovePositionString() {
			return mMovePositionString;
		}

		public ArrayList <WinningCombo> getAffectedWinningCombos() {
			return mAffectedWinningCombos;
		}

		public ArrayList <WinningCombo> getWinningCombosToCheckForWin() {
			return mWinningCombosToCheckForWin;
		}

	
	
	
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	private EvaluationThreadInfo mEvaluationThreadInfo = null;
	
	public void setEvaluationThreadInfoBit (byte pPos, boolean pValue, String pLogContext) throws ConfigurationException, KnowledgeBaseException, InvalidMoveException {
		if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
			sLogger.trace("Entering");
		}
		
		byte lEvaluationThreadInfo = mEvaluationThreadInfo.getEvaluationThreadInfo();
		
		byte lMask = (byte) ( 1 << pPos );
		
		byte lValue = 0;
		
		if (pValue) {
			lValue = 1;
		} 
		
		byte lNewValue = (byte) ((lEvaluationThreadInfo & ~lMask) | ((lValue << pPos) & lMask));
	
		mEvaluationThreadInfo.setEvaluationThreadInfo(lNewValue);

		sLogger.debug("Creating and starting Knowledge Base File Access Thread for writing");
		KnowledgeBaseFileAccessWriteEvaluatedTask lKnowledgeBaseFileAccessWriteEvaluatedTask = new KnowledgeBaseFileAccessWriteEvaluatedTask(mKnowledgeBaseFilePool, mStateStringForKB, mFileIndexString, lNewValue, pLogContext);
		lKnowledgeBaseFileAccessWriteEvaluatedTask.executeTask();
		if (!lKnowledgeBaseFileAccessWriteEvaluatedTask.isTransactionSuccessful() || !lKnowledgeBaseFileAccessWriteEvaluatedTask.isTransactionFinished()) {
			sLogger.error("Knowledge Base Error occurred!");
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			throw new KnowledgeBaseException();
		}

		if (!mFileIndexString.equals(mReciprocalFileIndexString)) {
			lKnowledgeBaseFileAccessWriteEvaluatedTask = new KnowledgeBaseFileAccessWriteEvaluatedTask(mKnowledgeBaseFilePool, mReciprocalStateStringForKB, mReciprocalFileIndexString, lNewValue, pLogContext);
			lKnowledgeBaseFileAccessWriteEvaluatedTask.executeTask();
			if (!lKnowledgeBaseFileAccessWriteEvaluatedTask.isTransactionSuccessful() || !lKnowledgeBaseFileAccessWriteEvaluatedTask.isTransactionFinished()) {
				sLogger.error("Knowledge Base Error occurred!");
				if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
					sLogger.trace("Exiting");
				}
				throw new KnowledgeBaseException();
			}
		}
		
		if (sLogger.isDebugEnabled()) {
			sLogger.debug("Move: " + mFileIndexString + " Knowledge Base Move Score: " + mMoveScore.getMoveScore());
		}
		
		
		
		
		
		if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
			sLogger.trace("Exiting");
		}
	}
	

	public boolean getEvaluationThreadInfoBit (byte pPos, String pLogContext) throws ConfigurationException, KnowledgeBaseException, InvalidMoveException {
		if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
			sLogger.trace("Entering");
		}
		
		sLogger.debug("Creating and starting Knowledge Base File Access Evaluated Thread for reading");
		KnowledgeBaseFileAccessReadEvaluatedTask lKnowledgeBaseFileAccessReadEvaluatedTask = new KnowledgeBaseFileAccessReadEvaluatedTask(mKnowledgeBaseFilePool, mStateStringForKB,  mFileIndexString, pLogContext);
				
		lKnowledgeBaseFileAccessReadEvaluatedTask.executeTask();
		if (!lKnowledgeBaseFileAccessReadEvaluatedTask.isTransactionSuccessful() || !lKnowledgeBaseFileAccessReadEvaluatedTask.isTransactionFinished()) {
			sLogger.error("Knowledge Base Error occurred!");
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			throw new KnowledgeBaseException();
		} else {
			sLogger.debug("EvaluationThreadInfo is found, setting to BoardState");
			mEvaluationThreadInfo.setEvaluationThreadInfo(lKnowledgeBaseFileAccessReadEvaluatedTask.getEvaluationThreadInfo());
		}

		byte lEvaluationThreadInfo = mEvaluationThreadInfo.getEvaluationThreadInfo();
					
		if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
			sLogger.trace("Exiting");
		}

		lEvaluationThreadInfo = (byte) ((lEvaluationThreadInfo >> pPos) & 1);
		
		if (sLogger.isDebugEnabled()) {
			sLogger.debug("Move: " + mFileIndexString + " Knowledge Base Evaluation Thread Info: " + lEvaluationThreadInfo);
		}
		

		if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
			sLogger.trace("Exiting");
		}
		
		
		return  lEvaluationThreadInfo == 0 ? false : true;
	}

	
	
	

	private String mFileIndexString = null;
	private String mReciprocalFileIndexString = null;

	private BoardState mPreviousMoveNode = null;

	public String toString() {
		if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
			sLogger.trace("Entering");
		}
		if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
			sLogger.trace("Exiting");
		}
		return "BoardState: " + mFileIndexString + " (" + mMoveScore.getMoveScore() + ")";
	}

	public static enum GameState {
		WIN(true, true, "WIN"), LOSS(true, false, "LOSS"), DRAW(false, true, "DRAW"), CONTINUE(false, false, "CONTINUE");
		private BitSet mGameState = new BitSet(BoardState.GAME_STATE_SIZE_IN_BITS);
		private String mGameStateString = new String();

		GameState(boolean pFirst, boolean pSecond, String lGameStateString) {
			mGameState.set(0, pFirst);
			mGameState.set(1, pSecond);
			mGameStateString = lGameStateString;
		}

		public BitSet getGameStateBitSet() {
			return mGameState;
		}

		public String getGameStateString() {
			return mGameStateString;
		}
	}

	public static enum CellState {
		UNOCCUPIED(false, false, " ", 0), OPPONENT_OCCUPIED(false, true, "O", 1), SELF_OCCUPIED(true, true, "X", 2);

		private BitSet mCellState = new BitSet(BoardState.CELL_STATE_SIZE_IN_BITS);
		private String mOccupationString = new String();
		private int mOccupationInt = 0;

		CellState(boolean pFirst, boolean pSecond, String lOccupationString, int lOccupationInt) {
			mCellState.set(0, pFirst);
			mCellState.set(1, pSecond);
			mOccupationString = lOccupationString;
			mOccupationInt = lOccupationInt;
		}

		public BitSet getCellStateBitSet() {
			return mCellState;
		}

		public String getOccupationString() {
			return mOccupationString;
		}

		public int getOccupationInt() {
			return mOccupationInt;
		}
	}

	public static enum Move {
		SELF_SLOT0(true, false, false, false, 1), SELF_SLOT1(true, false, false, true, 2), SELF_SLOT2(true, false, true, false, 3), SELF_SLOT3(true, false,
				true, true, 4), SELF_SLOT4(true, true, false, false, 5), SELF_SLOT5(true, true, false, true, 6), SELF_SLOT6(true, true, true, false, 7), OPPONENT_SLOT0(
				false, false, false, false, 1), OPPONENT_SLOT1(false, false, false, true, 2), OPPONENT_SLOT2(false, false, true, false, 3), OPPONENT_SLOT3(
				false, false, true, true, 4), OPPONENT_SLOT4(false, true, false, false, 5), OPPONENT_SLOT5(false, true, false, true, 6), OPPONENT_SLOT6(false,
				true, true, false, 7), SELF_NOMOVE(true, true, true, true, 0), OPPONENT_NOMOVE(false, true, true, true, 0);

		private final BitSet mMove = new BitSet(BoardState.MOVE_STATE_SIZE_IN_BITS);
		private int mMoveIntValue = 0;

		Move(boolean pFirst, boolean pSecond, boolean pThird, boolean pFourth, int pValue) {
			mMove.set(0, pFirst);
			mMove.set(1, pSecond);
			mMove.set(2, pThird);
			mMove.set(3, pFourth);
			mMoveIntValue = pValue;
		}

		public int getMoveIntValue() {
			return mMoveIntValue;
		}

		public static Move getOpponentMove(int pMoveInt) {
			if (pMoveInt == OPPONENT_SLOT0.getMoveIntValue()) {
				if (sLogger.isDebugEnabled()) {
					sLogger.debug("Returning move: " + BoardState.Move.OPPONENT_SLOT0.toString());
				}
				if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
					sLogger.trace("Exiting");
				}
				return BoardState.Move.OPPONENT_SLOT0;
			} else if (pMoveInt == OPPONENT_SLOT1.getMoveIntValue()) {
				if (sLogger.isDebugEnabled()) {
					sLogger.debug("Returning move: " + BoardState.Move.OPPONENT_SLOT1.toString());
				}
				if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
					sLogger.trace("Exiting");
				}
				return BoardState.Move.OPPONENT_SLOT1;
			} else if (pMoveInt == OPPONENT_SLOT2.getMoveIntValue()) {
				if (sLogger.isDebugEnabled()) {
					sLogger.debug("Returning move: " + BoardState.Move.OPPONENT_SLOT2.toString());
				}
				if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
					sLogger.trace("Exiting");
				}
				return BoardState.Move.OPPONENT_SLOT2;
			} else if (pMoveInt == OPPONENT_SLOT3.getMoveIntValue()) {
				if (sLogger.isDebugEnabled()) {
					sLogger.debug("Returning move: " + BoardState.Move.OPPONENT_SLOT3.toString());
				}
				if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
					sLogger.trace("Exiting");
				}
				return BoardState.Move.OPPONENT_SLOT3;
			} else if (pMoveInt == OPPONENT_SLOT4.getMoveIntValue()) {
				if (sLogger.isDebugEnabled()) {
					sLogger.debug("Returning move: " + BoardState.Move.OPPONENT_SLOT4.toString());
				}
				if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
					sLogger.trace("Exiting");
				}
				return BoardState.Move.OPPONENT_SLOT4;
			} else if (pMoveInt == OPPONENT_SLOT5.getMoveIntValue()) {
				if (sLogger.isDebugEnabled()) {
					sLogger.debug("Returning move: " + BoardState.Move.OPPONENT_SLOT5.toString());
				}
				if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
					sLogger.trace("Exiting");
				}
				return BoardState.Move.OPPONENT_SLOT5;
			} else if (pMoveInt == OPPONENT_SLOT6.getMoveIntValue()) {
				if (sLogger.isDebugEnabled()) {
					sLogger.debug("Returning move: " + BoardState.Move.OPPONENT_SLOT6.toString());
				}
				if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
					sLogger.trace("Exiting");
				}
				return BoardState.Move.OPPONENT_SLOT6;
			} else if (pMoveInt == OPPONENT_NOMOVE.getMoveIntValue()) {
				if (sLogger.isDebugEnabled()) {
					sLogger.debug("Returning move: " + BoardState.Move.OPPONENT_NOMOVE.toString());
				}
				if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
					sLogger.trace("Exiting");
				}
				return BoardState.Move.OPPONENT_NOMOVE;
			} else {
				if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
					sLogger.trace("Exiting");
				}
				return null;
			}

		}

		public static Move getSelfMove(int pMoveInt) {
			if (pMoveInt == SELF_SLOT0.getMoveIntValue()) {
				if (sLogger.isDebugEnabled()) {
					sLogger.debug("Returning move: " + BoardState.Move.SELF_SLOT0.toString());
				}
				if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
					sLogger.trace("Exiting");
				}
				return BoardState.Move.SELF_SLOT0;
			} else if (pMoveInt == SELF_SLOT1.getMoveIntValue()) {
				if (sLogger.isDebugEnabled()) {
					sLogger.debug("Returning move: " + BoardState.Move.SELF_SLOT1.toString());
				}
				if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
					sLogger.trace("Exiting");
				}
				return BoardState.Move.SELF_SLOT1;
			} else if (pMoveInt == SELF_SLOT2.getMoveIntValue()) {
				if (sLogger.isDebugEnabled()) {
					sLogger.debug("Returning move: " + BoardState.Move.SELF_SLOT2.toString());
				}
				if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
					sLogger.trace("Exiting");
				}
				return BoardState.Move.SELF_SLOT2;
			} else if (pMoveInt == SELF_SLOT3.getMoveIntValue()) {
				if (sLogger.isDebugEnabled()) {
					sLogger.debug("Returning move: " + BoardState.Move.SELF_SLOT3.toString());
				}
				if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
					sLogger.trace("Exiting");
				}
				return BoardState.Move.SELF_SLOT3;
			} else if (pMoveInt == SELF_SLOT4.getMoveIntValue()) {
				if (sLogger.isDebugEnabled()) {
					sLogger.debug("Returning move: " + BoardState.Move.SELF_SLOT4.toString());
				}
				if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
					sLogger.trace("Exiting");
				}
				return BoardState.Move.SELF_SLOT4;
			} else if (pMoveInt == SELF_SLOT5.getMoveIntValue()) {
				if (sLogger.isDebugEnabled()) {
					sLogger.debug("Returning move: " + BoardState.Move.SELF_SLOT5.toString());
				}
				if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
					sLogger.trace("Exiting");
				}
				return BoardState.Move.SELF_SLOT5;
			} else if (pMoveInt == SELF_SLOT6.getMoveIntValue()) {
				if (sLogger.isDebugEnabled()) {
					sLogger.debug("Returning move: " + BoardState.Move.SELF_SLOT6.toString());
				}
				if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
					sLogger.trace("Exiting");
				}
				return BoardState.Move.SELF_SLOT6;
			} else if (pMoveInt == SELF_NOMOVE.getMoveIntValue()) {
				if (sLogger.isDebugEnabled()) {
					sLogger.debug("Returning move: " + BoardState.Move.SELF_NOMOVE.toString());
				}
				if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
					sLogger.trace("Exiting");
				}
				return BoardState.Move.SELF_NOMOVE;
			} else {
				if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
					sLogger.trace("Exiting");
				}
				return null;
			}

		}

		public BitSet getMoveBitSet() {
			return mMove;
		}
	}

	public BoardState getParentBoardState() {
		if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
			sLogger.trace("Entering");
		}
		if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
			sLogger.trace("Exiting");
		}
		return this.mPreviousMoveNode;
	}

	public String getFileIndexString() {
		return mFileIndexString;
	}
	
	public String getReciprocalFileIndexString () {
		return mReciprocalFileIndexString;
	}

	public static final int WINNING_COMBOS_SCORE_SIZE = 69;
	private BitSet mSelfWinningCombos = null;
	private BitSet mOpponentWinningCombos = null;
	private MovePosition mCurrentMovePosition = null;

	public BitSet getSelfWinningCombos () {
		return mSelfWinningCombos;
	}
	
	public BitSet getOpponentWinningCombos () {
		return mOpponentWinningCombos;
	}
	
	public MovePosition getCurrentMovePosition () {
		return mCurrentMovePosition;
	}
	
	
	
	
	
	
	static {
		WinningCombo.OPPONENT_VERTICAL_0_0.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_0_0,
				MovePosition.OPPONENT_0_1,
				MovePosition.OPPONENT_0_2,
				MovePosition.OPPONENT_0_3
				))); 
		WinningCombo.OPPONENT_VERTICAL_0_1.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_0_1,
				MovePosition.OPPONENT_0_2,
				MovePosition.OPPONENT_0_3,
				MovePosition.OPPONENT_0_4
				)));  
		WinningCombo.OPPONENT_VERTICAL_0_2.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_0_2,
				MovePosition.OPPONENT_0_3,
				MovePosition.OPPONENT_0_4,
				MovePosition.OPPONENT_0_5
				)));   

		WinningCombo.OPPONENT_VERTICAL_1_0.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_1_0,
				MovePosition.OPPONENT_1_1,
				MovePosition.OPPONENT_1_2,
				MovePosition.OPPONENT_1_3
				)));
		WinningCombo.OPPONENT_VERTICAL_1_1.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_1_1,
				MovePosition.OPPONENT_1_2,
				MovePosition.OPPONENT_1_3,
				MovePosition.OPPONENT_1_4
				)));
		
		WinningCombo.OPPONENT_VERTICAL_1_2.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_1_2,
				MovePosition.OPPONENT_1_3,
				MovePosition.OPPONENT_1_4,
				MovePosition.OPPONENT_1_5
				)));  

		WinningCombo.OPPONENT_VERTICAL_2_0.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_2_0,
				MovePosition.OPPONENT_2_1,
				MovePosition.OPPONENT_2_2,
				MovePosition.OPPONENT_2_3
				)));   
		WinningCombo.OPPONENT_VERTICAL_2_1.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_2_1,
				MovePosition.OPPONENT_2_2,
				MovePosition.OPPONENT_2_3,
				MovePosition.OPPONENT_2_4
				)));    
		WinningCombo.OPPONENT_VERTICAL_2_2.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_2_2,
				MovePosition.OPPONENT_2_3,
				MovePosition.OPPONENT_2_4,
				MovePosition.OPPONENT_2_5
				)));     

		WinningCombo.OPPONENT_VERTICAL_3_0.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_3_0,
				MovePosition.OPPONENT_3_1,
				MovePosition.OPPONENT_3_2,
				MovePosition.OPPONENT_3_3
				)));      
		WinningCombo.OPPONENT_VERTICAL_3_1.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_3_1,
				MovePosition.OPPONENT_3_2,
				MovePosition.OPPONENT_3_3,
				MovePosition.OPPONENT_3_4
				)));      
		WinningCombo.OPPONENT_VERTICAL_3_2.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_3_2,
				MovePosition.OPPONENT_3_3,
				MovePosition.OPPONENT_3_4,
				MovePosition.OPPONENT_3_5
				)));       

		WinningCombo.OPPONENT_VERTICAL_4_0.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_4_0,
				MovePosition.OPPONENT_4_1,
				MovePosition.OPPONENT_4_2,
				MovePosition.OPPONENT_4_3
				)));       
		WinningCombo.OPPONENT_VERTICAL_4_1.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_4_1,
				MovePosition.OPPONENT_4_2,
				MovePosition.OPPONENT_4_3,
				MovePosition.OPPONENT_4_4
				)));        
		WinningCombo.OPPONENT_VERTICAL_4_2.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_4_2,
				MovePosition.OPPONENT_4_3,
				MovePosition.OPPONENT_4_4,
				MovePosition.OPPONENT_4_5
				)));         

		WinningCombo.OPPONENT_VERTICAL_5_0.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_5_0, 
				MovePosition.OPPONENT_5_1,
				MovePosition.OPPONENT_5_2,
				MovePosition.OPPONENT_5_3
				)));          
		WinningCombo.OPPONENT_VERTICAL_5_1.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_5_1, 
				MovePosition.OPPONENT_5_2,
				MovePosition.OPPONENT_5_3,
				MovePosition.OPPONENT_5_4
				)));           
		WinningCombo.OPPONENT_VERTICAL_5_2.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_5_2, 
				MovePosition.OPPONENT_5_3,
				MovePosition.OPPONENT_5_4,
				MovePosition.OPPONENT_5_5
				)));            

		WinningCombo.OPPONENT_VERTICAL_6_0.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_6_0, 
				MovePosition.OPPONENT_6_1,
				MovePosition.OPPONENT_6_2,
				MovePosition.OPPONENT_6_3
				)));           
		WinningCombo.OPPONENT_VERTICAL_6_1.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_6_1, 
				MovePosition.OPPONENT_6_2,
				MovePosition.OPPONENT_6_3,
				MovePosition.OPPONENT_6_4
				)));            
		WinningCombo.OPPONENT_VERTICAL_6_2.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_6_2, 
				MovePosition.OPPONENT_6_3,
				MovePosition.OPPONENT_6_4,
				MovePosition.OPPONENT_6_5
				)));          


		//							Who			Direction			COLUMN POS				ROW POS
		WinningCombo.OPPONENT_HORIZONTAL_0_0.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_0_0, 
				MovePosition.OPPONENT_1_0,
				MovePosition.OPPONENT_2_0,
				MovePosition.OPPONENT_3_0
				)));            
		WinningCombo.OPPONENT_HORIZONTAL_0_1.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_0_1, 
				MovePosition.OPPONENT_1_1,
				MovePosition.OPPONENT_2_1,
				MovePosition.OPPONENT_3_1
				)));             
		WinningCombo.OPPONENT_HORIZONTAL_0_2.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_0_2, 
				MovePosition.OPPONENT_1_2,
				MovePosition.OPPONENT_2_2,
				MovePosition.OPPONENT_3_2
				)));             
		WinningCombo.OPPONENT_HORIZONTAL_0_3.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_0_3, 
				MovePosition.OPPONENT_1_3,
				MovePosition.OPPONENT_2_3,
				MovePosition.OPPONENT_3_3
				)));             
		WinningCombo.OPPONENT_HORIZONTAL_0_4.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_0_4, 
				MovePosition.OPPONENT_1_4,
				MovePosition.OPPONENT_2_4,
				MovePosition.OPPONENT_3_4
				)));             
		WinningCombo.OPPONENT_HORIZONTAL_0_5.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_0_5, 
				MovePosition.OPPONENT_1_5,
				MovePosition.OPPONENT_2_5,
				MovePosition.OPPONENT_3_5
				)));             

		WinningCombo.OPPONENT_HORIZONTAL_1_0.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_1_0, 
				MovePosition.OPPONENT_2_0,
				MovePosition.OPPONENT_3_0,
				MovePosition.OPPONENT_4_0
				)));             
		WinningCombo.OPPONENT_HORIZONTAL_1_1.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_1_1, 
				MovePosition.OPPONENT_2_1,
				MovePosition.OPPONENT_3_1,
				MovePosition.OPPONENT_4_1
				)));              
		WinningCombo.OPPONENT_HORIZONTAL_1_2.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_1_2, 
				MovePosition.OPPONENT_2_2,
				MovePosition.OPPONENT_3_2,
				MovePosition.OPPONENT_4_2
				)));               
		WinningCombo.OPPONENT_HORIZONTAL_1_3.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_1_3, 
				MovePosition.OPPONENT_2_3,
				MovePosition.OPPONENT_3_3,
				MovePosition.OPPONENT_4_3
				)));               
		WinningCombo.OPPONENT_HORIZONTAL_1_4.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_1_4, 
				MovePosition.OPPONENT_2_4,
				MovePosition.OPPONENT_3_4,
				MovePosition.OPPONENT_4_4
				)));               
		WinningCombo.OPPONENT_HORIZONTAL_1_5.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_1_5, 
				MovePosition.OPPONENT_2_5,
				MovePosition.OPPONENT_3_5,
				MovePosition.OPPONENT_4_5
				)));               
		
		WinningCombo.OPPONENT_HORIZONTAL_2_0.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_2_0, 
				MovePosition.OPPONENT_3_0,
				MovePosition.OPPONENT_4_0,
				MovePosition.OPPONENT_5_0
				)));              
		WinningCombo.OPPONENT_HORIZONTAL_2_1.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_2_1, 
				MovePosition.OPPONENT_3_1,
				MovePosition.OPPONENT_4_1,
				MovePosition.OPPONENT_5_1
				)));               
		WinningCombo.OPPONENT_HORIZONTAL_2_2.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_2_2, 
				MovePosition.OPPONENT_3_2,
				MovePosition.OPPONENT_4_2,
				MovePosition.OPPONENT_5_2
				)));                
		WinningCombo.OPPONENT_HORIZONTAL_2_3.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_2_3, 
				MovePosition.OPPONENT_3_3,
				MovePosition.OPPONENT_4_3,
				MovePosition.OPPONENT_5_3
				)));               
		WinningCombo.OPPONENT_HORIZONTAL_2_4.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_2_4, 
				MovePosition.OPPONENT_3_4,
				MovePosition.OPPONENT_4_4,
				MovePosition.OPPONENT_5_4
				)));               
		WinningCombo.OPPONENT_HORIZONTAL_2_5.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_2_5, 
				MovePosition.OPPONENT_3_5,
				MovePosition.OPPONENT_4_5,
				MovePosition.OPPONENT_5_5
				)));               
		
		WinningCombo.OPPONENT_HORIZONTAL_3_0.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_3_0, 
				MovePosition.OPPONENT_4_0,
				MovePosition.OPPONENT_5_0,
				MovePosition.OPPONENT_6_0
				)));               
		WinningCombo.OPPONENT_HORIZONTAL_3_1.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_3_1, 
				MovePosition.OPPONENT_4_1,
				MovePosition.OPPONENT_5_1,
				MovePosition.OPPONENT_6_1
				)));                
		WinningCombo.OPPONENT_HORIZONTAL_3_2.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_3_2, 
				MovePosition.OPPONENT_4_2,
				MovePosition.OPPONENT_5_2,
				MovePosition.OPPONENT_6_2
				)));                
		WinningCombo.OPPONENT_HORIZONTAL_3_3.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_3_3, 
				MovePosition.OPPONENT_4_3,
				MovePosition.OPPONENT_5_3,
				MovePosition.OPPONENT_6_3
				)));                
		WinningCombo.OPPONENT_HORIZONTAL_3_4.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_3_4, 
				MovePosition.OPPONENT_4_4,
				MovePosition.OPPONENT_5_4,
				MovePosition.OPPONENT_6_4
				)));                
		WinningCombo.OPPONENT_HORIZONTAL_3_5.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_3_5, 
				MovePosition.OPPONENT_4_5,
				MovePosition.OPPONENT_5_5,
				MovePosition.OPPONENT_6_5
				)));                
		
		//						Who			Direction			COLUMN POS				ROW POS
		WinningCombo.OPPONENT_DIAGONAL_0_0.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_0_0, 
				MovePosition.OPPONENT_1_1,
				MovePosition.OPPONENT_2_2,
				MovePosition.OPPONENT_3_3
				)));                
		WinningCombo.OPPONENT_DIAGONAL_0_1.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_0_1, 
				MovePosition.OPPONENT_1_2,
				MovePosition.OPPONENT_2_3,
				MovePosition.OPPONENT_3_4
				)));                 
		WinningCombo.OPPONENT_DIAGONAL_0_2.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_0_2, 
				MovePosition.OPPONENT_1_3,
				MovePosition.OPPONENT_2_4,
				MovePosition.OPPONENT_3_5
				)));                  

		WinningCombo.OPPONENT_DIAGONAL_1_0.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_1_0, 
				MovePosition.OPPONENT_2_1,
				MovePosition.OPPONENT_3_2,
				MovePosition.OPPONENT_4_3
				)));                 
		WinningCombo.OPPONENT_DIAGONAL_1_1.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_1_1, 
				MovePosition.OPPONENT_2_2,
				MovePosition.OPPONENT_3_3,
				MovePosition.OPPONENT_4_4
				)));                  
		WinningCombo.OPPONENT_DIAGONAL_1_2.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_1_2, 
				MovePosition.OPPONENT_2_3,
				MovePosition.OPPONENT_3_4,
				MovePosition.OPPONENT_4_5
				)));                   
		
		WinningCombo.OPPONENT_DIAGONAL_2_0.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_2_0, 
				MovePosition.OPPONENT_3_1,
				MovePosition.OPPONENT_4_2,
				MovePosition.OPPONENT_5_3
				)));                  
		WinningCombo.OPPONENT_DIAGONAL_2_1.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_2_1, 
				MovePosition.OPPONENT_3_2,
				MovePosition.OPPONENT_4_3,
				MovePosition.OPPONENT_5_4
				)));                   
		WinningCombo.OPPONENT_DIAGONAL_2_2.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_2_2, 
				MovePosition.OPPONENT_3_3,
				MovePosition.OPPONENT_4_4,
				MovePosition.OPPONENT_5_5
				)));                    
		
		WinningCombo.OPPONENT_DIAGONAL_3_0.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_3_0, 
				MovePosition.OPPONENT_4_1,
				MovePosition.OPPONENT_5_2,
				MovePosition.OPPONENT_6_3
				)));                   
		WinningCombo.OPPONENT_DIAGONAL_3_1.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_3_1, 
				MovePosition.OPPONENT_4_2,
				MovePosition.OPPONENT_5_3,
				MovePosition.OPPONENT_6_4
				)));                    
		WinningCombo.OPPONENT_DIAGONAL_3_2.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_3_2, 
				MovePosition.OPPONENT_4_3,
				MovePosition.OPPONENT_5_4,
				MovePosition.OPPONENT_6_5
				)));                    
		
		//							Who			Direction			COLUMN POS				ROW POS
		WinningCombo.OPPONENT_OPPOSITE_0_3.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_0_3, 
				MovePosition.OPPONENT_1_2,
				MovePosition.OPPONENT_2_1,
				MovePosition.OPPONENT_3_0
				)));                    
		WinningCombo.OPPONENT_OPPOSITE_0_4.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_0_4, 
				MovePosition.OPPONENT_1_3,
				MovePosition.OPPONENT_2_2,
				MovePosition.OPPONENT_3_1
				)));                     
		WinningCombo.OPPONENT_OPPOSITE_0_5.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_0_5, 
				MovePosition.OPPONENT_1_4,
				MovePosition.OPPONENT_2_3,
				MovePosition.OPPONENT_3_2
				)));                      

		WinningCombo.OPPONENT_OPPOSITE_1_3.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_1_3, 
				MovePosition.OPPONENT_2_2,
				MovePosition.OPPONENT_3_1,
				MovePosition.OPPONENT_4_0
				)));                     
		WinningCombo.OPPONENT_OPPOSITE_1_4.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_1_4, 
				MovePosition.OPPONENT_2_3,
				MovePosition.OPPONENT_3_2,
				MovePosition.OPPONENT_4_1
				)));                      
		WinningCombo.OPPONENT_OPPOSITE_1_5.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_1_5, 
				MovePosition.OPPONENT_2_4,
				MovePosition.OPPONENT_3_3,
				MovePosition.OPPONENT_4_2
				)));                       
		
		WinningCombo.OPPONENT_OPPOSITE_2_3.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_2_3, 
				MovePosition.OPPONENT_3_2,
				MovePosition.OPPONENT_4_1,
				MovePosition.OPPONENT_5_0
				)));                      
		WinningCombo.OPPONENT_OPPOSITE_2_4.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_2_4, 
				MovePosition.OPPONENT_3_3,
				MovePosition.OPPONENT_4_2,
				MovePosition.OPPONENT_5_1
				)));                       
		WinningCombo.OPPONENT_OPPOSITE_2_5.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_2_5, 
				MovePosition.OPPONENT_3_4,
				MovePosition.OPPONENT_4_3,
				MovePosition.OPPONENT_5_2
				)));                       
		
		WinningCombo.OPPONENT_OPPOSITE_3_3.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_3_3, 
				MovePosition.OPPONENT_4_2,
				MovePosition.OPPONENT_5_1,
				MovePosition.OPPONENT_6_0
				)));                       
		WinningCombo.OPPONENT_OPPOSITE_3_4.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_3_4, 
				MovePosition.OPPONENT_4_3,
				MovePosition.OPPONENT_5_2,
				MovePosition.OPPONENT_6_1
				)));                        
		WinningCombo.OPPONENT_OPPOSITE_3_5.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.OPPONENT_3_5, 
				MovePosition.OPPONENT_4_4,
				MovePosition.OPPONENT_5_3,
				MovePosition.OPPONENT_6_2
				)));                        
		
		
		//						Who			Direction			COLUMN POS				ROW POS
		WinningCombo.SELF_VERTICAL_0_0.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_0_0,
				MovePosition.SELF_0_1,
				MovePosition.SELF_0_2,
				MovePosition.SELF_0_3
				))); 
		WinningCombo.SELF_VERTICAL_0_1.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_0_1,
				MovePosition.SELF_0_2,
				MovePosition.SELF_0_3,
				MovePosition.SELF_0_4
				)));  
		WinningCombo.SELF_VERTICAL_0_2.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_0_2,
				MovePosition.SELF_0_3,
				MovePosition.SELF_0_4,
				MovePosition.SELF_0_5
				)));   

		WinningCombo.SELF_VERTICAL_1_0.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_1_0,
				MovePosition.SELF_1_1,
				MovePosition.SELF_1_2,
				MovePosition.SELF_1_3
				)));  
		WinningCombo.SELF_VERTICAL_1_1.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_1_1,
				MovePosition.SELF_1_2,
				MovePosition.SELF_1_3,
				MovePosition.SELF_1_4
				)));  
		WinningCombo.SELF_VERTICAL_1_2.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_1_2,
				MovePosition.SELF_1_3,
				MovePosition.SELF_1_4,
				MovePosition.SELF_1_5
				)));  

		WinningCombo.SELF_VERTICAL_2_0.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_2_0,
				MovePosition.SELF_2_1,
				MovePosition.SELF_2_2,
				MovePosition.SELF_2_3
				)));   
		WinningCombo.SELF_VERTICAL_2_1.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_2_1,
				MovePosition.SELF_2_2,
				MovePosition.SELF_2_3,
				MovePosition.SELF_2_4
				)));    
		WinningCombo.SELF_VERTICAL_2_2.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_2_2,
				MovePosition.SELF_2_3,
				MovePosition.SELF_2_4,
				MovePosition.SELF_2_5
				)));     

		WinningCombo.SELF_VERTICAL_3_0.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_3_0,
				MovePosition.SELF_3_1,
				MovePosition.SELF_3_2,
				MovePosition.SELF_3_3
				)));      
		WinningCombo.SELF_VERTICAL_3_1.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_3_1,
				MovePosition.SELF_3_2,
				MovePosition.SELF_3_3,
				MovePosition.SELF_3_4
				)));      
		WinningCombo.SELF_VERTICAL_3_2.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_3_2,
				MovePosition.SELF_3_3,
				MovePosition.SELF_3_4,
				MovePosition.SELF_3_5
				)));       

		WinningCombo.SELF_VERTICAL_4_0.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_4_0,
				MovePosition.SELF_4_1,
				MovePosition.SELF_4_2,
				MovePosition.SELF_4_3
				)));       
		WinningCombo.SELF_VERTICAL_4_1.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_4_1,
				MovePosition.SELF_4_2,
				MovePosition.SELF_4_3,
				MovePosition.SELF_4_4
				)));        
		WinningCombo.SELF_VERTICAL_4_2.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_4_2,
				MovePosition.SELF_4_3,
				MovePosition.SELF_4_4,
				MovePosition.SELF_4_5
				)));         

		WinningCombo.SELF_VERTICAL_5_0.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_5_0, 
				MovePosition.SELF_5_1,
				MovePosition.SELF_5_2,
				MovePosition.SELF_5_3
				)));          
		WinningCombo.SELF_VERTICAL_5_1.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_5_1, 
				MovePosition.SELF_5_2,
				MovePosition.SELF_5_3,
				MovePosition.SELF_5_4
				)));           
		WinningCombo.SELF_VERTICAL_5_2.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_5_2, 
				MovePosition.SELF_5_3,
				MovePosition.SELF_5_4,
				MovePosition.SELF_5_5
				)));            

		WinningCombo.SELF_VERTICAL_6_0.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_6_0, 
				MovePosition.SELF_6_1,
				MovePosition.SELF_6_2,
				MovePosition.SELF_6_3
				)));           
		WinningCombo.SELF_VERTICAL_6_1.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_6_1, 
				MovePosition.SELF_6_2,
				MovePosition.SELF_6_3,
				MovePosition.SELF_6_4
				)));            
		WinningCombo.SELF_VERTICAL_6_2.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_6_2, 
				MovePosition.SELF_6_3,
				MovePosition.SELF_6_4,
				MovePosition.SELF_6_5
				)));            


		//							Who			Direction			COLUMN POS				ROW POS
		WinningCombo.SELF_HORIZONTAL_0_0.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_0_0, 
				MovePosition.SELF_1_0,
				MovePosition.SELF_2_0,
				MovePosition.SELF_3_0
				)));            
		WinningCombo.SELF_HORIZONTAL_0_1.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_0_1, 
				MovePosition.SELF_1_1,
				MovePosition.SELF_2_1,
				MovePosition.SELF_3_1
				)));             
		WinningCombo.SELF_HORIZONTAL_0_2.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_0_2, 
				MovePosition.SELF_1_2,
				MovePosition.SELF_2_2,
				MovePosition.SELF_3_2
				)));             
		WinningCombo.SELF_HORIZONTAL_0_3.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_0_3, 
				MovePosition.SELF_1_3,
				MovePosition.SELF_2_3,
				MovePosition.SELF_3_3
				)));             
		WinningCombo.SELF_HORIZONTAL_0_4.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_0_4, 
				MovePosition.SELF_1_4,
				MovePosition.SELF_2_4,
				MovePosition.SELF_3_4
				)));             
		WinningCombo.SELF_HORIZONTAL_0_5.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_0_5, 
				MovePosition.SELF_1_5,
				MovePosition.SELF_2_5,
				MovePosition.SELF_3_5
				)));             

		WinningCombo.SELF_HORIZONTAL_1_0.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_1_0, 
				MovePosition.SELF_2_0,
				MovePosition.SELF_3_0,
				MovePosition.SELF_4_0
				)));             
		WinningCombo.SELF_HORIZONTAL_1_1.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_1_1, 
				MovePosition.SELF_2_1,
				MovePosition.SELF_3_1,
				MovePosition.SELF_4_1
				)));              
		WinningCombo.SELF_HORIZONTAL_1_2.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_1_2, 
				MovePosition.SELF_2_2,
				MovePosition.SELF_3_2,
				MovePosition.SELF_4_2
				)));               
		WinningCombo.SELF_HORIZONTAL_1_3.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_1_3, 
				MovePosition.SELF_2_3,
				MovePosition.SELF_3_3,
				MovePosition.SELF_4_3
				)));               
		WinningCombo.SELF_HORIZONTAL_1_4.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_1_4, 
				MovePosition.SELF_2_4,
				MovePosition.SELF_3_4,
				MovePosition.SELF_4_4
				)));               
		WinningCombo.SELF_HORIZONTAL_1_5.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_1_5, 
				MovePosition.SELF_2_5,
				MovePosition.SELF_3_5,
				MovePosition.SELF_4_5
				)));               
		
		WinningCombo.SELF_HORIZONTAL_2_0.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_2_0, 
				MovePosition.SELF_3_0,
				MovePosition.SELF_4_0,
				MovePosition.SELF_5_0
				)));              
		WinningCombo.SELF_HORIZONTAL_2_1.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_2_1, 
				MovePosition.SELF_3_1,
				MovePosition.SELF_4_1,
				MovePosition.SELF_5_1
				)));               
		WinningCombo.SELF_HORIZONTAL_2_2.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_2_2, 
				MovePosition.SELF_3_2,
				MovePosition.SELF_4_2,
				MovePosition.SELF_5_2
				)));                
		WinningCombo.SELF_HORIZONTAL_2_3.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_2_3, 
				MovePosition.SELF_3_3,
				MovePosition.SELF_4_3,
				MovePosition.SELF_5_3
				)));               
		WinningCombo.SELF_HORIZONTAL_2_4.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_2_4, 
				MovePosition.SELF_3_4,
				MovePosition.SELF_4_4,
				MovePosition.SELF_5_4
				)));               
		WinningCombo.SELF_HORIZONTAL_2_5.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_2_5, 
				MovePosition.SELF_3_5,
				MovePosition.SELF_4_5,
				MovePosition.SELF_5_5
				)));               
		
		WinningCombo.SELF_HORIZONTAL_3_0.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_3_0, 
				MovePosition.SELF_4_0,
				MovePosition.SELF_5_0,
				MovePosition.SELF_6_0
				)));               
		WinningCombo.SELF_HORIZONTAL_3_1.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_3_1, 
				MovePosition.SELF_4_1,
				MovePosition.SELF_5_1,
				MovePosition.SELF_6_1
				)));                
		WinningCombo.SELF_HORIZONTAL_3_2.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_3_2, 
				MovePosition.SELF_4_2,
				MovePosition.SELF_5_2,
				MovePosition.SELF_6_2
				)));                
		WinningCombo.SELF_HORIZONTAL_3_3.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_3_3, 
				MovePosition.SELF_4_3,
				MovePosition.SELF_5_3,
				MovePosition.SELF_6_3
				)));                
		WinningCombo.SELF_HORIZONTAL_3_4.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_3_4, 
				MovePosition.SELF_4_4,
				MovePosition.SELF_5_4,
				MovePosition.SELF_6_4
				)));                
		WinningCombo.SELF_HORIZONTAL_3_5.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_3_5, 
				MovePosition.SELF_4_5,
				MovePosition.SELF_5_5,
				MovePosition.SELF_6_5
				)));                
		
		//						Who			Direction			COLUMN POS				ROW POS
		WinningCombo.SELF_DIAGONAL_0_0.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_0_0, 
				MovePosition.SELF_1_1,
				MovePosition.SELF_2_2,
				MovePosition.SELF_3_3
				)));                
		WinningCombo.SELF_DIAGONAL_0_1.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_0_1, 
				MovePosition.SELF_1_2,
				MovePosition.SELF_2_3,
				MovePosition.SELF_3_4
				)));                 
		WinningCombo.SELF_DIAGONAL_0_2.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_0_2, 
				MovePosition.SELF_1_3,
				MovePosition.SELF_2_4,
				MovePosition.SELF_3_5
				)));                  

		WinningCombo.SELF_DIAGONAL_1_0.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_1_0, 
				MovePosition.SELF_2_1,
				MovePosition.SELF_3_2,
				MovePosition.SELF_4_3
				)));                 
		WinningCombo.SELF_DIAGONAL_1_1.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_1_1, 
				MovePosition.SELF_2_2,
				MovePosition.SELF_3_3,
				MovePosition.SELF_4_4
				)));                  
		WinningCombo.SELF_DIAGONAL_1_2.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_1_2, 
				MovePosition.SELF_2_3,
				MovePosition.SELF_3_4,
				MovePosition.SELF_4_5
				)));                   
		
		WinningCombo.SELF_DIAGONAL_2_0.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_2_0, 
				MovePosition.SELF_3_1,
				MovePosition.SELF_4_2,
				MovePosition.SELF_5_3
				)));                  
		WinningCombo.SELF_DIAGONAL_2_1.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_2_1, 
				MovePosition.SELF_3_2,
				MovePosition.SELF_4_3,
				MovePosition.SELF_5_4
				)));                   
		WinningCombo.SELF_DIAGONAL_2_2.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_2_2, 
				MovePosition.SELF_3_3,
				MovePosition.SELF_4_4,
				MovePosition.SELF_5_5
				)));                    
		
		WinningCombo.SELF_DIAGONAL_3_0.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_3_0, 
				MovePosition.SELF_4_1,
				MovePosition.SELF_5_2,
				MovePosition.SELF_6_3
				)));                   
		WinningCombo.SELF_DIAGONAL_3_1.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_3_1, 
				MovePosition.SELF_4_2,
				MovePosition.SELF_5_3,
				MovePosition.SELF_6_4
				)));                    
		WinningCombo.SELF_DIAGONAL_3_2.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_3_2, 
				MovePosition.SELF_4_3,
				MovePosition.SELF_5_4,
				MovePosition.SELF_6_5
				)));                    
		
		//							Who			Direction			COLUMN POS				ROW POS
		WinningCombo.SELF_OPPOSITE_0_3.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_0_3, 
				MovePosition.SELF_1_2,
				MovePosition.SELF_2_1,
				MovePosition.SELF_3_0
				)));                    
		WinningCombo.SELF_OPPOSITE_0_4.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_0_4, 
				MovePosition.SELF_1_3,
				MovePosition.SELF_2_2,
				MovePosition.SELF_3_1
				)));                     
		WinningCombo.SELF_OPPOSITE_0_5.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_0_5, 
				MovePosition.SELF_1_4,
				MovePosition.SELF_2_3,
				MovePosition.SELF_3_2
				)));                      

		WinningCombo.SELF_OPPOSITE_1_3.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_1_3, 
				MovePosition.SELF_2_2,
				MovePosition.SELF_3_1,
				MovePosition.SELF_4_0
				)));                     
		WinningCombo.SELF_OPPOSITE_1_4.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_1_4, 
				MovePosition.SELF_2_3,
				MovePosition.SELF_3_2,
				MovePosition.SELF_4_1
				)));                      
		WinningCombo.SELF_OPPOSITE_1_5.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_1_5, 
				MovePosition.SELF_2_4,
				MovePosition.SELF_3_3,
				MovePosition.SELF_4_2
				)));                       
		
		WinningCombo.SELF_OPPOSITE_2_3.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_2_3, 
				MovePosition.SELF_3_2,
				MovePosition.SELF_4_1,
				MovePosition.SELF_5_0
				)));                      
		WinningCombo.SELF_OPPOSITE_2_4.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_2_4, 
				MovePosition.SELF_3_3,
				MovePosition.SELF_4_2,
				MovePosition.SELF_5_1
				)));                       
		WinningCombo.SELF_OPPOSITE_2_5.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_2_5, 
				MovePosition.SELF_3_4,
				MovePosition.SELF_4_3,
				MovePosition.SELF_5_2
				)));                       
		
		WinningCombo.SELF_OPPOSITE_3_3.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_3_3, 
				MovePosition.SELF_4_2,
				MovePosition.SELF_5_1,
				MovePosition.SELF_6_0
				)));                       
		WinningCombo.SELF_OPPOSITE_3_4.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_3_4, 
				MovePosition.SELF_4_3,
				MovePosition.SELF_5_2,
				MovePosition.SELF_6_1
				)));                        
		WinningCombo.SELF_OPPOSITE_3_5.setRequiredMovePositions (new ArrayList <MovePosition>(Arrays.asList(
				MovePosition.SELF_3_5, 
				MovePosition.SELF_4_4,
				MovePosition.SELF_5_3,
				MovePosition.SELF_6_2
				)));                        
		
	}
	
	
	
	
	
	
	
	
	public BoardState(KnowledgeBaseFilePool pKnowledgeBaseFilePool, BoardState.Move pFirstMove, boolean pEvaluation, String pLogContext) throws InvalidMoveException, KnowledgeBaseException,
			ConfigurationException {
		if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
			sLogger.trace("Entering");
		}

		mKnowledgeBaseFilePool = pKnowledgeBaseFilePool;

		mPreviousMoveNode = null;

		sLogger.debug("Creating Clean Board");
		mBoardStateInBits = new BitSet(BOARD_STATE_SIZE_IN_BITS);

		sLogger.debug("Setting Board Cell Map Delimiter");
		mBoardStateInBits.set(BOARD_DELIMITERLOCATION_BIT, true);

		sLogger.debug("Setting Board Move Map Delimiter");
		mBoardStateInBits.set(MOVE_DELIMITERLOCATION_BIT, true);

		sLogger.debug("Setting Game State Map Delimiter");
		mBoardStateInBits.set(GAMESTATE_DELIMITERLOCATION_BIT, true);

		for (int i = 0; i < MOVE_STATE_SIZE_IN_BITS; i++) {
			mBoardStateInBits.set(MOVE_STARTLOCATION_BIT + i, pFirstMove.getMoveBitSet().get(i));
		}

		for (int i = 0; i < GAME_STATE_SIZE_IN_BITS; i++) {
			mBoardStateInBits.set(GAMESTATE_STARTLOCATION_BIT + i, BoardState.GameState.CONTINUE.getGameStateBitSet().get(i));
		}

		mMoveScore = new MoveScore(this);
		mEvaluationThreadInfo = new EvaluationThreadInfo (this);
		

		
		
		mSelfWinningCombos = new BitSet(WINNING_COMBOS_SCORE_SIZE);
		for (int i = 0; i < WINNING_COMBOS_SCORE_SIZE; i++) {
			mSelfWinningCombos.set(i, true);
		}
		
		
		mOpponentWinningCombos = new BitSet(WINNING_COMBOS_SCORE_SIZE);
		for (int i = 0; i < WINNING_COMBOS_SCORE_SIZE; i++) {
			mOpponentWinningCombos.set(i, true);
		}
		


		
		
		
		mFileIndexString = String.valueOf(pFirstMove.getMoveIntValue());
		if (sLogger.isDebugEnabled()) {
			sLogger.debug("Constructing next fileindexstring to: " + mFileIndexString);
		}
		mReciprocalFileIndexString = new String ();

		for (int i = 0; i < mFileIndexString.length(); i++) {
			if (mFileIndexString.charAt(i) == '3') {
				mReciprocalFileIndexString += "5";
			} else if (mFileIndexString.charAt(i) == '5') {
				mReciprocalFileIndexString += "3";
			} else if (mFileIndexString.charAt(i) == '2') {
				mReciprocalFileIndexString += "6";
			} else if (mFileIndexString.charAt(i) == '6') {
				mReciprocalFileIndexString += "2";
			} else if (mFileIndexString.charAt(i) == '1') {
				mReciprocalFileIndexString += "7";
			} else if (mFileIndexString.charAt(i) == '7') {
				mReciprocalFileIndexString += "1";
			} else {
				mReciprocalFileIndexString += mFileIndexString.charAt(i);
			}
		}
		
		if (pFirstMove.equals(BoardState.Move.OPPONENT_SLOT0)) {
			for (int i = 0; i < MAX_ROWS; i++) {
				if (getCellState(0, i).equals(BoardState.CellState.UNOCCUPIED)) {
					setCellState(0, i, BoardState.CellState.OPPONENT_OCCUPIED);
					mCurrentMovePosition = MovePosition.getMovePosition(CellState.OPPONENT_OCCUPIED, 0, i);
					break;
				}
			}
		} else if (pFirstMove.equals(BoardState.Move.OPPONENT_SLOT1)) {
			for (int i = 0; i < MAX_ROWS; i++) {
				if (getCellState(1, i).equals(BoardState.CellState.UNOCCUPIED)) {
					setCellState(1, i, BoardState.CellState.OPPONENT_OCCUPIED);
					mCurrentMovePosition = MovePosition.getMovePosition(CellState.OPPONENT_OCCUPIED, 1, i);
					break;
				}
			}
		} else if (pFirstMove.equals(BoardState.Move.OPPONENT_SLOT2)) {
			for (int i = 0; i < MAX_ROWS; i++) {
				if (getCellState(2, i).equals(BoardState.CellState.UNOCCUPIED)) {
					setCellState(2, i, BoardState.CellState.OPPONENT_OCCUPIED);
					mCurrentMovePosition = MovePosition.getMovePosition(CellState.OPPONENT_OCCUPIED, 2, i);
					break;
				}
			}
		} else if (pFirstMove.equals(BoardState.Move.OPPONENT_SLOT3)) {
			for (int i = 0; i < MAX_ROWS; i++) {
				if (getCellState(3, i).equals(BoardState.CellState.UNOCCUPIED)) {
					setCellState(3, i, BoardState.CellState.OPPONENT_OCCUPIED);
					mCurrentMovePosition = MovePosition.getMovePosition(CellState.OPPONENT_OCCUPIED, 3, i);
					break;
				}
			}
		} else if (pFirstMove.equals(BoardState.Move.OPPONENT_SLOT4)) {
			for (int i = 0; i < MAX_ROWS; i++) {
				if (getCellState(4, i).equals(BoardState.CellState.UNOCCUPIED)) {
					setCellState(4, i, BoardState.CellState.OPPONENT_OCCUPIED);
					mCurrentMovePosition = MovePosition.getMovePosition(CellState.OPPONENT_OCCUPIED, 4, i);
					break;
				}
			}
		} else if (pFirstMove.equals(BoardState.Move.OPPONENT_SLOT5)) {
			for (int i = 0; i < MAX_ROWS; i++) {
				if (getCellState(5, i).equals(BoardState.CellState.UNOCCUPIED)) {
					setCellState(5, i, BoardState.CellState.OPPONENT_OCCUPIED);
					mCurrentMovePosition = MovePosition.getMovePosition(CellState.OPPONENT_OCCUPIED, 5, i);
					break;
				}
			}
		} else if (pFirstMove.equals(BoardState.Move.OPPONENT_SLOT6)) {
			for (int i = 0; i < MAX_ROWS; i++) {
				if (getCellState(6, i).equals(BoardState.CellState.UNOCCUPIED)) {
					setCellState(6, i, BoardState.CellState.OPPONENT_OCCUPIED);
					mCurrentMovePosition = MovePosition.getMovePosition(CellState.OPPONENT_OCCUPIED, 6, i);
					break;
				}
			}
		} else if (!pFirstMove.equals(BoardState.Move.OPPONENT_NOMOVE)) {
			sLogger.error("Invalid First move");
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			throw new InvalidMoveException();
		}


		
		setBoardStateString();
		
		buildStateStringForKB();
		
		
		sLogger.debug("Evaluating Board");
		BoardState.GameState lGameState = evaluateGameState(false);
		BitSet lGameStateBitSet = lGameState.getGameStateBitSet();
		for (int i = 0; i < GAME_STATE_SIZE_IN_BITS; i++) {
			mBoardStateInBits.set(GAMESTATE_STARTLOCATION_BIT + i, lGameStateBitSet.get(i));
		}
		

		if (pEvaluation) {
			readMoveScoreFromKnowledgeBase(pLogContext);
		}

		sLogger.trace("Exiting");
	}

	
	
	
	
	public static final int SELF_MOVE_NEXT = 0;
	public static final int OPPONENT_MOVE_NEXT = 1;

	public int whosMove() {
		BoardState.Move lMove = getMove();
		if (lMove.equals(BoardState.Move.OPPONENT_NOMOVE) || lMove.equals(BoardState.Move.OPPONENT_SLOT0) || lMove.equals(BoardState.Move.OPPONENT_SLOT1)
				|| lMove.equals(BoardState.Move.OPPONENT_SLOT2) || lMove.equals(BoardState.Move.OPPONENT_SLOT3) || lMove.equals(BoardState.Move.OPPONENT_SLOT4)
				|| lMove.equals(BoardState.Move.OPPONENT_SLOT5) || lMove.equals(BoardState.Move.OPPONENT_SLOT6)) {
			return SELF_MOVE_NEXT;
		} else {
			return OPPONENT_MOVE_NEXT;
		}

	}

	private String mStateStringForKB = null;
	private String mReciprocalStateStringForKB = null;
	
	
	public String getStateStringForKB () {
		return mStateStringForKB;
	}
	
	public String getReciprocalStateStringForKB () {
		return mReciprocalStateStringForKB;
	}

	private void buildStateStringForKB () {
		String lActionString = mFileIndexString;
		
		if (lActionString.startsWith("0")) {
			lActionString = lActionString.substring(1);
		}

		int lFileMoveStringModulus = lActionString.length() % mKnowledgeBaseFilePool.getActionsPerFile();

		BoardState lBoardStateforMoveFile = this; 
		for (int i = 0; i < lFileMoveStringModulus; i++) {
			lBoardStateforMoveFile = lBoardStateforMoveFile.getParentBoardState();
		}
		
		mStateStringForKB = lBoardStateforMoveFile.getBoardStateString();
		
		
		mReciprocalStateStringForKB = new String();
		for (int i = 0; i < BoardState.MAX_ROWS * BoardState.MAX_COLUMNS; i = i + BoardState.MAX_COLUMNS) {
			String lRowString = mStateStringForKB.substring(i, i + BoardState.MAX_COLUMNS);
			StringBuilder lReverseStringBuilder = new StringBuilder();
			lReverseStringBuilder.append(lRowString);
			lRowString = lReverseStringBuilder.reverse().toString();
			mReciprocalStateStringForKB += lRowString;
		}
		
	}

	

	private String mBoardStateString;
	
	public String getBoardStateString() {
		return mBoardStateString;
	}
	
	
	private void setBoardStateString () {
		if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
			sLogger.trace("Entering");
		}

		String lLine = new String();

		for (int j = 0; j < MAX_ROWS; j++) {
			for (int i = 0; i < MAX_COLUMNS; i++) {
				BitSet lCellState = new BitSet(CELL_STATE_SIZE_IN_BITS);
				for (int k = 0; k < CELL_STATE_SIZE_IN_BITS; k++) {
					lCellState.set(k, mBoardStateInBits.get((j * MAX_COLUMNS + i) * CELL_STATE_SIZE_IN_BITS + k));
				}
				BoardState.CellState lActualCellState = getCellState(lCellState);
				String lOccupationString = lActualCellState.getOccupationString();
				if (lOccupationString.equals(" ")) {
					lLine += "E";
				} else if (lOccupationString.equals("X")) { 
					lLine += "X";
				} else {
					lLine += "O";
				}
			}
		}
		mBoardStateString = lLine;
		if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
			sLogger.trace("Exiting");
		}
		return;
	}
	
	
	
	public BoardState(KnowledgeBaseFilePool pKnowledgeBaseFilePool, BoardState pPreviousBoardState, BoardState.Move pMove, boolean pEvaluation, String pLogContext) throws InvalidMoveException,
			KnowledgeBaseException, ConfigurationException {
		
		
		if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
			sLogger.trace("Entering");
		}
		
		mKnowledgeBaseFilePool = pKnowledgeBaseFilePool;

		mPreviousMoveNode = pPreviousBoardState;

		sLogger.debug("Cloning Board");
		mBoardStateInBits = (BitSet) pPreviousBoardState.getBoardStateInBits().clone();

		BoardState.GameState lGameState = getGameState();
		if (lGameState != BoardState.GameState.CONTINUE) {
			sLogger.info("Game is Over. Move is invalid.");
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			throw new InvalidMoveException();
		}

		mMoveScore = new MoveScore(this);
		mEvaluationThreadInfo = new EvaluationThreadInfo(this);


		//Set up for grafting
		mSelfWinningCombos = (BitSet) pPreviousBoardState.getSelfWinningCombos().clone();
		mOpponentWinningCombos = (BitSet) pPreviousBoardState.getOpponentWinningCombos().clone();

		
		
		mFileIndexString = pPreviousBoardState.getFileIndexString() + pMove.getMoveIntValue();
		if (sLogger.isDebugEnabled()) {
			sLogger.debug("Constructing next fileindexstring to: " + mFileIndexString);
		}
		
		mReciprocalFileIndexString = new String ();
		
		for (int i = 0; i < mFileIndexString.length(); i++) {
			if (mFileIndexString.charAt(i) == '3') {
				mReciprocalFileIndexString += "5";
			} else if (mFileIndexString.charAt(i) == '5') {
				mReciprocalFileIndexString += "3";
			} else if (mFileIndexString.charAt(i) == '2') {
				mReciprocalFileIndexString += "6";
			} else if (mFileIndexString.charAt(i) == '6') {
				mReciprocalFileIndexString += "2";
			} else if (mFileIndexString.charAt(i) == '1') {
				mReciprocalFileIndexString += "7";
			} else if (mFileIndexString.charAt(i) == '7') {
				mReciprocalFileIndexString += "1";
			} else {
				mReciprocalFileIndexString += mFileIndexString.charAt(i);
			}
		}
		
		
		
		
		BoardState.Move lMove = getMove();
		if ((pMove.equals(BoardState.Move.OPPONENT_SLOT0) || pMove.equals(BoardState.Move.OPPONENT_SLOT1) || pMove.equals(BoardState.Move.OPPONENT_SLOT2)
				|| pMove.equals(BoardState.Move.OPPONENT_SLOT3) || pMove.equals(BoardState.Move.OPPONENT_SLOT4) || pMove.equals(BoardState.Move.OPPONENT_SLOT5) || pMove
					.equals(BoardState.Move.OPPONENT_SLOT6))
				&& (lMove.equals(BoardState.Move.OPPONENT_NOMOVE) || lMove.equals(BoardState.Move.OPPONENT_SLOT0)
						|| lMove.equals(BoardState.Move.OPPONENT_SLOT1) || lMove.equals(BoardState.Move.OPPONENT_SLOT2)
						|| lMove.equals(BoardState.Move.OPPONENT_SLOT3) || lMove.equals(BoardState.Move.OPPONENT_SLOT4)
						|| lMove.equals(BoardState.Move.OPPONENT_SLOT5) || lMove.equals(BoardState.Move.OPPONENT_SLOT6))) {
			sLogger.info("Opponent is attempting to move twice");
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			throw new InvalidMoveException();
		}

		if ((pMove.equals(BoardState.Move.SELF_SLOT0) || pMove.equals(BoardState.Move.SELF_SLOT1) || pMove.equals(BoardState.Move.SELF_SLOT2)
				|| pMove.equals(BoardState.Move.SELF_SLOT3) || pMove.equals(BoardState.Move.SELF_SLOT4) || pMove.equals(BoardState.Move.SELF_SLOT5) || pMove
					.equals(BoardState.Move.SELF_SLOT6))
				&& (lMove.equals(BoardState.Move.SELF_NOMOVE) || lMove.equals(BoardState.Move.SELF_SLOT0) || lMove.equals(BoardState.Move.SELF_SLOT1)
						|| lMove.equals(BoardState.Move.SELF_SLOT2) || lMove.equals(BoardState.Move.SELF_SLOT3) || lMove.equals(BoardState.Move.SELF_SLOT4)
						|| lMove.equals(BoardState.Move.SELF_SLOT5) || lMove.equals(BoardState.Move.SELF_SLOT6))) {
			sLogger.info("Self is attempting to move twice");
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			throw new InvalidMoveException();
		}

		boolean lMoveValid = false;
		if (pMove.equals(BoardState.Move.OPPONENT_SLOT0)) {
			for (int i = 0; i < MAX_ROWS; i++) {
				if (getCellState(0, i).equals(BoardState.CellState.UNOCCUPIED)) {
					setCellState(0, i, BoardState.CellState.OPPONENT_OCCUPIED);
					mCurrentMovePosition = MovePosition.getMovePosition(CellState.OPPONENT_OCCUPIED, 0, i);
					lMoveValid = true;
					break;
				}
			}
		} else if (pMove.equals(BoardState.Move.OPPONENT_SLOT1)) {
			for (int i = 0; i < MAX_ROWS; i++) {
				if (getCellState(1, i).equals(BoardState.CellState.UNOCCUPIED)) {
					setCellState(1, i, BoardState.CellState.OPPONENT_OCCUPIED);
					mCurrentMovePosition = MovePosition.getMovePosition(CellState.OPPONENT_OCCUPIED, 1, i);
					lMoveValid = true;
					break;
				}
			}
		} else if (pMove.equals(BoardState.Move.OPPONENT_SLOT2)) {
			for (int i = 0; i < MAX_ROWS; i++) {
				if (getCellState(2, i).equals(BoardState.CellState.UNOCCUPIED)) {
					setCellState(2, i, BoardState.CellState.OPPONENT_OCCUPIED);
					mCurrentMovePosition = MovePosition.getMovePosition(CellState.OPPONENT_OCCUPIED, 2, i);
					lMoveValid = true;
					break;
				}
			}
		} else if (pMove.equals(BoardState.Move.OPPONENT_SLOT3)) {
			for (int i = 0; i < MAX_ROWS; i++) {
				if (getCellState(3, i).equals(BoardState.CellState.UNOCCUPIED)) {
					setCellState(3, i, BoardState.CellState.OPPONENT_OCCUPIED);
					mCurrentMovePosition = MovePosition.getMovePosition(CellState.OPPONENT_OCCUPIED, 3, i);
					lMoveValid = true;
					break;
				}
			}
		} else if (pMove.equals(BoardState.Move.OPPONENT_SLOT4)) {
			for (int i = 0; i < MAX_ROWS; i++) {
				if (getCellState(4, i).equals(BoardState.CellState.UNOCCUPIED)) {
					setCellState(4, i, BoardState.CellState.OPPONENT_OCCUPIED);
					mCurrentMovePosition = MovePosition.getMovePosition(CellState.OPPONENT_OCCUPIED, 4, i);
					lMoveValid = true;
					break;
				}
			}
		} else if (pMove.equals(BoardState.Move.OPPONENT_SLOT5)) {
			for (int i = 0; i < MAX_ROWS; i++) {
				if (getCellState(5, i).equals(BoardState.CellState.UNOCCUPIED)) {
					setCellState(5, i, BoardState.CellState.OPPONENT_OCCUPIED);
					mCurrentMovePosition = MovePosition.getMovePosition(CellState.OPPONENT_OCCUPIED, 5, i);
					lMoveValid = true;
					break;
				}
			}
		} else if (pMove.equals(BoardState.Move.OPPONENT_SLOT6)) {
			for (int i = 0; i < MAX_ROWS; i++) {
				if (getCellState(6, i).equals(BoardState.CellState.UNOCCUPIED)) {
					setCellState(6, i, BoardState.CellState.OPPONENT_OCCUPIED);
					mCurrentMovePosition = MovePosition.getMovePosition(CellState.OPPONENT_OCCUPIED, 6, i);
					lMoveValid = true;
					break;
				}
			}
		} else if (pMove.equals(BoardState.Move.SELF_SLOT0)) {
			for (int i = 0; i < MAX_ROWS; i++) {
				if (getCellState(0, i).equals(BoardState.CellState.UNOCCUPIED)) {
					setCellState(0, i, BoardState.CellState.SELF_OCCUPIED);
					mCurrentMovePosition = MovePosition.getMovePosition(CellState.SELF_OCCUPIED, 0, i);
					lMoveValid = true;
					break;
				}
			}
		} else if (pMove.equals(BoardState.Move.SELF_SLOT1)) {
			for (int i = 0; i < MAX_ROWS; i++) {
				if (getCellState(1, i).equals(BoardState.CellState.UNOCCUPIED)) {
					setCellState(1, i, BoardState.CellState.SELF_OCCUPIED);
					mCurrentMovePosition = MovePosition.getMovePosition(CellState.SELF_OCCUPIED, 1, i);
					lMoveValid = true;
					break;
				}
			}
		} else if (pMove.equals(BoardState.Move.SELF_SLOT2)) {
			for (int i = 0; i < MAX_ROWS; i++) {
				if (getCellState(2, i).equals(BoardState.CellState.UNOCCUPIED)) {
					setCellState(2, i, BoardState.CellState.SELF_OCCUPIED);
					mCurrentMovePosition = MovePosition.getMovePosition(CellState.SELF_OCCUPIED, 2, i);
					lMoveValid = true;
					break;
				}
			}
		} else if (pMove.equals(BoardState.Move.SELF_SLOT3)) {
			for (int i = 0; i < MAX_ROWS; i++) {
				if (getCellState(3, i).equals(BoardState.CellState.UNOCCUPIED)) {
					setCellState(3, i, BoardState.CellState.SELF_OCCUPIED);
					mCurrentMovePosition = MovePosition.getMovePosition(CellState.SELF_OCCUPIED, 3, i);
					lMoveValid = true;
					break;
				}
			}
		} else if (pMove.equals(BoardState.Move.SELF_SLOT4)) {
			for (int i = 0; i < MAX_ROWS; i++) {
				if (getCellState(4, i).equals(BoardState.CellState.UNOCCUPIED)) {
					setCellState(4, i, BoardState.CellState.SELF_OCCUPIED);
					mCurrentMovePosition = MovePosition.getMovePosition(CellState.SELF_OCCUPIED, 4, i);
					lMoveValid = true;
					break;
				}
			}
		} else if (pMove.equals(BoardState.Move.SELF_SLOT5)) {
			for (int i = 0; i < MAX_ROWS; i++) {
				if (getCellState(5, i).equals(BoardState.CellState.UNOCCUPIED)) {
					setCellState(5, i, BoardState.CellState.SELF_OCCUPIED);
					mCurrentMovePosition = MovePosition.getMovePosition(CellState.SELF_OCCUPIED, 5, i);
					lMoveValid = true;
					break;
				}
			}
		} else if (pMove.equals(BoardState.Move.SELF_SLOT6)) {
			for (int i = 0; i < MAX_ROWS; i++) {
				if (getCellState(6, i).equals(BoardState.CellState.UNOCCUPIED)) {
					setCellState(6, i, BoardState.CellState.SELF_OCCUPIED);
					mCurrentMovePosition = MovePosition.getMovePosition(CellState.SELF_OCCUPIED, 6, i);
					lMoveValid = true;
					break;
				}
			}
		}

		if (!lMoveValid) {
			sLogger.debug("Move is invalid.");
			throw new InvalidMoveException();
		}

		sLogger.debug("Setting Move");
		BitSet lMoveBitSet = pMove.getMoveBitSet();
		for (int i = 0; i < MOVE_STATE_SIZE_IN_BITS; i++) {
			mBoardStateInBits.set(MOVE_STARTLOCATION_BIT + i, lMoveBitSet.get(i));
		}


		setBoardStateString();

		buildStateStringForKB();
		
		
//		sLogger.error("SelfWinningCombos=" + mSelfWinningCombos.cardinality());
//		sLogger.error("OpponentWinningCombos=" + mOpponentWinningCombos.cardinality());
//		sLogger.error("CurrentMovePosition=" + mCurrentMovePosition.getMovePositionString());
//		sLogger.error("Filestring=" + mFileIndexString);
		

		
		
		
		sLogger.debug("Evaluating Board");
		lGameState = evaluateGameState(false);
		BitSet lGameStateBitSet = lGameState.getGameStateBitSet();
		for (int i = 0; i < GAME_STATE_SIZE_IN_BITS; i++) {
			mBoardStateInBits.set(GAMESTATE_STARTLOCATION_BIT + i, lGameStateBitSet.get(i));
		}

		if (pEvaluation) {
			readMoveScoreFromKnowledgeBase(pLogContext);
		}
		
		
		
		
		


		if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
			sLogger.trace("Exiting");
		}
	}

	public void readMoveScoreFromKnowledgeBase(String pLogContext) throws KnowledgeBaseException, ConfigurationException, KnowledgeBaseException,
			InvalidMoveException {
		if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
			sLogger.trace("Entering");
		}

		sLogger.debug("Creating and starting Knowledge Base File Access Thread for reading");
		KnowledgeBaseFileAccessReadTask lKnowledgeBaseFileAccessReadTask = new KnowledgeBaseFileAccessReadTask(mKnowledgeBaseFilePool, mStateStringForKB, mFileIndexString, pLogContext);
		lKnowledgeBaseFileAccessReadTask.executeTask();
		if (!lKnowledgeBaseFileAccessReadTask.isTransactionSuccessful() || !lKnowledgeBaseFileAccessReadTask.isTransactionFinished()) {
			sLogger.error("Knowledge Base Error occurred!");
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			throw new KnowledgeBaseException();
		} else if (lKnowledgeBaseFileAccessReadTask.isScoreFound()) {
			sLogger.debug("Score is found, setting to BoardState");
			mMoveScore.setMoveScore(lKnowledgeBaseFileAccessReadTask.getBoardScore());
		} else {
			sLogger.debug("Score is not found, writing from Current Board State");
			sLogger.debug("Creating and starting Knowledge Base File Access Thread for writing");
			
			KnowledgeBaseFileAccessWriteTask lKnowledgeBaseFileAccessWriteTask = new KnowledgeBaseFileAccessWriteTask(mKnowledgeBaseFilePool, mStateStringForKB, mFileIndexString,
					mMoveScore.getMoveScore(), pLogContext);
			lKnowledgeBaseFileAccessWriteTask.executeTask();
			if (!lKnowledgeBaseFileAccessWriteTask.isTransactionSuccessful() || !lKnowledgeBaseFileAccessWriteTask.isTransactionFinished()) {
				sLogger.error("Knowledge Base Error occurred!");
				if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
					sLogger.trace("Exiting");
				}
				throw new KnowledgeBaseException();
			}
			
			if (!mFileIndexString.equals(mReciprocalFileIndexString)) {
				lKnowledgeBaseFileAccessWriteTask = new KnowledgeBaseFileAccessWriteTask(mKnowledgeBaseFilePool, mReciprocalStateStringForKB, mReciprocalFileIndexString, mMoveScore.getMoveScore(),
						pLogContext);
				lKnowledgeBaseFileAccessWriteTask.executeTask();
				if (!lKnowledgeBaseFileAccessWriteTask.isTransactionSuccessful() || !lKnowledgeBaseFileAccessWriteTask.isTransactionFinished()) {
					sLogger.error("Knowledge Base Error occurred!");
					if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
						sLogger.trace("Exiting");
					}
					throw new KnowledgeBaseException();
				}
			}
			
			
		}
		if (sLogger.isDebugEnabled()) {
			sLogger.debug("Move: " + mFileIndexString + " Knowledge Base Move Score: " + mMoveScore.getMoveScore());
		}
		if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
			sLogger.trace("Exiting");
		}
	}

	public MoveScore getMoveScore() {
		if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
			sLogger.trace("Entering");
		}
		if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
			sLogger.trace("Exiting");
		}
		return mMoveScore;
	}
	
	
	

	public void writeMoveScoreToKnowledge(String pLogContext) throws KnowledgeBaseException, ConfigurationException, KnowledgeBaseException,
			InvalidMoveException {
		if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
			sLogger.trace("Entering");
		}

		sLogger.debug("Creating and starting Knowledge Base File Access Thread for writing");
		KnowledgeBaseFileAccessWriteTask lKnowledgeBaseFileAccessWriteTask = new KnowledgeBaseFileAccessWriteTask(mKnowledgeBaseFilePool, mStateStringForKB, mFileIndexString, mMoveScore.getMoveScore(),
				pLogContext);
		lKnowledgeBaseFileAccessWriteTask.executeTask();
		if (!lKnowledgeBaseFileAccessWriteTask.isTransactionSuccessful() || !lKnowledgeBaseFileAccessWriteTask.isTransactionFinished()) {
			sLogger.error("Knowledge Base Error occurred!");
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			throw new KnowledgeBaseException();
		}
		
		if (!mFileIndexString.equals(mReciprocalFileIndexString)) {
			lKnowledgeBaseFileAccessWriteTask = new KnowledgeBaseFileAccessWriteTask(mKnowledgeBaseFilePool, mReciprocalStateStringForKB, mReciprocalFileIndexString, mMoveScore.getMoveScore(),
					pLogContext);
			lKnowledgeBaseFileAccessWriteTask.executeTask();
			if (!lKnowledgeBaseFileAccessWriteTask.isTransactionSuccessful() || !lKnowledgeBaseFileAccessWriteTask.isTransactionFinished()) {
				sLogger.error("Knowledge Base Error occurred!");
				if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
					sLogger.trace("Exiting");
				}
				throw new KnowledgeBaseException();
			}
		}
		
		
		if (sLogger.isDebugEnabled()) {
			sLogger.debug("Move: " + mFileIndexString + " Knowledge Base Move Score: " + mMoveScore.getMoveScore());
			sLogger.debug("Move: " + mReciprocalFileIndexString + " Knowledge Base Move Score: " + mMoveScore.getMoveScore());
		}
		if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
			sLogger.trace("Exiting");
		}
	}

	public void logBoardState(Level pLevel) {
		if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
			sLogger.trace("Entering");
		}
		if (slogBoardStateActive) {
			String lBoardStateLog = new String();

			String lLine = new String();
			lLine = "---------------------------------\n";
			lLine += "\n";
			lBoardStateLog = lLine;

			for (int j = 0; j < MAX_ROWS; j++) {
				lLine = "|                               |\n";
				lLine += "|";
				for (int i = 0; i < MAX_COLUMNS; i++) {
					BitSet lCellState = new BitSet(CELL_STATE_SIZE_IN_BITS);
					for (int k = 0; k < CELL_STATE_SIZE_IN_BITS; k++) {
						lCellState.set(k, mBoardStateInBits.get((j * MAX_COLUMNS + i) * CELL_STATE_SIZE_IN_BITS + k));
					}

					BoardState.CellState lActualCellState = getCellState(lCellState);
					lLine += "   ";
					lLine += lActualCellState.getOccupationString();
				}
				lLine += "   |\n";
				lBoardStateLog = lLine + lBoardStateLog;
			}
			lBoardStateLog = "Current Board Layout:\n" + lBoardStateLog;

			sLogger.log(pLevel, lBoardStateLog);
		}
		if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
			sLogger.trace("Exiting");
		}
		return;
	}


	public BoardState.GameState getGameState() {
		if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
			sLogger.trace("Entering");
		}
		BitSet lGameStateBitSet = new BitSet(GAME_STATE_SIZE_IN_BITS);

		for (int i = 0; i < GAME_STATE_SIZE_IN_BITS; i++) {
			lGameStateBitSet.set(i, mBoardStateInBits.get(GAMESTATE_STARTLOCATION_BIT + i));
		}

		if (lGameStateBitSet.equals(BoardState.GameState.CONTINUE.getGameStateBitSet())) {
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			return BoardState.GameState.CONTINUE;
		} else if (lGameStateBitSet.equals(BoardState.GameState.WIN.getGameStateBitSet())) {
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			return BoardState.GameState.WIN;
		} else if (lGameStateBitSet.equals(BoardState.GameState.LOSS.getGameStateBitSet())) {
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			return BoardState.GameState.LOSS;
		} else {
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			return BoardState.GameState.DRAW;
		}

	}

	public BoardState.Move getMove() {
		if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
			sLogger.trace("Entering");
		}
		BitSet lMoveBitSet = new BitSet(MOVE_STATE_SIZE_IN_BITS);

		for (int i = 0; i < MOVE_STATE_SIZE_IN_BITS; i++) {
			lMoveBitSet.set(i, mBoardStateInBits.get(MOVE_STARTLOCATION_BIT + i));
		}

		if (lMoveBitSet.equals(BoardState.Move.OPPONENT_SLOT0.getMoveBitSet())) {
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			return BoardState.Move.OPPONENT_SLOT0;
		} else if (lMoveBitSet.equals(BoardState.Move.OPPONENT_SLOT1.getMoveBitSet())) {
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			return BoardState.Move.OPPONENT_SLOT1;
		} else if (lMoveBitSet.equals(BoardState.Move.OPPONENT_SLOT2.getMoveBitSet())) {
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			return BoardState.Move.OPPONENT_SLOT2;
		} else if (lMoveBitSet.equals(BoardState.Move.OPPONENT_SLOT3.getMoveBitSet())) {
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			return BoardState.Move.OPPONENT_SLOT3;
		} else if (lMoveBitSet.equals(BoardState.Move.OPPONENT_SLOT4.getMoveBitSet())) {
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			return BoardState.Move.OPPONENT_SLOT4;
		} else if (lMoveBitSet.equals(BoardState.Move.OPPONENT_SLOT5.getMoveBitSet())) {
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			return BoardState.Move.OPPONENT_SLOT5;
		} else if (lMoveBitSet.equals(BoardState.Move.OPPONENT_SLOT6.getMoveBitSet())) {
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			return BoardState.Move.OPPONENT_SLOT6;
		} else if (lMoveBitSet.equals(BoardState.Move.SELF_SLOT0.getMoveBitSet())) {
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			return BoardState.Move.SELF_SLOT0;
		} else if (lMoveBitSet.equals(BoardState.Move.SELF_SLOT1.getMoveBitSet())) {
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			return BoardState.Move.SELF_SLOT1;
		} else if (lMoveBitSet.equals(BoardState.Move.SELF_SLOT2.getMoveBitSet())) {
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			return BoardState.Move.SELF_SLOT2;
		} else if (lMoveBitSet.equals(BoardState.Move.SELF_SLOT3.getMoveBitSet())) {
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			return BoardState.Move.SELF_SLOT3;
		} else if (lMoveBitSet.equals(BoardState.Move.SELF_SLOT4.getMoveBitSet())) {
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			return BoardState.Move.SELF_SLOT4;
		} else if (lMoveBitSet.equals(BoardState.Move.SELF_SLOT5.getMoveBitSet())) {
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			return BoardState.Move.SELF_SLOT5;
		} else if (lMoveBitSet.equals(BoardState.Move.SELF_SLOT6.getMoveBitSet())) {
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			return BoardState.Move.SELF_SLOT6;
		} else if (lMoveBitSet.equals(BoardState.Move.SELF_NOMOVE.getMoveBitSet())) {
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			return BoardState.Move.SELF_NOMOVE;
		} else {
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			return BoardState.Move.OPPONENT_NOMOVE;
		}
	}

	public BoardState.CellState getCellState(BitSet pBitSet) {
		if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
			sLogger.trace("Entering");
		}

		if (pBitSet.equals(CellState.OPPONENT_OCCUPIED.getCellStateBitSet())) {
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			return CellState.OPPONENT_OCCUPIED;
		} else if (pBitSet.equals(CellState.SELF_OCCUPIED.getCellStateBitSet())) {
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			return CellState.SELF_OCCUPIED;
		} else {
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			return CellState.UNOCCUPIED;
		}
	}

	public BoardState.CellState getCellState(int lColumn, int lRow) {
		if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
			sLogger.trace("Entering");
		}

		BitSet lBitSet = new BitSet(CELL_STATE_SIZE_IN_BITS);

		for (int i = 0; i < CELL_STATE_SIZE_IN_BITS; i++) {
			lBitSet.set(i, mBoardStateInBits.get((lRow * MAX_COLUMNS + lColumn) * CELL_STATE_SIZE_IN_BITS + i));
		}

		if (lBitSet.equals(CellState.OPPONENT_OCCUPIED.getCellStateBitSet())) {
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			return CellState.OPPONENT_OCCUPIED;
		} else if (lBitSet.equals(CellState.SELF_OCCUPIED.getCellStateBitSet())) {
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			return CellState.SELF_OCCUPIED;
		} else {
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			return CellState.UNOCCUPIED;
		}
	}


	
	
	public void setCellState(int lColumn, int lRow, BoardState.CellState pNewCellState) {
		if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
			sLogger.trace("Entering");
		}

		for (int i = 0; i < CELL_STATE_SIZE_IN_BITS; i++) {
			mBoardStateInBits.set((lRow * MAX_COLUMNS + lColumn) * CELL_STATE_SIZE_IN_BITS + i, pNewCellState.getCellStateBitSet().get(i));
		}

		if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
			sLogger.trace("Exiting");
		}
	}
	
	
	
	
	

	private BoardState.GameState evaluateGameState(boolean lWriteGameStateToKnowledgeBase) throws InvalidMoveException, KnowledgeBaseException {
		if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
			sLogger.trace("Entering");
		}



		
		
		//New Code
		int lNewSelfWinningMoves = 0;
		int lNewOpponentWinningMoves = 0;
		int lNewMoveScore = 0;
		
		boolean lNewFoundWinner = false;
		boolean lNewFoundLoser = false;
		boolean lNewFoundDraw = false;
		
		
		
		
		
		
		if (mCurrentMovePosition == null) {
			lNewSelfWinningMoves = mSelfWinningCombos.cardinality();
			lNewOpponentWinningMoves = mOpponentWinningCombos.cardinality();
		} else {
			ArrayList <WinningCombo> lWinningCombosToCheck = mCurrentMovePosition.getWinningCombosToCheckForWin();

			for (Iterator<WinningCombo> lIterator = lWinningCombosToCheck.iterator(); lIterator.hasNext();) {
				WinningCombo lWinningComboToCheck = lIterator.next();
				
				ArrayList <MovePosition> lPositionsToCheck = lWinningComboToCheck.getRequiredMovePositions();
				
				boolean lFoundWin = true;
				for (Iterator<MovePosition> lIterator2 = lPositionsToCheck.iterator(); lIterator2.hasNext();) {
					MovePosition lMovePositionToCheck = lIterator2.next();
					
					if (getCellState(lMovePositionToCheck.getColumn(), lMovePositionToCheck.getRow()).getOccupationInt() != lMovePositionToCheck.getCellState().getOccupationInt()) {
						lFoundWin = false;
						break;
					}
				}			
				if (lFoundWin && mCurrentMovePosition.isSelfMove()) {
					lNewFoundWinner = true;
					break;
				} else if (lFoundWin && mCurrentMovePosition.isOpponentMove()){
					lNewFoundLoser = true;
					break;
				}
			}		
			
			
			if (!lNewFoundWinner && !lNewFoundLoser) {
				ArrayList <WinningCombo> lAffectedWinningCombos = mCurrentMovePosition.getAffectedWinningCombos();
				
				for (Iterator<WinningCombo> lIterator = lAffectedWinningCombos.iterator(); lIterator.hasNext();) {
					WinningCombo lAffectedWinningCombo = lIterator.next();

					if (lAffectedWinningCombo.isOpponentWinningCombo()) {
						mOpponentWinningCombos.set(lAffectedWinningCombo.getWinningComboBitPosition(),false);
					} else {
						mSelfWinningCombos.set(lAffectedWinningCombo.getWinningComboBitPosition(),false);
					}

				}
				lNewSelfWinningMoves = mSelfWinningCombos.cardinality();
				lNewOpponentWinningMoves = mOpponentWinningCombos.cardinality();
				lNewMoveScore = mSelfWinningCombos.cardinality() - mOpponentWinningCombos.cardinality();
				
				if (lNewSelfWinningMoves == 0 && lNewOpponentWinningMoves == 0) {
					lNewFoundDraw = true;
				}
			}
		}
		


		
		if (lNewFoundWinner) {
			mMoveScore.setMoveScore(SCORE_WINNING_MOVE);
			if (sLogger.isDebugEnabled()) {
				sLogger.error("Moves:" + mFileIndexString + " MoveScore:" + mMoveScore.getMoveScore());
				sLogger.error("X wins, O loses! Game Over");
			}
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			return BoardState.GameState.WIN;
		} else if (lNewFoundLoser) {
			mMoveScore.setMoveScore(SCORE_LOSING_MOVE);
			if (sLogger.isDebugEnabled()) {
				sLogger.error("Moves:" + mFileIndexString + " MoveScore:" + mMoveScore.getMoveScore());
				sLogger.error("O wins, X loses! Game Over");
			}
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			return BoardState.GameState.LOSS;
		} else if ((mFileIndexString.startsWith("0") && mFileIndexString.length() <= 42) || (!mFileIndexString.startsWith("0") && mFileIndexString.length() <= 41)) {
			if (lNewOpponentWinningMoves == 0 && lNewSelfWinningMoves == 0) {
				//No Valid Winning Moves left
				mMoveScore.setMoveScore(SCORE_DRAW_MOVE);
				if (sLogger.isDebugEnabled()) {
					sLogger.debug("Moves:" + mFileIndexString + " MoveScore:" + mMoveScore.getMoveScore());
					sLogger.debug("Game Is a Draw! Game Over");
				}
				if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
					sLogger.trace("Exiting");
				}
				return BoardState.GameState.DRAW;
			}
			mMoveScore.setMoveScore((byte) (lNewMoveScore));
			if (sLogger.isDebugEnabled()) {
				sLogger.debug("Opponent Grid Score:" + lNewOpponentWinningMoves);
				sLogger.debug("Self Grid Score:" + lNewSelfWinningMoves);
				sLogger.debug("Moves:" + mFileIndexString + " MoveScore:" + mMoveScore.getMoveScore());
				sLogger.debug("Game Continues!");
			}
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			return BoardState.GameState.CONTINUE;
		} else {
			mMoveScore.setMoveScore(SCORE_DRAW_MOVE);
			if (sLogger.isDebugEnabled()) {
				sLogger.debug("Moves:" + mFileIndexString + " MoveScore:" + mMoveScore.getMoveScore());
				sLogger.debug("Game Is a Draw! Game Over");
			}
			if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
				sLogger.trace("Exiting");
			}
			return BoardState.GameState.DRAW;
		}


	
	}

	public BitSet getBoardStateInBits() {
		return mBoardStateInBits;
	}

	public static synchronized boolean isLogBoardStateActive() {
		return slogBoardStateActive;
	}

	public static synchronized void setLogBoardStateActive(boolean pSlogBoardStateActive) {
		slogBoardStateActive = pSlogBoardStateActive;
	}
	
	public KnowledgeBaseFilePool getKnowledgeBaseFilePool () {
		return mKnowledgeBaseFilePool;
	}

}