package util;

public class XMLWordUtil {
	public static final Integer TABLE_WIDTH=9520;
	public static final Integer TABLE_WIDTH_THREE=3173;
	public static final Integer TABLE_WIDTH_FOUR=2130;
	public static final Integer TABLE_WIDTH_FIVE=1740;
	public static final Integer TABLE_WIDTH_SIX=1420;
	public static final Integer TABLE_WIDTH_SEVER=1217;
	public static final Integer TABLE_WIDTH_EIGHT=1065;
	
	public static void getTableHead(StringBuffer str,int col){
		str.append("<w:tbl>");
        str.append("<w:tblPr>");
        str.append("<w:tblW w:w=\"0\" w:type=\"auto\"/>");
        str.append("<w:tblBorders>");
        str.append("<w:top w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"000000\"/>");
        str.append("<w:left w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"000000\"/>");
        str.append("<w:bottom w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"000000\"/>");
        str.append("<w:right w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"000000\"/>");
        str.append("<w:insideH w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"000000\"/>");        
        str.append("<w:insideV w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"000000\"/>");
        str.append("</w:tblBorders>");
        str.append("<w:tblLook w:val=\"04A0\"/>");
        str.append("</w:tblPr>");
        str.append("<w:tblGrid>");
        int tdWidth=2130;
		switch (col) {
		case 3:
			tdWidth=TABLE_WIDTH_THREE;
			break;
		case 4:
			tdWidth=TABLE_WIDTH_FOUR;
			break;
		case 5:
			tdWidth=TABLE_WIDTH_FIVE;
			break;
		case 6:
			tdWidth=TABLE_WIDTH_SIX;
			break;
		case 7:
			tdWidth=TABLE_WIDTH_SEVER;
			break;
		case 8:
			tdWidth=TABLE_WIDTH_EIGHT;
			break;
		default:
			tdWidth=TABLE_WIDTH_THREE;
			break;
		}
		System.out.println(tdWidth);
		for(int i=0;i<col;i++){
			 str.append("<w:gridCol w:w=\""+tdWidth+"\"/>");
		}
        str.append("</w:tblGrid>");
		
	}
	public static void getTableTail(StringBuffer str){
		 str.append("</w:tbl>");
		
	}
	public static void getTrHead(StringBuffer str){
		str.append("<w:tr wsp:rsidR=\"00636587\" wsp:rsidRPr=\"004E0313\" wsp:rsidTr=\"004E0313\">");
	}
	public static void getTrTail(StringBuffer str){
		str.append("</w:tr>");
	}
	public static void getTd(StringBuffer str,String data){
		 str.append("<w:tc>");
	     str.append("<w:tcPr><w:tcW w:w=\"2130\" w:type=\"dxa\"/></w:tcPr>");
	     str.append("<w:p wsp:rsidR=\"00636587\" wsp:rsidRPr=\"004E0313\" wsp:rsidRDefault=\"00636587\">");       
	     str.append("<w:pPr>");
	     str.append("<w:rPr><w:rFonts w:ascii=\"Calibri\" w:fareast=\"宋体\" w:h-ansi=\"Calibri\" w:cs=\"Times New Roman\"/></w:rPr>");
	     str.append("</w:pPr>");
	     str.append("<w:r wsp:rsidRPr=\"004E0313\">");
	     str.append("<w:rPr>");
	     str.append("<w:rFonts w:ascii=\"Calibri\" w:fareast=\"宋体\" w:h-ansi=\"Calibri\" w:cs=\"Times New Roman\"/>");
	     str.append("<wx:font wx:val=\"宋体\"/>");
	     str.append("</w:rPr>");
	     str.append("<w:t>"+data+"</w:t>");
	     str.append("</w:r>");
	     str.append("</w:p>");
	     str.append("</w:tc>");
	}
	

}
