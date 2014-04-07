<#-- @ftlvariable name="environment" type="org.springframework.core.env.Environment" -->
<#-- @ftlvariable name="email" type="java.lang.String" -->
<#-- @ftlvariable name="oneTimeToken" type="java.lang.String" -->
<#-- @ftlvariable name="passwordPattern" type="java.lang.String" -->
<#-- @ftlvariable name="csrfParameterName" type="java.lang.String" -->
<#-- @ftlvariable name="csrfToken" type="java.lang.String" -->
<#include "layout/default.ftl" />

<#macro page_body>
<h3>Update Password</h3>
<style>
	#password:invalid.filled + .error_message::after {
		content: "${environment.getProperty("validation.user.password")}";
	}

	#passwordConfirm.invalid + .error_message::after {
		content: "${environment.getProperty("validation.user.passwordNonMatching")}";
	}
</style>
<form action="/updatePassword" method="POST">

	<p class="page_message">${environment.getProperty("validation.user.password")}</p>

    <@messages.print_errors_list/>
	<div>
		<input id="email" name="email" type="hidden" value="${email!""}">
        <input id="oneTimeToken" name="oneTimeToken" type="hidden" value="${oneTimeToken!""}">

		<p>
			<label for="password">Password:</label> <input type="password" id="password" name="password" value="" required="required" pattern="${passwordPattern}" class="show_validation" autocomplete="off"/> <span class="error_message"></span>
		</p>

		<p>
			<label for="passwordConfirm">Password Confirm:</label> <input type="password" id="passwordConfirm" name="passwordConfirm" value="" required="required" pattern="${passwordPattern}" class="show_validation" autocomplete="off"/> <span class="error_message"></span>
		</p>

		<div style="width:100%; height: 1em;"></div>

		<p>
			<input type="submit" formnovalidate name="submit" value="Update Password">
		</p>
	</div>
    <#if csrfParameterName?? && csrfToken?? >
	    <input id="csrf" type="hidden" name="${csrfParameterName}" value="${csrfToken}"/>
    </#if>
</form>
</#macro>

<@page_html/>