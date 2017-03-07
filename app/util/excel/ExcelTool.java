package util.excel;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;


public class ExcelTool {

	public void exportExcel(OutputStream os, List list, String[] columnNames,String[] columns,String sheetName ) {
		WritableWorkbook wwb = null;
		OutputStream fos = os;
		try {
			String yearString = "2015";
			if(sheetName!=null && !"".equals(sheetName))
			{
				String[] dateStrings = sheetName.split("-");
				if(dateStrings!=null && dateStrings.length>1)
				{
					yearString = dateStrings[0];
				}
			}
			wwb = Workbook.createWorkbook(fos);
			WritableSheet ws = wwb.createSheet(sheetName, 10);
			
			WritableFont mainHeaderFont = new WritableFont(WritableFont.ARIAL,15,WritableFont.BOLD,false,UnderlineStyle.NO_UNDERLINE,Colour.BLACK);
			WritableCellFormat mainHeaderFormat = new WritableCellFormat(mainHeaderFont);
			mainHeaderFormat.setAlignment(Alignment.CENTRE);
			mainHeaderFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			mainHeaderFormat.setWrap(false);
			mainHeaderFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN); 
			
			WritableFont infoHheaderFont = new WritableFont(WritableFont.ARIAL,15,WritableFont.BOLD,false,UnderlineStyle.NO_UNDERLINE,Colour.BLACK);
			WritableCellFormat infoHeaderFormat = new WritableCellFormat(infoHheaderFont);
			infoHeaderFormat.setAlignment(Alignment.CENTRE);
			infoHeaderFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			infoHeaderFormat.setWrap(false);
			infoHeaderFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN); 
			
			
			//2013年度HACMP双机集群和GPFS日常巡检服务
			WritableFont viceHeaderFont = new WritableFont(WritableFont.ARIAL,10,WritableFont.BOLD,false,UnderlineStyle.NO_UNDERLINE,Colour.BLUE);
			WritableCellFormat viceHeaderFormat = new WritableCellFormat(viceHeaderFont);
			viceHeaderFormat.setAlignment(Alignment.CENTRE);
			viceHeaderFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			viceHeaderFormat.setWrap(false);
			viceHeaderFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN); 
			viceHeaderFormat.setBackground(jxl.format.Colour.YELLOW);
			
			WritableFont wf = new WritableFont(WritableFont.ARIAL, 10,WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,Colour.BLACK);
			WritableCellFormat wcf1 = new WritableCellFormat(wf);
			wcf1.setWrap(false);
//			wcf1.setVerticalAlignment(VerticalAlignment.CENTRE);
//			wcf1.setAlignment(Alignment.CENTRE);
			
			ws.mergeCells(0, 0, columns.length, 0);
			Label title = new Label(0,0,yearString+"年度HACMP双机集群和GPFS日常巡检服务",mainHeaderFormat);
			ws.addCell(title);
			
			Label patrolPerson = new Label(1,1,"巡检人：",infoHeaderFormat);
			ws.addCell(patrolPerson);
			Label dateTile = new Label(4,1,"日期：",infoHeaderFormat);
			ws.addCell(dateTile);
			Label dateInfo = new Label(5,1,sheetName,infoHeaderFormat);
			ws.addCell(dateInfo);
			Label serverNum = new Label(7,1,"服务编号：",infoHeaderFormat);
			ws.addCell(serverNum);
			
			ws.setRowView(0, 500);// 标题行高
			for (int i = 0; i < columns.length; i++) {
				if(columns[i]!=null && !"".equals(columns[i]))
				{
					ws.addCell(new Label(i, 2, columnNames[i], viceHeaderFormat));
					if(i==0)
					{
						ws.setColumnView(i, 5);
					}
					else {
						ws.setColumnView(i, 20);
					}
				}
			}
			for (int i = 0; i < list.size(); i++) {
				HashMap map = (HashMap) list.get(i);
				for (int j = 0; j < columns.length; j++) {
					if (map.get(columns[j]) != null
							&& !map.get(columns[j]).toString().equals("")) {
						String value = map.get(columns[j]).toString();
						if(value!=null && !"".equals(value))
						{
							ws.addCell(new Label(j, i + 3, value, wcf1));
						}
					}
				}
			}
			wwb.write();
			wwb.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}
}
