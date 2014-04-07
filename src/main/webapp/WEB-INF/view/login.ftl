<#-- @ftlvariable name="csrfParameterName" type="java.lang.String" -->
<#-- @ftlvariable name="csrfToken" type="java.lang.String" -->
<#include "layout/default.ftl" />

<#macro page_body>
<h3>Login with Username and Password</h3>

    <#if SPRING_SECURITY_LAST_EXCEPTION??>
	<p class="errors_warnings">${SPRING_SECURITY_LAST_EXCEPTION.message}</p>
    </#if>

<form name="spring_security_check_form" action="/login" method="POST">
	<table>
		<tbody>
            <tr>
                <td>User:</td>
                <td><input type="text" name="username" value="" tabindex="1"></td>
            </tr>
            <tr>
                <td>Password:</td>
                <td><input type="password" name="password" tabindex="2"></td>
            </tr>
            <tr>
                <td colspan="1"><input name="submit" type="submit" value="Login" tabindex="2"></td>
                <td colspan="1"><input type="button" value="Register" tabindex="3" onClick="parent.location='/register'"></td>
            </tr>
		</tbody>
	</table>
    <#if csrfParameterName?? && csrfToken?? >
	    <input id="csrf" type="hidden" name="${csrfParameterName}" value="${csrfToken}"/>
    </#if>
</form>
</#macro>

<@page_html/>