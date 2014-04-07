<#-- @ftlvariable name="environment" type="org.springframework.core.env.Environment" -->
<#-- @ftlvariable name="name" type="java.lang.String" -->
<#-- @ftlvariable name="email" type="java.lang.String" -->
<#-- @ftlvariable name="emailPattern" type="java.lang.String" -->
<#-- @ftlvariable name="csrfParameterName" type="java.lang.String" -->
<#-- @ftlvariable name="csrfToken" type="java.lang.String" -->
<#include "/WEB-INF/view/layout/default.ftl" />

<#macro page_body>
<h3>Update Password</h3>
<style>
	#name:invalid + .error_message::after {
		content: "${environment.getProperty("validation.user.name")}";
	}

	#email.invalid + .error_message::after {
		content: "${environment.getProperty("validation.user.email")}";
	}
</style>
<form action="/register" method="POST">

	<p class="page_message">${environment.getProperty("validation.user.register")}</p>

    <@messages.print_binding_errors "user"/>
	<div>
		<p>
			<label for="name">Name:</label> <@spring.formInput path="user.name" attributes='required="required" pattern=".{3,50}" maxlength="50" class="show_validation" autocorrect="off" autocapitalize="off" autocomplete="off"' />
			<span class="error_message"></span>
		</p>

		<p>
			<label for="email">Email:</label> <@spring.formInput path="user.email" fieldType="email" attributes='required="required" pattern="${emailPattern}" class="show_validation" autocorrect="off" autocapitalize="off" autocomplete="off"' />
			<span class="error_message"></span>
		</p>

		<div style="width:100%; height: 1em;"></div>

		<p>
			<input type="submit" formnovalidate name="submit" value="Register">
		</p>
	</div>
    <#if csrfParameterName?? && csrfToken?? >
		<input id="csrf" type="hidden" name="${csrfParameterName}" value="${csrfToken}"/>
    </#if>
</form>
</#macro>

<@page_html/>