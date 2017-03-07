package util.excel;

import java.awt.Color;
import java.util.List;

import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ExcelInput {

	public void commonality(WritableWorkbook wwb, List<String[]> list,
			String[] columnNames, String headLine, String sheetName,
			String username) {

		try {
			WritableSheet ws = wwb.createSheet(sheetName, 0);
			ws.getSettings().setPrintGridLines(true);
			// 标题样式
			WritableFont mainHeaderFont = new WritableFont(WritableFont.ARIAL,
					10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,
					Colour.BLACK);
			WritableCellFormat mainHeaderFormat = new WritableCellFormat(
					mainHeaderFont);// 生成格式
			mainHeaderFormat.setAlignment(Alignment.CENTRE);
			mainHeaderFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			mainHeaderFormat.setWrap(false);
			mainHeaderFormat.setBorder(jxl.format.Border.ALL,
					jxl.format.BorderLineStyle.THIN);

			// 副标题格式
			WritableFont infoHheaderFont = new WritableFont(WritableFont.ARIAL,
					8, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,
					Colour.BLACK);
			WritableCellFormat infoHeaderFormat = new WritableCellFormat(
					infoHheaderFont);
			infoHeaderFormat.setAlignment(Alignment.LEFT);
			infoHeaderFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			infoHeaderFormat.setWrap(false);
			infoHeaderFormat.setBorder(jxl.format.Border.ALL,
					jxl.format.BorderLineStyle.THIN);

			// 正文格式
			WritableFont viceHeaderFont = new WritableFont(WritableFont.ARIAL,
					8, WritableFont.NO_BOLD, false,
					UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
			WritableCellFormat viceHeaderFormat = new WritableCellFormat(
					viceHeaderFont);
			viceHeaderFormat.setAlignment(Alignment.LEFT);
			viceHeaderFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			viceHeaderFormat.setWrap(false);
			viceHeaderFormat.setBorder(jxl.format.Border.ALL,
					jxl.format.BorderLineStyle.THIN);
			// viceHeaderFormat.setBackground(jxl.format.Colour.YELLOW);//背景颜色

			// 序号格式
			WritableCellFormat integerFormat = new WritableCellFormat(
					NumberFormats.INTEGER);
			integerFormat.setAlignment(Alignment.LEFT);
			integerFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			integerFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
			Number numaa = null;
			// NumberFormat numHeaderFont = new NumberFormat("#.0");
			// WritableCellFormat numberFormat = new
			// WritableCellFormat(numHeaderFont);
			// numberFormat.setAlignment(Alignment.LEFT);
			// numberFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			// numberFormat.setWrap(false);
			// numberFormat.setBorder(jxl.format.Border.ALL,
			// jxl.format.BorderLineStyle.THIN);

			// 正文数据格式 背景颜色 yellow
			WritableFont dataYellowFont = new WritableFont(
					WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD,
					false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
			WritableCellFormat dataYellowFormat = new WritableCellFormat(
					dataYellowFont);
			dataYellowFormat.setAlignment(Alignment.LEFT);
			dataYellowFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			dataYellowFormat.setWrap(false);
			dataYellowFormat.setBackground(Colour.YELLOW);
			dataYellowFormat.setBorder(jxl.format.Border.ALL,
					jxl.format.BorderLineStyle.THIN);
			// 正文数据格式 背景颜色 red
			WritableFont dataRedFont = new WritableFont(
					WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD,
					false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
			WritableCellFormat dataRedFormat = new WritableCellFormat(
					dataRedFont);
			dataRedFormat.setAlignment(Alignment.LEFT);
			dataRedFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			dataRedFormat.setWrap(false);
			dataRedFormat.setBackground(Colour.RED);
			dataRedFormat.setBorder(jxl.format.Border.ALL,
					jxl.format.BorderLineStyle.THIN);
			// 正文数据格式 背景颜色 ORANGE
			WritableFont dataOrangeFont = new WritableFont(
					WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD,
					false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
			WritableCellFormat dataOrangeFormat = new WritableCellFormat(
					dataOrangeFont);
			dataOrangeFormat.setAlignment(Alignment.LEFT);
			dataOrangeFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			dataOrangeFormat.setWrap(false);
			dataOrangeFormat.setBackground(Colour.ORANGE);
			dataOrangeFormat.setBorder(jxl.format.Border.ALL,
					jxl.format.BorderLineStyle.THIN);
			// 正文数据格式 背景颜色 PINK
			WritableFont dataPinkFont = new WritableFont(
					WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD,
					false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
			WritableCellFormat dataPinkFormat = new WritableCellFormat(
					dataPinkFont);
			dataPinkFormat.setAlignment(Alignment.LEFT);
			dataPinkFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			dataPinkFormat.setWrap(false);
			Color colorpink = Color.decode("#FFC7CE");
			wwb.setColourRGB(Colour.PINK, colorpink.getRed(),
					colorpink.getGreen(), colorpink.getBlue());
			dataPinkFormat.setBackground(Colour.PINK);
			dataPinkFormat.setBorder(jxl.format.Border.ALL,
					jxl.format.BorderLineStyle.THIN);
			// 正文数据格式 背景颜色 BULE
			WritableFont dataBlueFont = new WritableFont(
					WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD,
					false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
			WritableCellFormat dataBlueFormat = new WritableCellFormat(
					dataBlueFont);
			dataBlueFormat.setAlignment(Alignment.LEFT);
			dataBlueFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			dataBlueFormat.setWrap(false);
			dataBlueFormat.setBackground(Colour.BLUE);
			dataBlueFormat.setBorder(jxl.format.Border.ALL,
					jxl.format.BorderLineStyle.THIN);
			// 正文数据格式 背景颜色 GREEN
			WritableFont dataGreenFont = new WritableFont(
					WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD,
					false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
			WritableCellFormat dataGreenFormat = new WritableCellFormat(
					dataGreenFont);
			dataGreenFormat.setAlignment(Alignment.LEFT);
			dataGreenFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			dataGreenFormat.setWrap(false);
			dataGreenFormat.setBackground(Colour.GREEN);
			dataGreenFormat.setBorder(jxl.format.Border.ALL,
					jxl.format.BorderLineStyle.THIN);

			// 标题
			ws.mergeCells(0, 0, columnNames.length - 1, 0);
			Label title = new Label(0, 0, headLine, mainHeaderFormat);
			ws.addCell(title);

			// 巡检人
			ws.mergeCells(0, 1, columnNames.length - 1, 1);
			Label patrolPerson = new Label(0, 1, "巡检人：" + username,
					infoHeaderFormat);
			ws.addCell(patrolPerson);

			// // 巡检内容、巡检描述或方法、填写说明、序号、服务器ip地址。
			// ws.mergeCells(0, 2, 1, 2);
			// Label patrolcontent = new Label(0, 2, "巡检内容", infoHeaderFormat);
			// ws.addCell(patrolcontent);
			// ws.mergeCells(0, 3, 1, 3);
			// Label patrolmethod = new Label(0, 3, "巡检描述或方法",
			// infoHeaderFormat);
			// ws.addCell(patrolmethod);
			// ws.mergeCells(0, 4, 1, 4);
			// Label patrolexplain = new Label(0, 4, "填写说明", infoHeaderFormat);
			// ws.addCell(patrolexplain);
			// Label patrolsequence = new Label(0, 5, "序号", infoHeaderFormat);
			// ws.addCell(patrolsequence);
			// Label patrollocation = new Label(1, 5, addressName,
			// infoHeaderFormat);
			// ws.addCell(patrollocation);
			// copyName

			ws.setRowView(0, 500);// 标题行高
			// 巡检内容行
			for (int i = 0; i < columnNames.length; i++) {
				if (columnNames[i] != null && !"".equals(columnNames[i])) {
					ws.addCell(new Label(i, 2, columnNames[i], infoHeaderFormat));
					if (columnNames[i].length() > 8) {
						ws.setColumnView(i, 20);
					} else {
						ws.setColumnView(i, 10);
					}
				}
			}

			ws.setRowView(0, 500);// 标题行高

			// 序号
			int length = 1;
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					String[] listStr = (String[]) list.get(i);
					if (listStr != null && !"".equals(listStr)) {
						ws.addCell(new Number(0, i + 3, length, integerFormat));
						ws.setColumnView(0, 10);
						length++;
					}
				}
			}

			// 正文
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					String[] listStr = (String[]) list.get(i);
					if (listStr != null && !"".equals(listStr)) {
						for (int m = 0; m < ((String[]) list.get(i)).length; m++) {
							String value = listStr[m];
							if (value.matches("[0-9]+") && !value.matches("0")
									&& m != 0) {
								ws.addCell(new Number(m + 1, i + 3, Integer
										.parseInt(value), dataPinkFormat));
							} else if (value.matches("0") && m != 0) {
								ws.addCell(new Number(m + 1, i + 3, Integer
										.parseInt(value), viceHeaderFormat));
							} else {
								ws.addCell(new Label(m + 1, i + 3, value,
										viceHeaderFormat));
							}
						}
					}
				}
			}

			ws.mergeCells(0, list.size() + 3, columnNames.length - 1,
					list.size() + 3);
			// 运维人员签字
			Label operationpersion = new Label(0, list.size() + 3, "运维人员签字：",
					infoHeaderFormat);
			ws.addCell(operationpersion);
			ws.getSettings().setVerticalFreeze(3);

		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}
}
