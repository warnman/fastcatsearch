<%--
# Copyright (C) 2011 - 2013 Websquared, Inc.
# All rights reserved.
--%>

<%@ page contentType="text/html; charset=UTF-8"%> 

<%@page import="com.fastcatsearch.util.WebUtils"%>
<%@page import="org.fastcatsearch.ir.IRService"%>
<%@page import="org.fastcatsearch.settings.IRSettings"%>
<%@page import="org.fastcatsearch.ir.config.*"%>
<%@page import="org.fastcatsearch.ir.analysis.Tokenizer"%>
<%@page import="org.fastcatsearch.ir.io.CharVector"%>
<%@page import="org.fastcatsearch.util.DynamicClassLoader2"%>

<%@include file="../common.jsp" %>

<%
	String tokenizerStr = WebUtils.getString(request.getParameter("tokenizer"),"org.fastcatsearch.ir.tokenizer.Tokenizer.KoreanTokenizer");
	String contents = WebUtils.getString(request.getParameter("contents"),"");
	Object object = DynamicClassLoader.loadObject(tokenizerStr);
	Tokenizer tokenizer = null;
	if(object != null){
		tokenizer = (Tokenizer)object;
	}
	
	IRConfig irConfig = IRSettings.getConfig(true);
	String collectinListStr = irConfig.getString("collection.list");
	String[] colletionList = collectinListStr.split(",");
	String collection = "";
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>FASTCAT 검색엔진 관리도구</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="<%=FASTCAT_MANAGE_ROOT%>css/reset.css" rel="stylesheet" type="text/css" />
<link href="<%=FASTCAT_MANAGE_ROOT%>css/style.css" rel="stylesheet" type="text/css" />
<link href="<%=FASTCAT_MANAGE_ROOT%>css/jquery-ui.css" type="text/css" rel="stylesheet" />
<link href="<%=FASTCAT_MANAGE_ROOT%>css/ui.jqgrid.css" type="text/css" rel="stylesheet" />
<!--[if lte IE 6]>
<link href="<%=FASTCAT_MANAGE_ROOT%>css/style_ie.css" rel="stylesheet" type="text/css" />
<![endif]-->
<script type="text/javascript" src="<%=FASTCAT_MANAGE_ROOT%>js/common.js"></script>
	<script type="text/javascript" src="<%=FASTCAT_MANAGE_ROOT%>js/jquery-1.4.4.min.js"></script>
	<script type="text/javascript" src="<%=FASTCAT_MANAGE_ROOT%>js/grid.locale-en.js"></script> 
	<script type="text/javascript" src="<%=FASTCAT_MANAGE_ROOT%>js/jquery.jqGrid.min.js"></script>
	<script src="<%=FASTCAT_MANAGE_ROOT%>js/help.js" type="text/javascript"></script>
	<script type="text/javascript">
		var utf8_decode = function(utftext) 
		{ 
			var string = ''; 
			var i = 0; 
			var c = c1 = c2 = 0; 
			
			while( i < utftext.length ) {
				c = utftext.charCodeAt(i); 
				
				if( c < 128 ) {
					string += String.fromCharCode(c); 
					i++; 
				} 
				else if( (c > 191) && (c < 224) ) { 
					c2 = utftext.charCodeAt( i+1 ); 
					string += String.fromCharCode( ((c & 31) << 6) | (c2 & 63) ); 
					i += 2; 
				} else{
					c2 = utftext.charCodeAt( i+1 ); 
					c3 = utftext.charCodeAt( i+2 ); 
					string += String.fromCharCode( ((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63) ); 
					i += 3; 
				} 
			} 
			return string; 
		};
		
	function selectCollection(dropdown){
		var myindex  = dropdown.selectedIndex
	    var selValue = dropdown.options[myindex].value
		$('#cn').val(selValue);		
	}
	
	$(function(){
		restoreParametersFromCookie();
	});
    </script>
    
    
    
</head>

<body>
<div id="container">
<!-- header -->
<%@include file="../header.jsp" %>

<div id="sidebar">
	<div class="sidebox">
		<h3>테스트</h3>
			<ul class="latest">
			<li><a href="<%=FASTCAT_MANAGE_ROOT%>tester/search.jsp" class="selected">검색테스트</a></li>
			<li><a href="<%=FASTCAT_MANAGE_ROOT%>tester/analyzer.jsp">분석기테스트</a></li>
			<li><a href="<%=FASTCAT_MANAGE_ROOT%>tester/dbTester.jsp">DB테스트</a></li>
			<li><a href="<%=FASTCAT_MANAGE_ROOT%>tester/searchDoc.jsp">문서원문조회</a></li>
			</ul>
	</div>
</div><!-- E : #sidebar -->


<div id="mainContent">

<h2>검색 테스트</h2>
<h4>검색파라미터</h4>
<div class="fbox">
	<table summary="컬렉션" class="tbl01">
	<colgroup><col width="25%" /><col width="" /></colgroup>
	<tbody>
	<tr>
	<th>검색Context<span class="req_opt">*</span></th>
	<td style="text-align:left;">
		<select id="searchContext" style="width:200px">
			<option value="search/json">일반검색 (search/json)</option>
			<option value="cluster/search/json">분산검색 (cluster/search/json)</option>
			<option value="group/json">일반그룹핑검색 (group/json)</option>
			<option value="cluster/group/json">분산그룹핑검색 (cluster/group/json)</option>
		</select>
	</td>
	</tr>
	<tr>
	<th>컬렉션이름(cn) <span class="req_opt">*</span></th>
	<td style="text-align:left;">
		<input type="text" id="cn" name="cn" size="20" class='inp02'/>
		<select id="selectCollection" onchange="selectCollection(this)" style="width:100px">
		<option value="">::선택::</option>
	<% for(int i = 0;i < colletionList.length;i++){ %>
		<% String col = colletionList[i]; %>
		<% if(collection == null){ %>
			<% if(i == 0){ %>
				<% collection = col; %>
			<% } %>
		<% } %>
		<option value="<%=col %>" <%=col.equals(collection) ? "selected" : "" %> ><%=col %></option>
	<% } %>
	</select>
	</td>
	</tr>
	<tr>
	<th>선택필드(fl)<span class="req_opt">*</span></th>
	<td style="text-align:left;"><input type="text" id="fl" name="fl" size="80" value="title,body:50,_score_" class='inp02 help'/></td>
	</tr>
	<tr>
	<th>검색조건(se)<span class="req_opt">*</span></th>
	<td style="text-align:left;"><input type="text" id="se" name="se" size="80" value="{title,body:AND(방송):100:15}" class='inp02 help'/></td>
	</tr>
	<tr>
	<th>그룹핑(gr)</th>
	<td style="text-align:left;"><input type="text" id="gr" name="gr" size="80" value="" class='inp02 help'/></td>
	</tr>
	<tr>
	<th>그룹조건(gc)</th>
	<td style="text-align:left;"><input type="text" id="gc" name="gc" size="80" value="" class='inp02 help'/></td>
	</tr>
	<tr>
	<th>그룹필터(gf)</th>
	<td style="text-align:left;"><input type="text" id="gf" name="gf" size="80" value="" class='inp02 help'/></td>
	</tr>
	<tr>
	<th>정렬(ra)</th>
	<td style="text-align:left;"><input type="text" name="ra" id="ra" size="80" class='inp02 help'/></td>
	</tr>
	<tr>
	<th>필터(ft)</th>
	<td style="text-align:left;"><input type="text" name="ft" id="ft" size="80" class='inp02 help'/></td>
	</tr>
	<tr>
	<th>시작번호(sn)<span class="req_opt">*</span></th>
	<td style="text-align:left;"><input type="text" name="sn" id="sn" size="80" value="1" class='inp02 help'/></td>
	</tr>
	<tr>
	<th>결과갯수(ln)<span class="req_opt">*</span></th>
	<td style="text-align:left;"><input type="text" name="ln" id="ln" size="80" value="50" class='inp02 help'/></td>
	</tr>
	<tr>
	<th>하이라이트 태그(ht)</th>
	<td style="text-align:left;"><input type="text" name="ht" id="ht" size="80" value="<b>:</b>" class='inp02 help'/></td>
	</tr>
	<tr>
	<th>검색옵션(so)</th>
	<td style="text-align:left;"><input type="text" name="so" id="so" size="80" class='inp02 help'/></td>
	</tr>
	<tr>
	<th>사용자데이터(ud)</th>
	<td style="text-align:left;"><input type="text" name="ud" id="ud" size="80" class='inp02 help'/></td>
	</tr>
	<th>타임아웃</th>
	<td style="text-align:left;">
		<select id="timeout" style="width:100px">
			<option value="5">5초</option>
			<option value="60">1분</option>
			<option value="300">5분</option>
		</select>
	</td>
	</tr>
	<tr>
	<td colspan="2"><input type="button" id="createUrl" value="쿼리보기" class="btn_c" onclick="createQueryUrl()" /> <input type="button" id="search_button" value="검색" class="btn_c"/></td>
	</tr>
	<tr>
		<th>URL</th>
		<td  style="text-align:left;"><textarea class="inp02" cols="70" style="height:50px;" id="queryTxt"></textarea>
		<input type="button" id="go_search" onclick="window.open($('#queryTxt').val())" style="height:45px;" value="바로가기" class="btn_c"/>
		</td>
	</tr>
		
	</tbody>
	</table>
</div>


<h4>결과요약</h4>
<div class="fbox">
<table class="tbl01">
	<colgroup><col width="25%" /><col width="" /></colgroup>
	<tbody>
	<tr>
	<th>실행시각</th>
	<td style="text-align:left;padding-left:30px;"><span id="s_now">&nbsp;</span></td>
	</tr>
	<tr>
	<th>상태값</th>
	<td style="text-align:left;padding-left:30px;"><span id="s_0">&nbsp;</span></td>
	</tr>
	<tr>
	<th>검색시간</th>
	<td style="text-align:left;padding-left:30px;"><span id="s_1">&nbsp;</span></td>
	</tr>
	<tr>
	<th>총 갯수</th>
	<td style="text-align:left;padding-left:30px;"><span id="s_2">&nbsp;</span></td>
	</tr>
	<tr>
	<th>결과갯수</th>
	<td style="text-align:left;padding-left:30px;"><span id="s_3">&nbsp;</span></td>
	</tr>
	<tr>
	<th>메시지</th>
	<td style="text-align:left;padding-left:30px;"><span id="s_4">&nbsp;</span></td>
	</tr>
	</tbody>
</table>
</div>


	<script type="text/javascript">
	
	$('#search_button').click(function() {
		doingSearch();
	});
	
	$('#go_search').click(function() {
		doingSearch();
	});
	
	$(window).bind('resize', function() {
		//$("#searchResult").setGridWidth($(window).width()-50);
		$("#searchResult").setGridWidth(740);
	}).trigger('resize');
	
	function createQueryUrl() {
			var uri = "<%=FASTCAT_SEARCH_ROOT%>" + $('#searchContext').val();
			uri = uri.replace(/[\/][\/]/g,"\/");
			var queryStr = location.protocol+"//"+location.host+uri+
					"?cn="+$('#cn').val() +
					"&ht="+$('#ht').val()+
					"&sn="+$('#sn').val()+
					"&ln="+$('#ln').val()+
					"&so="+$('#so').val()+
					"&ud="+encodeURIComponent($('#ud').val())+
					"&fl="+$('#fl').val()+
					"&se="+encodeURIComponent($('#se').val())+
					"&gr="+$('#gr').val()+
					"&gc="+encodeURIComponent($('#gc').val())+
					"&gf="+$('#gf').val()+
					"&ra="+$('#ra').val()+
					"&ft="+encodeURIComponent($('#ft').val())+
					"&timeout="+$('#timeout').val();
			$("#queryTxt").val(queryStr);
	}
	
	
	
	function getDatetime(){
		var currentTime = new Date()
		var month = currentTime.getMonth() + 1
		var day = currentTime.getDate()
		var year = currentTime.getFullYear()
		var hours = currentTime.getHours()
		var minutes = currentTime.getMinutes()
		var seconds = currentTime.getSeconds()
		if (month < 10){
			month = "0" + month
		}
		if (day < 10){
			day = "0" + day
		}
		if (hours < 10){
			hours = "0" + hours
		}
		if (minutes < 10){
			minutes = "0" + minutes
		}
		if (seconds < 10){
			seconds = "0" + seconds
		}
		
		return year + "-" + month + "-" + day + " " + hours + ":" + minutes + ":" + seconds;
	}
	
	function restoreParametersFromCookie(){
		$('#searchContext').val(getCookie("searchContext"));
		$('#cn').val(getCookie("cn"));
		$('#ht').val(getCookie("ht"));
		$('#sn').val(getCookie("sn"));
		$('#ln').val(getCookie("ln"));
		$('#so').val(getCookie("so"));
		$('#ud').val(getCookie("ud"));
		$('#fl').val(getCookie("fl"));
		$('#se').val(getCookie("se"));
		$('#gr').val(getCookie("gr"));
		$('#gc').val(getCookie("gc"));
		$('#gf').val(getCookie("gf"));
		$('#ra').val(getCookie("ra"));
		$('#ft').val(getCookie("ft"));
		$('#timeout').val(getCookie("timeout"));
	}
	function saveParametersToCookie(){
		setCookie("searchContext", $('#searchContext').val());
		setCookie("cn", $('#cn').val());
		setCookie("ht", $('#ht').val());
		setCookie("sn", $('#sn').val());
		setCookie("ln", $('#ln').val());
		setCookie("so", $('#so').val());
		setCookie("ud", $('#ud').val());
		setCookie("fl", $('#fl').val());
		setCookie("se", $('#se').val());
		setCookie("gr", $('#gr').val());
		setCookie("gc", $('#gc').val());
		setCookie("gf", $('#gf').val());
		setCookie("ra", $('#ra').val());
		setCookie("ft", $('#ft').val());
		setCookie("timeout", $('#timeout').val());
	}
	function doingSearch(){
		saveParametersToCookie();
		
		$("#s_0").text("검색중..");
		$("#s_1").text("");
		$("#s_2").text(0);
		$("#s_3").text(0);
		$("#s_4").text("");
		
		if($("#resultWrapper").children())
			$("#resultWrapper").children().remove();
			
		if($("#groupResultWrapper").children())
			$("#groupResultWrapper").children().remove();
		
		$("#SearchResultSummary").show();
		var nkeyword = $("#se").val().replace(",","\\\\,");
		$.ajax({
			  url: "<%=FASTCAT_SEARCH_ROOT%>" + $('#searchContext').val(),
			  data: {
				admin: true,
				timeout: 5,
				cn: $('#cn').val(),
				ht: $('#ht').val(),
				sn: $('#sn').val(),
				ln: $('#ln').val(),
				so: $('#so').val(),
				ud: encodeURIComponent($('#ud').val()),
				fl: $('#fl').val(),
				se: encodeURIComponent($('#se').val()),
				gr: $('#gr').val(),
				gc: encodeURIComponent($('#gc').val()),
				gf: $('#gf').val(),
				ra: $('#ra').val(),
				ft: encodeURIComponent($('#ft').val()),
				timeout: $('#timeout').val()
			  },
			  type: 'POST',
			  dataType: 'json',
			  error: function(XMLHttpRequest, textStatus, errorThrown) {
			  	  $("#s_now").text(getDatetime());
			  	  $("#s_0").text("JSON ERROR");
			  	  $("#s_1").text(0);
				  $("#s_2").text(0);
				  $("#s_3").text(0);
				  $("#s_4").text(textStatus);
			  },
			  success: function(data_obj) {
			  	$("#s_now").text(getDatetime());
			  	if(data_obj != null && data_obj.hasOwnProperty("status")){
			  		if(data_obj.status > 0){
					  	$("#s_0").text(data_obj.status);
						$("#s_1").text(data_obj.time);
						$("#s_2").text(0);
						$("#s_3").text(0);
						$("#s_4").text(data_obj.error_msg);
				  		return;
				  	}
			  	}else{
			  		//timeout
			  		$("#s_0").text("");
					$("#s_1").text("");
					$("#s_2").text(0);
					$("#s_3").text(0);
					$("#s_4").text("검색에러 또는 타임아웃 >> "+data_obj);
			  		return;
			  	}
			  	
			  	
				$("#s_0").text(data_obj.status);
				$("#s_1").text(data_obj.time);
				$("#s_2").text(data_obj.total_count);
				if(data_obj.hasOwnProperty("count")){ //그룹전용결과에는 count가 없음.
					$("#s_3").text(data_obj.count);
			  	}
				$("#s_4").text("");
		
			  	$("#resultWrapper").append($("<table id='searchResult'></table>"));
				$("#resultWrapper").append($("<div id='pSearchResult'></div>"));

			  	//make fieldname_list
			  	fieldname_list = new Array();

			  	if(data_obj.hasOwnProperty("fieldname_list")){
					$.each(data_obj.fieldname_list, function(i, row){
						fieldname_list[i] = row.name
					});
			  	}
			  	
				if(data_obj.hasOwnProperty("result")){
				  	$("#searchResult").jqGrid({
						data: data_obj.result,
						datatype: "local",
						height: 'auto',
						mtype: 'POST',
						rowNum: 20,
						rowList: [10,20,30,40,50],
					   	colNames: fieldname_list,
					   	colModel: data_obj.colmodel_list,
					   	pager: "#pSearchResult",
					   	viewrecords: false,
					   	caption: "검색결과" + " " + data_obj.result.length+"개",
					   	width: 740
					});
				}
			 	//for loop
			 	if(data_obj.group_result != null){
				 	if( data_obj.group_result != "null"){
						for(i = 0;i < data_obj.group_result.length; i++){
							
							$("#groupResultWrapper").append($("<table id='groupResult"+i+"'></table>"));
							$("#groupResultWrapper").append($("<div id='pGroupResult"+i+"'></div>"));
							$("#groupResultWrapper").append($("<p>"));

							var colNames = ["no", "key", "frequency"];
							
							var colModel = [
							   		{"name": "_no_", "index": "no", "width": "50", "sorttype": "int", "align": "center"},
									{"name": "key", "index": "key", "width": "200", "sorttype": "text"},
									{"name": "freq", "index": "freq", "width": "100", "sorttype": "int", "align": "center"}
							   	];							

							if( data_obj.group_result[i][0]["sum"] ) {
								colModel[colModel.length] = {"name": "sum"};//, "index": "sum", "width": "100", "sorttype": "int", "align": "center"};
								colNames[colNames.length] = "sum";
							}
							if(data_obj.group_result[i][0]["max"] ) {
								colModel[colModel.length] = {"name": "max"};//, "index": "max", "width": "100", "sorttype": "int", "align": "center"};
								colNames[colNames.length] = "max";
							}

							$("#groupResult"+i).jqGrid({
								data: data_obj.group_result[i],
								datatype: "local",
								height: 'auto',
								mtype: 'POST',
								rowNum: 10,
								rowList: [10,20,30,40,50],
							   	colNames: colNames,
							   	colModel: colModel,
							   	pager: "#pGroupResult"+i,
							   	viewrecords: false,
							   	caption: "그룹결과"+(i+1) + " - " + data_obj.group_result[i].length
							});											 	
						}
				 	}//if
				}//if
			 	
			  }
		});
	}
	</script>
	<p>
	
	<div id="resultWrapper"></div>
	<p>
	<div id="groupResultWrapper"></div>
	
	
	
	
	<!-- E : #mainContent --></div>
	
<!-- footer -->
<%@include file="../footer.jsp" %>
	
</div><!-- //E : #container -->

</body>

</html>
