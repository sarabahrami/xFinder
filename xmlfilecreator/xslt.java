/*
 * Author: Sara Bahrami
 * Date Created: 11/2013
 * Most Recent Edit: 08/01/2014 (Sara Bahrami)
 * 
 * Purpose:
 * 	Makes an interaction log from all interaction attachment files.
 */

package xmlfilecreator;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.NodeList;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import edu.wichita.serl.xfinder.InteractionTuple;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class xslt
{
	//CONSTANTS
	public static  String dirPath = "/home/sunshine40270/mine/projectdata/extensionwork/EclipseBenchmark-interaction/";
	public static String dirPath1 = "/home/sunshine40270/mine/projectdata/extensionwork/";
	public static HashMap<String,String> Benchlist = new HashMap<String,String>();
	//METHODS
	public static void main(String[] args) 
	{
	
		try
		{	
			 Benchmark_Issues();
			//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			//Date date1 = sdf.parse("2010-03-15");
			//Date date2 = sdf.parse("2008-10-27");
			File folder = new File(dirPath);
			File[] listOfFiles = folder.listFiles();
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT,"Yes");
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			
			//making docnew for writing to new XML file
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document docnew = docBuilder.newDocument();
			StreamResult result = new StreamResult(new File(dirPath1+"result.xml"));
			
			String x=" ";
			String y=" ";
			String Z=" ";
			//defining  interactions as root for new XML file
			Element rootElement = docnew.createElement("Interactions");
			docnew.appendChild(rootElement);
			
			//picking  files in folder
			
			for (File file : listOfFiles)		
			{ 
				
				//getting  bugId from the filename which is the first part of the filename
				String name=file.getName();
				x=Regbugfname(name);
				//System.out.println(x);
				y=Regattachfname(name);
				Z=Attacher(x,y);
				//loading XML files in folder
				
				if (!(Benchlist.containsKey(x))) {
					File fXmlFile = new File(dirPath + name);
					//parsing XML file
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory
							.newInstance();
					DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
					Document doc = dBuilder.parse(fXmlFile);
					doc.getDocumentElement().normalize();
					NodeList nList = doc
							.getElementsByTagName("InteractionEvent");
					//creating element bugid with attribute Id
					Element bug = docnew.createElement("BugId");
					rootElement.appendChild(bug);
					Attr Id = docnew.createAttribute("Id");
					Id.setValue(x);
					bug.setAttributeNode(Id);
					//creating element attachmentId with attribute Id
					Element attachid = docnew.createElement("AttachId");
					bug.appendChild(attachid);
					Attr Idt = docnew.createAttribute("Id");
					Idt.setValue(y);
					attachid.setAttributeNode(Idt);
					//creating element attacher with attribute name
					Element attacher = docnew.createElement("Attacher");
					bug.appendChild(attacher);
					Attr attname = docnew.createAttribute("name");
					attname.setValue(Z);
					attacher.setAttributeNode(attname);
					//parsing all InteractionEvent elements 
					for (int temp = 0; temp < nList.getLength(); temp++) {
						Node nNode = nList.item(temp);
						Date endDate;
						String value, value1;
						value = nNode.getAttributes().getNamedItem("Kind")
								.getNodeValue();
						endDate = ConvertDate(nNode.getAttributes()
								.getNamedItem("EndDate").getNodeValue());
						value1 = nNode.getAttributes()
								.getNamedItem("StructureHandle").getNodeValue();
						Pattern pattern1 = Pattern.compile(".java");
						Matcher matcher1 = pattern1.matcher(value1);
						Pattern pattern2 = Pattern.compile("lt;");
						Matcher matcher2 = pattern2.matcher(value1);
						Pattern pattern3 = Pattern.compile("org");
						Matcher matcher3 = pattern3.matcher(value1);

						//filter interaction event elements which value of attribute of kind is equal to edit
						//if((value.equalsIgnoreCase("edit"))&&(endDate.compareTo(date1)<0))
						//if(((endDate.compareTo(date1)<0)&&(endDate.compareTo(date2)>0))&&(value.equalsIgnoreCase("edit")))
						//if((endDate.compareTo(date1)<=0)&&(endDate.compareTo(date2)>=0))	
						//if (value.equalsIgnoreCase("edit"))

						//{
							if ((matcher1.find()) && (matcher2.find())
									&& (matcher3.find())) {

								Element eElement = (Element) nNode;

								//adding element interactionevent with attributes to docnew for writing to new XML file
								Element InteractionEvent = docnew
										.createElement("InteractionEvent");
								bug.appendChild(InteractionEvent);
								Attr EndDate = docnew
										.createAttribute("EndDate");
								EndDate.setValue(eElement.getAttributeNode(
										"EndDate").getNodeValue());
								InteractionEvent.setAttributeNode(EndDate);
								Attr Kind = docnew.createAttribute("Kind");
								Kind.setValue(eElement.getAttributeNode("Kind")
										.getNodeValue());
								InteractionEvent.setAttributeNode(Kind);
								Attr StructureHandle = docnew
										.createAttribute("StructureHandle");
								StructureHandle.setValue(eElement
										.getAttributeNode("StructureHandle")
										.getNodeValue());
								InteractionEvent
										.setAttributeNode(StructureHandle);
							}
						//}

					}//end for
				}//end if
				
		
			}//end for
			
			//making a DOMSource to writ docnew to new XML file
			DOMSource source = new DOMSource(docnew);
			transformer.transform(source, result);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	
	}
	
		
	
	//pattern for bugId from the filename which is the first part of the filename
	
	public static String Regbugfname (String fname)	
	{   
		String x=" ";
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher matcher = pattern.matcher(fname); 
		if (matcher.find())
	 	{
			x=matcher.group();
		}
		return x;
	}

	public static String Regattachfname (String fname)	
	{   
		String x=" ";
		Pattern pattern = Pattern.compile("([0-9]*)(.xml)");
		Matcher matcher = pattern.matcher(fname); 
		if (matcher.find())
	 	{
			x=matcher.group(1);
		}
		return x;
	} 
	public static String Regattachment (String row)	
	{   
		String x=" ";
		Pattern pattern = Pattern.compile("\\d.*");
		Matcher matcher = pattern.matcher(row); 
		if (matcher.find())
	 	{
			x=matcher.group();
		}
		return x;
	}
	public static String Regattacher (String row)	
	{   
		String x=" ";
		Pattern pattern = Pattern.compile("[a-zA-Z]*\\s[a-zA-Z]*");
		Matcher matcher = pattern.matcher(row); 
		if (matcher.find())
	 	{
			x=matcher.group();
		}
		return x;
	}


	public static String Attacher (String x,String y) throws IOException
	{
		String AttId="";
		String dirPath = "/home/sunshine40270/mine/projectdata/extensionwork/";
		File attacher = new File(dirPath + "Eclipsattacher");
		FileInputStream fstream; 
		fstream = new FileInputStream(attacher);
        // Get the object of DataInputStream
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
         
        //Read File Line By Line
        String strLine=null;
        while ((strLine = br.readLine()) != null)
        {
		 	String[] row= strLine.split("\t");
		 	if (row[0].equalsIgnoreCase(x))
		 	{
		 		String[] row1=strLine.split("\\]");
		 		for(int i=0;i<=row1.length-1;i++)
				{ 
		 			if (!(row1[i].trim().isEmpty()))
					{
		 				String[] row2=row1[i].split("\t");
						if (Regattachment(row2[1]).equalsIgnoreCase(y) )
				 		{
							AttId=row2[3];
						}
	
					
					}
	
	        	 }
		 	}
        }
        br.close();
		return AttId;
	}
	
	public static Date ConvertDate (String date ) throws ParseException
	{
		String pattern = "yyyy-MM-dd";
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		Date date1 = format.parse(date);
		return date1;
    }
	
	
	//This function will return all the bug ids in benchmark
	public static void Benchmark_Issues()throws IOException
	{
		String strLine=null;
		BufferedReader reader1 = new BufferedReader(new FileReader(dirPath1+"/"+"IssueinBenchmark"));
		while ((strLine = reader1.readLine())!= null) {
	           	String[] row= strLine.split("\t");
	           	Benchlist.put(row[0], row[1]);         	
	}
		/*for (Entry<String, String> entry : Benchlist.entrySet()){
			System.out.println(entry.getKey());
		}*/

	}
	
}

