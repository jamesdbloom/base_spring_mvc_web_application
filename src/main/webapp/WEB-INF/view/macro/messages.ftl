<#-- @ftlvariable name="environment" type="org.springframework.core.env.Environment" -->

<#--
* print_binding_errors
*
* Macro to print out binding errors
 -->
<#macro print_binding_errors objectName="">
    <#if bindingResult?? && (bindingResult.getAllErrors()?size > 0) && (bindingResult.objectName = objectName)>
        <div id="validation_error_${objectName}" class="errors_warnings">
            <p>There were problems with the data you entered:</p>
            <#list bindingResult.getAllErrors() as error>
                <p class="validation_error" style="margin-left: 2em;">&ndash; ${environment.getProperty(error.defaultMessage)}</p>
            </#list>
        </div>
    </#if>
</#macro>

<#--
* print_errors_list
*
* Macro to print out form errors
 -->
<#macro print_errors_list>
    <#if validationErrors?? && (validationErrors?size > 0)>
        <div id="validation_error" class="errors_warnings">
            <p>There were problems with the data you entered:</p>
            <#list validationErrors as error>
                <p class="validation_error" style="margin-left: 2em;">&ndash; ${environment.getProperty(error)}</p>
            </#list>
        </div>
    </#if>
</#macro>

<#--
 * message
 *
 * Macro to translate a message code into a message,
 * where [[code]] is used if it does not exist,
 * where {{code}} is used if it does exist and is blank
  -->
<#function getMessage code>
    <#local message = springMacroRequestContext.getMessage(code, "[["+code+"]]")>
    <#if message == "" >
        <#return "{{"+code+"}}" />
    <#else>
        <#return message />
    </#if>
</#function>

<#macro message code>
    <#local message = getMessage(code)>
    ${message}
</#macro>

<#--
 * message with arguments
 *
 * Macro to translate a message code into a message,
 * where [[code]] is used if it does not exist,
 * where {{code}} is used if it does exist and is blank
  -->
<#function getMessageWithArgs code args>
    <#local message = springMacroRequestContext.getMessage(code, "[["+code+"]]", args)>
    <#if message == "" >
        <#return "{{"+code+"}}" />
    <#else>
        <#return message />
    </#if>
</#function>

<#macro messageWithArgs code args>
    <#local message = getMessageWithArgs(code, args)>
    ${message}
</#macro>

