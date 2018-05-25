package com.twobomb.template_engine;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


//Добавить нумерация, переносы,необяхательные, сохранение, в getBlock проверка синтаксиса (закрывающая есть\нет)
public class WordController {
    public WordController(){
    }
    public void convert(InputStream in,OutputStream os,HashMap<String,Object> rootmap) throws Exception {
        XWPFDocument doc = read(in);
        Unit u = Unit.Instance(getText(doc));
        List<Unit> ll = u.getChildren();
//        Object test = ll.get(0).getChildren().get(0).getChildren().get(2).getText(rootmap);
        for (Unit z:ll)
            replace(doc, z.getTextblock(), z.getText(rootmap));
        write(os,doc);
        doc.close();
    }
    public String convert(InputStream in,HashMap<String,Object> rootmap) throws Exception {
        XWPFDocument doc = read(in);
        Unit u = Unit.Instance(getText(doc));
        List<Unit> ll = u.getChildren();
//        Object test = ll.get(0).getChildren().get(0).getChildren().get(2).getText(rootmap);
        for (Unit z:ll)
            replace(doc, z.getTextblock(), z.getText(rootmap));
        doc.close();
        return u.getText(rootmap);
    }
    private XWPFDocument read(InputStream is) throws IOException, InvalidFormatException {
        XWPFDocument doc = new XWPFDocument(OPCPackage.open(is));
        return doc;
    }

    private void setStyle(XWPFParagraph p){
        p.setAlignment(ParagraphAlignment.BOTH);//по ширине
        p.setFirstLineIndent(711);// первая строка отступ 1,25 см
        p.setSpacingBetween(1f,LineSpacingRule.AUTO);//Междустрочный одинарный

        p.setSpacingLineRule(LineSpacingRule.AUTO);
        p.setIndentFromRight(0);
        p.setIndentFromLeft(0);
        p.setIndentationLeft(0);
        p.setIndentationRight(0);
        //p.setIndentationHanging(0);
        p.setSpacingAfter(0);
        p.setSpacingBeforeLines(0);
        p.setSpacingBefore(0);
        p.setSpacingAfterLines(0);

    }
    private void replace(XWPFDocument doc, String oldValue, String newValue) {
        String str = "";
        int ss = -1;
        for (int i = 0; i < doc.getParagraphs().size(); i++) {
            XWPFParagraph par = doc.getParagraphs().get(i);
            str+=par.getText() + "\n";
            if(ss == -1)
                ss = i;
            if(Unit.getCountDuplicate(str,"\\$") % 2 == 0 || Unit.getCountDuplicate(str,"\\$") == 0){
                if(str.contains(oldValue)){
                    List<XWPFParagraph> parToDelete = new ArrayList<XWPFParagraph>();

                    for(int j = ss; j <= i ; j++)
                        parToDelete.add(doc.getParagraphs().get(j));

                    XmlCursor cur = parToDelete.get(0).getCTP().newCursor();


                    for(XWPFParagraph del:parToDelete) {
                        doc.removeBodyElement(doc.getPosOfParagraph(del));
                    }
                    str = str.replaceFirst(Unit.addSlashes(oldValue),newValue);
                    XWPFRun run = null;
                    XWPFParagraph p = null;
                        for (int k = 0; k < str.length(); k++) {
                            if(k == 0) {
                                p = doc.insertNewParagraph(cur);
                                setStyle(p);
                                run= p.createRun();

                            }
                            switch (str.charAt(k)) {
                                case '\t':
//                                   System.out.print("\t");
                                    run.addTab();
                                    break;
                                case '\n':
                                    if(k == str.length()-1)
                                        return;
//                                    System.out.print("\n");
                                    //run.addCarriageReturn();
                                    //Старим курсор в конец певого параграфа или в начало следующего
                                    if(doc.getParagraphs().indexOf(p) != -1 && doc.getParagraphs().indexOf(p)+1 < doc.getParagraphs().size()) {
                                        cur = doc.getParagraphs().get(doc.getParagraphs().indexOf(p) + 1).getCTP().newCursor();
                                        p = doc.insertNewParagraph(cur);
                                    }
                                    else {
                                        p = doc.createParagraph();
                                    }

                                    setStyle(p);
                                    run= p.createRun();
                                    break;
                                default:
//                                    System.out.print(String.valueOf(str.charAt(k)));
                                    run.setText(String.valueOf(str.charAt(k)));
                            }

                            run.setFontSize(14);
                            run.setFontFamily("Times New Roman");
                        }



                    return;
                }
                else{
                    ss = -1;
                    str = "";
                }
            }

        }
    }

    private String getText(XWPFDocument doc){
        String str = "";
        for(XWPFParagraph p :doc.getParagraphs()) {
            str += p.getText()+ "\n";
        }
        return str;
    }
    private void write(OutputStream os,XWPFDocument doc) throws IOException {
        doc.write(os);
    }
}