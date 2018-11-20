package com.tydic.datatransfer;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.util.Iterator;

public class TransferXmlToTxt {

    private String filePath = "";
    private String outputPath = "E:\\output\\";

    /**
     * 将目录下的XML转换为cvs文件
     * @param filePath XML文件路径
     * @param outputPath CVS文件路径
     * @throws Exception
     */
    public void transferXmlToTxtInDir(String filePath, String outputPath) throws Exception{
        String[] xmlFiles=(new File(filePath)).list(new FilenameExtFilter(".xml"));
        for(String xmlFile: xmlFiles) {
            transferXmlToTxt(filePath, xmlFile, outputPath);
        }
    }

    /**
     * 将XML转换为cvs文件
     * @param filePath XML文件路径
     * @param fileName XML文件名
     * @param outputPath CVS文件路径
     * @throws Exception
     */
    public void transferXmlToTxt(String filePath, String fileName, String outputPath) throws Exception{

        if(filePath!=null && !"".equals(filePath)){
            this.filePath = filePath;
        } else {
            System.out.println("filePath is null");
            return;
        }
        if(outputPath!=null && !"".equals(outputPath)){
            this.outputPath = outputPath;
        } else {
            System.out.println("outputPath is null");
            return;
        }

        // 创建SAXReader的对象reader
        SAXReader reader = new SAXReader();
        try {
            File file = new File(this.filePath + fileName);
            String xmlFileName = file.getName();
            String cvsFileName = xmlFileName.substring(0,12) + ".csv";

            // 通过reader对象的read方法加载alarms.xml文件,获取document对象。
            Document document = reader.read(file);
            // 通过document对象获取根节点alarms
            Element rootElement = document.getRootElement();

            // 通过element对象的elementIterator方法获取迭代器
            Iterator alarmIterator = rootElement.elementIterator();
            StringBuilder tableHeadText = new StringBuilder("");//表头
            boolean tableHeadOutputFlag = false;//表头已写入文件标志
            // 遍历迭代器，获取根节点中的信息（告警）
            while (alarmIterator.hasNext()) {
                //=====开始遍历某一条告警=====
                Element alarm = (Element) alarmIterator.next();
                Iterator alarmProperties = alarm.elementIterator();
                StringBuilder alarmText = new StringBuilder("");
                while (alarmProperties.hasNext()) {
                    Element alarmChildNode = (Element) alarmProperties.next();
                    if(tableHeadOutputFlag==false){
                        tableHeadText.append("\"").append(alarmChildNode.getName()).append("\"").append(",");
                    }
                    String StringValue = alarmChildNode.getStringValue();
                    StringValue = StringValue.replaceAll("[\\t\\n\\r]", "");//去掉换行符
                    alarmText.append("\"").append(StringValue).append("\"").append(",");
                }
                if(tableHeadOutputFlag==false){
                    String tableHeadTextString = tableHeadText.toString();
                    writeFile(this.outputPath,cvsFileName,tableHeadTextString.substring(0,tableHeadTextString.length()-1));
                    tableHeadOutputFlag = true;
                }
                String alarmTextString = alarmText.toString();
                writeFile(this.outputPath,cvsFileName,alarmTextString.substring(0,alarmTextString.length()-1));
                //=====结束遍历某一条告警=====
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * 以追加方式一行一行写入文件
     * @param filePath
     * @param fileName
     * @param lineContent
     */
    private void writeFile(String filePath, String fileName, String lineContent) {
        if(fileName==null || lineContent==null){
            return;
        }

        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
//        FileWriter fw = null;
        try {
            //如果文件存在，则追加内容；如果文件不存在，则创建文件
            File f = new File(filePath + fileName);

            fos = new FileOutputStream(f, true);
            osw = new OutputStreamWriter(fos, "GBK");
//            fw = new FileWriter(f, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw = new PrintWriter(osw);
        pw.println(lineContent);
        pw.flush();
        try {
            osw.flush();
            pw.close();
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class FilenameExtFilter implements FilenameFilter{
        private String ext;
        public FilenameExtFilter(String ext){
            this.ext=ext;
        }
        public boolean accept(File dir, String name){
            return name.endsWith(ext);
        }
    }

    public static void main(String[] args) {
        try {
            TransferXmlToTxt transferXmlToTxt = new TransferXmlToTxt();
//            transferXmlToTxt.transferXmlToTxt("E:\\Work\\Task_tydic\\20181017 告警数据XML解析\\test\\","201808311045.xml","E:\\Work\\Task_tydic\\20181017 告警数据XML解析\\test\\");
//            transferXmlToTxt.transferXmlToTxtInDir("E:\\Work\\Task_tydic\\20181017_告警数据XML解析\\test\\","E:\\Work\\Task_tydic\\20181017_告警数据XML解析\\test\\");

            transferXmlToTxt.transferXmlToTxtInDir(args[0],args[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
