<#-- @ftlvariable name="title" type="java.lang.String" -->
<#-- @ftlvariable name="message" type="java.lang.String" -->
<#include "layout/default.ftl" />

<#macro page_body>
<h3>${title!"Message"}</h3>

<p <#if error?? && error> class="error_message" <#else> class="message" </#if>>${message!""}</p>
</#macro>

<@page_html/>