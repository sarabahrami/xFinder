/*
 * Author: Sara Bahrami
 * Date Created: 11/2013
 * Most Recent Edit: 08/01/2014 (Sara Bahrami)
 * 
 * Purpose:
 * 	Creates a dictionary of the interactions which include the
 * desired file/path. Also allows paths to be split into the
 * file path and the package path.
 */

package xmlfilecreator;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import edu.wichita.serl.xfinder.InteractionTuple;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class dictionary {
	//CONSTANTS
	//public static String dirPath1 = "/home/sunshine40270/mine/projects/interaction2/benchmark2/";
	public static HashMap<String, ArrayList<InteractionTuple>> mPathInteractionDict = new HashMap<String, ArrayList<InteractionTuple>>();
	
	//CONSTRUCTOR
	public dictionary (String dirPath1)
	{
		try
		{
			System.out.println(dirPath1);
			File fxmlfile=new File(dirPath1+"result.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fxmlfile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("BugId");
			String BugNumber=" ";
			String Attacher=" ";
			Integer IntBug=0;
			for (int temp=0; temp<nList.getLength();temp++)
			{
				Node nNode = nList.item(temp);
				BugNumber=nNode.getAttributes().getNamedItem("Id").getNodeValue();
				IntBug=Integer.parseInt(BugNumber);
				NodeList nList1 = nNode.getChildNodes();
				for(int j=0;j<nList1.getLength();j++)
				{	
					String name;
					name=nList1.item(j).getNodeName();

					if (name.equalsIgnoreCase("Attacher"))
						Attacher=nList1.item(j).getAttributes().getNamedItem("name").getNodeValue();
					
				}
				
				for(int j=0;j<nList1.getLength();j++)
				{	String name;
					name=nList1.item(j).getNodeName();
					try
					{
						if (name.equalsIgnoreCase("InteractionEvent"))
						{	
							String path;
							Date endDate;
							path=nList1.item(j).getAttributes().getNamedItem("StructureHandle").getNodeValue();
							path= path.replace('\\', File.separatorChar);
							endDate=ConvertDate(nList1.item(j).getAttributes().getNamedItem("EndDate").getNodeValue());
							//System.out.println(endDate);
							InteractionTuple info = new InteractionTuple(IntBug, Attacher, endDate);
							ArrayList<String> Result = pathsplit(path);
							if (mPathInteractionDict.size()==0)
							{
								for (int x=0;x<Result.size();x++)
								{
									ArrayList<InteractionTuple> infoList = new ArrayList<InteractionTuple>(1);
									infoList.add(info);
									mPathInteractionDict.put(Result.get(x), infoList);
								}
							} 
							else
							{	
								for (int x=0;x<Result.size();x++)
						  		{	
									int Flag=0;
						  			for (Entry<String, ArrayList<InteractionTuple>> entry : mPathInteractionDict.entrySet())
					  				{	
						  				if (entry.getKey().equalsIgnoreCase(Result.get(x)))
						  					Flag=1;
					  				}
					  				if (Flag==0)
					  				{
										ArrayList<InteractionTuple> infoList = new ArrayList<InteractionTuple>(1);
										infoList.add(info);
										mPathInteractionDict.put(Result.get(x), infoList);
									}
					  				else
					  					mPathInteractionDict.get(Result.get(x)).add(info);
						  		}	
							}
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
					 
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		/*Print out dictionary
		for (Entry<String, ArrayList<InteractionTuple>> entry : mPathInteractionDict.entrySet())
			for(int i=0;i<entry.getValue().size();i++ )
			{
				System.out.print(entry.getKey() + " ");
				System.out.print(entry.getValue().get(i).mBugID + " ");
				System.out.print(entry.getValue().get(i).mAttacher + " ");
				System.out.println(entry.getValue().get(i).mDate);
			}
		*/
	}
	
	//GETTER
	public HashMap<String, ArrayList<InteractionTuple>> getDict()
	{
		return mPathInteractionDict;
	}
	
	//METHODS
	public  ArrayList<String> pathsplit(String path) throws Exception
	{
		ArrayList<String> retval = new ArrayList<String>(2);
		retval.add(" ");
		retval.add(" ");
		Pattern pattern = Pattern.compile("lt;");
		Matcher matcher = pattern.matcher(path);
		if (matcher.find())
		{
			int startIndex=path.indexOf("lt;");
			path=path.substring(startIndex+3);
			Pattern pattern1 = Pattern.compile("\\[");
			Matcher matcher1 = pattern1.matcher(path);
			if (matcher1.find())
			{
				int endIndex=path.indexOf("[");
				path=path.substring(0,endIndex);
			}	 
			Pattern pattern2 = Pattern.compile(".java");
			Matcher matcher2 = pattern2.matcher(path);
			if (matcher2.find())
			{
				int endIndex=path.indexOf(".java");
				path=path.substring(0,endIndex).replace('.', File.separatorChar);
				path=path+".java";
			}

			Pattern pattern3 = Pattern.compile("[{]");
			Matcher matcher3 = pattern3.matcher(path);
			if (matcher3.find())
			{
				retval.set(0, path.split("\\{")[0]);   
				retval.set(1, path.replace('{', File.separatorChar));
			}
			
		}
		
		return retval;
	}
	//this function added becasue the input files path changed, so this new function can handle split path for new input sourcefile path
	/**********************************************************************/
	public static  ArrayList<String> pathsplitnew(String path) throws Exception{
	 
		
		ArrayList<String> retval = new ArrayList<String>(2);
		retval.add(" ");
		retval.add(" ");
		int startIndex=path.indexOf("src");
		path=path.substring(startIndex+4);
		String x=" ";
		Pattern pattern = Pattern.compile("(\\w*.java)");
		Matcher matcher = pattern.matcher(path); 
		if (matcher.find())
	 	{
			x=matcher.group();
		}
		int startindex1=path.indexOf(x);
		String path1=path.substring(0, startindex1-1);

			retval.set(0, path1);   
			retval.set(1, path);
			//System.out.println(retval.get(0));
			//System.out.println(retval.get(1));
			return retval;
		}
	 
	/***********************************************************************/ 
	// * @param date
	// * @return
	// * @throws ParseException
	
	
	public  Date ConvertDate (String date ) throws ParseException
	{
		String pattern = "yyyy-MM-dd";
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		Date date1 = format.parse(date);
		return date1;
    }
	
	//This function creates a new path from input query path for just Eclipse Project
		/*************************************************************************/
		public static  ArrayList<String> pathsplitnewEC(String path) throws Exception{
			 
			ArrayList<String> retval = new ArrayList<String>(2);
			retval.add(" ");
			retval.add(" ");
			int index=path.lastIndexOf("org");
			 path= path.substring(index);
			String x=" ";
			Pattern pattern = Pattern.compile("(\\w*.java)");
			Matcher matcher = pattern.matcher(path); 
			if (matcher.find())
		 	{
				x=matcher.group();
			}
			int startindex1=path.indexOf(x);
			String path1=path.substring(0, startindex1-1);

				retval.set(0, path1);   
				retval.set(1, path);
				//System.out.println(retval.get(0));
				//System.out.println(retval.get(1));
				return retval;
			}
		
	/**********************************************************************/	
		public static  ArrayList<String> pathsplitEC(String path) throws Exception
		{
			path=path.replace("=","");
			ArrayList<String> retval = new ArrayList<String>(2);
			retval.add(" ");
			retval.add(" ");
			Pattern pattern = Pattern.compile("lt;");
			Matcher matcher = pattern.matcher(path);
			if (matcher.find())
			{
				path=path.replace("&amp;lt;","/");
				Pattern pattern1 = Pattern.compile("\\[");
				Matcher matcher1 = pattern1.matcher(path);
				if (matcher1.find())
				{
					int endIndex=path.indexOf("[");
					path=path.substring(0,endIndex);
				}	 
				Pattern pattern2 = Pattern.compile(".java");
				Matcher matcher2 = pattern2.matcher(path);
				if (matcher2.find())
				{
					int endIndex=path.indexOf(".java");
					path=path.substring(0,endIndex).replace('.', File.separatorChar);
					path=path+".java";
				}

				Pattern pattern3 = Pattern.compile("[{]");
				Matcher matcher3 = pattern3.matcher(path);
				if (matcher3.find())
				{
					retval.set(0, path.split("\\{")[0]);   
					retval.set(1, path.replace('{', File.separatorChar));
				}
				
			}
			
			return retval;
		}
}
