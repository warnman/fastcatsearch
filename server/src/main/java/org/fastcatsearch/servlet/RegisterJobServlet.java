/*
 * Copyright (c) 2013 Websquared, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     swsong - initial API and implementation
 */

package org.fastcatsearch.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fastcatsearch.control.JobService;
import org.fastcatsearch.control.ResultFuture;
import org.fastcatsearch.ir.util.Formatter;
import org.fastcatsearch.job.Job;
import org.fastcatsearch.util.DynamicClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterJobServlet extends WebServiceHttpServlet {
	
	private static final long serialVersionUID = -6765756082395762010L;
	private static Logger logger = LoggerFactory.getLogger(RegisterJobServlet.class);
	
    public RegisterJobServlet(int resultType){
    	super(resultType);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    
    	response.setContentType("text/html");
    	response.setStatus(HttpServletResponse.SC_OK);
    	PrintWriter writer = response.getWriter();
    	
    	String className = request.getParameter("class");
    	String isJoin = request.getParameter("join");
    	String args = request.getParameter("args");
    	logger.debug("className = "+className);
    	logger.debug("args = "+args);
    	if(className == null){
    		//print usage
    		writer.write("Usage: /execute/");
    		writer.write("<br/>");
    		writer.write("       param: class [required]");
    		writer.write("<br/>");
    		writer.write("              ex) &class=org.fastcatsearch.job.TestJob");
    		writer.write("<br/>");
    		writer.write("       param: args [optional]");
    		writer.write("<br/>");
    		writer.write("              ex) &args=hello fastcatsearch 100");
    		writer.write("<br/>");
    		writer.write("       param: join [optional]");
    		writer.write("<br/>");
    		writer.write("              ex) &join=true");
    		writer.write("<br/>");
    		writer.close();
    		
    		return;
    	}
    	Job job = DynamicClassLoader.loadObject(className, Job.class);
    	
    	if(args != null){
    		String[] argList = args.split("[\\s]+");
    		job.setArgs(argList);
    	}
    	
    	ResultFuture jobResult = null;
    	
    	
    	
    	long st = System.currentTimeMillis();
		jobResult = JobService.getInstance().offer(job);
		
		
		writer.println("{");
		Object result = null;
		if(isJoin != null && isJoin.equalsIgnoreCase("true")){
			result = jobResult.take();
			if(jobResult.isSuccess())
				writer.println("\t\"status\": \"OK\",");
			else
				writer.println("\t\"status\": \"ERROR\",");
		}else{
			writer.println("\t\"status\": \"OK\",");
		}
		
		
		writer.println("\t\"time\": \""+Formatter.getFormatTime(System.currentTimeMillis() - st)+"\"");
		writer.println("\t\"result\": \""+result+"\",");
		writer.println("}");
		writer.close();
    	
    }
}
