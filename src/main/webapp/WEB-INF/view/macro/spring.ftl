<#import "/spring.ftl" as spring />

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

